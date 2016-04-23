package NFPGenerator;
import java.util.Comparator;

/**
 * Compares the edge numbers of polygons for the Minkowski sums method
 * @author Stiaan Uyttersprot
 *
 */
public class EdgeNumberComparator implements Comparator<Edge> {

	@Override
	public int compare(Edge edge1, Edge edge2) {
		
		return  (int) (edge1.getEdgeNumber()-edge2.getEdgeNumber());
	}

}
