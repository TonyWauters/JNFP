package JNFP;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is used in a static way to call the Orbiting method
 * @author Stiaan Uyttersprot
 *
 */
public class Orbiting {

	protected static int numberOfFails = 0;
	protected static int numberOfSecFails = 0;
	protected static int numberStuckInfinite = 0;

	/**
	 * Given two polygons, calculates the resulting no-fit polygon using the Orbiting method.
	 * If rounding problems arise adjust the round, a good value is 1e-4(use method adjustRound(double round))
	 * or use the Orbiting method provided in NFPTools.
	 * @param statPoly the stationary polygon
	 * @param orbPoly the orbiting polygon
	 * @return the NFP
	 * @throws FileNotFoundException when the file is not found
	 */
	public static String generateNFP(MultiPolygon statPoly, MultiPolygon orbPoly) throws FileNotFoundException {

		Coordinate bottomCoord = statPoly.findBottomCoord();
		Coordinate topCoord = orbPoly.findTopCoord();
		
		statPoly.isStationary();
		orbPoly.translate(bottomCoord.getxCoord() - topCoord.getxCoord(),
				bottomCoord.getyCoord() - topCoord.getyCoord());

		NoFitPolygon nfp = new NoFitPolygon(orbPoly.getOuterPolygon()[0], statPoly, orbPoly);
		// we need to choose a vector to translate with an angle that is closest
		// to the last angle chosen to translate
		
		orbitPolygon(nfp, statPoly, orbPoly, true);

		//nfp steps for interlocking concavities and holes----------------------
		
		Edge possibleStartEdge;
		
		List<Coordinate[]> startPointList;
		Vector placeOrbPoly;
		
		while(!statPoly.allEdgesTraversed()){
			
			possibleStartEdge = statPoly.findUntraversedEdge();
			possibleStartEdge.markTraversed();
			startPointList = statPoly.searchStartPointList(possibleStartEdge, orbPoly);
			for(Coordinate[] startPoint: startPointList){
				
				if(!nfp.containsPoint(startPoint[1])){
					
					placeOrbPoly = new Vector(orbPoly.getOuterPolygon()[0], startPoint[1]);
					orbPoly.translate(placeOrbPoly);
					//check for perfectly overlapping polygons
					if(!perfectOverlap(statPoly, orbPoly)){
						nfp.startNewActiveList(orbPoly.getOuterPolygon()[0]);
						//startpoint has been found, now to start orbiting here
						
						// start the orbiting
						orbitPolygon(nfp, statPoly, orbPoly, false);
					}
				}
			}
		}
		while(!orbPoly.allEdgesTraversed()){
			
			possibleStartEdge = orbPoly.findUntraversedEdge();
			possibleStartEdge.markTraversed();
			startPointList = statPoly.searchOrbStartPointList(possibleStartEdge, orbPoly);
			for(Coordinate[] startPoint: startPointList){
				if(!nfp.containsPoint(startPoint[1])){
					
					placeOrbPoly = new Vector(orbPoly.getOuterPolygon()[0], startPoint[1]);
					orbPoly.translate(placeOrbPoly);
					if(!perfectOverlap(statPoly, orbPoly)){
						nfp.startNewActiveList(orbPoly.getOuterPolygon()[0]);
						//startpoint has been found, now to start orbiting here
						
						// start the orbiting
						orbitPolygon(nfp, statPoly, orbPoly, false);
					}
				}
			}
		}
		
//		nfp.removeExcessivePoints();
//		System.out.println(nfp);
		return nfp.toString();
	}

	private static boolean perfectOverlap(MultiPolygon statPoly, MultiPolygon orbPoly) {
		boolean perfectOverlap = true;
		int startIndex = 0;
		if(statPoly.getOuterPolygon().length==orbPoly.getOuterPolygon().length){
			while(startIndex<statPoly.getOuterPolygon().length && !statPoly.getOuterPolygon()[0].equalValuesRounded(orbPoly.getOuterPolygon()[startIndex])){
				startIndex++;
			}
			int j = startIndex;
			if(j == statPoly.getOuterPolygon().length)return false;
			for(int i = 0; i<statPoly.getOuterPolygon().length; i++){
				if(statPoly.getOuterPolygon()[i].equalValuesRounded(orbPoly.getOuterPolygon()[j])){
					j = (j+1)%orbPoly.getOuterPolygon().length;
				}
				else{
					perfectOverlap = false;
					break;
				}
			}
			return perfectOverlap;
		}
		else return false;
	}

	private static void orbitPolygon(NoFitPolygon nfp, MultiPolygon statPoly, MultiPolygon orbPoly, boolean outer) {
		double previousEdge = 0;
		List<Vector> usedTranslationVectorList = new ArrayList<>();
		Coordinate startPoint = new Coordinate(orbPoly.getOuterPolygon()[0]);
		Coordinate currentPoint = orbPoly.getOuterPolygon()[0];

		int aantalStappen = 10000;
		int stap = 0;
		// start the orbiting
		do{
			//Storing data for drawing step by step----------------------------------------------------------------------------------------
//			NoFitPolygonStages.addNFP(new NoFitPolygon(nfp));
			
			// ---------------------------------------------------------------------------------------------------------------------
			// detecting touching edges
			
			List<TouchingEdgePair> touchingEdgeList = statPoly.findTouchingEdges(orbPoly);
			

			for (TouchingEdgePair tEP : touchingEdgeList) {
				tEP.calcFeasibleAngleRange();
			}
			
			// ---------------------------------------------------------------------------------------------------------------------
			// create potential translation vectors

			Set<Vector> potentialVectorList = new HashSet<>();
			Vector potVector;

			for (TouchingEdgePair tEP : touchingEdgeList) {
				potVector = tEP.getPotentialVector();
				if (potVector != null && !potentialVectorList.contains(potVector)) {
					potentialVectorList.add(potVector);
				}
			}
			
			// ----------------------------------------------------------------------------------------------------------------------
			// find the feasible vectors
			boolean feasibleVector;
			List<Vector> feasibleVectorList = new ArrayList<>();

			for (Vector vector : potentialVectorList) {
				int i = 0;
				feasibleVector = true;
				while (feasibleVector && i < touchingEdgeList.size()) {
					TouchingEdgePair tEP = touchingEdgeList.get(i);
					//we use rounded angles to avoid rounding errors
					if (!tEP.isFeasibleVectorWithRounding(vector)){
						feasibleVector = false;
					}
						
					i++;

				}
				if (feasibleVector) {
					feasibleVectorList.add(vector);
				}

			}
			//sorting by edgenumber and vectors made from stationary edges get privileges
			Collections.sort(feasibleVectorList, new VectorComparator());

			//-------------------------------------------------------------------------------------------------------------------------
			//look for the translation vector

			
			Vector translationVector;
			if (feasibleVectorList.size() > 1) {
				
				int i = 0;
				// look for the vector that is closest in edgenumber to the one
				// previously translated by
				while (i < feasibleVectorList.size() && feasibleVectorList.get(i).getEdgeNumber() < previousEdge ) {
					i++;
				}
				
				
				///look if the point after translation with this vector is the same as the previously visited point, if this is true, we skip this vector to not get stuck
				if(i < feasibleVectorList.size()&&usedTranslationVectorList.size()>0&&nfp.getActiveList().size()> 1){
					
					double lastAngle = usedTranslationVectorList.get(usedTranslationVectorList.size()-1).getVectorAngle();
					
					double nextAngle = feasibleVectorList.get(i).getVectorAngle();
					
					if(Math.abs(nextAngle - lastAngle)==Math.PI){
						int nFeas = feasibleVectorList.size()-1;
						i = (i+1)%feasibleVectorList.size();
						nextAngle = feasibleVectorList.get(i).getVectorAngle();
						while(nFeas>0 && Math.abs(nextAngle - lastAngle)==Math.PI){
							i = (i+1)%feasibleVectorList.size();
							nextAngle = feasibleVectorList.get(i).getVectorAngle();
							nFeas--;
						}
						
					}
					
				}
				

				// if the value of i is smaller then the listsize, a next vector
				// is found, if it reaches the end, it means the next vector is
				// the one with the smallest edgenumber
				if (i < feasibleVectorList.size()) {
					translationVector = feasibleVectorList.get(i);
				} else
					translationVector = feasibleVectorList.get(0);
			} else if(feasibleVectorList.size()==0){
				if(outer){
					System.err.println("Something went wrong\n forming the outer path of the NFP, no feasible vectors were found");
					numberOfFails++;
				}
				else{
//					System.out.println("stuck inner, mostly marks a perfect fit");
					numberOfSecFails++;
				}
				break;
			}
			else translationVector = feasibleVectorList.get(0);
			
			translationVector.trimFeasibleVector(orbPoly, statPoly, true);
			translationVector.trimFeasibleVector(statPoly, orbPoly, false);
			
			//-------------------------------------------------------------------------------------------------------------------------
			//translating the polygon and storing the data in the nfp
			orbPoly.translate(translationVector);
			usedTranslationVectorList.add(translationVector);
			nfp.addTranslation(orbPoly.getOuterPolygon()[0]);
			//store this angle as the previous angle
			previousEdge = translationVector.getEdgeNumber();
			
			//-------------------------------------------------------------------------------------------------------------------------
			//mark traversed edge (if working with findTouchingEdgesWithoutTravMark)
			translationVector.getParentEdge().markTraversed();
			for(Vector vect :feasibleVectorList){
				if(vect.getVectorAngle()==translationVector.getVectorAngle()){
					vect.getParentEdge().markTraversed();
				}
			}
			
			stap++;
		}
		while(!currentPoint.equalValuesRounded(startPoint) && stap < aantalStappen);
		if(!currentPoint.equalValuesRounded(startPoint) && stap == aantalStappen){
			System.err.println("stuck, the startpoint is never reached when orbiting");
			numberOfFails++;
			numberStuckInfinite++;
		}
		nfp.removeLastDoubleCoordinate();
		
	}
	
	/**
	 * Configurate the round if problems arise caused by rounding
	 * 
	 * @param round the new value for the round
	 */
	public static void adjustRound(double round){
		Coordinate.round = round;
		Edge.round = round;
		MultiPolygon.round = round;
		TouchingEdgePair.round = round;
	}
}
