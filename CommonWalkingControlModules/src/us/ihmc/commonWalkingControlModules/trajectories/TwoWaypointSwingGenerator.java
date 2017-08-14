package us.ihmc.commonWalkingControlModules.trajectories;

import java.util.ArrayList;
import java.util.List;

import us.ihmc.commons.PrintTools;
import us.ihmc.graphicsDescription.appearance.YoAppearance;
import us.ihmc.graphicsDescription.yoGraphics.BagOfBalls;
import us.ihmc.graphicsDescription.yoGraphics.YoGraphicsListRegistry;
import us.ihmc.robotics.MathTools;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoBoolean;
import us.ihmc.yoVariables.variable.YoDouble;
import us.ihmc.robotics.geometry.FramePoint3D;
import us.ihmc.robotics.geometry.FrameVector;
import us.ihmc.robotics.lists.RecyclingArrayList;
import us.ihmc.robotics.math.trajectories.PositionTrajectoryGenerator;
import us.ihmc.robotics.math.trajectories.waypoints.FrameEuclideanTrajectoryPoint;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;
import us.ihmc.robotics.trajectories.TrajectoryType;

public class TwoWaypointSwingGenerator implements PositionTrajectoryGenerator
{
   private static final int maxTimeIterations = -1; // setting this negative activates continuous updating
   private static final int numberWaypoints = 2;
   private static final double[] defaultWaypointProportions = new double[] {0.15, 0.85};

   private static final ReferenceFrame worldFrame = ReferenceFrame.getWorldFrame();

   private final YoVariableRegistry registry;

   private final YoDouble stepTime;
   private final YoDouble timeIntoStep;
   private final YoBoolean isDone;
   private final YoDouble swingHeight;
   private final YoDouble maxSwingHeight;
   private final YoDouble minSwingHeight;

   private final List<YoDouble> waypointProportions = new ArrayList<>();
   private final List<YoDouble> obstacleClearanceWaypointProportions = new ArrayList<>();

   private TrajectoryType trajectoryType;
   private final PositionOptimizedTrajectoryGenerator trajectory;

   private final FramePoint3D initialPosition = new FramePoint3D();
   private final FrameVector initialVelocity = new FrameVector();
   private final FramePoint3D finalPosition = new FramePoint3D();
   private final FrameVector finalVelocity = new FrameVector();
   private final ArrayList<FramePoint3D> waypointPositions = new ArrayList<>();
   private final FramePoint3D stanceFootPosition = new FramePoint3D();

   private final FrameVector initialVelocityNoTimeDimension = new FrameVector();
   private final FrameVector finalVelocityNoTimeDiemension = new FrameVector();
   private final FrameVector tempWaypointVelocity = new FrameVector();

   private final BagOfBalls waypointViz;

   public TwoWaypointSwingGenerator(String namePrefix, double minSwingHeight, double maxSwingHeight, YoVariableRegistry parentRegistry,
         YoGraphicsListRegistry yoGraphicsListRegistry)
   {
      this(namePrefix, null, null, minSwingHeight, maxSwingHeight, parentRegistry, yoGraphicsListRegistry);
   }

   public TwoWaypointSwingGenerator(String namePrefix, double[] waypointProportions, double[] obstacleClearanceProportions, double minSwingHeight,
         double maxSwingHeight, YoVariableRegistry parentRegistry, YoGraphicsListRegistry yoGraphicsListRegistry)
   {
      registry = new YoVariableRegistry(namePrefix + getClass().getSimpleName());
      parentRegistry.addChild(registry);

      stepTime = new YoDouble(namePrefix + "StepTime", registry);
      timeIntoStep = new YoDouble(namePrefix + "TimeIntoStep", registry);
      isDone = new YoBoolean(namePrefix + "IsDone", registry);
      swingHeight = new YoDouble(namePrefix + "SwingHeight", registry);
      swingHeight.set(minSwingHeight);

      this.maxSwingHeight = new YoDouble(namePrefix + "MaxSwingHeight", registry);
      this.maxSwingHeight.set(maxSwingHeight);

      this.minSwingHeight = new YoDouble(namePrefix + "MinSwingHeight", registry);
      this.minSwingHeight.set(minSwingHeight);

      if (waypointProportions == null)
         waypointProportions = defaultWaypointProportions;
      if (obstacleClearanceProportions == null)
         obstacleClearanceProportions = defaultWaypointProportions;

      for (int i = 0; i < numberWaypoints; i++)
      {
         YoDouble waypointProportion = new YoDouble(namePrefix + "WaypointProportion" + i, registry);
         YoDouble obstacleClearanceWaypointProportion = new YoDouble(namePrefix + "ObstacleClearanceWaypointProportion" + i, registry);
         waypointProportion.set(waypointProportions[i]);
         obstacleClearanceWaypointProportion.set(obstacleClearanceProportions[i]);
         this.waypointProportions.add(waypointProportion);
         this.obstacleClearanceWaypointProportions.add(obstacleClearanceWaypointProportion);
      }

      trajectory = new PositionOptimizedTrajectoryGenerator(namePrefix, registry, yoGraphicsListRegistry, maxTimeIterations, numberWaypoints);

      for (int i = 0; i < numberWaypoints; i++)
         waypointPositions.add(new FramePoint3D());

      if (yoGraphicsListRegistry != null)
         waypointViz = new BagOfBalls(numberWaypoints, 0.02, namePrefix + "Waypoints", YoAppearance.White(), registry, yoGraphicsListRegistry);
      else
         waypointViz = null;
   }

   public void setStepTime(double stepTime)
   {
      this.stepTime.set(stepTime);
   }

   public void setInitialConditions(FramePoint3D initialPosition, FrameVector initialVelocity)
   {
      this.initialPosition.setIncludingFrame(initialPosition);
      this.initialVelocity.setIncludingFrame(initialVelocity);
   }

   public void setFinalConditions(FramePoint3D finalPosition, FrameVector finalVelocity)
   {
      this.finalPosition.setIncludingFrame(finalPosition);
      this.finalVelocity.setIncludingFrame(finalVelocity);
   }

   public void setTrajectoryType(TrajectoryType trajectoryType)
   {
      setTrajectoryType(trajectoryType, null);
   }

   public void setTrajectoryType(TrajectoryType trajectoryType, RecyclingArrayList<FramePoint3D> waypoints)
   {
      if (trajectoryType == TrajectoryType.CUSTOM && waypoints == null)
      {
         PrintTools.warn("Recieved no waypoints but trajectory type is custom. Using default trajectory.");
         this.trajectoryType = TrajectoryType.DEFAULT;
      }
      else if (trajectoryType == TrajectoryType.CUSTOM && waypoints.size() != numberWaypoints)
      {
         PrintTools.warn("Recieved unexpected amount of waypoints. Using default trajectory.");
         this.trajectoryType = TrajectoryType.DEFAULT;
      }
      else
      {
         this.trajectoryType = trajectoryType;
      }

      if (this.trajectoryType != TrajectoryType.CUSTOM)
         return;

      for (int i = 0; i < numberWaypoints; i++)
      {
         waypointPositions.get(i).setIncludingFrame(waypoints.get(i));
         waypointPositions.get(i).changeFrame(worldFrame);
      }
   }

   public void setSwingHeight(double swingHeight)
   {
      if (Double.isNaN(swingHeight))
         this.swingHeight.set(minSwingHeight.getDoubleValue());
      else if (swingHeight < minSwingHeight.getDoubleValue())
         this.swingHeight.set(minSwingHeight.getDoubleValue());
      else if (swingHeight > maxSwingHeight.getDoubleValue())
         this.swingHeight.set(maxSwingHeight.getDoubleValue());
      else
         this.swingHeight.set(swingHeight);
   }

   public void setStanceFootPosition(FramePoint3D stanceFootPosition)
   {
      this.stanceFootPosition.setIncludingFrame(stanceFootPosition);
   }

   public void informDone()
   {
      trajectory.informDone();
   }

   @Override
   public void initialize()
   {
      timeIntoStep.set(0.0);
      isDone.set(false);

      initialPosition.changeFrame(worldFrame);
      finalPosition.changeFrame(worldFrame);
      double maxStepZ = Math.max(initialPosition.getZ(), finalPosition.getZ());

      switch (trajectoryType)
      {
      case OBSTACLE_CLEARANCE:
         for (int i = 0; i < numberWaypoints; i++)
         {
            waypointPositions.get(i).interpolate(initialPosition, finalPosition, obstacleClearanceWaypointProportions.get(i).getDoubleValue());
            waypointPositions.get(i).setZ(maxStepZ + swingHeight.getDoubleValue());
         }
         break;
      case DEFAULT:
         for (int i = 0; i < numberWaypoints; i++)
         {
            waypointPositions.get(i).interpolate(initialPosition, finalPosition, waypointProportions.get(i).getDoubleValue());
            waypointPositions.get(i).add(0.0, 0.0, swingHeight.getDoubleValue());
         }
         break;
      case CUSTOM:
         break;
      default:
         throw new RuntimeException("Trajectory type not implemented");
      }

      stanceFootPosition.changeFrame(worldFrame);
      double maxWaypointZ = Math.max(stanceFootPosition.getZ() + maxSwingHeight.getDoubleValue(), maxStepZ + minSwingHeight.getDoubleValue());
      for (int i = 0; i < numberWaypoints; i++)
      {
         waypointPositions.get(i).setZ(Math.min(waypointPositions.get(i).getZ(), maxWaypointZ));
      }

      initialVelocityNoTimeDimension.setIncludingFrame(initialVelocity);
      finalVelocityNoTimeDiemension.setIncludingFrame(finalVelocity);

      initialVelocityNoTimeDimension.scale(stepTime.getDoubleValue());
      finalVelocityNoTimeDiemension.scale(stepTime.getDoubleValue());

      trajectory.setEndpointConditions(initialPosition, initialVelocityNoTimeDimension, finalPosition, finalVelocityNoTimeDiemension);
      trajectory.setWaypoints(waypointPositions);
      trajectory.initialize();

      visualize();
   }

   private void visualize()
   {
      if (waypointViz == null)
         return;

      for (int i = 0; i < numberWaypoints; i++)
         waypointViz.setBall(waypointPositions.get(i), i);
   }
   
   public boolean doOptimizationUpdate()
   {
      return trajectory.doOptimizationUpdate();
   }

   @Override
   public void compute(double time)
   {
      double trajectoryTime = stepTime.getDoubleValue();
      isDone.set(time >= trajectoryTime);

      time = MathTools.clamp(time, 0.0, trajectoryTime);
      timeIntoStep.set(time);

      double percent = time / trajectoryTime;
      trajectory.compute(percent);
   }

   @Override
   public boolean isDone()
   {
      return isDone.getBooleanValue();
   }

   @Override
   public void getPosition(FramePoint3D positionToPack)
   {
      trajectory.getPosition(positionToPack);
   }

   @Override
   public void getVelocity(FrameVector velocityToPack)
   {
      trajectory.getVelocity(velocityToPack);
      velocityToPack.scale(1.0 / stepTime.getDoubleValue());
   }

   @Override
   public void getAcceleration(FrameVector accelerationToPack)
   {
      trajectory.getAcceleration(accelerationToPack);
      accelerationToPack.scale(1.0 / stepTime.getDoubleValue());
      accelerationToPack.scale(1.0 / stepTime.getDoubleValue());
   }

   @Override
   public void getLinearData(FramePoint3D positionToPack, FrameVector velocityToPack, FrameVector accelerationToPack)
   {
      getPosition(positionToPack);
      getVelocity(velocityToPack);
      getAcceleration(accelerationToPack);
   }

   @Override
   public void showVisualization()
   {
      trajectory.showVisualization();
   }

   @Override
   public void hideVisualization()
   {
      trajectory.hideVisualization();
   }

   public static double[] getDefaultWaypointProportions()
   {
      return defaultWaypointProportions;
   }
   
   public int getNumberOfWaypoints()
   {
      return numberWaypoints;
   }

   public void getWaypointData(int waypointIndex, FrameEuclideanTrajectoryPoint waypointDataToPack)
   {
      double waypointTime = stepTime.getDoubleValue() * trajectory.getWaypointTime(waypointIndex);
      trajectory.getWaypointVelocity(waypointIndex, tempWaypointVelocity);
      tempWaypointVelocity.scale(1.0 / stepTime.getDoubleValue());

      waypointDataToPack.setToNaN(worldFrame);
      waypointDataToPack.setTime(waypointTime);
      waypointDataToPack.setPosition(waypointPositions.get(waypointIndex));
      waypointDataToPack.setLinearVelocity(tempWaypointVelocity);
   }

   public double computeAndGetMaxSpeed()
   {
      trajectory.computeMaxSpeed();
      return trajectory.getMaxSpeed() / stepTime.getDoubleValue();
   }
}