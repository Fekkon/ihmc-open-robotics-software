package us.ihmc.geometry.polytope.DCELPolytope.Frame;

import us.ihmc.euclid.referenceFrame.FramePoint3D;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.referenceFrame.interfaces.ReferenceFrameHolder;
import us.ihmc.geometry.polytope.DCELPolytope.ExtendedConvexPolytope;
import us.ihmc.geometry.polytope.DCELPolytope.Basics.ConvexPolytopeBasics;
import us.ihmc.geometry.polytope.DCELPolytope.Providers.ConvexPolytopeFaceProvider;
import us.ihmc.geometry.polytope.DCELPolytope.Providers.FrameConvexPolytopeFaceBuilder;
import us.ihmc.geometry.polytope.DCELPolytope.Providers.FramePolytopeVertexBuilder;
import us.ihmc.geometry.polytope.DCELPolytope.Providers.PolytopeVertexProvider;

public class FrameConvexPolytope extends ConvexPolytopeBasics<FramePolytopeVertex, FramePolytopeHalfEdge, FrameConvexPolytopeFace, FrameConvexPolytope, FrameSimplex> implements ReferenceFrameHolder
{
   private final ReferenceFrame referenceFrame;
   private final FrameConvexPolytopeFaceBuilder faceBuilder = new FrameConvexPolytopeFaceBuilder(this);
   private final FramePolytopeVertexBuilder vertexBuilder = new FramePolytopeVertexBuilder(this);

   public FrameConvexPolytope()
   {
      this.referenceFrame = ReferenceFrame.getWorldFrame();
   }
   
   public FrameConvexPolytope(ReferenceFrame referenceFrame)
   {
      this.referenceFrame = referenceFrame;
   }
   
   public FrameConvexPolytope(ReferenceFrame referenceFrame, ExtendedConvexPolytope polytope)
   {
      this(referenceFrame);
      throw new RuntimeException("Unimplemented exception");
   }
   
   @Override
   public ReferenceFrame getReferenceFrame()
   {
      return referenceFrame;
   }

   @Override
   protected PolytopeVertexProvider<FramePolytopeVertex, FramePolytopeHalfEdge, FrameConvexPolytopeFace, FrameSimplex> getVertexProvider()
   {
      return vertexBuilder;
   }

   @Override
   protected ConvexPolytopeFaceProvider<FramePolytopeVertex, FramePolytopeHalfEdge, FrameConvexPolytopeFace, FrameSimplex> getConvexFaceProvider()
   {
      return faceBuilder;
   }
   
   @Override
   public void addVertex(FramePolytopeVertex vertexToAdd, double epsilon)
   {
      checkReferenceFrameMatch(vertexToAdd);
      super.addVertex(vertexToAdd, epsilon);
   }
   
   public void addVertex(FramePoint3D verteToAdd, double epsilon)
   {
      checkReferenceFrameMatch(verteToAdd);
      super.addVertex(vertexBuilder.getVertex(verteToAdd), epsilon);
   }
   
   @Override
   public String toString()
   {
      return super.toString() + " - " + referenceFrame.toString();
   }
}