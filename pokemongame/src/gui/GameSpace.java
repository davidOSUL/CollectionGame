package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;


/**
 * Component with image that autoexpands when image is added and returns to the passed in value for width/height when there is no image
 * @author David O'Sullivan
 */
public class GameSpace extends JComponent {
	private static final long serialVersionUID = 1L;
	private Image imageAtSpace = null;
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 100;
	private Dimension emptyDimension = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	public GameSpace() {
		this.setBounds(0,0, DEFAULT_WIDTH, DEFAULT_HEIGHT);	
	}
	public GameSpace(Dimension dimension) {
		this(0, 0, dimension);
	}
	public GameSpace(int x, int y) {
		this.setBounds(x,y, DEFAULT_WIDTH, DEFAULT_HEIGHT);

	}
	public GameSpace(int x, int y, Image thingAtSpace) {
		this(x,y);
		setImage(thingAtSpace);
	}
	public GameSpace(int x, int y, Dimension dimension) {
		this(x,y);
		emptyDimension = dimension;
		setSize(dimension);
	}
	public GameSpace(int x, int y, int width, int height) {
		this(x,y,new Dimension(width, height));
	}
	public GameSpace(int x, int y, Image thingAtSpace, Dimension dimension) {
		this(x,y, dimension);
		setImage(thingAtSpace);
	
	}
	public GameSpace(Rectangle r) {
		this((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
	}
	public GameSpace(GameSpace g) {
		this.setBounds(g.getBounds());
		this.setImage(g.imageAtSpace);
	}
	/**
	 * @param image image to set space to
	 * Changes Image of box AND update size
	 */
	public void setImage(Image image) {
		if (image == null)
			return;
		this.setSize(image.getWidth(null), image.getHeight(null));
		this.imageAtSpace = image;
	}
	public void removeImage() {
		this.setSize(emptyDimension);
		this.imageAtSpace = null;
	}
	public Image getImage() {
		return imageAtSpace;
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		//g.drawRect(0,0, width-1, height-1);
		if (!isEmpty())
			g.drawImage(imageAtSpace, 0, 0, null);
	}
	public boolean isEmpty() {
		return imageAtSpace == null;
	}
	
}
