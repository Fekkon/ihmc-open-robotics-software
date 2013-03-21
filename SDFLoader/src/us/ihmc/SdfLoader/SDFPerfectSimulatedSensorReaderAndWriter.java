package us.ihmc.SdfLoader;

import java.util.ArrayList;
import java.util.Random;

import javax.media.j3d.Transform3D;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;

import us.ihmc.commonWalkingControlModules.dynamics.FullRobotModel;
import us.ihmc.commonWalkingControlModules.referenceFrames.CommonWalkingReferenceFrames;
import us.ihmc.utilities.Pair;
import us.ihmc.utilities.math.geometry.FrameVector;
import us.ihmc.utilities.math.geometry.ReferenceFrame;
import us.ihmc.utilities.screwTheory.OneDoFJoint;
import us.ihmc.utilities.screwTheory.SixDoFJoint;
import us.ihmc.utilities.screwTheory.Twist;

import com.yobotics.simulationconstructionset.OneDegreeOfFreedomJoint;
import com.yobotics.simulationconstructionset.YoVariableRegistry;
import com.yobotics.simulationconstructionset.robotController.RawOutputWriter;
import com.yobotics.simulationconstructionset.robotController.RawSensorReader;

public class SDFPerfectSimulatedSensorReaderAndWriter implements RawSensorReader, RawOutputWriter
{
   
   private static final double FILTERING_ALPHA = 1e-1;
   private static final boolean FILTER_GAUSSIAN_POSITION_NOISE = false;
   private static final double STD_DEVIATION_OF_UNNORMALIZED_QUATERNION_DISTURBANCE = 0.01;
   private static final double STD_DEVIATION_OF_POSITION_DISTURBANCE = 0.01;
   private static final boolean ADD_GAUSSIAN_POSITION_NOISE = false;
   private final String name;
   private final SDFRobot robot;
   private final FullRobotModel fullRobotModel;
   private final CommonWalkingReferenceFrames referenceFrames;

   private final ArrayList<Pair<OneDegreeOfFreedomJoint,OneDoFJoint>> revoluteJoints = new ArrayList<Pair<OneDegreeOfFreedomJoint, OneDoFJoint>>();

   public SDFPerfectSimulatedSensorReaderAndWriter(SDFRobot robot, FullRobotModel fullRobotModel, CommonWalkingReferenceFrames referenceFrames)
   {
      this.name = robot.getName() + "SimulatedSensorReader";
      this.robot = robot;
      this.fullRobotModel = fullRobotModel;
      this.referenceFrames = referenceFrames;

      OneDoFJoint[] revoluteJointsArray = fullRobotModel.getOneDoFJoints();

      for (OneDoFJoint revoluteJoint : revoluteJointsArray)
      {
         String name = revoluteJoint.getName();
         OneDegreeOfFreedomJoint oneDoFJoint = robot.getOneDoFJoint(name);

         Pair<OneDegreeOfFreedomJoint,OneDoFJoint> jointPair = new Pair<OneDegreeOfFreedomJoint, OneDoFJoint>(oneDoFJoint, revoluteJoint);
         this.revoluteJoints.add(jointPair);
      }

   }

   public void initialize()
   {
      read();
   }

   public YoVariableRegistry getYoVariableRegistry()
   {
      return null;
   }

   public String getName()
   {
      return name;
   }

   public String getDescription()
   {
      return getName();
   }

   private Transform3D temporaryRootToWorldTransform = new Transform3D();

   private Random rand = new Random(124381L);
   private Quat4d rotationError = new Quat4d();
   private Vector3d positionError = new Vector3d();

   private Quat4d rotationFilter = new Quat4d();
   private Vector3d positionFilter = new Vector3d();
   
   public void read()
   {
      for (Pair<OneDegreeOfFreedomJoint, OneDoFJoint> jointPair : revoluteJoints)
      {
         OneDegreeOfFreedomJoint pinJoint = jointPair.first();
         OneDoFJoint revoluteJoint = jointPair.second();

         revoluteJoint.setQ(pinJoint.getQ().getDoubleValue());
         revoluteJoint.setQd(pinJoint.getQD().getDoubleValue());
         revoluteJoint.setQdd(pinJoint.getQDD().getDoubleValue());

      }

      SixDoFJoint rootJoint = fullRobotModel.getRootJoint();
      robot.getRootJointToWorldTransform(temporaryRootToWorldTransform);
      
      if(ADD_GAUSSIAN_POSITION_NOISE)
      {
         rotationError.w = 1;
         rotationError.x = rand.nextGaussian()*STD_DEVIATION_OF_UNNORMALIZED_QUATERNION_DISTURBANCE;
         rotationError.y = rand.nextGaussian()*STD_DEVIATION_OF_UNNORMALIZED_QUATERNION_DISTURBANCE;
         rotationError.z = rand.nextGaussian()*STD_DEVIATION_OF_UNNORMALIZED_QUATERNION_DISTURBANCE;
         rotationError.normalize();
         
         positionError.x = rand.nextGaussian()*STD_DEVIATION_OF_POSITION_DISTURBANCE;
         positionError.y = rand.nextGaussian()*STD_DEVIATION_OF_POSITION_DISTURBANCE;
         positionError.z = rand.nextGaussian()*STD_DEVIATION_OF_POSITION_DISTURBANCE;
         
         Transform3D disturbanceTransform = new Transform3D();
         if (FILTER_GAUSSIAN_POSITION_NOISE)
         {
            double alpha = FILTERING_ALPHA;
            rotationFilter.scale(1-alpha);
            rotationError.scale(alpha);
            rotationFilter.add(rotationError);
            rotationFilter.normalize();
            
            positionFilter.scale(1-alpha);
            positionError.scale(alpha);
            positionFilter.add(positionError);
            disturbanceTransform.set(rotationFilter, positionFilter, 1.0);
         }
         else
         {
            disturbanceTransform.set(rotationError, positionError, 1.0);
         }
         temporaryRootToWorldTransform.mul(disturbanceTransform);
      }
      
      rootJoint.setPositionAndRotation(temporaryRootToWorldTransform);

      referenceFrames.updateFrames();


      ReferenceFrame elevatorFrame = rootJoint.getFrameBeforeJoint();
      ReferenceFrame pelvisFrame = rootJoint.getFrameAfterJoint();

      FrameVector linearVelocity = robot.getRootJointVelocity();
      linearVelocity.changeFrame(pelvisFrame);

      FrameVector angularVelocity = robot.getPelvisAngularVelocityInPelvisFrame(pelvisFrame);
      angularVelocity.changeFrame(pelvisFrame);

      Twist bodyTwist = new Twist(pelvisFrame, elevatorFrame, pelvisFrame, linearVelocity.getVector(), angularVelocity.getVector());
      rootJoint.setJointTwist(bodyTwist);

      // Think about adding root body acceleration to the fullrobotmodel

   }

   public void write()
   {
      for (Pair<OneDegreeOfFreedomJoint, OneDoFJoint> jointPair : revoluteJoints)
      {
         OneDegreeOfFreedomJoint pinJoint = jointPair.first();
         OneDoFJoint revoluteJoint = jointPair.second();

         pinJoint.setTau(revoluteJoint.getTau());
      }
   }

}
