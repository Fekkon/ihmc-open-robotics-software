package us.ihmc.darpaRoboticsChallenge.networkProcessor.camera;

import java.awt.image.BufferedImage;

import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;

import us.ihmc.SdfLoader.SDFFullRobotModelFactory;
import us.ihmc.communication.net.NetStateListener;
import us.ihmc.communication.packetCommunicator.PacketCommunicator;
import us.ihmc.communication.packets.sensing.FisheyePacket;
import us.ihmc.communication.producers.CompressedVideoHandler;
import us.ihmc.communication.producers.RobotConfigurationDataBuffer;
import us.ihmc.darpaRoboticsChallenge.sensors.blackfly.BlackFlyParameterSetter;
import us.ihmc.sensorProcessing.parameters.DRCRobotCameraParameters;
import us.ihmc.utilities.robotSide.RobotSide;
import us.ihmc.utilities.ros.PPSTimestampOffsetProvider;
import us.ihmc.utilities.ros.RosMainNode;
import us.ihmc.utilities.ros.subscriber.RosCompressedImageSubscriber;
import boofcv.struct.calib.IntrinsicParameters;

public class FisheyeCameraReceiver extends CameraDataReceiver
{
   public FisheyeCameraReceiver(SDFFullRobotModelFactory fullRobotModelFactory, final DRCRobotCameraParameters cameraParameters,
         RobotConfigurationDataBuffer robotConfigurationDataBuffer, PacketCommunicator packetCommunicator,
         PPSTimestampOffsetProvider ppsTimestampOffsetProvider, final RosMainNode rosMainNode)
   {
      super(fullRobotModelFactory, cameraParameters.getSensorNameInSdf(), robotConfigurationDataBuffer, new CompressedFisheyeHandler(packetCommunicator),
            ppsTimestampOffsetProvider);

      BlackFlyParameterSetter blackFlyParameterSetter = new BlackFlyParameterSetter(rosMainNode, packetCommunicator);

      if (!cameraParameters.useIntrinsicParametersFromRos())
      {
         throw new RuntimeException("You really want to use intrinisic parameters from ROS");
      }

      final RosCameraInfoSubscriber imageInfoSubscriber = new RosCameraInfoSubscriber(cameraParameters.getRosCameraInfoTopicName());
      rosMainNode.attachSubscriber(cameraParameters.getRosCameraInfoTopicName(), imageInfoSubscriber);

      RosCompressedImageSubscriber imageSubscriberSubscriber = new RosCompressedImageSubscriber()
      {
         @Override
         protected void imageReceived(long timeStamp, BufferedImage image)
         {
            IntrinsicParameters intrinsicParameters = imageInfoSubscriber.getIntrinisicParameters();
            updateImage(image, timeStamp, intrinsicParameters);

         }
      };
      rosMainNode.attachSubscriber(cameraParameters.getRosTopic(), imageSubscriberSubscriber);

      blackFlyParameterSetter.initializeParameterListeners();

   }

   private static class CompressedFisheyeHandler implements CompressedVideoHandler
   {
      private final PacketCommunicator packetCommunicator;

      public CompressedFisheyeHandler(PacketCommunicator packetCommunicator)
      {
         this.packetCommunicator = packetCommunicator;
      }

      @Override
      public void newVideoPacketAvailable(RobotSide robotSide, long timeStamp, byte[] data, Point3d position, Quat4d orientation,
            IntrinsicParameters intrinsicParameters)
      {
         packetCommunicator.send(new FisheyePacket(robotSide, timeStamp, data, position, orientation, intrinsicParameters));
      }

      @Override
      public void addNetStateListener(NetStateListener compressedVideoDataServer)
      {
         packetCommunicator.attachStateListener(compressedVideoDataServer);
      }

      @Override
      public boolean isConnected()
      {
         return packetCommunicator.isConnected();
      }

   }

}
