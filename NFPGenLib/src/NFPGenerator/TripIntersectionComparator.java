package NFPGenerator;
import java.util.Comparator;

/**
 * 
 * Used for comparisons in the Minkowski sums method
 * @author Stiaan Uyttersprot
 *
 */
public class TripIntersectionComparator implements Comparator<TripIntersection>{

	@Override
	public int compare(TripIntersection ti1, TripIntersection ti2) {
		
		int tripSequenceDiff = ti1.getIntersectionEdge().getTripSequenceNumber() - ti2.getIntersectionEdge().getTripSequenceNumber();
		if(tripSequenceDiff != 0) return tripSequenceDiff;
		//sort in sequence closest to the startpoint
		double distanceToStart1 = ti1.getIntersectionPoint().distanceTo(ti1.getIntersectionEdge().getStartPoint());
		double distanceToStart2 = ti2.getIntersectionPoint().distanceTo(ti2.getIntersectionEdge().getStartPoint());
		return (int) (distanceToStart1-distanceToStart2)*10000;
	}
}
