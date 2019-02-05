package fr.epennecot.deminons.builders;

import java.util.HashMap;

import javafx.scene.layout.Pane;

public enum PaneBuilderFactory {
	INSTANCE;
	
	private HashMap<String, PaneBuilder> cache = new HashMap<String, PaneBuilder>();
	
	public PaneBuilder build(String target) {
		PaneBuilder paneBuilder = cache.get(target);
		if (paneBuilder == null) {
			paneBuilder = getBuilder(target);
			cache.put(target, paneBuilder);
		}
		return paneBuilder;
	}
	
	private PaneBuilder getBuilder(String target) {
		PaneBuilder paneBuilder = null;
		switch(target) {
		case "game":
			paneBuilder = new GamePaneBuilder();
			break;
		case "setup":
			paneBuilder = new SetupPaneBuilder();
		}
		return paneBuilder;
	}
}
