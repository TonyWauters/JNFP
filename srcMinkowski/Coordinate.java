/**
 *this class is used for storing points of a polygon
 * @author Stiaan Uyttersprot
 * 
 *         
 */
public class Coordinate {
	private double xCoord;
	private double yCoord;
	
	public static double round = 1;
	
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
		return xCoord + ", " + yCoord;
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
		if(Math.abs(xCoord)==0)xCoord = Math.abs(xCoord);
		if(Math.abs(yCoord)==0)yCoord = Math.abs(yCoord);
		if(Math.abs(other.xCoord)==0)other.xCoord = Math.abs(other.xCoord);
		if(Math.abs(other.yCoord)==0)other.yCoord = Math.abs(other.yCoord);
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

	public double shortestDistanceToEdge(Edge edge){
		double distanceToStartPoint = distanceTo(edge.getStartPoint());
		double anglePointEdge = edge.getStartPoint().calculateAngle(this, edge.getEndPoint());
		double shortestDistance = distanceToStartPoint*Math.sin(anglePointEdge);
		return shortestDistance;
	}
	// calculating the angle: the coordinate that calls the method is the one
	// where the angle needs to be calculated
	public double calculateAngle(Coordinate coord2, Coordinate coord3) {

		double distA = coord2.distanceTo(coord3);
		double distB = this.distanceTo(coord3);
		double distC = this.distanceTo(coord2);

		double cosAngle = (distB * distB + distC * distC - distA * distA) / (2 * distB * distC);
		double angle = Math.acos(cosAngle);
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
	public double dFunction(Edge e) {

		double dValue = (e.getStartPoint().getxCoord() - e.getEndPoint().getxCoord()) * (e.getStartPoint().getyCoord() - yCoord)
				- (e.getStartPoint().getyCoord() - e.getEndPoint().getyCoord()) * (e.getStartPoint().getxCoord() - xCoord);

		return dValue;
	}
	
	//check if the value is zero or not, if zero the point falls on the line (trying to cope with very small deviation values)
	public boolean dFunctionCheck(Coordinate startPoint, Coordinate endPoint) {
		boolean touching = false;
		double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
				- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);
		if(Math.abs(dValue) <= round)touching = true;
		return touching;
	}
	
	public boolean dFunctionCheck(Edge e) {
		boolean touching = false;
		double dValue = (e.getStartPoint().getxCoord() - e.getEndPoint().getxCoord()) * (e.getStartPoint().getyCoord() - yCoord)
				- (e.getStartPoint().getyCoord() - e.getEndPoint().getyCoord()) * (e.getStartPoint().getxCoord() - xCoord);
		if(Math.abs(dValue) <= round)touching = true;
		return touching;
	}

	public void move(double x, double y) {
		xCoord += x;
		yCoord += y;
	}

	//check if two coordinates are equal (use round to make sure mistakes by rounding in the calculations are ignored
	public boolean equalValuesRounded(Coordinate coord) {
		
		if (Math.abs(xCoord - coord.getxCoord())>round)
			return false;
		if (Math.abs(yCoord - coord.getyCoord())>round)
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

	public void replaceByNegative() {
		
		this.xCoord = -xCoord;
		this.yCoord = -yCoord;
		
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
		double helpXCoord = -yCoord;
		
		this.yCoord = xCoord;
		this.xCoord = helpXCoord;
		
	}
}
