package fr.epennecot.deminons;

import fr.epennecot.deminons.Exception.DeminonsException;
import fr.epennecot.deminons.builders.PaneBuilder;
import fr.epennecot.deminons.builders.PaneBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public enum Router {
	INSTANCE;
		
	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void route(String target) throws DeminonsException {
		PaneBuilder builder = PaneBuilderFactory.INSTANCE.build(target);
		Pane pane = builder.build();
		
		Scene scene = new Scene(pane);
		stage.setScene(scene);
	}


	
}
