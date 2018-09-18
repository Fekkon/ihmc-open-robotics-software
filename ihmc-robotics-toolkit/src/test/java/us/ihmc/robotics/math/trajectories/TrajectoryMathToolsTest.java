package us.ihmc.robotics.math.trajectories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gnu.trove.list.array.TDoubleArrayList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import us.ihmc.commons.Epsilons;
import us.ihmc.continuousIntegration.ContinuousIntegrationAnnotations.ContinuousIntegrationPlan;
import us.ihmc.continuousIntegration.ContinuousIntegrationAnnotations.ContinuousIntegrationTest;
import us.ihmc.continuousIntegration.IntegrationCategory;
import us.ihmc.euclid.referenceFrame.FramePoint3D;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.robotics.math.trajectories.*;

import static org.junit.Assert.assertEquals;

@ContinuousIntegrationPlan(categories = {IntegrationCategory.FAST})
public class TrajectoryMathToolsTest
{
   private static final ReferenceFrame worldFrame = ReferenceFrame.getWorldFrame();
   private static TrajectoryMathTools trajMath = new TrajectoryMathTools(16);
   private static final double epsilon = 1e-6;

   @Before
   public void setupTest()
   {
   }

   @After
   public void finishTest()
   {
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testTrajectoryAddition()
   {
      Trajectory traj1 = new Trajectory(7);
      Trajectory traj2 = new Trajectory(7);
      traj1.setCubic(1, 10, 1.5, -2.5);
      traj2.setCubic(1, 10, 12.5, 3.5);
      Assert.assertTrue(traj1.getNumberOfCoefficients() == 4);
      Assert.assertTrue(traj2.getNumberOfCoefficients() == 4);
      TrajectoryMathTools.add(traj1, traj1, traj2);
      Assert.assertTrue(traj1.getNumberOfCoefficients() == 4);
      assertEquals(traj1.getCoefficient(0), 13.482853223593963, epsilon);
      assertEquals(traj1.getCoefficient(1), 1.069958847736625, epsilon);
      assertEquals(traj1.getCoefficient(2), -0.588477366255144, epsilon);
      assertEquals(traj1.getCoefficient(3), 0.03566529492455418, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testTrajectoryMultiTimeScaleAddition()
   {
      List<Trajectory> resultTrajectoryList = new ArrayList<>(3);
      resultTrajectoryList.add(new Trajectory(6));
      resultTrajectoryList.add(new Trajectory(6));
      resultTrajectoryList.add(new Trajectory(6));
      resultTrajectoryList.add(new Trajectory(6));
      Trajectory traj1 = new Trajectory(7);
      Trajectory traj2 = new Trajectory(7);
      traj1.setCubic(1, 6, 1.5, -2.5);
      traj2.setCubic(3, 5, 12.5, 3.5);
      Assert.assertTrue(traj1.getNumberOfCoefficients() == 4);
      Assert.assertTrue(traj2.getNumberOfCoefficients() == 4);
      int numberOfSegments = trajMath.add(resultTrajectoryList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Trajectory traj3 = resultTrajectoryList.get(0);
      Trajectory traj4 = resultTrajectoryList.get(1);
      Trajectory traj5 = resultTrajectoryList.get(2);
      assertEquals(traj3.getInitialTime(), 1.0, epsilon);
      assertEquals(traj3.getFinalTime(), 3.0, epsilon);
      Assert.assertTrue(traj3.getNumberOfCoefficients() == 4);
      assertEquals(traj3.getCoefficient(0), 0.9560, epsilon);
      assertEquals(traj3.getCoefficient(1), 1.1520, epsilon);
      assertEquals(traj3.getCoefficient(2), -0.6720, epsilon);
      assertEquals(traj3.getCoefficient(3), 0.0640, epsilon);

      assertEquals(traj4.getInitialTime(), 3.0, epsilon);
      assertEquals(traj4.getFinalTime(), 5.0, epsilon);
      Assert.assertTrue(traj4.getNumberOfCoefficients() == 4);
      assertEquals(traj4.getCoefficient(0), 0.9560 - 109.0000, epsilon);
      assertEquals(traj4.getCoefficient(1), 1.1520 + 101.2500, epsilon);
      assertEquals(traj4.getCoefficient(2), -0.6720 - 27.0000, epsilon);
      assertEquals(traj4.getCoefficient(3), 0.0640 + 2.2500, epsilon);

      assertEquals(traj5.getInitialTime(), 5.0, epsilon);
      assertEquals(traj5.getFinalTime(), 6.0, epsilon);
      Assert.assertTrue(traj5.getNumberOfCoefficients() == 4);
      assertEquals(traj5.getCoefficient(0), 0.9560, epsilon);
      assertEquals(traj5.getCoefficient(1), 1.1520, epsilon);
      assertEquals(traj5.getCoefficient(2), -0.6720, epsilon);
      assertEquals(traj5.getCoefficient(3), 0.0640, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testMultiTimeScaleOperation()
   {
      TDoubleArrayList timeList = new TDoubleArrayList(4);
      Trajectory traj1 = new Trajectory(10);
      Trajectory traj2 = new Trajectory(10);
      traj1.setConstant(5, 10, 1);
      traj2.setConstant(1, 4, 1);
      int numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 1);
      Assert.assertTrue(timeList.get(1) == 4);
      Assert.assertTrue(timeList.get(2) == 5);
      Assert.assertTrue(timeList.get(3) == 10);

      traj1.setConstant(5, 10, 1);
      traj2.setConstant(1, 6, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 1);
      Assert.assertTrue(timeList.get(1) == 5);
      Assert.assertTrue(timeList.get(2) == 6);
      Assert.assertTrue(timeList.get(3) == 10);

      traj1.setConstant(5, 10, 1);
      traj2.setConstant(1, 11, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 1);
      Assert.assertTrue(timeList.get(1) == 5);
      Assert.assertTrue(timeList.get(2) == 10);
      Assert.assertTrue(timeList.get(3) == 11);

      traj1.setConstant(5, 10, 1);
      traj2.setConstant(6, 9, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 6);
      Assert.assertTrue(timeList.get(2) == 9);
      Assert.assertTrue(timeList.get(3) == 10);

      traj1.setConstant(5, 10, 1);
      traj2.setConstant(6, 11, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 6);
      Assert.assertTrue(timeList.get(2) == 10);
      Assert.assertTrue(timeList.get(3) == 11);

      traj1.setConstant(5, 10, 1);
      traj2.setConstant(11, 12, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 10);
      Assert.assertTrue(timeList.get(2) == 11);
      Assert.assertTrue(timeList.get(3) == 12);

      // 
      traj2.setConstant(5, 10, 1);
      traj1.setConstant(1, 4, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 1);
      Assert.assertTrue(timeList.get(1) == 4);
      Assert.assertTrue(timeList.get(2) == 5);
      Assert.assertTrue(timeList.get(3) == 10);

      traj2.setConstant(5, 10, 1);
      traj1.setConstant(1, 6, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 1);
      Assert.assertTrue(timeList.get(1) == 5);
      Assert.assertTrue(timeList.get(2) == 6);
      Assert.assertTrue(timeList.get(3) == 10);

      traj2.setConstant(5, 10, 1);
      traj1.setConstant(1, 11, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 1);
      Assert.assertTrue(timeList.get(1) == 5);
      Assert.assertTrue(timeList.get(2) == 10);
      Assert.assertTrue(timeList.get(3) == 11);

      traj2.setConstant(5, 10, 1);
      traj1.setConstant(6, 9, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 6);
      Assert.assertTrue(timeList.get(2) == 9);
      Assert.assertTrue(timeList.get(3) == 10);

      traj2.setConstant(5, 10, 1);
      traj1.setConstant(6, 11, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 6);
      Assert.assertTrue(timeList.get(2) == 10);
      Assert.assertTrue(timeList.get(3) == 11);

      traj2.setConstant(5, 10, 1);
      traj1.setConstant(11, 12, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 10);
      Assert.assertTrue(timeList.get(2) == 11);
      Assert.assertTrue(timeList.get(3) == 12);

      //
      traj1.setConstant(5, 10, 1);
      traj2.setConstant(5, 9, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 2);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 9);
      Assert.assertTrue(timeList.get(2) == 10);

      traj1.setConstant(5, 10, 1);
      traj2.setConstant(5, 11, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 2);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 10);
      Assert.assertTrue(timeList.get(2) == 11);

      traj1.setConstant(5, 10, 1);
      traj2.setConstant(4, 10, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 2);
      Assert.assertTrue(timeList.get(0) == 4);
      Assert.assertTrue(timeList.get(1) == 5);
      Assert.assertTrue(timeList.get(2) == 10);

      traj1.setConstant(5, 10, 1);
      traj2.setConstant(6, 10, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 2);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 6);
      Assert.assertTrue(timeList.get(2) == 10);

      //
      traj2.setConstant(5, 10, 1);
      traj1.setConstant(5, 9, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 2);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 9);
      Assert.assertTrue(timeList.get(2) == 10);

      traj2.setConstant(5, 10, 1);
      traj1.setConstant(5, 11, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 2);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 10);
      Assert.assertTrue(timeList.get(2) == 11);

      traj2.setConstant(5, 10, 1);
      traj1.setConstant(4, 10, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 2);
      Assert.assertTrue(timeList.get(0) == 4);
      Assert.assertTrue(timeList.get(1) == 5);
      Assert.assertTrue(timeList.get(2) == 10);

      traj2.setConstant(5, 10, 1);
      traj1.setConstant(6, 10, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 2);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 6);
      Assert.assertTrue(timeList.get(2) == 10);

      traj1.setConstant(5, 10, 1);
      traj2.setConstant(5, 10, 1);
      numberOfSegments = TrajectoryMathTools.getSegmentTimeList(timeList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 1);
      Assert.assertTrue(timeList.get(0) == 5);
      Assert.assertTrue(timeList.get(1) == 10);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testTrajectorySubtraction()
   {
      Trajectory traj1 = new Trajectory(7);
      Trajectory traj2 = new Trajectory(7);
      traj1.setCubic(0, 2, 3.5, -2.5);
      traj2.setCubic(0, 2, -0.5, -0.6);
      Assert.assertTrue(traj1.getNumberOfCoefficients() == 4);
      Assert.assertTrue(traj2.getNumberOfCoefficients() == 4);
      TrajectoryMathTools.subtract(traj1, traj1, traj2);
      Assert.assertTrue(traj1.getNumberOfCoefficients() == 4);
      assertEquals(traj1.getCoefficient(0), 3.5 + 0.500, epsilon);
      assertEquals(traj1.getCoefficient(1), 0.0 - 0.000, epsilon);
      assertEquals(traj1.getCoefficient(2), -4.5 + 0.075, epsilon);
      assertEquals(traj1.getCoefficient(3), 1.5 - 0.025, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testTrajectoryMultiTimeScaleSubtraction()
   {
      List<Trajectory> resultTrajectoryList = new ArrayList<>(3);
      resultTrajectoryList.add(new Trajectory(6));
      resultTrajectoryList.add(new Trajectory(6));
      resultTrajectoryList.add(new Trajectory(6));
      resultTrajectoryList.add(new Trajectory(6));
      Trajectory traj1 = new Trajectory(7);
      Trajectory traj2 = new Trajectory(7);
      traj1.setCubic(0, 6, 2.1, 10.5);
      traj2.setCubic(1, 10, -10.6, 6.5);
      Assert.assertTrue(traj1.getNumberOfCoefficients() == 4);
      Assert.assertTrue(traj2.getNumberOfCoefficients() == 4);
      int numberOfSegments = trajMath.subtract(resultTrajectoryList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 3);
      Trajectory traj3 = resultTrajectoryList.get(0);
      Trajectory traj4 = resultTrajectoryList.get(1);
      Trajectory traj5 = resultTrajectoryList.get(2);
      assertEquals(traj3.getInitialTime(), 0.0, epsilon);
      assertEquals(traj3.getFinalTime(), 1.0, epsilon);
      Assert.assertTrue(traj3.getNumberOfCoefficients() == 4);
      assertEquals(traj3.getCoefficient(0), 2.1000, epsilon);
      assertEquals(traj3.getCoefficient(1), 0.0000, epsilon);
      assertEquals(traj3.getCoefficient(2), 0.7000, epsilon);
      assertEquals(traj3.getCoefficient(3), -0.0777778, epsilon);

      assertEquals(traj4.getInitialTime(), 1.0, epsilon);
      assertEquals(traj4.getFinalTime(), 6.0, epsilon);
      Assert.assertTrue(traj4.getNumberOfCoefficients() == 4);
      assertEquals(traj4.getCoefficient(0), 2.1000 + 9.91975309, epsilon);
      assertEquals(traj4.getCoefficient(1), 0.0000 + 1.407407407, epsilon);
      assertEquals(traj4.getCoefficient(2), 0.7000 - 0.77407407, epsilon);
      assertEquals(traj4.getCoefficient(3), -0.077777778 + 0.0469135802, epsilon);

      assertEquals(traj5.getInitialTime(), 6.0, epsilon);
      assertEquals(traj5.getFinalTime(), 10.0, epsilon);
      Assert.assertTrue(traj5.getNumberOfCoefficients() == 4);
      assertEquals(traj5.getCoefficient(0), 9.91975309, epsilon);
      assertEquals(traj5.getCoefficient(1), 1.407407407, epsilon);
      assertEquals(traj5.getCoefficient(2), -0.77407407, epsilon);
      assertEquals(traj5.getCoefficient(3), 0.0469135802, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testTrajectoryMultiplication2()
   {
      Trajectory traj1 = new Trajectory(8);
      Trajectory traj2 = new Trajectory(8);
      traj1.setLinear(0, 10, 1, 2);
      traj2.setLinear(0, 10, 2, 3);
      trajMath.multiply(traj1, traj1, traj2);
      assertEquals(traj1.getCoefficient(0), 2, epsilon);
      assertEquals(traj1.getCoefficient(1), 0.3, epsilon);
      assertEquals(traj1.getCoefficient(2), 0.01, epsilon);

   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testTrajectoryMultiplication()
   {
      Trajectory traj1 = new Trajectory(7);
      Trajectory traj2 = new Trajectory(7);
      traj1.setCubic(12, 15, 19.5, 200.5);
      traj2.setCubic(12, 15, 0.5, 0.1);
      Assert.assertTrue(traj1.getNumberOfCoefficients() == 4);
      Assert.assertTrue(traj2.getNumberOfCoefficients() == 4);
      trajMath.multiply(traj1, traj1, traj2);
      Assert.assertTrue(traj1.getNumberOfCoefficients() == 7);
      assertEquals(traj1.getCoefficient(0), -2.228097449999825e+06, epsilon);
      assertEquals(traj1.getCoefficient(1), 1.016083999999920e+06, epsilon);
      assertEquals(traj1.getCoefficient(2), -1.920462999999848e+05, epsilon);
      assertEquals(traj1.getCoefficient(3), 1.925763703703550e+04, epsilon);
      assertEquals(traj1.getCoefficient(4), -1.080637037036950e+03, epsilon);
      assertEquals(traj1.getCoefficient(5), 32.177777777775184, epsilon);
      assertEquals(traj1.getCoefficient(6), -0.397256515775002, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testTrajectoryMultiTimeScaleMultiplication()
   {
      List<Trajectory> resultTrajectoryList = new ArrayList<>(2);
      resultTrajectoryList.add(new Trajectory(7));
      resultTrajectoryList.add(new Trajectory(7));
      Trajectory traj1 = new Trajectory(7);
      Trajectory traj2 = new Trajectory(7);
      traj1.setCubic(1, 6, 1.1, 2.5);
      traj2.setCubic(1, 10, -1.6, 0.5);
      Assert.assertTrue(traj1.getNumberOfCoefficients() == 4);
      Assert.assertTrue(traj2.getNumberOfCoefficients() == 4);
      int numberOfSegments = trajMath.multiply(resultTrajectoryList, traj1, traj2, Epsilons.ONE_MILLIONTH);
      Assert.assertTrue(numberOfSegments == 2);
      Trajectory traj3 = resultTrajectoryList.get(0);
      Trajectory traj4 = resultTrajectoryList.get(1);
      assertEquals(traj3.getInitialTime(), 1.0, epsilon);
      assertEquals(traj3.getFinalTime(), 6.0, epsilon);
      Assert.assertTrue(traj3.getNumberOfCoefficients() == 7);
      assertEquals(traj3.getCoefficient(0), -1.956841152263374, epsilon);
      assertEquals(traj3.getCoefficient(1), 0.388404938271605, epsilon);
      assertEquals(traj3.getCoefficient(2), -0.164315061728395, epsilon);
      assertEquals(traj3.getCoefficient(3), -0.052446419753086, epsilon);
      assertEquals(traj3.getCoefficient(4), 0.028553086419753, epsilon);
      assertEquals(traj3.getCoefficient(5), -0.003484444444444, epsilon);
      assertEquals(traj3.getCoefficient(6), 0.000129053497942, epsilon);

      assertEquals(traj4.getInitialTime(), 6.0, epsilon);
      assertEquals(traj4.getFinalTime(), 10.0, epsilon);
      Assert.assertTrue(traj4.getNumberOfCoefficients() == 4);
      assertEquals(traj4.getCoefficient(0), 0, epsilon);
      assertEquals(traj4.getCoefficient(1), 0, epsilon);
      assertEquals(traj4.getCoefficient(2), 0, epsilon);
      assertEquals(traj4.getCoefficient(3), 0, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void test3DTrajectoryAddition()
   {
      Trajectory3D traj1 = new Trajectory3D(3);
      Trajectory3D traj2 = new Trajectory3D(3);
      traj1.setLinear(0, 1, new Point3D(0.5, 0.1, 10), new Point3D(1, 10, 5));
      traj2.setLinear(0, 1, new Point3D(), new Point3D(5, 7.7, 1));
      TrajectoryMathTools.add(traj1, traj1, traj2);

      Trajectory traj = traj1.getTrajectoryX();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 2);
      assertEquals(traj.getCoefficient(0), 0.5, epsilon);
      assertEquals(traj.getCoefficient(1), 5.5, epsilon);

      traj = traj1.getTrajectoryY();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 2);
      assertEquals(traj.getCoefficient(0), 0.1, epsilon);
      assertEquals(traj.getCoefficient(1), 9.9 + 7.7, epsilon);

      traj = traj1.getTrajectoryZ();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 2);
      assertEquals(traj.getCoefficient(0), 10, epsilon);
      assertEquals(traj.getCoefficient(1), -4, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void test3DTrajectorySubtraction()
   {
      Trajectory3D resultingTrajectory = new Trajectory3D(3);
      Trajectory3D traj1 = new Trajectory3D(3);
      Trajectory3D traj2 = new Trajectory3D(3);
      traj1.setLinear(0, 1, new Point3D(0.1, 3.414, 1.87), new Point3D(2.09, 1.35, 5.35));
      traj2.setLinear(0, 1, new Point3D(3.14, 1.59, 12.9), new Point3D(4.51, 5.32, 1.12));
      TrajectoryMathTools.subtract(resultingTrajectory, traj1, traj2);

      Trajectory traj = resultingTrajectory.getTrajectoryX();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 2);
      assertEquals(traj.getCoefficient(0), -3.04, epsilon);
      assertEquals(traj.getCoefficient(1), 0.62, epsilon);

      traj = resultingTrajectory.getTrajectoryY();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 2);
      assertEquals(traj.getCoefficient(0), 1.824, epsilon);
      assertEquals(traj.getCoefficient(1), -5.794, epsilon);

      traj = resultingTrajectory.getTrajectoryZ();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 2);
      assertEquals(traj.getCoefficient(0), -11.03, epsilon);
      assertEquals(traj.getCoefficient(1), 15.26, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void test3DTrajectorySubtractionEquals()
   {
      Trajectory3D traj1 = new Trajectory3D(3);
      Trajectory3D traj2 = new Trajectory3D(3);
      traj1.setLinear(0, 1, new Point3D(0.1, 3.414, 1.87), new Point3D(2.09, 1.35, 5.35));
      traj2.setLinear(0, 1, new Point3D(3.14, 1.59, 12.9), new Point3D(4.51, 5.32, 1.12));
      TrajectoryMathTools.subtractEquals(traj1, traj2);

      Trajectory traj = traj1.getTrajectoryX();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 2);
      assertEquals(traj.getCoefficient(0), -3.04, epsilon);
      assertEquals(traj.getCoefficient(1), 0.62, epsilon);

      traj = traj1.getTrajectoryY();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 2);
      assertEquals(traj.getCoefficient(0), 1.824, epsilon);
      assertEquals(traj.getCoefficient(1), -5.794, epsilon);

      traj = traj1.getTrajectoryZ();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 2);
      assertEquals(traj.getCoefficient(0), -11.03, epsilon);
      assertEquals(traj.getCoefficient(1), 15.26, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void test3DTrajectoryDotProducts()
   {
      Trajectory3D traj1 = new Trajectory3D(3);
      Trajectory3D traj2 = new Trajectory3D(3);
      traj1.setLinear(0, 1, new Point3D(1, 3, 5), new Point3D(6, 4, 2));
      traj2.setLinear(0, 1, new Point3D(2, 4, 6), new Point3D(5, 3, 1));
      trajMath.dotProduct(traj1, traj1, traj2);

      Trajectory traj = traj1.getTrajectoryX();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 3);
      assertEquals(traj.getCoefficient(0), 2, epsilon);
      assertEquals(traj.getCoefficient(1), 13, epsilon);
      assertEquals(traj.getCoefficient(2), 15, epsilon);

      traj = traj1.getTrajectoryY();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 3);
      assertEquals(traj.getCoefficient(0), 12, epsilon);
      assertEquals(traj.getCoefficient(1), 1, epsilon);
      assertEquals(traj.getCoefficient(2), -1, epsilon);

      traj = traj1.getTrajectoryZ();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 3);
      assertEquals(traj.getCoefficient(0), 30, epsilon);
      assertEquals(traj.getCoefficient(1), -43, epsilon);
      assertEquals(traj.getCoefficient(2), 15, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void test3DTrajectoryCrossProduct()
   {
      Trajectory3D traj1 = new Trajectory3D(3);
      Trajectory3D traj2 = new Trajectory3D(3);
      Trajectory3D resultingTrajectory = new Trajectory3D(3);
      traj1.setLinear(0, 1, new Point3D(1, 3, 5), new Point3D(6, 4, 2));
      traj2.setLinear(0, 1, new Point3D(2, 4, 6), new Point3D(5, 3, 1));
      trajMath.crossProduct(resultingTrajectory, traj1, traj2);

      Trajectory traj = resultingTrajectory.getTrajectoryX();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 3);
      assertEquals(traj.getCoefficient(0), -2, epsilon);
      assertEquals(traj.getCoefficient(1), 8, epsilon);
      assertEquals(traj.getCoefficient(2), -8, epsilon);

      traj = resultingTrajectory.getTrajectoryY();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 3);
      assertEquals(traj.getCoefficient(0), 4, epsilon);
      assertEquals(traj.getCoefficient(1), -16, epsilon);
      assertEquals(traj.getCoefficient(2), 16, epsilon);

      traj = resultingTrajectory.getTrajectoryZ();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 3);
      assertEquals(traj.getCoefficient(0), -2, epsilon);
      assertEquals(traj.getCoefficient(1), 8, epsilon);
      assertEquals(traj.getCoefficient(2), -8, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void test3DTrajectoryCrossProductStoreInSelf()
   {
      Trajectory3D traj1 = new Trajectory3D(3);
      Trajectory3D traj2 = new Trajectory3D(3);
      traj1.setLinear(0, 1, new Point3D(1, 3, 5), new Point3D(6, 4, 2));
      traj2.setLinear(0, 1, new Point3D(2, 4, 6), new Point3D(5, 3, 1));
      trajMath.crossProduct(traj1, traj2);

      Trajectory traj = traj1.getTrajectoryX();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 3);
      assertEquals(traj.getCoefficient(0), -2, epsilon);
      assertEquals(traj.getCoefficient(1), 8, epsilon);
      assertEquals(traj.getCoefficient(2), -8, epsilon);

      traj = traj1.getTrajectoryY();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 3);
      assertEquals(traj.getCoefficient(0), 4, epsilon);
      assertEquals(traj.getCoefficient(1), -16, epsilon);
      assertEquals(traj.getCoefficient(2), 16, epsilon);

      traj = traj1.getTrajectoryZ();
      assertEquals(traj.getInitialTime(), 0, epsilon);
      assertEquals(traj.getFinalTime(), 1, epsilon);
      Assert.assertTrue(traj.getNumberOfCoefficients() == 3);
      assertEquals(traj.getCoefficient(0), -2, epsilon);
      assertEquals(traj.getCoefficient(1), 8, epsilon);
      assertEquals(traj.getCoefficient(2), -8, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testIntegration()
   {
      Trajectory traj1 = new Trajectory(2);
      Trajectory traj2 = new Trajectory(3);
      traj1.setLinear(1, 11, 4, 5);
      TrajectoryMathTools.getIntegral(traj2, traj1);
      assertEquals(traj2.getCoefficient(0), -4.00, epsilon);
      assertEquals(traj2.getCoefficient(1), 3.90, epsilon);
      assertEquals(traj2.getCoefficient(2), 0.05, epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testDifferentiation()
   {
      Trajectory traj1 = new Trajectory(3);
      Trajectory traj2 = new Trajectory(2);
      traj1.setQuadratic(1, 11, 4, 0, 5);
      TrajectoryMathTools.getDerivative(traj2, traj1);
      assertEquals(traj2.getCoefficient(0), traj1.getCoefficient(1), epsilon);
      assertEquals(traj2.getCoefficient(1), 2 * traj1.getCoefficient(2), epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testShifting()
   {
      Trajectory traj1 = new Trajectory(3);
      traj1.setDirectlyFast(0, 1);
      traj1.setDirectlyFast(1, 2);
      traj1.setDirectlyFast(2, 3);
      traj1.setTime(2, 4);
      trajMath.addTimeOffset(traj1, 2);
      assertEquals(traj1.getCoefficient(0), 9, epsilon);
      assertEquals(traj1.getCoefficient(1), -10, epsilon);
      assertEquals(traj1.getCoefficient(2), 3, epsilon);
      trajMath.addTimeOffset(traj1, -4);
      assertEquals(traj1.getCoefficient(0), 17, epsilon);
      assertEquals(traj1.getCoefficient(1), 14, epsilon);
      assertEquals(traj1.getCoefficient(2), 3, epsilon);
   }


   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testSegmentedAddition()
   {
      SegmentedFrameTrajectory3D traj1 = new SegmentedFrameTrajectory3D(4, 2);
      SegmentedFrameTrajectory3D traj2 = new SegmentedFrameTrajectory3D(4, 2);
      SegmentedFrameTrajectory3D traj3 = new SegmentedFrameTrajectory3D(10, 2);
      assertEquals(0, traj1.getNumberOfSegments());
      assertEquals(0, traj2.getNumberOfSegments());
      assertEquals(0, traj3.getNumberOfSegments());

      traj1.add().setLinear(0.0, 1.0, new FramePoint3D(worldFrame, 10, 11, 12), new FramePoint3D(worldFrame, 13, 14, 15));
      traj1.add().setLinear(1.0, 2.0, new FramePoint3D(worldFrame, 15, 20, 25), new FramePoint3D(worldFrame, 20, 25, 30));
      traj1.add().setLinear(2.0, 3.0, new FramePoint3D(worldFrame, 25, 28, 31), new FramePoint3D(worldFrame, 35, 38, 41));

      assertEquals(0.0, traj1.getSegment(0).getInitialTime(), epsilon);
      assertEquals(1.0, traj1.getSegment(0).getFinalTime(), epsilon);
      assertEquals(1.0, traj1.getSegment(1).getInitialTime(), epsilon);
      assertEquals(2.0, traj1.getSegment(1).getFinalTime(), epsilon);
      assertEquals(2.0, traj1.getSegment(2).getInitialTime(), epsilon);
      assertEquals(3.0, traj1.getSegment(2).getFinalTime(), epsilon);

      traj2.add().setLinear(0.5, 0.6, new FramePoint3D(worldFrame, 1, 2, 3), new FramePoint3D(worldFrame, 3, 2, 1));
      traj2.add().setLinear(1.2, 2.2, new FramePoint3D(worldFrame, 3, 2, 1), new FramePoint3D(worldFrame, 4, 5, 6));

      assertEquals(0.5, traj2.getSegment(0).getInitialTime(), epsilon);
      assertEquals(0.6, traj2.getSegment(0).getFinalTime(), epsilon);
      assertEquals(1.2, traj2.getSegment(1).getInitialTime(), epsilon);
      assertEquals(2.2, traj2.getSegment(1).getFinalTime(), epsilon);

      // should have 0.0-0.5, 0.5-0.6, 0.6-1.0, 1.0-1.2, 1.2-2.0, 2.0-2.2, 2.2-3.0
      TrajectoryMathTools.addSegmentedTrajectories(traj3, traj1, traj2, Epsilons.ONE_BILLIONTH);

      assertEquals(0.0, traj3.getSegment(0).getInitialTime(), epsilon);
      assertEquals(0.5, traj3.getSegment(0).getFinalTime(), epsilon);
      assertEquals(0.5, traj3.getSegment(1).getInitialTime(), epsilon);
      assertEquals(0.6, traj3.getSegment(1).getFinalTime(), epsilon);
      assertEquals(0.6, traj3.getSegment(2).getInitialTime(), epsilon);
      assertEquals(1.0, traj3.getSegment(2).getFinalTime(), epsilon);
      assertEquals(1.0, traj3.getSegment(3).getInitialTime(), epsilon);
      assertEquals(1.2, traj3.getSegment(3).getFinalTime(), epsilon);

      assertEquals(1.2, traj3.getSegment(4).getInitialTime(), epsilon);
      assertEquals(2.0, traj3.getSegment(4).getFinalTime(), epsilon);

      assertEquals(2.0, traj3.getSegment(5).getInitialTime(), epsilon);
      assertEquals(2.2, traj3.getSegment(5).getFinalTime(), epsilon);

      assertEquals(2.2, traj3.getSegment(6).getInitialTime(), epsilon);
      assertEquals(3.0, traj3.getSegment(6).getFinalTime(), epsilon);

      assertEquals(7, traj3.getNumberOfSegments());



   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 3000000)
   public void testSegmentedAdditionHard()
   {
      SegmentedFrameTrajectory3D traj1 = new SegmentedFrameTrajectory3D(4, 2);
      SegmentedFrameTrajectory3D traj2 = new SegmentedFrameTrajectory3D(4, 2);
      SegmentedFrameTrajectory3D traj3 = new SegmentedFrameTrajectory3D(20, 2);
      assertEquals(0, traj1.getNumberOfSegments());
      assertEquals(0, traj2.getNumberOfSegments());
      assertEquals(0, traj3.getNumberOfSegments());

      traj1.add().setLinear(0.0, 1.0, new FramePoint3D(worldFrame, 10, 11, 12), new FramePoint3D(worldFrame, 13, 14, 15));
      traj1.add().setLinear(1.0, 2.0, new FramePoint3D(worldFrame, 15, 20, 25), new FramePoint3D(worldFrame, 20, 25, 30));
      traj1.add().setLinear(2.0, 3.0, new FramePoint3D(worldFrame, 25, 28, 31), new FramePoint3D(worldFrame, 35, 38, 41));
      traj1.add().setLinear(3.0, 4.0, new FramePoint3D(worldFrame, 25, 28, 31), new FramePoint3D(worldFrame, 35, 38, 41));

      assertEquals(0.0, traj1.getSegment(0).getInitialTime(), epsilon);
      assertEquals(1.0, traj1.getSegment(0).getFinalTime(), epsilon);
      assertEquals(1.0, traj1.getSegment(1).getInitialTime(), epsilon);
      assertEquals(2.0, traj1.getSegment(1).getFinalTime(), epsilon);
      assertEquals(2.0, traj1.getSegment(2).getInitialTime(), epsilon);
      assertEquals(3.0, traj1.getSegment(2).getFinalTime(), epsilon);
      assertEquals(3.0, traj1.getSegment(3).getInitialTime(), epsilon);
      assertEquals(4.0, traj1.getSegment(3).getFinalTime(), epsilon);

      traj2.add().setLinear(0.5, 0.6, new FramePoint3D(worldFrame, 1, 2, 3), new FramePoint3D(worldFrame, 3, 2, 1));
      traj2.add().setLinear(0.7, 1.0, new FramePoint3D(worldFrame, 1, 2, 3), new FramePoint3D(worldFrame, 3, 2, 1));
      traj2.add().setLinear(1.2, 2.2, new FramePoint3D(worldFrame, 3, 2, 1), new FramePoint3D(worldFrame, 4, 5, 6));
      traj2.add().setLinear(2.7, 3.5, new FramePoint3D(worldFrame, 3, 2, 1), new FramePoint3D(worldFrame, 4, 5, 6));
      traj2.add().setLinear(3.9, 4.7, new FramePoint3D(worldFrame, 3, 2, 1), new FramePoint3D(worldFrame, 4, 5, 6));

      assertEquals(0.5, traj2.getSegment(0).getInitialTime(), epsilon);
      assertEquals(0.6, traj2.getSegment(0).getFinalTime(), epsilon);
      assertEquals(0.7, traj2.getSegment(1).getInitialTime(), epsilon);
      assertEquals(1.0, traj2.getSegment(1).getFinalTime(), epsilon);
      assertEquals(1.2, traj2.getSegment(2).getInitialTime(), epsilon);
      assertEquals(2.2, traj2.getSegment(2).getFinalTime(), epsilon);


      TrajectoryMathTools.addSegmentedTrajectories(traj3, traj1, traj2, Epsilons.ONE_BILLIONTH);


      // should have 0.0-0.5, 0.5-0.6, 0.6-0.7, 0.7-1.0, 1.0-1.2, 1.2-2.0, 2.0-2.2, 2.2-2.7,
      // 2.7-3.0, 3.0-3.5, 3.5-3.9, 3.9-4.0, 4.0-4.7

      assertEquals(0.0, traj3.getSegment(0).getInitialTime(), epsilon);
      assertEquals(0.5, traj3.getSegment(0).getFinalTime(), epsilon);

      assertEquals(0.5, traj3.getSegment(1).getInitialTime(), epsilon);
      assertEquals(0.6, traj3.getSegment(1).getFinalTime(), epsilon);

      assertEquals(0.6, traj3.getSegment(2).getInitialTime(), epsilon);
      assertEquals(0.7, traj3.getSegment(2).getFinalTime(), epsilon);

      assertEquals(0.7, traj3.getSegment(3).getInitialTime(), epsilon);
      assertEquals(1.0, traj3.getSegment(3).getFinalTime(), epsilon);

      assertEquals(1.0, traj3.getSegment(4).getInitialTime(), epsilon);
      assertEquals(1.2, traj3.getSegment(4).getFinalTime(), epsilon);

      assertEquals(1.2, traj3.getSegment(5).getInitialTime(), epsilon);
      assertEquals(2.0, traj3.getSegment(5).getFinalTime(), epsilon);

      assertEquals(2.0, traj3.getSegment(6).getInitialTime(), epsilon);
      assertEquals(2.2, traj3.getSegment(6).getFinalTime(), epsilon);

      assertEquals(2.2, traj3.getSegment(7).getInitialTime(), epsilon);
      assertEquals(2.7, traj3.getSegment(7).getFinalTime(), epsilon);

      assertEquals(2.7, traj3.getSegment(8).getInitialTime(), epsilon);
      assertEquals(3.0, traj3.getSegment(8).getFinalTime(), epsilon);

      assertEquals(3.0, traj3.getSegment(9).getInitialTime(), epsilon);
      assertEquals(3.5, traj3.getSegment(9).getFinalTime(), epsilon);

      assertEquals(3.5, traj3.getSegment(10).getInitialTime(), epsilon);
      assertEquals(3.9, traj3.getSegment(10).getFinalTime(), epsilon);

      assertEquals(3.9, traj3.getSegment(11).getInitialTime(), epsilon);
      assertEquals(4.0, traj3.getSegment(11).getFinalTime(), epsilon);

      assertEquals(4.0, traj3.getSegment(12).getInitialTime(), epsilon);
      assertEquals(4.7, traj3.getSegment(12).getFinalTime(), epsilon);

      assertEquals(13, traj3.getNumberOfSegments());
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testSegmentedAdditionWithLimitsA()
   {
      SegmentedFrameTrajectory3D traj1 = new SegmentedFrameTrajectory3D(4, 2);
      SegmentedFrameTrajectory3D traj2 = new SegmentedFrameTrajectory3D(4, 2);
      SegmentedFrameTrajectory3D traj3 = new SegmentedFrameTrajectory3D(10, 2);
      assertEquals(0, traj1.getNumberOfSegments());
      assertEquals(0, traj2.getNumberOfSegments());
      assertEquals(0, traj3.getNumberOfSegments());

      traj1.add().setLinear(0.0, 1.0, new FramePoint3D(worldFrame, 10, 11, 12), new FramePoint3D(worldFrame, 13, 14, 15));
      traj1.add().setLinear(1.0, 2.0, new FramePoint3D(worldFrame, 15, 20, 25), new FramePoint3D(worldFrame, 20, 25, 30));
      traj1.add().setLinear(2.0, 3.0, new FramePoint3D(worldFrame, 25, 28, 31), new FramePoint3D(worldFrame, 35, 38, 41));


      traj2.add().setLinear(0.5, 0.6, new FramePoint3D(worldFrame, 1, 2, 3), new FramePoint3D(worldFrame, 3, 2, 1));
      traj2.add().setLinear(1.1, 2.2, new FramePoint3D(worldFrame, 3, 2, 1), new FramePoint3D(worldFrame, 4, 5, 6));

      double minimumDuration = 0.11;
      TrajectoryMathTools.addSegmentedTrajectories(traj3, traj1, traj2, minimumDuration, Epsilons.ONE_BILLIONTH);

      assertEquals(0.0, traj3.getSegment(0).getInitialTime(), epsilon);
      assertEquals(0.5, traj3.getSegment(0).getFinalTime(), epsilon);

      assertEquals(0.5, traj3.getSegment(1).getInitialTime(), epsilon);
      assertEquals(0.6, traj3.getSegment(1).getFinalTime(), epsilon);

      assertEquals(0.6, traj3.getSegment(2).getInitialTime(), epsilon);
      assertEquals(1.0, traj3.getSegment(2).getFinalTime(), epsilon);

      assertEquals(1.0, traj3.getSegment(3).getInitialTime(), epsilon);
      assertEquals(2.0, traj3.getSegment(3).getFinalTime(), epsilon);

      assertEquals(2.0, traj3.getSegment(4).getInitialTime(), epsilon);
      assertEquals(2.2, traj3.getSegment(4).getFinalTime(), epsilon);

      assertEquals(2.2, traj3.getSegment(5).getInitialTime(), epsilon);
      assertEquals(3.0, traj3.getSegment(5).getFinalTime(), epsilon);

   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testSegmentedAdditionWithLimitsB()
   {
      SegmentedFrameTrajectory3D traj1 = new SegmentedFrameTrajectory3D(4, 2);
      SegmentedFrameTrajectory3D traj2 = new SegmentedFrameTrajectory3D(4, 2);
      SegmentedFrameTrajectory3D traj3 = new SegmentedFrameTrajectory3D(10, 2);
      assertEquals(0, traj1.getNumberOfSegments());
      assertEquals(0, traj2.getNumberOfSegments());
      assertEquals(0, traj3.getNumberOfSegments());

      traj1.add().setLinear(0.0, 1.0, new FramePoint3D(worldFrame, 10, 11, 12), new FramePoint3D(worldFrame, 13, 14, 15));
      traj1.add().setLinear(1.0, 2.0, new FramePoint3D(worldFrame, 15, 20, 25), new FramePoint3D(worldFrame, 20, 25, 30));
      traj1.add().setLinear(2.0, 3.0, new FramePoint3D(worldFrame, 25, 28, 31), new FramePoint3D(worldFrame, 35, 38, 41));


      traj2.add().setLinear(0.5, 0.6, new FramePoint3D(worldFrame, 1, 2, 3), new FramePoint3D(worldFrame, 3, 2, 1));
      traj2.add().setLinear(1.2, 2.1, new FramePoint3D(worldFrame, 3, 2, 1), new FramePoint3D(worldFrame, 4, 5, 6));

      double minimumDuration = 0.11;
      TrajectoryMathTools.addSegmentedTrajectories(traj3, traj1, traj2, minimumDuration, Epsilons.ONE_BILLIONTH);

      assertEquals(0.0, traj3.getSegment(0).getInitialTime(), epsilon);
      assertEquals(0.5, traj3.getSegment(0).getFinalTime(), epsilon);

      assertEquals(0.5, traj3.getSegment(1).getInitialTime(), epsilon);
      assertEquals(0.6, traj3.getSegment(1).getFinalTime(), epsilon);

      assertEquals(0.6, traj3.getSegment(2).getInitialTime(), epsilon);
      assertEquals(1.0, traj3.getSegment(2).getFinalTime(), epsilon);

      assertEquals(1.0, traj3.getSegment(3).getInitialTime(), epsilon);
      assertEquals(1.2, traj3.getSegment(3).getFinalTime(), epsilon);

      assertEquals(1.2, traj3.getSegment(4).getInitialTime(), epsilon);
      assertEquals(2.1, traj3.getSegment(4).getFinalTime(), epsilon);

      assertEquals(2.1, traj3.getSegment(5).getInitialTime(), epsilon);
      assertEquals(3.0, traj3.getSegment(5).getFinalTime(), epsilon);

   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testSetCurrentSegmentPolynomial()
   {
      SegmentedFrameTrajectory3D traj1 = new SegmentedFrameTrajectory3D(4, 2);
      SegmentedFrameTrajectory3D traj2 = new SegmentedFrameTrajectory3D(4, 2);
      SegmentedFrameTrajectory3D traj3 = new SegmentedFrameTrajectory3D(10, 2);
      assertEquals(0, traj1.getNumberOfSegments());
      assertEquals(0, traj2.getNumberOfSegments());
      assertEquals(0, traj3.getNumberOfSegments());

      traj1.add().setLinear(0.0, 1.0, new FramePoint3D(worldFrame, 10, 11, 12), new FramePoint3D(worldFrame, 13, 14, 15));
      traj1.add().setLinear(1.0, 2.0, new FramePoint3D(worldFrame, 15, 20, 25), new FramePoint3D(worldFrame, 20, 25, 30));
      traj1.add().setLinear(2.0, 3.0, new FramePoint3D(worldFrame, 25, 28, 31), new FramePoint3D(worldFrame, 35, 38, 41));

      assertEquals(0.0, traj1.getSegment(0).getInitialTime(), epsilon);
      assertEquals(1.0, traj1.getSegment(0).getFinalTime(), epsilon);
      assertEquals(1.0, traj1.getSegment(1).getInitialTime(), epsilon);
      assertEquals(2.0, traj1.getSegment(1).getFinalTime(), epsilon);
      assertEquals(2.0, traj1.getSegment(2).getInitialTime(), epsilon);
      assertEquals(3.0, traj1.getSegment(2).getFinalTime(), epsilon);


      TrajectoryMathTools.setCurrentSegmentPolynomial(traj3.add(), traj1.getSegment(0), 0.0, 1.0, Epsilons.ONE_MILLIONTH);

      assertEquals(1, traj3.getNumberOfSegments());
      assertEquals(0.0, traj3.getSegment(0).getInitialTime(), epsilon);
      assertEquals(1.0, traj3.getSegment(0).getFinalTime(), epsilon);

      TrajectoryMathTools.setCurrentSegmentPolynomial(traj3.add(), traj1.getSegment(1), 1.0, 2.0, Epsilons.ONE_MILLIONTH);

      assertEquals(2, traj3.getNumberOfSegments());
      assertEquals(1.0, traj3.getSegment(1).getInitialTime(), epsilon);
      assertEquals(2.0, traj3.getSegment(1).getFinalTime(), epsilon);

      TrajectoryMathTools.setCurrentSegmentPolynomial(traj3.add(), traj1.getSegment(2), 2.25, 2.5, Epsilons.ONE_MILLIONTH);

      assertEquals(3, traj3.getNumberOfSegments());
      assertEquals(2.250, traj3.getSegment(2).getInitialTime(), epsilon);
      assertEquals(2.5, traj3.getSegment(2).getFinalTime(), epsilon);
   }
}