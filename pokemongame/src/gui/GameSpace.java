package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JComponent;
import javax.swing.border.LineBorder;


public class GameSpace extends JComponent {
	private static final long serialVersionUID = 1L;
	private Image imageAtSpace = null;
	public static final int DEFAULT_WIDTH = 50;
	public static final int DEFAULT_HEIGHT = 50;
	int locationX = 0;
	int locationY = 0;
	int width = DEFAULT_WIDTH;
	int height = DEFAULT_HEIGHT;
	public GameSpace() {
		setBounds(locationX, locationY, width, height);
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
	public GameSpace(int x, int y, int width, int height) {
		this(x,y,new Dimension(width, height));
	}
	public GameSpace(int x, int y, Image thingAtSpace, Dimension dimension) {
		this(x,y, dimension);
		this.imageAtSpace = thingAtSpace;
	
	}
	public GameSpace(Rectangle r) {
		this((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
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
		g.drawRect(0,0, width-1, height-1);
		if (!isEmpty())
			g.drawImage(imageAtSpace, 0, 0, this);
	}
	public boolean isEmpty() {
		return imageAtSpace == null;
	}
	
}
