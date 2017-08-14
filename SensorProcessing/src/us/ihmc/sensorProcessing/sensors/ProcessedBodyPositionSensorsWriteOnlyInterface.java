package us.ihmc.sensorProcessing.sensors;

import us.ihmc.robotics.geometry.FramePoint3D;
import us.ihmc.robotics.geometry.FrameVector;

public interface ProcessedBodyPositionSensorsWriteOnlyInterface
{
   public abstract void setBodyPosition(FramePoint3D bodyPosition);
   public abstract void setBodyVelocity(FrameVector bodyVelocity);
}
