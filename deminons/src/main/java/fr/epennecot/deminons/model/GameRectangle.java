package fr.epennecot.deminons.model;

import java.util.Objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class GameRectangle extends Rectangle {

	private boolean isABomb;
	private int bombs;
	private boolean marked;
	private int column;
	private int row;
	private StackPane backgroung;
	public GameRectangle() {
		super();
	}
	
	public GameRectangle(int x, int y, int width, int height, boolean isABomb) {
		super(x,y,width,height);
		this.isABomb = isABomb;
	}

	public boolean isABomb() {
		return isABomb;
	}

	public void setABomb(boolean isABomb) {
		this.isABomb = isABomb;
	}

	public int getBombs() {
		return bombs;
	}

	public void setBombs(int bombs) {
		this.bombs = bombs;
	}

	public boolean isMarked() {
		return marked;
	}
	
	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public StackPane getBackgroung() {
		return backgroung;
	}
	
	public void setBackgroung(StackPane backgroung) {
		this.backgroung = backgroung;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GameRectangle))
			return false;
		GameRectangle gr = (GameRectangle) obj;
	return column == gr.getColumn() && row == gr.getRow();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(column, row);
	}


}
