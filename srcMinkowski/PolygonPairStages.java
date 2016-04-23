import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

/**
 * this class is used to prepare the stages for polygon pairs for the GUI.
 * the values and methods are static to be able to add polygon pairs from every location in the code
 * and draw them in the drawtool without having to give an object to the drawtool
 * @author Stiaan Uyttersprot
 */

public class PolygonPairStages {

	static int aantalPolygonPairStages = 0;
	static List<MultiPolygon[]> MultiPolygonsToDraw = new ArrayList<>();
	
	static double sceneSizeX = 300;
	static double sceneSizeY = 300;
	
	public static int getAantalPolygonPairStages() {
		return aantalPolygonPairStages;
	}

	public static void setAantalPolygonPairStages(int aantalPolygonPairStages) {
		PolygonPairStages.aantalPolygonPairStages = aantalPolygonPairStages;
	}

	public static List<MultiPolygon[]> getMultiPolygonsToDraw() {
		return MultiPolygonsToDraw;
	}

	public static void setMultiPolygonsToDraw(List<MultiPolygon[]> multiPolygonsToDraw) {
		MultiPolygonsToDraw = multiPolygonsToDraw;
	}

	public static double getSceneSizeX() {
		return sceneSizeX;
	}

	public static void setSceneSizeX(double sceneSizeX) {
		PolygonPairStages.sceneSizeX = sceneSizeX;
	}

	public static double getSceneSizeY() {
		return sceneSizeY;
	}

	public static void setSceneSizeY(double sceneSizeY) {
		PolygonPairStages.sceneSizeY = sceneSizeY;
	}
	
	public static void addPolygonPair(MultiPolygon mp1, MultiPolygon mp2){
		MultiPolygon[] mPolygonPair = new MultiPolygon[2];
		mPolygonPair[0] = new MultiPolygon(mp1);
		mPolygonPair[1] = new MultiPolygon(mp2);
		aantalPolygonPairStages++;
		MultiPolygonsToDraw.add(mPolygonPair);
		
	}

	

	public static List<Stage> drawPolygonPairs(){
		List<Stage> polyPairStageList = new ArrayList<>();
		for(MultiPolygon[] multiPolys: MultiPolygonsToDraw){
			polyPairStageList.add(drawPair(multiPolys));
			
		}
		return polyPairStageList;

	}

	private static Stage drawPair(MultiPolygon[] multiPolys) {
		
		Stage stage = new Stage();
		
		Group multiPolygonPairGroup = new Group();
		Scene scene = new Scene(multiPolygonPairGroup, sceneSizeX, sceneSizeY, Color.WHITESMOKE);
		
		Line xAxis = new Line(0,sceneSizeY/2,10000,sceneSizeY/2);
	    Line yAxis = new Line(sceneSizeX/2,0,sceneSizeX/2,10000);
        
		multiPolygonPairGroup.getChildren().add(xAxis);
		multiPolygonPairGroup.getChildren().add(yAxis);
        
		double biggestXCoordValue = multiPolys[0].getBiggestX() + multiPolys[1].getBiggestX();//biggest value of x and y coords of the polygons, used for autoscaling
		double biggestYCoordValue = multiPolys[0].getBiggestY() + multiPolys[1].getBiggestY();;
		double biggestValue = Math.max(biggestXCoordValue, biggestYCoordValue);
		
		makeMultiPolygonScene(multiPolygonPairGroup, multiPolys[0], 0, biggestValue);
		makeMultiPolygonScene(multiPolygonPairGroup, multiPolys[1],1, biggestValue);
        
        stage.setScene(scene);
        //stage.show();
        return stage;
	}
	
	public static void makeMultiPolygonScene(Group group, MultiPolygon mPolygon, int color, double biggestValue) {
		
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
