package us.ihmc.steppr.hardware.visualization;

import java.io.IOException;
import java.util.EnumMap;

import net.java.games.input.Component;
import us.ihmc.acsell.treadmill.TreadmillJoystickEventListener;
import us.ihmc.acsell.treadmill.TreadmillSerialManager;
import us.ihmc.robotDataLogger.YoVariableClient;
import us.ihmc.robotDataVisualizer.visualizer.SCSVisualizer;
import us.ihmc.yoVariables.dataBuffer.IndexChangedListener;
import us.ihmc.yoVariables.dataBuffer.YoVariableHolder;
import us.ihmc.yoVariables.listener.VariableChangedListener;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoBoolean;
import us.ihmc.yoVariables.variable.YoDouble;
import us.ihmc.yoVariables.variable.YoEnum;
import us.ihmc.yoVariables.variable.YoVariable;
import us.ihmc.simulationconstructionset.FloatingRootJointRobot;
import us.ihmc.simulationconstructionset.OneDegreeOfFreedomJoint;
import us.ihmc.simulationconstructionset.Robot;
import us.ihmc.simulationconstructionset.SimulationConstructionSet;
import us.ihmc.simulationConstructionSetTools.joystick.BooleanYoVariableJoystickEventListener;
import us.ihmc.simulationConstructionSetTools.joystick.DoubleYoVariableJoystickEventListener;
import us.ihmc.simulationConstructionSetTools.util.inputdevices.SliderBoardConfigurationManager;
import us.ihmc.steppr.hardware.StepprDashboard;
import us.ihmc.steppr.hardware.StepprJoint;
import us.ihmc.steppr.hardware.controllers.StepprStandPrepSetpoints;
import us.ihmc.tools.inputDevices.joystick.Joystick;

public class StepprStandPrepSliderboard extends SCSVisualizer implements IndexChangedListener
{
   private final boolean CONTROL_TREADMILL_WITH_JOYSTICK = true;  
   private final YoVariableRegistry sliderBoardRegistry = new YoVariableRegistry("StepprStandPrepSliderBoard");
   private final YoEnum<StepprStandPrepSetpoints> selectedJointPair = new YoEnum<>("selectedJointPair", sliderBoardRegistry,
         StepprStandPrepSetpoints.class);

   private final YoDouble selectedJoint_q_d = new YoDouble("selectedJoint_q_d", sliderBoardRegistry);
   private final YoDouble selectedJoint_kp = new YoDouble("selectedJoint_kp", sliderBoardRegistry);
   private final YoDouble selectedJoint_kd = new YoDouble("selectedJoint_kd", sliderBoardRegistry);
   private final YoDouble selectedJoint_damping = new YoDouble("selectedJoint_damping", sliderBoardRegistry);
   private final YoDouble selectedJoint_positionerror = new YoDouble("selectedJoint_positionerror", sliderBoardRegistry);
   //private final YoDouble maxDesiredVelocityX = new YoDouble("maxDesiredVelocityX", sliderBoardRegistry);
   private final YoDouble desiredVelX_Setpoint = new YoDouble("DesiredVelocityX_setpoint", sliderBoardRegistry);
   private final YoDouble desiredVelX_Adjust = new YoDouble("DesiredVelocityX_adjustment", sliderBoardRegistry);
   
   private final EnumMap<StepprStandPrepSetpoints, StandPrepVariables> allSetpoints = new EnumMap<>(StepprStandPrepSetpoints.class);
   
   private final TreadmillSerialManager treadmillManager;

   public StepprStandPrepSliderboard(int bufferSize)
   {
      super(bufferSize);
      if (CONTROL_TREADMILL_WITH_JOYSTICK)
    	  treadmillManager = new TreadmillSerialManager("/dev/ttyS0");
      else
    	  treadmillManager = null;
   }

   @Override
   public void starting(SimulationConstructionSet scs, Robot robot, YoVariableRegistry registry)
   {


      registry.addChild(sliderBoardRegistry);
      final SliderBoardConfigurationManager sliderBoardConfigurationManager = new SliderBoardConfigurationManager(scs);

      YoVariable<?> crouch = registry.getVariable("StepprStandPrep", "crouch");
      
      YoVariable<?> controlRatio = registry.getVariable("StepprOutputWriter", "controlRatio");
      YoVariable<?> height = registry.getVariable("LookAheadCoMHeightTrajectoryGenerator", "offsetHeightAboveGround");
      YoVariable<?> icpX = registry.getVariable("PelvisICPBasedTranslationManager", "desiredICPOffsetX");
      YoVariable<?> icpY = registry.getVariable("PelvisICPBasedTranslationManager", "desiredICPOffsetY");
      YoVariable<?> userPelvisRoll = registry.getVariable("UserDesiredPelvisPoseProvider","userDesiredPelvisRoll");
      YoVariable<?> userPelvisYaw = registry.getVariable("UserDesiredPelvisPoseProvider","userDesiredPelvisYaw");
      YoVariable<?> userPelvisPitch = registry.getVariable("UserDesiredPelvisPoseProvider","userDesiredPelvisPitch");
      
      YoVariable<?> userDesiredLateralFeetForce = registry.getVariable("MomentumBasedController","userLateralFeetForce");
      YoVariable<?> userDesiredForwardFeetForce = registry.getVariable("MomentumBasedController","userForwardFeetForce");
      YoVariable<?> userYawFeetTorque = registry.getVariable("MomentumBasedController","userYawFeetTorque");
      YoVariable<?> masterMotorDamping = registry.getVariable("StepprOutputWriter","masterMotorDamping");

      
      final YoVariable<?> motorPowerStateRequest = registry.getVariable("StepprSetup", "motorPowerStateRequest");
      YoBoolean requestPowerOff = new YoBoolean("requestPowerOff", registry);
      requestPowerOff.addVariableChangedListener(new VariableChangedListener()
      {
         @Override
         public void variableChanged(YoVariable<?> v)
         {
            motorPowerStateRequest.setValueFromDouble(-1);
         }
      });
      
      for (StepprStandPrepSetpoints setpoint : StepprStandPrepSetpoints.values)
      {
         StandPrepVariables variables = new StandPrepVariables(setpoint, registry);

         StepprJoint aJoint = setpoint.getJoints()[0];
         OneDegreeOfFreedomJoint oneDoFJoint = ((FloatingRootJointRobot)robot).getOneDegreeOfFreedomJoint(aJoint.getSdfName());
         sliderBoardConfigurationManager.setKnob(1, selectedJointPair, 0, StepprJoint.values.length);
         sliderBoardConfigurationManager.setSlider(1, variables.q_d, oneDoFJoint.getJointLowerLimit(), oneDoFJoint.getJointUpperLimit());
         sliderBoardConfigurationManager.setSlider(2, variables.kp, 0, 100 * aJoint.getRatio() * aJoint.getRatio());
         sliderBoardConfigurationManager.setSlider(3, variables.damping, 0, 5 * aJoint.getRatio() * aJoint.getRatio());
         sliderBoardConfigurationManager.setSlider(4, crouch, 0, 1);
         sliderBoardConfigurationManager.setSlider(5, controlRatio, 0, 1);
         sliderBoardConfigurationManager.setSlider(6, height, -0.3, 0.3);
         sliderBoardConfigurationManager.setSlider(7, icpX, -0.3, 0.3);
         sliderBoardConfigurationManager.setSlider(8, icpY, -0.3, 0.3);
         sliderBoardConfigurationManager.setKnob(2, userPelvisYaw, -0.4,0.4);
         sliderBoardConfigurationManager.setKnob(3, userPelvisPitch, -0.4,0.4);
         sliderBoardConfigurationManager.setKnob(4, userPelvisRoll, -0.4,0.4);
         sliderBoardConfigurationManager.setKnob(5, userDesiredLateralFeetForce, -100,100);
         sliderBoardConfigurationManager.setKnob(6, userDesiredForwardFeetForce, -100,100);
         sliderBoardConfigurationManager.setKnob(7, userYawFeetTorque, -25, 25);
         sliderBoardConfigurationManager.setKnob(8, masterMotorDamping, 0, 2);
         
         sliderBoardConfigurationManager.setButton(1, registry.getVariable("PelvisICPBasedTranslationManager","manualModeICPOffset"));



         

         sliderBoardConfigurationManager.setButton(1, registry.getVariable("StepprOutputWriter","enableOutput"));
         sliderBoardConfigurationManager.setButton(2, registry.getVariable("StepprStandPrep","startStandPrep"));
         sliderBoardConfigurationManager.setButton(8, requestPowerOff);
         
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
      scs.getDataBuffer().attachIndexChangedListener(this);

      
      setupJoyStick(registry);
   }
   
  public void setupJoyStick(YoVariableHolder registry)
   {
      Joystick joystickUpdater;
      try
      {
         joystickUpdater = new Joystick();
      }
      catch (IOException e)
      {
         e.printStackTrace();
         return;
      }
      
      final double deadZone = 0.02;
      //final double desiredVelocityX_Bias = 0.0;
      final double desiredVelocityY_Bias = 0.0;
      final double desiredHeadingDot_Bias = 0.0;
      final double maxVelocityX = 0.35;
      final double maxDesiredVelocityX_Setpoint = 0.25;
      final double maxDesiredVelocityX_Adjust = 0.25;
      final double minVelocityX = -0.35;
      
      
      final YoDouble desiredVelocityX = (YoDouble) registry.getVariable("ManualDesiredVelocityControlModule", "desiredVelocityX");
      if(desiredVelocityX==null || joystickUpdater==null)
         return;
      
      joystickUpdater.addJoystickEventListener(new DoubleYoVariableJoystickEventListener(desiredVelX_Setpoint, joystickUpdater.findComponent(Component.Identifier.Axis.SLIDER),
           -maxDesiredVelocityX_Setpoint, 0.0, 0.0, false));
      joystickUpdater.addJoystickEventListener(new DoubleYoVariableJoystickEventListener(desiredVelX_Adjust, joystickUpdater.findComponent(Component.Identifier.Axis.Y),
    		  -maxDesiredVelocityX_Adjust, maxDesiredVelocityX_Adjust, deadZone, false));
      desiredVelX_Adjust.addVariableChangedListener(new VariableChangedListener()
      {         
         @Override
         public void variableChanged(YoVariable<?> v)
         {
           desiredVelocityX.set(v.getValueAsDouble()+desiredVelX_Setpoint.getDoubleValue());
         }
      });
      desiredVelX_Setpoint.addVariableChangedListener(new VariableChangedListener()
      {         
         @Override
         public void variableChanged(YoVariable<?> v)
         {
           desiredVelocityX.set(v.getValueAsDouble()+desiredVelX_Adjust.getDoubleValue());
         }
      });      
      desiredVelocityX.addVariableChangedListener(new VariableChangedListener()
      {
         @Override
         public void variableChanged(YoVariable<?> v)
         {
         	if (v.getValueAsDouble() < minVelocityX)
            	 v.setValueFromDouble(minVelocityX, false);
         	if (v.getValueAsDouble() > maxVelocityX)
         	    v.setValueFromDouble(maxVelocityX, false);
         }
      });
      
      YoDouble desiredVelocityY = (YoDouble) registry.getVariable("ManualDesiredVelocityControlModule", "desiredVelocityY");
      desiredVelocityY.set(desiredVelocityY_Bias);
      joystickUpdater.addJoystickEventListener(new DoubleYoVariableJoystickEventListener(desiredVelocityY, joystickUpdater.findComponent(Component.Identifier.Axis.X),
    		  -0.1+desiredVelocityY_Bias, 0.1+desiredVelocityY_Bias, deadZone, false));

      YoDouble desiredHeadingDot = (YoDouble) registry.getVariable("RateBasedDesiredHeadingControlModule", "desiredHeadingDot");
      desiredHeadingDot.set(desiredHeadingDot_Bias);
      joystickUpdater.addJoystickEventListener(new DoubleYoVariableJoystickEventListener(desiredHeadingDot, joystickUpdater.findComponent(Component.Identifier.Axis.RZ),
    		  -0.1+desiredHeadingDot_Bias, 0.1+desiredHeadingDot_Bias, deadZone/2.0, true));
      
      YoBoolean walk = (YoBoolean) registry.getVariable("DesiredFootstepCalculatorFootstepProviderWrapper","walk");
      joystickUpdater.addJoystickEventListener(new BooleanYoVariableJoystickEventListener(walk, joystickUpdater.findComponent(Component.Identifier.Button.TRIGGER), true));
      
      if (treadmillManager!=null)
    	  joystickUpdater.addJoystickEventListener(new TreadmillJoystickEventListener(treadmillManager.getSerialOutputStream()));
   }

   private class StandPrepVariables
   {
      private final YoDouble q_d;
      private final YoDouble kp;
      private final YoDouble kd;
      private final YoDouble damping;
      private final YoDouble positionerror;

      public StandPrepVariables(StepprStandPrepSetpoints setpoint, YoVariableHolder variableHolder)
      {
         String prefix = setpoint.getName();
         String ajoint = setpoint.getJoints()[0].getSdfName();
         q_d = (YoDouble) variableHolder.getVariable("StepprStandPrep", prefix + "_q_d");
         kp = (YoDouble) variableHolder.getVariable("StepprStandPrep", prefix + "_kp");
         kd = (YoDouble) variableHolder.getVariable("StepprStandPrep", prefix + "_kd");
         damping = (YoDouble) variableHolder.getVariable("StepprStandPrep", prefix + "_damping");
         positionerror = (YoDouble) variableHolder.getVariable("StepprStandPrep", "positionError_" + ajoint);
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
   public void notifyOfIndexChange(int newIndex, double newTime)
   {
      StepprStandPrepSetpoints joint = selectedJointPair.getEnumValue();
      allSetpoints.get(joint).update();

   }

   @Override public void notifyOfManualEndChange(int inPoint, int outPoint)
   {

   }

   public static void main(String[] args)
   {
      SCSVisualizer scsYoVariablesUpdatedListener = new StepprStandPrepSliderboard(64000);
      scsYoVariablesUpdatedListener.setShowOverheadView(false);

      YoVariableClient client = new YoVariableClient(scsYoVariablesUpdatedListener, "remote");
      client.start();

   }
}
