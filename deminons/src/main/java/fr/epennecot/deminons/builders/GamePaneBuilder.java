package fr.epennecot.deminons.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.epennecot.deminons.Exception.DeminonsException;
import fr.epennecot.deminons.model.GameRectangle;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;

public class GamePaneBuilder implements PaneBuilder {
	
	private static final Logger LOGGER = LogManager.getLogger(GamePaneBuilder.class);
	
	private Pane pane = new Pane();
	private VBox vBox = new VBox();
	private Dialog<ButtonType> dialog = new Dialog<ButtonType>();
	private Pane rootPane = new StackPane(pane);
	private int rectXNumber = 34;
	private int rectYNumber = 34;
	private int bombNumber = 220;
	private List<List<GameRectangle>> gameRectangles = new ArrayList<List<GameRectangle>>();
	
	@Override
	public void buildContent() throws DeminonsException {
		
		pane.getStylesheets().add("./deminons.css");


		Canvas canvas = new Canvas(510,510);
		pane.getChildren().add(canvas);
		
		int x = 0;
		int y = 0;
		int width = 20;
		int height = 20;
		for (int i=0; i<rectXNumber; i++ ) {
			gameRectangles.add(new ArrayList<GameRectangle>());
			for (int j=0; j<rectYNumber; j++) {
				GameRectangle hiddenRect = new GameRectangle(x, y, width, height, false);
				hiddenRect.setColumn(i);
				hiddenRect.setRow(j);
				hiddenRect.getStyleClass().add("hiddenRect");
				gameRectangles.get(i).add(hiddenRect);
				y += height;
			}
			x += width;
			y = 0;
		}

		for (List<GameRectangle> list : gameRectangles) {
			for (GameRectangle rect : list) {
				pane.getChildren().add(rect);
			}
		}
		
		placeBombs(bombNumber);
		for (int i=0; i<gameRectangles.size(); i++) {
			for (int j=0; j<gameRectangles.get(i).size(); j++) {
				GameRectangle gameRect = gameRectangles.get(i).get(j);
				if (gameRect.isABomb() == false) {
					Map<String,List<GameRectangle>> surrounding = getSurrounding(i, j);
					if (surrounding.get("bombs") != null) {
						gameRect.setBombs(surrounding.get("bombs").size());
					}
					else
						gameRect.setBombs(0);
				}
			}
		}
	}
	
	private Map<String,List<GameRectangle>> getSurrounding(int x, int y){
		Map<String,List<GameRectangle>> surrounding = new HashMap<String,List<GameRectangle>>();
		List<GameRectangle> lBombs = new ArrayList<GameRectangle>();
		List<GameRectangle> lSafe = new ArrayList<GameRectangle>();
		
		if (LOGGER.isDebugEnabled()) LOGGER.debug("getSurronding for rectangle in: " + x +","+y);
		for (int i=-1; i<2; i++) {
			for (int j=-1; j<2; j++) {
				if (!(i==0 && j==0)) {
					if ((x+i)>=0 && (x+i)<rectXNumber && (y+j)>=0 && (y+j)<rectYNumber) {
						GameRectangle scannedRect = gameRectangles.get(x+i).get(y+j);
						if (scannedRect.isABomb()) {
							lBombs.add(scannedRect);
						}
						else {
							lSafe.add(scannedRect);
						}
					}
				}
			}
		}
		surrounding.put("bombs", lBombs);
		surrounding.put("safe", lSafe);
		return surrounding;
	}

	private void placeBombs(int bombNumber) {
		GameRectangle gameRectangle;
		for (int i=0; i<bombNumber; i++) {
			do {
				int x =ThreadLocalRandom.current().nextInt(rectXNumber);
				int y =ThreadLocalRandom.current().nextInt(rectYNumber);
				gameRectangle = gameRectangles.get(x).get(y);
			}
			while(gameRectangle.isABomb());
			gameRectangle.getStyleClass().add("bomb");
			gameRectangle.setABomb(true);
		}
	}

	@Override
	public void addListeners() throws DeminonsException {
		
		for (List<GameRectangle> listRect : gameRectangles) {
			for (GameRectangle gameRectangle : listRect) {
				gameRectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						GameRectangle source = (GameRectangle)event.getSource();
						boolean marking = event.isControlDown();
						if (marking)
							source.setMarked(true);
						else {
							if (source.isABomb())
								try {
									endGame(false);
								} catch (DeminonsException e) {
									e.printStackTrace();
								}
							else {
								Map<String,List<GameRectangle>> surrounding = getSurrounding(source.getColumn(), source.getRow());
								showRectangle(source);
								if (surrounding.get("bombs").size()==0){
									revealSafeZone(source);
								}							
							}
						}
						int points = 0;
						int objective = rectXNumber * rectYNumber - bombNumber;
						for (List<GameRectangle> yAxis : gameRectangles) {
							for (GameRectangle gameRectangle : yAxis) {
								if (!gameRectangle.isABomb() && gameRectangle.getStyleClass().contains("seen"))
									points ++;
							}
						}
						if (points == objective)
							try {
								endGame(true);
							} catch (DeminonsException e) {
								e.printStackTrace();
							}
					}
				});
				gameRectangle.setOnMouseEntered(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						GameRectangle source = (GameRectangle)event.getSource();
						source.getStyleClass().add("entered");
					}
				});
				gameRectangle.setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						GameRectangle source = (GameRectangle)event.getSource();
						source.getStyleClass().remove("entered");
					}
				});
			}
		}
	}

	public void showRectangle(GameRectangle gameRectangle) {
		Map<String,List<GameRectangle>> surrounding = getSurrounding(gameRectangle.getColumn(), gameRectangle.getRow());
		gameRectangle.setBombs(surrounding.get("bombs").size());
		Bounds bounds = gameRectangle.getBoundsInParent();
		double minX = bounds.getMinX();
		double maxY = bounds.getMaxY();
		if (gameRectangle.getBombs() != 0 && !gameRectangle.getStyleClass().contains("seen")) {
		Text seenText = new Text(minX+7,maxY-5, Integer.toString(gameRectangle.getBombs()));
		Color seenColor = Color.MEDIUMBLUE;
		switch(gameRectangle.getBombs()) {
		case (2):
			seenColor = Color.GREEN; break;
		case (3):
			seenColor = Color.ORANGE; break;
		case (4):
			seenColor = Color.ORANGERED; break;
		case (5):
			seenColor = Color.RED; break;
		case (6):
			seenColor = Color.BROWN; break;
		case (7):
			seenColor = Color.DARKORCHID; break;
		case (8):
			seenColor = Color.PURPLE; break;
		}
		seenText.setFill(seenColor);
		seenText.setStroke(seenColor);
		seenText.setScaleX(1.2);
		seenText.setScaleY(1.2);
		if (!pane.getChildren().contains(seenText))
			pane.getChildren().add(seenText);
		}
		gameRectangle.getStyleClass().add("seen");
	}
	
	public void revealSafeZone(GameRectangle gameRectangle) {
		showRectangle(gameRectangle);
		List<GameRectangle> surrounding = getSurrounding(gameRectangle.getColumn(), gameRectangle.getRow()).get("safe");
		for (GameRectangle neighboor : surrounding) {
			if (neighboor.getBombs()==0 && !neighboor.getStyleClass().contains("seen")) {
				showRectangle(neighboor);
				revealSafeZone(neighboor);
			}
			else {
				showRectangle(neighboor);
			}
		}
	}

	public void endGame(boolean victory) throws DeminonsException {
		ButtonType yesButton = new ButtonType("Recommencer une nouvelle partie.");
		ButtonType noButton = new ButtonType("Fermer l'application.");
		CheckBox checkBox = new CheckBox("Ne plus me demander.");
		DialogPane dialogPane = new DialogPane();
		dialogPane.getButtonTypes().addAll(yesButton, noButton);
		dialogPane.setContent(checkBox);
		dialog.setDialogPane(dialogPane);
		ImageView imageView = null;
		if (victory) {
			dialog.setTitle("Victoire!");
			dialogPane.setHeaderText("Félicitations, vous avez gagné!");
			imageView = new ImageView("images/positiveIcon.png");
		}
		else {
			dialog.setTitle("Défaite");
			dialogPane.setHeaderText("Dommage, vous avez perdu.");
			imageView = new ImageView("images/negativeIcon.png");
		}
		dialogPane.setGraphic(imageView);
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.get() == yesButton) {
			pane.getChildren().clear();
			gameRectangles.clear();
			buildContent();
			addListeners();
		}
		
		Bounds rootBounds = rootPane.getLayoutBounds();
		Bounds sceneBounds = pane.localToParent(rootBounds);
		double xMid= (rootBounds.getMinX() + rootBounds.getMaxX()) /2;
		double yMid= (rootBounds.getMinY() + rootBounds.getMaxY()) /2;
	}
	
	@Override
	public Pane getRootPane() {
		return rootPane;
	}
	
}
