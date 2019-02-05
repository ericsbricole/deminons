package fr.epennecot.deminons.builders;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.epennecot.deminons.Exception.DeminonsException;
import fr.epennecot.deminons.model.GameRectangle;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GamePaneBuilder implements PaneBuilder {
	
	private static final Logger LOGGER = LogManager.getLogger(GamePaneBuilder.class);
	private Properties props;
	
	private Pane pane = new Pane();
	private StackPane rootPane = new StackPane(pane);
	private Dialog<ButtonType> dialog = new Dialog<ButtonType>();
	private int rectXNumber;
	private int rectYNumber;
	private int bombNumber;
	private List<List<GameRectangle>> gameRectangles = new ArrayList<List<GameRectangle>>();
	
	public void setRectXNumber(int rectXNumber) {
		this.rectXNumber = rectXNumber;
	}

	public void setRectYNumber(int rectYNumber) {
		this.rectYNumber = rectYNumber;
	}

	public void setBombNumber(int bombNumber) {
		this.bombNumber = bombNumber;
	}

	@Override
	public void buildContent() throws DeminonsException {
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Building content with parameters rectX: " + rectXNumber + " rectY: " + rectYNumber + " bombs: " + bombNumber);
		props = new Properties();
		String fileName = "deminons.properties";
		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream(fileName);
			props.load(input);
		} catch (FileNotFoundException e) {
			System.err.println("Could not find the property file " + fileName);
		} catch (IOException e) {
			System.err.println("An error occured loading the property file" + fileName);
		}
		pane.getStylesheets().add("deminons.css");
//		Canvas canvas = new Canvas(510,510);
//		pane.getChildren().add(canvas);
		
		int x = 0;
		int y = 0;
		int width = 20;
		int height = 20;
		for (int i=0; i<rectXNumber; i++ ) {
			gameRectangles.add(new ArrayList<GameRectangle>());
			for (int j=0; j<rectYNumber; j++) {
				GameRectangle hiddenRect = new GameRectangle(0, 0, width, height, false);
				hiddenRect.setColumn(i);
				hiddenRect.setRow(j);
				hiddenRect.getStyleClass().add("hiddenRect");
				gameRectangles.get(i).add(hiddenRect);
				y += height;
			}
			x += width;
			y = 0;
		}

		pane.setMinWidth(rectXNumber*width);
		pane.setMaxWidth(rectXNumber*width);
		pane.setMinHeight(rectYNumber*height);
		pane.setMaxHeight(rectYNumber*height);
		for (List<GameRectangle> list : gameRectangles) {
			for (GameRectangle rect : list) {
				StackPane stack = new StackPane(rect);
				stack.setLayoutX(rect.getRow()*20);
				stack.setLayoutY(rect.getColumn()*20);
				pane.getChildren().add(stack);
			}
		}
		
		placeBombs(bombNumber);
		//set the bombs
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
		
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("getSurronding for rectangle in: " + x +","+y);
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
						boolean marking = event.getButton() == MouseButton.SECONDARY;
						if (marking) {
							if (source.getStyleClass().contains("seen"))
								return;
							StackPane stack = (StackPane)source.getParent();
							if (!source.isMarked()) {
								source.setMarked(true);
								source.getStyleClass().add("marked");
								ImageView imageView =new ImageView(new Image("images/flag.png"));
								imageView.fitHeightProperty().bind(gameRectangle.heightProperty());
								imageView.fitWidthProperty().bind(gameRectangle.widthProperty());
								stack.getChildren().add(imageView);
								playSound("unlockMine.mp3");
							}
							else {
								removeTheMark(source);
							}
						}
						
						else {
							if (source.isABomb()) {
									try {
										endGame(false);
									} catch (DeminonsException e) {
										e.printStackTrace();
									}
							}
							else {
								removeTheMark(source);
								Map<String,List<GameRectangle>> surrounding = getSurrounding(source.getColumn(), source.getRow());
								showRectangle(source);
								if (surrounding.get("bombs").size()==0){
									revealSafeZone(source);
								}							
								int points = 0;
								int objective = rectXNumber * rectYNumber - bombNumber;
								for (List<GameRectangle> yAxis : gameRectangles) {
									for (GameRectangle gameRectangle : yAxis) {
										if (!gameRectangle.isABomb() && gameRectangle.getStyleClass().contains("seen"))
											points ++;
									}
								}
								if (points >= objective)
									try {
										endGame(true);
									} catch (DeminonsException e) {
										e.printStackTrace();
									}
							}
							}
						}
				});
				gameRectangle.getParent().setOnMouseEntered(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						StackPane source = (StackPane)event.getSource();
						source.getStyleClass().add("entered");
						source.toFront();
					}
				});
				gameRectangle.getParent().setOnMouseExited(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						StackPane source = (StackPane)event.getSource();
						source.getStyleClass().remove("entered");
					}
				});
			}
		}
	}

	public void showRectangle(GameRectangle gameRectangle) {
		Map<String,List<GameRectangle>> surrounding = getSurrounding(gameRectangle.getColumn(), gameRectangle.getRow());
		gameRectangle.setBombs(surrounding.get("bombs").size());
		StackPane stack = (StackPane) gameRectangle.getParent();
		if (gameRectangle.getBombs() != 0 && !gameRectangle.getStyleClass().contains("seen")) {
			Text seenText = new Text(Integer.toString(gameRectangle.getBombs()));
			Color seenColor = Color.MEDIUMBLUE;
			switch(gameRectangle.getBombs()) {
			case (2):
				seenColor = Color.GREEN; break;
			case (3):
				seenColor = Color.ORANGE; break;
			case (4):
				seenColor = Color.ORANGERED; break;
			case (5):
				seenColor = Color.BROWN; break;
			case (6):
				seenColor = Color.RED; break;
			case (7):
				seenColor = Color.DARKORCHID; break;
			case (8):
				seenColor = Color.PURPLE; break;
			}
			seenText.setFill(seenColor);
			seenText.setStroke(seenColor);
			seenText.setScaleX(1.2);
			seenText.setScaleY(1.2);
	//		if (!pane.getChildren().contains(seenText))
	//			pane.getChildren().add(seenText);
			
			if (!stack.getChildren().contains(seenText))
				stack.getChildren().add(seenText);
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
	
	private void clearRootPane() {
		pane.getChildren().clear();
		gameRectangles.clear();
	}

	public void endGame(boolean victory) throws DeminonsException {
		ButtonType setupButton = new ButtonType(props.getProperty("game.toSetup"));
		ButtonType yesButton = new ButtonType(props.getProperty("game.newGame"));
		DialogPane dialogPane = new DialogPane();
		dialogPane.getButtonTypes().addAll(setupButton, yesButton);
		dialog.setDialogPane(dialogPane);
		ImageView imageView = null;
		if (victory) {
			dialog.setTitle(props.getProperty("game.victoryWindow"));
			dialogPane.setHeaderText(props.getProperty("game.victoryText"));
			imageView = new ImageView("images/bombWet.png");
		}
		else {
			dialog.setTitle(props.getProperty("game.defeatWindow"));
			dialogPane.setHeaderText(props.getProperty("game.defeatText"));
			imageView = new ImageView("images/explosion.png");
			playSound("explosion.mp3");
		}
		dialogPane.setGraphic(imageView);
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.get() == setupButton) {
			clearRootPane();
			route("setup", rectXNumber, rectYNumber, bombNumber);
		}
		else if (result.get() == yesButton) {
			clearRootPane();
			buildContent();
			addListeners();
		}
	}
	
	private void removeTheMark(GameRectangle gameRectangle) {
		gameRectangle.setMarked(false);
		gameRectangle.getStyleClass().remove("marked");
		StackPane stack = (StackPane) gameRectangle.getParent();
		Optional<Node> child = stack.getChildren().stream().filter(c -> c instanceof ImageView).findAny();
		if (child.isPresent()) {
			stack.getChildren().remove(child.get());			
		}
	}
	
	private void playSound(String fileName) {
		File file = new File("src/main/resources/sounds/" + fileName);
		Media media = new Media(file.toURI().toString());
		new MediaPlayer(media).play();
	}
	
	@Override
	public Pane getRootPane() {
		return rootPane;
	}
	
	@Override
	public void setRouterParams(int rectXNumber, int rectYNumber, int bombNumber){
		this.rectXNumber = rectXNumber;
		this.rectYNumber = rectYNumber;
		this.bombNumber = bombNumber;
	}
	
}
