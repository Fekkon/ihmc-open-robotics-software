package us.ihmc.pathPlanning;

import us.ihmc.robotics.geometry.PlanarRegionsList;

import java.util.ArrayList;
import java.util.List;

public class DataSet
{
   private final String name;
   private final PlanarRegionsList planarRegionsList;
   private PlannerInput plannerInput = null;

   DataSet(String name, PlanarRegionsList planarRegionsList)
   {
      this.name = name;
      this.planarRegionsList = planarRegionsList;
   }

   public String getName()
   {
      return name;
   }

   public PlanarRegionsList getPlanarRegionsList()
   {
      return planarRegionsList;
   }

   public boolean hasPlannerInput()
   {
      return plannerInput != null;
   }

   public PlannerInput getPlannerInput()
   {
      return plannerInput;
   }

   void setPlannerInput(PlannerInput plannerInput)
   {
      this.plannerInput = plannerInput;
   }
}