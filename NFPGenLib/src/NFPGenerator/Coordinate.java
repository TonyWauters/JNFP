package NFPGenerator;

/**
 * the class used for storing and using a coordinate
 * @author Stiaan Uyttersprot
 *
 */
public class Coordinate {
	private double xCoord;
	private double yCoord;

	protected static double round = 1e-4;
	
	/**
	 * Create a new coordinate giving the x- and y-coord
	 * @param x-coordinate the x-value of the coordinate
	 * @param y-coordinate the y-value of the coordinate
	 */
	public Coordinate(double x, double y) {
		xCoord = x;
		yCoord = y;
	}

	
	/**
	 * Make a deep copy of a Coordinate
	 * @param coordinate the coordinate to copy
	 */
	public Coordinate(Coordinate coordinate) {
		xCoord = coordinate.getxCoord();
		yCoord = coordinate.getyCoord();
	}

	/**
	 * @return the x-value
	 */
	public double getxCoord() {
		return xCoord;
	}

	/**
	 * @param xCoord the new x-value
	 */
	public void setxCoord(double xCoord) {
		this.xCoord = xCoord;
	}

	/**
	 * @return the y-value
	 */
	public double getyCoord() {
		return yCoord;
	}

	/**
	 * @param yCoord the new y-value
	 */
	public void setyCoord(double yCoord) {
		this.yCoord = yCoord;
	}

	protected void printCoordinate() {
		System.out.println("( " + xCoord + " , " + yCoord + " ) ");
	}

	public String toString() {
		return "( " + xCoord + " , " + yCoord + " ) ";
	}
	
	protected String toNfpString(){
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

	/**
	 * Calculates the distance between this coordinate and the given coordinate
	 * 
	 * @param coord the coordinate to get the distance to
	 * @return the distance between the two points
	 */
	public double distanceTo(Coordinate coord) {
		double dX = xCoord - coord.getxCoord();
		double dY = yCoord - coord.getyCoord();
		double distance = Math.sqrt(dX * dX + dY * dY);
		return distance;
	}
	
	/**
	 * Calculates the shortest distance between this coordinate and the given edge
	 * @param edge the edge to get the distance to
	 * @return the shortest distance between the edge and this coordinate
	 */
	public double shortestDistanceToEdge(Edge edge){
		double distanceToStartPoint = distanceTo(edge.getStartPoint());
		double anglePointEdge = edge.getStartPoint().calculateAngle(this, edge.getEndPoint());
		double shortestDistance = distanceToStartPoint*Math.sin(anglePointEdge);
		return shortestDistance;
	}
	
	/**
	 * Calculates the angle of three coordinates.
	 * The coordinate that calls the method is the middle coordinate,
	 * where the angle needs to be calculated
	 * 
	 * @param coord2 the coordinate before this coordinate
	 * @param coord3 the coordinate after this coordinate
	 * @return the angle formed by these three coordinates
	 */
	public double calculateAngle(Coordinate coord2, Coordinate coord3) {

		double distA = coord2.distanceTo(coord3);
		double distB = this.distanceTo(coord3);
		double distC = this.distanceTo(coord2);

		double cosAngle = (distB * distB + distC * distC - distA * distA) / (2 * distB * distC);
		double angle = Math.acos(cosAngle);
		return angle;
	}

	/**
	 * D-function is used to calculate where a point is located in reference to 
	 * an edge, given by it's start- and endpoint
	 * If the value is larger than 0 the point is on the left.
	 * If it is larger than 0 it's to the right.
	 * Equal to 0 then the point coincides with the edge.
	 * Dabp = ((Xa-Xb)*(Ya-Yp)-(Ya-Yb)*(Xa-Xp))
	 * @param startPoint the startpoint of the edge
	 * @param endPoint the endpoint of the edge
	 * @return D-Function value
	 */
	public double dFunction(Coordinate startPoint, Coordinate endPoint) {

		double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
				- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);

		return dValue;
	}
	/**
	 * D-function is used to calculate where a point is located in reference to 
	 * an edge.
	 * If the value is larger than 0 the point is on the left.
	 * If it is larger than 0 it's to the right.
	 * Equal to 0 then the point coincides with the edge.
	 * Dabp = ((Xa-Xb)*(Ya-Yp)-(Ya-Yb)*(Xa-Xp))
	 * @param edge the edge for calculating the D-function
	 * @return D-Function value
	 */
	public double dFunction(Edge edge) {

		double dValue = (edge.getStartPoint().getxCoord() - edge.getEndPoint().getxCoord()) * (edge.getStartPoint().getyCoord() - yCoord)
				- (edge.getStartPoint().getyCoord() - edge.getEndPoint().getyCoord()) * (edge.getStartPoint().getxCoord() - xCoord);

		return dValue;
	}
	
	/**
	 * Calculates the D-Function to check if the coordinate is placed on the edge 
	 * given by start and endpoint or not.
	 * @param startPoint the startpoint of the edge
	 * @param endPoint the endpoint of the edge
	 * @return if the point falls on the edge or not
	 */
	public boolean dFunctionCheck(Coordinate startPoint, Coordinate endPoint) {
		boolean touching = false;
		double dValue = (startPoint.getxCoord() - endPoint.getxCoord()) * (startPoint.getyCoord() - yCoord)
				- (startPoint.getyCoord() - endPoint.getyCoord()) * (startPoint.getxCoord() - xCoord);
		if(Math.abs(dValue) <= round)touching = true;
		return touching;
	}
	
	
	/**
	 * Calculates the D-Function to check if the coordinate is placed on the edge or not.
	 * @param edge the edge the point may possibly be on
	 * @return if the point falls on the edge or not
	 */
	public boolean dFunctionCheck(Edge edge) {
		boolean touching = false;
		double dValue = (edge.getStartPoint().getxCoord() - edge.getEndPoint().getxCoord()) * (edge.getStartPoint().getyCoord() - yCoord)
				- (edge.getStartPoint().getyCoord() - edge.getEndPoint().getyCoord()) * (edge.getStartPoint().getxCoord() - xCoord);
		if(Math.abs(dValue) <= round)touching = true;
		return touching;
	}
	
	/**
	 * moves the edge with the vector described by x- and y-coordinate
	 * @param x the x-coord of the vector
	 * @param y the y-coord of the vector
	 */
	public void move(double x, double y) {
		xCoord += x;
		yCoord += y;
	}

	//
	/**
	 * check if two coordinates are equal, ignoring really small differences, set by round
	 * @param coord the coordinate to compare
	 * @return if the coordinates are practically the same or not
	 */
	public boolean equalValuesRounded(Coordinate coord) {

		
		if (Math.abs(xCoord - coord.getxCoord())>round)
			return false;
		if (Math.abs(yCoord - coord.getyCoord())>round)
			return false;
		return true;
	}

	/**
	 * this coordinate minus the given coordinate
	 * @param coordinate the coordinate to subtract
	 * @return the coordinate formed by subtraction
	 */
	public Coordinate subtract(Coordinate coordinate) {

		return new Coordinate(xCoord - coordinate.getxCoord(), yCoord - coordinate.getyCoord());
	}

	protected Coordinate subtract(Vector vector) {
		
		return new Coordinate(xCoord - vector.getxCoord(), yCoord - vector.getyCoord());
	}
	
	/**
	 * adds two coordinate and returns the result
	 * @param coordinate the coordinate to add
	 * @return the coordinate formed by addition
	 */
	public Coordinate add(Coordinate coordinate) {

		return new Coordinate(xCoord + coordinate.getxCoord(), yCoord + coordinate.getyCoord());
	}

	protected Coordinate add(Vector vector) {
		
		return new Coordinate(xCoord + vector.getxCoord(), yCoord + vector.getyCoord());
	}

	/**
	 * translates the coordinate with a given vector and returns the new coordinate
	 * @param vector the translationvector
	 * @return the translated coordinate
	 */
	public Coordinate translatedTo(Vector vector) {
		Coordinate transCoord = new Coordinate(xCoord+vector.getxCoord(), yCoord+ vector.getyCoord());
		return transCoord;
	}
	
	/**
	 * translate this coordinate to a new position
	 * @param vector the translationvector
	 */
	public void translate(Vector vector){
		xCoord+=vector.getxCoord();
		yCoord+=vector.getyCoord();
	}
	
	
	/**
	 * negates this coordinate
	 */
	public void replaceByNegative() {
		
		this.xCoord = -xCoord;
		this.yCoord = -yCoord;
		
	}

	protected Edge getEdgeWithTranslation(Vector vector, Edge edge) {
		
		Coordinate edgeStart = this;
		Coordinate edgeEnd = edgeStart.translatedTo(vector);
		Edge translationEdge = new Edge(edge);
		translationEdge.setStartPoint(edgeStart);
		translationEdge.setEndPoint(edgeEnd);
		translationEdge.calculateRanges();
		return translationEdge;
		
	}
	
	
	/**
	 * rotates this coordinate over 90 degrees
	 */
	public void rotateNinety() {
		double helpXCoord = -yCoord;
		
		this.yCoord = xCoord;
		this.xCoord = helpXCoord;
		
	}
}
