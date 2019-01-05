package us.ihmc.quadrupedPlanning.pathPlanning;

import com.vividsolutions.jts.math.Vector2D;
import gnu.trove.list.array.TDoubleArrayList;
import us.ihmc.euclid.geometry.Pose2D;
import us.ihmc.euclid.geometry.interfaces.Pose2DReadOnly;

import java.util.ArrayList;
import java.util.List;

public class QuadrupedBodyPathPlan
{
   private final Pose2D startPose = new Pose2D();
   private final Pose2D goalPose = new Pose2D();

   private final List<Pose2D> bodyPathPoseWaypoints = new ArrayList<>();
   private final List<Vector2D> bodyPathLinearVelocityWaypoints = new ArrayList<>();

   private final TDoubleArrayList bodyPathYawRateWaypoints = new TDoubleArrayList();
   private final TDoubleArrayList bodyPathTimes = new TDoubleArrayList();

   public void addWaypoint(Pose2DReadOnly waypointPose, Vector2D linearVelocity, double yawRate, double time)
   {
      bodyPathPoseWaypoints.add(new Pose2D(waypointPose));
      bodyPathLinearVelocityWaypoints.add(new Vector2D(linearVelocity));
      bodyPathYawRateWaypoints.add(yawRate);
      bodyPathTimes.add(time);
   }

   public void clear()
   {
      bodyPathPoseWaypoints.clear();
      bodyPathLinearVelocityWaypoints.clear();

      bodyPathYawRateWaypoints.clear();
      bodyPathTimes.clear();
   }

   public void setStartPose(Pose2DReadOnly startPose)
   {
      this.startPose.set(startPose);
   }

   public void setGoalPose(Pose2DReadOnly goalPose)
   {
      this.goalPose.set(goalPose);
   }

}
