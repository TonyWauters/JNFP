import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

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

		}
		return nfpStageList;
	}

	private static Stage drawNFP(NoFitPolygon nfp) {

		Stage stage = new Stage();

		Color background = Color.WHITESMOKE;
		
		Group nfpGroup = new Group();
		Scene scene = new Scene(nfpGroup, sceneSizeX, sceneSizeY, background);

		
		
		Line xAxis = new Line(0, sceneSizeY / 2, 10000, sceneSizeY / 2);
		Line yAxis = new Line(sceneSizeX / 2, 0, sceneSizeX / 2, 10000);

		nfpGroup.getChildren().add(xAxis);
		nfpGroup.getChildren().add(yAxis);

		double biggestXCoordValue = nfp.getStationaryPolygon().getBiggestX() + nfp.getOrbitingPolygon().getBiggestX();
		double biggestYCoordValue = nfp.getStationaryPolygon().getBiggestY() + nfp.getOrbitingPolygon().getBiggestY();
		
		double biggestValue = Math.max(biggestXCoordValue, biggestYCoordValue);

		
		makeMultiPolygonScene(nfpGroup, nfp.getStationaryPolygon(), 0, biggestValue);
		makeMultiPolygonScene(nfpGroup, nfp.getOrbitingPolygon(), 1, biggestValue);
		makeNFPScene(nfpGroup, nfp, biggestValue);
		
		stage.setScene(scene);
		return stage;
	}


	private static void makeNFPScene(Group group,NoFitPolygon nfp, double biggestValue) {
		
		//sceneSize divided by 2 because x and y axis are in the middle
		double resizeFactor = sceneSizeY/biggestValue/2;
		Color sligthlyLighterBlack = Color.web("0x201F18");
		Polygon[] nfpPolygonList = nfp.toPolygonList(sceneSizeX, sceneSizeY, resizeFactor);
		
		for(Polygon polygon : nfpPolygonList){
			polygon.setStrokeWidth(5);
	        polygon.setStroke(Color.BLACK);
	        polygon.setFill(Color.TRANSPARENT);
	        group.getChildren().add(polygon);
		}
		
	}
	
	private static void makeMultiPolygonScene(Group group, MultiPolygon mPolygon, int color,
			double biggestValue) {
		//sceneSize divided by 2 because x and y axis are in the middle
		double resizeFactor = sceneSizeY/biggestValue/2;
		
		Color background = Color.web("0xc8c5b4");
		Color aColor = Color.GREY;
		Color bColor = Color.DARKGREY;
		
		Color sligthlyLighterBlack = Color.web("0x201F18");
		
		
        Polygon polygon = mPolygon.makeOuterPolygon(sceneSizeX, sceneSizeY, resizeFactor);
        Circle refPoint = new Circle(resizeFactor * mPolygon.getOuterPolygon()[0].getxCoord() + sceneSizeX / 2,
        		-1* resizeFactor *mPolygon.getOuterPolygon()[0].getyCoord() + sceneSizeY / 2 , 5);
        switch(color){
        case 0:{
	        	polygon.setFill(aColor);
//	        	refPoint.setFill(Color.TRANSPARENT);
//	        	refPoint.setStrokeType(StrokeType.INSIDE);
//	        	refPoint.setStrokeWidth(0);
//	        	refPoint.setStroke(Color.TRANSPARENT);
	        	break;
        }
        case 1: {
	        	polygon.setFill(bColor);
//	        	refPoint.setFill(bColor);
//	        	refPoint.setStrokeType(StrokeType.INSIDE);
//	         	refPoint.setStrokeWidth(1);
//	         	refPoint.setStroke(Color.TRANSPARENT);
	        	break;
        	}
        }
       
        polygon.setStrokeWidth(2);
        polygon.setStroke(Color.BLACK);
        
        
        group.getChildren().add(polygon);
//        group.getChildren().add(refPoint);
        
        Polygon[] holes = mPolygon.makeHoles(sceneSizeX, sceneSizeY, resizeFactor);
        
        for(Polygon hole: holes){
            hole.setFill(Color.WHITESMOKE);
            hole.setStrokeWidth(1);
            hole.setStroke(Color.BLACK);
            group.getChildren().add(hole);
        }
		
	}

}
