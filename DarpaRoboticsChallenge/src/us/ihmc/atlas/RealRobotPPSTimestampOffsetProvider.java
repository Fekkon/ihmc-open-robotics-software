package us.ihmc.atlas;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.zeromq.ZMQ;

import std_msgs.Time;
import us.ihmc.darpaRoboticsChallenge.DRCConfigParameters;
import us.ihmc.utilities.ros.RosMainNode;
import us.ihmc.utilities.ros.RosMultisensePPSSubscriber;

public class RealRobotPPSTimestampOffsetProvider implements PPSTimestampOffsetProvider
{
   private static final String MULTISENSE_SL_PPS_TOPIC = "/multisense_sl/pps";
   private RosMultisensePPSSubscriber ppsSubscriber;
   private final AtomicLong currentTimeStampOffset = new AtomicLong(0);
   private ZMQ.Socket requester;

   private final byte[] requestPayload = {PPSRequestType.GET_NEW_PPS_TIMESTAMP};
   private final ByteBuffer responseBuffer = ByteBuffer.allocate(8);
   
   private final AtomicBoolean offsetIsDetermined = new AtomicBoolean(false);

   public RealRobotPPSTimestampOffsetProvider()
   {
      setupZMQSocket();

      setupPPSSubscriber();
   }

   private void setupPPSSubscriber()
   {
      ppsSubscriber = new RosMultisensePPSSubscriber()
      {
         @Override
         public void onNewMessage(Time message)
         {
            currentTimeStampOffset.set(requestNewestRobotTimestamp() - message.getData().totalNsecs());
            System.out.println("offset: " + currentTimeStampOffset.get()/1000000);
            offsetIsDetermined.set(true);
         }
      };
   }

   private void setupZMQSocket()
   {
      ZMQ.Context context = ZMQ.context(1);
      requester = context.socket(ZMQ.REQ);
      requester.connect("tcp://" + DRCConfigParameters.SCS_MACHINE_IP_ADDRESS + ":" + DRCConfigParameters.PPS_PROVIDER_PORT);
   }

   @Override
   public void attachToRosMainNode(RosMainNode rosMainNode)
   {
      rosMainNode.attachSubscriber(MULTISENSE_SL_PPS_TOPIC, ppsSubscriber);
   }

   @Override
   public long getCurrentTimestampOffset()
   {
      return currentTimeStampOffset.get();
   }

   @Override
   public long ajustTimeStampToRobotClock(long timeStamp)
   {
      return timeStamp + currentTimeStampOffset.get();
   }

   
   @Override
   public long requestNewestRobotTimestamp()
   {
      requester.send(requestPayload, 0);
      responseBuffer.rewind();
      responseBuffer.put(requester.recv());
      return responseBuffer.getLong(0);
   }

   @Override
   public boolean offsetIsDetermined()
   {
      return offsetIsDetermined.get();
   }

}