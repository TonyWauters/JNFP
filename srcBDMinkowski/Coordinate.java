import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import org.nevec.rjm.BigDecimalMath;

/**
 * this class is used for storing points of a polygon
 * @author  Stiaan Uyttersprot
 * 
 */
public class Coordinate {
	private BigDecimal xCoord;
	private BigDecimal yCoord;
	private static MathContext mc = MathContext.UNLIMITED;
	
	public static BigDecimal round = new BigDecimal(1e-4);
	
	Coordinate(BigDecimal x, BigDecimal y) {
		xCoord = x;
		yCoord = y;
	}

	public Coordinate(Coordinate coordinate) {
		xCoord = coordinate.getxCoord();
		yCoord = coordinate.getyCoord();
	}

	public BigDecimal getxCoord() {
		return xCoord;
	}

	public void setxCoord(BigDecimal xCoord) {
		this.xCoord = xCoord;
	}

	public BigDecimal getyCoord() {
		return yCoord;
	}

	public void setyCoord(BigDecimal yCoord) {
		this.yCoord = yCoord;
	}

	public void printCoordinate() {
		System.out.println("( " + xCoord + " , " + yCoord + " ) ");
	}

	public String toString() {
		return "( " + xCoord + " , " + yCoord + " ) ";
	}
	
	public String toNfpString(){
		return xCoord + " " + yCoord;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((xCoord == null) ? 0 : xCoord.hashCode());
		result = prime * result + ((yCoord == null) ? 0 : yCoord.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (xCoord == null) {
			if (other.xCoord != null)
				return false;
		} else if (xCoord.compareTo(other.xCoord)!=0)
			return false;
		if (yCoord == null) {
			if (other.yCoord != null)
				return false;
		} else if (yCoord.compareTo(other.yCoord)!=0)
			return false;
		return true;
	}

	public BigDecimal distanceTo(Coordinate coord) {
		BigDecimal dX = xCoord.subtract(coord.getxCoord());
		BigDecimal dY = yCoord.subtract(coord.getyCoord());
		BigDecimal distanceSquared = (dX.multiply(dX)).add(dY.multiply(dY));
		BigDecimal distance = BigDecimalMath.sqrt(distanceSquared, mc);
		return distance;
	}

	public BigDecimal shortestDistanceToEdge(Edge edge){
		BigDecimal distanceToStartPoint = distanceTo(edge.getStartPoint());
		BigDecimal anglePointEdge = edge.getStartPoint().calculateAngle(this, edge.getEndPoint());
		BigDecimal shortestDistance = distanceToStartPoint.multiply(BigDecimalMath.sin(anglePointEdge));
		return shortestDistance;
	}
	// calculating the angle: the coordinate that calls the method is the one
	// where the angle needs to be calculated
	public BigDecimal calculateAngle(Coordinate coord2, Coordinate coord3) {

		BigDecimal distA = coord2.distanceTo(coord3);
		BigDecimal distB = this.distanceTo(coord3);
		BigDecimal distC = this.distanceTo(coord2);

		BigDecimal cosAngleNum = (distB.multiply(distB).add(distC.multiply(distC)).subtract(distA.multiply(distA))); 
		BigDecimal cosAngleDenom = (distB.multiply(distC)).multiply(new BigDecimal(2));
		BigDecimal angle = BigDecimalMath.acos(cosAngleNum.divide(cosAngleDenom,MathContext.DECIMAL128));
		return angle;
	}

	// D-function is used to calculate where a point is located in reference to
	// a vector
	// if the value is larger then 0 the point is on the left
	// Dabp = ((Xa-Xb)*(Ya-Yp)-(Ya-Yb)*(Xa-Xp))
	public BigDecimal dFunction(Coordinate startPoint, Coordinate endPoint) {

		BigDecimal dxab = startPoint.getxCoord().subtract(endPoint.getxCoord());
		BigDecimal dyap = startPoint.getyCoord().subtract(yCoord);
		BigDecimal dyab = startPoint.getyCoord().subtract(endPoint.getyCoord());
		BigDecimal dxap = startPoint.getxCoord().subtract(xCoord);
				
		BigDecimal dValue = dxab.multiply(dyap).subtract(dyab.multiply(dxap));
		return dValue;
	}
	public BigDecimal dFunction(Edge e) {

		BigDecimal dxab = e.getStartPoint().getxCoord().subtract(e.getEndPoint().getxCoord());
		BigDecimal dyap = e.getStartPoint().getyCoord().subtract(yCoord);
		BigDecimal dyab = e.getStartPoint().getyCoord().subtract(e.getEndPoint().getyCoord());
		BigDecimal dxap = e.getStartPoint().getxCoord().subtract(xCoord);
		
		BigDecimal dValue = dxab.multiply(dyap).subtract(dyab.multiply(dxap));

		return dValue;
	}
	//check if the value is zero or not, if zero the point falls on the line (trying to cope with very small deviation values)
	public boolean dFunctionCheck(Coordinate startPoint, Coordinate endPoint) {
		boolean touching = false;
		
		BigDecimal dxab = startPoint.getxCoord().subtract(endPoint.getxCoord());
		BigDecimal dyap = startPoint.getyCoord().subtract(yCoord);
		BigDecimal dyab = startPoint.getyCoord().subtract(endPoint.getyCoord());
		BigDecimal dxap = startPoint.getxCoord().subtract(xCoord);
		
		BigDecimal dValue = dxab.multiply(dyap).subtract(dyab.multiply(dxap));
		
		if((dValue.abs()).compareTo(round) < 0)touching = true;
		return touching;
	}

	public void move(BigDecimal x, BigDecimal y) {
		xCoord = xCoord.add(x);
		yCoord = yCoord.add(y);
	}

	//check if two coordinates are equal (use round to make sure mistakes by rounding in the calculations are ignored
	public boolean equalValuesRounded(Coordinate coord) {
		
		if (((xCoord.subtract(coord.getxCoord())).abs()).compareTo(round)>0)
			return false;
		if (((yCoord.subtract(coord.getyCoord())).abs()).compareTo(round)>0)
			return false;
		return true;
	}

	// this coordinate minus the given coordinate
	public Coordinate subtract(Coordinate point) {

		return new Coordinate(xCoord.subtract(point.getxCoord()), yCoord.subtract(point.getyCoord()));
	}

	public Coordinate subtract(Vector vector) {
		
		return new Coordinate(xCoord.subtract(vector.getxCoord()), yCoord.subtract(vector.getyCoord()));
	}
	
	public Coordinate add(Coordinate point) {

		return new Coordinate(xCoord.add(point.getxCoord()), yCoord.add(point.getyCoord()));
	}

	public Coordinate add(Vector vector) {
		
		return new Coordinate(xCoord.add(vector.getxCoord()), yCoord.add(vector.getyCoord()));
	}

public BigDecimal getLengthSquared() {
		
		return xCoord.multiply(xCoord).add(yCoord.multiply(yCoord));
	}

	public Coordinate translatedTo(Vector vector) {
		Coordinate transCoord = new Coordinate(xCoord.add(vector.getxCoord()), yCoord.add(vector.getyCoord()));
		return transCoord;
	}
	
	public void translate(Vector vector){
		xCoord = xCoord.add(vector.getxCoord());
		yCoord = yCoord.add(vector.getyCoord());
	}
	
	public void roundCoord(){
		System.err.println("roundCoord bigdecimal not implemented");
	}

	public void replaceByNegative() {
		
		this.xCoord = xCoord.negate();
		this.yCoord = yCoord.negate();
		
	}

	public Edge getEdgeWithTranslation(Vector vector, Edge edge) {
		
		Coordinate edgeStart = this;
		Coordinate edgeEnd = edgeStart.translatedTo(vector);
		Edge translationEdge = new Edge(edge);
		translationEdge.setStartPoint(edgeStart);
		translationEdge.setEndPoint(edgeEnd);
		translationEdge.calculateRanges();
		return translationEdge;
		
	}
	
	public void rotateNinety() {
		BigDecimal helpXCoord = yCoord.negate();
		
		this.yCoord = xCoord;
		this.xCoord = helpXCoord;
		
	}
}