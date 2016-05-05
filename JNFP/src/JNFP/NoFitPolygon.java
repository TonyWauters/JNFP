package JNFP;
import java.util.ArrayList;
import java.util.List;

/**
 * the NFP class contains an NFP describing the area of overlap for two polygons 
 * @author Stiaan Uyttersprot
 *
 */
public class NoFitPolygon {

	//a list of the polygons that are contained in the no-fit polygon
	private List<List<Coordinate>>nfpPolygonsList;
	
	//this gives the polygon that is currently being created
	private List<Coordinate> activeList;
	
	private MultiPolygon stationaryPolygon;
	private MultiPolygon orbitingPolygon;
	
	protected NoFitPolygon(Coordinate coordinate, MultiPolygon stat, MultiPolygon orb) {
		nfpPolygonsList = new ArrayList<>();
		activeList = new ArrayList<>();
		activeList.add(new Coordinate(coordinate));
		nfpPolygonsList.add(activeList);
		
		stationaryPolygon = stat;
		orbitingPolygon = orb;
	}
	
	/**
	 * deep copy of an NFP
	 * @param nfp the nfp to copy
	 */
	public NoFitPolygon(NoFitPolygon nfp) {
		nfpPolygonsList = new ArrayList<>();
		
		for(List<Coordinate> list : nfp.getNfpPolygonsList()){
			activeList = new ArrayList<>();
			for(Coordinate coord : list){
				activeList.add(new Coordinate(coord));
			}
			nfpPolygonsList.add(activeList);
		}
		
		stationaryPolygon = new MultiPolygon(nfp.getStationaryPolygon());
		orbitingPolygon = new MultiPolygon(nfp.getOrbitingPolygon());
	}

	protected NoFitPolygon(List<List<Edge>> minkowskiCycleList, Vector translationVector){
		nfpPolygonsList = new ArrayList<>();
		int nCycles = minkowskiCycleList.size();
		int outerCycleIndex = 0;
		double lowestX = Double.MAX_VALUE;
		double lowestY = Double.MAX_VALUE;
		List<Edge> edgeList;
		for(int i = 0; i< minkowskiCycleList.size(); i++){
			edgeList = minkowskiCycleList.get(i);
			for(Edge edge: edgeList){
				if(edge.getStartPoint().getxCoord()<lowestX){
					lowestX = edge.getStartPoint().getxCoord();
					outerCycleIndex = i;
				}
				if(edge.getStartPoint().getyCoord()<lowestY){
					lowestY = edge.getStartPoint().getyCoord();
					outerCycleIndex = i;
				}
			}
		}
		int currentCycle = outerCycleIndex;
		for(int i =0; i<nCycles; i++){
			edgeList = minkowskiCycleList.get(currentCycle);
			currentCycle = (currentCycle+1)%nCycles;
			activeList = new ArrayList<>();
			for(Edge edge: edgeList){
				activeList.add(edge.getStartPoint().translatedTo(translationVector));
			}
			nfpPolygonsList.add(activeList);
		}
	}
	
	/**
	 * @return a list containing all paths of the NFP
	 */
	public List<List<Coordinate>> getNfpPolygonsList() {
		return nfpPolygonsList;
	}

	protected void setNfpPolygonsList(List<List<Coordinate>> nfpPolygonsList) {
		this.nfpPolygonsList = nfpPolygonsList;
	}

	protected List<Coordinate> getActiveList() {
		return activeList;
	}

	protected void setActiveList(List<Coordinate> activeList) {
		this.activeList = activeList;
	}

	/**
	 * @return the stationary polygon of the NFP
	 */
	public MultiPolygon getStationaryPolygon() {
		return stationaryPolygon;
	}

	protected void setStationaryPolygon(MultiPolygon stationaryPolygon) {
		this.stationaryPolygon = stationaryPolygon;
	}

	/**
	 * @return the orbiting polygon of the NFP
	 */
	public MultiPolygon getOrbitingPolygon() {
		return orbitingPolygon;
	}

	protected void setOrbitingPolygon(MultiPolygon orbitingPolygon) {
		this.orbitingPolygon = orbitingPolygon;
	}
	
	protected void addTranslation(Coordinate coord){
		activeList.add(new Coordinate(coord));
	}

	protected void startNewActiveList(Coordinate coord){
		activeList = new ArrayList<>();
		activeList.add(new Coordinate(coord));
		nfpPolygonsList.add(activeList);
	}
	
	//this method will remove coordinates that aren't necessary to draw the nfp(when more than two points fall on the same line
	protected void removeExcessivePoints(){
		int start;
		int checkPoint;
		for(List<Coordinate> coordinateList: nfpPolygonsList){
			start = 0;
			if(coordinateList.size()>1){
				while(start+1<coordinateList.size()){
					checkPoint = (start+2)%coordinateList.size();
					while(coordinateList.size()>1 && start + 1< coordinateList.size()
							&& coordinateList.get(checkPoint).dFunctionCheck(coordinateList.get(start), coordinateList.get(start+1))){
						coordinateList.remove(start+1);
						if(checkPoint>=coordinateList.size())checkPoint = 0;
					}
					start++;
				}
			}
			
		}
	}

	protected boolean containsPoint(Coordinate coordinate) {
		Edge testEdge;
		for(List<Coordinate> partList : nfpPolygonsList){
			for (int i = 0; i < partList.size(); i++) {
				
				testEdge = new Edge(partList.get(i), partList.get((i+1)%partList.size()));
				
				if(testEdge.containsPoint(coordinate))return true;
				if(partList.get(i).equals(coordinate))return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		String nfp = "";
		
		nfp += nfpPolygonsList.size() + "\n";
		for(List<Coordinate> partList : nfpPolygonsList){
			nfp+= partList.size();
			
			for(Coordinate coord: partList){
				nfp+="\n";
				nfp+= coord.toNfpString();
			}
			nfp+="\n\n";
		}
		
		return nfp;
	}

	protected void removeLastDoubleCoordinate() {
		if(activeList.size()>1)activeList.remove(activeList.size()-1);
		
	}
	
	
}
