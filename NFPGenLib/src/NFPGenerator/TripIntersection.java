package NFPGenerator;

/**
 * Used in the Minkowski sums method.
 * No Tools are provided for this class
 * @author Stiaan Uyttersprot
 *
 */
public class TripIntersection {
	private Coordinate intersectionPoint;
	private Edge intersectionEdge;
	private boolean intersectionSign;
	
	TripIntersection(){
		
	}
	
	protected TripIntersection(Coordinate iP, Edge iE, boolean iS){
		intersectionPoint = iP;
		intersectionEdge = iE;
		intersectionSign = iS;
	}

	protected Coordinate getIntersectionPoint() {
		return intersectionPoint;
	}
	protected void setIntersectionPoint(Coordinate intersectionPoint) {
		this.intersectionPoint = intersectionPoint;
	}
	protected Edge getIntersectionEdge() {
		return intersectionEdge;
	}
	protected void setIntersectionEdge(Edge intersectionEdge) {
		this.intersectionEdge = intersectionEdge;
	}
	protected boolean getIntersectionSign() {
		return intersectionSign;
	}
	protected void setIntersectionSign(boolean intersectionSign) {
		this.intersectionSign = intersectionSign;
	}
	@Override
	public String toString() {
		return "TripIntersection [intersectionPoint=" + intersectionPoint + "\nintersectionEdge=" + intersectionEdge
				+ "\nintersectionSign=" + intersectionSign + "]";
	}
}
