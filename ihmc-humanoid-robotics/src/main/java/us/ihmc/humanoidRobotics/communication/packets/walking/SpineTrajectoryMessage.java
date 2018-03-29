package us.ihmc.humanoidRobotics.communication.packets.walking;

import us.ihmc.communication.packets.Packet;
import us.ihmc.communication.ros.generators.RosExportedField;
import us.ihmc.humanoidRobotics.communication.packets.JointspaceTrajectoryMessage;

public class SpineTrajectoryMessage extends Packet<SpineTrajectoryMessage>
{
   @RosExportedField(documentation = "Trajectories for each joint.")
   public JointspaceTrajectoryMessage jointspaceTrajectory = new JointspaceTrajectoryMessage();

   /**
    * Empty constructor for serialization. Set the id of the message to
    * {@link Packet#VALID_MESSAGE_DEFAULT_ID}.
    */
   public SpineTrajectoryMessage()
   {
      setUniqueId(VALID_MESSAGE_DEFAULT_ID);
   }

   /**
    * Clone constructor.
    * 
    * @param spineTrajectoryMessage message to clone.
    */
   public SpineTrajectoryMessage(SpineTrajectoryMessage spineTrajectoryMessage)
   {
      jointspaceTrajectory = new JointspaceTrajectoryMessage(spineTrajectoryMessage.jointspaceTrajectory);
   }

   @Override
   public void set(SpineTrajectoryMessage other)
   {
      jointspaceTrajectory = new JointspaceTrajectoryMessage();
      jointspaceTrajectory.set(other.jointspaceTrajectory);
      setPacketInformation(other);
   }

   public void setJointspaceTrajectory(JointspaceTrajectoryMessage jointspaceTrajectory)
   {
      this.jointspaceTrajectory = jointspaceTrajectory;
   }

   public JointspaceTrajectoryMessage getJointspaceTrajectory()
   {
      return jointspaceTrajectory;
   }

   @Override
   public boolean epsilonEquals(SpineTrajectoryMessage other, double epsilon)
   {
      if (!jointspaceTrajectory.epsilonEquals(other.jointspaceTrajectory, epsilon))
         return false;
      return true;
   }
}
