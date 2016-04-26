package us.ihmc.tools.thread;

public class NonRealTimeThreadCreatorAndStarter implements ThreadCreatorAndStarter
{
   @Override
   public void createAndStartAThread(Runnable runnable)
   {
      Thread thread = new Thread(runnable);
      thread.start();
   }
}
