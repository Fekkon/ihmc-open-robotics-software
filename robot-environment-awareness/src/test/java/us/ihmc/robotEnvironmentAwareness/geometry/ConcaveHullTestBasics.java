package us.ihmc.robotEnvironmentAwareness.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sun.javafx.application.PlatformImpl;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import us.ihmc.commons.MutationTestFacilitator;
import us.ihmc.commons.PrintTools;
import us.ihmc.commons.thread.ThreadTools;
import us.ihmc.euclid.geometry.LineSegment2D;
import us.ihmc.euclid.geometry.LineSegment3D;
import us.ihmc.euclid.tuple2D.Point2D;
import us.ihmc.euclid.tuple3D.Point3D;
import us.ihmc.javaFXToolkit.messager.JavaFXMessager;
import us.ihmc.javaFXToolkit.messager.SharedMemoryJavaFXMessager;
import us.ihmc.messager.Messager;
import us.ihmc.messager.SharedMemoryMessager;
import us.ihmc.robotEnvironmentAwareness.polygonizer.PolygonizerManager;
import us.ihmc.robotEnvironmentAwareness.polygonizer.PolygonizerVisualizerUI;
import us.ihmc.robotEnvironmentAwareness.ui.io.PlanarRegionDataImporter;
import us.ihmc.robotics.geometry.PlanarRegionsList;

public class ConcaveHullTestBasics
{
	protected static final int ITERATIONS = 10000;
	protected static boolean VISUALIZE = false;
	protected static final double EPS = 1.0e-5;

	protected Messager messager;
	protected MutableBoolean uiIsGoingDown = new MutableBoolean(false);

	protected static List<Point2D> pointcloud2D = null;
	protected static List<LineSegment2D> lineConstraints2D = null;
	protected static List<Point3D> pointcloud3D = null;
	protected static List<LineSegment3D> lineConstraints3D = null;

	private static final boolean VERBOSE = true;
	private Window ownerWindow;

	private PlanarRegionsList loadedPlanarRegions = null;

	public void attachMessager(Messager messager)
	{
		this.messager = messager;
	}

	public void setMainWindow(Window ownerWindow)
	{
		this.ownerWindow = ownerWindow;
	}

	@SuppressWarnings("deprecation")
	public void loadPlanarRegions()
	{
		loadedPlanarRegions = PlanarRegionDataImporter.importUsingFileChooser(ownerWindow);

		if (loadedPlanarRegions != null)
		{
			if (VERBOSE)
				PrintTools.info(this, "Loaded planar regions, broadcasting data.");
			//			messager.submitMessage(FootstepPlannerMessagerAPI.GlobalResetTopic, true);
			//			messager.submitMessage(FootstepPlannerMessagerAPI.PlanarRegionDataTopic, loadedPlanarRegions);
		}
		else
		{
			if (VERBOSE)
				PrintTools.info(this, "Failed to load planar regions.");
		}
	}

	public static List<Point2D> getPointcloud2D()
	{
		return pointcloud2D;
	}

	public static List<LineSegment2D> getLineConstraints2D()
	{
		return lineConstraints2D;
	}

	public static List<Point3D> getPointcloud3D()
	{
		return pointcloud3D;
	}

	public static List<LineSegment3D> getLineConstraints3D()
	{
		return lineConstraints3D;
	}

	public boolean initializeBaseClass() 
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

		lineConstraints3D = new ArrayList<>();
		lineConstraints3D.add(new LineSegment3D(0.0, -0.5, 0.0, 0.0, 0.5, 0.0));
		lineConstraints3D.add(new LineSegment3D(2.0, -0.5, 0.0, 2.0, 0.5, 0.0));
		lineConstraints3D.add(new LineSegment3D(0.0, 0.5, 0.0, 2.0, 0.5, 0.0));
		lineConstraints3D.add(new LineSegment3D(0.0, -0.5, 0.0, 2.0, -0.5, 0.0));
		// System.out.println("ConcaveHullToolsTest: lineConstraints: " + lineConstraints.toString());

		pointcloud2D = new ArrayList<>();
		for (Point3D i : pointcloud3D)
			pointcloud2D.add(new Point2D(i.getX(), i.getY()));

		lineConstraints2D = new ArrayList<>();
		for (LineSegment3D i : lineConstraints3D)
			lineConstraints2D.add(new LineSegment2D(i.getFirstEndpointX(), i.getFirstEndpointY(), i.getSecondEndpointX(), i.getSecondEndpointY()));

		sombreroInitialized = createSombrero(-5.0, 5.0, 51);  //createSombrero(-5, 5, 15);
		
		sombreroCollection = new ConcaveHullCollection();
		sombreroCollection.add(sombrero);
		sombreroHull = new ConcaveHull(sombrero);
		
		return true;
	}
	
	ConcaveHullCollection sombreroCollection = null;
	ConcaveHull sombreroHull = null;

	protected static boolean sombreroInitialized = false;
	protected static List<Point2D> sombrero = null;
	protected static List<LineSegment2D> sombreroConstraints2D = null;
	protected static List<Point3D> sombrero3D = null;
	protected static List<LineSegment3D> sombreroConstraints3D = null;
	protected int max, min1, min2;

	
	public boolean createSombrero(double lb, double ub, int n)
	{
		max = -1;
		min1 = -1;
		min2 = -1;

		double[] x = linspace(lb, ub, n);
		double[] y = sombrero(x);
		int xlen = x.length;

		//for(int i=0; i<xlen; i++)
		//	System.out.printf("%3.5f ", y[i]);

		double highestY = Double.MIN_VALUE;
		double lowestY1 = Double.MAX_VALUE;
		double lowestY2 = Double.MAX_VALUE;
		Point3D leftLowerCornerPoint = new Point3D();
		Point3D rightLowerCornerPoint = new Point3D();

		sombrero3D = new ArrayList<>();
		sombrero3D.add(leftLowerCornerPoint);
		for (int i = 0; i < xlen; i++)
		{
			sombrero3D.add(new Point3D(x[i], y[i], 0));
		}
		sombrero3D.add(rightLowerCornerPoint);

		// locate the peak
		for (int i = 0; i < xlen; i++)
		{	
			if (y[i] > highestY)
			{
				highestY = y[i];
				max = i;
			}
		}
		
			
		// find the first minimum
		for (int i = 0; i < max; i++)
		{
			if (y[i] < lowestY1)
			{
				lowestY1 = y[i];
				min1 = i;
			}
		}
		
		// find the second minimum
		for (int i = max; i <xlen; i++)
		{
			if (y[i] < lowestY2)
			{
				lowestY2 = y[i];
				min2 = i;
			}
		}
//		System.out.printf("\n max, min1, min2  = %d %e %d %e %d %e " , max, y[max], min1, y[min1], min2, y[min2] );

		leftLowerCornerPoint.set(x[0], lowestY1 - 1, 0);
		rightLowerCornerPoint.set(x[xlen - 1], lowestY1 - 1, 0);

		sombrero = new ArrayList<>();
		for (Point3D i : sombrero3D)
			sombrero.add(new Point2D(i.getX(), i.getY()));				

		//System.out.println("\n"+sombrero2D);
		return true;
	}

	/*
	 * Some Matlab code to plot a mexicanHat...
	 * https://searchcode.com/codesearch/view/9537655/ function [psi,x] =
	 * mexihat(lb,ub,n) if (nargin < 3) usage('[psi,x] = mexihat(lb,ub,n)'); end
	 * if (n <= 0) error("n must be strictly positive"); end x =
	 * linspace(lb,ub,n); for i = 1:n psi(i) = (1-x(i)^2)*2/(sqrt(3)*pi^0.25) *
	 * exp(-x(i)^2/2); end end lb = -3; ub = 3; N = 20; [psi,xval] =
	 * mexihat(lb,ub,N); plot(xval,psi) title('Mexican Hat Wavelet');
	 */

	// Equivalent Java functions...	
	double[] linspace(double lb, double ub, int n)
	{
		if (n < 1)
			return null;

		double[] x = new double[n];
		for (int i=0; i < n; i++)
		{
			x[i] = lb + i * (ub - lb) / (n-1);
			//System.out.printf("\n %d %e ", i, x[i]);
		}
		return x;
	}

	double[] sombrero(double[] x)
	{
		int n = x.length;
		double[] psi = new double[n];
		for (int i = 0; i < n; i++)
		{
			psi[i] = (1 - Math.pow(x[i], 2)) * 2 / (Math.sqrt(3.0) * Math.pow(Math.PI, 0.25)) * Math.exp(-Math.pow(x[i], 2.0) / 2);
			//System.out.printf("\n %d %e ", i, psi[i]);
		}
		return psi;
	}

	@BeforeEach
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

	@AfterEach
	public void tearDown()
	{
		if (VISUALIZE)
		{
			while (!uiIsGoingDown.booleanValue())
				ThreadTools.sleep(100);
		}
	}

	
	public static void main(String[] args)
	{
		MutationTestFacilitator.facilitateMutationTestForClass(ConcaveHullTestBasics.class, ConcaveHullTestBasics.class);	
		
		ConcaveHullTestBasics basics = new ConcaveHullTestBasics();
		basics.createSombrero(-5.0, -5.0, 51);
	}


}
