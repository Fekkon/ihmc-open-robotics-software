package us.ihmc.communication.packets;

import java.util.ArrayList;
import java.util.List;

import controller_msgs.msg.dds.PlanarRegionMessage;
import controller_msgs.msg.dds.PlanarRegionsListMessage;
import us.ihmc.commons.PrintTools;
import us.ihmc.euclid.axisAngle.AxisAngle;
import us.ihmc.euclid.geometry.ConvexPolygon2D;
import us.ihmc.euclid.geometry.tools.EuclidGeometryTools;
import us.ihmc.euclid.transform.RigidBodyTransform;
import us.ihmc.euclid.tuple2D.Point2D;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.idl.IDLSequence.Object;
import us.ihmc.robotics.geometry.PlanarRegion;
import us.ihmc.robotics.geometry.PlanarRegionsList;

public class PlanarRegionMessageConverter
{
   public static PlanarRegionMessage convertToPlanarRegionMessage(PlanarRegion planarRegion)
   {
      PlanarRegionMessage message = new PlanarRegionMessage();
      message.setRegionId(planarRegion.getRegionId());
      planarRegion.getPointInRegion(message.getRegionOrigin());
      planarRegion.getNormal(message.getRegionNormal());

      message.setConcaveHullSize(planarRegion.getConcaveHullSize());
      message.setNumberOfConvexPolygons(planarRegion.getNumberOfConvexPolygons());

      Object<Point3D> vertexBuffer = message.getVertexBuffer();
      vertexBuffer.clear();

      for (int vertexIndex = 0; vertexIndex < planarRegion.getConcaveHullSize(); vertexIndex++)
      {
         vertexBuffer.add().set(planarRegion.getConcaveHullVertex(vertexIndex), 0.0);
      }

      for (int polygonIndex = 0; polygonIndex < planarRegion.getNumberOfConvexPolygons(); polygonIndex++)
      {
         ConvexPolygon2D convexPolygon = planarRegion.getConvexPolygon(polygonIndex);
         message.getConvexPolygonsSize().add(convexPolygon.getNumberOfVertices());

         for (int vertexIndex = 0; vertexIndex < convexPolygon.getNumberOfVertices(); vertexIndex++)
         {
            vertexBuffer.add().set(convexPolygon.getVertex(vertexIndex), 0.0);
         }
      }

      return message;
   }

   public static PlanarRegion convertToPlanarRegion(PlanarRegionMessage message)
   {
      RigidBodyTransform transformToWorld = new RigidBodyTransform();

      Vector3D regionOrigin = new Vector3D(message.getRegionOrigin());
      Vector3D regionNormal = new Vector3D(message.getRegionNormal());
      AxisAngle regionOrientation = EuclidGeometryTools.axisAngleFromZUpToVector3D(regionNormal);
      transformToWorld.set(regionOrientation, regionOrigin);

      Object<Point3D> vertexBuffer = message.getVertexBuffer();

      List<Point2D> concaveHullVertices = new ArrayList<>();
      int vertexIndex = 0;
      int upperBound = message.getConcaveHullSize();

      for (; vertexIndex < upperBound; vertexIndex++)
      {
         concaveHullVertices.add(new Point2D(vertexBuffer.get(vertexIndex)));
      }

      List<ConvexPolygon2D> convexPolygons = new ArrayList<>();

      for (int polygonIndex = 0; polygonIndex < message.getNumberOfConvexPolygons(); polygonIndex++)
      {
         upperBound += message.getConvexPolygonsSize().get(polygonIndex);
         ConvexPolygon2D convexPolygon = new ConvexPolygon2D();

         for (; vertexIndex < upperBound; vertexIndex++)
            convexPolygon.addVertex(vertexBuffer.get(vertexIndex));
         convexPolygon.update();
         convexPolygons.add(convexPolygon);
      }

      PlanarRegion planarRegion = new PlanarRegion(transformToWorld, concaveHullVertices.toArray(new Point2D[0]), convexPolygons);
      planarRegion.setRegionId(message.getRegionId());
      return planarRegion;
   }

   public static PlanarRegionsListMessage convertToPlanarRegionsListMessage(PlanarRegionsList planarRegionsList)
   {
      PlanarRegionsListMessage message = new PlanarRegionsListMessage();

      Object<Point3D> vertexBuffer = message.getVertexBuffer();

      vertexBuffer.clear();
      
      PrintTools.debug("Num regs: " + planarRegionsList.getNumberOfPlanarRegions());

      for (PlanarRegion planarRegion : planarRegionsList.getPlanarRegionsAsList())
      {
         planarRegion.getPointInRegion(message.getRegionOrigin().add());
         planarRegion.getNormal(message.getRegionNormal().add());
         message.getRegionId().add(planarRegion.getRegionId());

         message.getConcaveHullsSize().add(planarRegion.getConcaveHullSize());
         message.getNumberOfConvexPolygons().add(planarRegion.getNumberOfConvexPolygons());

         for (int vertexIndex = 0; vertexIndex < planarRegion.getConcaveHullSize(); vertexIndex++)
         {
            vertexBuffer.add().set(planarRegion.getConcaveHullVertex(vertexIndex), 0.0);
         }

         for (int polygonIndex = 0; polygonIndex < planarRegion.getNumberOfConvexPolygons(); polygonIndex++)
         {
            ConvexPolygon2D convexPolygon = planarRegion.getConvexPolygon(polygonIndex);
            message.getConvexPolygonsSize().add(convexPolygon.getNumberOfVertices());

            for (int vertexIndex = 0; vertexIndex < convexPolygon.getNumberOfVertices(); vertexIndex++)
            {
               vertexBuffer.add().set(convexPolygon.getVertex(vertexIndex), 0.0);
            }
         }
      }

      return message;
   }

   public static PlanarRegionsList convertToPlanarRegionsList(PlanarRegionsListMessage message)
   {
      int vertexIndex = 0;
      Object<Vector3D> normals = message.getRegionNormal();
      Object<Point3D> origins = message.getRegionOrigin();

      Object<Point3D> vertexBuffer = message.getVertexBuffer();

      List<PlanarRegion> planarRegions = new ArrayList<>();

      int upperBound = 0;
      int convexPolygonIndexStart = 0;

      for (int regionIndex = 0; regionIndex < message.getConcaveHullsSize().size(); regionIndex++)
      {
         RigidBodyTransform transformToWorld = new RigidBodyTransform();
         AxisAngle regionOrientation = EuclidGeometryTools.axisAngleFromZUpToVector3D(normals.get(regionIndex));
         transformToWorld.set(regionOrientation, origins.get(regionIndex));

         upperBound += message.getConcaveHullsSize().get(regionIndex);
         List<Point2D> concaveHullVertices = new ArrayList<>();

         for (; vertexIndex < upperBound; vertexIndex++)
         {
            concaveHullVertices.add(new Point2D(vertexBuffer.get(vertexIndex)));
         }

         List<ConvexPolygon2D> convexPolygons = new ArrayList<>();
         int polygonIndex = 0;
         for (; polygonIndex < message.getNumberOfConvexPolygons().get(regionIndex); polygonIndex++)
         {
            upperBound += message.getConvexPolygonsSize().get(convexPolygonIndexStart + polygonIndex);
            ConvexPolygon2D convexPolygon = new ConvexPolygon2D();

            for (; vertexIndex < upperBound; vertexIndex++)
               convexPolygon.addVertex(vertexBuffer.get(vertexIndex));
            convexPolygon.update();
            convexPolygons.add(convexPolygon);
         }
         convexPolygonIndexStart += polygonIndex;

         PlanarRegion planarRegion = new PlanarRegion(transformToWorld, concaveHullVertices.toArray(new Point2D[0]), convexPolygons);
         planarRegion.setRegionId(message.getRegionId().get(regionIndex));
         planarRegions.add(planarRegion);
      }

      return new PlanarRegionsList(planarRegions);
   }

   public static PlanarRegionsListMessage createPlanarRegionsListMessage(List<PlanarRegionMessage> planarRegions)
   {
      PlanarRegionsListMessage message = new PlanarRegionsListMessage();
      for (PlanarRegionMessage planarRegionMessage : planarRegions)
      {
         message.getRegionId().add(planarRegionMessage.getRegionId());
         message.getRegionOrigin().add().set(planarRegionMessage.getRegionOrigin());
         message.getRegionNormal().add().set(planarRegionMessage.getRegionNormal());
         message.getConcaveHullsSize().add(planarRegionMessage.getConcaveHullSize());
         message.getNumberOfConvexPolygons().add(planarRegionMessage.getNumberOfConvexPolygons());
         message.getConvexPolygonsSize().addAll(planarRegionMessage.getConvexPolygonsSize());
         for (int i = 0; i < planarRegionMessage.getVertexBuffer().size(); i++)
            message.getVertexBuffer().add().set(planarRegionMessage.getVertexBuffer().get(i));
      }
      return message;
   }
}
