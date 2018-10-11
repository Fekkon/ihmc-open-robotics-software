package us.ihmc.footstepPlanning.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import us.ihmc.footstepPlanning.communication.FootstepPlannerSharedMemoryAPI;
import us.ihmc.javaFXToolkit.messager.JavaFXMessager;
import us.ihmc.javaFXToolkit.messager.SharedMemoryJavaFXMessager;
import us.ihmc.javaFXToolkit.messager.SharedMemoryMessager;
import us.ihmc.pubsub.DomainFactory;

/**
 * This class provides a visualizer for the remote footstep planner found in the footstep planner toolbox.
 * It allows users to view the resulting plans calculated by the toolbox. It also allows the user to tune
 * the planner parameters, and request a new plan from the planning toolbox.
 */
public class RemoteFootstepPlannerUI extends Application
{

   private SharedMemoryJavaFXMessager messager;
   private RemoteUIMessageConverter messageConverter;

   private FootstepPlannerUI ui;

   @Override
   public void start(Stage primaryStage) throws Exception
   {
      messager = new SharedMemoryJavaFXMessager(FootstepPlannerSharedMemoryAPI.API);
      messageConverter = RemoteUIMessageConverter.createConverter(messager, "", DomainFactory.PubSubImplementation.INTRAPROCESS);

      messager.startMessager();

      ui = FootstepPlannerUI.createMessagerUI(primaryStage, messager);
      ui.show();
   }

   @Override
   public void stop() throws Exception
   {
      super.stop();

      messager.closeMessager();
      messageConverter.destroy();
      ui.stop();

      Platform.exit();
   }

   public static void main(String[] args)
   {
      launch(args);
   }
}
