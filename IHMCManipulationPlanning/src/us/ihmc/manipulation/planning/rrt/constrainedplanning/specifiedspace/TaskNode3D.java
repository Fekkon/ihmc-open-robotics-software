package us.ihmc.manipulation.planning.rrt.constrainedplanning.specifiedspace;

import us.ihmc.commons.PrintTools;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple4D.Quaternion;
import us.ihmc.manipulation.planning.rrt.constrainedplanning.tools.WheneverWholeBodyKinematicsSolver;
import us.ihmc.manipulation.planning.trajectory.EndEffectorTrajectory;
import us.ihmc.robotics.geometry.FrameOrientation;
import us.ihmc.robotics.geometry.FramePoint;
import us.ihmc.robotics.geometry.FramePose;
import us.ihmc.robotics.geometry.transformables.Pose;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;

public class TaskNode3D extends TaskNode
{   
   public static WheneverWholeBodyKinematicsSolver nodeTester;
   public static EndEffectorTrajectory endEffectorTrajectory;
   public static ReferenceFrame midZUpFrame;
   public static ReferenceFrame worldFrame = ReferenceFrame.getWorldFrame();
   
   
   public TaskNode3D()
   {
      super(4);      
   }
   
   public TaskNode3D(double time, double pelvisHeight, double chestYaw, double chestPitch)
   {
      super(4);
      setNodeData(0, time);
      setNodeData(1, pelvisHeight);
      setNodeData(2, chestYaw);
      setNodeData(3, chestPitch);
   }
   
   @Override
   public boolean isValidNode()
   {
      /*
       * using @code WheneverWholeBodyKinematicsSolver.
       * set initial configuration
       */
      
      if(getParentNode() != null)
      {
         nodeTester.updateRobotConfigurationDataJointsOnly(getParentNode().getOneDoFJoints());
         for (int i = 0; i < getParentNode().getOneDoFJoints().length; i++)
         {         
            double jointPosition = getParentNode().getOneDoFJoints()[i].getQ();         
         }
         
      }
      else
      {         
         PrintTools.warn("parentNode is required.");
      }

      nodeTester.initialize();

      nodeTester.holdCurrentTrajectoryMessages();
      /*
       * set whole body tasks.
       */            
      Pose desiredPose = endEffectorTrajectory.getEndEffectorPose(getNodeData(0));
      FramePoint desiredPointToWorld = new FramePoint(worldFrame, desiredPose.getPosition());
      FrameOrientation desiredOrientationToWorld = new FrameOrientation(worldFrame, desiredPose.getOrientation());
            
      FramePose desiredPoseToWorld = new FramePose(desiredPointToWorld, desiredOrientationToWorld);      
      
      desiredPoseToWorld.changeFrame(midZUpFrame);
      
      Pose desiredPoseToMidZUp = new Pose(new Point3D(desiredPoseToWorld.getPosition()), new Quaternion(desiredPoseToWorld.getOrientation()));
      nodeTester.setDesiredHandPose(endEffectorTrajectory.getRobotSide(), desiredPoseToMidZUp);
      nodeTester.setHandSelectionMatrixFree(endEffectorTrajectory.getAnotherRobotSide());
      
      Quaternion desiredChestOrientation = new Quaternion();
      desiredChestOrientation.appendYawRotation(getNodeData(2));
      desiredChestOrientation.appendPitchRotation(getNodeData(3));
      nodeTester.setDesiredChestOrientation(desiredChestOrientation);
            
      nodeTester.setDesiredPelvisHeight(getNodeData(1));
      
      nodeTester.putTrajectoryMessages();
      
      setIsValidNode(nodeTester.isSolved());
      
      setConfigurationJoints(nodeTester.getFullRobotModelCopy());
     
      return isValid;
   }

   @Override
   public TaskNode createNode()
   {
      return new TaskNode3D();
   }

   

   
}
