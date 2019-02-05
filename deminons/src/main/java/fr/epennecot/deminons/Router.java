package fr.epennecot.deminons;

import java.util.HashMap;
import java.util.Map;

import fr.epennecot.deminons.Exception.DeminonsException;
import fr.epennecot.deminons.builders.PaneBuilder;
import fr.epennecot.deminons.builders.PaneBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public enum Router {
	INSTANCE;
		
	private Stage stage;
	private Map<String, Scene> sceneCache = new HashMap<String, Scene>();

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void route(String target) throws DeminonsException {
		PaneBuilder builder = PaneBuilderFactory.INSTANCE.build(target);
		Pane pane = builder.build();
		setWindowTitle(target);
		
		if (sceneCache.get(target) == null) {
			Scene scene = new Scene(pane);
			sceneCache.put(target, scene);
			stage.setScene(scene);
		}
		else {
			sceneCache.get(target).setRoot(pane);
			stage.setScene(sceneCache.get(target));			
		}
	}

	public void route(String target, int rectXNumber, int rectYNumber, int bombNumber) throws DeminonsException {
		PaneBuilder builder = PaneBuilderFactory.INSTANCE.build(target);
		builder.setRouterParams(rectXNumber, rectYNumber, bombNumber);
		Pane pane = builder.build();
		setWindowTitle(target);
		
		if (sceneCache.get(target) == null) {			
			Scene scene = new Scene(pane);
			sceneCache.put(target, scene);
			stage.setScene(scene);
		}
		else {			
			sceneCache.get(target).setRoot(pane);
			stage.setScene(sceneCache.get(target));
		}
	}
	
	private void setWindowTitle(String target) {
		switch(target) {
		case "setup":
			stage.setTitle("Préparation de la partie");
			break;
		case "game":
			stage.setTitle("Déminons");
		}
	}


	
}
