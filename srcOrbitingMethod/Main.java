import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stiaan
 */
public class Main {

	private static final String directoryT1 = "Terashima1Polygons\\";
	private static final String directoryT2 = "Terashima2Polygons\\";
	/**
	 * @param args
	 *            the command line arguments
	 * @throws java.io.FileNotFoundException
	 */
	
	
	public static void main(String[] args) throws FileNotFoundException {

		DrawJavaFX drawTool = new DrawJavaFX();

		File convex1Data = new File("Convex1.txt");
		File convex2Data = new File("Convex2.txt");

		File concave1Data = new File("Concave1.txt");
		File concave2Data = new File("Concave2.txt");
		
		File rectangle1Data = new File("Rectangle1.txt");
		File block1Data = new File("Block1.txt");
		File block2Data = new File("Block2.txt");
		
		File puzzle1Data = new File("Puzzle4.txt");
		File puzzle2Data = new File("Puzzle2.txt");
		File puzzle3Data = new File("Puzzle3.txt");
		
		File sawtooth1Data = new File("Sawtooth1.txt");
		File sawtooth2Data = new File("Sawtooth2.txt");
		
		File clockwiseData = new File("clockwise.txt");
		
		File interlockingConc1Data = new File("interCav1.txt");
		File triangleData = new File("triangle1.txt");

		File interlockingConc2Data = new File("interCav2.txt");
		File interlockingConc3Data = new File("interCav3.txt");
		
		File holes1Data = new File("Holes1.txt");
		File holes2Data = new File("Holes2.txt");
		
		File mink1Data = new File("Mink1.txt");
		File mink2Data = new File("Mink2.txt");
		
		File simple1Data = new File("SimpleFig1.txt");
		File simple2Data = new File("SimpleFig2.txt");
		
		File folderT1 = new File(directoryT1);
		File[] listOfFilesT1 = folderT1.listFiles();
		
		File folderT2 = new File(directoryT2);
		File[] listOfFilesT2 = folderT2.listFiles();
		
		List<MultiPolygon> randomList = new ArrayList<>();
		List<NoFitPolygon> nfpList = new ArrayList<>();
		
		randomList.add(new MultiPolygon(convex1Data));
		randomList.add(new MultiPolygon(convex2Data));
		
		randomList.add(new MultiPolygon(concave1Data));
		randomList.add(new MultiPolygon(concave2Data));
		
		List<MultiPolygon> polygonsT1 = new ArrayList<>();
		List<MultiPolygon> polygonsT2 = new ArrayList<>();
		
//		System.out.println("T1 to multipolys");
//		int n = 0;
//		int numberOfPolys = 1000;
//		for(File polygonT1: listOfFilesT1){
//			if(n == numberOfPolys)break;
//			polygonsT1.add(new MultiPolygon(polygonT1));
//			n++;
//		}
//		n = 0;
//		System.out.println("T1 done");
//		System.out.println("T2 to multipolys");
//		for(File polygonT2: listOfFilesT2){
//			if(n == numberOfPolys)break;
//			polygonsT2.add(new MultiPolygon(polygonT2));
//			n++;
//		}
//		
//		System.out.println("T2 done");
		
		
		// mPolygon.printPolygonData();

		

		// ------------------------------------------------------------------------------
		// checking methods of calculations with coordinates
		// testCoordinateMethods();
		// testEdgeMethods();

		//------------------------------------------------------------------------------
		//testing clockwise and counterclockwise detection and fixing
		
		//MultiPolygon clockwisePolygon = new MultiPolygon(clockwiseData);
		//clockwisePolygon.printPolygonData();
		
		// -------------------------------------------------------------------------------------
		// orbiting method
		 
//		Orbiting.generateNFP(new MultiPolygon(multiPolyList.get(3)), new MultiPolygon(multiPolyList.get(0)));
		
		
		long startTime;
		long endTime;

		long duration;  //divide by 1000000 to get milliseconds.
		int scaleOfTime = 1000000;
		List<Long> durations = new ArrayList<>();
		int totalIts = 0;
		int numberOfIterations = 100;
		

		startTime = System.currentTimeMillis();

		int i = 0;
//		for(MultiPolygon stat : randomList){
//			int j = 0;
//			
//			for(MultiPolygon orb : randomList){
//				j++;
//				nfpList.add(Orbiting.generateNFP(new MultiPolygon(stat), new MultiPolygon(orb)));
//				totalIts++;
////				System.out.println("["+i+"]["+j+"]");
//			}
//			i++;
//		}
//				
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(rectangle1Data), new MultiPolygon(rectangle1Data)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(puzzle1Data), new MultiPolygon(puzzle2Data)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(puzzle3Data), new MultiPolygon(block1Data)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(sawtooth1Data), new MultiPolygon(sawtooth2Data)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(interlockingConc1Data), new MultiPolygon(triangleData)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(triangleData), new MultiPolygon(interlockingConc2Data)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(triangleData), new MultiPolygon(interlockingConc3Data)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(holes1Data), new MultiPolygon(triangleData)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(holes1Data), new MultiPolygon(holes2Data)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(interlockingConc3Data), new MultiPolygon(interlockingConc2Data)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(interlockingConc2Data), new MultiPolygon(interlockingConc3Data)));
//		totalIts++;
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(holes1Data), new MultiPolygon(block2Data)));
//		totalIts++;	
		nfpList.add(Orbiting.generateNFP(new MultiPolygon(mink1Data), new MultiPolygon(mink2Data)));
		totalIts++;	
//		nfpList.add(Orbiting.generateNFP(new MultiPolygon(simple2Data), new MultiPolygon(simple1Data)));
//		totalIts++;	
		
//		int i = 0;
//		int j = 0;
//		
//		for (MultiPolygon stat: polygonsT2) {
//			//if(i == 10)break;
//			j = 0;
//			for (MultiPolygon orb: polygonsT2) {
////						System.out.println("["+i+"]["+j+"]");
////						if(i == 10 && j == 42){
//			
//					nfpList.add(Orbiting.generateNFP(new MultiPolygon(stat), new MultiPolygon(orb)));
////							break;
////						}
//				totalIts++;
//				j++;
//		    }
//			i++;
//			
//		}
//		System.out.println("current total: " + totalIts);
//		System.out.println("fails: " + Orbiting.numberOfFails);
//		System.out.println("infinite stuck: " + Orbiting.numberStuckInfinite);
//			MultiPolygon holes = new MultiPolygon(holes1Data);
//			for(int j=0; j<650;j+=10){
//				for(int i=0; i<1000;i+=10){
//					if(holes.pointInPolygon(new Coordinate(i,j)))System.out.print("x");
//					else System.out.print(" ");
//				}
//				System.out.println();
//			}
			//System.out.println(new MultiPolygon(holes1Data).pointInPolygon(new Coordinate(400, 100)));
			
//			}
		endTime = System.currentTimeMillis();
		duration = (endTime - startTime);
		
		System.out.println("duration: " + duration);
			
			
			//print the nfp's------------------------------------------------------------------------
//			for(NoFitPolygon nfp : nfpList){
//				System.out.println(nfp.toString());
//			}
			
//		}
		// ------------------------------------------------------------------------------------
		// graphical representation
		 
		 drawTool.launchDrawer(args);
	}
/*
	private static void testCoordinateMethods() {
		Coordinate coord1 = new Coordinate(-2, -2);
		Coordinate coord2 = new Coordinate(4, 3);
		Coordinate coord3 = new Coordinate(5, 7);
		Coordinate coord4 = new Coordinate(-2, -5);

		double dist = coord1.distanceTo(coord2);
		System.out.println(dist);
		double angle = coord2.calculateAngle(coord1, coord3);

		angle += coord3.calculateAngle(coord1, coord2);
		angle += coord1.calculateAngle(coord3, coord2);// sum 180° correct

		Vector vect = new Vector(coord1,0, true);
		

		System.out.println(Math.toDegrees(vect.getVectorAngle()));
		System.out.println(Math.toDegrees(angle));

		double dVal = coord2.dFunction(coord1, coord3);
		System.out.println(dVal);
	}

	private static void testEdgeMethods() {
		Coordinate coord1 = new Coordinate(-2, -2);
		Coordinate coord2 = new Coordinate(4, 3);
		Coordinate coord3 = new Coordinate(5, 7);
		Coordinate coord4 = new Coordinate(-2, -5);

		Edge edge1 = new Edge(coord1, coord2, 0);
		Edge edge2 = new Edge(coord3, coord4, 5);

		System.out.println(edge1.boundingBoxIntersect(edge2));
		System.out.println(edge1.lineIntersect(edge2));
		System.out.println(edge1.calcIntersection(edge2).toString());
	}
*/
}
