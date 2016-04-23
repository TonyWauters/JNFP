import java.math.BigDecimal;
import java.util.Comparator;

/**
 * @author Stiaan Uyttersprot
 *
 */
public class TripIntersectionComparator implements Comparator<TripIntersection>{

	@Override
	public int compare(TripIntersection ti1, TripIntersection ti2) {
		
		int tripSequenceDiff = ti1.getIntersectionEdge().getTripSequenceNumber() - ti2.getIntersectionEdge().getTripSequenceNumber();
		if(tripSequenceDiff != 0) return tripSequenceDiff;
		//sort in sequence closest to the startpoint
		BigDecimal distanceToStart1 = ti1.getIntersectionPoint().distanceTo(ti1.getIntersectionEdge().getStartPoint());
		BigDecimal distanceToStart2 = ti2.getIntersectionPoint().distanceTo(ti2.getIntersectionEdge().getStartPoint());
		return distanceToStart1.compareTo(distanceToStart2);
	}
}
