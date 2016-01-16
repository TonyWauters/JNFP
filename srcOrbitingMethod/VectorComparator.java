import java.util.Comparator;

public class VectorComparator implements Comparator<Vector> {

	@Override
	public int compare(Vector vect1, Vector vect2) {
		int edgeDiff = vect1.getEdgeNumber()-vect2.getEdgeNumber();
		if(edgeDiff == 0 && vect1.isFromStatEdge() && !vect2.isFromStatEdge())return 1;
		if(edgeDiff == 0 && !vect1.isFromStatEdge() && vect2.isFromStatEdge())return -1;
		return  vect1.getEdgeNumber()-vect2.getEdgeNumber();
	}

}
