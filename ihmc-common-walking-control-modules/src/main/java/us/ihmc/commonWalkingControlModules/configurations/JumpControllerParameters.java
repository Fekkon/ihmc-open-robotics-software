package us.ihmc.commonWalkingControlModules.configurations;

import us.ihmc.commonWalkingControlModules.momentumBasedController.optimization.MomentumOptimizationSettings;

public abstract class JumpControllerParameters extends AbstractHighLevelControllerParameters
{
   
   public JumpControllerParameters()
   {
      super();
   }
   
   public abstract MomentumOptimizationSettings getMomentumOptimizationSettings();

}