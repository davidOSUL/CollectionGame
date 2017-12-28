package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;


public class GameSpace extends JComponent {
	private static final long serialVersionUID = 1L;
	private Image imageAtSpace = null;
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 100;
	int locationX = 0;
	int locationY = 0;
	int width = DEFAULT_WIDTH;
	int height = DEFAULT_HEIGHT;
	public GameSpace() {
		//setBounds(locationX, locationY, width, height);
	}
	public GameSpace(Dimension dimension) {
		this(0, 0, dimension);
	}
	public GameSpace(int x, int y) {
		this.locationX = x;
		this.locationY = y;
		this.setBounds(locationX, locationY, width, height);

	}
	public GameSpace(int x, int y, Image thingAtSpace) {
		this(x,y);
		this.imageAtSpace = thingAtSpace;
	}
	public GameSpace(int x, int y, Dimension dimension) {
		this(x,y);
		width = (int) dimension.getWidth();
		height = (int) dimension.getHeight(); 
		setSize(dimension);
	}
	public GameSpace(int x, int y, Image thingAtSpace, Dimension dimension) {
		this(x,y, dimension);
		this.imageAtSpace = thingAtSpace;
	
	}
	public void setImage(Image image) {
		this.imageAtSpace = image;
	}
	public void removeImage() {
		this.imageAtSpace = null;
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//g.drawRect(0,0, width, height);
		if (!isEmpty())
			g.drawImage(imageAtSpace, 0, 0, width, height, null);
	}
	public boolean isEmpty() {
		return imageAtSpace == null;
	}
}
