import java.math.BigDecimal;
import java.math.MathContext;


/**
 *
 * @author  Stiaan Uyttersprot
 */
public class Edge {
	private Coordinate startPoint;
	private Coordinate endPoint;
	private int edgeNumber;
	private double edgeAngle;
	private double deltaAngle;
	
	// values to be used for bounding box intersection
	private BigDecimal smallX;
	private BigDecimal bigX;
	private BigDecimal smallY;
	private BigDecimal bigY;
		
	private int edgeLabel; //wordt momenteel niet gebruikt
	private boolean polygonA;
	private boolean turningPoint;
	private boolean additional = false; //this edge is additional and is not used for track line trips
	
	private int tripSequenceNumber;
	
	public static BigDecimal round = new BigDecimal(0);
	private static MathContext mc = MathContext.DECIMAL128;
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

	public BigDecimal getSmallX() {
		return smallX;
	}

	public void setSmallX(BigDecimal smallX) {
		this.smallX = smallX;
	}

	public BigDecimal getBigX() {
		return bigX;
	}

	public void setBigX(BigDecimal bigX) {
		this.bigX = bigX;
	}

	public BigDecimal getSmallY() {
		return smallY;
	}

	public void setSmallY(BigDecimal smallY) {
		this.smallY = smallY;
	}

	public BigDecimal getBigY() {
		return bigY;
	}

	public void setBigY(BigDecimal bigY) {
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
		double numerator = endPoint.getyCoord().doubleValue()- startPoint.getyCoord().doubleValue();
		
		double denominator = endPoint.getxCoord().doubleValue()- startPoint.getxCoord().doubleValue();

		edgeAngle = Math.atan2(numerator, denominator);
	}
	
	public void calcInverseEdgeAngle() {
		double numerator = startPoint.getyCoord().doubleValue() - endPoint.getyCoord().doubleValue();
		double denominator = startPoint.getxCoord().doubleValue() - endPoint.getxCoord().doubleValue();
		edgeAngle = Math.atan2(numerator,denominator);
	}
	public Coordinate calcIntersection(Edge testEdge) {
		/*
		 * the used formula is
		 * x=((x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4))/((x1-x2)*(y3-y4)-(y1-
		 * y2)*(x3-x4));
		 * y=((x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4))/((x1-x2)*(y3-y4)-(y1-
		 * y2)*(x3-x4));
		 */

		BigDecimal x1 = startPoint.getxCoord();
		BigDecimal x2 = endPoint.getxCoord();
		BigDecimal y1 = startPoint.getyCoord();
		BigDecimal y2 = endPoint.getyCoord();

		BigDecimal x3 = testEdge.getStartPoint().getxCoord();
		BigDecimal x4 = testEdge.getEndPoint().getxCoord();
		BigDecimal y3 = testEdge.getStartPoint().getyCoord();
		BigDecimal y4 = testEdge.getEndPoint().getyCoord();

		// x1 - x2
		BigDecimal dx1 = x1.subtract(x2);
		// x3 - x4
		BigDecimal dx2 = x3.subtract(x4);
		// y1 - y2
		BigDecimal dy1 = y1.subtract(y2);
		// y3 - y4
		BigDecimal dy2 = y3.subtract(y4);

		// (x1*y2-y1*x2)
		BigDecimal pd1 = x1.multiply(y2).subtract(y1.multiply(x2));
		// (x3*y4-y3*x4)
		BigDecimal pd2 = x3.multiply(y4).subtract(y3.multiply(x4));

		// (x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4)
		BigDecimal xNumerator = pd1.multiply(dx2).subtract(dx1.multiply(pd2));
		// (x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4)
		BigDecimal yNumerator = pd1.multiply(dy2).subtract(dy1.multiply(pd2));

		// (x1-x2)*(y3-y4)-(y1-y2)*(x3-x4)
		BigDecimal denominator = dx1.multiply(dy2).subtract(dy1.multiply(dx2));

		BigDecimal xCoord = xNumerator.divide(denominator, MathContext.DECIMAL128);
		BigDecimal yCoord = yNumerator.divide(denominator, MathContext.DECIMAL128);

		return new Coordinate(xCoord, yCoord);
	}

	//when a translation is taking place, the values of min and max have to be adjusted
	public void changeRangeValues(BigDecimal x, BigDecimal y) {
		smallX = smallX.add(x);
		bigX = bigX.add(x);
		smallY = smallY.add(y);
		bigY = bigY.add(y);
		
	}
	
	public BigDecimal calcClockwiseValue() {
		
		//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
		BigDecimal xDiff = endPoint.getxCoord().subtract(startPoint.getxCoord());
		BigDecimal ySum = endPoint.getyCoord().add(startPoint.getyCoord());
		
		return xDiff.multiply(ySum);
	}

	public boolean edgesOrientatedRight(Edge preEdge, Edge postEdge) {
		//the edges are right of or parallel with this edge 
		if(preEdge.getStartPoint().dFunction(startPoint, endPoint).compareTo(round)<=0 && 
				postEdge.getEndPoint().dFunction(startPoint, endPoint).compareTo(round)<=0)
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
			edgeAngle -= Math.PI;
		}
		else{
			edgeAngle += Math.PI;
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
	
	public void calculateRanges() {

		Coordinate start = getStartPoint();
		Coordinate end = getEndPoint();

		if (start.getxCoord().compareTo(end.getxCoord())<0) {
			smallX = start.getxCoord();
			bigX = end.getxCoord();
		} else {
			smallX = end.getxCoord();
			bigX = start.getxCoord();
		}

		if (start.getyCoord().compareTo(end.getyCoord())<0) {
			smallY = start.getyCoord();
			bigY = end.getyCoord();
		} else {
			smallY = end.getyCoord();
			bigY = start.getyCoord();
		}
	}
	public boolean boundingBoxIntersect(Edge edge) {

		boolean intersect = true;

		if (edge.getBigX().compareTo(smallX)<=0|| edge.getSmallX().compareTo(bigX) >= 0
				|| edge.getBigY().compareTo(smallY)<=0
				|| edge.getSmallY().compareTo(bigY) >= 0)
			intersect = false;

		return intersect;
	}

	public boolean lineIntersect(Edge testEdge) {
		boolean intersect = true;
		// the lines intersect if the start coordinate and the end coordinate
		// of one of the edges are not both on the same side
		//in most cases this will guarantee an intersection, but there are cases where the intersection point will not be part of one of the lines
		if (testEdge.getStartPoint().dFunction(startPoint, endPoint).compareTo(round)<=0
				&& testEdge.getEndPoint().dFunction(startPoint, endPoint).compareTo(round)<=0) {
			intersect = false;
		} else if (testEdge.getStartPoint().dFunction(startPoint, endPoint).compareTo(round.negate()) >= 0
				&& testEdge.getEndPoint().dFunction(startPoint, endPoint).compareTo(round.negate()) >=0) {
			intersect = false;
		}

		return intersect;
	}
	
	public boolean containsIntersectionPoint(Coordinate intersectionCoord) {
		if(intersectionCoord.getxCoord().compareTo(smallX)<0){
			return false;
		}
		if(intersectionCoord.getxCoord().compareTo(bigX)>0){
			return false;
		}
		if(intersectionCoord.getyCoord().compareTo(smallY)<0){
			return false;
		}
		if(intersectionCoord.getyCoord().compareTo(bigY)>0){
			return false;
		}
		return true;
	}
	
	public boolean containsPoint(Coordinate intersectionCoord) {
		boolean onLine;
		onLine = intersectionCoord.dFunctionCheck(startPoint, endPoint);
		if(onLine == false){
			BigDecimal distanceToCoord = intersectionCoord.shortestDistanceToEdge(this);
			if(distanceToCoord.compareTo(new BigDecimal(0.5))>0)return false;
		};
		if(intersectionCoord.getxCoord().compareTo(smallX)<0){
			return false;
		}
		if(intersectionCoord.getxCoord().compareTo(bigX)>0){
			return false;
		}
		if(intersectionCoord.getyCoord().compareTo(smallY)<0){
			return false;
		}
		if(intersectionCoord.getyCoord().compareTo(bigY)>0){
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
		if(!startPoint.equals(edge.getStartPoint()))return false;
		if(!endPoint.equals(edge.getEndPoint()))return false;
		return true;
	}

	public Vector makeFullVector(int eN) {

		Vector vector;
		// if the orbiting edge is being used for the vector, it needs to be
		// inversed
		// this means startPoint-endPoint in stead of endPoint-startPoint
		vector = new Vector(endPoint.subtract(startPoint), eN, polygonA);
		if(eN<0){
			vector.setxCoord(vector.getxCoord().negate());
			vector.setyCoord(vector.getyCoord().negate());
		}

		return vector;

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
}
