package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;

import javax.swing.JComponent;


public class GameSpace extends JComponent {
	private static final long serialVersionUID = 1L;
	Image imageAtSpace = null;
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 100;
	public GameSpace() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	public GameSpace(int x, int y) {
		this();
		this.setLocation(x, y);
	}
	public GameSpace(int x, int y, Image thingAtSpace) {
		this(x,y);
		this.imageAtSpace = thingAtSpace;
	}
	public GameSpace(int x, int y, Dimension dimension) {
		this(x,y);
		setSize(dimension);
	}
	public GameSpace(int x, int y, Image thingAtSpace, Dimension dimension) {
		this(x,y);
		setSize(dimension);
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
		g.drawRect(getX(), getY(), getWidth(), getHeight());
		if (!isEmpty())
			g.drawImage(imageAtSpace, getX(), getY(), null);
	}
	public boolean isEmpty() {
		return imageAtSpace == null;
	}
}
