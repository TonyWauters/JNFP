
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javafx.scene.shape.Polygon;

/**
 * this class contains a polygon to be used to generate the no-fit
 *  polygon the polygon exists of coordinates and can have holes
 * @author  Stiaan Uyttersprot
 *      
 */
public class MultiPolygon {

	private int nHoles; // the number of holes
	
	private Coordinate[] outerPolygon; // the polygon that envelops the holes
	private Edge[] outerPolygonEdges;

	private Coordinate[][] holes;
	private Edge[][] holeEdges;

	private Coordinate lowestCoord;
	private Coordinate highestCoord;
	
	private Scanner input;

	private BigDecimal biggestX = BigDecimal.ZERO;
	private BigDecimal biggestY = BigDecimal.ZERO;
	private BigDecimal smallestX = new BigDecimal(Double.MAX_VALUE);
	private BigDecimal smallestY = new BigDecimal(Double.MAX_VALUE);
	
	private int scale = 10;
	private MathContext mc = MathContext.DECIMAL32;
	// constructor reads file to create a polygon
	MultiPolygon(File file) throws FileNotFoundException {

		input = new Scanner(file);
		nHoles = input.nextInt();
		int nPoints = input.nextInt();// number of points of the polygon that is
										// currently being read (this value
										// changes when holes are read too)

		// used for autoscaling in drawtool
		BigDecimal readX;
		BigDecimal readY;

		outerPolygon = new Coordinate[nPoints];
		holes = new Coordinate[nHoles][];

		for (int i = 0; i < nPoints; i++) {

			readX = new BigDecimal(input.nextDouble()*100, mc);
			readX = readX.setScale(scale);
			readY = new BigDecimal(input.nextDouble()*100, mc);
			readY = readY.setScale(scale);
			outerPolygon[i] = new Coordinate(readX, readY);
			
			//-----------------------------------------------------
			//values used for scaling in gui
			if (readX.compareTo(biggestX) > 0)
				biggestX = readX;
			else if(readX.compareTo(smallestX) < 0)smallestX = readX;
			if (readY.compareTo(biggestY) > 0)
				biggestY = readY;
			else if(readY.compareTo(smallestY) < 0)smallestY = readY;
			
			
			//-----------------------------------------------------
			
		}
		
//		if(checkClockwise(outerPolygon)){
			
			//changeClockOrientation(outerPolygon);

//		}

		for (int i = 0; i < nHoles; i++) {

			nPoints = input.nextInt();
			holes[i] = new Coordinate[nPoints];

			for (int j = 0; j < nPoints; j++) {
				holes[i][j] = new Coordinate(new BigDecimal(input.nextDouble()), new BigDecimal(input.nextDouble()));
			}
			
//			if(!checkClockwise(holes[i])){
				//changeClockOrientation(holes[i]);
//			}
		}
		input.close();

		// now we will make the edge arrays
		// this array contains the same information but in pairs of coordinates
		// to allow an easier way to use edges
		createEdges();

	}

	MultiPolygon(MultiPolygon mp){
		nHoles = mp.getnHoles();

		// used for autoscaling in drawtool
		BigDecimal readX;
		BigDecimal readY;

		outerPolygon = new Coordinate[mp.getOuterPolygon().length];
		holes = new Coordinate[nHoles][];
		
		for (int i = 0; i < mp.getOuterPolygon().length ; i++) {
			
			readX = mp.getOuterPolygon()[i].getxCoord();
			readY = mp.getOuterPolygon()[i].getyCoord();
			outerPolygon[i] = new Coordinate(mp.getOuterPolygon()[i]);
			
			biggestX = mp.getBiggestX();
			smallestX = mp.getSmallestX();
			biggestY = mp.getBiggestY();
			smallestY = mp.getSmallestY();

		}

		for (int i = 0; i < nHoles; i++) {

			holes[i] = new Coordinate[mp.getHoles()[i].length];
			int j = 0;
			for (Coordinate coord : mp.getHoles()[i]) {
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

	public BigDecimal getSmallestX() {
		return smallestX;
	}

	public void setSmallestX(BigDecimal smallestX) {
		this.smallestX = smallestX;
	}

	public BigDecimal getSmallestY() {
		return smallestY;
	}

	public void setSmallestY(BigDecimal smallestY) {
		this.smallestY = smallestY;
	}

	public BigDecimal getBiggestX() {
		return biggestX;
	}

	public void setBiggestX(BigDecimal biggestX) {
		this.biggestX = biggestX;
	}

	public BigDecimal getBiggestY() {
		return biggestY;
	}

	public void setBiggestY(BigDecimal biggestY) {
		this.biggestY = biggestY;
	}

	public Coordinate[] getOuterPolygon() {
		return outerPolygon;
	}

	public void setOuterPolygon(Coordinate[] outerPolygon) {
		this.outerPolygon = outerPolygon;
	}

	public Coordinate[][] getHoles() {
		return holes;
	}

	public void setHoles(Coordinate[][] holes) {
		this.holes = holes;
	}

	public int getnHoles() {
		return nHoles;
	}

	public void setnHoles(int nHoles) {
		this.nHoles = nHoles;
	}

	public Edge[] getOuterPolygonEdges() {
		return outerPolygonEdges;
	}

	public void setOuterPolygonEdges(Edge[] outerPolygonEdges) {
		this.outerPolygonEdges = outerPolygonEdges;
	}

	public Edge[][] getHoleEdges() {
		return holeEdges;
	}

	public void setHoleEdges(Edge[][] holeEdges) {
		this.holeEdges = holeEdges;
	}

	// methods for
	// GUI----------------------------------------------------------------------------------------------------------------
	// converting outer polygon into polygon for UI
	public Polygon makeOuterPolygon(double xSize, double ySize, double sizeFactor) {

		Polygon polygon = new Polygon();
		for (Coordinate coord : outerPolygon) {
			// add 300 to coord to move axis
			polygon.getPoints().add(sizeFactor * coord.getxCoord().doubleValue() + xSize / 2);
			// yCoord*-1 to invert to normal axis and add 700 to move axis
			polygon.getPoints().add(-1 * sizeFactor * coord.getyCoord().doubleValue() + ySize / 2);
		}

		return polygon;
	}

	// converting holes into polygons for UI
	public Polygon[] makeHoles(double xSize, double ySize, double sizeFactor) {
		Polygon[] polyHoles = new Polygon[nHoles];
		for (int i = 0; i < nHoles; i++) {
			polyHoles[i] = new Polygon();
			for (Coordinate coord : holes[i]) {
				polyHoles[i].getPoints().add(sizeFactor * coord.getxCoord().doubleValue() + xSize / 2);
				// yCoord*-1 to invert to normal axis
				polyHoles[i].getPoints().add(-1 * sizeFactor * coord.getyCoord().doubleValue() + ySize / 2);
			}
		}
		return polyHoles;
	}
	
	public void translate(BigDecimal x, BigDecimal y) {

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

	// print out the data of a polygon
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

	public Coordinate findBottomCoord() {
		Coordinate bottomCoord = outerPolygon[0];
		for (Coordinate coord : outerPolygon) {
			// if the y-value of coord is lower then the current bottomCoord,
			// replace bottomCoord
			if (coord.getyCoord().compareTo(bottomCoord.getyCoord())<0)
				bottomCoord = coord;
		}
		lowestCoord = bottomCoord;
		return bottomCoord;
	}

	public Coordinate findTopCoord() {
		Coordinate topCoord = outerPolygon[0];
		for (Coordinate coord : outerPolygon) {
			// if the y-value of coord is higher then the current topCoord,
			// replace topCoord
			if (coord.getyCoord().compareTo(topCoord.getyCoord())>0)
				topCoord = coord;
		}
		highestCoord = topCoord;
		return topCoord;
	}

	//the next method returns true if the polygon is clockwise
	public boolean checkClockwise(Coordinate[] polygon) {
		BigDecimal clockwiseValue = BigDecimal.ZERO;
		
		BigDecimal xDiff;
		BigDecimal ySum;
		
		//If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
		for(int i = 0; i < polygon.length; i++){
			if(i < polygon.length-1){
				//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
				xDiff = polygon[i+1].getxCoord().subtract(polygon[i].getxCoord());
				ySum = polygon[i+1].getyCoord().add(polygon[i].getyCoord());
				clockwiseValue = clockwiseValue.add(xDiff.multiply(ySum));
				
			}
			else{
				xDiff = polygon[0].getxCoord().subtract(polygon[i].getxCoord());
				ySum = polygon[0].getyCoord().add(polygon[i].getyCoord());
				clockwiseValue = clockwiseValue.add(xDiff.multiply(ySum));
			}
		}
		
		if(clockwiseValue.compareTo(BigDecimal.ZERO) > 0) return true;
		else return false;
	}
	
	public void changeClockOrientation(Coordinate[] polygon){
		Coordinate[] changedPolygon = new Coordinate[polygon.length];
		changedPolygon[0] = polygon[0];
		for(int i = 1; i < polygon.length; i++){
			changedPolygon[i] = polygon[polygon.length-i];
			System.out.println("placing " + polygon[polygon.length-i].toString() + "to location " + i);
		}
		for(int i = 0; i < polygon.length; i++){
			polygon[i] = changedPolygon[i];
		}
		createEdges();
	}

	public void printEdges() {
		
		for(Edge e: outerPolygonEdges){
			System.out.println(e.toString());
		}
		System.out.println();
	}
	
	private int getCoordinateIndexOf(Coordinate currentStartPoint) {
		int i = 0;
		for(Coordinate coord: outerPolygon){
			if(coord.equals(currentStartPoint))return i;
		}
		i++;
		return -1;
	}

	//-----------------------------------------------------------------------------------------------------------------------------
//	http://alienryderflex.com/polygon/

//  The function will return true if the point x,y is inside the polygon, or
//  false if it is not.  If the point is exactly on the edge of the polygon,
//  then the function may return true or false.

	public boolean pointInPolygon(Coordinate coord) {

		int polyCorners = outerPolygon.length;
		int i, j = polyCorners - 1;
		boolean oddNodes = false;

		for (i = 0; i < polyCorners; i++) {
			if ((outerPolygon[i].getyCoord().doubleValue() < coord.getyCoord().doubleValue() 
					&& outerPolygon[j].getyCoord().doubleValue() >= coord.getyCoord().doubleValue()
					|| outerPolygon[j].getyCoord().doubleValue() < coord.getyCoord().doubleValue()
							&& outerPolygon[i].getyCoord().doubleValue() >= coord.getyCoord().doubleValue())
					&& (outerPolygon[i].getxCoord().doubleValue() <= coord.getxCoord().doubleValue()
							|| outerPolygon[j].getxCoord().doubleValue() <= coord.getxCoord().doubleValue())) {
				// ^= is bitwise XOR assignement
				oddNodes ^= (outerPolygon[i].getxCoord().doubleValue() + (coord.getyCoord().doubleValue() - outerPolygon[i].getyCoord().doubleValue())
						/ (outerPolygon[j].getyCoord().doubleValue() - outerPolygon[i].getyCoord().doubleValue())
						* (outerPolygon[j].getxCoord().doubleValue() - outerPolygon[i].getxCoord().doubleValue()) < coord.getxCoord().doubleValue());
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
					if ((hole[i].getyCoord().doubleValue() < coord.getyCoord().doubleValue() && hole[j].getyCoord().doubleValue() >= coord.getyCoord().doubleValue()
							|| hole[j].getyCoord().doubleValue() < coord.getyCoord().doubleValue()
									&& hole[i].getyCoord().doubleValue() >= coord.getyCoord().doubleValue())
							&& (hole[i].getxCoord().doubleValue() <= coord.getxCoord().doubleValue()
									|| hole[j].getxCoord().doubleValue() <= coord.getxCoord().doubleValue())) {
						// ^= is bitwise XOR assignement
						inHole ^= (hole[i].getxCoord().doubleValue() + (coord.getyCoord().doubleValue() - hole[i].getyCoord().doubleValue())
								/ (hole[j].getyCoord().doubleValue() - hole[i].getyCoord().doubleValue())
								* (hole[j].getxCoord().doubleValue() - hole[i].getxCoord().doubleValue()) < coord.getxCoord().doubleValue());
					}
					j = i;
				}
				if(inHole) return false;
			}
		}
		return oddNodes;
	}

	public void replaceByNegative() {
		
		for(Coordinate coord: outerPolygon){
			coord.replaceByNegative();
		}
		for(Edge e: outerPolygonEdges){
			e.replaceByNegative();
		}
		
	}

	public void labelCounterClockwise() {
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

	public void calcDeltaAngles() {
		
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
	public void changeEdgeAnglesCounterClockwise() {
		
		for(Edge e : outerPolygonEdges){
			e.calcInverseEdgeAngle();
		}
		
	}

	public boolean overlapping(MultiPolygon polyA) {
		int f = 0;
		int k;
		boolean overlap = false;
		while(f < polyA.getOuterPolygonEdges().length && !overlap){
			Edge edgeA = polyA.getOuterPolygonEdges()[f];
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
							if(edgeB.getEndPoint().dFunction(edgeA).compareTo(BigDecimal.ZERO)>0)overlap = true;
						}
					}
				}
			}
		}
		return overlap;
		
	}
	
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
