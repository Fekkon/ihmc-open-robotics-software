package us.ihmc.exampleSimulations.skippy;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import us.ihmc.commonWalkingControlModules.controlModules.foot.SupportState;
import us.ihmc.exampleSimulations.skippy.SkippyRobot.RobotType;
import us.ihmc.robotics.dataStructures.registry.YoVariableRegistry;
import us.ihmc.robotics.dataStructures.variable.DoubleYoVariable;
import us.ihmc.robotics.geometry.AngleTools;
import us.ihmc.robotics.geometry.FramePoint;
import us.ihmc.robotics.geometry.FrameVector;
import us.ihmc.robotics.geometry.RigidBodyTransform;
import us.ihmc.robotics.math.filters.FilteredVelocityYoVariable;
import us.ihmc.robotics.math.frames.YoFramePoint;
import us.ihmc.robotics.math.frames.YoFrameVector;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;
import us.ihmc.robotics.robotSide.SideDependentList;
import us.ihmc.robotics.stateMachines.*;
import us.ihmc.simulationconstructionset.ExternalForcePoint;
import us.ihmc.simulationconstructionset.FloatingJoint;
import us.ihmc.simulationconstructionset.PinJoint;
import us.ihmc.simulationconstructionset.gui.EventDispatchThreadHelper;
import us.ihmc.simulationconstructionset.robotController.RobotController;

public class SkippyController implements RobotController
{

   private enum States
   {
      BALANCE,
      PREPARE,
      LIFTOFF,
      REPOSITION,
      RECOVER
   }

   private final StateMachine<States> stateMachine;

   private final YoVariableRegistry registry = new YoVariableRegistry("SkippyController");

   // tau_* is torque, q_* is position, qd_* is velocity for joint *
//   private DoubleYoVariable q_foot_X, q_hip, qHipIncludingOffset, qd_foot_X, qd_hip, qd_shoulder;
   private final DoubleYoVariable k1, k2, k3, k4, k5, k6, k7, k8, angleToCoMInYZPlane, angleToCoMInXZPlane, angularVelocityToCoMYZPlane, angularVelocityToCoMXZPlane; // controller gain parameters
   private final DoubleYoVariable planarDistanceYZPlane, planarDistanceXZPlane;

   private final DoubleYoVariable alphaAngularVelocity;
   private final FilteredVelocityYoVariable angularVelocityToCoMYZPlane2, angularVelocityToCoMXZPlane2;

   private final YoFramePoint bodyLocation = new YoFramePoint("body", ReferenceFrame.getWorldFrame(), registry);

   private final YoFramePoint centerOfMass = new YoFramePoint("centerOfMass", ReferenceFrame.getWorldFrame(), registry);
   private final YoFramePoint footLocation = new YoFramePoint("foot", ReferenceFrame.getWorldFrame(), registry);
   private final YoFrameVector footToCoMInBodyFrame;
   private final ExternalForcePoint forceToCOM;

   private final DoubleYoVariable robotMass = new DoubleYoVariable("robotMass", registry);
   private final DoubleYoVariable qHipIncludingOffset = new DoubleYoVariable("qHipIncludingOffset", registry);
   private final DoubleYoVariable qDHipIncludingOffset = new DoubleYoVariable("qDHipIncludingOffset", registry);
   private final DoubleYoVariable qDShoulderIncludingOffset = new DoubleYoVariable("qDShoulderIncludingOffset", registry);
   private final DoubleYoVariable q_d_hip = new DoubleYoVariable("q_d_hip", registry);
   private final DoubleYoVariable qShoulderIncludingOffset = new DoubleYoVariable("qShoulderIncludingOffset", registry);
   private final DoubleYoVariable q_d_shoulder = new DoubleYoVariable("q_d_shoulder", registry);

   private String name;
   private SkippyRobot robot;
   private RobotType robotType;

   private static ArrayList<double[]> desiredPositions;

   private double legIntegralTermX = 0.0;
   private double legIntegralTermY = 0.0;
   private double hipIntegralTerm = 0.0;
   private double shoulderIntegralTerm = 0.0;

   private static int timeCounter = 0;

   public SkippyController(SkippyRobot robot, RobotType robotType, String name, double controlDT)
   {
      this.name = name;
      this.robot = robot;
      this.robotType = robotType;

      footToCoMInBodyFrame = new YoFrameVector("footToCoMInBody", robot.updateAndGetBodyFrame(), registry);
      forceToCOM = new ExternalForcePoint("FORCETOCOM", robot);

      k1 = new DoubleYoVariable("k1", registry);
      k2 = new DoubleYoVariable("k2", registry);
      k3 = new DoubleYoVariable("k3", registry);
      k4 = new DoubleYoVariable("k4", registry);
      k5 = new DoubleYoVariable("k5", registry);
      k6 = new DoubleYoVariable("k6", registry);
      k7 = new DoubleYoVariable("k7", registry);
      k8 = new DoubleYoVariable("k8", registry);

      k1.set(-3600.0); //110);
      k2.set(-1500.0); //-35);
      k3.set(-170.0); //30);
      k4.set(-130.0); //-15);

      k5.set(-1900);
      k6.set(-490.0);
      k7.set(-60.0);
      k8.set(-45.0);

      q_d_hip.set(0.6);  //some values don't work too well - angles that result in a more balanced model work better
      q_d_shoulder.set(0.0);

      planarDistanceYZPlane = new DoubleYoVariable("planarDistanceYZPlane", registry);
      planarDistanceXZPlane = new DoubleYoVariable("planarDistanceXZPlane", registry);
      angleToCoMInYZPlane = new DoubleYoVariable("angleToCoMYZPlane", registry);
      angleToCoMInXZPlane = new DoubleYoVariable("angleToCoMXZPlane", registry);
      angularVelocityToCoMYZPlane = new DoubleYoVariable("angularVelocityToCoMYZPlane", registry);
      angularVelocityToCoMXZPlane = new DoubleYoVariable("angularVelocityToCoMXZPlane", registry);

      alphaAngularVelocity = new DoubleYoVariable("alphaAngularVelocity", registry);
      alphaAngularVelocity.set(0.8);
      angularVelocityToCoMYZPlane2 = new FilteredVelocityYoVariable("angularVelocityToCoMYZPlane2", "", alphaAngularVelocity, angleToCoMInYZPlane, controlDT, registry);
      angularVelocityToCoMXZPlane2 = new FilteredVelocityYoVariable("angularVelocityToCoMXZPlane2", "", alphaAngularVelocity, angleToCoMInXZPlane, controlDT, registry);

      //createPositionsForShowForPositionControl();

      stateMachine = new StateMachine<States>("stateMachine", "stateMachineTime", States.class, robot.t, registry);
      setUpStateMachines();
      createStateMachineWindow();
   }

   public void createPositionsForShowForPositionControl()
   {
      //for show
      desiredPositions = new ArrayList<double[]>();
      //double[] position = {qLegDesiredX, qLegDesiredY, qHipDesired, qShoulderDesired} <- format
      double[] firstSetOfPoints = {0.0,0.0,0.0,0.0};
      double[] secondSetOfPoints = {Math.PI/4, 0.0, -2*Math.PI/4, 0.0};
      double[] thirdSetOfPoints = {-Math.PI/4, 0.0, 2*Math.PI/4, 0.0};

      desiredPositions.add(secondSetOfPoints);
      desiredPositions.add(thirdSetOfPoints);
      desiredPositions.add(firstSetOfPoints);
   }

   public void doControl()
   {
      computeCenterOfMass();
      computeFootToCenterOfMassLocation();

      balanceControl();
      //positionControl();
   }


   private final FramePoint tempFootLocation = new FramePoint(ReferenceFrame.getWorldFrame());
   private final FramePoint tempCoMLocation = new FramePoint(ReferenceFrame.getWorldFrame());
   private final FrameVector tempFootToCoM = new FrameVector(ReferenceFrame.getWorldFrame());

   private void computeCenterOfMass()
   {
      Point3d tempCenterOfMass = new Point3d();
      robotMass.set(robot.computeCenterOfMass(tempCenterOfMass));
      centerOfMass.set(tempCenterOfMass);
   }

   private void computeFootToCenterOfMassLocation()
   {
      ReferenceFrame bodyFrame = robot.updateAndGetBodyFrame();

      FramePoint bodyPoint = new FramePoint(bodyFrame);
      bodyPoint.changeFrame(ReferenceFrame.getWorldFrame());

      bodyLocation.set(bodyPoint);

      footLocation.set(robot.computeFootLocation());

      footLocation.getFrameTupleIncludingFrame(tempFootLocation);
      centerOfMass.getFrameTupleIncludingFrame(tempCoMLocation);

      tempFootLocation.changeFrame(bodyFrame);
      tempCoMLocation.changeFrame(bodyFrame);

      tempFootToCoM.setIncludingFrame(tempCoMLocation);
      tempFootToCoM.sub(tempFootLocation);

      footToCoMInBodyFrame.set(tempFootToCoM);
   }

   private void balanceControl()
   {
      stateMachine.doAction();
      stateMachine.checkTransitionConditions();
      applyTorqueToHip(q_d_hip.getDoubleValue());
      applyTorqueToShoulder(q_d_shoulder.getDoubleValue());
   }

   private void applyTorqueToHip(double hipDesired)
   {
      /*
         angular pos : angle created w/ com to groundpoint against vertical
       */

//      double footToComZ = centerOfMass.getZ()-footLocation.getZ();
//      double footToComY = centerOfMass.getY()-footLocation.getY();

      double footToComZ = footToCoMInBodyFrame.getZ();
      double footToComY = footToCoMInBodyFrame.getY();

      planarDistanceYZPlane.set(Math.sqrt(Math.pow(centerOfMass.getY()-footLocation.getY(), 2) + Math.pow(footToComZ, 2)));
      double angle = (Math.atan2(footToComY, footToComZ));
      angleToCoMInYZPlane.set(angle);

      /*
         angular vel : angle created w/ com to groundpoint against vertical
       */
      Vector3d linearMomentum = new Vector3d();
      robot.computeLinearMomentum(linearMomentum);

      //1: projection vector
      Vector3d componentPerpendicular = new Vector3d(0, 1, -centerOfMass.getY()/centerOfMass.getZ());
      componentPerpendicular.normalize();
      double angleVel = componentPerpendicular.dot(linearMomentum) / componentPerpendicular.length();
      angleVel = angleVel / robotMass.getDoubleValue();

      //2: not used
      //double angleVel = Math.pow(Math.pow(linearMomentum.getY(), 2) + Math.pow(linearMomentum.getZ(), 2), 0.5)/robotMass;
      //angleVel = angleVel / planarDistanceYZPlane;

      //3: average rate of change (buggy)
      //double angleVel = (angle - prevAngleHip) / SkippySimulation.DT;

      angularVelocityToCoMYZPlane.set(angleVel);
      angularVelocityToCoMYZPlane2.update();
      double angularVelocityForControl = angularVelocityToCoMYZPlane2.getDoubleValue();

      /*
         angular pos/vel of hipjoint
       */
      double hipAngle = 0;
      double hipAngleVel = 0;
      double[] hipAngleValues = new double[2];

      hipAngleValues = calculateAnglePosAndDerOfHipJointTippy(robot.getHipJointTippy());

      hipAngle = hipAngleValues[0];
      hipAngleVel = hipAngleValues[1];
      qHipIncludingOffset.set((hipAngle));
      qDHipIncludingOffset.set(hipAngleVel);

      robot.getHipJointTippy().setTau(k1.getDoubleValue() * (0.0 - angle) + k2.getDoubleValue() * (0.0 - angularVelocityForControl) + k3.getDoubleValue() * (hipDesired - hipAngle) + k4.getDoubleValue() * (0.0 - hipAngleVel));


      //torque set (alternate version for torque on hipJoint - not used)
//      if(robotType == RobotType.TIPPY)
//      {
//         robot.getHipJointTippy().setTau(k1.getDoubleValue() * (0.0 - angle) + k2.getDoubleValue() * (0.0 - angularVelocityForControl) + k3.getDoubleValue() * (hipDesired - hipAngle) + k4.getDoubleValue() * (0.0 - hipAngleVel));
//      }
//      else if(robotType == RobotType.SKIPPY)
//      {
//         //torque ~> force ; probably will create a method for this
//
//         double tau = k1.getDoubleValue() * (0.0 - angle) + k2.getDoubleValue() * (0.0 - angularVelocityForControl) + k3.getDoubleValue() * (hipDesired - hipAngle) + k4.getDoubleValue() * (0.0 - hipAngleVel);
//         Vector3d point2 = createVectorInDirectionOfHipJointAlongHip();
//         Vector3d forceDirectionVector = new Vector3d(0, 1.0, point2.getY()/point2.getZ()*1.0);
//         forceDirectionVector.normalize();
//         forceDirectionVector.scale(tau/(robot.getHipLength()/2.0));
//         robot.setRootJointForce(forceDirectionVector.getX(), forceDirectionVector.getY(), forceDirectionVector.getZ());
//      }

   }

   private Vector3d createVectorInDirectionOfHipJointAlongHip()
   {
      Vector3d rootJointCoordinates = new Vector3d();
      robot.getHipJointSkippy().getTranslationToWorld(rootJointCoordinates);
      Vector3d hipEndPointCoordinates = new Vector3d();
      robot.getGroundContactPoints().get(1).getPosition(hipEndPointCoordinates);
      rootJointCoordinates.sub(hipEndPointCoordinates);
      return rootJointCoordinates;
   }

   private void applyTorqueToShoulder(double shoulderDesired)
   {
      /*
         angular pos : angle created w/ com to groundpoint against vertical
       */

      double footToComZ = footToCoMInBodyFrame.getZ();
      double footToComX = footToCoMInBodyFrame.getX();

      planarDistanceXZPlane.set(Math.sqrt(Math.pow(footToComX, 2) + Math.pow(footToComZ, 2)));
      double angle = (Math.atan2(footToComX, footToComZ));
      angleToCoMInXZPlane.set(angle);

      /*
         angular vel : angle created w/ com to groundpoint against vertical
       */
      Vector3d linearMomentum = new Vector3d();
      robot.computeLinearMomentum(linearMomentum);

      //1: projection vector
      Vector3d componentPerpendicular = new Vector3d(1, 0, -centerOfMass.getX()/centerOfMass.getZ());
      componentPerpendicular.normalize();
      double angleVel = componentPerpendicular.dot(linearMomentum) / componentPerpendicular.length();
      angleVel = angleVel / robotMass.getDoubleValue();

      //2: not used
      //double angleVel = Math.pow(Math.pow(linearMomentum.getY(), 2) + Math.pow(linearMomentum.getZ(), 2), 0.5)/robotMass;
      //angleVel = angleVel / planarDistanceYZPlane;

      //3: average rate of change (buggy)
      //double angleVel = (angle - prevAngleHip) / SkippySimulation.DT;

      angularVelocityToCoMXZPlane.set(angleVel);
      angularVelocityToCoMXZPlane2.update();
      double angularVelocityForControl = angularVelocityToCoMXZPlane2.getDoubleValue();


      /*
         angular pos/vel of hipjoint
       */
      double shoulderAngle = 0;
      double shoulderAngleVel = 0;
      double[] shoulderAngleValues = new double[2];

      shoulderAngleValues = calculateAnglePosAndDerOfShoulderJointTippy(robot.getShoulderJoint());

      shoulderAngle = shoulderAngleValues[0];
      shoulderAngleVel = shoulderAngleValues[1];
      qShoulderIncludingOffset.set((shoulderAngle));
      qDShoulderIncludingOffset.set(shoulderAngleVel);

      double shoulderAngleError = AngleTools.computeAngleDifferenceMinusPiToPi(shoulderDesired, shoulderAngle);
      robot.getShoulderJoint().setTau(k5.getDoubleValue()*Math.sin(0.0-angle) + k6.getDoubleValue()*(0.0 - angularVelocityForControl) + k7.getDoubleValue()*(shoulderAngleError) + k8.getDoubleValue()*(0.0 - shoulderAngleVel));

   }

   private Vector3d createVectorInDirectionOfShoulderJointAlongShoulder()
   {
      Vector3d shoulderJointCoordinates = new Vector3d();
      robot.getShoulderJoint().getTranslationToWorld(shoulderJointCoordinates);
      Vector3d shoulderEndPointCoordinates = new Vector3d();
      robot.getGroundContactPoints().get(2).getPosition(shoulderEndPointCoordinates);
      shoulderEndPointCoordinates.sub(shoulderJointCoordinates);
      return shoulderEndPointCoordinates;
   }

   private double[] calculateAnglePosAndDerOfHipJointTippy(PinJoint joint)
   {
      double[] finale = new double[2];

      //for different definition of hipJointAngle (angle b/w hipJoint and vertical (z axis) )
//      double firstAngle = robot.getLegJoint().getQ().getDoubleValue()%(Math.PI*2);
//      if(firstAngle>Math.PI)
//         firstAngle = (Math.PI*2-firstAngle)*-1;
//      double angle = (joint.getQ().getDoubleValue())%(Math.PI*2)+firstAngle;
//      if(angle > Math.PI)
//         angle = angle - Math.PI*2;

      double angle = joint.getQ().getDoubleValue();
      double angleVel = joint.getQD().getDoubleValue();
      finale[0] = angle;
      finale[1] = (angleVel);
      return finale;
   }

   private double[] calculateAnglePosAndDerOfHipJointSkippy(FloatingJoint joint)  //using groundcontact points to create vectors
   {
      double[] finale = new double[2];

      Vector3d verticalVector = new Vector3d(0.0, 0.0, 1.0);
      Vector3d floatVector = createVectorInDirectionOfHipJointAlongHip();
      verticalVector.setX(0.0);
      floatVector.setX(0.0);  //angle wrt yz plane only

      double cosineTheta = (floatVector.dot(verticalVector)/(floatVector.length() * verticalVector.length()));
      double angle = Math.acos(cosineTheta);
      if(floatVector.getY()<0)
         angle = angle * -1;

      double angleVel = robot.getLegJoint().getQD().getDoubleValue();  //increases same speed wrt angle diff. between root and leg
      finale[0] = angle;
      finale[1] = (angleVel);
      return finale;
   }

   private double[] calculateAnglePosAndDerOfShoulderJointTippy(PinJoint joint)
   {
      double[] finale = new double[2];

      //for different definition of shoulderJointAngle (angle b/w shoulderJoint and vertical (z-axis) )
//      double firstAngle = 0;
//
//      firstAngle = (robot.getLegJoint().getSecondJoint().getQ().getDoubleValue())%(Math.PI*2);
//      if(firstAngle>Math.PI)
//         firstAngle = (Math.PI*2-firstAngle)*-1;
//      double angle = (joint.getQ().getDoubleValue())%(Math.PI*2)+firstAngle;
//      if(angle > Math.PI)
//         angle = angle - Math.PI*2;

      double angle = joint.getQ().getDoubleValue();
      double angleVel = joint.getQD().getDoubleValue();

      finale[0] = angle;
      finale[1] = angleVel;
      return finale;
   }

   private double[] calculateAnglePosAndDerOfShoulderJointSkippy(PinJoint joint)
   {
      double[] finale = new double[2];

      Vector3d horizontalVector = new Vector3d(1.0, 0.0, 0.0);
      Vector3d shoulderVector = createVectorInDirectionOfShoulderJointAlongShoulder();
      horizontalVector.setY(0);
      shoulderVector.setY(0);

      double cosineTheta = (horizontalVector.dot(shoulderVector)/(horizontalVector.length()*shoulderVector.length()));
      double angle = Math.abs(Math.acos(cosineTheta));

      Vector3d shoulderJointPosition = new Vector3d();
      joint.getTranslationToWorld(shoulderJointPosition);

      if(robot.getGroundContactPoints().get(2).getZ()<shoulderJointPosition.getZ())
         angle = angle * -1;

      double angleVel = robot.getShoulderJoint().getQD().getDoubleValue();
      finale[0] = angle;
      finale[1] = angleVel;
      return finale;
   }

   private double fromRadiansToDegrees(double radians)
   {
      return radians * 180 / Math.PI;
   }


   private void positionControl()
   {
      double interval = Math.round(SkippySimulation.TIME/desiredPositions.size()*100.0)/100.0;
      double time = Math.round(robot.getTime()*(1/SkippySimulation.DT))/(1/SkippySimulation.DT);

      positionJointsBasedOnError(robot.getLegJoint(),desiredPositions.get(timeCounter)[0], legIntegralTermX, 20000, 150, 2000, true);
      positionJointsBasedOnError(robot.getLegJoint().getSecondJoint(), desiredPositions.get(timeCounter)[1], legIntegralTermY, 20000, 150, 2000, false);
      positionJointsBasedOnError(robot.getHipJointTippy(), desiredPositions.get(timeCounter)[2], hipIntegralTerm, 20000, 150, 2000, false);
      positionJointsBasedOnError(robot.getShoulderJoint(), desiredPositions.get(timeCounter)[3], shoulderIntegralTerm, 20000, 150, 2000, false);
      //System.out.println();

      if(time%interval==0 && time != 0.0 && timeCounter < desiredPositions.size())
      {
         timeCounter++;
         legIntegralTermX = 0;
         legIntegralTermY = 0;
         hipIntegralTerm = 0;
         shoulderIntegralTerm = 0;
      }
      if(timeCounter==desiredPositions.size())
         timeCounter = desiredPositions.size()-1;
   }

   public void positionJointsBasedOnError(PinJoint joint, double desiredValue, double integralTerm, double positionErrorGain, double integralErrorGain, double derivativeErrorGain, boolean isBasedOnWorldCoordinates)
   {
      //try to change position based on angular position wrt xyz coordinate system
      Matrix3d rotationMatrixForWorld = new Matrix3d();
      joint.getRotationToWorld(rotationMatrixForWorld);
      double rotationToWorld = Math.asin((rotationMatrixForWorld.getM21()));
      //if(rotationMatrixForWorld.getM11()<0)
      //   rotationToWorld = rotationToWorld * -1;
      if(isBasedOnWorldCoordinates)
         System.out.println(joint.getName() + " " + (joint.getQ().getDoubleValue()) + " " + rotationToWorld);
      else
         rotationToWorld = joint.getQ().getDoubleValue();


      double positionError = (positionErrorGain)*((desiredValue-rotationToWorld));
      integralTerm += (integralErrorGain)*positionError*SkippySimulation.DT;
      double derivativeError = (derivativeErrorGain)*(0-joint.getQD().getDoubleValue());
      joint.setTau(positionError+integralTerm+derivativeError);
      //System.out.print(joint.getName() + ": " + (joint.getQ().getDoubleValue() - desiredValue));
   }

   public YoVariableRegistry getYoVariableRegistry()
   {
      return registry;
   }

   public String getName()
   {
      return name;
   }

   public void initialize()
   {
   }

   public String getDescription()
   {
      return getName();
   }




   /*
      STATE MACHINES
    */

   private void setUpStateMachines()
   {
      //states
      State<States> balanceState = new BalanceState();
      State<States> prepareState = new PrepareState();
      State<States> liftoffState = new LiftoffState();
      State<States> repositionState = new RepositionState();
      State<States> recoverState = new RecoverState();

      //transitions
      StateTransitionCondition balanceTransitionCondition = new BalanceTransitionCondition();
      StateTransitionCondition prepareTransitionCondition = new PrepareTransitionCondition();
      StateTransitionCondition liftoffTransitionCondition = new LiftoffTransitionCondition();
      StateTransitionCondition repositionTransitionCondition = new RepositionTransitionCondition();
      StateTransitionCondition recoverTransitionCondition = new RecoverTransitionCondition();

      StateTransition<States> balanceToPrepare = new StateTransition<States>(States.PREPARE, balanceTransitionCondition);
      balanceState.addStateTransition(balanceToPrepare);

      StateTransition<States> prepareToLiftoff = new StateTransition<States>(States.LIFTOFF, prepareTransitionCondition);
      prepareState.addStateTransition(prepareToLiftoff);

      StateTransition<States> liftoffToReposition = new StateTransition<States>(States.REPOSITION, liftoffTransitionCondition);
      liftoffState.addStateTransition(liftoffToReposition);

      StateTransition<States> repositionToRecover = new StateTransition<States>(States.RECOVER, repositionTransitionCondition);
      repositionState.addStateTransition(repositionToRecover);

      StateTransition<States> recoverToBalance = new StateTransition<States>(States.BALANCE, recoverTransitionCondition);
      recoverState.addStateTransition(recoverToBalance);

      stateMachine.addState(balanceState);
      stateMachine.addState(prepareState);
      stateMachine.addState(liftoffState);
      stateMachine.addState(repositionState);
      stateMachine.addState(recoverState);

      stateMachine.setCurrentState(States.BALANCE);
   }

   public void createStateMachineWindow()
   {
      EventDispatchThreadHelper.invokeAndWait(new Runnable()
      {
         public void run()
         {
            createStateMachineWindowLocal();
         }
      });
   }

   public void createStateMachineWindowLocal()
   {
      JFrame frame = new JFrame("Skippy Jump State Machine");
      Container contentPane = frame.getContentPane();
      contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

      StateMachinesJPanel<States> stateMachinePanel = new StateMachinesJPanel<States>(stateMachine, true);

      frame.getContentPane().add(stateMachinePanel);

      frame.pack();
      frame.setSize(450, 300);
      frame.setAlwaysOnTop(false);
      frame.setVisible(true);

      stateMachine.attachStateChangedListener(stateMachinePanel);
   }

   public class BalanceTransitionCondition implements StateTransitionCondition
   {
      public BalanceTransitionCondition()
      {

      }
      public boolean checkCondition()
      {
         double time = robot.t.getDoubleValue() % SkippySimulation.TIME;
         return time < 4.01 && time > 3.99;
      }
   }
   public class PrepareTransitionCondition implements StateTransitionCondition
   {
      public PrepareTransitionCondition()
      {

      }
      public boolean checkCondition()
      {
         double time = robot.t.getDoubleValue() % SkippySimulation.TIME;
         return time < 11.01 && time > 10.99;
      }
   }
   public class LiftoffTransitionCondition implements StateTransitionCondition
   {
      public LiftoffTransitionCondition()
      {

      }
      public boolean checkCondition()
      {
         double time = robot.t.getDoubleValue() % SkippySimulation.TIME;
         return time < 11.38 && time > 11.36;
      }
   }
   public class RepositionTransitionCondition implements StateTransitionCondition
   {
      public RepositionTransitionCondition()
      {

      }
      public boolean checkCondition()
      {
         double time = robot.t.getDoubleValue() % SkippySimulation.TIME;
         return time < 11.99 && time > 11.97;
      }
   }
   public class RecoverTransitionCondition implements StateTransitionCondition
   {
      public RecoverTransitionCondition()
      {

      }
      public boolean checkCondition()
      {
         double time = robot.t.getDoubleValue() % SkippySimulation.TIME;
         return time < 20.01 && time > 19.99;
      }
   }


   private class BalanceState extends State<States>
   {
      public BalanceState()
      {
         super(States.BALANCE);
      }
      public void doAction()
      {
         q_d_hip.set(0.6);
      }
      public void doTransitionIntoAction()
      {

      }
      public void doTransitionOutOfAction()
      {

      }
   }

   private class PrepareState extends State<States>
   {
      public PrepareState()
      {
         super(States.PREPARE);
      }
      public void doAction()
      {
         q_d_hip.set(1.6);
      }
      public void doTransitionIntoAction()
      {

      }
      public void doTransitionOutOfAction()
      {

      }
   }

   private class LiftoffState extends State<States>
   {
      public LiftoffState()
      {
         super(States.LIFTOFF);
      }
      public void doAction()
      {
         k1.set(0.0);
         k2.set(0.0);
         k3.set(300.0);
         k4.set(30.0);
         q_d_hip.set(0.45);
      }
      public void doTransitionIntoAction()
      {

      }
      public void doTransitionOutOfAction()
      {

      }
   }

   private class RepositionState extends State<States>
   {
      public RepositionState()
      {
         super(States.REPOSITION);
      }
      public void doAction()
      {
         q_d_hip.set(-1.3);
      }
      public void doTransitionIntoAction()
      {

      }
      public void doTransitionOutOfAction()
      {

      }
   }

   private class RecoverState extends State<States>
   {
      public RecoverState()
      {
         super(States.RECOVER);
      }
      public void doAction()
      {
         k1.set(-3600.0);
         k2.set(-1500.0);
         k3.set(-170.0);
         k4.set(-130.0);

         q_d_hip.set(0.6);
         q_d_shoulder.set(0.0);
      }
      public void doTransitionIntoAction()
      {

      }
      public void doTransitionOutOfAction()
      {

      }
   }
}
