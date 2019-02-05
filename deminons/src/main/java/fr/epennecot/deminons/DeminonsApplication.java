package fr.epennecot.deminons;

import javax.annotation.Resource;

import fr.epennecot.deminons.builders.GamePaneBuilder;
import fr.epennecot.deminons.builders.PaneBuilder;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import fr.epenneco.java.builders.GamePaneBuilder;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
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
import javafx.stage.StageStyle;

public class DeminonsApplication extends Application{

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				stage.centerOnScreen();
				stage.show();
				
			}
		});
//		stage.heightProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable, Number number, Number number2) {
//				System.out.println("ca change la stage");
//				setCurrentHeightToStage(number2);
//			}
//
//			private void setCurrentHeightToStage(Number number2) {
//				stage.setHeight((double) number2);
//			}
//		});
		stage.setResizable(false);
		Router.INSTANCE.setStage(stage);
		Router.INSTANCE.route("setup", 34, 34, 100);
		stage.hide();
		stage.show();
	}
}
