import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Minkowski {
	static Boolean clockwiseContainsTurningpoints;
	public static NoFitPolygon generateMinkowskiNFP(MultiPolygon polyA, MultiPolygon polyB) {
		
		NoFitPolygon nfp = null;
		clockwiseContainsTurningpoints = null;
		//---------------------------------------------------------------------------------------------
		//Generate Minkowski sum edge list
		
		//polyA has to be counterclockwise
		if(polyA.checkClockwise(polyA.getOuterPolygon()))polyA.changeClockOrientation(polyA.getOuterPolygon());
		//polyB has to be counterclockwise
		if(polyB.checkClockwise(polyB.getOuterPolygon()))polyB.changeClockOrientation(polyB.getOuterPolygon());
	
		polyA.labelCounterClockwise();
		polyB.labelCounterClockwise();

		polyB.replaceByNegative();
		
//		polyA.calcDeltaAngles();
//		polyB.calcDeltaAngles();
		
		//TODO: is het met de hoeken in de zin van de edge(die in wijzerzin staat) of tegen die zin in
//		polyB.changeEdgeAnglesCounterClockwise();
		
		List <Edge> polyASortList = new ArrayList<Edge>();
		List <Edge> polyBSortList = new ArrayList<Edge>();
		
		for(Edge e: polyA.getOuterPolygonEdges()){
			e.setPolygonA(true);
			polyASortList.add(e);
		}
		
		Collections.sort(polyASortList, new EdgeNumberComparator());
		
		for(Edge e: polyB.getOuterPolygonEdges()){
			e.setPolygonA(false);
			polyBSortList.add(e);
		}
		Collections.sort(polyBSortList, new EdgeNumberComparator());
		
		calcDeltaAngles(polyASortList);
		calcDeltaAngles(polyBSortList);
		
		printEdgeList(polyASortList);
		System.out.println();
		printEdgeList(polyBSortList);
		
		//divide B in groups by turningpoint
		List<List<Edge>> dividedListB = new ArrayList<>();
		List<Edge> turningGroupB = new ArrayList<>();
		
		boolean isConcaveB = false;
		boolean isConcaveA = false;
		
		for(Edge e: polyBSortList){
			if(e.isTurningPoint()){
				isConcaveB = true;
				break;
			}
		}
		for(Edge e: polyASortList){
			if(e.isTurningPoint()){
				isConcaveA = true;
				break;
			}
		}
		int i = 0;
		int aantalToegevoegd = 0;
		if(isConcaveB){
			while(aantalToegevoegd<polyBSortList.size()){
				//find starting turning point
				while(!polyBSortList.get(i).isTurningPoint()){
					//i = (i+1)%polyBSortList.size();
					i--;
					if(i<0)i = polyBSortList.size()-1;
				}
				turningGroupB.add(polyBSortList.get(i));
				aantalToegevoegd++;
				i = (i+1)%polyBSortList.size();
				while(!polyBSortList.get(i).isTurningPoint()){
					turningGroupB.add(polyBSortList.get(i));
					aantalToegevoegd++;
					i = (i+1)%polyBSortList.size();
				}
				//stop at next turning point
				turningGroupB.add(polyBSortList.get(i));
				aantalToegevoegd++;
				dividedListB.add(turningGroupB);
				i = (i+1)%polyBSortList.size();
				
				if(!polyBSortList.get(i).isTurningPoint()){
					turningGroupB = new ArrayList<>();
					while(!polyBSortList.get(i).isTurningPoint()){
						turningGroupB.add(polyBSortList.get(i));
						aantalToegevoegd++;
						i = (i+1)%polyBSortList.size();
					}
					dividedListB.add(turningGroupB);
				}
				if(aantalToegevoegd<polyBSortList.size()){
					turningGroupB = new ArrayList<>();
				}
			}
			if(aantalToegevoegd>polyBSortList.size())System.err.println("teveel toegevoegd aan de dividedListB");
			
		}
		else{
			for(Edge e: polyBSortList){
				turningGroupB.add(e);
			}
			dividedListB.add(turningGroupB);
		}
		
		for(List<Edge> edgeList: dividedListB){
			System.out.println("group: ");
			
			printSimpleEdgeList(edgeList);
			
		}
		List <List<Edge>> seqList = new ArrayList<>();
		List<Edge> msEdgeList = new ArrayList<>();
		
		Edge helpEdge;
		List<Edge> helpEdgeList;
		
		i = 0;
		System.out.println(dividedListB.size());
		
		for(List<Edge> edgeListB: dividedListB){
			if(clockwiseB(edgeListB)){
				helpEdgeList = MinkNeg(polyASortList, edgeListB);
			}
			else{
				helpEdgeList = MinkPos(polyASortList, edgeListB);
			}
			seqList.add(helpEdgeList);
			if(seqList.size()==1){
				msEdgeList.addAll(helpEdgeList);
				
			}
			else{
				i = 0;
				while(!helpEdgeList.get(i).isPolygonA()){
					i++;
				}
				int edgeAFinishNumber = helpEdgeList.get(i).getEdgeNumber();
				i = msEdgeList.size()-1;
				while(msEdgeList.get(i).isPolygonA()){
					msEdgeList.remove(i);
					i--;
					
				}
				while(!msEdgeList.get(i).isPolygonA()){
					i--;
				}
				
				int edgeAStartNumber = msEdgeList.get(i).getEdgeNumber();
				
				//last edgelist had postive a values, and new edgeList too
				if(edgeAFinishNumber>0 && edgeAStartNumber>0){
					i = polyASortList.size()-1;
					//find the place of the start edgeA in the polyAlist
					while(polyASortList.get(i).getEdgeNumber()> edgeAStartNumber){
						i--;
					}
					boolean isReached = false;
					//descending the edges of A untill the finish edge is reached
					while(!isReached){
						
						helpEdge = new Edge(polyASortList.get(i));
						if(helpEdge.getEdgeNumber()== edgeAFinishNumber)isReached = true;
						helpEdge.setEdgeNumber(helpEdge.getEdgeNumber()*-1);
						msEdgeList.add(helpEdge);
						i--;
						if(i<0)i = polyASortList.size()-1;
					}
					msEdgeList.addAll(helpEdgeList);
				}
				if(edgeAFinishNumber < 0 && edgeAStartNumber>0){
					i = polyASortList.size()-1;
					//find the place of the start edgeA in the polyAlist
					while(polyASortList.get(i).getEdgeNumber()> edgeAStartNumber){
						i--;
					}
					boolean isReached = false;
					//descending the edges of A untill the finish edge is reached
					while(!isReached){
						
						helpEdge = new Edge(polyASortList.get(i));
						if(helpEdge.getEdgeNumber()== edgeAFinishNumber*-1)isReached = true;
						if(!isReached){
							helpEdge.setEdgeNumber(helpEdge.getEdgeNumber()*-1);
							msEdgeList.add(helpEdge);
						}
						i--;
						if(i<0)i = polyASortList.size()-1;
					}
					msEdgeList.addAll(helpEdgeList);
				}
				//when the startNumber is negatif we will add positive edges untill finish is reached
				if(edgeAFinishNumber > 0 && edgeAStartNumber < 0){
					i = 0;
					System.out.println("sit 3");
					//find the place of the start edgeA in the polyAlist
					while(polyASortList.get(i).getEdgeNumber() < edgeAStartNumber*-1){
						i++;
					}
					boolean isReached = false;
					//descending the edges of A untill the finish edge is reached
					while(!isReached){
						
						helpEdge = new Edge(polyASortList.get(i));
						if(helpEdge.getEdgeNumber()== edgeAFinishNumber)isReached = true;
						if(!isReached){
							helpEdge.setEdgeNumber(helpEdge.getEdgeNumber());
							msEdgeList.add(helpEdge);
						}
						i = (i+1)%polyASortList.size();
						if(i<0)i = polyASortList.size()-1;
					}
					msEdgeList.addAll(helpEdgeList);
				}
				if(edgeAFinishNumber < 0 && edgeAStartNumber < 0){
					i = 0;
					System.out.println("sit 4");
					//find the place of the start edgeA in the polyAlist
					while(polyASortList.get(i).getEdgeNumber() > edgeAStartNumber*-1){
						i++;
					}
					boolean isReached = false;
					//descending the edges of A untill the finish edge is reached
					while(!isReached){
						
						helpEdge = new Edge(polyASortList.get(i));
						if(helpEdge.getEdgeNumber()== edgeAFinishNumber)isReached = true;
						helpEdge.setEdgeNumber(helpEdge.getEdgeNumber());
						msEdgeList.add(helpEdge);
						i++;
						if(i<0)i = polyASortList.size()-1;
					}
					msEdgeList.addAll(helpEdgeList);
				}
				
			}
		}
		//link the last edge to the first
		List<Edge> linkingEdgeList = new ArrayList<>();
		i = 0;
		while(!msEdgeList.get(i).isPolygonA()){
			i++;
		}
		int edgeAFinishNumber = msEdgeList.get(i).getEdgeNumber();
		i = msEdgeList.size()-1;
		while(!msEdgeList.get(i).isPolygonA()){
			i--;
		}
		int edgeAStartNumber = msEdgeList.get(i).getEdgeNumber();
		//last edgelist had postive a values, and new edgeList too
		if(edgeAFinishNumber>0 && edgeAStartNumber>0){
			i = polyASortList.size()-1;
			//find the place of the start edgeA in the polyAlist
			while(polyASortList.get(i).getEdgeNumber()> edgeAStartNumber){
				i--;
			}
			boolean isReached = false;
			//descending the edges of A untill the finish edge is reached
			while(!isReached){
				
				helpEdge = new Edge(polyASortList.get(i));
				if(helpEdge.getEdgeNumber()== edgeAFinishNumber)isReached = true;
				helpEdge.setEdgeNumber(helpEdge.getEdgeNumber()*-1);
				msEdgeList.add(helpEdge);
				i--;
				if(i<0)i = polyASortList.size()-1;
			}
			msEdgeList.addAll(linkingEdgeList);
		}
		if(edgeAFinishNumber < 0 && edgeAStartNumber>0){
			i = polyASortList.size()-1;
			//find the place of the start edgeA in the polyAlist
			while(polyASortList.get(i).getEdgeNumber()> edgeAStartNumber){
				i--;
			}
			boolean isReached = false;
			//descending the edges of A untill the finish edge is reached
			while(!isReached){
				
				helpEdge = new Edge(polyASortList.get(i));
				if(helpEdge.getEdgeNumber()== edgeAFinishNumber*-1)isReached = true;
				if(!isReached){
					helpEdge.setEdgeNumber(helpEdge.getEdgeNumber()*-1);
					msEdgeList.add(helpEdge);
				}
				i--;
				if(i<0)i = polyASortList.size()-1;
			}
			msEdgeList.addAll(linkingEdgeList);
		}
		//when the startNumber is negatif we will add positive edges untill finish is reached
		if(edgeAFinishNumber > 0 && edgeAStartNumber < 0){
			i = 0;
//			System.out.println("sit 3");
			//find the place of the start edgeA in the polyAlist
			while(polyASortList.get(i).getEdgeNumber() < edgeAStartNumber*-1){
				i++;
			}
			boolean isReached = false;
			//descending the edges of A untill the finish edge is reached
			while(!isReached){
				
				helpEdge = new Edge(polyASortList.get(i));
				if(helpEdge.getEdgeNumber()== edgeAFinishNumber)isReached = true;
				if(!isReached){
					helpEdge.setEdgeNumber(helpEdge.getEdgeNumber());
					msEdgeList.add(helpEdge);
				}
				i++;
				if(i<0)i = polyASortList.size()-1;
			}
			msEdgeList.addAll(linkingEdgeList);
		}
		if(edgeAFinishNumber < 0 && edgeAStartNumber < 0){
			i = 0;
//			System.out.println("sit 4");
			//find the place of the start edgeA in the polyAlist
			while(polyASortList.get(i).getEdgeNumber() > edgeAStartNumber*-1){
				i++;
			}
			boolean isReached = false;
			//descending the edges of A untill the finish edge is reached
			while(!isReached){
				
				helpEdge = new Edge(polyASortList.get(i));
				if(helpEdge.getEdgeNumber()== edgeAFinishNumber)isReached = true;
				helpEdge.setEdgeNumber(helpEdge.getEdgeNumber());
				msEdgeList.add(helpEdge);
				i++;
				if(i<0)i = polyASortList.size()-1;
			}
			msEdgeList.addAll(linkingEdgeList);
		}
		
		System.out.println("msEdgeList: ");
		printSimpleEdgeList(msEdgeList);
		
		List<Edge> complexPolygonEdgeList = makeIntoPolygon(msEdgeList);
		System.out.println("complexPolygonEdgeList: ");
		printEdgeList(complexPolygonEdgeList);
		ComplexPolygonStage.addComplexPolygon(complexPolygonEdgeList);
		//---------------------------------------------------------------------------------------------------------------------------------------
		//algorithm 2
		List<List<Edge>> trackLineTripList;
		trackLineTripList = makeTrackLineTrips(complexPolygonEdgeList);
		ComplexPolygonStage.addTrackLineTrips(trackLineTripList);
		for(List<Edge> trackLineTrip: trackLineTripList){
			System.out.println("trackLineTrip");
			for(Edge e: trackLineTrip){
				System.out.println(e);
			}
		}
//		
//		//algorithm 3
		List<List<Edge>> cycleList;
		System.out.println("algorithm 3");
		
		cycleList = boundarySearch(trackLineTripList);
//		ComplexPolygonStage.addTrackLineTrips(cycleList);
//		
//		for(List<Edge> cycle: cycleList){
//			System.out.println("cycle");
//			for(Edge e: cycle){
//				System.out.println(e);
//			}
//		}
		
		return nfp;
		
	}

	private static List<Edge> makeIntoPolygon(List<Edge> msEdgeList) {
		List<Vector> msVectorList;
		List<Edge> complexPolygonEdges = new ArrayList<>();
		msVectorList = getVectorList(msEdgeList);
		int startIndex = 0;
		Coordinate startCoord;
//		for(int i = 0; i< msEdgeList.size();i++){
//			if(msEdgeList.get(i).getEdgeNumber() == 1 && !msEdgeList.get(i).isPolygonA()){
//				startIndex = i;
//				break;
//			}
//		}
//		startCoord = new Coordinate(msEdgeList.get(startIndex).getStartPoint());
//		if(!msEdgeList.get(startIndex).isPolygonA())startCoord.replaceByNegative();
//		System.out.println("startCoord: " + startCoord);
		
		startCoord = new Coordinate(0,0);
		
		Edge complexEdge;// = bStart.getEdgeWithTranslation(msVectorList.get(startIndex), msEdgeList.get(0));
//		complexPolygonEdges.add(complexEdge);
		Coordinate startPoint;
		int index = startIndex;
		
		while(complexPolygonEdges.size()<msEdgeList.size()){
			if(complexPolygonEdges.size()==0){
				startPoint = startCoord;
			}
			else{
				startPoint = complexPolygonEdges.get(complexPolygonEdges.size()-1).getEndPoint();
			}
//			System.out.println();
//			System.out.println("edge: " + msEdgeList.get(index));
//			System.out.println("vector: " + msVectorList.get(index).toString());
//			System.out.println("startPoint: " + startPoint);
			
			complexEdge = startPoint.getEdgeWithTranslation(msVectorList.get(index), msEdgeList.get(index));
			
//			System.out.println("new edge: " + complexEdge);
			complexPolygonEdges.add(complexEdge);
			index = (index+1)%msEdgeList.size();
		}
		return complexPolygonEdges;
	}

	private static List<Vector> getVectorList(List<Edge> msEdgeList) {
		List<Vector> vectorList = new ArrayList<Vector>();
		Vector vect;
		for(Edge e: msEdgeList){
			vect = e.makeFullVector(e.getEdgeNumber());
			vectorList.add(vect);
		}
//		for(Vector vector: vectorList){
//			System.out.println(vector);
//		}
		return vectorList;
	}

	private static void printEdgeList(List<Edge> list) {
		for(Edge e: list){
			System.out.println(e);
		}
		
	}
	private static void printSimpleEdgeList(List<Edge> list) {
		for(Edge e: list){
			if(e.isPolygonA()){
				System.out.print("a"+e.getEdgeNumber());
			}
			else System.out.print("b"+e.getEdgeNumber());
			System.out.print(", ");
		}
		System.out.println();
	}

	//	calculate if polygon B is clockwise or not (can differ if you take the edge order as clockwise values, or the direction of the vectors)
	private static boolean clockwiseB(List<Edge> edgeList) {
		
//		double clockwiseValue = 0;
//		
//		double xDiff;
//		double ySum;
//		
//		Coordinate endCoord;
//		Coordinate beginCoord;
//		//If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
//		for(Edge e: edgeList){
//			//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
//			endCoord = e.getStartPoint();
//			beginCoord = e.getEndPoint();
//			xDiff = endCoord.getxCoord() - beginCoord.getxCoord();
//			ySum = endCoord.getyCoord() + beginCoord.getyCoord();
//			clockwiseValue += xDiff*ySum;
//		}
//		
//		if(clockwiseValue < 0) return true;
//		else return false;
		
		if(clockwiseContainsTurningpoints == null){
			
			double clockwiseValue = 0;
			
			double xDiff;
			double ySum;
			
			Coordinate endCoord;
			Coordinate beginCoord;
			//If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
			for(Edge e: edgeList){
				//Sum over the edges, (x2-x1)(y2+y1). If the result is positive the curve is clockwise, if it's negative the curve is counter-clockwise.
				endCoord = e.getStartPoint();
				beginCoord = e.getEndPoint();
				xDiff = endCoord.getxCoord() - beginCoord.getxCoord();
				ySum = endCoord.getyCoord() + beginCoord.getyCoord();
				clockwiseValue += xDiff*ySum;
			}
			
			if(clockwiseValue < 0) clockwiseContainsTurningpoints = true;
			else clockwiseContainsTurningpoints = false;
			
		}

		if(clockwiseContainsTurningpoints){
			if(edgeList.get(0).isTurningPoint()){
				return true;
			}
			else return false;
		}
		else{
			if(edgeList.get(0).isTurningPoint()){
				return false;
			}
			else return true;
		}
		
	}

	private static void calcDeltaAngles(List<Edge> polySortList) {
		
		for(int i = 0; i< polySortList.size();i++){
			
			if(i > 0){
				polySortList.get(i).setDeltaAngle(polySortList.get(i).getEdgeAngle() - polySortList.get(i-1).getEdgeAngle());
			}
			else{
				polySortList.get(i).setDeltaAngle(polySortList.get(i).getEdgeAngle() - polySortList.get(polySortList.size()-1).getEdgeAngle());
			}
			if(polySortList.get(i).getDeltaAngle() > Math.PI){
				polySortList.get(i).setDeltaAngle(polySortList.get(i).getDeltaAngle() - 2* Math.PI);
			}
			else if(polySortList.get(i).getDeltaAngle() < -Math.PI){
				
				polySortList.get(i).setDeltaAngle(polySortList.get(i).getDeltaAngle() + 2* Math.PI);
			}
		}
		for(int i = 0; i< polySortList.size();i++){
//			if(i > 0){
//				if(Math.signum(polySortList.get(i).getDeltaAngle())!= Math.signum(polySortList.get(i-1).getDeltaAngle())){
//					polySortList.get(i).setTurningPoint(true);
//				}
//			}
//			else{
//				if(Math.signum(polySortList.get(i).getDeltaAngle())!= Math.signum(polySortList.get(polySortList.size()-1).getDeltaAngle())){
//					polySortList.get(i).setTurningPoint(true);
//				}
//			}
			if(Math.signum(polySortList.get(i).getDeltaAngle())!= Math.signum(polySortList.get((i+1)%polySortList.size()).getDeltaAngle())){
				polySortList.get(i).setTurningPoint(true);
			}
		}
	}
	
	private static List<Edge> MinkPos(List<Edge> qList, List<Edge> rList) {
		
		System.out.println();
		System.out.println("qList");
		printSimpleEdgeList(qList);
		System.out.println("rList");
		printSimpleEdgeList(rList);
		
		List<Edge> mergeList = new ArrayList <> ();
		mergeList.addAll(qList);
		mergeList.addAll(rList);
		Collections.sort(mergeList, new EdgeAngleComparator());
	
		System.out.println("mergelist:");
		printSimpleEdgeList(mergeList);
		
		List<Edge> sList = new ArrayList<>();
		
		int i = 0;
		int direction = 1;
		
		direction = 1;
		
		Edge helpEdge;
		Edge qi;
		boolean qiFound = false;
		
//		if(qList.get(0).isTurningPoint()){
//			direction = -1;
//		}
		sList.add(new Edge(qList.get(0)));
		int mergeListStartPosition = 0;
		while(!mergeList.get(mergeListStartPosition).isPolygonA() || mergeList.get(mergeListStartPosition).getEdgeNumber() != qList.get(0).getEdgeNumber()){
			mergeListStartPosition++;
		}
		int checkPos;
		int position = mergeListStartPosition;
//		System.out.println(mergeListStartPosition);
		boolean toStep4 = false;
		do{
			i = (i+1)%qList.size();
			qi = new Edge(qList.get(i));
			qiFound = false;
			
			if(direction>0){
//				System.out.println("positive direction");
				//moving forward through mergeList looking for Qi
				
				while(!qiFound){
//					System.out.println("position: " + position);
					//if from R
					if(!mergeList.get(position).isPolygonA()){
						
						helpEdge = new Edge(mergeList.get(position));
						helpEdge.changeEdgeNumber(direction);
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("edgeOrigin: b" + mergeList.get(position).getEdgeNumber());
						sList.add(helpEdge);
					}
					else{//if from Q
						if(mergeList.get(position).getEdgeNumber() == qi.getEdgeNumber()){
							qiFound = true;
							if(i==0){
//								System.out.println("go to step 4");
								toStep4 = true;
							}
							else{
								
								
								if(qi.isTurningPoint()){
									boolean sameAngle = true;
									boolean hasSameAngleB = false;
									int z = 0;
									do{
										z++;
										checkPos = (position+z)%mergeList.size();
										if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
												Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
											if(!mergeList.get(checkPos).isPolygonA()){
												helpEdge = new Edge(mergeList.get(checkPos));
												helpEdge.changeEdgeNumber(direction);
												sList.add(helpEdge);
	//											System.out.println("extra add");
	//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
												hasSameAngleB = true;
											}
										}
										else sameAngle = false;
									}while(sameAngle);
									
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
									
									direction = -1*direction;
									
//									System.out.println("qi is turningpoint: " + direction );
									if(hasSameAngleB){
										z = 0;
										sameAngle = true;
										do{
											z++;
											checkPos = (position+z)%mergeList.size();
											if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
													Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
												if(!mergeList.get(checkPos).isPolygonA()){
													helpEdge = new Edge(mergeList.get(checkPos));
													helpEdge.changeEdgeNumber(direction);
													sList.add(helpEdge);
		//											System.out.println("extra add");
		//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
													hasSameAngleB = true;
												}
											}
											else sameAngle = false;
										}while(sameAngle);
									}
								}
								else{
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
								}
//								System.out.println("repeat step 3");
								
							}
						}
					}
					
					position = (position+direction)%mergeList.size();
					if(position<0){
						position = mergeList.size()-1;
					}
				}
				
			}
			else if(direction<0){
//				System.out.println("negative direction");
				//moving backwards through mergeList looking for Qi
				while(!qiFound){
//					System.out.println("position: " + position);
					if(!mergeList.get(position).isPolygonA()){
						helpEdge = new Edge(mergeList.get(position));
						helpEdge.changeEdgeNumber(direction);
//						System.out.println("direction: " + direction);
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("edgeOrigin: b" + mergeList.get(position).getEdgeNumber());
						sList.add(helpEdge);
					}
					else{//if from Q
						if(mergeList.get(position).getEdgeNumber() == qi.getEdgeNumber()){
							qiFound = true;
							if(i==0){
//								System.out.println("go to step 4");
								toStep4 = true;
							}
							else{
								
								if(qi.isTurningPoint()){
									int z = 0;
									
									boolean sameAngle = true;
									boolean hasSameAngleB = false;
									do{
										z++;
										if(position-z<0){
											checkPos = mergeList.size()-(position-z);
										}
										else{
											checkPos = (position-z);
										}
										if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
												Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
											if(!mergeList.get(checkPos).isPolygonA()){

												helpEdge = new Edge(mergeList.get(checkPos));
												helpEdge.changeEdgeNumber(direction);
//												System.out.println("extra add");
//												System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
												sList.add(helpEdge);
												hasSameAngleB = true;
											}
										}
										else{
											sameAngle = false;
										}
									}while(sameAngle);
									
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
									
									direction= -1*direction;
									
//									System.out.println("qi is turningpoint: " + direction );
									if(hasSameAngleB){
										z = 0;
										sameAngle = true;
										do{
											z++;
											if(position-z<0){
												checkPos = mergeList.size()-(position-z);
											}
											else{
												checkPos = (position-z);
											}
											
											if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
													Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
												if(!mergeList.get(checkPos).isPolygonA()){

													helpEdge = new Edge(mergeList.get(checkPos));
													helpEdge.changeEdgeNumber(direction);
//													System.out.println("extra add");
//													System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
													sList.add(helpEdge);
													hasSameAngleB = true;
												}
											}
											else{
												sameAngle = false;
											}
										}while(sameAngle);
										
										
									}
								}
								else{
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
								}
//								System.out.println("repeat step 3");
							}
						}
					}
					position= (position + direction)%mergeList.size();
//					System.out.println("direction: " + direction);
//					System.out.println("new position: " + position);
					if(position < 0){
						position = mergeList.size()-1;
					}
				}
			}
			
		}while(i!=0&&!toStep4);
		
		Edge startingEdge = rList.get(0);
		
		i = 0;
		
		System.out.println("rList");
		printSimpleEdgeList(rList);
		
		System.out.println("sList");
		printSimpleEdgeList(sList);
		
		
//		for(Edge e: sList){
//			if(!e.isPolygonA()&&e.getEdgeNumber()==startingEdge.getEdgeNumber()){
//				break;
//			}
//			i++;
//		}
		
		int start = 0;
		boolean startFound = false;
		for(Edge e: sList){
			if(!e.isPolygonA()&&e.getEdgeNumber()==startingEdge.getEdgeNumber()){
				start = i;
				startFound = true;
			}
			if(startFound && e.getEdgeNumber() == startingEdge.getEdgeNumber()*-1){
				break;
			}
			i++;
		}
		
		//i is the position of r0 in the mergeList
		i =start;
		int j = 0;
		int next = 1;
		
		direction = 1;
		List<Edge> seqList = new ArrayList<>();
		Edge si = new Edge(sList.get(i));
		seqList.add(si);
		
		while(j<sList.size()-1){
			i = (i+1)%sList.size();
			
			si = new Edge(sList.get(i));
			
			if(si.isPolygonA()){
				j = j+1;
				seqList.add(si);
				
				if(si.isTurningPoint()){
					direction*=-1;
					next = next+direction;
					if(next<0){
//						System.out.println("next is: " + next);
						next = rList.size()-1;
//						System.out.println("next is " + next);
					}
					if(next>rList.size()-1){
//						System.out.println("next is: " + next);
						next = 0;
						
					}
//					System.out.println("next is " + next + " in direction " + direction);
				}
			}
			else if(si.getEdgeNumber() == direction * rList.get(next).getEdgeNumber()){
				j = j+1;
				seqList.add(si);
//				System.out.println("added b" + si.getEdgeNumber());
				next = next+direction;
				if(next<0){
//						System.out.println("next is: " + next);
					next = rList.size()-1;
//						System.out.println("changed to: " + next);
				}
				if(next>rList.size()-1){
//						System.out.println("next is: " + next);
					next = 0;
//						System.out.println("changed to: " + next);
				}
//				System.out.println("next is " + next + " in direction " + direction);
			}
			
		}

		System.out.println("seqList");
		printSimpleEdgeList(seqList);
		return seqList;
		
	}
	
	

	private static List<List<Edge>> makeTrackLineTrips(List<Edge> mList) {
		
		int i = 0; //number of Minkowski sums obtained
		int j = 0; //number of track line trips with nj number of edges in track line trip j;
		int k = 0; //index of each track line trip
		List<List<Edge>> trackLineTripList = new ArrayList <>();
		List<Edge> trackLineTrip = new ArrayList<>();
		trackLineTripList.add(trackLineTrip);
		Edge tjk;
		boolean positiveFound = true;
		
		while(positiveFound){
			while(i<mList.size() && mList.get(i).getEdgeNumber()<0){
				i++;
			}
			if(i<mList.size()){//gevonden
				trackLineTrip = new ArrayList<>();
				trackLineTripList.add(trackLineTrip);
				tjk = mList.get(i);
				trackLineTrip.add(tjk);
				i++;
				k++;
				
				while(i< mList.size() && mList.get(i).getEdgeNumber()>0 && correspondsToTrackLine(mList.get(i), trackLineTripList)){
					tjk = mList.get(i);
					trackLineTrip.add(tjk);
					i++;
					k++;
				}
				
			}
			else{
				positiveFound = false; 
			}
		}
		return trackLineTripList;
		
	}
	//we work with aList and bList in this method because we don't want to reverse the original here (MinkPos doesn't need reversal)
	private static List<Edge> MinkNeg(List<Edge> aList, List<Edge> bList) {
		
		System.out.println();
		
		List<Edge> mergeList = new ArrayList <> ();
		mergeList.addAll(aList);
		mergeList.addAll(bList);
		
		List<Edge> qList = new ArrayList<>();
		qList.addAll(aList);
		
		List<Edge> rList = new ArrayList<>();
		rList.addAll(bList);
		
		Collections.sort(mergeList, new EdgeAngleComparator());
		Collections.reverse(mergeList);
		Collections.reverse(qList);
		
//		Collections.reverse(rList);//danger zone
		
		System.out.println("qList");
		printSimpleEdgeList(aList);
		System.out.println("rList");
		printSimpleEdgeList(bList);
		
		System.out.println("mergelist:");
		printSimpleEdgeList(mergeList);
		
		List<Edge> sList = new ArrayList<>();
		
		int i = 0;
		int direction = 1;
		
		direction = 1;
		
		Edge helpEdge;
		Edge qi;
		boolean qiFound = false;
		
//		if(qList.get(0).isTurningPoint()){
//			direction = -1;
//		}
		sList.add(new Edge(qList.get(0)));
		int mergeListStartPosition = 0;
		while(!mergeList.get(mergeListStartPosition).isPolygonA() || mergeList.get(mergeListStartPosition).getEdgeNumber() != qList.get(0).getEdgeNumber()){
			mergeListStartPosition++;
		}
		int checkPos;
		int position = mergeListStartPosition;
//		System.out.println(mergeListStartPosition);
		boolean toStep4 = false;
		do{
			i = (i+1)%qList.size();
			qi = new Edge(qList.get(i));
			qiFound = false;
			
			if(direction>0){
//				System.out.println("positive direction");
				//moving forward through mergeList looking for Qi
				
				while(!qiFound){
//					System.out.println("position: " + position);
					//if from R
					if(!mergeList.get(position).isPolygonA()){
						
						helpEdge = new Edge(mergeList.get(position));
						helpEdge.changeEdgeNumber(direction);
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("edgeOrigin: b" + mergeList.get(position).getEdgeNumber());
						sList.add(helpEdge);
					}
					else{//if from Q
						if(mergeList.get(position).getEdgeNumber() == qi.getEdgeNumber()){
							qiFound = true;
							if(i==0){
//								System.out.println("go to step 4");
								toStep4 = true;
							}
							else{
								
								
								if(qi.isTurningPoint()){
									boolean sameAngle = true;
									boolean hasSameAngleB = false;
									int z = 0;
									do{
										z++;
										checkPos = (position+z)%mergeList.size();
										if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
												Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
											if(!mergeList.get(checkPos).isPolygonA()){
												helpEdge = new Edge(mergeList.get(checkPos));
												helpEdge.changeEdgeNumber(direction);
												sList.add(helpEdge);
	//											System.out.println("extra add");
	//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
												hasSameAngleB = true;
											}
										}
										else sameAngle = false;
									}while(sameAngle);
									
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
									
									direction = -1*direction;
									
//									System.out.println("qi is turningpoint: " + direction );
									if(hasSameAngleB){
										z = 0;
										sameAngle = true;
										do{
											z++;
											checkPos = (position+z)%mergeList.size();
											if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
													Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
												if(!mergeList.get(checkPos).isPolygonA()){
													helpEdge = new Edge(mergeList.get(checkPos));
													helpEdge.changeEdgeNumber(direction);
													sList.add(helpEdge);
		//											System.out.println("extra add");
		//											System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
													hasSameAngleB = true;
												}
											}
											else sameAngle = false;
										}while(sameAngle);
									}
								}
								else{
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
								}
//								System.out.println("repeat step 3");
								
							}
						}
					}
					
					position = (position+direction)%mergeList.size();
					if(position<0){
						position = mergeList.size()-1;
					}
				}
				
			}
			else if(direction<0){
//				System.out.println("negative direction");
				//moving backwards through mergeList looking for Qi
				while(!qiFound){
//					System.out.println("position: " + position);
					if(!mergeList.get(position).isPolygonA()){
						helpEdge = new Edge(mergeList.get(position));
						helpEdge.changeEdgeNumber(direction);
//						System.out.println("direction: " + direction);
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
//						System.out.println("edgeOrigin: b" + mergeList.get(position).getEdgeNumber());
						sList.add(helpEdge);
					}
					else{//if from Q
						if(mergeList.get(position).getEdgeNumber() == qi.getEdgeNumber()){
							qiFound = true;
							if(i==0){
//								System.out.println("go to step 4");
								toStep4 = true;
							}
							else{
								
								if(qi.isTurningPoint()){
									int z = 0;
									
									boolean sameAngle = true;
									boolean hasSameAngleB = false;
									do{
										z++;
										if(position-z<0){
											checkPos = mergeList.size()-(position-z);
										}
										else{
											checkPos = (position-z);
										}
										if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
												Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
											if(!mergeList.get(checkPos).isPolygonA()){

												helpEdge = new Edge(mergeList.get(checkPos));
												helpEdge.changeEdgeNumber(direction);
//												System.out.println("extra add");
//												System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
												sList.add(helpEdge);
												hasSameAngleB = true;
											}
										}
										else{
											sameAngle = false;
										}
									}while(sameAngle);
									
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
									
									direction= -1*direction;
									
//									System.out.println("qi is turningpoint: " + direction );
									if(hasSameAngleB){
										z = 0;
										sameAngle = true;
										do{
											z++;
											if(position-z<0){
												checkPos = mergeList.size()-(position-z);
											}
											else{
												checkPos = (position-z);
											}
											
											if(Math.round(Math.toDegrees(mergeList.get(checkPos).getEdgeAngle()))==
													Math.round(Math.toDegrees(mergeList.get(position).getEdgeAngle()))){
												if(!mergeList.get(checkPos).isPolygonA()){

													helpEdge = new Edge(mergeList.get(checkPos));
													helpEdge.changeEdgeNumber(direction);
//													System.out.println("extra add");
//													System.out.println("inserting edge in sList: b" + helpEdge.getEdgeNumber());
													sList.add(helpEdge);
													hasSameAngleB = true;
												}
											}
											else{
												sameAngle = false;
											}
										}while(sameAngle);
										
										
									}
								}
								else{
//									System.out.println("inserting edge in sList: a" + qi.getEdgeNumber());
									sList.add(qi);
								}
//								System.out.println("repeat step 3");
							}
						}
					}
					position= (position + direction)%mergeList.size();
//					System.out.println("direction: " + direction);
//					System.out.println("new position: " + position);
					if(position < 0){
						position = mergeList.size()-1;
					}
				}
			}
			
		}while(i!=0&&!toStep4);
		
		Edge startingEdge = rList.get(0);
		
		i = 0;
		
		System.out.println("rList");
		printSimpleEdgeList(rList);
		
		System.out.println("sList");
		printSimpleEdgeList(sList);
		
		
//		for(Edge e: sList){
//			if(!e.isPolygonA()&&e.getEdgeNumber()==startingEdge.getEdgeNumber()){
//				break;
//			}
//			i++;
//		}
		
		int start = 0;
		boolean startFound = false;
		for(Edge e: sList){
			if(!e.isPolygonA()&&e.getEdgeNumber()==startingEdge.getEdgeNumber()){
				start = i;
				startFound = true;
			}
			if(startFound && e.getEdgeNumber() == startingEdge.getEdgeNumber()*-1){
				break;
			}
			i++;
		}
		
		//i is the position of r0 in the mergeList
		i =start;
		int j = 0;
		int next = 1;
		
		direction = 1;
		List<Edge> seqList = new ArrayList<>();
		Edge si = new Edge(sList.get(i));
		seqList.add(si);
		
		while(j<sList.size()-1){
			i = (i+1)%sList.size();
			
			si = new Edge(sList.get(i));
			
			if(si.isPolygonA()){
				j = j+1;
				seqList.add(si);
				
				if(si.isTurningPoint()){
					direction*=-1;
					next = next+direction;
					if(next<0){
//						System.out.println("next is: " + next);
						next = rList.size()-1;
//						System.out.println("next is " + next);
					}
					if(next>rList.size()-1){
//						System.out.println("next is: " + next);
						next = 0;
						
					}
//					System.out.println("next is " + next + " in direction " + direction);
				}
			}
			else if(si.getEdgeNumber() == direction * rList.get(next).getEdgeNumber()){
				j = j+1;
				seqList.add(si);
//				System.out.println("added b" + si.getEdgeNumber());
				next = next+direction;
				if(next<0){
//						System.out.println("next is: " + next);
					next = rList.size()-1;
//						System.out.println("changed to: " + next);
				}
				if(next>rList.size()-1){
//						System.out.println("next is: " + next);
					next = 0;
//						System.out.println("changed to: " + next);
				}
//				System.out.println("next is " + next + " in direction " + direction);
			}
			
		}
		for(Edge e: seqList){
			if(e.isPolygonA()){
				e.changeEdgeNumber(-1);
			}
		}

		System.out.println("seqList");
		printSimpleEdgeList(seqList);
		return seqList;
		
	}
	private static boolean correspondsToTrackLine(Edge edge, List<List<Edge>> trackLineTripList) {
		for(List<Edge> trip: trackLineTripList){
			for(Edge e: trip){
				if(e.getEdgeNumber()==edge.getEdgeNumber() && e.isPolygonA() == edge.isPolygonA()){
					return true;
				}
			}
		}
		return false;
	}

	private static List<List<Edge>> boundarySearch(List<List<Edge>> trackLineTripList){
		List<List<Coordinate>> tripIntersectionPoints = new ArrayList<>();
		List<Coordinate> intersectionPoints;
		
		List<List<Edge>> tripIntersectionEdges = new ArrayList<>();
		List<Edge> intersectionEdges;
		
		List<List<Boolean>> tripIntersectionSigns = new ArrayList<>();
		List<Boolean> intersectionSigns;
		
		List<Edge>trackLineTrip;
		List<Edge>trackLineTripI;
		List<Edge>trackLineTripJ;

		for(int j = 0; j < trackLineTripList.size(); j++){
			intersectionPoints = new ArrayList<>();
			intersectionEdges = new ArrayList<>();
			intersectionSigns = new ArrayList<>();
			for(int i = 0; i< trackLineTripList.size();i++){
				if(i!=j){
					
					trackLineTripJ = trackLineTripList.get(j);
					trackLineTripI = trackLineTripList.get(i);
					
					for(Edge edgeJ: trackLineTripJ){
						for(Edge edgeI: trackLineTripI){
//							System.out.println("looking for intersection");
//							System.out.println(edgeJ);
//							System.out.println(edgeI );
							if(edgeJ.testIntersect(edgeI)){
								intersectionPoints.add(edgeJ.calcIntersection(edgeI));
								intersectionEdges.add(edgeJ);
//								System.out.println("edge added to intersectionEdges");
								if(edgeI.getStartPoint().dFunction(edgeJ)>0){
									intersectionSigns.add(true);
								}
								else{
									intersectionSigns.add(false);
								}
							}
						}
					}
				}
			}
			tripIntersectionPoints.add(intersectionPoints);
			tripIntersectionEdges.add(intersectionEdges);
			tripIntersectionSigns.add(intersectionSigns);
		}
		
		//step 2
		List<List<Edge>> fragmentList = new ArrayList<>();
		List<Edge> fragment;
		int k = 0;
		for(int i = 0; i<tripIntersectionSigns.size(); i++){
//			fragment = new ArrayList<>();
			trackLineTrip = trackLineTripList.get(i);
			intersectionSigns = tripIntersectionSigns.get(i);
			intersectionEdges = tripIntersectionEdges.get(i);
			intersectionPoints = tripIntersectionPoints.get(i);
			
			for(int j = 0; j< intersectionSigns.size()-1;j++){
				if(intersectionSigns.get(j)==false){
					if(intersectionSigns.get((j+1))==true){
						fragment = new ArrayList<>();
						System.out.println("track line trip closed");
						Edge trimmedEdge = new Edge(intersectionEdges.get(j));
						trimmedEdge.setStartPoint(intersectionPoints.get(j));
						fragment.add(trimmedEdge);
						k=0;
						while(!trackLineTrip.get(k).equals(intersectionEdges.get(j))){
							//TODO: modulo may not be necessary
							k = (k+1)%trackLineTrip.size();
							
						}
						k = (k+1)%trackLineTrip.size();
						while(!trackLineTrip.get(k).equals(intersectionEdges.get((j+1)%intersectionEdges.size()))){
							fragment.add(trackLineTrip.get(k));
							k = (k+1)%trackLineTrip.size();
							
						}
						if(trackLineTrip.size()>1){
							trimmedEdge = new Edge(trackLineTrip.get(k));
							trimmedEdge.setEndPoint(intersectionPoints.get(j+1));
							fragment.add(trimmedEdge);
						}
						else{
							trimmedEdge.setEndPoint(intersectionPoints.get(j+1));
						}
						fragmentList.add(fragment);
					}
				}
			}
//			if(fragment.size()>0){
//				fragmentList.add(fragment);
//			}
			
		}
		int aantalFragmentEdges = 0;
		for(List<Edge>frag: fragmentList){
			System.out.println("fragment");
			printEdgeList(frag);
			aantalFragmentEdges += frag.size();
		}
		System.out.println(aantalFragmentEdges);
		//step 3
		System.out.println("starting step 3");
		
		int numberOfFragments = fragmentList.size();
		List<Edge> fragI;
		List<Edge> fragJ;
		List<List<Edge>> cycleList = new ArrayList<>();
		List<Edge> cycle;
		int stuckIterator = 100;
		while(numberOfFragments>0 && stuckIterator>0){
			stuckIterator--;
//			System.out.println(numberOfFragments);
			for (int i = 0; i < fragmentList.size(); i++) {
				for (int j = 0; j < fragmentList.size(); j++) {
					if(i!=j){
						fragI = fragmentList.get(i);
						fragJ = fragmentList.get(j);
						if(fragI.size()!=0&&fragJ.size()!=0){
//							System.out.println("beiden verschillend van 0");
							if(fragI.get(0).equalValuesRounded(fragJ.get(fragJ.size()-1))){
								System.out.println("fragments match!");
								if(fragI.get(fragI.size()-1).getEndPoint().equals(fragJ.get(0).getStartPoint())){
									
									fragJ.remove(0);
									fragJ.remove(fragJ.size()-1);
									
									cycle = new ArrayList<>();
									cycle.addAll(fragI);
									cycle.addAll(fragJ);
									
									cycleList.add(cycle);
									
									fragI.clear();
									numberOfFragments--;
									fragJ.clear();	
									numberOfFragments--;
								}
								else{
									fragI.remove(0);
									fragJ.addAll(fragI);
									
									fragI.clear();
									numberOfFragments--;
								}
							}
							else if(fragI.get(fragI.size()-1).equals(fragJ.get(0))){
								fragJ.remove(0);
								fragI.addAll(fragJ);
								
								fragJ.clear();
								numberOfFragments--;
							}
						}
					}
				}
			}
		}
		return cycleList;
	}
}
