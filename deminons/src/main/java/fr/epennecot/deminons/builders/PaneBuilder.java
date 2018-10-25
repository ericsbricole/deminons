package fr.epennecot.deminons.builders;

import fr.epennecot.deminons.Exception.DeminonsException;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public interface PaneBuilder {

	public default Pane build() throws DeminonsException {
		buildContent();
		addListeners();
		return getRootPane();
	}
	public void buildContent() throws DeminonsException;
	public void addListeners() throws DeminonsException;
	public Pane getRootPane();
}
