package fr.epennecot.deminons.builders;

import fr.epennecot.deminons.Router;
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
	public default void route(String target, int rectXNumber, int rectYNumber, int bombNumber) {
		try {
			Router.INSTANCE.route(target, rectXNumber, rectYNumber, bombNumber);
		} catch (DeminonsException e) {
			System.err.println("An error occured while routing to " + target);
		}
	}
	public void setRouterParams(int rectXNumber, int rectYNumber, int bombNumber);
}
