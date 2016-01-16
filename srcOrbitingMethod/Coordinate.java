/**
 *
 * @author Stiaan
 * 
 *         this class is used for storing points of a polygon
 */
public class Coordinate {
	private double xCoord;
	private double yCoord;
	
	private static double roundingValue = 10000;
	private static double round = 1e-4;
	
	Coordinate(double x, double y) {
		xCoord = x;
		yCoord = y;
	}

	public Coordinate(Coordinate coordinate) {
		xCoord = coordinate.getxCoord();
		yCoord = coordinate.getyCoord();
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
		long temp;
		temp = Double.doubleToLongBits(xCoord);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(yCoord);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (Double.doubleToLongBits(xCoord) != Double.doubleToLongBits(other.xCoord))
			return false;
		if (Double.doubleToLongBits(yCoord) != Double.doubleToLongBits(other.yCoord))
			return false;
		return true;
	}

	public double distanceTo(Coordinate coord) {
		double dX = xCoord - coord.getxCoord();
		double dY = yCoord - coord.getyCoord();
		double distance = Math.sqrt(dX * dX + dY * dY);
		return distance;
	}

	// calculating the angle: the coordinate that calls the method is the one
	// where the angle needs to be calculated
	public double calculateAngle(Coordinate coord2, Coordinate coord3) {

		double distA = coord2.distanceTo(coord3);
		// System.out.println(distA);
		double distB = this.distanceTo(coord3);
		// System.out.println(distB);
		double distC = this.distanceTo(coord2);
		// System.out.println(distC);

		double cosAngle = (distB * distB + distC * distC - distA * distA) / (2 * distB * distC);
		// System.out.println(cosAngle);
		double angle = Math.acos(cosAngle);
		// System.out.println(angle);
		return angle;
	}

	// D-function is used to calculate where a point is located in reference to
	// a vector
	// if the value is larger then 0 the point is on the left
	// Dabp = ((Xa-Xb)*(Ya-Yp)-(Ya-Yb)*(Xa-Xp))
	public double dFunction(Coordinate startPoint, Coordinate endPoint) {

		double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
				- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);

		return dValue;
	}
	
	//check if the value is zero or not (trying to cope with very small deviation values)
	public boolean dFunctionCheck(Coordinate startPoint, Coordinate endPoint) {
		boolean touching = false;
		double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
				- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);
		if(dValue < round && dValue > -round)touching = true;
		return touching;
	}

	public void move(double x, double y) {
		xCoord += x;
		yCoord += y;
	}

	//check if two coordinates are equal (use round to make sure mistakes by rounding in the calculations are ignored
	public boolean equalValuesRounded(Coordinate coord) {

		
		if (Math.round(xCoord*roundingValue)/roundingValue != Math.round(coord.getxCoord()*roundingValue)/roundingValue)
			return false;
		if (Math.round(yCoord*roundingValue)/roundingValue != Math.round(coord.getyCoord()*roundingValue)/roundingValue)
			return false;
		return true;
	}

	// this coordinate minus the given coordinate
	public Coordinate subtract(Coordinate point) {

		return new Coordinate(xCoord - point.getxCoord(), yCoord - point.getyCoord());
	}

	public Coordinate subtract(Vector vector) {
		
		return new Coordinate(xCoord - vector.getxCoord(), yCoord - vector.getyCoord());
	}
	
	public Coordinate add(Coordinate point) {

		return new Coordinate(xCoord + point.getxCoord(), yCoord + point.getyCoord());
	}

	public Coordinate add(Vector vector) {
		
		return new Coordinate(xCoord + vector.getxCoord(), yCoord + vector.getyCoord());
	}

	
	
	public boolean isBiggerThen(Coordinate biggestCoord) {

		return false;
	}

	public double getLengthSquared() {
		
		return xCoord*xCoord + yCoord*yCoord;
	}

	public Coordinate translatedTo(Vector vector) {
		Coordinate transCoord = new Coordinate(xCoord+vector.getxCoord(), yCoord+ vector.getyCoord());
		return transCoord;
	}
	
	public void translate(Vector vector){
		xCoord+=vector.getxCoord();
		yCoord+=vector.getyCoord();
	}
	
	public void roundCoord(){

		xCoord = Math.round(xCoord*roundingValue)/roundingValue;
		yCoord = Math.round(yCoord*roundingValue)/roundingValue;
	}
}
