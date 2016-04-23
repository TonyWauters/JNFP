import java.util.Comparator;

/**
 * @author Stiaan Uyttersprot
 *
 */
public class EdgeNumberComparator implements Comparator<Edge> {

	@Override
	public int compare(Edge edge1, Edge edge2) {
		
		return  (int) (edge1.getEdgeNumber()-edge2.getEdgeNumber());
	}

}
