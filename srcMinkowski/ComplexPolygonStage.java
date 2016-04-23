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

public class ComplexPolygonStage {

	static int aantalComplexPolygonStages = 0;
	static List<List<Edge>> complexPolygonToDraw = new ArrayList<>();
	
	static double sceneSizeX = 300;
	static double sceneSizeY = 300;

	
	public static int getAantalPolygonPairStages() {
		return aantalComplexPolygonStages;
	}

	public static void setAantalPolygonPairStages(int aantalPolygonPairStages) {
		ComplexPolygonStage.aantalComplexPolygonStages = aantalPolygonPairStages;
	}

	public static List<List<Edge>> getMultiPolygonsToDraw() {
		return complexPolygonToDraw;
	}

	public static void setMultiPolygonsToDraw(List<List<Edge>> complexPolygonsToDraw) {
		complexPolygonToDraw = complexPolygonsToDraw;
	}

	public static double getSceneSizeX() {
		return sceneSizeX;
	}

	public static void setSceneSizeX(double sceneSizeX) {
		ComplexPolygonStage.sceneSizeX = sceneSizeX;
	}

	public static double getSceneSizeY() {
		return sceneSizeY;
	}

	public static void setSceneSizeY(double sceneSizeY) {
		ComplexPolygonStage.sceneSizeY = sceneSizeY;
	}
	
	public static void addComplexPolygon(List<Edge> complexPolygon){
		List<Edge> compPoly = new ArrayList<>();
		for(Edge e: complexPolygon){
			compPoly.add(new Edge(e));
		}
		aantalComplexPolygonStages++;
		complexPolygonToDraw.add(compPoly);
		
	}

	public static void addTrackLineTrips(List<List<Edge>> trackLineTripList) {
		List<Edge> compPoly = new ArrayList<>();
		for(List<Edge> l: trackLineTripList){
			for(Edge e: l){
				compPoly.add(new Edge(e));
			}
		}
		aantalComplexPolygonStages++;
		complexPolygonToDraw.add(compPoly);
		
	}

	public static List<Stage> drawAllComplexPolygon(){
		List<Stage> polyPairStageList = new ArrayList<>();
		for(List<Edge> complexPolygon: complexPolygonToDraw){
			polyPairStageList.add(drawComplexPolygon(complexPolygon));
			
		}
		return polyPairStageList;

	}

	private static Stage drawComplexPolygon(List<Edge> complexPolygon) {
		Stage stage = new Stage();
		
		Color background = Color.WHITESMOKE;
		
		Group complexPolygonGroup = new Group();
		Scene scene = new Scene(complexPolygonGroup, sceneSizeX, sceneSizeY, background);
		
		Line xAxis = new Line(0,sceneSizeY/2,10000,sceneSizeY/2);
	    Line yAxis = new Line(sceneSizeX/2,0,sceneSizeX/2,10000);
        
		complexPolygonGroup.getChildren().add(xAxis);
		complexPolygonGroup.getChildren().add(yAxis);
        
		double biggestXCoordValue = 0;//biggest value of x and y coords of the polygons, used for autoscaling
		double biggestYCoordValue = 0;
		for(Edge e: complexPolygon){
			if(Math.abs(e.getStartPoint().getxCoord())>biggestXCoordValue){
				biggestXCoordValue = Math.abs(e.getStartPoint().getxCoord());
			}
			if(Math.abs(e.getEndPoint().getxCoord())>biggestXCoordValue){
				biggestXCoordValue = Math.abs(e.getEndPoint().getxCoord());
			}
			if(Math.abs(e.getStartPoint().getyCoord())>biggestYCoordValue){
				biggestYCoordValue = Math.abs(e.getStartPoint().getyCoord());
			}
			if(Math.abs(e.getEndPoint().getyCoord())>biggestYCoordValue){
				biggestYCoordValue = Math.abs(e.getEndPoint().getyCoord());
			}
		}
		double biggestValue = Math.max(biggestXCoordValue, biggestYCoordValue);
		makeComplexPolygonScene(complexPolygonGroup, complexPolygon, 0, biggestValue + 10);
		
        stage.setScene(scene);
        return stage;
	}

	private static void makeComplexPolygonScene(Group group, List<Edge> complexPolygon, int i,
			double biggestValue) {
		//sceneSize divided by 2 because x and y axis are in the middle
		double resizeFactor = sceneSizeY/biggestValue/2;
		Line edge = new Line(0,sceneSizeY/2,10000,sceneSizeY/2);
//		Color sligthlyLighterBlack = Color.web("0x201F18");
		
		for(Edge e: complexPolygon){
			
			edge = new Line(resizeFactor*e.getStartPoint().getxCoord() +(sceneSizeX/2),
					-resizeFactor*e.getStartPoint().getyCoord()+(sceneSizeY/2),
					resizeFactor*e.getEndPoint().getxCoord()+(sceneSizeX/2),
					-resizeFactor*e.getEndPoint().getyCoord()+(sceneSizeY/2));
			edge.setStrokeWidth(6);
//			edge.setStroke(sligthlyLighterBlack);
			group.getChildren().add(edge);
			
		}
	}

	
}
