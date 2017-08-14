package us.ihmc.robotics.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import us.ihmc.commons.Assertions;
import us.ihmc.commons.MutationTestFacilitator;
import us.ihmc.commons.RandomNumbers;
import us.ihmc.commons.RunnableThatThrows;
import us.ihmc.continuousIntegration.ContinuousIntegrationAnnotations.ContinuousIntegrationTest;
import us.ihmc.euclid.tools.EuclidCoreTestTools;
import us.ihmc.euclid.transform.AffineTransform;
import us.ihmc.euclid.transform.RigidBodyTransform;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.euclid.tuple3D.interfaces.Tuple3DBasics;
import us.ihmc.robotics.random.RandomGeometry;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;

public class FramePointTest extends FrameTupleTest<Point3D>
{
   public static double epsilon = 1e-10;

   @Override
   public FramePoint3D createEmptyFrameTuple()
   {
      return new FramePoint3D();
   }

   @Override
   public FramePoint3D createFrameTuple(ReferenceFrame referenceFrame, double x, double y, double z)
   {
      return new FramePoint3D(referenceFrame, x, y, z);
   }

   @Override
   public FramePoint3D createFrameTuple(ReferenceFrame referenceFrame, double x, double y, double z, String name)
   {
      return new FramePoint3D(referenceFrame, x, y, z, name);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public final void testRunTestMain()
   {
      // create frames
      ReferenceFrame A = ReferenceFrame.constructARootFrame("A");

      RigidBodyTransform transform = new RigidBodyTransform();
      Vector3D euler = new Vector3D(Math.PI / 2.0, 0.0, 0.0);
      transform.setRotationEulerAndZeroTranslation(euler);
      Vector3D translation = new Vector3D(5.0, 0.0, 0.0);
      transform.setTranslation(translation);

      //    System.out.println("B translation = \n" + transform);
      ReferenceFrame B = ReferenceFrame.constructFrameWithUnchangingTransformToParent("B", A, transform);

      transform = new RigidBodyTransform();
      euler = new Vector3D(0.0, Math.PI / 2.0, 0.0);
      transform.setRotationEulerAndZeroTranslation(euler);
      translation = new Vector3D(5.0, 0.0, 0.0);
      transform.setTranslation(translation);

      //    System.out.println("C translation = \n" + transform);
      ReferenceFrame C = ReferenceFrame.constructFrameWithUnchangingTransformToParent("C", B, transform);

      transform = new RigidBodyTransform();
      euler = new Vector3D(0.0, 0.0, Math.PI / 2.0);
      transform.setRotationEulerAndZeroTranslation(euler);
      translation = new Vector3D(5.0, 0.0, 0.0);
      transform.setTranslation(translation);

      //    System.out.println("D translation = \n" + transform);
      ReferenceFrame D = ReferenceFrame.constructFrameWithUnchangingTransformToParent("D", C, transform);

      FramePoint3D V1 = new FramePoint3D(D, 2.0, 3.0, 4.0);

      // System.out.println("V1 = " + V1);
      assertEquals(2, V1.getX(), epsilon);
      assertEquals(3, V1.getY(), epsilon);
      assertEquals(4, V1.getZ(), epsilon);

      try
      {
         FramePoint3D V1inD = new FramePoint3D(V1);
         V1inD.changeFrame(D);

         //       System.out.println(V1inD);
         assertEquals(2, V1inD.getX(), epsilon);
         assertEquals(3, V1inD.getY(), epsilon);
         assertEquals(4, V1inD.getZ(), epsilon);

         FramePoint3D V1inC = new FramePoint3D(V1);
         V1inC.changeFrame(C);

         //       System.out.println(V1inC);
         assertEquals(2, V1inC.getX(), epsilon);
         assertEquals(2, V1inC.getY(), epsilon);
         assertEquals(4, V1inC.getZ(), epsilon);

         FramePoint3D V1inB = new FramePoint3D(V1);
         V1inB.changeFrame(B);

         //       System.out.println(V1inB);
         assertEquals(9, V1inB.getX(), epsilon);
         assertEquals(2, V1inB.getY(), epsilon);
         assertEquals(-2, V1inB.getZ(), epsilon);

         FramePoint3D V1inA = new FramePoint3D(V1);
         V1inA.changeFrame(A);

         //       System.out.println(V1inA);
         assertEquals(14, V1inA.getX(), epsilon);
         assertEquals(2, V1inA.getY(), epsilon);
         assertEquals(2, V1inA.getZ(), epsilon);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }

      V1 = new FramePoint3D(A, 2.0, 3.0, 4.0);
      assertEquals(2, V1.getX(), epsilon);
      assertEquals(3, V1.getY(), epsilon);
      assertEquals(4, V1.getZ(), epsilon);

      // System.out.println("V2 = " + V1);
      try
      {
         FramePoint3D V1inA = new FramePoint3D(V1);
         V1inA.changeFrame(A);
         assertEquals(2, V1inA.getX(), epsilon);
         assertEquals(3, V1inA.getY(), epsilon);
         assertEquals(4, V1inA.getZ(), epsilon);

         FramePoint3D V1inB = new FramePoint3D(V1);
         V1inB.changeFrame(B);
         assertEquals(-3, V1inB.getX(), epsilon);
         assertEquals(4, V1inB.getY(), epsilon);
         assertEquals(-3, V1inB.getZ(), epsilon);

         FramePoint3D V1inC = new FramePoint3D(V1);
         V1inC.changeFrame(C);
         assertEquals(3, V1inC.getX(), epsilon);
         assertEquals(4, V1inC.getY(), epsilon);
         assertEquals(-8, V1inC.getZ(), epsilon);

         FramePoint3D V1inD = new FramePoint3D(V1);
         V1inD.changeFrame(D);
         assertEquals(4, V1inD.getX(), epsilon);
         assertEquals(2, V1inD.getY(), epsilon);
         assertEquals(-8, V1inD.getZ(), epsilon);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }

      V1 = new FramePoint3D(B, 2.0, 3.0, 4.0);
      assertEquals(2, V1.getX(), epsilon);
      assertEquals(3, V1.getY(), epsilon);
      assertEquals(4, V1.getZ(), epsilon);

      try
      {
         FramePoint3D V1inA = new FramePoint3D(V1);
         V1inA.changeFrame(A);
         assertEquals(7, V1inA.getX(), epsilon);
         assertEquals(-4, V1inA.getY(), epsilon);
         assertEquals(3, V1inA.getZ(), epsilon);
         FramePoint3D V1inB = new FramePoint3D(V1);
         V1inB.changeFrame(B);
         assertEquals(2, V1inB.getX(), epsilon);
         assertEquals(3, V1inB.getY(), epsilon);
         assertEquals(4, V1inB.getZ(), epsilon);
         FramePoint3D V1inC = new FramePoint3D(V1);
         V1inC.changeFrame(C);
         assertEquals(-4, V1inC.getX(), epsilon);
         assertEquals(3, V1inC.getY(), epsilon);
         assertEquals(-3, V1inC.getZ(), epsilon);
         FramePoint3D V1inD = new FramePoint3D(V1);
         V1inD.changeFrame(D);
         assertEquals(3, V1inD.getX(), epsilon);
         assertEquals(9, V1inD.getY(), epsilon);
         assertEquals(-3, V1inD.getZ(), epsilon);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }

   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testOtherConstructors() //Brett was here
   {
      Tuple3DBasics position = new Point3D(1.0, 1.0, 1.0);
      String name = "myName";
      FramePoint3D framePosition = new FramePoint3D(theFrame, position);
      assertEquals("These should be equal", position, framePosition.getPoint());
      framePosition.setName(name);
      assertEquals("These should be equal", name, framePosition.getName());
      assertEquals("These should be equal", theFrame, framePosition.getReferenceFrame());

      FramePoint3D framePositionName = new FramePoint3D(aFrame, position, name);
      assertEquals("These should be equal", position, framePositionName.getPoint());
      assertEquals("These should be equal", name, framePositionName.getName());
      assertEquals("These should be equal", aFrame, framePositionName.getReferenceFrame());

      double[] doubleArray = { 7.0, 7.0, 7.0 };
      Tuple3DBasics position2 = new Point3D(doubleArray);
      String name2 = "name-O";
      FramePoint3D framePositionArray = new FramePoint3D(theFrame, doubleArray);
      assertEquals("These should be equal", position2, framePositionArray.getPoint());
      framePositionArray.setName(name2);
      assertEquals("These should be equal", name2, framePositionArray.getName());
      assertEquals("These should be equal", theFrame, framePositionArray.getReferenceFrame());

      double[] doubleArray2 = { -7.0, 14.0, 21.0 };
      Tuple3DBasics position3 = new Point3D(doubleArray2);
      String name3 = "name-P";
      FramePoint3D framePositionArrayName = new FramePoint3D(theFrame, doubleArray2);
      assertEquals("These should be equal", position3, framePositionArrayName.getPoint());
      framePositionArrayName.setName(name3);
      assertEquals("These should be equal", name3, framePositionArrayName.getName());
      assertEquals("These should be equal", theFrame, framePositionArrayName.getReferenceFrame());

      String name4 = "name-Q";
      FramePoint3D frameName = new FramePoint3D(theFrame, name4);
      Tuple3DBasics position4 = new Point3D();
      assertEquals("These should be equal", position4, frameName.getPoint());
      assertEquals("These should be equal", name4, frameName.getName());
      assertEquals("These should be equal", theFrame, frameName.getReferenceFrame());
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testMidPoint()
   {
      FramePoint3D expectedMidPoint = new FramePoint3D(theFrame, 9.0, 3.6, -1.16);
      FrameVector difference = new FrameVector(theFrame, 1.5, -3.1, 12.9);

      FramePoint3D framePoint1 = new FramePoint3D(expectedMidPoint);
      FramePoint3D framePoint2 = new FramePoint3D(expectedMidPoint);
      framePoint1.add(difference);
      framePoint2.sub(difference);

      FramePoint3D actualMidPoint = FramePoint3D.getMidPoint(framePoint1, framePoint2);

      assertTrue(expectedMidPoint.epsilonEquals(actualMidPoint, epsilon));
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000, expected = RuntimeException.class)
   public void testMidPointException()
   {
      FramePoint3D framePoint1 = new FramePoint3D(theFrame, 1.0, 2.0, 3.0);
      FramePoint3D framePoint2 = new FramePoint3D(aFrame, 0.0, -1.0, 8.2);

      FramePoint3D.getMidPoint(framePoint1, framePoint2);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testAverage()
   {
      List<FramePoint3D> framePoints = new ArrayList<FramePoint3D>();
      framePoints.add(new FramePoint3D(theFrame, 1.0, 3.0, 9.0));
      framePoints.add(new FramePoint3D(theFrame, 5.0, 3.0, 3.0));
      framePoints.add(new FramePoint3D(theFrame, 6.0, 0.0, 18.0));
      FramePoint3D average = FramePoint3D.average(framePoints);
      assertEquals(4.0, average.getX(), epsilon);
      assertEquals(2.0, average.getY(), epsilon);
      assertEquals(10.0, average.getZ(), epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000, expected = ReferenceFrameMismatchException.class)
   public void testGetXYplaneDistance()
   {
      FramePoint3D firstPoint = new FramePoint3D(theFrame, 1.0, 2.0, 5.0);
      FramePoint3D secondPoint = new FramePoint3D(theFrame, 4.0, -2.0, -3.0);

      assertEquals(5.0, firstPoint.getXYPlaneDistance(secondPoint), epsilon);

      //Test for reference frame mismatch
      FramePoint3D thirdPoint = new FramePoint3D(aFrame, 4.0, -2.0, -3.0);
      firstPoint.getXYPlaneDistance(thirdPoint);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000, expected = ReferenceFrameMismatchException.class)
   public void testDistance() //Brett
   {
      FramePoint3D framePoint1 = new FramePoint3D(theFrame);
      FramePoint3D framePoint2 = new FramePoint3D(aFrame);

      FramePoint3D framePoint = new FramePoint3D(theFrame, 1.0, 2.0, 3.0);
      double expectedReturn = Math.sqrt(14.0);
      double actualReturn = framePoint1.distance(framePoint);
      assertEquals("return value", expectedReturn, actualReturn, Double.MIN_VALUE);

      //Test for reference frame mismatch
      framePoint1.distance(framePoint2);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000, expected = ReferenceFrameMismatchException.class)
   public void testDistanceSquared() //Brett
   {
      FramePoint3D framePoint1 = new FramePoint3D(theFrame);
      FramePoint3D framePoint2 = new FramePoint3D(aFrame);

      FramePoint3D framePoint = new FramePoint3D(theFrame, 1.0, 2.0, 3.0);
      double expectedReturn = 14.0;
      double actualReturn = framePoint.distanceSquared(framePoint1);
      assertEquals("return value", expectedReturn, actualReturn, Double.MIN_VALUE);

      //Test for reference frame mismatch
      framePoint1.distanceSquared(framePoint2);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testToFramePoint2d() //Brett
   {
      FramePoint3D framePoint = new FramePoint3D(theFrame, 1.0, 2.0, 5.0);
      FramePoint2d framePoint2d = framePoint.toFramePoint2d();

      assertSame(theFrame, framePoint2d.getReferenceFrame());
      assertEquals(1.0, framePoint2d.getX(), epsilon);
      assertEquals(2.0, framePoint2d.getY(), epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testGetFramePoint2d() //Brett
   {
      FramePoint3D framePoint = new FramePoint3D(theFrame, 1.0, 2.0, 5.0);
      FramePoint2d framePoint2d = new FramePoint2d();
      framePoint.getFramePoint2d(framePoint2d);
      //      System.out.println(framePoint);
      //      System.out.println(framePoint2d);
      assertSame(theFrame, framePoint2d.getReferenceFrame());
      assertEquals(1.0, framePoint2d.getX(), epsilon);
      assertEquals(2.0, framePoint2d.getY(), epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testGetPoint()
   {
      Point3D tuple3d = new Point3D(1.0, 1.0, 1.0);
      Point3D tuple3dCopy = new Point3D();
      FramePoint3D framePoint = new FramePoint3D(theFrame, tuple3d, "framePoint");

      tuple3dCopy = framePoint.getPoint();
      assertTrue(tuple3d.epsilonEquals(tuple3dCopy, epsilon));
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testFrameChanges()
   {
      FramePoint3D framePoint = new FramePoint3D(theFrame);
      RigidBodyTransform transform3d = new RigidBodyTransform();
      FramePoint3D result = new FramePoint3D(framePoint);

      result.changeFrameUsingTransform(childFrame, transform3d);
      result.checkReferenceFrameMatch(childFrame);

      result = new FramePoint3D(framePoint);
      result.changeFrame(theFrame);
      result.checkReferenceFrameMatch(theFrame);

      framePoint.changeFrameUsingTransform(childFrame, transform3d);
      framePoint.checkReferenceFrameMatch(childFrame);

      framePoint.changeFrame(theFrame); //cause of failure
      framePoint.checkReferenceFrameMatch(theFrame);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public final void testApplyTransform()
   {
      FramePoint3D frameTuple = new FramePoint3D(theFrame);

      RigidBodyTransform transform = new RigidBodyTransform();
      transform.setTranslation(new Vector3D(2.0, 4.0, 8.0));
      frameTuple.applyTransform(transform);

      assertEquals(2.0, frameTuple.getX(), epsilon);
      assertEquals(4.0, frameTuple.getY(), epsilon);
      assertEquals(8.0, frameTuple.getZ(), epsilon);
      frameTuple.applyTransform(transform);
      assertEquals(4.0, frameTuple.getX(), epsilon);
      assertEquals(8.0, frameTuple.getY(), epsilon);
      assertEquals(16.0, frameTuple.getZ(), epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public final void testApplyTransformScale()
   {
      FramePoint3D framePoint = new FramePoint3D(theFrame, 1.0, 2.0, 3.0);
      AffineTransform transform3D = new AffineTransform();
      transform3D.setScale(0.1, 0.01, 0.001);

      FramePoint3D resultPoint = new FramePoint3D(framePoint);
      resultPoint.applyTransform(transform3D);
      FramePoint3D expectedResultPoint = new FramePoint3D(theFrame, 0.1, 0.02, 0.003);

      assertNotSame(framePoint, resultPoint);
      assertTrue(expectedResultPoint.epsilonEquals(resultPoint, epsilon));
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public final void testApplyTransformTranslate()
   {
      FramePoint3D framePoint = new FramePoint3D(theFrame, 1.0, 2.0, 3.0);
      AffineTransform transform3D = new AffineTransform();

      Vector3D translateVector = new Vector3D(0.1, 0.5, 0.9);
      transform3D.setTranslation(translateVector);
      FramePoint3D resultPoint = new FramePoint3D(framePoint);
      resultPoint.applyTransform(transform3D);
      FramePoint3D expectedResultPoint = new FramePoint3D(theFrame, 1.1, 2.5, 3.9);

      assertNotSame(framePoint, resultPoint);
      assertTrue(expectedResultPoint.epsilonEquals(resultPoint, epsilon));
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public final void testApplyTransformRotateZ()
   {
      FramePoint3D framePoint = new FramePoint3D(theFrame, 1.0, 2.0, 3.0);
      RigidBodyTransform transform3D = new RigidBodyTransform();

      Vector3D rotateVector = new Vector3D(0.0, 0.0, Math.PI / 2.0);
      transform3D.setRotationEulerAndZeroTranslation(rotateVector);
      FramePoint3D resultPoint = new FramePoint3D(framePoint);
      resultPoint.applyTransform(transform3D);
      FramePoint3D expectedResultPoint = new FramePoint3D(theFrame, -2.0, 1.0, 3.0);

      assertNotSame(framePoint, resultPoint);
      assertTrue(expectedResultPoint.epsilonEquals(resultPoint, epsilon));
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testConstructors()
   {
      Random random = new Random();
      double[] xyz = RandomNumbers.nextDoubleArray(random, 3, Double.MAX_VALUE);
      FramePoint3D pointToBeTested;
      ReferenceFrame referenceFrame = null;
      pointToBeTested = new FramePoint3D(referenceFrame, xyz);

      pointToBeTested = new FramePoint3D(aFrame, xyz);
      Point3D point3dExpected = new Point3D(xyz);
      assertTrue(aFrame == pointToBeTested.getReferenceFrame());
      assertTrue(pointToBeTested.getPoint().epsilonEquals(point3dExpected, epsilon));

      double max = Double.MAX_VALUE / 2.0;
      point3dExpected = RandomGeometry.nextPoint3D(random, max, max, max);
      pointToBeTested = new FramePoint3D(referenceFrame, point3dExpected, "");

      pointToBeTested = new FramePoint3D(aFrame, point3dExpected, "");
      assertTrue(aFrame == pointToBeTested.getReferenceFrame());
      assertTrue("Expected: " + point3dExpected + ", actual: " + pointToBeTested.getPoint(), point3dExpected.epsilonEquals(pointToBeTested.getPoint(), epsilon));

      xyz = RandomNumbers.nextDoubleArray(random, 3, Double.MAX_VALUE);
      pointToBeTested = new FramePoint3D(referenceFrame, xyz, "");

      pointToBeTested = new FramePoint3D(aFrame, xyz, "");
      point3dExpected = new Point3D(xyz);
      assertTrue(aFrame == pointToBeTested.getReferenceFrame());
      assertTrue(pointToBeTested.getPoint().epsilonEquals(point3dExpected, epsilon));
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testYawAboutPointRegression()
   {
      // Do not change value! For regression.
      Random r = new Random(2899234L);

      ReferenceFrame referenceFrame;
      FramePoint3D pointToYawAbout;
      FramePoint3D point;
      double yaw;
      FramePoint3D result;

      referenceFrame = ReferenceFrame.generateRandomReferenceFrame("randomFrame", r, ReferenceFrame.getWorldFrame());
      pointToYawAbout = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      yaw = randomAngle(r);
      result = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point.yawAboutPoint(pointToYawAbout, result, yaw);
      System.out.println(result);
      assertEquals("not equal", -2681.624165883151, result.getX(), epsilon);
      assertEquals("not equal", -1528.2007328131492, result.getY(), epsilon);
      assertEquals("not equal", 2998.298763316407, result.getZ(), epsilon);

      referenceFrame = ReferenceFrame.generateRandomReferenceFrame("randomFrame", r, ReferenceFrame.getWorldFrame());
      pointToYawAbout = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      yaw = randomAngle(r);
      result = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point.yawAboutPoint(pointToYawAbout, result, yaw);
      System.out.println(result);
      assertEquals("not equal", 2868.1077772133904, result.getX(), epsilon);
      assertEquals("not equal", -3773.703916968001, result.getY(), epsilon);
      assertEquals("not equal", -3313.247345650209, result.getZ(), epsilon);

      referenceFrame = ReferenceFrame.generateRandomReferenceFrame("randomFrame", r, ReferenceFrame.getWorldFrame());
      pointToYawAbout = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      yaw = randomAngle(r);
      result = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point.yawAboutPoint(pointToYawAbout, result, yaw);
      System.out.println(result);
      assertEquals("not equal", 9865.290784196699, result.getX(), epsilon);
      assertEquals("not equal", 1276.040690119471, result.getY(), epsilon);
      assertEquals("not equal", -3096.5574256022164, result.getZ(), epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testPitchAboutPointRegression()
   {
      // Do not change value! For regression.
      Random r = new Random(689291994L);

      ReferenceFrame referenceFrame;
      FramePoint3D pointToPitchAbout;
      FramePoint3D point;
      double pitch;
      FramePoint3D result;

      referenceFrame = ReferenceFrame.generateRandomReferenceFrame("randomFrame", r, ReferenceFrame.getWorldFrame());
      pointToPitchAbout = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      pitch = randomAngle(r);
      result = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point.pitchAboutPoint(pointToPitchAbout, result, pitch);
      System.out.println(result);
      assertEquals("not equal", -256.24551976827297, result.getX(), epsilon);
      assertEquals("not equal", 1443.7013411938358, result.getY(), epsilon);
      assertEquals("not equal", 11103.259343203952, result.getZ(), epsilon);

      referenceFrame = ReferenceFrame.generateRandomReferenceFrame("randomFrame", r, ReferenceFrame.getWorldFrame());
      pointToPitchAbout = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      pitch = randomAngle(r);
      result = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point.pitchAboutPoint(pointToPitchAbout, result, pitch);
      System.out.println(result);
      assertEquals("not equal", -2273.346187036131, result.getX(), epsilon);
      assertEquals("not equal", 3010.5651766598717, result.getY(), epsilon);
      assertEquals("not equal", -3513.344540982049, result.getZ(), epsilon);

      referenceFrame = ReferenceFrame.generateRandomReferenceFrame("randomFrame", r, ReferenceFrame.getWorldFrame());
      pointToPitchAbout = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      pitch = randomAngle(r);
      result = new FramePoint3D(referenceFrame, randomScalar(r), randomScalar(r), randomScalar(r));
      point.pitchAboutPoint(pointToPitchAbout, result, pitch);
      System.out.println(result);
      assertEquals("not equal", 3978.4131392851787, result.getX(), epsilon);
      assertEquals("not equal", 682.5708442089929, result.getY(), epsilon);
      assertEquals("not equal", 8214.605434738955, result.getZ(), epsilon);
   }

   private double randomScalar(Random random)
   {
      return (random.nextDouble() - 0.5) * 10000.0;
   }

   private double randomAngle(Random random)
   {
      return (random.nextDouble() - 0.5) * 2.0 * Math.PI;
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testYawAboutPoint()
   {
      final FramePoint3D pointToYawAboutException = new FramePoint3D(theFrame, 0.0, 0.0, 0.0);
      final FramePoint3D pointException = new FramePoint3D(ReferenceFrame.getWorldFrame(), 1.0, 1.0, 1.0);
      final FramePoint3D resultException = new FramePoint3D();
      final double yawException = Math.PI;
      Assertions.assertExceptionThrown(ReferenceFrameMismatchException.class, new RunnableThatThrows()
      {
         @Override
         public void run() throws Throwable
         {
            pointException.yawAboutPoint(pointToYawAboutException, resultException, yawException);
         }
      });

      FramePoint3D pointToYawAbout = new FramePoint3D(ReferenceFrame.getWorldFrame(), 0.0, 0.0, 0.0);
      FramePoint3D point = new FramePoint3D(ReferenceFrame.getWorldFrame(), 1.0, 1.0, 1.0);
      double yaw = Math.PI;

      FramePoint3D result = new FramePoint3D();
      point.yawAboutPoint(pointToYawAbout, result, yaw);
      //      System.out.println(result);
      assertEquals("These should be equal", -1.0, result.getX(), epsilon);
      assertEquals("These should be equal", -1.0, result.getY(), epsilon);
      assertEquals("These should be equal", 1.0, result.getZ(), epsilon);

      //Check for reference frame mismatch
      FramePoint3D point2 = new FramePoint3D(ReferenceFrame.getWorldFrame(), 1.0, 1.0, 1.0);
      point2.yawAboutPoint(pointToYawAbout, point2, yaw);

      pointToYawAbout = new FramePoint3D(ReferenceFrame.getWorldFrame(), 0.0, 0.0, 0.0);
      point = new FramePoint3D(ReferenceFrame.getWorldFrame(), 1.0, 0.0, 1.0);
      yaw = Math.PI / 2;

      result = new FramePoint3D();
      point.yawAboutPoint(pointToYawAbout, result, yaw);
      //      System.out.println(result);
      assertEquals("These should be equal", 0.0, result.getX(), epsilon);
      assertEquals("These should be equal", 1.0, result.getY(), epsilon);
      assertEquals("These should be equal", 1.0, result.getZ(), epsilon);
   }

   @ContinuousIntegrationTest(estimatedDuration = 0.0)
   @Test(timeout = 30000)
   public void testPitchAboutPoint()
   {
      final FramePoint3D pointToPitchAboutException = new FramePoint3D(theFrame, 0.0, 0.0, 0.0);
      final FramePoint3D pointException = new FramePoint3D(ReferenceFrame.getWorldFrame(), 1.0, 1.0, 1.0);
      final FramePoint3D resultException = new FramePoint3D();
      final double pitchException = Math.PI;
      Assertions.assertExceptionThrown(ReferenceFrameMismatchException.class, new RunnableThatThrows()
      {
         @Override
         public void run() throws Throwable
         {
            pointException.yawAboutPoint(pointToPitchAboutException, resultException, pitchException);
         }
      });

      FramePoint3D pointToPitchAbout = new FramePoint3D(theFrame, 0, 0, 0);
      FramePoint3D point = new FramePoint3D(theFrame, 1, 1, 1);
      double pitch = Math.PI;

      FramePoint3D result = new FramePoint3D();
      point.pitchAboutPoint(pointToPitchAbout, result, pitch);
      //      System.out.println(result);
      assertEquals("These should be equal", -1.0, result.getX(), epsilon);
      assertEquals("These should be equal", 1.0, result.getY(), epsilon);
      assertEquals("These should be equal", -1.0, result.getZ(), epsilon);
   }

   public static void assertFramePointEquals(FramePoint3D expected, FramePoint3D actual, double delta)
   {
      expected.checkReferenceFrameMatch(actual);
      EuclidCoreTestTools.assertTuple3DEquals(expected.getPoint(), actual.getPoint(), delta);
   }

   public static void main(String[] args)
   {
      MutationTestFacilitator.facilitateMutationTestForClass(FramePoint3D.class, FramePointTest.class);
   }
}
