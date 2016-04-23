
/**
 *
 * @author Stiaan Uyttersprot
 */
public class Edge {
	private boolean stationary = false;
	private Coordinate startPoint;
	private Coordinate endPoint;
	private int edgeNumber;
	private boolean traversed = false;
	
	public static double round = 1e-4;

	// values to be used for bounding box intersection
	private double smallX;
	private double bigX;
	private double smallY;
	private double bigY;

	Edge(Coordinate s, Coordinate e, int eN) {
		startPoint = s;
		endPoint = e;
		edgeNumber = eN;
		calculateRanges();
	}

	public Edge(Edge edge) {
		startPoint = new Coordinate(edge.getStartPoint());
		endPoint = new Coordinate(edge.getEndPoint());
		edgeNumber = edge.getEdgeNumber();
		calculateRanges();
	}

	public Edge(Coordinate s, Coordinate e) {
		startPoint = s;
		endPoint = e;
		edgeNumber = -1;
		calculateRanges();
	}

	public boolean isStationary() {
		return stationary;
	}

	public void setStationary(boolean stationary) {
		this.stationary = stationary;
	}

	public Coordinate getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Coordinate startPoint) {
		this.startPoint = startPoint;
	}

	public Coordinate getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Coordinate endPoint) {
		this.endPoint = endPoint;
	}

	public double getSmallX() {
		return smallX;
	}

	public void setSmallX(double smallX) {
		this.smallX = smallX;
	}

	public double getBigX() {
		return bigX;
	}

	public void setBigX(double bigX) {
		this.bigX = bigX;
	}

	public double getSmallY() {
		return smallY;
	}

	public void setSmallY(double smallY) {
		this.smallY = smallY;
	}

	public double getBigY() {
		return bigY;
	}

	public void setBigY(double bigY) {
		this.bigY = bigY;
	}

	public int getEdgeNumber() {
		return edgeNumber;
	}

	public void setEdgeNumber(int edgeNumber) {
		this.edgeNumber = edgeNumber;
	}

	@Override
	public String toString() {
		return "Edge [ nr=" + edgeNumber + " startPoint=" + startPoint + ", endPoint=" + endPoint + ", smallX="
				+ smallX + ", bigX=" + bigX + ", smallY=" + smallY + ", bigY=" + bigY + " traversed= "+ traversed + "]";
	}

	public void print() {
		System.out.println(startPoint.toString() + ";" + endPoint.toString());
	}

	private void calculateRanges() {

		Coordinate start = getStartPoint();
		Coordinate end = getEndPoint();

		if (start.getxCoord() < end.getxCoord()) {
			smallX = start.getxCoord();
			bigX = end.getxCoord();
		} else {
			smallX = end.getxCoord();
			bigX = start.getxCoord();
		}

		if (start.getyCoord() < end.getyCoord()) {
			smallY = start.getyCoord();
			bigY = end.getyCoord();
		} else {
			smallY = end.getyCoord();
			bigY = start.getyCoord();
		}
	}

	public TouchingEdgePair touching(Edge orbEdge) {
		
		if (startPoint.dFunctionCheck(orbEdge.getStartPoint(), orbEdge.getEndPoint())) {
			if (orbEdge.containsRounded(startPoint)) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, startPoint);
				return tEP;
			}
		}
		if (endPoint.dFunctionCheck(orbEdge.getStartPoint(), orbEdge.getEndPoint())) {
			if (orbEdge.containsRounded(endPoint)) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, endPoint);
				return tEP;
			}
		}
		if (orbEdge.getStartPoint().dFunctionCheck(startPoint, endPoint)) {
			if (containsRounded(orbEdge.getStartPoint())) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getStartPoint());
				return tEP;
			}
		}
		if (orbEdge.getEndPoint().dFunctionCheck(startPoint, endPoint)) {
			if (containsRounded(orbEdge.getEndPoint())) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getEndPoint());
				return tEP;
			}
		}

		return null;
	}
	
	
	
	public TouchingEdgePair touchingV2(Edge orbEdge){
		//first look if the start or end points are equal, if this is the case they are certainly touching
		if (startPoint.equals(orbEdge.startPoint)){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, startPoint);
			return tEP;
		}
		if (startPoint.equals(orbEdge.endPoint)){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, startPoint);
			return tEP;
		}
		if (endPoint.equals(orbEdge.startPoint)){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, endPoint);
			return tEP;
		}
		if (endPoint.equals(orbEdge.endPoint)){
			TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, endPoint);
			return tEP;
		}
		//after checking those points, check if it is somewhere in between start and end
		
		if (startPoint.dFunctionCheck(orbEdge.getStartPoint(), orbEdge.getEndPoint())) {
			if (orbEdge.contains(startPoint)) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, startPoint);
				return tEP;
			}
		}
		if (endPoint.dFunctionCheck(orbEdge.getStartPoint(), orbEdge.getEndPoint())) {
			if (orbEdge.contains(endPoint)) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, endPoint);
				return tEP;
			}
		}
		if (orbEdge.getStartPoint().dFunctionCheck(startPoint, endPoint)) {
			if (contains(orbEdge.getStartPoint())) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getStartPoint());
				return tEP;
			}
		}
		if (orbEdge.getEndPoint().dFunctionCheck(startPoint, endPoint)) {
			if (contains(orbEdge.getEndPoint())) {
				TouchingEdgePair tEP = new TouchingEdgePair(this, orbEdge, orbEdge.getEndPoint());
				return tEP;
			}
		}

		return null;
	}

	private boolean contains(Coordinate coord) {

		boolean containsX = false;
		boolean containsY = false;
		// check x
		// coordinate-----------------------------------------------------------------------------------------------------
		if (startPoint.getxCoord() < endPoint.getxCoord()) {
			if (startPoint.getxCoord() <= coord.getxCoord() && endPoint.getxCoord() >= coord.getxCoord())
				containsX = true;
		} else if (startPoint.getxCoord() >= coord.getxCoord() && endPoint.getxCoord() <= coord.getxCoord())
			containsX = true;

		// check
		// y-coordinate-----------------------------------------------------------------------------------------------------
		if (startPoint.getyCoord() < endPoint.getyCoord()) {
			if (startPoint.getyCoord() <= coord.getyCoord() && endPoint.getyCoord() >= coord.getyCoord())
				containsY = true;
		} else if (startPoint.getyCoord() >= coord.getyCoord() && endPoint.getyCoord() <= coord.getyCoord())
			containsY = true;

		return containsX && containsY;
	}
	
	private boolean containsRounded(Coordinate coord) {
		
		boolean containsX = false;
		boolean containsY = false;
		
		// check x
		// coordinate-----------------------------------------------------------------------------------------------------
		if (startPoint.getxCoord() < endPoint.getxCoord()+round) {
			if (startPoint.getxCoord() <= coord.getxCoord()+round && endPoint.getxCoord() >= coord.getxCoord()-round)
				containsX = true;
		} else if (startPoint.getxCoord() >= coord.getxCoord()-round && endPoint.getxCoord() <= coord.getxCoord()+round)
			containsX = true;

		// check
		// y-coordinate-----------------------------------------------------------------------------------------------------
		if (startPoint.getyCoord() < endPoint.getyCoord()+round) {
			if (startPoint.getyCoord() <= coord.getyCoord()+round && endPoint.getyCoord() >= coord.getyCoord()-round)
				containsY = true;
		} else if (startPoint.getyCoord() >= coord.getyCoord()-round && endPoint.getyCoord() <= coord.getyCoord()+round)
			containsY = true;

		return containsX && containsY;
	}

	// if the vector will be created from the whole edge
	public Vector makeFullVector(int eN) {

		Vector vector;
		// if the orbiting edge is being used for the vector, it needs to be
		// inversed
		// this means startPoint-endPoint in stead of endPoint-startPoint
		if (!stationary)
			vector = new Vector(startPoint.subtract(endPoint), eN, stationary);
		else {
			vector = new Vector(endPoint.subtract(startPoint), eN, stationary);
		}
		
		return vector;

	}

	public Vector makePartialVector(Coordinate touchPoint, int eN) {
		Vector vector;
		// if the orbiting edge is being used for the vector, it needs to be
		// inversed
		// this means startPoint-endPoint in stead of endPoint-startPoint
		if (!stationary)
			//TODO:the edgenumber from the orbiting edge may be wrong and cause errors
			vector = new Vector(touchPoint.subtract(endPoint), eN, stationary);
		else {
			vector = new Vector(endPoint.subtract(touchPoint), eN, stationary);
		}

		return vector;
	}

	public double getAngle() {
		// we can't use the method makeFullVector, this will reverse the vector
		// if it's from the orbiting polygon
		Vector vector = new Vector(endPoint.subtract(startPoint), edgeNumber, stationary);
		
		return vector.getVectorAngle();
	}

	public boolean boundingBoxIntersect(Edge edge) {

		boolean intersect = true;

		if (edge.getBigX() <= smallX-round|| edge.getSmallX() >= bigX+round || edge.getBigY() <= smallY-round
				|| edge.getSmallY() >= bigY+round)
			intersect = false;

		return intersect;
	}

	public boolean lineIntersect(Edge testEdge) {
		boolean intersect = true;
		// the lines intersect if the start coordinate and the end coordinate
		// of one of the edges are not both on the same side
		//in most cases this will guarantee an intersection, but there are cases where the intersection point will not be part of one of the lines
		if (testEdge.getStartPoint().dFunction(startPoint, endPoint) <= round
				&& testEdge.getEndPoint().dFunction(startPoint, endPoint) <= round) {
			intersect = false;
		} else if (testEdge.getStartPoint().dFunction(startPoint, endPoint) >= -round
				&& testEdge.getEndPoint().dFunction(startPoint, endPoint) >= -round) {
			intersect = false;
		}

		return intersect;
	}

	public Coordinate calcIntersection(Edge testEdge) {
		/*
		 * the used formula is
		 * x=((x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4))/((x1-x2)*(y3-y4)-(y1-
		 * y2)*(x3-x4));
		 * y=((x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4))/((x1-x2)*(y3-y4)-(y1-
		 * y2)*(x3-x4));
		 */

		double x1 = startPoint.getxCoord();
		double x2 = endPoint.getxCoord();
		double y1 = startPoint.getyCoord();
		double y2 = endPoint.getyCoord();

		double x3 = testEdge.getStartPoint().getxCoord();
		double x4 = testEdge.getEndPoint().getxCoord();
		double y3 = testEdge.getStartPoint().getyCoord();
		double y4 = testEdge.getEndPoint().getyCoord();

		// x1 - x2
		double dx1 = x1 - x2;
		// x3 - x4
		double dx2 = x3 - x4;
		// y1 - y2
		double dy1 = y1 - y2;
		// y3 - y4
		double dy2 = y3 - y4;

		// (x1*y2-y1*x2)
		double pd1 = x1 * y2 - y1 * x2;
		// (x3*y4-y3*x4)
		double pd2 = x3 * y4 - y3 * x4;

		// (x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4)
		double xNumerator = pd1 * dx2 - dx1 * pd2;
		// (x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4)
		double yNumerator = pd1 * dy2 - dy1 * pd2;

		// (x1-x2)*(y3-y4)-(y1-y2)*(x3-x4)
		double denominator = dx1 * dy2 - dy1 * dx2;

		double xCoord = xNumerator / denominator;
		double yCoord = yNumerator / denominator;

		return new Coordinate(xCoord, yCoord);
	}

	//when a translation is taking place, the values of min and max have to be adjusted
	public void changeRangeValues(double x, double y) {
		smallX += x;
		bigX += x;
		smallY += y;
		bigY += y;
		
	}

	public boolean containsIntersectionPoint(Coordinate intersectionCoord) {
		if(intersectionCoord.getxCoord()<smallX-round){
			return false;
		}
		if(intersectionCoord.getxCoord()>bigX+round){
			return false;
		}
		if(intersectionCoord.getyCoord()<smallY-round){
			return false;
		}
		if(intersectionCoord.getyCoord()>bigY+round){
			return false;
		}
		return true;
	}
	
	public boolean containsPoint(Coordinate intersectionCoord) {
		boolean onLine;
		onLine = intersectionCoord.dFunctionCheck(startPoint, endPoint);
		if(onLine == false)return false;
		if(intersectionCoord.getxCoord()<smallX-round){
			return false;
		}
		if(intersectionCoord.getxCoord()>bigX+round){
			return false;
		}
		if(intersectionCoord.getyCoord()<smallY-round){
			return false;
		}
		if(intersectionCoord.getyCoord()>bigY+round){
			return false;
		}
		return true;
	}

	public double calcClockwiseValue() {
		
		//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
		double xDiff = endPoint.getxCoord() - startPoint.getxCoord();
		double ySum = endPoint.getyCoord() + startPoint.getyCoord();
		
		return xDiff*ySum;
	}

	public void markTraversed() {
		
		traversed = true;
		
	}

	public boolean isTraversed() {
		return traversed;
	}

	public void setTraversed(boolean traversed) {
		this.traversed = traversed;
	}

	public boolean testIntersect(Edge edge) {
		Coordinate intersectionCoord;
		boolean intersection = false;

		// if the bounding boxes intersect, line intersection
		// has to be checked and the edge may need to be trimmed
		if (boundingBoxIntersect(edge)) {
			if (lineIntersect(edge)) {
				intersectionCoord = calcIntersection(edge);
				if(containsIntersectionPoint(intersectionCoord)&&edge.containsIntersectionPoint(intersectionCoord)){
					if(intersectionCoord.equalValuesRounded(edge.getStartPoint())||intersectionCoord.equalValuesRounded(edge.getEndPoint())
							||intersectionCoord.equalValuesRounded(startPoint)||intersectionCoord.equalValuesRounded(endPoint)){
						
					}
					else intersection = true;
				}
			}
			
		}
		return intersection;
	}

	public Coordinate getMiddlePointEdge() {
		double midxCoord = (startPoint.getxCoord()+endPoint.getxCoord())/2;
		double midyCoord = (startPoint.getyCoord()+endPoint.getyCoord())/2;
		return new Coordinate(midxCoord, midyCoord);
	}

	public boolean edgesOrientatedRight(Edge preEdge, Edge postEdge) {
		//the edges are right of or parallel with this edge 
		if(preEdge.getStartPoint().dFunction(startPoint, endPoint)<=round && 
				postEdge.getEndPoint().dFunction(startPoint, endPoint) <= round)
			return true;
		return false;
	}
	
	public void replaceByNegative() {
		startPoint.replaceByNegative();
		endPoint.replaceByNegative();
	}
}
