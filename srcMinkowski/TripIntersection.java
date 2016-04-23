
/**
 * @author Stiaan Uyttersprot
 *
 */
public class TripIntersection { 
	private Coordinate intersectionPoint;
	private Edge intersectionEdge;
	private boolean intersectionSign;
	
	TripIntersection(){
		
	}
	TripIntersection(Coordinate iP, Edge iE, boolean iS){
		intersectionPoint = iP;
		intersectionEdge = iE;
		intersectionSign = iS;
	}

	public Coordinate getIntersectionPoint() {
		return intersectionPoint;
	}
	public void setIntersectionPoint(Coordinate intersectionPoint) {
		this.intersectionPoint = intersectionPoint;
	}
	public Edge getIntersectionEdge() {
		return intersectionEdge;
	}
	public void setIntersectionEdge(Edge intersectionEdge) {
		this.intersectionEdge = intersectionEdge;
	}
	public boolean getIntersectionSign() {
		return intersectionSign;
	}
	public void setIntersectionSign(boolean intersectionSign) {
		this.intersectionSign = intersectionSign;
	}
	@Override
	public String toString() {
		return "TripIntersection [intersectionPoint=" + intersectionPoint + "\nintersectionEdge=" + intersectionEdge
				+ "\nintersectionSign=" + intersectionSign + "]";
	}
}
