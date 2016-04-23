/**
 *
 * @author Stiaan Uyttersprot
 * 
 */
public class Vector {
	private double xCoord;
	private double yCoord;
	private double vectorAngle;
	//the number of the edge that the vector slides over
	private int edgeNumber;
	private boolean fromStatEdge;
	
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
		fromStatEdge = vect.isFromStatEdge();
	}

	public Vector(Coordinate coord, int eN, boolean fromStat) {
		xCoord = coord.getxCoord();
		yCoord = coord.getyCoord();
		calculateVectorAngle();
		edgeNumber = eN;
		fromStatEdge = fromStat;
	}

	public Vector(Coordinate startPoint, Coordinate endPoint) {
		
		Coordinate vectorCoord = endPoint.subtract(startPoint);
		xCoord = vectorCoord.getxCoord();
		yCoord = vectorCoord.getyCoord();
		calculateVectorAngle();
		edgeNumber = -1;
		fromStatEdge = false;
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

	public boolean isFromStatEdge() {
		return fromStatEdge;
	}

	public void setFromStatEdge(boolean fromStatEdge) {
		this.fromStatEdge = fromStatEdge;
	}

	public void printVector() {
		System.out.println("( " + xCoord + " , " + yCoord + " ) " + " " + Math.toDegrees(vectorAngle) + " EdgeNumber: " + edgeNumber + " translates over stationary: " + fromStatEdge);
	}

	public String toString() {
		return "( " + xCoord + " , " + yCoord + " ) EdgeNumber: " + edgeNumber+ " translates over stationary: " + fromStatEdge;
	}

	public double distanceTo(Vector vect) {
		double dX = xCoord - vect.getxCoord();
		double dY = yCoord - vect.getyCoord();
		double distance = Math.sqrt(dX * dX + dY * dY);
		return distance;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector other = (Vector) obj;
		if (edgeNumber != other.edgeNumber)
			return false;
		if (Double.doubleToLongBits(vectorAngle) != Double.doubleToLongBits(other.vectorAngle))
			return false;
		if (Double.doubleToLongBits(xCoord) != Double.doubleToLongBits(other.xCoord))
			return false;
		if (Double.doubleToLongBits(yCoord) != Double.doubleToLongBits(other.yCoord))
			return false;
		return true;
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

	public Vector reflect() {
		xCoord = 0-xCoord;
		yCoord = 0-yCoord;
		return this;
	}

	public void trimTo(Coordinate intersectionCoord, Coordinate startPoint) {
		xCoord = intersectionCoord.getxCoord()-startPoint.getxCoord();
		yCoord = intersectionCoord.getyCoord()-startPoint.getyCoord();
	}

	public double getLengthSquared() {
		
		return xCoord*xCoord + yCoord*yCoord;
	}

	public void trimFeasibleVector(MultiPolygon coordinatePolygon, MultiPolygon edgePolygon,boolean orbCoords) {
		Edge testEdge;
		boolean trimmed;
		for (Coordinate coord : coordinatePolygon.getOuterPolygon()) {
			//this is a testEdge and does not have a real number
			if(orbCoords)testEdge = new Edge(coord, coord.add(this), -1);
			else{
				// the translation will be in the other direction if we're working with stationary coords,
				//so we subtract the vector to get the edge
				testEdge = new Edge(coord, coord.subtract(this), -1);
			}
			// checking the coordinates with the outer edges of the
			// the other polygon
			for (Edge edge : edgePolygon.getOuterPolygonEdges()) {
				
				trimmed = testAndTrimVector(edge, testEdge, coord);
				if(trimmed){
					if(orbCoords){
						testEdge = new Edge(coord, coord.add(this), -1);
					}
					else{
						// in this case the new vector will be in the wrong
						// direction because we are using coordinates of
						// the stationary polygon to translate, so we need
						// to reflect the vector
						reflect();
						testEdge = new Edge(coord, coord.subtract(this), -1);
					}
				}
				
				
			}
			
			for (Edge[] edgeArray : edgePolygon.getHoleEdges()) {
				for (Edge edge : edgeArray) {
					trimmed = testAndTrimVector(edge, testEdge, coord);
					if(trimmed){
						if(orbCoords){
							testEdge = new Edge(coord, coord.add(this), -1);
						}
						else{
							// in this case the new vector will be in the wrong
							// direction because we are using coordinates of
							// the stationary polygon to translate, so we need
							// to reflect the vector
							reflect();
							testEdge = new Edge(coord, coord.subtract(this), -1);
						}
					}
				}
			}

		}
		
	}


	private boolean testAndTrimVector(Edge edge, Edge testEdge, Coordinate coord) {
		Coordinate intersectionCoord;
		boolean trimmed = false;
		// if the bounding boxes intersect, line intersection
		// has to
		// be checked and the vector may need to be trimmed
		if (edge.boundingBoxIntersect(testEdge)) {
			if (edge.lineIntersect(testEdge)) {
				intersectionCoord = edge.calcIntersection(testEdge);
				if(edge.containsIntersectionPoint(intersectionCoord)&&testEdge.containsIntersectionPoint(intersectionCoord)){
					// trim the vector with
					// endpoint = intersectionCoordinate
					trimTo(intersectionCoord,coord);
					trimmed = true;
					//because the vector gets trimmed the testEdge changes, this will result in less intersection because of the shorter vector
					//also the Vector will not be overwritten by every new intersection if the testEdge is changed, only when it has to be shorter
				}
				
				
			}

		}
		return trimmed;
	}

	public Edge getParentEdge() {
		return parentEdge;
	}

	public void setParentEdge(Edge parentEdge) {
		this.parentEdge = parentEdge;
	}

}
