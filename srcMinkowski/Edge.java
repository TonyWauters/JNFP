
/**
 * @author Stiaan Uyttersprot
 *
 */
public class Edge {
	private Coordinate startPoint;
	private Coordinate endPoint;
	private int edgeNumber;
	private double edgeAngle;
	private double deltaAngle;
	
	// values to be used for bounding box intersection
	private double smallX;
	private double bigX;
	private double smallY;
	private double bigY;
		
	private int edgeLabel; 
	private boolean polygonA;
	private boolean turningPoint;
	private boolean additional = false; //this edge is additional and is not used for track line trips
	
	private int tripSequenceNumber;
	
	private static double round = 1e-5;
	
	//values for boundarySearch
	private boolean positive;
	private boolean negative;
	private Coordinate startIntersect;
	private Coordinate endIntersect;
	
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
		edgeAngle =edge.getEdgeAngle();
		deltaAngle = edge.getDeltaAngle();
		turningPoint = edge.isTurningPoint();
		polygonA = edge.isPolygonA();
		calculateRanges();
	}
	
	public Edge(Edge edge, boolean add) {
		startPoint = new Coordinate(edge.getStartPoint());
		endPoint = new Coordinate(edge.getEndPoint());
		edgeNumber = edge.getEdgeNumber();
		edgeAngle =edge.getEdgeAngle();
		deltaAngle = edge.getDeltaAngle();
		turningPoint = edge.isTurningPoint();
		polygonA = edge.isPolygonA();
		additional = add;
		calculateRanges();
	}

	public Edge(Coordinate s, Coordinate e) {
		startPoint = s;
		endPoint = e;
		edgeNumber = -1;
		calculateRanges();
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

	public int getEdgeNumber() {
		return edgeNumber;
	}

	public void setEdgeNumber(int edgeNumber) {
		this.edgeNumber = edgeNumber;
	}

	public void print() {
		System.out.println(startPoint.toString() + ";" + endPoint.toString());
	}

	public double getEdgeAngle() {
		
		return edgeAngle;
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
	
	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}

	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}

	public Coordinate getStartIntersect() {
		return startIntersect;
	}

	public void setStartIntersect(Coordinate startIntersect) {
		this.startIntersect = startIntersect;
	}

	public Coordinate getEndIntersect() {
		return endIntersect;
	}

	public void setEndIntersect(Coordinate endIntersect) {
		this.endIntersect = endIntersect;
	}

	public void calcEdgeAngle(){
		edgeAngle = Math.atan2(endPoint.getyCoord()-startPoint.getyCoord(), endPoint.getxCoord()- startPoint.getxCoord());
		
	}
	
	public void calcInverseEdgeAngle() {
		edgeAngle = Math.atan2(startPoint.getyCoord() - endPoint.getyCoord(), startPoint.getxCoord() - endPoint.getxCoord());
		
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
		
	public double calcClockwiseValue() {
		
		//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
		double xDiff = endPoint.getxCoord() - startPoint.getxCoord();
		double ySum = endPoint.getyCoord() + startPoint.getyCoord();
		
		return xDiff*ySum;
	}

	public boolean edgesOrientatedRight(Edge preEdge, Edge postEdge) {
		//the edges are right of or parallel with this edge 
		if(preEdge.getStartPoint().dFunction(startPoint, endPoint)<=round && 
				postEdge.getEndPoint().dFunction(startPoint, endPoint) <= round)
			return true;
		return false;
	}

	public void setEdgeAngle(double edgeAngle) {
		this.edgeAngle = edgeAngle;
	}

	public int getEdgeLabel() {
		return edgeLabel;
	}

	public void setEdgeLabel(int edgeLabel) {
		this.edgeLabel = edgeLabel;
	}

	public double getDeltaAngle() {
		return deltaAngle;
	}

	public void setDeltaAngle(double deltaAngle) {
		this.deltaAngle = deltaAngle;
	}

	public boolean isTurningPoint() {
		return turningPoint;
	}

	public void setTurningPoint(boolean turningPoint) {
		this.turningPoint = turningPoint;
	}

	@Override
	public String toString() {
		return "Edge [startPoint=" + startPoint + ", endPoint=" + endPoint + ", edgeNumber=" + edgeNumber
				+ ", edgeAngle=" + edgeAngle + ", deltaAngle=" + deltaAngle + ", turningPoint=" + turningPoint +", polygon A=" + polygonA + "]";
	}

	public boolean isPolygonA() {
		return polygonA;
	}

	public void setPolygonA(boolean polygonA) {
		this.polygonA = polygonA;
	}

	public void replaceByNegative() {
		startPoint.replaceByNegative();
		endPoint.replaceByNegative();
		if(edgeAngle>0){
			edgeAngle-=Math.PI;
		}
		else{
			edgeAngle+=Math.PI;
		}
	}

	public void changeEdgeNumber(int direction) {
		
		edgeNumber = direction*edgeNumber;
		
	}

	public boolean testIntersect(Edge edge) {
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
	
	public boolean testIntersectWithoutBorders(Edge edge) {
		Coordinate intersectionCoord;
		boolean intersection = false;

		// if the bounding boxes intersect, line intersection
		// has to be checked
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
	
	public void calculateRanges() {

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
		if(onLine == false){
			return false;
		};
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
	
	public boolean equals(Edge edge) {
		if(edgeNumber!=edge.getEdgeNumber())return false;
		if(polygonA!=edge.isPolygonA())return false;
		return true;
	}
	//we need to check the coordinates here, they have to be the same too, not only the edgeNumber and polygon
	public boolean equalsComplexPolyEdge(Edge edge) {
		if(edgeNumber!=edge.getEdgeNumber())return false;
		if(polygonA!=edge.isPolygonA())return false;
		if(!startPoint.equalValuesRounded(edge.getStartPoint()))return false;
		if(!endPoint.equalValuesRounded(edge.getEndPoint()))return false;
		return true;
	}

	public Vector makeFullVector(int eN) {

		Vector vector;
		// if the orbiting edge is being used for the vector, it needs to be
		// inversed
		// this means startPoint-endPoint instead of endPoint-startPoint
		vector = new Vector(endPoint.subtract(startPoint), eN, polygonA);
		if(eN<0){
			vector.setxCoord(-vector.getxCoord());
			vector.setyCoord(-vector.getyCoord());
		}

		return vector;

	}

	public Coordinate getMiddlePointEdge() {
		double midxCoord = (startPoint.getxCoord()+endPoint.getxCoord())/2;
		double midyCoord = (startPoint.getyCoord()+endPoint.getyCoord())/2;
		return new Coordinate(midxCoord, midyCoord);
	}
	
	public boolean equalValuesRounded(Edge edge) {
		if(!startPoint.equalValuesRounded(edge.getEndPoint()))return false;
		if(!endPoint.equalValuesRounded(edge.getEndPoint()))return false;
		return true;
	}

	public boolean isAdditional() {
		return additional;
	}

	public void setAdditional(boolean additional) {
		this.additional = additional;
	}

	public int getTripSequenceNumber() {
		return tripSequenceNumber;
	}

	public void setTripSequenceNumber(int tripSequenceNumber) {
		this.tripSequenceNumber = tripSequenceNumber;
	}
}
