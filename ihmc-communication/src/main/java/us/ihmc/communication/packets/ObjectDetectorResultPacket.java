package us.ihmc.communication.packets;

/**
 * @author Doug Stephen <a href="mailto:dstephen@ihmc.us">(dstephen@ihmc.us)</a>
 */
public class ObjectDetectorResultPacket extends Packet<ObjectDetectorResultPacket>
{
   public HeatMapPacket heatMap = new HeatMapPacket();
   public BoundingBoxesPacket boundingBoxes = new BoundingBoxesPacket();

   public ObjectDetectorResultPacket()
   {

   }

   @Override
   public void set(ObjectDetectorResultPacket other)
   {
      heatMap.set(other.heatMap);
      boundingBoxes.set(boundingBoxes);
      setPacketInformation(other);
   }

   @Override
   public boolean epsilonEquals(ObjectDetectorResultPacket other, double epsilon)
   {
      return this.heatMap.epsilonEquals(other.heatMap, epsilon) && this.boundingBoxes.epsilonEquals(other.boundingBoxes, epsilon);
   }
}
