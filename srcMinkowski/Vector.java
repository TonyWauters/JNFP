/**
 *
 * @author Stiaan
 * 
 *         this class is used for storing points of a polygon
 */
public class Vector {
	private double xCoord;
	private double yCoord;
	private double vectorAngle;
	//the number of the edge that the vector slides over
	private int edgeNumber;

	private boolean polygonA;
	private static double roundingValue = 10000;
	
	private Edge parentEdge;
	
	Vector(double x, double y) {
		xCoord = x;
		yCoord = y;
	}

	public Vector(Vector vect) {
		xCoord = vect.getxCoord();
		yCoord = vect.getyCoord();
		edgeNumber = vect.getEdgeNumber();
		calculateVectorAngle();
	}

	public Vector(Coordinate coord, int eN, boolean polygonA) {
		xCoord = coord.getxCoord();
		yCoord = coord.getyCoord();
		calculateVectorAngle();
		edgeNumber = eN;
		this.setPolygonA(polygonA);
	}

	public Vector(Coordinate startPoint, Coordinate endPoint) {
		
		Coordinate vectorCoord = endPoint.subtract(startPoint);
		xCoord = vectorCoord.getxCoord();
		yCoord = vectorCoord.getyCoord();
		calculateVectorAngle();
		edgeNumber = -1;
	}

	public double getxCoord() {
		return xCoord;
	}

	public void setxCoord(double xCoord) {
		this.xCoord = xCoord;
	}

	public double getyCoord() {
		return yCoord;
	}

	public void setyCoord(double yCoord) {
		this.yCoord = yCoord;
	}

	public double getVectorAngle() {
		return vectorAngle;
	}

	public void setVectorAngle(double vectorAngle) {
		this.vectorAngle = vectorAngle;
	}

	public int getEdgeNumber() {
		return edgeNumber;
	}

	public void setEdgeNumber(int edgeNumber) {
		this.edgeNumber = edgeNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + edgeNumber;
		long temp;
		temp = Double.doubleToLongBits(vectorAngle);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(xCoord);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(yCoord);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	public boolean equals(Vector vect) {
		if(xCoord != vect.getxCoord()) return false;
		if(yCoord != vect.getyCoord()) return false;
		return true;
	}

	// D-function is used to calculate where a point is located in reference to
	// a vector
	// if the value is larger then 0 the point is on the left
	// Dabp = ((Xa-Xb)*(Ya-Yp)-(Ya-Yb)*(Xa-Xp))
	public double dFunction(Vector startPoint, Vector endPoint) {

		double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
				- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);

		return dValue;
	}
	
	//check if the value is zero or not (trying to cope with very small deviation values)
	public boolean dFunctionCheck(Vector startPoint, Vector endPoint) {
		boolean touching = false;
		double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
				- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);
		if(dValue < 1e-4 && dValue > -1e-4)touching = true;
		return touching;
	}

	public void move(double x, double y) {
		xCoord += x;
		yCoord += y;
	}

	//check if two vectors are equal (use round to make sure mistakes by rounding in the calculations are ignored
	public boolean equalValuesRounded(Vector vect) {

		if (Math.round(xCoord*roundingValue)/roundingValue != Math.round(vect.getxCoord()*roundingValue)/roundingValue)
			return false;
		if (Math.round(yCoord*roundingValue)/roundingValue != Math.round(vect.getyCoord()*roundingValue)/roundingValue)
			return false;
		return true;
	}

	// this vector minus the given vector
	public Vector subtract(Vector point) {

		return new Vector(xCoord - point.getxCoord(), yCoord - point.getyCoord());
	}

	public Vector add(Vector point) {

		return new Vector(xCoord + point.getxCoord(), yCoord + point.getyCoord());
	}

	public boolean isBiggerThen(Vector biggestCoord) {

		return false;
	}

	private void calculateVectorAngle() {

		vectorAngle = Math.atan2(yCoord, xCoord);

	}

	public double getLengthSquared() {
		
		return xCoord*xCoord + yCoord*yCoord;
	}

	public Edge getParentEdge() {
		return parentEdge;
	}

	public void setParentEdge(Edge parentEdge) {
		this.parentEdge = parentEdge;
	}

	public boolean isPolygonA() {
		return polygonA;
	}

	public void setPolygonA(boolean polygonA) {
		this.polygonA = polygonA;
	}

	@Override
	public String toString() {
		return "Vector [xCoord=" + xCoord + ", yCoord=" + yCoord + ", vectorAngle=" + vectorAngle + ", edgeNumber="
				+ edgeNumber + ", polygonA=" + polygonA + ", parentEdge=" + parentEdge + "]";
	}

	
}
