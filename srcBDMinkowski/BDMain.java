import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stiaan Uyttersprot
 *
 */
public class BDMain {

	private static final String Terashima1 = "testsets\\Terashima1Polygons\\";
	private static final String Terashima2 = "testsets\\Terashima2Polygons\\";
	private static final String albano = "testsets\\albano\\";
	private static final String blaz = "testsets\\blaz\\";
	private static final String dagli = "testsets\\dagli\\";
	private static final String dighe1 = "testsets\\dighe1\\";
	private static final String dighe2 = "testsets\\dighe2\\";
	private static final String fu = "testsets\\fu\\";
	private static final String han = "testsets\\han\\";
	private static final String jakobs1 = "testsets\\jakobs1\\";
	private static final String jakobs2 = "testsets\\jakobs2\\";
	private static final String mao = "testsets\\mao\\";
	private static final String marques = "testsets\\marques\\";
	private static final String polygons_3 = "testsets\\polygons_3\\";
	private static final String polygons_5 = "testsets\\polygons_5\\";
	private static final String polygons_7 = "testsets\\polygons_7\\";
	private static final String polygons_10 = "testsets\\polygons_10\\";
	private static final String polygons_20 = "testsets\\polygons_20\\";
	private static final String polygons_50 = "testsets\\polygons_50\\";
	private static final String polygons_100 = "testsets\\polygons_100\\";
	private static final String polygons_200 = "testsets\\polygons_200\\";
	private static final String polygons_500 = "testsets\\polygons_500\\";
	private static final String polygons_1000 = "testsets\\polygons_1000\\";
	private static final String shapes0 = "testsets\\shapes0\\";
	private static final String shapes1 = "testsets\\shapes1\\";
	private static final String swim = "testsets\\swim\\";
	private static final String shirts = "testsets\\shirts_2007-05-15\\";	
	public static void main(String[] args) throws FileNotFoundException {
		
		System.out.println("Big Decimal Minkowski");
		
		Minkowski.printMinkData = false;
		Minkowski.printEdgeListData = false;
		Minkowski.printBoundaryData = false;
		Minkowski.drawFigures = false;
		Minkowski.drawNFP = false;
		Minkowski.handleError = true;
		
		boolean testMass = true;
		boolean testSpecial = false;
		boolean testSpecific = false;
		int findIDimensionPoly = 228;
		int findJDimensionPoly = 192;
		int numberOfPolys = 100;
		//3861
		if(testSpecific){
			Minkowski.printMinkData = true;
			Minkowski.printEdgeListData = true;
			Minkowski.printBoundaryData = true;
			Minkowski.drawFigures = true;
			Minkowski.drawNFP = true;
			Minkowski.handleError = true;
		}
		if(testMass && !testSpecific){
			Minkowski.printMinkData = false;
			Minkowski.printEdgeListData = false;
			Minkowski.printBoundaryData = false;
			Minkowski.drawFigures = false;
			Minkowski.drawNFP = false;
		}
		
		DrawJavaFX drawTool = new DrawJavaFX();

		
		List<NoFitPolygon> nfpList = new ArrayList<>();
		
		File thesisAData = new File("thesisexA.txt");
		File thesisBData = new File("thesisexB.txt");
		
		File simple1Data = new File("SimpleFig1.txt");
		File simple2Data = new File("SimpleFig2.txt");
		
		File mink1Data = new File("Mink1.txt");
		File mink2Data = new File("Mink2.txt");
		
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
		
		File jigsaw1Data = new File("Jigsaw1.txt");
		File jigsaw2Data = new File("Jigsaw2.txt");
		
		List<MultiPolygon> polygonsT1 = new ArrayList<>();
		List<MultiPolygon> polygonsT2 = new ArrayList<>();
		
		File folder = new File(Terashima1);
		File[] listOfFilesT1 = folder.listFiles();
		
		folder = new File(Terashima2);
		File[] listOfFilesT2 = folder.listFiles();
		
		folder = new File(albano);
		File[] albano = folder.listFiles();
		
		folder = new File(blaz);
		File[] blaz = folder.listFiles();
		
		folder = new File(dagli);
		File[] dagli = folder.listFiles();
		
		folder = new File(dighe1);
		File[] dighe1 = folder.listFiles();
		
		folder = new File(dighe2);
		File[] dighe2 = folder.listFiles();
		
		folder = new File(fu);
		File[] fu = folder.listFiles();
		
		folder = new File(han);
		File[] han = folder.listFiles();
		
		folder = new File(jakobs1);
		File[] jakobs1 = folder.listFiles();
		
		folder = new File(jakobs2);
		File[] jakobs2 = folder.listFiles();
		
		folder = new File(mao);
		File[] mao = folder.listFiles();
		
		folder = new File(marques);
		File[] marques = folder.listFiles();
		
		folder = new File(polygons_3);
		File[] polygons_3 = folder.listFiles();
		
		folder = new File(polygons_5);
		File[] polygons_5 = folder.listFiles();
		
		folder = new File(polygons_7);
		File[] polygons_7 = folder.listFiles();
		
		folder = new File(polygons_10);
		File[] polygons_10 = folder.listFiles();
		
		folder = new File(polygons_20);
		File[] polygons_20 = folder.listFiles();
		
		folder = new File(polygons_50);
		File[] polygons_50 = folder.listFiles();
		
		folder = new File(polygons_100);
		File[] polygons_100 = folder.listFiles();
		
		folder = new File(polygons_200);
		File[] polygons_200 = folder.listFiles();
		
		folder = new File(polygons_500);
		File[] polygons_500 = folder.listFiles();
		
		folder = new File(polygons_1000);
		File[] polygons_1000 = folder.listFiles();
		
		folder = new File(shapes0);
		File[] shapes0 = folder.listFiles();
		
		folder = new File(shapes1);
		File[] shapes1 = folder.listFiles();
		
		folder = new File(shirts);
		File[] shirts = folder.listFiles();
		
		folder = new File(swim);
		File[] swim = folder.listFiles();

		if(testMass){
//			System.out.println("Terashima1");
//			System.out.println("---------------");
//			generateNFPsForList(listOfFilesT1, 1);
//			
//			System.out.println("Terashima1");
//			System.out.println("---------------");
//			generateNFPsForList(listOfFilesT1, 1);
//
//			System.out.println("Terashima2");
//			System.out.println("---------------");
//			generateNFPsForList(listOfFilesT2, 1);
			
			System.out.println("albano");
			System.out.println("---------------");
			generateNFPsForList(albano, 4);
			
			System.out.println("blaz");
			System.out.println("---------------");
			generateNFPsForList(blaz, 4);
			
			System.out.println("dagli");
			System.out.println("---------------");
			generateNFPsForList(dagli, 4);
			
			System.out.println("dighe1");
			System.out.println("---------------");
			generateNFPsForList(dighe1, 4);
			
			System.out.println("dighe2");
			System.out.println("---------------");
			generateNFPsForList(dighe2, 4);
			
			System.out.println("fu");
			System.out.println("---------------");
			generateNFPsForList(fu, 4);
			
			System.out.println("han");
			System.out.println("---------------");
			generateNFPsForList(han, 4);
			
			System.out.println("jakobs1");
			System.out.println("---------------");
			generateNFPsForList(jakobs1, 4);
			
			System.out.println("jakobs2");
			System.out.println("---------------");
			generateNFPsForList(jakobs2, 4);
			
			System.out.println("mao");
			System.out.println("---------------");
			generateNFPsForList(mao, 4);
			
			System.out.println("marques");
			System.out.println("---------------");
			generateNFPsForList(marques, 4);
			
			System.out.println("shapes0");
			System.out.println("---------------");
			generateNFPsForList(shapes0, 4);
			
			System.out.println("shapes1");
			System.out.println("---------------");
			generateNFPsForList(shapes1, 4);
			
			System.out.println("shirts");
			System.out.println("---------------");
			generateNFPsForList(shirts, 4);
			
			System.out.println("swim");
			System.out.println("---------------");
			generateNFPsForList(swim, 4);
//			
//			System.out.println("polygons_3");
//			System.out.println("---------------");
//			generateNFPsForList(polygons_3, 4);
//			
//			System.out.println("polygons_5");
//			System.out.println("---------------");
//			generateNFPsForList(polygons_5, 4);
//			
//			System.out.println("polygons_7");
//			System.out.println("---------------");
//			generateNFPsForList(polygons_7, 4);
//			
//			Coordinate.round = 10;
//			System.out.println("polygons_10");
//			System.out.println("---------------");
//			generateNFPsForList(polygons_10, 4);
//			
//			System.out.println("polygons_20");
//			System.out.println("---------------");
//			generateNFPsForList(polygons_20, 4);
//			
//			System.out.println("polygons_50");
//			System.out.println("---------------");
//			generateNFPsForList(polygons_50, 2);
//			
//			System.out.println("polygons_100");
//			System.out.println("---------------");
//			generateNFPsForList(polygons_100, 1);
		}
		
		long startTime;
		long endTime;

		long duration;

		List<Long> durations = new ArrayList<>();
		int totalIts = 0;

		

		startTime = System.currentTimeMillis();

		int i = 0;
		
		if(testSpecial){
			
			System.out.println("speciale gevallen");
			System.out.println("----------------------");
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(thesisAData), new MultiPolygon(thesisBData)); //correct
//			totalIts++;
			
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(simple2Data), new MultiPolygon(simple1Data)); //correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(simple1Data), new MultiPolygon(simple2Data)); //correct
//			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(concave1Data), new MultiPolygon(concave2Data)); // correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(rectangle1Data), new MultiPolygon(rectangle1Data));//correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(puzzle1Data), new MultiPolygon(puzzle2Data));
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(puzzle3Data), new MultiPolygon(block1Data));
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(sawtooth1Data), new MultiPolygon(sawtooth2Data));//correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc1Data), new MultiPolygon(triangleData));//correct
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(triangleData), new MultiPolygon(interlockingConc2Data));
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(triangleData), new MultiPolygon(interlockingConc3Data));
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(holes1Data), new MultiPolygon(triangleData));
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(holes1Data), new MultiPolygon(holes2Data));
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc3Data), new MultiPolygon(interlockingConc2Data));
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc2Data), new MultiPolygon(interlockingConc3Data));
			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(holes1Data), new MultiPolygon(block2Data));
			totalIts++;	
//			
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(simple2Data), new MultiPolygon(simple1Data));
//			totalIts++;
			Minkowski.generateMinkowskiNFP(new MultiPolygon(jigsaw1Data), new MultiPolygon(jigsaw2Data));
			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(mink1Data), new MultiPolygon(mink2Data)); //correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(mink2Data), new MultiPolygon(mink1Data)); //correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(convex1Data), new MultiPolygon(convex2Data)); //correct
//			totalIts++;
			
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(concave2Data), new MultiPolygon(concave1Data)); //correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(concave1Data), new MultiPolygon(convex2Data)); //correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(convex1Data), new MultiPolygon(concave2Data)); //correct
//			totalIts++;
//			
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc2Data),new MultiPolygon(interlockingConc3Data));//correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(holes2Data), new MultiPolygon(holes1Data)); //correct 2richtingen (zonder gaten)
//			totalIts++;
//
//			
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(jigsaw1Data), new MultiPolygon(jigsaw2Data));
//			totalIts++;	
//			
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(triangleData), new MultiPolygon(interlockingConc2Data));//correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(triangleData), new MultiPolygon(interlockingConc3Data));//correct
//			totalIts++;
//			Minkowski.generateMinkowskiNFP(new MultiPolygon(interlockingConc2Data), new MultiPolygon(interlockingConc3Data));//correct
//			totalIts++;

			endTime = System.currentTimeMillis();
			duration = (endTime - startTime);
			System.out.println("current total: " + totalIts);
			System.out.println("fails: " + Minkowski.numberOfFails);
			System.out.println("infinite stuck: " + Minkowski.numberStuckInfinite);		
			System.out.println("duration: " + duration + " ms");
			System.out.println("total itterations: " + totalIts);
			
			Minkowski.numberOfFails = 0;
			Minkowski.numberStuckInfinite = 0;
			
			System.out.println();
		}
		
		if(Minkowski.drawFigures || Minkowski.drawNFP){
			drawTool.launchDrawer(args);
		}
	}
	private static void generateNFPsForList(File[] listOfFiles, int rotations) throws FileNotFoundException {
		int n = 0;
		
		List<MultiPolygon> polygonsList = new ArrayList<>();
		int numberOfPolys = 100;
		MultiPolygon original;
		MultiPolygon inverse;
		MultiPolygon ninety;
		MultiPolygon twoseventy;
		for(File polygon: listOfFiles){
			if(n == numberOfPolys)break;
			switch(rotations){
			case 1:
				polygonsList.add(new MultiPolygon(polygon));
				break;
			case 2:
				original = new MultiPolygon(polygon);
				inverse = new MultiPolygon(polygon);
				inverse.replaceByNegative();
				polygonsList.add(original);
				polygonsList.add(inverse);
				break;
			case 4:
				original = new MultiPolygon(polygon);
				inverse = new MultiPolygon(polygon);
				inverse.replaceByNegative();
				ninety = new MultiPolygon(polygon);
				ninety.shiftNinety();
				twoseventy = new MultiPolygon(polygon);
				twoseventy.shiftNinety();
				polygonsList.add(original);
				polygonsList.add(inverse);
				polygonsList.add(ninety);
				polygonsList.add(twoseventy);
				break;
			default:
				polygonsList.add(new MultiPolygon(polygon));
				break;
			}
			
			n++;
		}
		System.out.println(n);

		long startTime;
		long endTime;
		long duration;

		int totalIts = 0;

		

		startTime = System.currentTimeMillis();
		long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		
		for (MultiPolygon stat : polygonsList) {

			for (MultiPolygon orb : polygonsList) {

				Minkowski.generateMinkowskiNFP(new MultiPolygon(stat), new MultiPolygon(orb));
				
				totalIts++;
			}
			
		}
		long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long diffMemory = endMemory - startMemory;
		
		endTime = System.currentTimeMillis();
		duration = (endTime - startTime);
		System.out.println("current total: " + totalIts);
		System.out.println("fails: " + Minkowski.numberOfFails);
		System.out.println("infinite stuck: " + Minkowski.numberStuckInfinite);		
		System.out.println("duration: " + duration + " ms");
		System.out.println("total itterations: " + totalIts);
		System.out.println("Memory used: " + diffMemory);
		Minkowski.numberOfFails = 0;
		Minkowski.numberStuckInfinite = 0;
		
		System.out.println();
		
	}

}
