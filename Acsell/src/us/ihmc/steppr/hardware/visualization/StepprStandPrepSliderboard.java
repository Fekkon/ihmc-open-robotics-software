package us.ihmc.steppr.hardware.visualization;

import java.util.EnumMap;

import us.ihmc.SdfLoader.SDFRobot;
import us.ihmc.acsell.parameters.BonoRobotModel;
import us.ihmc.robotDataCommunication.YoVariableClient;
import us.ihmc.robotDataCommunication.visualizer.SCSVisualizer;
import us.ihmc.steppr.hardware.StepprDashboard;
import us.ihmc.steppr.hardware.StepprJoint;
import us.ihmc.steppr.hardware.configuration.StepprNetworkParameters;
import us.ihmc.steppr.hardware.controllers.StepprStandPrepSetpoints;
import us.ihmc.yoUtilities.dataStructure.YoVariableHolder;
import us.ihmc.yoUtilities.dataStructure.listener.VariableChangedListener;
import us.ihmc.yoUtilities.dataStructure.registry.YoVariableRegistry;
import us.ihmc.yoUtilities.dataStructure.variable.DoubleYoVariable;
import us.ihmc.yoUtilities.dataStructure.variable.EnumYoVariable;
import us.ihmc.yoUtilities.dataStructure.variable.YoVariable;

import com.yobotics.simulationconstructionset.IndexChangedListener;
import com.yobotics.simulationconstructionset.OneDegreeOfFreedomJoint;
import com.yobotics.simulationconstructionset.util.inputdevices.SliderBoardConfigurationManager;

public class StepprStandPrepSliderboard extends SCSVisualizer implements IndexChangedListener
{
   private final SDFRobot robot;
   private final YoVariableRegistry sliderBoardRegistry = new YoVariableRegistry("StepprStandPrepSliderBoard");
   private final EnumYoVariable<StepprStandPrepSetpoints> selectedJointPair = new EnumYoVariable<>("selectedJointPair", sliderBoardRegistry,
         StepprStandPrepSetpoints.class);

   private final DoubleYoVariable selectedJoint_q_d = new DoubleYoVariable("selectedJoint_q_d", sliderBoardRegistry);
   private final DoubleYoVariable selectedJoint_kp = new DoubleYoVariable("selectedJoint_kp", sliderBoardRegistry);
   private final DoubleYoVariable selectedJoint_kd = new DoubleYoVariable("selectedJoint_kd", sliderBoardRegistry);
   private final DoubleYoVariable selectedJoint_damping = new DoubleYoVariable("selectedJoint_damping", sliderBoardRegistry);
   private final DoubleYoVariable selectedJoint_positionerror = new DoubleYoVariable("selectedJoint_positionerror", sliderBoardRegistry);

   private final EnumMap<StepprStandPrepSetpoints, StandPrepVariables> allSetpoints = new EnumMap<>(StepprStandPrepSetpoints.class);

   public StepprStandPrepSliderboard(SDFRobot robot, int bufferSize)
   {
      super(robot, bufferSize);
      this.robot = robot;

      registry.addChild(sliderBoardRegistry);
   }

   @Override
   public void start()
   {
      final SliderBoardConfigurationManager sliderBoardConfigurationManager = new SliderBoardConfigurationManager(scs);

      YoVariable<?> crouch = registry.getVariable("StepprStandPrep", "crouch");
      
      YoVariable<?> controlRatio = registry.getVariable("StepprOutputWriter", "controlRatio");
      YoVariable<?> height = registry.getVariable("LookAheadCoMHeightTrajectoryGenerator", "offsetHeightAboveGround");
      YoVariable<?> icpX = registry.getVariable("WalkingHighLevelHumanoidController", "icpStandOffsetX");
      YoVariable<?> icpY = registry.getVariable("WalkingHighLevelHumanoidController", "icpStandOffsetY");
      
      
      for (StepprStandPrepSetpoints setpoint : StepprStandPrepSetpoints.values)
      {
         StandPrepVariables variables = new StandPrepVariables(setpoint, registry);

         StepprJoint aJoint = setpoint.getJoints()[0];
         OneDegreeOfFreedomJoint oneDoFJoint = robot.getOneDegreeOfFreedomJoint(aJoint.getSdfName());
         sliderBoardConfigurationManager.setKnob(1, selectedJointPair, 0, StepprJoint.values.length);
         sliderBoardConfigurationManager.setSlider(1, variables.q_d, oneDoFJoint.getJointLowerLimit(), oneDoFJoint.getJointUpperLimit());
         sliderBoardConfigurationManager.setSlider(2, variables.kp, 0, 100 * aJoint.getRatio() * aJoint.getRatio());
         sliderBoardConfigurationManager.setSlider(3, variables.damping, 0, 5 * aJoint.getRatio() * aJoint.getRatio());
         sliderBoardConfigurationManager.setSlider(4, crouch, 0, 1);
         sliderBoardConfigurationManager.setSlider(5, controlRatio, 0, 1);
         sliderBoardConfigurationManager.setSlider(6, height, -0.3, 0.3);
         sliderBoardConfigurationManager.setSlider(7, icpX, -0.3, 0.3);
         sliderBoardConfigurationManager.setSlider(8, icpY, -0.3, 0.3);
         

         
         
         sliderBoardConfigurationManager.saveConfiguration(setpoint.toString());

         allSetpoints.put(setpoint, variables);
      }

      selectedJointPair.addVariableChangedListener(new VariableChangedListener()
      {

         @Override
         public void variableChanged(YoVariable<?> v)
         {
            sliderBoardConfigurationManager.loadConfiguration(selectedJointPair.getEnumValue().toString());
         }
      });

      selectedJointPair.set(StepprStandPrepSetpoints.HIP_Y);

      StepprDashboard.createDashboard(scs, registry);
      super.start();
      scs.getDataBuffer().attachIndexChangedListener(this);

   }

   private class StandPrepVariables
   {
      private final DoubleYoVariable q_d;
      private final DoubleYoVariable kp;
      private final DoubleYoVariable kd;
      private final DoubleYoVariable damping;
      private final DoubleYoVariable positionerror;

      public StandPrepVariables(StepprStandPrepSetpoints setpoint, YoVariableHolder variableHolder)
      {
         String prefix = setpoint.getName();
         String ajoint = setpoint.getJoints()[0].getSdfName();
         q_d = (DoubleYoVariable) variableHolder.getVariable("StepprStandPrep", prefix + "_q_d");
         kp = (DoubleYoVariable) variableHolder.getVariable("StepprStandPrep", prefix + "_kp");
         kd = (DoubleYoVariable) variableHolder.getVariable("StepprStandPrep", prefix + "_kd");
         damping = (DoubleYoVariable) variableHolder.getVariable("StepprStandPrep", prefix + "_damping");
         positionerror = (DoubleYoVariable) variableHolder.getVariable("StepprStandPrep", "positionError_" + ajoint);
      }

      public void update()
      {
         selectedJoint_q_d.set(q_d.getDoubleValue());
         selectedJoint_kp.set(kp.getDoubleValue());
         selectedJoint_kd.set(kd.getDoubleValue());
         selectedJoint_damping.set(damping.getDoubleValue());
         selectedJoint_positionerror.set(positionerror.getDoubleValue());
      }

   }

   @Override
   public void indexChanged(int newIndex, double newTime)
   {
      StepprStandPrepSetpoints joint = selectedJointPair.getEnumValue();
      allSetpoints.get(joint).update();

   }

   public static void main(String[] args)
   {
      System.out.println("Connecting to host " + StepprNetworkParameters.CONTROL_COMPUTER_HOST);
      BonoRobotModel robotModel = new BonoRobotModel(true, false);
      SDFRobot robot = robotModel.createSdfRobot(false);

      SCSVisualizer scsYoVariablesUpdatedListener = new StepprStandPrepSliderboard(robot, 64000);

      YoVariableClient client = new YoVariableClient(StepprNetworkParameters.CONTROL_COMPUTER_HOST, StepprNetworkParameters.VARIABLE_SERVER_PORT,
            scsYoVariablesUpdatedListener, "remote", false);
      client.start();

   }
}
