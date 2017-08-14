package us.ihmc.sensorProcessing.stateEstimation.processModelElements;


import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import us.ihmc.controlFlow.ControlFlowOutputPort;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.robotics.geometry.FramePoint3D;
import us.ihmc.robotics.geometry.FrameVector;
import us.ihmc.robotics.linearAlgebra.MatrixTools;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;
import us.ihmc.sensorProcessing.stateEstimation.TimeDomain;

public class CenterOfMassPositionProcessModelElement extends AbstractProcessModelElement
{
   private static final int SIZE = 3;
   private final ControlFlowOutputPort<FramePoint3D> centerOfMassPositionPort;
   private final ControlFlowOutputPort<FrameVector> centerOfMassVelocityPort;

   // temp stuff
   private final FramePoint3D centerOfMassPosition;
   private final FrameVector centerOfMassPositionDelta;
   private final ReferenceFrame worldFrame = ReferenceFrame.getWorldFrame();

   public CenterOfMassPositionProcessModelElement(ControlFlowOutputPort<FramePoint3D> centerOfMassPositionPort,
           ControlFlowOutputPort<FrameVector> centerOfMassVelocity, String name, YoVariableRegistry registry)
   {
      super(centerOfMassPositionPort, TimeDomain.CONTINUOUS, false, SIZE, name, registry);
      this.centerOfMassPositionPort = centerOfMassPositionPort;
      this.centerOfMassVelocityPort = centerOfMassVelocity;
      this.centerOfMassPosition = new FramePoint3D(worldFrame);
      this.centerOfMassPositionDelta = new FrameVector(ReferenceFrame.getWorldFrame());

      stateMatrixBlocks.put(centerOfMassVelocityPort, new DenseMatrix64F(SIZE, SIZE));
      computeCenterOfMassVelocityStateMatrixBlock();
   }

   private void computeCenterOfMassVelocityStateMatrixBlock()
   {
      CommonOps.setIdentity(stateMatrixBlocks.get(centerOfMassVelocityPort));
   }

   public void computeMatrixBlocks()
   {
      // empty
   }

   public void propagateState(double dt)
   {
      FrameVector centerOfMassVelocity = centerOfMassVelocityPort.getData();
      centerOfMassVelocity.changeFrame(worldFrame);
      centerOfMassPositionDelta.set(centerOfMassVelocity);
      centerOfMassPositionDelta.scale(dt);

      updateCenterOfMassVelocity(centerOfMassPositionDelta);
   }

   public void correctState(DenseMatrix64F correction)
   {
      MatrixTools.extractTuple3dFromEJMLVector(centerOfMassPositionDelta.getVector(), correction, 0);
      updateCenterOfMassVelocity(centerOfMassPositionDelta);
   }

   private void updateCenterOfMassVelocity(FrameVector centerOfMassPositionDelta)
   {
      centerOfMassPosition.set(centerOfMassPositionPort.getData());
      centerOfMassPosition.add(centerOfMassPositionDelta);
      centerOfMassPositionPort.setData(centerOfMassPosition);
   }
}
