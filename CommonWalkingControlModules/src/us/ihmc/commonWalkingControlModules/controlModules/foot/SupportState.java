package us.ihmc.commonWalkingControlModules.controlModules.foot;

import us.ihmc.commonWalkingControlModules.bipedSupportPolygons.YoContactPoint;
import us.ihmc.commonWalkingControlModules.bipedSupportPolygons.YoPlaneContactState;
import us.ihmc.commonWalkingControlModules.configurations.WalkingControllerParameters;
import us.ihmc.commonWalkingControlModules.controlModules.foot.FootControlModule.ConstraintType;
import us.ihmc.commonWalkingControlModules.controllerCore.command.feedbackController.FeedbackControlCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.feedbackController.SpatialFeedbackControlCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseDynamics.InverseDynamicsCommand;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseDynamics.InverseDynamicsCommandList;
import us.ihmc.commonWalkingControlModules.controllerCore.command.inverseDynamics.SpatialAccelerationCommand;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.graphicsDescription.yoGraphics.YoGraphicReferenceFrame;
import us.ihmc.graphicsDescription.yoGraphics.YoGraphicsListRegistry;
import us.ihmc.robotModels.FullHumanoidRobotModel;
import us.ihmc.robotics.InterpolationTools;
import us.ihmc.robotics.controllers.YoSE3PIDGainsInterface;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.BooleanYoVariable;
import us.ihmc.yoVariables.variable.DoubleYoVariable;
import us.ihmc.robotics.geometry.*;
import us.ihmc.robotics.referenceFrames.PoseReferenceFrame;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;
import us.ihmc.robotics.screwTheory.RigidBody;
import us.ihmc.robotics.screwTheory.SelectionMatrix6D;
import us.ihmc.robotics.screwTheory.Twist;
import us.ihmc.robotics.sensors.FootSwitchInterface;
import us.ihmc.robotics.weightMatrices.SolverWeightLevels;

/**
 * This is the active foot state when the foot is in flat support. Usually the command to the QP
 * should be a zero acceleration command. When the foot is barely loaded or the CoP gets close
 * to the edges of the foot polygon some of the directions start becoming feedback controlled. E.g.
 * when barely loaded x and y position as well as foot yaw are controlled to remain constant.
 *
 * The state also contains the ability to shift the CoP around within the foothold in case the
 * support area needs to be explored.
 */
public class SupportState extends AbstractFootControlState
{
   private static final double defaultFootLoadThreshold = 0.2;
   private static final int dofs = Twist.SIZE;

   private final YoVariableRegistry registry;

   private final FrameConvexPolygon2d footPolygon = new FrameConvexPolygon2d();

   private final BooleanYoVariable footBarelyLoaded;
   private final BooleanYoVariable copOnEdge;
   private final DoubleYoVariable footLoadThreshold;
   private final boolean[] isDirectionFeedbackControlled = new boolean[dofs];

   private final FootSwitchInterface footSwitch;

   private final PoseReferenceFrame controlFrame;
   private final PoseReferenceFrame desiredSoleFrame;
   private final YoGraphicReferenceFrame frameViz;

   private final InverseDynamicsCommandList inverseDynamicsCommandsList = new InverseDynamicsCommandList();
   private final SpatialAccelerationCommand spatialAccelerationCommand = new SpatialAccelerationCommand();
   private final SpatialFeedbackControlCommand spatialFeedbackControlCommand = new SpatialFeedbackControlCommand();

   private final SelectionMatrix6D accelerationSelectionMatrix = new SelectionMatrix6D();
   private final SelectionMatrix6D feedbackSelectionMatrix = new SelectionMatrix6D();

   private final FramePoint2d cop2d = new FramePoint2d();
   private final FramePoint framePosition = new FramePoint();
   private final FrameOrientation frameOrientation = new FrameOrientation();
   private final FramePose bodyFixedControlledPose = new FramePose();
   private final FramePoint desiredCopPosition = new FramePoint();

   private final FramePoint2d cop = new FramePoint2d();
   private final FramePoint2d desiredCoP = new FramePoint2d();

   private final FramePoint footPosition = new FramePoint();
   private final FrameOrientation footOrientation = new FrameOrientation();

   // For testing:
   private final BooleanYoVariable assumeCopOnEdge;
   private final BooleanYoVariable assumeFootBarelyLoaded;
   private final BooleanYoVariable neverHoldRotation;

   // For line contact walking and balancing:
   private final BooleanYoVariable holdFootOrientationFlat;

   // For foothold exploration:
   private final ExplorationHelper explorationHelper;
   private final PartialFootholdControlModule partialFootholdControlModule;
   private final BooleanYoVariable requestFootholdExploration;
   private final DoubleYoVariable recoverTime;
   private final DoubleYoVariable timeBeforeExploring;

   // For straight legs with privileged configuration
   private final RigidBody pelvis;

   // Toe contact point loading time
   private final boolean rampUpAllowableToeLoadAfterContact;
   private final DoubleYoVariable toeLoadingDuration;
   private final DoubleYoVariable fullyLoadedMagnitude;

   private final FramePoint tempPoint = new FramePoint();

   public SupportState(FootControlHelper footControlHelper, YoSE3PIDGainsInterface holdPositionGains, YoVariableRegistry parentRegistry)
   {
      super(ConstraintType.FULL, footControlHelper);
      String prefix = footControlHelper.getRobotSide().getLowerCaseName() + "Foot";
      registry = new YoVariableRegistry(prefix + getClass().getSimpleName());
      parentRegistry.addChild(registry);

      footSwitch = footControlHelper.getHighLevelHumanoidControllerToolbox().getFootSwitches().get(robotSide);
      controlFrame = new PoseReferenceFrame(prefix + "HoldPositionFrame", contactableFoot.getSoleFrame());
      desiredSoleFrame = new PoseReferenceFrame(prefix + "DesiredSoleFrame", worldFrame);

      footBarelyLoaded = new BooleanYoVariable(prefix + "BarelyLoaded", registry);
      copOnEdge = new BooleanYoVariable(prefix + "CopOnEdge", registry);
      footLoadThreshold = new DoubleYoVariable(prefix + "LoadThreshold", registry);
      footLoadThreshold.set(defaultFootLoadThreshold);

      WalkingControllerParameters walkingControllerParameters = footControlHelper.getWalkingControllerParameters();
      rampUpAllowableToeLoadAfterContact = walkingControllerParameters.rampUpAllowableToeLoadAfterContact();
      toeLoadingDuration = new DoubleYoVariable(prefix + "ToeContactPointLoadingTime", registry);
      fullyLoadedMagnitude = new DoubleYoVariable(prefix + "FullyLoadedMagnitude", registry);
      toeLoadingDuration.set(walkingControllerParameters.getToeLoadingDuration());
      fullyLoadedMagnitude.set(walkingControllerParameters.getFullyLoadedToeForce());

      FullHumanoidRobotModel fullRobotModel = footControlHelper.getHighLevelHumanoidControllerToolbox().getFullRobotModel();
      pelvis = fullRobotModel.getPelvis();

      spatialAccelerationCommand.setWeight(SolverWeightLevels.FOOT_SUPPORT_WEIGHT);
      spatialAccelerationCommand.set(rootBody, contactableFoot.getRigidBody());
      spatialAccelerationCommand.setPrimaryBase(pelvis);

      spatialFeedbackControlCommand.setWeightForSolver(SolverWeightLevels.FOOT_SUPPORT_WEIGHT);
      spatialFeedbackControlCommand.set(rootBody, contactableFoot.getRigidBody());
      spatialFeedbackControlCommand.setPrimaryBase(pelvis);
      spatialFeedbackControlCommand.setGains(holdPositionGains);

      desiredLinearVelocity.setToZero(worldFrame);
      desiredAngularVelocity.setToZero(worldFrame);
      desiredLinearAcceleration.setToZero(worldFrame);
      desiredAngularAcceleration.setToZero(worldFrame);

      assumeCopOnEdge = new BooleanYoVariable(prefix + "AssumeCopOnEdge", registry);
      assumeFootBarelyLoaded = new BooleanYoVariable(prefix + "AssumeFootBarelyLoaded", registry);
      neverHoldRotation = new BooleanYoVariable(prefix + "NeverHoldRotation", registry);
      holdFootOrientationFlat = new BooleanYoVariable(prefix + "HoldFlatOrientation", registry);

      explorationHelper = new ExplorationHelper(contactableFoot, footControlHelper, prefix, registry);
      partialFootholdControlModule = footControlHelper.getPartialFootholdControlModule();
      requestFootholdExploration = new BooleanYoVariable(prefix + "RequestFootholdExploration", registry);
      ExplorationParameters explorationParameters = walkingControllerParameters.getOrCreateExplorationParameters(registry);
      if (explorationParameters != null)
      {
         recoverTime = explorationParameters.getRecoverTime();
         timeBeforeExploring = explorationParameters.getTimeBeforeExploring();
      }
      else
      {
         recoverTime = new DoubleYoVariable(prefix + "RecoverTime", registry);
         timeBeforeExploring = new DoubleYoVariable(prefix + "TimeBeforeExploring", registry);
      }

      YoGraphicsListRegistry graphicsListRegistry = footControlHelper.getHighLevelHumanoidControllerToolbox().getYoGraphicsListRegistry();
      frameViz = new YoGraphicReferenceFrame(controlFrame, registry, 0.2);
      if (graphicsListRegistry != null)
         graphicsListRegistry.registerYoGraphic(prefix + getClass().getSimpleName(), frameViz);
   }

   @Override
   public void doTransitionIntoAction()
   {
      super.doTransitionIntoAction();
      FrameVector fullyConstrainedNormalContactVector = footControlHelper.getFullyConstrainedNormalContactVector();
      controllerToolbox.setFootContactStateNormalContactVector(robotSide, fullyConstrainedNormalContactVector);

      for (int i = 0; i < dofs; i++)
         isDirectionFeedbackControlled[i] = false;

      footBarelyLoaded.set(false);
      copOnEdge.set(false);
      updateHoldPositionSetpoints();
   }

   @Override
   public void doTransitionOutOfAction()
   {
      super.doTransitionOutOfAction();
      footBarelyLoaded.set(false);
      copOnEdge.set(false);
      frameViz.hide();
      explorationHelper.stopExploring();
   }

   @Override
   public void doSpecificAction()
   {
      // handle partial foothold detection
      boolean recoverTimeHasPassed = getTimeInCurrentState() > recoverTime.getDoubleValue();
      boolean contactStateHasChanged = false;
      if (partialFootholdControlModule != null && recoverTimeHasPassed)
      {
         footSwitch.computeAndPackCoP(cop);
         controllerToolbox.getDesiredCenterOfPressure(contactableFoot, desiredCoP);
         partialFootholdControlModule.compute(desiredCoP, cop);
         YoPlaneContactState contactState = controllerToolbox.getFootContactState(robotSide);
         contactStateHasChanged = partialFootholdControlModule.applyShrunkPolygon(contactState);
         if (contactStateHasChanged)
            contactState.notifyContactStateHasChanged();
      }

      // foothold exploration
      boolean timeBeforeExploringHasPassed = getTimeInCurrentState() > timeBeforeExploring.getDoubleValue();
      if (requestFootholdExploration.getBooleanValue() && timeBeforeExploringHasPassed)
      {
         explorationHelper.startExploring();
         requestFootholdExploration.set(false);
      }
      if (partialFootholdControlModule != null && !timeBeforeExploringHasPassed)
         partialFootholdControlModule.clearCoPGrid();
      explorationHelper.compute(getTimeInCurrentState(), contactStateHasChanged);

      // toe contact point loading //// TODO: 6/5/17
      double currentTime = getTimeInCurrentState();
      if (rampUpAllowableToeLoadAfterContact && currentTime < toeLoadingDuration.getDoubleValue())
      {
         computeFootPolygon();

         double maxContactPointX = footPolygon.getMaxX();
         double minContactPointX = footPolygon.getMinX();

         double phaseInLoading = currentTime / toeLoadingDuration.getDoubleValue();
         double leadingToeMagnitude = InterpolationTools.linearInterpolate(0.0, fullyLoadedMagnitude.getDoubleValue(), phaseInLoading);
         YoPlaneContactState planeContactState = controllerToolbox.getFootContactState(robotSide);

         for (int i = 0; i < planeContactState.getTotalNumberOfContactPoints(); i++)
         {
            YoContactPoint contactPoint = planeContactState.getContactPoints().get(i);
            contactPoint.getPosition(tempPoint);
            double percentAlongFoot = (tempPoint.getX() - minContactPointX) / (maxContactPointX - minContactPointX);
            double contactPointMagnitude = InterpolationTools.linearInterpolate(fullyLoadedMagnitude.getDoubleValue(), leadingToeMagnitude, percentAlongFoot);

            planeContactState.setMaxContactPointNormalForce(contactPoint, contactPointMagnitude);
         }
      }
      else
      {
         YoPlaneContactState planeContactState = controllerToolbox.getFootContactState(robotSide);
         for (int i = 0; i < planeContactState.getTotalNumberOfContactPoints(); i++)
         {
            YoContactPoint contactPoint = planeContactState.getContactPoints().get(i);
            planeContactState.setMaxContactPointNormalForce(contactPoint, Double.POSITIVE_INFINITY);
         }
      }

      // determine foot state
      copOnEdge.set(footControlHelper.isCoPOnEdge());
      footBarelyLoaded.set(footSwitch.computeFootLoadPercentage() < footLoadThreshold.getDoubleValue());

      if (assumeCopOnEdge.getBooleanValue())
         copOnEdge.set(true);
      if (assumeFootBarelyLoaded.getBooleanValue())
         footBarelyLoaded.set(true);
      if (neverHoldRotation.getBooleanValue())
         copOnEdge.set(false);

      updateHoldPositionSetpoints();

      // update the control frame
      footSwitch.computeAndPackCoP(cop2d);
      if (cop2d.containsNaN())
         cop2d.setToZero(contactableFoot.getSoleFrame());
      framePosition.setXYIncludingFrame(cop2d);
      frameOrientation.setToZero(contactableFoot.getSoleFrame());
      controlFrame.setPoseAndUpdate(framePosition, frameOrientation);

      // assemble acceleration command
      ReferenceFrame bodyFixedFrame = contactableFoot.getRigidBody().getBodyFixedFrame();
      footAcceleration.setToZero(bodyFixedFrame, rootBody.getBodyFixedFrame(), controlFrame);
      footAcceleration.changeBodyFrameNoRelativeAcceleration(bodyFixedFrame);
      spatialAccelerationCommand.setSpatialAcceleration(controlFrame, footAcceleration);

      // assemble feedback command
      bodyFixedControlledPose.setToZero(controlFrame);
      bodyFixedControlledPose.changeFrame(contactableFoot.getRigidBody().getBodyFixedFrame());
      desiredCopPosition.setXYIncludingFrame(cop2d);
      desiredCopPosition.setIncludingFrame(desiredSoleFrame, desiredCopPosition.getPoint());
      desiredCopPosition.changeFrame(worldFrame);
      spatialFeedbackControlCommand.setControlFrameFixedInEndEffector(bodyFixedControlledPose);
      spatialFeedbackControlCommand.set(desiredCopPosition, desiredLinearVelocity, desiredLinearAcceleration);
      spatialFeedbackControlCommand.set(desiredOrientation, desiredAngularVelocity, desiredAngularAcceleration);

      // set selection matrices
      accelerationSelectionMatrix.resetSelection();
      feedbackSelectionMatrix.resetSelection();

      for (int i = 0; i < dofs; i++)
         isDirectionFeedbackControlled[i] = false;

      if (footBarelyLoaded.getBooleanValue())
      {
         isDirectionFeedbackControlled[3] = true; // control x position
         isDirectionFeedbackControlled[4] = true; // control y position
         isDirectionFeedbackControlled[2] = true; // control z orientation
      }
      if (copOnEdge.getBooleanValue())
      {
         isDirectionFeedbackControlled[0] = true; // control x orientation
         isDirectionFeedbackControlled[1] = true; // control y orientation
      }

      for (int i = dofs-1; i >= 0; i--)
      {
         if (isDirectionFeedbackControlled[i])
            accelerationSelectionMatrix.selectAxis(i, false);
         else
            feedbackSelectionMatrix.selectAxis(i, false);
      }

      spatialAccelerationCommand.setSelectionMatrix(accelerationSelectionMatrix);
      spatialFeedbackControlCommand.setSelectionMatrix(feedbackSelectionMatrix);

      // update visualization
      frameViz.setToReferenceFrame(controlFrame);
   }


   private void computeFootPolygon()
   {
      ReferenceFrame soleFrame = contactableFoot.getSoleFrame();
      footPolygon.clear(soleFrame);
      for (int i = 0; i < contactableFoot.getTotalNumberOfContactPoints(); i++)
         footPolygon.addVertex(contactableFoot.getContactPoints2d().get(i));
      footPolygon.update();
   }

   private void updateHoldPositionSetpoints()
   {
      footPosition.setToZero(contactableFoot.getSoleFrame());
      footOrientation.setToZero(contactableFoot.getSoleFrame());
      footPosition.changeFrame(worldFrame);
      footOrientation.changeFrame(worldFrame);

      desiredPosition.checkReferenceFrameMatch(footPosition);
      desiredOrientation.checkReferenceFrameMatch(footOrientation);

      // The z component is always updated as it is never held in place
      if (footBarelyLoaded.getBooleanValue() && copOnEdge.getBooleanValue()) // => Holding X-Y-Yaw-Components (cuz barely loaded) and Pitch-Roll-Components (cuz CoP on edge)
      { // Only the z component is not held
         desiredPosition.setZ(footPosition.getZ());
      }
      else if (footBarelyLoaded.getBooleanValue()) // => Holding X-Y-Yaw-Components (cuz barely loaded)
      { // Update pitch and roll for when the CoP will get on the edge, and z as always
         desiredPosition.setZ(footPosition.getZ());
         desiredOrientation.setYawPitchRoll(desiredOrientation.getYaw(), footOrientation.getPitch(), footOrientation.getRoll());
      }
      else if (copOnEdge.getBooleanValue()) // => Holding Pitch-Roll-Components (cuz CoP on edge)
      { // Update X-Y-Z and yaw for next time the foot will be barely loaded
         desiredPosition.set(footPosition);
         desiredOrientation.setYawPitchRoll(footOrientation.getYaw(), desiredOrientation.getPitch(), desiredOrientation.getRoll());
      }
      else // Not holding anything
      { // Update the full pose.
         desiredPosition.set(footPosition);
         desiredOrientation.set(footOrientation);
      }

      if (holdFootOrientationFlat.getBooleanValue())
         desiredOrientation.setYawPitchRoll(desiredOrientation.getYaw(), 0.0, 0.0);

      desiredSoleFrame.setPoseAndUpdate(desiredPosition, desiredOrientation);
   }

   public void requestFootholdExploration()
   {
      requestFootholdExploration.set(true);
      if (partialFootholdControlModule != null)
         partialFootholdControlModule.turnOnCropping();
   }

   @Override
   public InverseDynamicsCommand<?> getInverseDynamicsCommand()
   {
      inverseDynamicsCommandsList.clear();
      inverseDynamicsCommandsList.addCommand(spatialAccelerationCommand);
      inverseDynamicsCommandsList.addCommand(explorationHelper.getCommand());

      return inverseDynamicsCommandsList;
   }


   @Override
   public FeedbackControlCommand<?> getFeedbackControlCommand()
   {
      return spatialFeedbackControlCommand;
   }

   public void setWeight(double weight)
   {
      spatialAccelerationCommand.setWeight(weight);
      spatialFeedbackControlCommand.setWeightForSolver(weight);
   }

   public void setWeights(Vector3D angular, Vector3D linear)
   {
      spatialAccelerationCommand.setWeights(angular, linear);
      spatialFeedbackControlCommand.setWeightsForSolver(angular, linear);
   }

}
