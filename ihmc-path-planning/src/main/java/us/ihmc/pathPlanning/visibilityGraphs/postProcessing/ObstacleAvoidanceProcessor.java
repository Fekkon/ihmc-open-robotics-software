package us.ihmc.pathPlanning.visibilityGraphs.postProcessing;

import us.ihmc.euclid.geometry.tools.EuclidGeometryTools;
import us.ihmc.euclid.transform.RigidBodyTransform;
import us.ihmc.euclid.tuple2D.Point2D;
import us.ihmc.euclid.tuple2D.Vector2D;
import us.ihmc.euclid.tuple2D.interfaces.Point2DReadOnly;
import us.ihmc.euclid.tuple2D.interfaces.Vector2DReadOnly;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple3D.interfaces.Point3DReadOnly;
import us.ihmc.pathPlanning.visibilityGraphs.NavigableRegions;
import us.ihmc.pathPlanning.visibilityGraphs.clusterManagement.Cluster;
import us.ihmc.pathPlanning.visibilityGraphs.dataStructure.NavigableRegion;
import us.ihmc.pathPlanning.visibilityGraphs.dataStructure.VisibilityGraphNode;
import us.ihmc.pathPlanning.visibilityGraphs.dataStructure.VisibilityMapSolution;
import us.ihmc.pathPlanning.visibilityGraphs.interfaces.VisibilityGraphsParameters;
import us.ihmc.pathPlanning.visibilityGraphs.tools.PlanarRegionTools;
import us.ihmc.pathPlanning.visibilityGraphs.tools.VisibilityTools;
import us.ihmc.robotics.geometry.PlanarRegion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ObstacleAvoidanceProcessor
{
   private static final boolean includeMidpoints = true;
   private static final boolean adjustWaypoints = true;
   private static final boolean adjustMidpoints = true;

   private static final double minDistanceToMove = 0.01;
   private static final double cliffHeightToAvoid = 0.10;
   private static final double samePointEpsilon = 0.01;

   private final double desiredDistanceFromObstacleCluster;
   private final double desiredDistanceFromCliff;
   private final double minimumDistanceFromCliff; // FIXME this is currently unused
   private final double maxInterRegionConnectionLength;
   private final double clusterResolution;
   private final IntermediateComparator comparator = new IntermediateComparator();

   public ObstacleAvoidanceProcessor(VisibilityGraphsParameters parameters)
   {
      desiredDistanceFromObstacleCluster = parameters.getPreferredObstacleExtrusionDistance() - parameters.getObstacleExtrusionDistance();
      maxInterRegionConnectionLength = parameters.getMaxInterRegionConnectionLength();
      desiredDistanceFromCliff = parameters.getPreferredObstacleExtrusionDistance() - parameters.getNavigableExtrusionDistance();
//      desiredDistanceFromCliff = 0.5;
      minimumDistanceFromCliff = parameters.getObstacleExtrusionDistance();
      clusterResolution = 0.1;
   }

   public List<Point3DReadOnly> computePathFromNodes(List<VisibilityGraphNode> nodePath, VisibilityMapSolution visibilityMapSolution)
   {
      List<Point3D> newPath = nodePath.parallelStream().map(node -> new Point3D(node.getPointInWorld())).collect(Collectors.toList());

      int pathNodeIndex = 0;
      int waypointIndex = 0;
      // don't do the goal node
      while (pathNodeIndex < nodePath.size() - 1)
      {
         int nextPathNodeIndex = pathNodeIndex + 1;
         int nextWaypointIndex = waypointIndex + 1;

         Point3D startPointInWorld = newPath.get(waypointIndex);
         Point3D endPointInWorld = newPath.get(nextWaypointIndex);

         VisibilityGraphNode startVisGraphNode = nodePath.get(pathNodeIndex);
         VisibilityGraphNode endVisGraphNode = nodePath.get(nextPathNodeIndex);

         boolean isGoalNode = pathNodeIndex > nodePath.size() - 2;

         NavigableRegion startingRegion = startVisGraphNode.getVisibilityGraphNavigableRegion().getNavigableRegion();
         NavigableRegion endingRegion = endVisGraphNode.getVisibilityGraphNavigableRegion().getNavigableRegion();
         NavigableRegions allNavigableRegions = visibilityMapSolution.getNavigableRegions();

         if (!isGoalNode && adjustWaypoints)
         {
            adjustGoalNodePositionToAvoidObstaclesAndCliffs(endPointInWorld, startingRegion, endingRegion, allNavigableRegions);
         }


         if (includeMidpoints)
         {
            List<Point3D> intermediateWaypointsToAdd = computeIntermediateWaypointsToAddToAvoidObstacles(new Point2D(startPointInWorld), new Point2D(endPointInWorld), startVisGraphNode,
                                                                                                         endVisGraphNode);
            removeDuplicated3DPointsFromList(intermediateWaypointsToAdd, clusterResolution);
            removeDuplicateStartOrEndPointsFromList(intermediateWaypointsToAdd, startPointInWorld, endPointInWorld, clusterResolution);

            // shift all the points around
            if (adjustMidpoints)// && pathNodeIndex < 3 )
            {
               for (Point3D intermediateWaypointToAdd : intermediateWaypointsToAdd)
               {
                  adjustGoalNodePositionToAvoidObstaclesAndCliffs(intermediateWaypointToAdd, startingRegion, endingRegion, allNavigableRegions);
               }
            }

            // prune duplicated points
            removeDuplicated3DPointsFromList(intermediateWaypointsToAdd, clusterResolution);
            removeDuplicateStartOrEndPointsFromList(intermediateWaypointsToAdd, startPointInWorld, endPointInWorld, clusterResolution);

            for (Point3D intermediateWaypointToAdd : intermediateWaypointsToAdd)
            {
               waypointIndex++;
               newPath.add(waypointIndex, intermediateWaypointToAdd);
            }
         }

         waypointIndex++;
         pathNodeIndex++;
      }

      return newPath.parallelStream().map(Point3D::new).collect(Collectors.toList());
   }

   private void adjustGoalNodePositionToAvoidObstaclesAndCliffs(Point3D nodeLocationToPack, NavigableRegion startRegion, NavigableRegion endRegion,
                                                                NavigableRegions allNavigableRegions)
   {
      Point2D nextPointInWorld2D = new Point2D(nodeLocationToPack);

      List<Cluster> obstacleClusters = new ArrayList<>(startRegion.getObstacleClusters());
      if (!startRegion.equals(endRegion))
         obstacleClusters.addAll(endRegion.getObstacleClusters());

      List<Point2DReadOnly> closestObstacleClusterPoints = getClosestPointsOnClusters(nextPointInWorld2D, obstacleClusters);
      Vector2DReadOnly nodeShiftToAvoidObstacles = computeVectorToMaximizeAverageDistanceFromPoints(nextPointInWorld2D, closestObstacleClusterPoints,
                                                                                                    desiredDistanceFromObstacleCluster);

      Point2D shiftedPoint = new Point2D(nodeLocationToPack);
      shiftedPoint.add(nodeShiftToAvoidObstacles);

      // FIXME should be both regions
      boolean isShiftedPointNearACliff = isNearCliff(shiftedPoint, maxInterRegionConnectionLength, cliffHeightToAvoid, endRegion,
                                                     allNavigableRegions.getNaviableRegionsList());

      Vector2D nodeShift = new Vector2D();

      if (isShiftedPointNearACliff)
      {
         List<Cluster> homeRegionClusters = new ArrayList<>();
         homeRegionClusters.add(startRegion.getHomeRegionCluster());
         if (!startRegion.equals(endRegion))
            homeRegionClusters.add(endRegion.getHomeRegionCluster());

         List<Point2DReadOnly> closestCliffObstacleClusterPoints = getClosestPointsOnClusters(nextPointInWorld2D, homeRegionClusters);
         nodeShift.set(computeVectorToMaximizeAverageDistanceFromPoints(nextPointInWorld2D, closestObstacleClusterPoints, closestCliffObstacleClusterPoints,
                                                                        desiredDistanceFromObstacleCluster, desiredDistanceFromCliff));
      }
      else
      {
         nodeShift.set(nodeShiftToAvoidObstacles);
      }

      if (nodeShift.length() > minDistanceToMove)
      {
         nextPointInWorld2D.add(nodeShift);

         // FIXME should be both regions
         double newHeight = startRegion.getPlaneZGivenXY(nextPointInWorld2D.getX(), nextPointInWorld2D.getY());
         nodeLocationToPack.set(nextPointInWorld2D, newHeight);
      }
   }

   private List<Point3D> computeIntermediateWaypointsToAddToAvoidObstacles(Point2DReadOnly originPointInWorld, Point2DReadOnly nextPointInWorld,
                                                                           VisibilityGraphNode connectionStartNode, VisibilityGraphNode connectionEndNode)
   {
      List<NavigableRegion> navigableRegionsToSearch = new ArrayList<>();
      NavigableRegion startRegion = connectionStartNode.getVisibilityGraphNavigableRegion().getNavigableRegion();
      NavigableRegion endRegion = connectionEndNode.getVisibilityGraphNavigableRegion().getNavigableRegion();
      navigableRegionsToSearch.add(startRegion);
      if (!startRegion.equals(endRegion))
         navigableRegionsToSearch.add(endRegion);

      return computeIntermediateWaypointsToAddToAvoidObstacles(originPointInWorld, nextPointInWorld, navigableRegionsToSearch);
   }

   private List<Point3D> computeIntermediateWaypointsToAddToAvoidObstacles(Point2DReadOnly originPointInWorld2D, Point2DReadOnly nextPointInWorld2D,
                                                                           List<NavigableRegion> navigableRegionsToSearch)
   {
      List<Point2D> intermediateWaypointsToAdd = new ArrayList<>();

      for (NavigableRegion navigableRegion : navigableRegionsToSearch)
      {
         for (Cluster cluster : navigableRegion.getObstacleClusters())
         {
            List<Point2DReadOnly> clusterPolygon = cluster.getNonNavigableExtrusionsInWorld2D();
            boolean isClosed = cluster.isClosed();

            Point2D closestPointInCluster = new Point2D();
            Point2D closestPointOnConnection = new Point2D();

            double connectionDistanceToObstacle = VisibilityTools
                  .distanceToCluster(originPointInWorld2D, nextPointInWorld2D, clusterPolygon, closestPointOnConnection, closestPointInCluster, null,
                                     isClosed);
            if (connectionDistanceToObstacle < desiredDistanceFromObstacleCluster)
            {
               if (!intermediateWaypointsToAdd.contains(closestPointOnConnection))
               {
                  intermediateWaypointsToAdd.add(closestPointOnConnection);
               }
            }
         }
      }

      comparator.setStartPoint(originPointInWorld2D);
      comparator.setEndPoint(nextPointInWorld2D);
      intermediateWaypointsToAdd.sort(comparator);

      // collapse intermediate waypoints
      int intermediateWaypointIndex = 0;
      while (intermediateWaypointIndex < intermediateWaypointsToAdd.size() - 1)
      {
         Point2D thisWaypoint = intermediateWaypointsToAdd.get(intermediateWaypointIndex);
         Point2D nextWaypoint = intermediateWaypointsToAdd.get(intermediateWaypointIndex + 1);
         if (thisWaypoint.distance(nextWaypoint) < clusterResolution)
         { // collapse with the next one
            thisWaypoint.interpolate(thisWaypoint, nextWaypoint, 0.5);
            intermediateWaypointsToAdd.remove(intermediateWaypointIndex + 1);
         }
         else
         {
            intermediateWaypointIndex++;
         }
      }

      List<Point3D> intermediateWaypoints3DToAdd = new ArrayList<>();
      for (Point2D intermediateWaypoint : intermediateWaypointsToAdd)
      {
         double maxHeight = Double.NEGATIVE_INFINITY;
         for (NavigableRegion navigableRegion : navigableRegionsToSearch)
         {
            double height = navigableRegion.getPlaneZGivenXY(intermediateWaypoint.getX(), intermediateWaypoint.getY());
            if (height > maxHeight)
               maxHeight = height;
         }

         intermediateWaypoints3DToAdd.add(new Point3D(intermediateWaypoint.getX(), intermediateWaypoint.getY(), maxHeight));
      }

      return intermediateWaypoints3DToAdd;
   }

   private static List<Point2DReadOnly> getClosestPointsOnClusters(Point2DReadOnly pointInWorld, List<Cluster> clustersInWorld)
   {
      List<Point2DReadOnly> closestClusterPoints = new ArrayList<>();

      for (Cluster cluster : clustersInWorld)
      {
         List<Point2DReadOnly> clusterPolygon = cluster.getNonNavigableExtrusionsInWorld2D();

         Point2D closestPointInCluster = new Point2D();
         VisibilityTools.distanceToCluster(pointInWorld, clusterPolygon, closestPointInCluster, null);
         closestClusterPoints.add(closestPointInCluster);
      }

      removeDuplicated2DPointsFromList(closestClusterPoints, samePointEpsilon);

      return closestClusterPoints;
   }

   private static Vector2DReadOnly computeVectorToMaximizeAverageDistanceFromPoints(Point2DReadOnly pointToShift,
                                                                                    List<Point2DReadOnly> pointsToAvoidByDistanceA, double distanceA)
   {
      return computeVectorToMaximizeAverageDistanceFromPoints(pointToShift, pointsToAvoidByDistanceA, new ArrayList<>(), distanceA, 0.0);
   }

   private static Vector2DReadOnly computeVectorToMaximizeAverageDistanceFromPoints(Point2DReadOnly pointToShift,
                                                                                    List<Point2DReadOnly> pointsToAvoidByDistanceA,
                                                                                    List<Point2DReadOnly> pointsToAvoidByDistanceB, double distanceA,
                                                                                    double distanceB)
   {
      Vector2D averageShiftVector = new Vector2D();
      int numberOfPointsWithinProximity = 0;

      // sort these by distance
      List<Point2DReadOnly> pointsToAvoid = new ArrayList<>(pointsToAvoidByDistanceA);
      pointsToAvoid.addAll(pointsToAvoidByDistanceB);
      pointsToAvoid.sort((a, b) -> {
         double distanceForPointA = pointsToAvoidByDistanceA.contains(a) ? distanceA : distanceB;
         double distanceForPointB = pointsToAvoidByDistanceA.contains(b) ? distanceA : distanceB;

         double aShiftDistance = pointToShift.distance(a) - distanceForPointA;
         double bShiftDistance = pointToShift.distance(b) - distanceForPointB;

         return Double.compare(aShiftDistance, bShiftDistance);
      });

      Vector2D vectorToPoint = new Vector2D();

      for (Point2DReadOnly pointToShiftFrom : pointsToAvoid)
      {
         vectorToPoint.sub(pointToShift, pointToShiftFrom);
         double distanceToPoint = vectorToPoint.length();
         double distanceAfterShifting = distanceToPoint + averageShiftVector.dot(vectorToPoint);

         double distanceToAchieve = pointsToAvoidByDistanceA.contains(pointToShiftFrom) ? distanceA : distanceB;
         if (distanceAfterShifting < distanceToAchieve)
         {
            double extraDistanceToShift = distanceToAchieve - distanceToPoint;
            vectorToPoint.scale(extraDistanceToShift / distanceToPoint);

            // add this offset into shift vector
            averageShiftVector.scale(numberOfPointsWithinProximity);
            averageShiftVector.add(vectorToPoint);
            numberOfPointsWithinProximity++;
            averageShiftVector.scale(1.0 / numberOfPointsWithinProximity);
         }
      }

      // FIXME this doesn't currently work
      // enforce the minimum distance to a point
      /*
      for (Point2DReadOnly pointToShiftFrom : pointsToAvoid)
      {
         vectorToPoint.sub(pointToShift, pointToShiftFrom);
         vectorToPoint.add(averageShiftVector);
         double distanceToPoint = vectorToPoint.length();
         double distanceAfterShifting = distanceToPoint + averageShiftVector.dot(vectorToPoint);

         double distanceToAchieve = pointsToAvoidByDistanceA.contains(pointToShiftFrom) ? minimumDistanceA : minimumDistanceB;

         if (distanceAfterShifting < distanceToAchieve)
         {
            double extraDistanceToShift = distanceToAchieve - distanceToPoint;
            vectorToPoint.scale(extraDistanceToShift / distanceToPoint);

            averageShiftVector.add(vectorToPoint);
         }
      }
      */

      return averageShiftVector;
   }

   private Vector2DReadOnly getDirectionAndDistanceToShiftToAvoidCliffs(Point2DReadOnly pointToCheck, VisibilityGraphNode originalNode,
                                                                        NavigableRegions navigableRegions, double distanceInsideHomeRegionToAvoid)
   {
      double originalHeight = originalNode.getPointInWorld().getZ();
      double adjustedHeight = getMaxHeightOfPointInWorld(pointToCheck, navigableRegions);

      Vector2D nodeShiftToAvoidCliff = new Vector2D();

      if (Math.abs(adjustedHeight - originalHeight) > desiredDistanceFromCliff)
      {
         Cluster cluster = originalNode.getVisibilityGraphNavigableRegion().getNavigableRegion().getHomeRegionCluster();
         List<Point2DReadOnly> clusterPolygon = cluster.getNonNavigableExtrusionsInWorld2D();

         Point2D closestPointInCluster = new Point2D();
         double distanceToCluster = VisibilityTools.distanceToCluster(pointToCheck, clusterPolygon, closestPointInCluster, null);

         double distanceToMove = distanceToCluster + distanceInsideHomeRegionToAvoid;
         nodeShiftToAvoidCliff.sub(closestPointInCluster, pointToCheck);
         nodeShiftToAvoidCliff.normalize();
         nodeShiftToAvoidCliff.scale(distanceToMove);
      }

      return nodeShiftToAvoidCliff;
   }

   private static double getMaxHeightOfPointInWorld(Point2DReadOnly pointInWorldToCheck, NavigableRegions navigableRegions)
   {
      double maxHeight = Double.NEGATIVE_INFINITY;
      for (NavigableRegion navigableRegion : navigableRegions.getNaviableRegionsList())
      {
         PlanarRegion planarRegion = navigableRegion.getHomePlanarRegion();

         RigidBodyTransform transformToWorld = new RigidBodyTransform();
         planarRegion.getTransformToWorld(transformToWorld);
         Point2D pointInLocalToCheck = new Point2D(pointInWorldToCheck);
         pointInLocalToCheck.applyInverseTransform(transformToWorld, false);
         double height = planarRegion.getPlaneZGivenXY(pointInWorldToCheck.getX(), pointInWorldToCheck.getY());
         if (PlanarRegionTools.isPointInLocalInsidePlanarRegion(navigableRegion.getHomePlanarRegion(), pointInLocalToCheck) && height > maxHeight)
         {
            maxHeight = height;
         }
      }

      return maxHeight;
   }

   private static void removeDuplicated2DPointsFromList(List<? extends Point2DReadOnly> listOfPoints, double samePointEpsilon)
   {
      int pointIndex = 0;
      while (pointIndex < listOfPoints.size() - 1)
      {
         int otherPointIndex = pointIndex + 1;
         while (otherPointIndex < listOfPoints.size())
         {
            if (listOfPoints.get(pointIndex).distance(listOfPoints.get(otherPointIndex)) < samePointEpsilon)
               listOfPoints.remove(otherPointIndex);
            else
               otherPointIndex++;
         }
         pointIndex++;
      }
   }

   private static void removeDuplicated3DPointsFromList(List<? extends Point3DReadOnly> listOfPoints, double samePointEpsilon)
   {
      int pointIndex = 0;
      while (pointIndex < listOfPoints.size() - 1)
      {
         int otherPointIndex = pointIndex + 1;
         while (otherPointIndex < listOfPoints.size())
         {
            if (listOfPoints.get(pointIndex).distance(listOfPoints.get(otherPointIndex)) < samePointEpsilon)
               listOfPoints.remove(otherPointIndex);
            else
               otherPointIndex++;
         }
         pointIndex++;
      }
   }

   private static void removeDuplicateStartOrEndPointsFromList(List<? extends Point3DReadOnly> listOfPoints, Point3DReadOnly startPoint,
                                                               Point3DReadOnly endPoint, double samePointEpsilon)
   {
      int pointIndex = 0;
      while (pointIndex < listOfPoints.size())
      {
         Point3DReadOnly pointToCheck = listOfPoints.get(pointIndex);
         if (pointToCheck.distance(startPoint) < samePointEpsilon || pointToCheck.distance(endPoint) < samePointEpsilon)
            listOfPoints.remove(pointIndex);
         else
            pointIndex++;
      }
   }



   private boolean isNearCliff(Point2DReadOnly point, double maxConnectionDistance, double maxHeightDelta, NavigableRegion homeRegion,
                               List<NavigableRegion> navigableRegions)
   {
      // if point is sufficiently inside, it is not near a cliff
      Point2D closestPointToThrowAway = new Point2D();
      double distanceToContainingCluster = VisibilityTools
            .distanceToCluster(point, homeRegion.getHomeRegionCluster().getNavigableExtrusionsInWorld2D(), closestPointToThrowAway, null);
      if (distanceToContainingCluster < -desiredDistanceFromCliff)
         return false;

      List<NavigableRegion> nearbyRegions = filterNavigableRegionsWithBoundingCircle(point, maxConnectionDistance + desiredDistanceFromCliff, navigableRegions);
      List<NavigableRegion> closeEnoughRegions = filterNavigableRegionsConnectionWithDistanceAndHeightChange(homeRegion, nearbyRegions, maxConnectionDistance,
                                                                                                             maxHeightDelta);

      if (closeEnoughRegions.contains(homeRegion))
         return closeEnoughRegions.size() < 2;
      else
         return closeEnoughRegions.size() < 1;
   }

   private static List<NavigableRegion> filterNavigableRegionsConnectionWithDistanceAndHeightChange(NavigableRegion homeRegion,
                                                                                                    List<NavigableRegion> navigableRegions,
                                                                                                    double maxConnectionDistance, double maxHeightDelta)
   {
      return navigableRegions.stream().filter(
            otherRegion -> isOtherNavigableRegionWithinDistanceAndHeightDifference(homeRegion, otherRegion, maxConnectionDistance, maxHeightDelta))
                             .collect(Collectors.toList());
   }

   private static boolean isOtherNavigableRegionWithinDistanceAndHeightDifference(NavigableRegion regionA, NavigableRegion regionB,
                                                                                  double maxConnectionDistance, double maxHeightDelta)
   {
      for (Point3DReadOnly pointA : regionA.getHomeRegionCluster().getNavigableExtrusionsInWorld())
      {
         for (Point3DReadOnly pointB : regionB.getHomeRegionCluster().getNavigableExtrusionsInWorld())
         {
            if (pointA.distance(pointB) < maxConnectionDistance && Math.abs(pointA.getZ() - pointB.getZ()) < maxHeightDelta)
               return true;
         }
      }

      return false;
   }

   private static List<NavigableRegion> filterNavigableRegionsWithBoundingCircle(Point2DReadOnly circleOrigin, double circleRadius,
                                                                                 List<NavigableRegion> navigableRegions)
   {
      if (!Double.isFinite(circleRadius) || circleRadius < 0.0)
         return navigableRegions;

      return navigableRegions.stream().filter(
            navigableRegion -> PlanarRegionTools.isPlanarRegionIntersectingWithCircle(circleOrigin, circleRadius, navigableRegion.getHomePlanarRegion()))
                             .collect(Collectors.toList());
   }

   private class IntermediateComparator implements Comparator<Point2DReadOnly>
   {
      private final Point2D startPoint = new Point2D();
      private final Point2D endPoint = new Point2D();

      public void setStartPoint(Point2DReadOnly startPoint)
      {
         this.startPoint.set(startPoint);
      }

      public void setEndPoint(Point2DReadOnly endPoint)
      {
         this.endPoint.set(endPoint);
      }

      @Override
      public int compare(Point2DReadOnly pointA, Point2DReadOnly pointB)
      {
         double distanceA = EuclidGeometryTools.percentageAlongLineSegment2D(pointA, startPoint, endPoint);
         double distanceB = EuclidGeometryTools.percentageAlongLineSegment2D(pointB, startPoint, endPoint);
         return Double.compare(distanceA, distanceB);
      }
   }
}
