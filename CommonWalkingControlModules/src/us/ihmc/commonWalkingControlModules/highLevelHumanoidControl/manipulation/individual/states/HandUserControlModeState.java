package us.ihmc.commonWalkingControlModules.highLevelHumanoidControl.manipulation.individual.states;

import us.ihmc.commonWalkingControlModules.controllerCore.command.SolverWeightLevels;
import us.ihmc.commonWalkingControlModules.controllerCore.command.feedbackController.FeedbackControlCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseDynamics.InverseDynamicsCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseDynamics.InverseDynamicsCommandList;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseDynamics.JointAccelerationIntegrationCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseDynamics.JointspaceAccelerationCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.lowLevel.LowLevelJointControlMode;
import us.ihmc.commonWalkingControlModules.controllerCore.command.lowLevel.LowLevelOneDoFJointDesiredDataHolder;
import us.ihmc.commonWalkingControlModules.controllerCore.command.lowLevel.LowLevelOneDoFJointDesiredDataHolderReadOnly;
import us.ihmc.commonWalkingControlModules.highLevelHumanoidControl.manipulation.individual.HandControlMode;
import us.ihmc.commonWalkingControlModules.momentumBasedController.MomentumBasedController;
import us.ihmc.humanoidRobotics.communication.controllerAPI.command.ArmDesiredAccelerationsCommand;
import us.ihmc.robotics.dataStructures.registry.YoVariableRegistry;
import us.ihmc.robotics.dataStructures.variable.BooleanYoVariable;
import us.ihmc.robotics.dataStructures.variable.DoubleYoVariable;
import us.ihmc.robotics.robotSide.RobotSide;
import us.ihmc.robotics.screwTheory.OneDoFJoint;

public class HandUserControlModeState extends HandControlState
{
   public static final double TIME_WITH_NO_MESSAGE_BEFORE_ABORT = 0.25;

   private final RobotSide robotSide;
   private final OneDoFJoint[] userControlledJoints;
   private final DoubleYoVariable[] userDesiredJointAccelerations;
   private final DoubleYoVariable timeOfLastUserMesage;
   private final DoubleYoVariable timeSinceLastUserMesage;
   private final BooleanYoVariable abortUserControlMode;
   private final DoubleYoVariable yoTime;
   private final JointspaceAccelerationCommand jointspaceAccelerationCommand = new JointspaceAccelerationCommand();

   private final JointAccelerationIntegrationCommand jointAccelerationIntegrationCommand;
   private final LowLevelOneDoFJointDesiredDataHolder jointDesiredDataHolder;
   private final InverseDynamicsCommandList inverseDynamicsCommandList = new InverseDynamicsCommandList();

   public HandUserControlModeState(String namePrefix, RobotSide robotSide, OneDoFJoint[] userControlledJoints, OneDoFJoint[] positionControlledJoints,
         MomentumBasedController momentumBasedController, YoVariableRegistry parentRegistry)
   {
      super(HandControlMode.USER_CONTROL_MODE);

      YoVariableRegistry registry = new YoVariableRegistry(namePrefix + getClass().getSimpleName());
      parentRegistry.addChild(registry);

      this.robotSide = robotSide;
      this.userControlledJoints = userControlledJoints;
      userDesiredJointAccelerations = new DoubleYoVariable[userControlledJoints.length];
      for (int i = 0; i < userControlledJoints.length; i++)
      {
         String jointName = userControlledJoints[i].getName();
         userDesiredJointAccelerations[i] = new DoubleYoVariable("qdd_d_user_" + jointName, registry);
         jointspaceAccelerationCommand.addJoint(userControlledJoints[i], Double.NaN);
      }

      jointspaceAccelerationCommand.setWeight(SolverWeightLevels.HIGH);
      inverseDynamicsCommandList.addCommand(jointspaceAccelerationCommand);

      timeOfLastUserMesage = new DoubleYoVariable(namePrefix + "TimeOfLastUserMesage", registry);
      timeSinceLastUserMesage = new DoubleYoVariable(namePrefix + "TimeSinceLastUserMesage", registry);
      abortUserControlMode = new BooleanYoVariable(namePrefix + "AbortUserControlMode", registry);
      yoTime = momentumBasedController.getYoTime();

      if (positionControlledJoints.length > 0)
      {
         jointDesiredDataHolder = new LowLevelOneDoFJointDesiredDataHolder();
         jointDesiredDataHolder.registerJointsWithEmptyData(positionControlledJoints);
         jointDesiredDataHolder.setJointsControlMode(positionControlledJoints, LowLevelJointControlMode.POSITION_CONTROL);
         jointAccelerationIntegrationCommand = new JointAccelerationIntegrationCommand();
         for (int i = 0; i < positionControlledJoints.length; i++)
            jointAccelerationIntegrationCommand.addJointToComputeDesiredPositionFor(positionControlledJoints[i]);
         inverseDynamicsCommandList.addCommand(jointAccelerationIntegrationCommand);
      }
      else
      {
         jointDesiredDataHolder = null;
         jointAccelerationIntegrationCommand = null;
      }
   }

   public void setWeight(double weight)
   {
      jointspaceAccelerationCommand.setWeight(weight);
   }

   public void handleArmDesiredAccelerationsMessage(ArmDesiredAccelerationsCommand message)
   {
      if (message.getNumberOfJoints() != userControlledJoints.length)
      {
         abortUserControlMode.set(true);
         return;
      }

      if (message.getRobotSide() != robotSide)
      {
         abortUserControlMode.set(true);
         return;
      }

      for (int i = 0; i < userControlledJoints.length; i++)
         userDesiredJointAccelerations[i].set(message.getArmDesiredJointAcceleration(i));
      timeSinceLastUserMesage.set(0.0);
      timeOfLastUserMesage.set(yoTime.getDoubleValue());
   }

   @Override
   public void doTransitionIntoAction()
   {
      abortUserControlMode.set(false);
   }

   @Override
   public void doAction()
   {
      timeSinceLastUserMesage.set(yoTime.getDoubleValue() - timeOfLastUserMesage.getDoubleValue());

      if (timeSinceLastUserMesage.getDoubleValue() > TIME_WITH_NO_MESSAGE_BEFORE_ABORT)
      {
         abortUserControlMode.set(true);
         return;
      }

      for (int i = 0; i < userControlledJoints.length; i++)
      {
         jointspaceAccelerationCommand.setOneDoFJointDesiredAcceleration(i, userDesiredJointAccelerations[i].getDoubleValue());
      }
   }

   public boolean isAbortUserControlModeRequested()
   {
      return abortUserControlMode.getBooleanValue();
   }

   @Override
   public void doTransitionOutOfAction()
   {
      abortUserControlMode.set(false);
   }

   @Override
   public InverseDynamicsCommand<?> getInverseDynamicsCommand()
   {
      return inverseDynamicsCommandList;
   }

   @Override
   public FeedbackControlCommand<?> getFeedbackControlCommand()
   {
      return null;
   }

   @Override
   public LowLevelOneDoFJointDesiredDataHolderReadOnly getLowLevelJointDesiredData()
   {
      return jointDesiredDataHolder;
   }
}
