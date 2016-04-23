package NFPGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import javafx.scene.shape.Polygon;

/**
 * this class contains a polygon to be used to generate the no-fit
 * polygon. The polygon exists of coordinates and can have holes in it.
 * 
 * @author Stiaan Uyttersprot
 *  
 */
public class MultiPolygon {

	private int nHoles; // the number of holes

	private Coordinate[] outerPolygon; // the polygon that envelops the holes
	private Edge[] outerPolygonEdges;

	private Coordinate[][] holes;
	private Edge[][] holeEdges;

	private Coordinate lowestCoord;
	
	private Scanner input;
	
	protected static double round = 1e-4;

	private double biggestX = 0;
	private double biggestY = 0;
	private double smallestX = Double.MAX_VALUE;
	private double smallestY = Double.MAX_VALUE;

	/**
	 * constructor reads file to create a multiPolygon
	 * @param file the file of the multipolygon
	 * @throws FileNotFoundException when the file is not found
	 */
	public MultiPolygon(File file) throws FileNotFoundException {

		input = new Scanner(file);
		try{

			nHoles = input.nextInt();
			int nPoints = input.nextInt();// number of points of the polygon that is
											// currently being read (this value
											// changes when holes are read too)
			// used for autoscaling in drawtool
			double readX;
			double readY;
	
			outerPolygon = new Coordinate[nPoints];
			holes = new Coordinate[nHoles][];
	
			for (int i = 0; i < nPoints; i++) {
	
				readX = input.nextDouble();
				readY = input.nextDouble();
				outerPolygon[i] = new Coordinate(readX, readY);
				
				//-----------------------------------------------------
				//values used for scaling in gui
				if (readX > biggestX)
					biggestX = readX;
				else if(readX < smallestX)smallestX = readX;
				if (readY > biggestY)
					biggestY = readY;
				else if(readY < smallestY)smallestY = readY;
				
				//-----------------------------------------------------	
			}
		
			if(checkClockwise(outerPolygon)){
				
				changeClockOrientation(outerPolygon);
	
			}
	
			for (int i = 0; i < nHoles; i++) {
	
				nPoints = input.nextInt();
				holes[i] = new Coordinate[nPoints];
	
				for (int j = 0; j < nPoints; j++) {
					holes[i][j] = new Coordinate(input.nextDouble(), input.nextDouble());
				}
				
				if(!checkClockwise(holes[i])){
					changeClockOrientation(holes[i]);
				}
			}
			input.close();
	
			// now we will make the edge arrays
			// this array contains the same information but in pairs of coordinates
			// to allow an easier way to use edges
			createEdges();
		}catch(InputMismatchException e){
			System.err.println("the input file is not correct");
			e.printStackTrace();
		}
	}

	/**
	 * deep copy for the MultiPolygon
	 * @param multiPoly the multipolygon to copy
	 */
	public MultiPolygon(MultiPolygon multiPoly){
		nHoles = multiPoly.getNumberOfHoles();

		outerPolygon = new Coordinate[multiPoly.getOuterPolygon().length];
		holes = new Coordinate[nHoles][];
		
		for (int i = 0; i < multiPoly.getOuterPolygon().length ; i++) {	
			outerPolygon[i] = new Coordinate(multiPoly.getOuterPolygon()[i]);
		}

		biggestX = multiPoly.getBiggestX();
		smallestX = multiPoly.getSmallestX();
		biggestY = multiPoly.getBiggestY();
		smallestY = multiPoly.getSmallestY();
		
		for (int i = 0; i < nHoles; i++) {

			holes[i] = new Coordinate[multiPoly.getHoles()[i].length];
			int j = 0;
			for (Coordinate coord : multiPoly.getHoles()[i]) {
				holes[i][j] = new Coordinate(coord);
				j++;
			}
		}

		// now we will make the edge arrays
		// this array contains the same information but in pairs of coordinates
		// to allow an easier way to use edges
		createEdges();
	}
	
	private void createEdges() {
		outerPolygonEdges = new Edge[outerPolygon.length];
		holeEdges = new Edge[nHoles][];

		for (int i = 0; i < outerPolygon.length; i++) {
			if (i == outerPolygon.length - 1) {
				outerPolygonEdges[i] = new Edge(outerPolygon[i], outerPolygon[0], i+1);
			} else {
				outerPolygonEdges[i] = new Edge(outerPolygon[i], outerPolygon[i + 1], i+1);
			}
		}

		for (int i = 0; i < nHoles; i++) {
			holeEdges[i] = new Edge[holes[i].length];
			for (int j = 0; j < holes[i].length; j++) {

				if (j == holes[i].length - 1) {
					holeEdges[i][j] = new Edge(holes[i][j], holes[i][0], i+1);
				} else {
					holeEdges[i][j] = new Edge(holes[i][j], holes[i][j + 1], i+1);
				}

			}
		}
	}

	/**
	 * @return smallest x value in the polygon
	 */
	public double getSmallestX() {
		return smallestX;
	}

	/**
	 * @return smallest y value in the polygon
	 */
	public double getSmallestY() {
		return smallestY;
	}

	/**
	 * @return biggest x value in the polygon
	 */
	public double getBiggestX() {
		return biggestX;
	}

	/**
	 * @return biggest y value in the polygon
	 */
	public double getBiggestY() {
		return biggestY;
	}

	/**
	 * @return the coordinate array of the outer polygon
	 */
	public Coordinate[] getOuterPolygon() {
		return outerPolygon;
	}

	/**
	 * @return a coordinate matrix with each row describing a hole
	 */
	public Coordinate[][] getHoles() {
		return holes;
	}

	/**
	 * @return the number of holes
	 */
	public int getNumberOfHoles() {
		return nHoles;
	}

	protected Edge[] getOuterPolygonEdges() {
		return outerPolygonEdges;
	}

	protected Edge[][] getHoleEdges() {
		return holeEdges;
	}

	// methods for
	// GUI----------------------------------------------------------------------------------------------------------------
	// converting outer polygon into polygon for UI
	protected Polygon makeOuterPolygon(double xSize, double ySize, double sizeFactor) {

		Polygon polygon = new Polygon();
		for (Coordinate coord : outerPolygon) {
			// add xSize / 2 to coord to move axis
			polygon.getPoints().add(sizeFactor * coord.getxCoord() + xSize / 2);
			// yCoord*-1 to invert to normal axis and add ySize / 2 to move axis
			polygon.getPoints().add(-1 * sizeFactor * coord.getyCoord() + ySize / 2);
		}

		return polygon;
	}

	// converting holes into polygons for UI
	protected Polygon[] makeHoles(double xSize, double ySize, double sizeFactor) {
		Polygon[] polyHoles = new Polygon[nHoles];
		for (int i = 0; i < nHoles; i++) {
			polyHoles[i] = new Polygon();
			for (Coordinate coord : holes[i]) {
				polyHoles[i].getPoints().add(sizeFactor * coord.getxCoord() + xSize / 2);
				// yCoord*-1 to invert to normal axis
				polyHoles[i].getPoints().add(-1 * sizeFactor * coord.getyCoord() + ySize / 2);
			}
		}
		return polyHoles;
	}

	/**
	 * translate the multipolygon using the vector described by it's x- and y-coord.
	 * @param x the x-value of the translationvector
	 * @param y the y-value of the translationvector
	 */
	public void translate(double x, double y) {

		for (Coordinate coord : outerPolygon) {
			coord.move(x, y);
		}

		for (Coordinate[] hole : holes) {
			for (Coordinate coord : hole) {
				coord.move(x, y);
			}
		}
		//for the edges the new minimum and maximum values need to be recalculated
		for (Edge edge : outerPolygonEdges){
			edge.changeRangeValues(x, y);
		}
		for (Edge[] edgeList : holeEdges){
			for(Edge edge : edgeList){
				edge.changeRangeValues(x, y);
			}
		}
	}
	
	/**
	 * translate the multipolygon with vector
	 * @param vect the translationvector
	 */
	public void translate(Vector vect) {
		double x = vect.getxCoord();
		double y = vect.getyCoord();
		
		for (Coordinate coord : outerPolygon) {
			coord.move(x, y);
		}

		for (Coordinate[] hole : holes) {
			for (Coordinate coord : hole) {
				coord.move(x, y);
			}
		}
		
		//for the edges the new minimum and maximum values need to be recalculated
		for (Edge edge : outerPolygonEdges){
			edge.changeRangeValues(x, y);
		}
		for (Edge[] edgeList : holeEdges){
			for(Edge edge : edgeList){
				edge.changeRangeValues(x, y);
			}
		}
	}
	// ---------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * print out the data of the polygon
	 */
	public void printPolygonData() {
		System.out.println("outer polygon number of points: " + outerPolygon.length);
		for (Coordinate coord : outerPolygon) {
			coord.printCoordinate();
		}
		for (int i = 0; i < nHoles; i++) {
			System.out.println("hole " + (i + 1) + " number of points: " + holes[i].length);
			for (Coordinate holeCoord : holes[i]) {
				holeCoord.printCoordinate();
			}
		}
	}

	/**
	 * find the lowest coordinate in the multipolygon
	 * @return the lowest coordinate
	 */
	public Coordinate findBottomCoord() {
		Coordinate bottomCoord = outerPolygon[0];
		for (Coordinate coord : outerPolygon) {
			// if the y-value of coord is lower then the current bottomCoord,
			// replace bottomCoord
			if (coord.getyCoord() < bottomCoord.getyCoord())
				bottomCoord = coord;
			else if(coord.getyCoord() == bottomCoord.getyCoord()){
				if(coord.getxCoord() < bottomCoord.getxCoord()){
					bottomCoord = coord;
				}
			}
		}
		return bottomCoord;
	}

	/**
	 * find the highest coordinate in the multipolygon
	 * @return the highest coordinate
	 */
	public Coordinate findTopCoord() {
		Coordinate topCoord = outerPolygon[0];
		for (Coordinate coord : outerPolygon) {
			// if the y-value of coord is higher then the current topCoord,
			// replace topCoord
			if (coord.getyCoord() > topCoord.getyCoord())
				topCoord = coord;
			else if(coord.getyCoord() == topCoord.getyCoord()){
				if(coord.getxCoord() > topCoord.getxCoord()){
					topCoord = coord;
				}
			}
		}
		return topCoord;
	}

	/**
	 * find all touching edges with from this polygon with another polygon
	 * @param orbPoly the second polygon
	 * @return a list of touching edge pairs 
	 */
	public List<TouchingEdgePair> findTouchingEdges(MultiPolygon orbPoly) {

		List<TouchingEdgePair> touchingEdges = new ArrayList<>();
		TouchingEdgePair tEP;
		// the outer polygon of the orbiting multipolygon
		Edge[] orbOuterPolygonEdges = orbPoly.getOuterPolygonEdges();
		Edge[][] orbHoleEdges = orbPoly.getHoleEdges();
		// check for every point of orb if it touches an edge of stat
		for (Edge orbEdge : orbOuterPolygonEdges) {

			for (Edge statEdge : outerPolygonEdges) {

				tEP = statEdge.touching(orbEdge);
				if (tEP != null){
					touchingEdges.add(tEP);
					statEdge.markTraversed();
					orbEdge.markTraversed();
				}	
			}
		}
		
		for (Edge orbEdge : orbOuterPolygonEdges) {

			for (Edge[] statHole : holeEdges) {

				for (Edge statEdge : statHole) {

					tEP = statEdge.touching(orbEdge);
					if (tEP != null){
						touchingEdges.add(tEP);
						statEdge.markTraversed();
						orbEdge.markTraversed();
					}	
				}	
			}
		}
		for(Edge[] orbHole: orbHoleEdges){
			for (Edge orbEdge : orbHole) {
	
				for (Edge statEdge : outerPolygonEdges) {

					tEP = statEdge.touching(orbEdge);
					if (tEP != null){
						touchingEdges.add(tEP);
						statEdge.markTraversed();
						orbEdge.markTraversed();
					}	
				}
			}
		}
		for(Edge[] orbHole: orbHoleEdges){
			for (Edge orbEdge : orbHole) {
	
				for (Edge[] statHole : holeEdges) {
	
					for (Edge statEdge : statHole) {
	
						tEP = statEdge.touching(orbEdge);
						if (tEP != null){
							touchingEdges.add(tEP);
							statEdge.markTraversed();
							orbEdge.markTraversed();
						}	
					}	
				}
			}
		}
		
		return touchingEdges;
	}
	
	//this method does not mark the edges as traversed when touched
	protected List<TouchingEdgePair> findTouchingEdgesWithoutTravMark(MultiPolygon orbPoly) {

		List<TouchingEdgePair> touchingEdges = new ArrayList<>();
		TouchingEdgePair tEP;
		// the outer polygon of the orbiting multipolygon
		Edge[] orbOuterPolygonEdges = orbPoly.getOuterPolygonEdges();
		Edge[][] orbHoleEdges = orbPoly.getHoleEdges();
		// check for every point of orb if it touches an edge of stat
		for (Edge orbEdge : orbOuterPolygonEdges) {

			for (Edge statEdge : outerPolygonEdges) {

				tEP = statEdge.touching(orbEdge);
				if (tEP != null){
					touchingEdges.add(tEP);
				}	
			}
		}
		
		for (Edge orbEdge : orbOuterPolygonEdges) {

			for (Edge[] statHole : holeEdges) {

				for (Edge statEdge : statHole) {

					tEP = statEdge.touching(orbEdge);
					if (tEP != null){
						touchingEdges.add(tEP);
					}	
				}	
			}
		}
		for(Edge[] orbHole: orbHoleEdges){
			for (Edge orbEdge : orbHole) {
	
				for (Edge statEdge : outerPolygonEdges) {

					tEP = statEdge.touching(orbEdge);
					if (tEP != null){
						touchingEdges.add(tEP);
					}	
				}
			}
		}
		for(Edge[] orbHole: orbHoleEdges){
			for (Edge orbEdge : orbHole) {
	
				for (Edge[] statHole : holeEdges) {
	
					for (Edge statEdge : statHole) {
	
						tEP = statEdge.touching(orbEdge);
						if (tEP != null){
							touchingEdges.add(tEP);
						}	
					}	
				}
			}
		}
		
		return touchingEdges;
	}

	protected void isStationary() {
		for (Edge e : outerPolygonEdges) {
			e.setStationary(true);
		}
		for (Edge[] eA : holeEdges) {
			for (Edge e : eA) {
				e.setStationary(true);
			}
		}
	}
	
	/**
	 * this method returns true if the polygon is clockwise
	 * @param polygon an array of coordinates describing the polygon
	 * @return if the polygon is clockwise or counterclockwise
	 */
	public boolean checkClockwise(Coordinate[] polygon) {
		double clockwiseValue = 0;
		
		double xDiff;
		double ySum;
		
		//If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
		for(int i = 0; i < polygon.length; i++){
			if(i < polygon.length-1){
				//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
				xDiff = polygon[i+1].getxCoord() - polygon[i].getxCoord();
				ySum = polygon[i+1].getyCoord() + polygon[i].getyCoord();
				clockwiseValue += xDiff*ySum;
				
			}
			else{
				xDiff = polygon[0].getxCoord() - polygon[i].getxCoord();
				ySum = polygon[0].getyCoord() + polygon[i].getyCoord();
				clockwiseValue += xDiff*ySum;
			}
		}
		
		if(clockwiseValue > 0) return true;
		else return false;
	}
	

	/**
	 * changes the orientation of a polygon
	 * @param polygon an array of coordinates describing the polygon
	 */
	public static void changeClockOrientation(Coordinate[] polygon){
		Coordinate[] changedPolygon = new Coordinate[polygon.length];
		changedPolygon[0] = polygon[0];
		for(int i = 1; i < polygon.length; i++){
			changedPolygon[i] = polygon[polygon.length-i];
		}
		for(int i = 0; i < polygon.length; i++){
			polygon[i] = changedPolygon[i];
		}
		
	}

	protected boolean allEdgesTraversed() {
		for(Edge e : outerPolygonEdges){
			if(!e.isTraversed())return false;
		}
		
		for(Edge[] hole: holeEdges){
			for(Edge h: hole){
				if(!h.isTraversed())return false;
			}
		}
			
		return true;
	}

	protected Edge findUntraversedEdge() {
		//this is true when an untraversed edge is found
		Edge untraversedEdge = null;
		
		int i =0;
		while(untraversedEdge == null && i< outerPolygonEdges.length){
			if(!outerPolygonEdges[i].isTraversed()) untraversedEdge = outerPolygonEdges[i];
			i++;
			
		}
		i = 0;
		int j;
		while(untraversedEdge == null && i < holeEdges.length){
			j = 0;

			while(untraversedEdge == null && j < holeEdges[i].length){
				if(!holeEdges[i][j].isTraversed()) untraversedEdge = holeEdges[i][j];
				j++;
				
			}
			i++;
		}
		return untraversedEdge;
	}

	protected Coordinate searchStartPoint(Edge possibleStartEdge, MultiPolygon orbPoly) {
		
		Coordinate currentStartPoint = new Coordinate(possibleStartEdge.getStartPoint());
		System.out.println(currentStartPoint);
		//for every point of the orbiting polygon that can be placed at that spot
		int orbPointIndex = 0;
		boolean startPointPossible = false;
		Vector placeOrbPolyVector;
		
		Vector nextPossibleSpotVector;
		
		for(int i=0; i< orbPoly.getOuterPolygon().length; i++){
			Coordinate orbPoint = new Coordinate(orbPoly.getOuterPolygon()[i]);
			//translation to the startpoint from the current orbiting point
			placeOrbPolyVector = new Vector(orbPoint, currentStartPoint);
			
			orbPoly.translate(placeOrbPolyVector);
			
			if(!polygonsIntersectPointInPolygon(this, orbPoly)){
				return currentStartPoint;
			}
			else{//test 1, check if it is ever possible
				
				if(orbPointIndex == 0){
					startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPoly.getOuterPolygonEdges().length-1],
							orbPoly.getOuterPolygonEdges()[orbPointIndex]);
				}
				else{
					startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPointIndex-1],
							orbPoly.getOuterPolygonEdges()[orbPointIndex]);
				}
			}
			//keep looking for a place where the polygons don't overlap, till the end of the line has been reached
			if(startPointPossible){
				//trim the vector made by the possible startEdge and the current start point
				while(!currentStartPoint.equals(possibleStartEdge.getEndPoint())&&polygonsIntersectPointInPolygon(this, orbPoly)){
					nextPossibleSpotVector = new Vector(currentStartPoint, possibleStartEdge.getEndPoint());
					
					nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
					nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);
					
					orbPoly.translate(nextPossibleSpotVector);
					currentStartPoint.translate(nextPossibleSpotVector);
				}
				if(currentStartPoint.equals(possibleStartEdge.getEndPoint())&&!polygonsIntersectPointInPolygon(this, orbPoly)){
					return currentStartPoint;
				}
				else if(!currentStartPoint.equals(possibleStartEdge.getEndPoint())){
					return currentStartPoint;
				}
			}
			orbPointIndex++;
		}
		return null;
		
	}
	
	protected Coordinate searchOrbStartPoint(Edge possibleStartOrbEdge, MultiPolygon orbPoly) {
		Coordinate currentStartPoint;
		Coordinate orbitingStartPoint = possibleStartOrbEdge.getStartPoint();
		Vector placeOrbPolyVector;
		int statPointIndex = 0;
		boolean startPointPossible;
		
		for(int i=0; i< getOuterPolygon().length; i++){
			currentStartPoint = outerPolygon[i];
			placeOrbPolyVector = new Vector(orbitingStartPoint, currentStartPoint);
			
			Vector nextPossibleSpotVector;
			
			orbPoly.translate(placeOrbPolyVector);
			
			if(!polygonsIntersectPointInPolygon(this, orbPoly)){
				
				return currentStartPoint;
			}
			
			else{//test 1, check if it is ever possible	
				
				if(statPointIndex == 0){
					startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[getOuterPolygonEdges().length-1],
							getOuterPolygonEdges()[statPointIndex]);
				}
				else{
					startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[statPointIndex-1],
							getOuterPolygonEdges()[statPointIndex]);
				}
				if(startPointPossible){
					//trim the vector made by the possible startEdge and the current start point
					while(!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())&&polygonsIntersectPointInPolygon(this, orbPoly)){
						nextPossibleSpotVector = new Vector(possibleStartOrbEdge.getEndPoint(), currentStartPoint);
						
						nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
						nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);
						orbPoly.translate(nextPossibleSpotVector);

					}
					
					if(currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())&&!polygonsIntersectPointInPolygon(this, orbPoly)){
						return currentStartPoint;
					}
					else if(!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())){
						
						return currentStartPoint;
					}
				}
				
			}
			statPointIndex++;
		}
		return null;
	}
	
	protected Coordinate searchStartPoint(Edge possibleStartEdge, MultiPolygon orbPoly, NoFitPolygon nfp) {
		
		Coordinate currentStartPoint = new Coordinate(possibleStartEdge.getStartPoint());
		//for every point of the orbiting polygon that can be placed at that spot
		int orbPointIndex = 0;
		boolean startPointPossible = false;
		Vector placeOrbPolyVector;
		
		Vector nextPossibleSpotVector;
		
		for(int i=0; i< orbPoly.getOuterPolygon().length; i++){
			Coordinate orbPoint = new Coordinate(orbPoly.getOuterPolygon()[i]);
			//translation to the startpoint from the current orbiting point
			placeOrbPolyVector = new Vector(orbPoint, currentStartPoint);
			
			orbPoly.translate(placeOrbPolyVector);

			
			if(!polygonsIntersectPointInPolygon(this, orbPoly)&&!nfp.containsPoint(currentStartPoint)){
				return currentStartPoint;
			}
			else{//test 1, check if it is ever possible
				
				if(orbPointIndex == 0){
					startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPoly.getOuterPolygonEdges().length-1],
							orbPoly.getOuterPolygonEdges()[orbPointIndex]);
				}
				else{
					startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPointIndex-1],
							orbPoly.getOuterPolygonEdges()[orbPointIndex]);
				}
			}
			//keep looking for a place where the polygons don't overlap, till the end of the line has been reached
			if(startPointPossible){
				//trim the vector made by the possible startEdge and the current start point
				do{
					nextPossibleSpotVector = new Vector(currentStartPoint, possibleStartEdge.getEndPoint());
					
					nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
					nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);
					
					orbPoly.translate(nextPossibleSpotVector);
					currentStartPoint.translate(nextPossibleSpotVector);
					
				}while(!currentStartPoint.equals(possibleStartEdge.getEndPoint())&&(polygonsIntersectPointInPolygon(this, orbPoly)||nfp.containsPoint(currentStartPoint)));
				
				if(currentStartPoint.equals(possibleStartEdge.getEndPoint())&&!polygonsIntersectPointInPolygon(this, orbPoly)&&!nfp.containsPoint(currentStartPoint)){
					return currentStartPoint;
				}
				else if(!currentStartPoint.equals(possibleStartEdge.getEndPoint())&&!nfp.containsPoint(currentStartPoint)){
					return currentStartPoint;
				}
			}
			orbPointIndex++;
		}
		return null;
		
	}
	
	protected Coordinate searchOrbStartPoint(Edge possibleStartOrbEdge, MultiPolygon orbPoly, NoFitPolygon nfp) {
		Coordinate currentStartPoint;
		Coordinate orbitingStartPoint = possibleStartOrbEdge.getStartPoint();
		Vector placeOrbPolyVector;
		int statPointIndex = 0;
		boolean startPointPossible;
		
		for(int i=0; i< getOuterPolygon().length; i++){
			currentStartPoint = outerPolygon[i];
			placeOrbPolyVector = new Vector(orbitingStartPoint, currentStartPoint);
			
			Vector nextPossibleSpotVector;
			
			orbPoly.translate(placeOrbPolyVector);
			
			if(!polygonsIntersectPointInPolygon(this, orbPoly)&&!nfp.containsPoint(currentStartPoint)){
				
				return currentStartPoint;
			}
			
			else{//test 1, check if it is ever possible	
				
				if(statPointIndex == 0){
					startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[getOuterPolygonEdges().length-1],
							getOuterPolygonEdges()[statPointIndex]);
				}
				else{
					startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[statPointIndex-1],
							getOuterPolygonEdges()[statPointIndex]);
				}
				if(startPointPossible){
					//trim the vector made by the possible startEdge and the current start point
					do{
						nextPossibleSpotVector = new Vector(possibleStartOrbEdge.getEndPoint(), currentStartPoint);
						
						nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
						nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);
						orbPoly.translate(nextPossibleSpotVector);
						
					}while(!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())&&(polygonsIntersectPointInPolygon(this, orbPoly)||nfp.containsPoint(currentStartPoint)));
					
					if(currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())&&!polygonsIntersectPointInPolygon(this, orbPoly)&&!nfp.containsPoint(currentStartPoint)){
						return currentStartPoint;
					}
					else if(!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())&&!nfp.containsPoint(currentStartPoint)){
						return currentStartPoint;
					}
				}
				
			}
			statPointIndex++;
		}
		return null;
	}
	
	protected List<Coordinate[]> searchStartPointList(Edge possibleStartEdge, MultiPolygon orbPoly) {
		
		List<Coordinate[]> startPointList = new ArrayList<>();
		Coordinate[] startOrbStat;
		Coordinate currentStartPoint = new Coordinate(possibleStartEdge.getStartPoint());
		//for every point of the orbiting polygon that can be placed at that spot
		int orbPointIndex = 0;
		boolean startPointPossible = false;
		Vector placeOrbPolyVector;
		
		Vector nextPossibleSpotVector = null;
		
		for(int i=0; i< orbPoly.getOuterPolygon().length; i++){
			Coordinate orbPoint = new Coordinate(orbPoly.getOuterPolygon()[i]);
			//translation to the startpoint from the current orbiting point
			placeOrbPolyVector = new Vector(orbPoint, currentStartPoint);
			
			orbPoly.translate(placeOrbPolyVector);
			
			if(!polygonsIntersectPointInPolygon(this, orbPoly)){
				startOrbStat = new Coordinate[2];
				
				startOrbStat[0] =new Coordinate(currentStartPoint);
				startOrbStat[1] =new Coordinate(orbPoly.getOuterPolygon()[0]);
				
				startPointList.add(startOrbStat);
			}
			else{//test 1, check if it is ever possible
				
				if(orbPointIndex == 0){
					startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPoly.getOuterPolygonEdges().length-1],
							orbPoly.getOuterPolygonEdges()[orbPointIndex]);
				}
				else{
					startPointPossible = possibleStartEdge.edgesOrientatedRight(orbPoly.getOuterPolygonEdges()[orbPointIndex-1],
							orbPoly.getOuterPolygonEdges()[orbPointIndex]);
				}
			}
			//keep looking for a place where the polygons don't overlap, till the end of the line has been reached
			if(startPointPossible){
				//trim the vector made by the possible startEdge and the current start point
				while(!currentStartPoint.equals(possibleStartEdge.getEndPoint())&&polygonsIntersectPointInPolygon(this, orbPoly)){
					nextPossibleSpotVector = new Vector(currentStartPoint, possibleStartEdge.getEndPoint());
					
					nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
					nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);
					
					if(nextPossibleSpotVector.getLengthSquared()<round)break;
					
					orbPoly.translate(nextPossibleSpotVector);
					currentStartPoint.translate(nextPossibleSpotVector);
				}
				if(nextPossibleSpotVector.getLengthSquared()<round){
					//it wil not lead to a start point
				}
				else if(currentStartPoint.equals(possibleStartEdge.getEndPoint())&&!polygonsIntersectPointInPolygon(this, orbPoly)){
					startOrbStat = new Coordinate[2];
					startOrbStat[0] =new Coordinate(currentStartPoint);
					startOrbStat[1] =new Coordinate(orbPoly.getOuterPolygon()[0]);
					startPointList.add(startOrbStat);
				}
				else if(!currentStartPoint.equals(possibleStartEdge.getEndPoint())){
					startOrbStat = new Coordinate[2];
					startOrbStat[0] =new Coordinate(currentStartPoint);
					startOrbStat[1] =new Coordinate(orbPoly.getOuterPolygon()[0]);
					startPointList.add(startOrbStat);
				}
			}
			orbPointIndex++;
		}
		return startPointList;
		
	}
	
	protected List<Coordinate[]> searchOrbStartPointList(Edge possibleStartOrbEdge, MultiPolygon orbPoly) {
		List<Coordinate[]> startPointList = new ArrayList<>();
		Coordinate[] startOrbStat;
		
		Coordinate currentStartPoint;
		Coordinate orbitingStartPoint = possibleStartOrbEdge.getStartPoint();
		Vector placeOrbPolyVector;
		int statPointIndex = 0;
		boolean startPointPossible;
		
		for(int i=0; i< getOuterPolygon().length; i++){
			currentStartPoint = outerPolygon[i];
			placeOrbPolyVector = new Vector(orbitingStartPoint, currentStartPoint);
			
			Vector nextPossibleSpotVector;
			
			orbPoly.translate(placeOrbPolyVector);
			
			if(!polygonsIntersectPointInPolygon(this, orbPoly)){
				
				startOrbStat = new Coordinate[2];
				
				startOrbStat[0] =new Coordinate(currentStartPoint);
				startOrbStat[1] =new Coordinate(orbPoly.getOuterPolygon()[0]);
				
				startPointList.add(startOrbStat);
			}
			
			else{//test 1, check if it is ever possible	
				
				if(statPointIndex == 0){
					startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[getOuterPolygonEdges().length-1],
							getOuterPolygonEdges()[statPointIndex]);
				}
				else{
					startPointPossible = possibleStartOrbEdge.edgesOrientatedRight(getOuterPolygonEdges()[statPointIndex-1],
							getOuterPolygonEdges()[statPointIndex]);
				}
				if(startPointPossible){
					//trim the vector made by the possible startEdge and the current start point
					do{
						nextPossibleSpotVector = new Vector(possibleStartOrbEdge.getEndPoint(), currentStartPoint);
						
						nextPossibleSpotVector.trimFeasibleVector(orbPoly, this, true);
						nextPossibleSpotVector.trimFeasibleVector(this, orbPoly, false);
						orbPoly.translate(nextPossibleSpotVector);
					}while(!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())&&(polygonsIntersectPointInPolygon(this, orbPoly))
							&& nextPossibleSpotVector.getLengthSquared()>=round);
					if(nextPossibleSpotVector.getLengthSquared()<round){
						//it wil not lead to a start point
					}
					else if(currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())&&!polygonsIntersectPointInPolygon(this, orbPoly)){
						startOrbStat = new Coordinate[2];
						
						startOrbStat[0] =new Coordinate(currentStartPoint);
						startOrbStat[1] =new Coordinate(orbPoly.getOuterPolygon()[0]);
						
						startPointList.add(startOrbStat);
					}
					else if(!currentStartPoint.equals(possibleStartOrbEdge.getEndPoint())){
						
						startOrbStat = new Coordinate[2];
						
						startOrbStat[0] =new Coordinate(currentStartPoint);
						startOrbStat[1] =new Coordinate(orbPoly.getOuterPolygon()[0]);
						
						startPointList.add(startOrbStat);
					}
				}
				
			}
			statPointIndex++;
		}
		return startPointList;
	}

	protected static boolean polygonsIntersectEdgeIntersect(MultiPolygon statPoly, MultiPolygon orbPoly) {
		
		//outside check 
		for(Edge outerStatEdge: statPoly.getOuterPolygonEdges()){
			for(Edge outerOrbEdge : orbPoly.getOuterPolygonEdges()){
				
				if(outerOrbEdge.testIntersect(outerStatEdge)){
					return true;
				}
			}
		}
		//check orb outer with stat holes
		for(Edge outerOrbEdge: orbPoly.getOuterPolygonEdges()){
			for(Edge[] statHoles : statPoly.getHoleEdges()){
				for(Edge statHoleEdge: statHoles){
					if(outerOrbEdge.testIntersect(statHoleEdge)){
						return true;
					}
				}
				
			}
		}
		//check stat outer with orb holes
		for(Edge outerStatEdge: statPoly.getOuterPolygonEdges()){
			for(Edge[] orbHoles : orbPoly.getHoleEdges()){
				for(Edge orbHoleEdge: orbHoles){
					if(outerStatEdge.testIntersect(orbHoleEdge)){
						return true;
					}
				}
			}
		}
		//check holes
		for(Edge[] orbHoles : orbPoly.getHoleEdges()){
			for(Edge orbHoleEdge: orbHoles){
				for(Edge[] statHoles : statPoly.getHoleEdges()){
					for(Edge statHoleEdge: statHoles){
						if(orbHoleEdge.testIntersect(statHoleEdge)){
							return true;
						}
					}
				}
			}
		}
		return false;

	}
	

	
	protected static boolean polygonsIntersectPointInPolygon(MultiPolygon statPoly, MultiPolygon orbPoly) {
		boolean isOnEdge;
		boolean middlePointOnEdge;
		boolean touchedOuterEdge = false;
		boolean touchedHoleEdge = false;
		int i= 1;
		if(polygonsIntersectEdgeIntersect(statPoly, orbPoly))return true;
		if(polygonsIntersectEdgeOverlap(statPoly, orbPoly))return true;
		
		for(Coordinate coord: orbPoly.getOuterPolygon()){
			isOnEdge = false;
			for(Edge statEdge: statPoly.getOuterPolygonEdges()){
				if(statEdge.containsPoint(coord)){
					isOnEdge = true;
					touchedOuterEdge = true;
				}
			}
			for(Edge[] holes: statPoly.getHoleEdges()){
				for(Edge statEdge: holes){
					if(statEdge.containsPoint(coord)){
						isOnEdge = true;
						touchedHoleEdge = true;
					}
					
				}
			}
			if(touchedHoleEdge && touchedOuterEdge)return true;
			//our method for seeing if a point is in the polygon does not give a certain result for points that fall on the edge
			if(statPoly.pointInPolygon(coord)&&!isOnEdge){
				return true;
			}
			if(isOnEdge){
				middlePointOnEdge = false;
				Edge edgeToTest = new Edge(coord, orbPoly.getOuterPolygon()[i]);
				for(Edge statEdge: statPoly.getOuterPolygonEdges()){
					if(statEdge.testIntersect(edgeToTest)) return true;
				}
				for(Edge[] holes: statPoly.getHoleEdges()){
					for(Edge statEdge: holes){
						if(statEdge.testIntersect(edgeToTest)) return true;
					}
				}
				
				Coordinate middlePoint = edgeToTest.getMiddlePointEdge();
				for(Edge statEdge: statPoly.getOuterPolygonEdges()){
					if(statEdge.containsPoint(middlePoint)){
						middlePointOnEdge = true;
					}
					
				}
				for(Edge[] holes: statPoly.getHoleEdges()){
					for(Edge statEdge: holes){
						if(statEdge.containsPoint(middlePoint)){
							middlePointOnEdge = true;
						}
						
					}
				}
				if(!middlePointOnEdge){
					if(statPoly.pointInPolygon(middlePoint))return true;
				}
			}
			i++;
			if(i > orbPoly.getOuterPolygon().length-1)i = 0;
		}
		i = 1;
		for(Coordinate coord: statPoly.getOuterPolygon()){
			isOnEdge = false;
			for(Edge statEdge: orbPoly.getOuterPolygonEdges()){
				if(statEdge.containsPoint(coord))isOnEdge = true;
			}
			//our method for seeing if a point is in the polygon does not give a certain result for points that fall on the edge
			if(!isOnEdge && orbPoly.pointInPolygon(coord)){
				return true;
			}
			if(isOnEdge){
				middlePointOnEdge = false;
				Edge edgeToTest = new Edge(coord, statPoly.getOuterPolygon()[i]);
				for(Edge orbEdge: orbPoly.getOuterPolygonEdges()){
					if(orbEdge.testIntersect(edgeToTest)) return true;
				}
				for(Edge[] holes: orbPoly.getHoleEdges()){
					for(Edge orbEdge: holes){
						if(orbEdge.testIntersect(edgeToTest)) return true;
					}
				}
				Coordinate middlePoint = edgeToTest.getMiddlePointEdge();
				for(Edge orbEdge: orbPoly.getOuterPolygonEdges()){
					if(orbEdge.containsPoint(middlePoint)) middlePointOnEdge = true;
				}
				for(Edge[] holes: orbPoly.getHoleEdges()){
					for(Edge orbEdge: holes){
						if(orbEdge.containsPoint(middlePoint)) middlePointOnEdge = true;
					}
				}
				if(!middlePointOnEdge){
					if(orbPoly.pointInPolygon(middlePoint))return true;
				}
			}
			i++;
			if(i > statPoly.getOuterPolygon().length-1)i = 0;
		}
		return false;
	}
	

	private static boolean polygonsIntersectEdgeOverlap(MultiPolygon statPoly, MultiPolygon orbPoly) {
		for(Edge e: statPoly.getOuterPolygonEdges()){
			for(Edge f: orbPoly.getOuterPolygonEdges()){
				if(Math.abs(e.getAngle() - f.getAngle())%(Math.PI*2)==0){
					if(!e.getStartPoint().equalValuesRounded(f.getEndPoint())&&!e.getEndPoint().equalValuesRounded(f.getStartPoint())){
						if(e.getEndPoint().equalValuesRounded(f.getEndPoint())){
							return true;
						}
						if(e.getStartPoint().equalValuesRounded(f.getStartPoint())){
							return true;
						}
						if(e.containsPoint(f.getStartPoint())||e.containsPoint(f.getEndPoint())){
							return true;
						}
						if(f.containsPoint(e.getStartPoint())||f.containsPoint(e.getEndPoint()))return true;
					}
				}
			}
		}
		return false;
	}
	
	//-----------------------------------------------------------------------------------------------------------------------------


	/**
	 * The function will return true if the point x,y is inside the polygon, or
	 * false if it is not.  If the point is exactly on the edge of the polygon,
	 * then the function may return true or false.
	 * @author http://alienryderflex.com/polygon/
	 * @param coord the coord that may be in the polygon
	 * @return if the point is in the polygon or not
	 */
	public boolean pointInPolygon(Coordinate coord) {
		int polyCorners = outerPolygon.length;
		int i, j = polyCorners - 1;
		boolean oddNodes = false;

		for (i = 0; i < polyCorners; i++) {
			if ((outerPolygon[i].getyCoord() < coord.getyCoord() && outerPolygon[j].getyCoord() >= coord.getyCoord()
					|| outerPolygon[j].getyCoord() < coord.getyCoord()
							&& outerPolygon[i].getyCoord() >= coord.getyCoord())
					&& (outerPolygon[i].getxCoord() <= coord.getxCoord()
							|| outerPolygon[j].getxCoord() <= coord.getxCoord())) {
				// ^= is bitwise XOR assignement
				oddNodes ^= (outerPolygon[i].getxCoord() + (coord.getyCoord() - outerPolygon[i].getyCoord())
						/ (outerPolygon[j].getyCoord() - outerPolygon[i].getyCoord())
						* (outerPolygon[j].getxCoord() - outerPolygon[i].getxCoord()) < coord.getxCoord());
			}
			j = i;
		}
		boolean inHole = false;
		//if a hole contains the point it isn't contained by the polygon
		if(nHoles != 0){
			for (Coordinate[] hole : holes){
				polyCorners = hole.length;
				i = polyCorners - 1;
				j = polyCorners - 1;
				for (i = 0; i < polyCorners; i++) {
					if ((hole[i].getyCoord() < coord.getyCoord() && hole[j].getyCoord() >= coord.getyCoord()
							|| hole[j].getyCoord() < coord.getyCoord()
									&& hole[i].getyCoord() >= coord.getyCoord())
							&& (hole[i].getxCoord() <= coord.getxCoord()
									|| hole[j].getxCoord() <= coord.getxCoord())) {
						// ^= is bitwise XOR assignement
						inHole ^= (hole[i].getxCoord() + (coord.getyCoord() - hole[i].getyCoord())
								/ (hole[j].getyCoord() - hole[i].getyCoord())
								* (hole[j].getxCoord() - hole[i].getxCoord()) < coord.getxCoord());
					}
					j = i;
				}
				if(inHole) return false;
			}
		}
		return oddNodes;
	}

	protected void labelCounterClockwise() {
		lowestCoord = findBottomCoord();
		int startingCoordIndex = 0;
		boolean clockwise = checkClockwise(outerPolygon);
		for(Coordinate coord: outerPolygon){
			if(coord.equals(lowestCoord)){
				break;
			}
			startingCoordIndex++;
		}
		int orgEdgeNum;
		for(Edge e: outerPolygonEdges){
			orgEdgeNum = e.getEdgeNumber();
			e.calcEdgeAngle();
			if(clockwise){
				
				if(orgEdgeNum <= startingCoordIndex){
					e.setEdgeNumber(startingCoordIndex - orgEdgeNum);
				}
				else{
					e.setEdgeNumber(outerPolygonEdges.length - orgEdgeNum + startingCoordIndex);
				}
			}
			else{
				if(orgEdgeNum <= startingCoordIndex){
					e.setEdgeNumber(outerPolygonEdges.length + orgEdgeNum - startingCoordIndex);
				}
				else{
					e.setEdgeNumber(orgEdgeNum - startingCoordIndex);
				}
			}
		}
		
	}

	protected void calcDeltaAngles() {
		
		for(int i = 0; i< outerPolygonEdges.length;i++){
			if(i > 0){
				outerPolygonEdges[i].setDeltaAngle(outerPolygonEdges[i].getEdgeAngle() - outerPolygonEdges[i-1].getEdgeAngle());
			}
			else{
				outerPolygonEdges[i].setDeltaAngle(outerPolygonEdges[i].getEdgeAngle() - outerPolygonEdges[outerPolygonEdges.length-1].getEdgeAngle());
			}
			if(outerPolygonEdges[i].getDeltaAngle() > Math.PI){
				outerPolygonEdges[i].setDeltaAngle(outerPolygonEdges[i].getDeltaAngle() - 2* Math.PI);
			}
			else if(outerPolygonEdges[i].getDeltaAngle() < -Math.PI){
				outerPolygonEdges[i].setDeltaAngle(outerPolygonEdges[i].getDeltaAngle() + 2* Math.PI);
			}
		}
		for(int i = 0; i< outerPolygonEdges.length;i++){
			if(i > 0){
				if(Math.signum(outerPolygonEdges[i].getDeltaAngle())!= Math.signum(outerPolygonEdges[i-1].getDeltaAngle())){
					outerPolygonEdges[i].setTurningPoint(true);
				}
			}
			else{
				if(Math.signum(outerPolygonEdges[i].getDeltaAngle())!= Math.signum(outerPolygonEdges[outerPolygonEdges.length-1].getDeltaAngle())){
					outerPolygonEdges[i].setTurningPoint(true);
				}
			}
		}
		
	}

	//the standard algorithm calculates the edge angles in the way the edge is oriented, we need the angle in counterclockwise direction for
	//Polygon B that is in clockwise direction
	protected void changeEdgeAnglesCounterClockwise() {
		
		for(Edge e : outerPolygonEdges){
			e.calcInverseEdgeAngle();
		}
		
	}

	/**
	 * checks if the two polygons overlap or not
	 * @param polyB the polygon that may overlap this one
	 * @return if two polygons overlap or not
	 */
	public boolean overlapping(MultiPolygon polyB) {
		int f = 0;
		int k;
		boolean overlap = false;
		if(polygonsIntersectEdgeOverlap(polyB, this))return true;
		while(f < polyB.getOuterPolygonEdges().length && !overlap){
			Edge edgeA = polyB.getOuterPolygonEdges()[f];
			f++;
			k = 0;
			while(k < getOuterPolygonEdges().length && !overlap){
				Edge edgeB = getOuterPolygonEdges()[k];
				k++;
				if(edgeA.testIntersectWithoutBorders(edgeB)){
					overlap = true;
				}
				if(edgeA.containsPoint(edgeB.getStartPoint())&&!edgeB.getEndPoint().dFunctionCheck(edgeA.getStartPoint(), edgeA.getEndPoint())){
					if(!edgeA.getStartPoint().equalValuesRounded(edgeB.getStartPoint())&&!edgeA.getStartPoint().equalValuesRounded(edgeB.getEndPoint())){
						if(!edgeA.getEndPoint().equalValuesRounded(edgeB.getStartPoint())&&!edgeA.getEndPoint().equalValuesRounded(edgeB.getEndPoint())){
							if(edgeB.getEndPoint().dFunction(edgeA)>0)overlap = true;
						}
					}
				}
			}
		}
		return overlap;
		
	}
	
	protected void replaceByNegativeMink() {
		
		for(Coordinate coord: outerPolygon){
			coord.replaceByNegative();
		}
		for(Edge e: outerPolygonEdges){
			e.replaceByNegative();
		}
		
	}

	/**
	 * rotates the multipolygon 180°
	 */
	public void replaceByNegative() {
		
		for(Coordinate coord: outerPolygon){
			coord.replaceByNegative();
		}
		for(Coordinate[] hole: holes){
			for(Coordinate coord: hole){
				coord.replaceByNegative();
			}
		}
		createEdges();
		
	}
	/**
	 * rotates the multipolygon 90°
	 */
	public void shiftNinety() {
		for(Coordinate coord: outerPolygon){
			coord.rotateNinety();
		}
		for(Coordinate[] hole: holes){
			for(Coordinate coord: hole){
				coord.rotateNinety();
			}
		}
		
		createEdges();
	}
}
