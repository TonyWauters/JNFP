import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Polygon;

public class NoFitPolygon {

	//a list of the polygons that are contained in the no-fit polygon
	private List<List<Coordinate>>nfpPolygonsList;
	
	//this gives the polygon that is currently being created
	private List<Coordinate> activeList;
	
	private MultiPolygon stationaryPolygon;
	private MultiPolygon orbitingPolygon;
	
	public NoFitPolygon(Coordinate coordinate, MultiPolygon stat, MultiPolygon orb) {
		nfpPolygonsList = new ArrayList<>();
		activeList = new ArrayList<>();
		activeList.add(new Coordinate(coordinate));
		nfpPolygonsList.add(activeList);
		
		stationaryPolygon = stat;
		orbitingPolygon = orb;
	}
	
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

	public List<List<Coordinate>> getNfpPolygonsList() {
		return nfpPolygonsList;
	}

	public void setNfpPolygonsList(List<List<Coordinate>> nfpPolygonsList) {
		this.nfpPolygonsList = nfpPolygonsList;
	}

	public List<Coordinate> getActiveList() {
		return activeList;
	}

	public void setActiveList(List<Coordinate> activeList) {
		this.activeList = activeList;
	}

	public MultiPolygon getStationaryPolygon() {
		return stationaryPolygon;
	}

	public void setStationaryPolygon(MultiPolygon stationaryPolygon) {
		this.stationaryPolygon = stationaryPolygon;
	}

	public MultiPolygon getOrbitingPolygon() {
		return orbitingPolygon;
	}

	public void setOrbitingPolygon(MultiPolygon orbitingPolygon) {
		this.orbitingPolygon = orbitingPolygon;
	}
	
	public void addTranslation(Coordinate coord){
		activeList.add(new Coordinate(coord));
	}
	
	
	//for Graphic image
	public Polygon[] toPolygonList(double xSize, double ySize, double sizeFactor) {
		
		Polygon[] polygonList = new Polygon[nfpPolygonsList.size()];
		for (int i = 0; i < nfpPolygonsList.size(); i++) {
			polygonList[i] = new Polygon();
			for (Coordinate coord : nfpPolygonsList.get(i)) {
				polygonList[i].getPoints().add(sizeFactor * coord.getxCoord() + xSize / 2);
				// yCoord*-1 to invert to normal axis
				polygonList[i].getPoints().add(-1 * sizeFactor * coord.getyCoord() + ySize / 2);
			}
		}
		
		return polygonList;
	}

	public void startNewActiveList(Coordinate coord){
		activeList = new ArrayList<>();
		activeList.add(new Coordinate(coord));
		nfpPolygonsList.add(activeList);
	}

	@Override
	public String toString() {
		String nfp = "";
		
		nfp += nfpPolygonsList.size() + "\n";
		for(List<Coordinate> partList : nfpPolygonsList){
			if(partList.size()>1)partList.remove(partList.size()-1);
			nfp+= partList.size();
			
			for(Coordinate coord: partList){
				nfp+="\n";
				nfp+= coord.toNfpString();
			}
			nfp+="\n\n";
		}
		
		return nfp;
	}
	
	
}
