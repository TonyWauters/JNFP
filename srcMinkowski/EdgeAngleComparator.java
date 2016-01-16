import java.util.Comparator;

public class EdgeAngleComparator implements Comparator<Edge> {

	@Override
	public int compare(Edge edge1, Edge edge2) {
		int diff = (int) ( 10000*(edge1.getEdgeAngle()-edge2.getEdgeAngle()));
		if(diff == 0){
			if(edge1.isPolygonA()&&!edge2.isPolygonA()){
				diff = 1;
			}
			else if(!edge1.isPolygonA()&&edge2.isPolygonA()){
				diff = -1;
			}
		}
		return diff;
	}

}
