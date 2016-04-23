package NFPGenerator;

/**
 *	The Edge class describes an edge given by a start- and endpoint
 *	it can be used to calculate various geometric aspects
 *	
 * @author Stiaan Uyttersprot
 */
public class Edge {

	private boolean stationary = false;

	private Coordinate startPoint;
	private Coordinate endPoint;
	private int edgeNumber;

	private boolean traversed = false;

	protected static double round = 1e-4;

	private int edgeLabel;
	private boolean polygonA;
	private boolean turningPoint;
	private boolean additional = false; // this edge is additional and is not
										// used for track line trips
	private double edgeAngle;
	private double deltaAngle;
	private int tripSequenceNumber;

	// values for boundarySearch
	private boolean positive;
	private boolean negative;
	private Coordinate startIntersect;
	private Coordinate endIntersect;

	// values to be used for bounding box intersection
	private double smallX;
	private double bigX;
	private double smallY;
	private double bigY;

	protected Edge(Coordinate s, Coordinate e, int eN) {
		startPoint = s;
		endPoint = e;
		edgeNumber = eN;
		calculateRanges();
	}

	/**
	 * Make a deep copy of an edge
	 * @param edge the edge to copy
	 */
	public Edge(Edge edge) {
		startPoint = new Coordinate(edge.getStartPoint());
		endPoint = new Coordinate(edge.getEndPoint());
		edgeNumber = edge.getEdgeNumber();
		edgeAngle = edge.getEdgeAngle();
		deltaAngle = edge.getDeltaAngle();
		turningPoint = edge.isTurningPoint();
		polygonA = edge.isPolygonA();
		calculateRanges();
	}

	protected Edge(Edge edge, boolean add) {
		startPoint = new Coordinate(edge.getStartPoint());
		endPoint = new Coordinate(edge.getEndPoint());
		edgeNumber = edge.getEdgeNumber();
		edgeAngle = edge.getEdgeAngle();
		deltaAngle = edge.getDeltaAngle();
		turningPoint = edge.isTurningPoint();
		polygonA = edge.isPolygonA();
		additional = add;
		calculateRanges();
	}

	/**
	 * Create an edge with a given start- and endpoint
	 * @param startPoint the startpoint of the edge
	 * @param endPoint the endpoint of the edge
	 */
	public Edge(Coordinate startPoint, Coordinate endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		edgeNumber = -1;
		calculateRanges();
	}

	protected boolean isStationary() {
		return stationary;
	}

	protected void setStationary(boolean stationary) {
		this.stationary = stationary;
	}

	/**
	 * @return the startpoint of the edge
	 */
	public Coordinate getStartPoint() {
		return startPoint;
	}


	protected void setStartPoint(Coordinate startPoint) {
		this.startPoint = startPoint;
	}

	/**
	 * @return the endpoint of the edge
	 */
	public Coordinate getEndPoint() {
		return endPoint;
	}

	protected void setEndPoint(Coordinate endPoint) {
		this.endPoint = endPoint;
	}

	protected double getSmallX() {
		return smallX;
	}

	protected void setSmallX(double smallX) {
		this.smallX = smallX;
	}

	protected double getBigX() {
		return bigX;
	}

	protected void setBigX(double bigX) {
		this.bigX = bigX;
	}

	protected double getSmallY() {
		return smallY;
	}

	protected void setSmallY(double smallY) {
		this.smallY = smallY;
	}

	protected double getBigY() {
		return bigY;
	}

	protected void setBigY(double bigY) {
		this.bigY = bigY;
	}

	protected int getEdgeNumber() {
		return edgeNumber;
	}

	protected void setEdgeNumber(int edgeNumber) {
		this.edgeNumber = edgeNumber;
	}

	protected int getEdgeLabel() {
		return edgeLabel;
	}

	protected void setEdgeLabel(int edgeLabel) {
		this.edgeLabel = edgeLabel;
	}

	protected boolean isPolygonA() {
		return polygonA;
	}

	protected void setPolygonA(boolean polygonA) {
		this.polygonA = polygonA;
	}

	protected boolean isTurningPoint() {
		return turningPoint;
	}

	protected void setTurningPoint(boolean turningPoint) {
		this.turningPoint = turningPoint;
	}

	protected boolean isAdditional() {
		return additional;
	}

	protected void setAdditional(boolean additional) {
		this.additional = additional;
	}

	protected double getEdgeAngle() {
		return edgeAngle;
	}

	protected void setEdgeAngle(double edgeAngle) {
		this.edgeAngle = edgeAngle;
	}

	protected double getDeltaAngle() {
		return deltaAngle;
	}

	protected void setDeltaAngle(double deltaAngle) {
		this.deltaAngle = deltaAngle;
	}

	protected int getTripSequenceNumber() {
		return tripSequenceNumber;
	}

	protected void setTripSequenceNumber(int tripSequenceNumber) {
		this.tripSequenceNumber = tripSequenceNumber;
	}

	protected boolean isPositive() {
		return positive;
	}

	protected void setPositive(boolean positive) {
		this.positive = positive;
	}

	protected boolean isNegative() {
		return negative;
	}

	protected void setNegative(boolean negative) {
		this.negative = negative;
	}

	protected Coordinate getStartIntersect() {
		return startIntersect;
	}

	protected void setStartIntersect(Coordinate startIntersect) {
		this.startIntersect = startIntersect;
	}

	protected Coordinate getEndIntersect() {
		return endIntersect;
	}

	protected void setEndIntersect(Coordinate endIntersect) {
		this.endIntersect = endIntersect;
	}

	protected void calcEdgeAngle() {
		edgeAngle = Math.atan2(endPoint.getyCoord() - startPoint.getyCoord(),
				endPoint.getxCoord() - startPoint.getxCoord());

	}

	protected void calcInverseEdgeAngle() {
		edgeAngle = Math.atan2(startPoint.getyCoord() - endPoint.getyCoord(),
				startPoint.getxCoord() - endPoint.getxCoord());

	}

	@Override
	public String toString() {
		return "Edge [ startPoint=" + startPoint + ", endPoint=" + endPoint + "]";
	}

	protected void calculateRanges() {

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

	protected TouchingEdgePair touching(Edge orbEdge) {

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

	private boolean containsRounded(Coordinate coord) {

		boolean containsX = false;
		boolean containsY = false;

		// check x
		// coordinate-----------------------------------------------------------------------------------------------------
		if (startPoint.getxCoord() < endPoint.getxCoord() + round) {
			if (startPoint.getxCoord() <= coord.getxCoord() + round
					&& endPoint.getxCoord() >= coord.getxCoord() - round)
				containsX = true;
		} else if (startPoint.getxCoord() >= coord.getxCoord() - round
				&& endPoint.getxCoord() <= coord.getxCoord() + round)
			containsX = true;

		// check
		// y-coordinate-----------------------------------------------------------------------------------------------------
		if (startPoint.getyCoord() < endPoint.getyCoord() + round) {
			if (startPoint.getyCoord() <= coord.getyCoord() + round
					&& endPoint.getyCoord() >= coord.getyCoord() - round)
				containsY = true;
		} else if (startPoint.getyCoord() >= coord.getyCoord() - round
				&& endPoint.getyCoord() <= coord.getyCoord() + round)
			containsY = true;

		return containsX && containsY;
	}

	// if the vector will be created from the whole edge
	protected Vector makeFullVector(int eN) {

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
	
	protected Vector makeVectorForMink(int eN) {

		Vector vector;
		
		vector = new Vector(endPoint.subtract(startPoint), eN, polygonA);
		if(eN<0){
			vector.setxCoord(-vector.getxCoord());
			vector.setyCoord(-vector.getyCoord());
		}

		return vector;

	}

	protected Vector makePartialVector(Coordinate touchPoint, int eN) {
		Vector vector;
		// if the orbiting edge is being used for the vector, it needs to be
		// inversed
		// this means startPoint-endPoint in stead of endPoint-startPoint
		if (!stationary)
			// TODO:the edgenumber from the orbiting edge may be wrong and cause
			// errors
			vector = new Vector(touchPoint.subtract(endPoint), eN, stationary);
		else {
			vector = new Vector(endPoint.subtract(touchPoint), eN, stationary);
		}

		return vector;
	}

	
	/**
	 * Calculates the angle of this edge
	 * @return the angle in radians
	 */
	public double getAngle() {

		Vector vector = new Vector(endPoint.subtract(startPoint), edgeNumber, stationary);

		return vector.getVectorAngle();
	}

	
	/**
	 * Checks if the bounding boxes of two edges overlap.
	 * Can be used to quickly see if two edges can't possibly intersect (if false)
	 * @param edge the edge to check with
	 * @return if the bounding boxes overlap or not
	 */
	public boolean boundingBoxIntersect(Edge edge) {

		boolean intersect = true;

		if (edge.getBigX() <= smallX - round || edge.getSmallX() >= bigX + round || edge.getBigY() <= smallY - round
				|| edge.getSmallY() >= bigY + round)
			intersect = false;

		return intersect;
	}

	/**
	 * checks if two lines intersect by using the D-function
	 * @param testEdge the edge to check with
	 * @return if the two lines intersect or not
	 */
	public boolean lineIntersect(Edge testEdge) {
		boolean intersect = true;
		// the lines intersect if the start coordinate and the end coordinate
		// of one of the edges are not both on the same side
		// in most cases this will guarantee an intersection, but there are
		// cases where the intersection point will not be part of one of the
		// lines
		if (testEdge.getStartPoint().dFunction(startPoint, endPoint) <= round
				&& testEdge.getEndPoint().dFunction(startPoint, endPoint) <= round) {
			intersect = false;
		} else if (testEdge.getStartPoint().dFunction(startPoint, endPoint) >= -round
				&& testEdge.getEndPoint().dFunction(startPoint, endPoint) >= -round) {
			intersect = false;
		}

		return intersect;
	}

	/**
	 * Calculates the intersectionpoint of this edge and the given edge
	 * @param testEdge the edge that is intersecting
	 * @return the intersection Point of the two edges
	 */
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

	// when a translation is taking place, the values of min and max have to be
	// adjusted
	protected void changeRangeValues(double x, double y) {
		smallX += x;
		bigX += x;
		smallY += y;
		bigY += y;

	}

	protected boolean containsIntersectionPoint(Coordinate intersectionCoord) {
		if (intersectionCoord.getxCoord() < smallX - round) {
			return false;
		}
		if (intersectionCoord.getxCoord() > bigX + round) {
			return false;
		}
		if (intersectionCoord.getyCoord() < smallY - round) {
			return false;
		}
		if (intersectionCoord.getyCoord() > bigY + round) {
			return false;
		}
		return true;
	}

	/**
	 * Check if a point is on the edge
	 * @param coord the coordinate that may be on the edge
	 * @return if the point given is on the edge or not
	 */
	public boolean containsPoint(Coordinate coord) {
		boolean onLine;
		onLine = coord.dFunctionCheck(startPoint, endPoint);
		if (onLine == false)
			return false;
		if (coord.getxCoord() < smallX - round) {
			return false;
		}
		if (coord.getxCoord() > bigX + round) {
			return false;
		}
		if (coord.getyCoord() < smallY - round) {
			return false;
		}
		if (coord.getyCoord() > bigY + round) {
			return false;
		}
		return true;
	}

	protected double calcClockwiseValue() {

		// Sum over the edges, (x2-x1)(y2+y1). If the result is positive the
		// curve is clockwise, if it's negative the curve is counter-clockwise.
		double xDiff = endPoint.getxCoord() - startPoint.getxCoord();
		double ySum = endPoint.getyCoord() + startPoint.getyCoord();

		return xDiff * ySum;
	}

	protected void markTraversed() {

		traversed = true;

	}

	protected boolean isTraversed() {
		return traversed;
	}

	protected void setTraversed(boolean traversed) {
		this.traversed = traversed;
	}

	/**
	 * See if the two edges have an intersection point in between their borders,
	 * using bounding box test, line intersection 
	 * and checking if that intersection point is contained
	 * 
	 * @param edge the edge to check
	 * @return if the edges intersect or not
	 */
	public boolean testIntersect(Edge edge) {
		Coordinate intersectionCoord;
		boolean intersection = false;

		// if the bounding boxes intersect, line intersection
		// has to be checked and the edge may need to be trimmed
		if (boundingBoxIntersect(edge)) {
			// TODO: line intersection, trim vector to that
			// distance
			if (lineIntersect(edge)) {
				intersectionCoord = calcIntersection(edge);
				// intersectionCoord.roundCoord();
				if (containsIntersectionPoint(intersectionCoord) && edge.containsIntersectionPoint(intersectionCoord)) {
					if (intersectionCoord.equalValuesRounded(edge.getStartPoint())
							|| intersectionCoord.equalValuesRounded(edge.getEndPoint())
							|| intersectionCoord.equalValuesRounded(startPoint)
							|| intersectionCoord.equalValuesRounded(endPoint)) {

					} else
						intersection = true;
				}
			}

		}
		return intersection;
	}
	
	protected boolean testIntersectMink(Edge edge) {
		Coordinate intersectionCoord;
		boolean intersection = false;

		// if the bounding boxes intersect, line intersection
		// has to be checked
		if (boundingBoxIntersect(edge)) {
			if (lineIntersect(edge)) {
				intersectionCoord = calcIntersection(edge);
				
				if(containsIntersectionPoint(intersectionCoord)&&edge.containsIntersectionPoint(intersectionCoord)){
					
					intersection = true;
				}
			}
			
		}
		return intersection;
	}

	protected boolean testIntersectWithoutBorders(Edge edge) {
		Coordinate intersectionCoord;
		boolean intersection = false;

		// if the bounding boxes intersect, line intersection
		// has to be checked
		if (boundingBoxIntersect(edge)) {
			if (lineIntersect(edge)) {
				intersectionCoord = calcIntersection(edge);

				if (containsIntersectionPoint(intersectionCoord) && edge.containsIntersectionPoint(intersectionCoord)) {

					if (intersectionCoord.equalValuesRounded(edge.getStartPoint())
							|| intersectionCoord.equalValuesRounded(edge.getEndPoint())
							|| intersectionCoord.equalValuesRounded(startPoint)
							|| intersectionCoord.equalValuesRounded(endPoint)) {
					} else
						intersection = true;
				}
			}

		}
		return intersection;
	}

	
	/**
	 * Calculates the middle point of an edge
	 * @return the middle Point of the edge
	 */
	public Coordinate getMiddlePointEdge() {
		double midxCoord = (startPoint.getxCoord() + endPoint.getxCoord()) / 2;
		double midyCoord = (startPoint.getyCoord() + endPoint.getyCoord()) / 2;
		return new Coordinate(midxCoord, midyCoord);
	}

	protected boolean edgesOrientatedRight(Edge preEdge, Edge postEdge) {
		// the edges are right of or parallel with this edge
		if (preEdge.getStartPoint().dFunction(startPoint, endPoint) <= round
				&& postEdge.getEndPoint().dFunction(startPoint, endPoint) <= round)
			return true;
		return false;
	}

	// we need to check the coordinates here, they have to be the same too, not
	// only the edgeNumber and polygon
	protected boolean equalsComplexPolyEdge(Edge edge) {
		if (edgeNumber != edge.getEdgeNumber())
			return false;
		if (polygonA != edge.isPolygonA())
			return false;
		if (!startPoint.equalValuesRounded(edge.getStartPoint()))
			return false;
		if (!endPoint.equalValuesRounded(edge.getEndPoint()))
			return false;
		return true;
	}

	protected void replaceByNegative() {
		startPoint.replaceByNegative();
		endPoint.replaceByNegative();
		if (edgeAngle > 0) {
			edgeAngle -= Math.PI;
		} else {
			edgeAngle += Math.PI;
		}
	}

	protected void changeEdgeNumber(int direction) {

		edgeNumber = direction * edgeNumber;

	}
}
