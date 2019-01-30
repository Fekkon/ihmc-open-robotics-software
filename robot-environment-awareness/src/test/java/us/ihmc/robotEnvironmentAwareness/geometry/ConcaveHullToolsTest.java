package us.ihmc.robotEnvironmentAwareness.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.javafx.application.PlatformImpl;
import com.vividsolutions.jts.geom.MultiPoint;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import us.ihmc.commons.MathTools;
import us.ihmc.commons.thread.ThreadTools;
import us.ihmc.euclid.Axis;
import us.ihmc.euclid.geometry.ConvexPolygon2D;
import us.ihmc.euclid.geometry.Line2D;
import us.ihmc.euclid.geometry.Line3D;
import us.ihmc.euclid.geometry.LineSegment2D;
import us.ihmc.euclid.geometry.LineSegment3D;
import us.ihmc.euclid.geometry.interfaces.LineSegment2DReadOnly;
import us.ihmc.euclid.geometry.interfaces.Vertex2DSupplier;
import us.ihmc.euclid.geometry.interfaces.Vertex3DSupplier;
import us.ihmc.euclid.geometry.tools.EuclidGeometryRandomTools;
import us.ihmc.euclid.tools.EuclidCoreRandomTools;
import us.ihmc.euclid.transform.RigidBodyTransform;
import us.ihmc.euclid.tuple2D.Point2D;
import us.ihmc.euclid.tuple2D.Vector2D;
import us.ihmc.euclid.tuple2D.interfaces.Point2DBasics;
import us.ihmc.euclid.tuple2D.interfaces.Point2DReadOnly;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.euclid.tuple3D.interfaces.Point3DBasics;
import us.ihmc.javaFXToolkit.messager.JavaFXMessager;
import us.ihmc.javaFXToolkit.messager.SharedMemoryJavaFXMessager;
import us.ihmc.messager.Messager;
import us.ihmc.messager.SharedMemoryMessager;
import us.ihmc.robotEnvironmentAwareness.planarRegion.PlanarRegionSegmentationRawData;
import us.ihmc.robotEnvironmentAwareness.polygonizer.Polygonizer;
import us.ihmc.robotEnvironmentAwareness.polygonizer.Polygonizer.Output;
import us.ihmc.robotEnvironmentAwareness.polygonizer.PolygonizerManager;
import us.ihmc.robotEnvironmentAwareness.polygonizer.PolygonizerVisualizerUI;

public class ConcaveHullToolsTest
{
	private static boolean VISUALIZE = false;

	private Messager messager;
	private MutableBoolean uiIsGoingDown = new MutableBoolean(false);

	private List<Point2D> pointcloud2D = null;
	private List<LineSegment2D> lineConstraints2D = null;
	private List<Point3D> pointcloud3D = null;
	private List<LineSegment3D> lineConstraints3D = null;

	public List<Point2D> getPointcloud2D()
	{
		return pointcloud2D;
	}

	public List<LineSegment2D> getLineConstraints2D()
	{
		return lineConstraints2D;
	}

	public List<Point3D> getPointcloud3D()
	{
		return pointcloud3D;
	}

	public List<LineSegment3D> getLineConstraints3D()
	{
		return lineConstraints3D;
	}

	public ConcaveHullToolsTest()
	{
		// A random pointCloud generated in Matlab.
		// This cloud is generated by subtracting a rectangular section of the original
		// cloud,
		// creating a large pocket on the right side. The resulting hull should look
		// somewhat like a crooked 'C'.

		pointcloud3D = new ArrayList<>();
		pointcloud3D.add(new Point3D(0.417022005, 0.326644902, 0));
		pointcloud3D.add(new Point3D(0.000114375, 0.885942099, 0));
		pointcloud3D.add(new Point3D(0.302332573, 0.35726976, 0));
		pointcloud3D.add(new Point3D(0.146755891, 0.908535151, 0));
		pointcloud3D.add(new Point3D(0.092338595, 0.623360116, 0));
		pointcloud3D.add(new Point3D(0.186260211, 0.015821243, 0));
		pointcloud3D.add(new Point3D(0.345560727, 0.929437234, 0));
		pointcloud3D.add(new Point3D(0.396767474, 0.690896918, 0));
		pointcloud3D.add(new Point3D(0.538816734, 0.99732285, 0));
		pointcloud3D.add(new Point3D(0.419194514, 0.172340508, 0));
		pointcloud3D.add(new Point3D(0.6852195, 0.13713575, 0));
		pointcloud3D.add(new Point3D(0.20445225, 0.932595463, 0));
		pointcloud3D.add(new Point3D(0.027387593, 0.066000173, 0));
		pointcloud3D.add(new Point3D(0.417304802, 0.753876188, 0));
		pointcloud3D.add(new Point3D(0.558689828, 0.923024536, 0));
		pointcloud3D.add(new Point3D(0.140386939, 0.711524759, 0));
		pointcloud3D.add(new Point3D(0.198101489, 0.124270962, 0));
		pointcloud3D.add(new Point3D(0.800744569, 0.019880134, 0));
		pointcloud3D.add(new Point3D(0.968261576, 0.026210987, 0));
		pointcloud3D.add(new Point3D(0.313424178, 0.028306488, 0));
		pointcloud3D.add(new Point3D(0.876389152, 0.860027949, 0));
		pointcloud3D.add(new Point3D(0.085044211, 0.552821979, 0));
		pointcloud3D.add(new Point3D(0.039054783, 0.842030892, 0));
		pointcloud3D.add(new Point3D(0.16983042, 0.124173315, 0));
		pointcloud3D.add(new Point3D(0.098346834, 0.585759271, 0));
		pointcloud3D.add(new Point3D(0.421107625, 0.969595748, 0));
		pointcloud3D.add(new Point3D(0.533165285, 0.018647289, 0));
		pointcloud3D.add(new Point3D(0.691877114, 0.800632673, 0));
		pointcloud3D.add(new Point3D(0.315515631, 0.232974274, 0));
		pointcloud3D.add(new Point3D(0.686500928, 0.807105196, 0));
		pointcloud3D.add(new Point3D(0.018288277, 0.863541855, 0));
		pointcloud3D.add(new Point3D(0.748165654, 0.136455226, 0));
		pointcloud3D.add(new Point3D(0.280443992, 0.05991769, 0));
		pointcloud3D.add(new Point3D(0.789279328, 0.121343456, 0));
		pointcloud3D.add(new Point3D(0.103226007, 0.044551879, 0));
		pointcloud3D.add(new Point3D(0.447893526, 0.107494129, 0));
		pointcloud3D.add(new Point3D(0.293614148, 0.71298898, 0));
		pointcloud3D.add(new Point3D(0.287775339, 0.559716982, 0));
		pointcloud3D.add(new Point3D(0.130028572, 0.01255598, 0));
		pointcloud3D.add(new Point3D(0.019366958, 0.07197428, 0));
		pointcloud3D.add(new Point3D(0.678835533, 0.96727633, 0));
		pointcloud3D.add(new Point3D(0.211628116, 0.568100462, 0));
		pointcloud3D.add(new Point3D(0.265546659, 0.203293235, 0));
		pointcloud3D.add(new Point3D(0.491573159, 0.252325745, 0));
		pointcloud3D.add(new Point3D(0.053362545, 0.743825854, 0));
		pointcloud3D.add(new Point3D(0.574117605, 0.195429481, 0));
		pointcloud3D.add(new Point3D(0.146728575, 0.581358927, 0));
		pointcloud3D.add(new Point3D(0.589305537, 0.970019989, 0));
		pointcloud3D.add(new Point3D(0.69975836, 0.846828801, 0));
		pointcloud3D.add(new Point3D(0.102334429, 0.239847759, 0));
		pointcloud3D.add(new Point3D(0.414055988, 0.493769714, 0));
		pointcloud3D.add(new Point3D(0.41417927, 0.8289809, 0));
		pointcloud3D.add(new Point3D(0.049953459, 0.156791395, 0));
		pointcloud3D.add(new Point3D(0.535896406, 0.018576202, 0));
		pointcloud3D.add(new Point3D(0.663794645, 0.070022144, 0));
		pointcloud3D.add(new Point3D(0.137474704, 0.988616154, 0));
		pointcloud3D.add(new Point3D(0.139276347, 0.579745219, 0));
		pointcloud3D.add(new Point3D(0.397676837, 0.550948219, 0));
		pointcloud3D.add(new Point3D(0.165354197, 0.745334431, 0));
		pointcloud3D.add(new Point3D(0.34776586, 0.264919558, 0));
		pointcloud3D.add(new Point3D(0.750812103, 0.066334834, 0));
		pointcloud3D.add(new Point3D(0.348898342, 0.066536481, 0));
		pointcloud3D.add(new Point3D(0.269927892, 0.260315099, 0));
		pointcloud3D.add(new Point3D(0.895886218, 0.804754564, 0));
		pointcloud3D.add(new Point3D(0.42809119, 0.193434283, 0));
		pointcloud3D.add(new Point3D(0.62169572, 0.92480797, 0));
		pointcloud3D.add(new Point3D(0.114745973, 0.26329677, 0));
		pointcloud3D.add(new Point3D(0.949489259, 0.065961091, 0));
		pointcloud3D.add(new Point3D(0.449912133, 0.735065963, 0));
		pointcloud3D.add(new Point3D(0.408136803, 0.907815853, 0));
		pointcloud3D.add(new Point3D(0.23702698, 0.931972069, 0));
		pointcloud3D.add(new Point3D(0.903379521, 0.013951573, 0));
		pointcloud3D.add(new Point3D(0.002870327, 0.616778357, 0));
		pointcloud3D.add(new Point3D(0.617144914, 0.949016321, 0));

		pointcloud3D.add(new Point3D(0.5, -0.1, 0.0));
		pointcloud3D.add(new Point3D(1.4, 1.0, 0.0));
		pointcloud3D.add(new Point3D(1.5, 1.0, 0.0));
		pointcloud3D.add(new Point3D(1.4, 0.10, 0.0));
		// System.out.println("\npointcloud: " + pointcloud.toString());

		lineConstraints3D = new ArrayList<>();
		lineConstraints3D.add(new LineSegment3D(0.0, -0.5, 0.0, 0.0, 0.5, 0.0));
		lineConstraints3D.add(new LineSegment3D(2.0, -0.5, 0.0, 2.0, 0.5, 0.0));
		lineConstraints3D.add(new LineSegment3D(0.0, 0.5, 0.0, 2.0, 0.5, 0.0));
		lineConstraints3D.add(new LineSegment3D(0.0, -0.5, 0.0, 2.0, -0.5, 0.0));
		// System.out.println("\nlineConstraints: " + lineConstraints.toString());

		pointcloud2D = new ArrayList<>();
		for (Point3D i : pointcloud3D)
		{
			pointcloud2D.add(new Point2D(i.getX(), i.getY()));
		}

		lineConstraints2D = new ArrayList<>();
		for (LineSegment3D i : lineConstraints3D)
		{
			lineConstraints2D.add(new LineSegment2D(i.getFirstEndpointX(), i.getFirstEndpointY(), i.getSecondEndpointX(), i.getSecondEndpointY()));
		}

	}

	@Test(timeout = 30000)
	public void testPointCloudWithSurroundingLineConstraints() throws Exception
	{
		MultiPoint multiPoint = SimpleConcaveHullFactory.filterAndCreateMultiPoint(getPointcloud2D(), getLineConstraints2D(), .001);

		PlanarRegionSegmentationRawData data = new PlanarRegionSegmentationRawData(1, Axis.Z, new Point3D(), pointcloud3D);
		data.addIntersections(lineConstraints3D);
		// System.out.println("\ndata: " + data.toString());

		ConcaveHullFactoryParameters parameters = new ConcaveHullFactoryParameters();
		parameters.setTriangulationTolerance(1.0e-5);
		parameters.setEdgeLengthThreshold(0.15);
		// System.out.println("\nparameters: " + parameters.toString());

		messager.submitMessage(Polygonizer.PolygonizerParameters, parameters);

		AtomicReference<List<Output>> output = messager.createInput(Polygonizer.PolygonizerOutput, null);
		// System.out.println("output: " + output.get().toString());

		messager.submitMessage(PolygonizerManager.PlanarRegionSemgentationData, Collections.singletonList(data));

		while (output.get() == null)
			ThreadTools.sleep(100);

		ConcaveHullCollection concaveHullCollection = output.get().get(0).getConcaveHullFactoryResult().getConcaveHullCollection();
		// System.out.println("\nconcaveHullCollection: " +
		// concaveHullCollection.getConcaveHulls());

		int numberOfConcaveHulls = concaveHullCollection.getNumberOfConcaveHulls();

		System.out.printf("numberOfConcaveHulls = %d ", numberOfConcaveHulls);

		assertEquals(numberOfConcaveHulls, 2);

		messager = new SharedMemoryMessager(PolygonizerVisualizerUI.getMessagerAPI());
		messager.startMessager();
		new PolygonizerManager(messager);
	}

	@Before
	public void setup() throws Exception
	{
		uiIsGoingDown.setFalse();

		if (VISUALIZE)
		{
			SharedMemoryJavaFXMessager jfxMessager = new SharedMemoryJavaFXMessager(PolygonizerVisualizerUI.getMessagerAPI());
			messager = jfxMessager;
			createVisualizer(jfxMessager);
		}
		else
		{
			messager = new SharedMemoryMessager(PolygonizerVisualizerUI.getMessagerAPI());
			messager.startMessager();
			new PolygonizerManager(messager);
		}
	}

	@SuppressWarnings("restriction")
	private void createVisualizer(JavaFXMessager messager)
	{
		AtomicReference<PolygonizerVisualizerUI> ui = new AtomicReference<>(null);

		PlatformImpl.startup(() -> {
			try
			{
				Stage primaryStage = new Stage();
				primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> uiIsGoingDown.setTrue());

				ui.set(new PolygonizerVisualizerUI(messager, primaryStage));
				ui.get().show();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});

		while (ui.get() == null)
			ThreadTools.sleep(200);
	}

	@After
	public void tearDown()
	{
		if (VISUALIZE)
		{
			while (!uiIsGoingDown.booleanValue())
				ThreadTools.sleep(100);
		}
	}

}
