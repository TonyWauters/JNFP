import java.util.List;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
* this class supports drawing of multipolygons in javaFX
 * in its current state it only allows the user to draw once
 * trying to call the draw method multiple times in the main method will result in errors
 * @author Stiaan Uyttersprot
 
 */
public class DrawJavaFX extends Application{
	
    //starting the application
    @Override
    public void start(Stage primaryStage)
    {
    	double screenSizeX = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    	double screenSizeY = java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    	double stageWidth = PolygonPairStages.getSceneSizeX();
    	double stageHeight = PolygonPairStages.getSceneSizeY();
    	int stageNumber = 0;
    	int heightPlaceLine = 0;
    	int borderOffset = 30;
    	
    	for(Stage stage : PolygonPairStages.drawPolygonPairs()){
    		if(stageWidth*(stageNumber+1)> screenSizeX){
    			stageNumber = 0;
    			heightPlaceLine++;
    		}
    		stage.setX(stageWidth*(stageNumber));
    		stage.setY((borderOffset+stageHeight)*heightPlaceLine);
    		stage.show();
    		
    		stageNumber++;
    		
    	}
    	
    	List<Stage> nfpStages = NoFitPolygonStages.drawNFPFigures();
    	stageWidth = NoFitPolygonStages.getSceneSizeX();
    	stageHeight = NoFitPolygonStages.getSceneSizeY();
    	for(Stage stage : nfpStages){
    		//stageNumber + 1 to make sure it is inside the screen
    		if(stageWidth*(stageNumber+1)> screenSizeX){
    			stageNumber = 0;
    			heightPlaceLine++;
    		}
    		if(stageHeight*(heightPlaceLine+1) > screenSizeY){
    			heightPlaceLine = 0;
    		}
    		stage.setX(stageWidth*(stageNumber));
    		stage.setY((borderOffset+stageHeight)*heightPlaceLine);
    		stage.show();
    		
    		stageNumber++;
    		
    	}
    }
    
    public void launchDrawer(String[] args){
    	launch(args);
    }
      
}