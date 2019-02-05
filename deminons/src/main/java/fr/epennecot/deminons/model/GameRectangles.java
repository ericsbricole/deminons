package fr.epennecot.deminons.model;

import javafx.collections.ObservableList;

public class GameRectangles {

	private ObservableList<ObservableList<GameRectangle>> rectangles;

	public ObservableList<ObservableList<GameRectangle>> getRectangles() {
		return rectangles;
	}

	public void setRectangles(ObservableList<ObservableList<GameRectangle>> rectangles) {
		this.rectangles = rectangles;
	}
	
	public void init() {
		if (rectangles != null)
			rectangles.clear();
		
	}
}
