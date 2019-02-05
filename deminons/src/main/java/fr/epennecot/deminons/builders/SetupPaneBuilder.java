package fr.epennecot.deminons.builders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.epennecot.deminons.Exception.DeminonsException;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SetupPaneBuilder implements PaneBuilder {

	private static final Logger LOGGER = LogManager.getLogger(SetupPaneBuilder.class);
	private Properties props;

	private GridPane rootPane = new GridPane();
	private Label rectXLabel;
	private TextField rectXField;
	private Label rectYLabel;
	private TextField rectYField;
	private Label bombsLabel;
	private TextField bombsField;
	private int rectXNumber;
	private int rectYNumber;
	Text infoText = new Text();
	private int bombNumber;
	private ImageView startButton;
	private Text startText;


	@Override
	public void buildContent() throws DeminonsException {
//		rootPane.setMaxWidth(100);
		rootPane.getStylesheets().add("deminons.css");
//		rootPane.getStyleClass().add("setupRoot");
		rootPane.setAlignment(Pos.CENTER);
		rootPane.setHgap(5);
		rootPane.setVgap(25);
		rootPane.setPadding(new Insets(25, 25, 25, 25));
		rootPane.setBackground(new Background(new BackgroundImage(new Image("images/grayTexture.jpg"), null, null, null, null)));

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

		Text welcomeText = new Text(props.getProperty("setup.welcome"));
		welcomeText.setFont(Font.font("Tahoma", FontWeight.BOLD, 70));
		welcomeText.setId("welcome");
		
		rootPane.setLayoutX(welcomeText.getWrappingWidth());
		rootPane.setMaxWidth(welcomeText.getWrappingWidth());
		
		Text rulesText = new Text(props.getProperty("setup.rules"));
		Text settingText = new Text(props.getProperty("setup.settingText"));
		settingText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		
		rectXLabel = new Label(props.getProperty("setup.rectXField"));
		rectXLabel.getStyleClass().add("setupLabel");
		rectXField = new TextField(Integer.toString(rectXNumber));
		rectXField.getStyleClass().add("setupField");
		
		rectYLabel = new Label(props.getProperty("setup.rectYField"));
		rectYLabel.getStyleClass().add("setupLabel");
		rectYField = new TextField(Integer.toString(rectYNumber));
		
		bombsLabel = new Label(props.getProperty("setup.bombsLabel"));
		bombsLabel.getStyleClass().add("setupLabel");
		bombsField = new TextField(Integer.toString(bombNumber));
		bombsLabel.setText(props.getProperty("setup.bombsLabel").replace("${0}", Integer.toString(rectXNumber*rectYNumber-1)));

		
		infoText.setText(props.getProperty("setup.infoText").replace("${0}", getCaseNumber()));
		infoText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		
		startButton = new ImageView(new Image("images/bombShut.png"));
		startText = new Text(props.getProperty("setup.startButton"));
		startText.setFill(Color.WHITE);
		startText.setStrokeWidth(2);
		startText.setScaleX(1.9);
		startText.setScaleY(1.9);
		StackPane startButtonPane = new StackPane();
		startButtonPane.getChildren().add(startButton);
		startButtonPane.getChildren().add(startText);
		
//		startButton.getStyleClass().add("playButton");

		rootPane.add(welcomeText, 0, 0);
		GridPane.setConstraints(welcomeText, 0, 0, 2, 1, HPos.CENTER, VPos.CENTER);
		Bounds rootBounds = rootPane.getBoundsInParent();
		rulesText.setWrappingWidth(rootBounds.getWidth()*0.90);
		rootPane.add(rulesText, 0, 1);
		GridPane.setConstraints(rulesText , 0, 1, 2, 1, HPos.CENTER, VPos.CENTER);
		rootPane.add(settingText, 0, 2);
		rootPane.add(rectXLabel, 0, 3);
		GridPane.setConstraints(rectXLabel , 0, 3, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
		rootPane.add(rectXField, 1, 3);
		rootPane.add(rectYLabel, 0, 4);
		GridPane.setConstraints(rectYLabel , 0, 4, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS);
		rootPane.add(rectYField, 1, 4);
			rootPane.add(infoText, 0, 5, 2, 1);
		GridPane.setConstraints(infoText , 0, 5, 2, 1, HPos.CENTER, VPos.CENTER);
		rootPane.add(bombsLabel, 0, 6);
		rootPane.add(bombsField, 1, 6);
		GridPane.setConstraints(bombsLabel, 0, 6, 1, 1, HPos.CENTER, VPos.CENTER);
		rootPane.add(startButtonPane, 0, 7, 2, 1);
		GridPane.setConstraints(startButtonPane , 0, 7, 2, 1, HPos.CENTER, VPos.CENTER);
//			rootPane.add(errorText, 0, 8, 2, 1);
//		GridPane.setConstraints(errorText , 0, 8, 2, 1, HPos.CENTER, VPos.CENTER);
		
		ColumnConstraints column0 = new ColumnConstraints();
		column0.setPercentWidth(75);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(25);
		rootPane.getColumnConstraints().addAll(column0, column1);
	}

	@Override
	public void addListeners() throws DeminonsException {

		rectXField.textProperty().addListener((obs, oldText, NewText) -> {
			try {
				rectXNumber = Integer.parseInt(NewText);
				if (rectXNumber > 70 || rectXNumber <= 0) {
					rectXNumber = Integer.parseInt(oldText);
					rectXField.setText(oldText);
				}
			}
			catch (Exception e) {
				rectXField.setText(oldText);
				rectXNumber = Integer.parseInt(oldText);
			}
			infoText.setText("Ce plateau fera " + getCaseNumber() + " cases.");
			bombsLabel.setText(props.getProperty("setup.bombsLabel").replace("${0}", Integer.toString(rectXNumber*rectYNumber-1)));
			if (bombNumber > rectXNumber*rectYNumber-1) {
				bombNumber = rectXNumber*rectYNumber-1;
				bombsField.setText(Integer.toString(bombNumber));
			}
		});
		rectYField.textProperty().addListener((obs, oldText, NewText) -> {
			try {
				rectYNumber = Integer.parseInt(NewText);
				if (rectYNumber > 40 || rectYNumber <= 0) {
					rectYNumber = Integer.parseInt(oldText);
					rectYField.setText(oldText);
				}
			}
			catch (Exception e) {
				rectYNumber = Integer.parseInt(oldText);
				rectYField.setText(oldText);
			}
			infoText.setText("Ce plateau fera " + getCaseNumber() + " cases.");
			bombsLabel.setText(props.getProperty("setup.bombsLabel").replace("${0}", Integer.toString(rectXNumber*rectYNumber-1)));
			if (bombNumber > rectXNumber*rectYNumber-1) {
				bombNumber = rectXNumber*rectYNumber-1;
				bombsField.setText(Integer.toString(bombNumber));
			}
		});
		bombsField.textProperty().addListener((obs, oldText, newText) -> {
			try {
				bombNumber = Integer.parseInt(newText);
				if (bombNumber >= rectXNumber * rectYNumber || bombNumber < 0) {
					bombsField.setText(oldText);
					bombNumber = Integer.parseInt(oldText);
				}
			}
			catch (NumberFormatException e) {
				bombsField.setText(oldText);
				bombNumber = Integer.parseInt(oldText);
			}
		});
		
		
		startButton.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				startButton.setImage(new Image("images/bombOnFire.png"));
			}
		});
		startButton.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				startButton.setImage(new Image("images/bombShut.png"));
			}
		});
		//TODO: See if I can generalize those 2 listeners into one with the 2 above
		startText.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				startButton.setImage(new Image("images/bombOnFire.png"));
			}
		});
		startText.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				startButton.setImage(new Image("images/bombShut.png"));
			}
		});
		startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				startButtonClicked();
			}
		});
		startText.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				startButtonClicked();
			}
		});
	}

	private String getCaseNumber() {
		int xTextField = Integer.parseInt(rectXField.getText());
		int yTextField = Integer.parseInt(rectYField.getText());
		return Integer.toString(xTextField * yTextField);
	}
	
	@Override
	public Pane getRootPane() {
		return rootPane;
	}
	
	private void startButtonClicked(){
		rectXNumber = Integer.parseInt(rectXField.getText());
		rectYNumber = Integer.parseInt(rectYField.getText());
		bombNumber = Integer.parseInt(bombsField.getText());
		if (LOGGER.isInfoEnabled())
			LOGGER.info("routing to \"game\" " + " with parameters rectX: " + rectXNumber + " rectY: "
					+ rectYNumber + " bombs: " + bombNumber);
		rootPane = new GridPane();
		route("game", rectXNumber, rectYNumber, bombNumber);
		}

	@Override
	public void setRouterParams(int rectXNumber, int rectYNumber, int bombNumber) {
		this.rectXNumber = rectXNumber;
		this.rectYNumber = rectYNumber;
		this.bombNumber = bombNumber;
	}

}
