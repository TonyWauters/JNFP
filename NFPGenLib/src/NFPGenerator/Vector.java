package NFPGenerator;
/**
 * this class describes a vector starting in the origin
 * @author Stiaan Uyttersprot
 */
public class Vector {
	private double xCoord;
	private double yCoord;
	private double vectorAngle;
	//the number of the edge that the vector slides over
	private int edgeNumber;
	private boolean fromStatEdge;
	
	private boolean polygonA;
	
	protected static double round = 1e-4;
	
	private Edge parentEdge;
	/**
	 * create a vector from origin to a given x- and y-coord
	 * @param x x-value
	 * @param y y-value
	 */
	public Vector(double x, double y) {
		xCoord = x;
		yCoord = y;
	}
	/**
	 * deep copy of a vector
	 * @param vect vector to copy
	 */
	public Vector(Vector vect) {
		xCoord = vect.getxCoord();
		yCoord = vect.getyCoord();
		edgeNumber = vect.getEdgeNumber();
		calculateVectorAngle();
		fromStatEdge = vect.isFromStatEdge();
	}

	protected Vector(Coordinate coord, int eN, boolean fromStat) {
		xCoord = coord.getxCoord();
		yCoord = coord.getyCoord();
		calculateVectorAngle();
		edgeNumber = eN;
		fromStatEdge = fromStat;
	}
	/**
	 * a vector between two coordinates, translated to start in the origin
	 * @param startPoint startpoint of vector
	 * @param endPoint endpoint of vector
	 */
	public Vector(Coordinate startPoint, Coordinate endPoint) {
		
		Coordinate vectorCoord = endPoint.subtract(startPoint);
		xCoord = vectorCoord.getxCoord();
		yCoord = vectorCoord.getyCoord();
		calculateVectorAngle();
		edgeNumber = -1;
		fromStatEdge = false;
	}
	/**
	 * @return the x-coordinate
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
	 * @return the y-coordinate
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

	/**
	 * @return the vector angle in radians
	 */
	public double getVectorAngle() {
		return vectorAngle;
	}

	protected int getEdgeNumber() {
		return edgeNumber;
	}

	protected void setEdgeNumber(int edgeNumber) {
		this.edgeNumber = edgeNumber;
	}

	protected boolean isFromStatEdge() {
		return fromStatEdge;
	}

	protected void setFromStatEdge(boolean fromStatEdge) {
		this.fromStatEdge = fromStatEdge;
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
	
	private void calculateVectorAngle() {

		vectorAngle = Math.atan2(yCoord, xCoord);

	}

	/**
	 * inverts the values of the vector
	 */
	public void reflect() {
		xCoord = 0-xCoord;
		yCoord = 0-yCoord;
	}

	protected void trimTo(Coordinate intersectionCoord, Coordinate startPoint) {
		xCoord = intersectionCoord.getxCoord()-startPoint.getxCoord();
		yCoord = intersectionCoord.getyCoord()-startPoint.getyCoord();
	}

	
	/**
	 * @return the length of the vector squared
	 */
	public double getLengthSquared() {
		
		return xCoord*xCoord + yCoord*yCoord;
	}

	protected void trimFeasibleVector(MultiPolygon coordinatePolygon, MultiPolygon edgePolygon,boolean orbCoords) {
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

	protected Edge getParentEdge() {
		return parentEdge;
	}

	protected void setParentEdge(Edge parentEdge) {
		this.parentEdge = parentEdge;
	}

	protected boolean isPolygonA() {
		return polygonA;
	}

	protected void setPolygonA(boolean polygonA) {
		this.polygonA = polygonA;
	}

	@Override
	public String toString() {
		return "Vector [xCoord=" + xCoord + ", yCoord=" + yCoord + ", vectorAngle=" + vectorAngle + ", edgeNumber="
				+ edgeNumber + ", polygonA=" + polygonA + ", parentEdge=" + parentEdge + "]";
	}
}
