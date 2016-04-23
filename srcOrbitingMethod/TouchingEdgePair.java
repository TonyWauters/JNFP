
/**
 *         Pairs of edges that are touching used in the orbiting method are
 *         stored with the coordinates of the touching point
 *
 * @author Stiaan Uyttersprot
 * 

 */
public class TouchingEdgePair {

	
	private Edge statEdge;
	private Edge orbEdge;
	private Coordinate touchPoint;
	
	public static double angleRound = 1e-6;
	public static double round = 1e-4;
	
	// booleans saying if the touching point equals a start or end point from an
	// edge
	private boolean touchStatStart = false;
	private boolean touchStatEnd = false;

	private boolean touchOrbStart = false;
	private boolean touchOrbEnd = false;

	private double startAngle;
	private double endAngle;

	public TouchingEdgePair(Edge statEdge, Edge orbEdge, Coordinate touchPoint) {

		this.statEdge = statEdge;
		this.orbEdge = orbEdge;
		this.touchPoint = touchPoint;

		if (statEdge.getStartPoint().equalValuesRounded(touchPoint)) {
			touchStatStart = true;
		} else if (statEdge.getEndPoint().equalValuesRounded(touchPoint)) {
			touchStatEnd = true;
		}

		if (orbEdge.getStartPoint().equalValuesRounded(touchPoint)) {
			touchOrbStart = true;
		} else if (orbEdge.getEndPoint().equalValuesRounded(touchPoint)) {
			touchOrbEnd = true;
		}
		if(orbEdge.getStartPoint().equalValuesRounded(statEdge.getStartPoint())){
			touchStatStart = true;
			touchOrbStart = true;
		}
		if(orbEdge.getEndPoint().equalValuesRounded(statEdge.getEndPoint())){
			touchStatEnd = true;
			touchOrbEnd = true;
		}
		if(orbEdge.getStartPoint().equalValuesRounded(statEdge.getEndPoint())){
			
			touchStatEnd = true;
			touchOrbStart = true;
		}
		if(orbEdge.getEndPoint().equalValuesRounded(statEdge.getStartPoint())){
			touchStatStart = true;
			touchOrbEnd = true;
		}
	}

	public Edge getStatEdge() {
		return statEdge;
	}

	public void setStatEdge(Edge statEdge) {
		this.statEdge = statEdge;
	}

	public Edge getOrbEdge() {
		return orbEdge;
	}

	public void setOrbEdge(Edge orbEdge) {
		this.orbEdge = orbEdge;
	}

	public Coordinate getTouchPoint() {
		return touchPoint;
	}

	public void setTouchPoint(Coordinate touchPoint) {
		this.touchPoint = touchPoint;
	}

	public void print() {
		System.out.println("touching edge pair: ");
		statEdge.print();
		orbEdge.print();
		touchPoint.printCoordinate();
		System.out.println("start angle: " + Math.toDegrees(startAngle));
		System.out.println("end angle: " + Math.toDegrees(endAngle));
	}

	public Vector getPotentialVector() {

		Vector potentialVector = null;
		/*
		 * there are four possible ways that end or start points can be
		 * touching: stat orb ------------- end end start start start end end
		 * start
		 */
		// ---------------------------------------------------------------------------------------------------------------------
		// if the touching point is at the end of both edges, there will be no
		// potential vector
		if (touchStatEnd && touchOrbEnd)
			potentialVector = null;

		
		// ---------------------------------------------------------------------------------------------------------------------
		// if both startpoints are touching, the translationvector will be the
		// orbiting edge if the relative position
		// of the orbiting edge is left to the stationary edge (can be
		// determined with the D-function)
		// this by looking if the endpoint of the orbiting edge is located left
		// or right
		else if (touchStatStart && touchOrbStart) {
			// if Dfunction returns value > 0 the orbiting edge is left of the
			// stationary edge, and the translation
			// vector will be derived from the orbiting edge
			if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) > 0) {
				potentialVector =  orbEdge.makeFullVector(statEdge.getEdgeNumber());
				potentialVector.setParentEdge(orbEdge);
			} else {
				// if the D-function returns 0, edges are parallel, either edge
				// can be used.
				potentialVector =  statEdge.makeFullVector(statEdge.getEdgeNumber());
				potentialVector.setParentEdge(statEdge);
			}
		}
		// ---------------------------------------------------------------------------------------------------------------------
		else if (touchStatStart && touchOrbEnd) {
			// in this case, if the orbiting edge is located left of the
			// stationary edge, no vector will be possible
			// if it is on the right, the stationary edge will provide the
			// vector.
			if (orbEdge.getStartPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) > 0) {
				potentialVector = null;
			} else {
				potentialVector = statEdge.makeFullVector(statEdge.getEdgeNumber());
				potentialVector.setParentEdge(statEdge);
			}
		}
		// ---------------------------------------------------------------------------------------------------------------------
		else if (touchStatEnd && touchOrbStart) {
			if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) > 0) {
				potentialVector = null;
			} else {
				potentialVector = orbEdge.makeFullVector(statEdge.getEdgeNumber());
				potentialVector.setParentEdge(orbEdge);
			}
		}
		// ---------------------------------------------------------------------------------------------------------------------
		// the two other cases left are when one of the edges is touching the
		// other somewhere in between start and end point

		else if (touchStatStart || touchStatEnd) {
			potentialVector = orbEdge.makePartialVector(touchPoint, statEdge.getEdgeNumber());
			potentialVector.setParentEdge(orbEdge);
		}
		// ---------------------------------------------------------------------------------------------------------------------
		else if (touchOrbStart || touchOrbEnd) {
			potentialVector = statEdge.makePartialVector(touchPoint, statEdge.getEdgeNumber());
			potentialVector.setParentEdge(statEdge);
		}
		return potentialVector;
	}

	public void calcFeasibleAngleRange() {

		double stationaryAngle = statEdge.getAngle();
		if (stationaryAngle < 0)
			stationaryAngle = stationaryAngle + Math.PI * 2;

		double orbitingAngle = orbEdge.getAngle();
		if (orbitingAngle < 0)
			orbitingAngle = orbitingAngle + Math.PI * 2;

		//Situation 8: one edge is parallel with the other but starts at the end of the other one
		if ((stationaryAngle <= orbitingAngle + angleRound && stationaryAngle >= orbitingAngle - angleRound ) && ( (touchStatEnd && touchOrbStart)||(touchStatStart&&touchOrbEnd) ) ) {
			startAngle = stationaryAngle - Math.PI;
			endAngle = stationaryAngle + Math.PI;

			return;
		}
		//Situation 8.2: edges are parallel and end or start in the same point
		if (((stationaryAngle <= orbitingAngle  - Math.PI+ 1e-6 && stationaryAngle >= orbitingAngle  - Math.PI- angleRound )
				||(stationaryAngle <= orbitingAngle  + Math.PI+ 1e-6 && stationaryAngle >= orbitingAngle  + Math.PI- angleRound )
				&& ((touchStatEnd && touchOrbEnd)||(touchStatStart && touchOrbStart)))) {
			if(touchStatEnd && touchOrbEnd && touchStatStart && touchOrbStart){
				startAngle = stationaryAngle - Math.PI;
				endAngle = stationaryAngle;
				return;
			}
			startAngle = stationaryAngle - Math.PI;
			endAngle = stationaryAngle + Math.PI;

			return;
		}
		
		//Situation 7
		if ((stationaryAngle <= orbitingAngle + 1e-6 && stationaryAngle >= orbitingAngle - angleRound )
				||(stationaryAngle <= orbitingAngle  - Math.PI+ 1e-6 && stationaryAngle >= orbitingAngle  - Math.PI- angleRound )
				|| (stationaryAngle <= orbitingAngle  + Math.PI+ 1e-6 && stationaryAngle >= orbitingAngle  + Math.PI- angleRound )) {
			startAngle = stationaryAngle - Math.PI;
			endAngle = stationaryAngle;
			return;
		}
		
		
		// situation 1
		if (!touchStatStart && !touchStatEnd) {
			startAngle = stationaryAngle - Math.PI;
			endAngle = stationaryAngle;

			return;
		}

		// situation 2
		if (!touchOrbStart && !touchOrbEnd) {
			// stationary edge is located to the right of orbiting edge
			//we have to check the D-function for the start and end of the stationary edge to see if it is left or right, one of them will be zero, 
			//the other one will be smaller or bigger then zero
			if (statEdge.getEndPoint().dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint()) <= round 
					&& statEdge.getStartPoint().dFunction(orbEdge.getStartPoint(), orbEdge.getEndPoint()) <= round) {
				startAngle = orbitingAngle;
				endAngle = orbitingAngle + Math.PI;
			}
			// stationary edge is located to the right of orbiting edge
			else {
				startAngle = orbitingAngle - Math.PI;
				endAngle = orbitingAngle;
			}
			return;
		}

		// both angles are positive
		// situation 3
		if (touchStatStart && touchOrbStart) {

			// orbEdge is right of statEdge
			if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

				// I work with the negative angle of orbiting edge
				orbitingAngle -= 2 * Math.PI;

				startAngle = orbitingAngle - Math.PI;
				endAngle = stationaryAngle;

				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
				while (endAngle < startAngle)
					endAngle += 2 * Math.PI;
			}
			// orbEdge left of statEdge
			else {
				startAngle = stationaryAngle;
				endAngle = orbitingAngle + Math.PI;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;

			}
			return;
		}

		// situation 4
		if (touchStatStart && touchOrbEnd) {

			// orbEdge is right of statEdge
			if (orbEdge.getStartPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

				startAngle = orbitingAngle;
				endAngle = stationaryAngle;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			// orbEdge is left of statEdge
			else {
				startAngle = stationaryAngle;
				endAngle = orbitingAngle;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			return;
		}

		// situation 5
		if (touchStatEnd && touchOrbEnd) {

			// orbEdge is right of statEdge
			if (orbEdge.getStartPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

				startAngle = stationaryAngle - Math.PI;
				endAngle = orbitingAngle;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			// orbEdge is left of statEdge
			else {
				startAngle = orbitingAngle;
				endAngle = stationaryAngle + Math.PI;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			return;
		}

		// situation 6
		if (touchStatEnd && touchOrbStart) {

			// orbEdge is right of statEdge
			if (orbEdge.getEndPoint().dFunction(statEdge.getStartPoint(), statEdge.getEndPoint()) < 0) {

				startAngle = stationaryAngle - Math.PI;
				endAngle = orbitingAngle + Math.PI;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			// orbEdge is left of statEdge
			else {
				startAngle = orbitingAngle - Math.PI;
				endAngle = stationaryAngle + Math.PI;

				while (endAngle < startAngle)
					startAngle -= 2 * Math.PI;
				while (endAngle - 2 * Math.PI >= startAngle)
					startAngle += 2 * Math.PI;
			}
			return;
		}
	}

	public boolean isFeasibleVector(Vector vector) {
		
		// test all possible ranges
		double vectorAngle = vector.getVectorAngle();		
		
		if (startAngle <= vectorAngle && vectorAngle <= endAngle)
			return true;

		double rotatedVectorAngle = vectorAngle + 2 * Math.PI;
		if (startAngle <= rotatedVectorAngle && rotatedVectorAngle <= endAngle)
			return true;

		double negativeRotatedVectorAngle = vectorAngle - 2 * Math.PI;
		if (startAngle <= negativeRotatedVectorAngle && negativeRotatedVectorAngle <= endAngle)
			return true;

		return false;
	}
	
	public boolean isFeasibleVectorWithRounding(Vector vector) {
		
		// test all possible ranges
		double vectorAngle = vector.getVectorAngle();		
		
		if (startAngle-angleRound <= vectorAngle && vectorAngle <= endAngle+angleRound)
			return true;

		double rotatedVectorAngle = vectorAngle + 2 * Math.PI;
		if (startAngle-angleRound <= rotatedVectorAngle && rotatedVectorAngle <= endAngle+angleRound)
			return true;

		double negativeRotatedVectorAngle = vectorAngle - 2 * Math.PI;
		if (startAngle-angleRound <= negativeRotatedVectorAngle && negativeRotatedVectorAngle <= endAngle+angleRound)
			return true;

		return false;
	}

}
