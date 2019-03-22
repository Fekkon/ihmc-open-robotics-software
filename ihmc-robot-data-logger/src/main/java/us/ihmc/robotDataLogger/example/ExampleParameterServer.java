package us.ihmc.robotDataLogger.example;

import us.ihmc.log.LogTools;
import us.ihmc.robotDataLogger.YoVariableServer;
import us.ihmc.robotDataLogger.example.ExampleServer.SomeEnum;
import us.ihmc.robotDataLogger.logger.DataServerSettings;
import us.ihmc.yoVariables.listener.ParameterChangedListener;
import us.ihmc.yoVariables.parameters.BooleanParameter;
import us.ihmc.yoVariables.parameters.DefaultParameterReader;
import us.ihmc.yoVariables.parameters.DoubleParameter;
import us.ihmc.yoVariables.parameters.EnumParameter;
import us.ihmc.yoVariables.parameters.IntegerParameter;
import us.ihmc.yoVariables.registry.YoVariableRegistry;

public class ExampleParameterServer
{
   private static final double dt = 0.001;
   private static final DataServerSettings logSettings = new DataServerSettings(false, false);

   private final YoVariableServer yoVariableServer;
   private final YoVariableRegistry registry = new YoVariableRegistry(getClass().getSimpleName());

   public ExampleParameterServer()
   {
      createVariables(5, registry);
      yoVariableServer = new YoVariableServer(getClass(), null, logSettings, dt);
      yoVariableServer.setMainRegistry(registry, null, null);
      new DefaultParameterReader().readParametersInRegistry(registry);
      ParameterChangedListener changedPrinter = p -> System.out.println(p.getName() + " changed to " + p.getValueAsString());
      registry.getAllParameters().forEach(p -> p.addParameterChangedListener(changedPrinter ));
   }

   public void start()
   {
      yoVariableServer.start();
   }

   private void createVariables(int variablesPerType, YoVariableRegistry registry)
   {
      for (int i = 0; i < variablesPerType; i++)
      {
         new BooleanParameter("BooleanParameter" + i, registry);
         new DoubleParameter("DoubleParameter" + i, registry);
         new IntegerParameter("IntegerParameter" + i, registry);
         new EnumParameter<>("EnumParameter" + i, registry, SomeEnum.class, true);
      }
   }

   public static void main(String[] args)
   {
      LogTools.info("Starting " + ExampleParameterServer.class.getSimpleName());
      ExampleParameterServer exampleServer = new ExampleParameterServer();
      exampleServer.start();
   }
}
