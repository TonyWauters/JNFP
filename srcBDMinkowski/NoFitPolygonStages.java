import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

/**
 * @author Stiaan Uyttersprot
 *
 */
public class NoFitPolygonStages {

	static int aantalNFPStages = 0;
	static List<NoFitPolygon> nfpToDraw = new ArrayList<>();

	static double sceneSizeX = 800;
	static double sceneSizeY = 800;

	public static int getAantalNFPStages() {
		return aantalNFPStages;
	}

	public static void setAantalNFPStages(int aantalNFPStages) {
		NoFitPolygonStages.aantalNFPStages = aantalNFPStages;
	}

	public static List<NoFitPolygon> getNfpToDraw() {
		return nfpToDraw;
	}

	public static void setNfpToDraw(List<NoFitPolygon> nfpToDraw) {
		NoFitPolygonStages.nfpToDraw = nfpToDraw;
	}

	public static double getSceneSizeX() {
		return sceneSizeX;
	}

	public static void setSceneSizeX(double sceneSizeX) {
		NoFitPolygonStages.sceneSizeX = sceneSizeX;
	}

	public static double getSceneSizeY() {
		return sceneSizeY;
	}

	public static void setSceneSizeY(double sceneSizeY) {
		NoFitPolygonStages.sceneSizeY = sceneSizeY;
	}

	public static void addNFP(NoFitPolygon nfp) {

		nfpToDraw.add(nfp);
		aantalNFPStages++;

	}

	public static List<Stage> drawNFPFigures() {
		List<Stage> nfpStageList = new ArrayList<>();
		for (NoFitPolygon nfp : nfpToDraw) {
			nfpStageList.add(drawNFP(nfp));
			// drawPair(multiPolys);

		}
		return nfpStageList;
	}

	private static Stage drawNFP(NoFitPolygon nfp) {

		Stage stage = new Stage();

		Group nfpGroup = new Group();
		Scene scene = new Scene(nfpGroup, sceneSizeX, sceneSizeY, Color.WHITESMOKE);

		Line xAxis = new Line(0, sceneSizeY / 2, 10000, sceneSizeY / 2);
		Line yAxis = new Line(sceneSizeX / 2, 0, sceneSizeX / 2, 10000);

		nfpGroup.getChildren().add(xAxis);
		nfpGroup.getChildren().add(yAxis);

		double biggestXCoordValue = nfp.getStationaryPolygon().getBiggestX().doubleValue() + nfp.getOrbitingPolygon().getBiggestX().doubleValue();
		double biggestYCoordValue = nfp.getStationaryPolygon().getBiggestY().doubleValue() + nfp.getOrbitingPolygon().getBiggestY().doubleValue();
		
		double biggestValue = Math.max(biggestXCoordValue, biggestYCoordValue);

		
		makeMultiPolygonScene(nfpGroup, nfp.getStationaryPolygon(), 0, biggestValue+50);
		makeMultiPolygonScene(nfpGroup, nfp.getOrbitingPolygon(), 1, biggestValue+50);
		makeNFPScene(nfpGroup, nfp, biggestValue+50);
		
		stage.setScene(scene);
		return stage;
	}


	private static void makeNFPScene(Group group,NoFitPolygon nfp, double biggestValue) {
		
		//sceneSize divided by 2 because x and y axis are in the middle
		double resizeFactor = sceneSizeY/(biggestValue)/2;
		
		Polygon[] nfpPolygonList = nfp.toPolygonList(sceneSizeX, sceneSizeY, resizeFactor);
		
		for(Polygon polygon : nfpPolygonList){
			polygon.setStrokeWidth(3);
	        polygon.setStroke(Color.BLACK);
	        polygon.setFill(Color.TRANSPARENT);
	        group.getChildren().add(polygon);
		}
		
	}
	
	private static void makeMultiPolygonScene(Group group, MultiPolygon mPolygon, int color,
			double biggestValue) {
		//sceneSize divided by 2 because x and y axis are in the middle
		double resizeFactor = sceneSizeY/biggestValue/2;
		
        Polygon polygon = mPolygon.makeOuterPolygon(sceneSizeX, sceneSizeY, resizeFactor);
        
        switch(color){
        case 0: polygon.setFill(Color.GREY);break;
        case 1: polygon.setFill(Color.DARKGREY);break;
        }
        polygon.setStrokeWidth(1);
        polygon.setStroke(Color.BLACK);
        
        group.getChildren().add(polygon);
        
        Polygon[] holes = mPolygon.makeHoles(sceneSizeX, sceneSizeY, resizeFactor);
        
        for(Polygon hole: holes){
            hole.setFill(Color.WHITESMOKE);
            hole.setStrokeWidth(1);
            hole.setStroke(Color.BLACK);
            group.getChildren().add(hole);
        }
		
	}

}
