package us.ihmc.wholeBodyController;

import java.io.InputStream;

import us.ihmc.commonWalkingControlModules.configurations.ICPWithTimeFreezingPlannerParameters;
import us.ihmc.commonWalkingControlModules.configurations.WalkingControllerParameters;
import us.ihmc.humanoidRobotics.footstep.footstepGenerator.QuadTreeFootstepPlanningParameters;
import us.ihmc.sensorProcessing.parameters.DRCRobotSensorInformation;
import us.ihmc.sensorProcessing.stateEstimation.StateEstimatorParameters;

public interface WholeBodyControllerParameters
{
   public double getControllerDT();

   /**
    * Returns the parameters used to create Footstep Plans.
    */
   default public QuadTreeFootstepPlanningParameters getQuadTreeFootstepPlanningParameters()
   {
      return null;
   }

   public StateEstimatorParameters getStateEstimatorParameters();

   public ICPWithTimeFreezingPlannerParameters getCapturePointPlannerParameters();

	public WalkingControllerParameters getWalkingControllerParameters();

	public RobotContactPointParameters getContactPointParameters();

   public DRCRobotSensorInformation getSensorInformation();
   

   /**
    * Get the parameter XML file for the controller.
    * 
    * Each call to this method should return a new InputStream.
    * If null is returned the default values for the parameters are used.
    * 
    * @return new InputStream with the controller parameters
    */
   public InputStream getWholeBodyControllerParametersFile();
}

