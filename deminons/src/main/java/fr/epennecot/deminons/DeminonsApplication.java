package fr.epennecot.deminons;

import javax.annotation.Resource;

import fr.epennecot.deminons.builders.GamePaneBuilder;
import fr.epennecot.deminons.builders.PaneBuilder;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import fr.epenneco.java.builders.GamePaneBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DeminonsApplication extends Application{

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Deminons");
		
		Router.INSTANCE.setStage(stage);
		Router.INSTANCE.route("game");
		
		stage.show();
	}
}
