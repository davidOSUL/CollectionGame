package gui.gameComponents;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import gui.guiutils.GuiUtils;


/**
 * Component with image that autoexpands when image is added and returns to the passed in value for width/height when there is no image
 * @author David O'Sullivan
 */
public class GameSpace extends JComponent {
	private static final long serialVersionUID = 1L;
	private Image imageAtSpace = null;
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 100;
	private boolean resizeImage = false;
	private Dimension emptyDimension = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	/**
	 * Creates a new empty GameSpace with Default width, height, and location = 0,0
	 */
	public GameSpace() {
		this.setBounds(0,0, DEFAULT_WIDTH, DEFAULT_HEIGHT);	
	}
	/**
	 * Creates a new empty GameSpace with location = (0,0) and specified dimension
	 * @param dimension the size of the GameSpace
	 */
	public GameSpace(final Dimension dimension) {
		this(0, 0, dimension);
	}
	/**
	 * Createsa new empty GameSpace with default width and height, and at the specified location
	 * @param x the x coordinate of the GameSpace's location
	 * @param y the y coordinate of the GameSpace's location
	 */
	public GameSpace(final int x, final int y) {
		this.setBounds(x,y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	/**
	 * Createsa new empty GameSpace with default width and height, and at the specified location
	 * @param p The GameSpace's Location
	 */
	public GameSpace(final Point p) {
		this(p.x, p.y);
	}
	/**
	 * Creates a new GameSpace at location (0,0) and set with the specified image. As required by GameSpace, the size of the GameSpace will be set to the size of the image
	 * @param imageAtSpace The Image to set the GameSpace to 
	 */
	public GameSpace(final Image imageAtSpace) {
		this(imageAtSpace, new Point(0,0));
	}
	/**
	 * Creates a new GameSpace at location (0,0) and set with the specified image. As required by GameSpace, the size of the GameSpace will be set to the size of the image. If resize
	 * image is set to true, then will size the image according to this.getSize() 
	 * @param imageAtSpace The Image to set the GameSpace to 
	 * @param resizeImage if true will resize image as gamespace changes size
	 */
	public GameSpace(final Image imageAtSpace, final boolean resizeImage) {
		this(imageAtSpace, new Point(0,0));
		this.resizeImage = resizeImage;
	}
	/**
	 * Creates a new GameSpace at location (0,0) and set with the specified image. As required by GameSpace, the size of the GameSpace will be set to the size of the image
	 * @param imageAtSpace The Image to set the GameSpace to 
	 * @param name The name of the GameSpace
	 */
	public GameSpace(final Image imageAtSpace, final String name) {
		this(imageAtSpace, new Point(0,0));
		setName(name);
	}
	/**
	 * Creates a new GameSpace at location p and set with the specified image. As required by GameSpace, the size of the GameSpace will be set to the size of the image
	 * @param imageAtSpace The image to set the GameSpace to
	 * @param p The location of the GameSpace
	 */
	public GameSpace(final Image imageAtSpace, final Point p) {
		this(p.x, p.y);
		this.setImage(imageAtSpace);
	}
	/**
	 * Creates a new GameSpace at specified location and set with the specified image. As required by GameSpace, the size of the GameSpace will be set to the size of the image
	 * @param x the x coordinate of the GameSpace's location
	 * @param y the y coordinate of the GameSpace's location
	 * @param imageAtSpace The image to set the GameSpace to
	 */
	public GameSpace(final int x, final int y, final Image imageAtSpace) {
		this(x,y);
		setImage(imageAtSpace);
	}
	/**
	 * Creates a new Empty GameSpace at the specified location and with size of the specified dimension
	 * @param x the x coordinate of the GameSpace's location
	 * @param y the y coordinate of the GameSpace's location
	 * @param dimension the size of the GameSpace
	 */
	public GameSpace(final int x, final int y, final Dimension dimension) {
		this(x,y);
		emptyDimension = dimension;
		setSize(dimension);
	}
	/**
	 * Creates a new Empty GameSpace at the specified location and with size of the specified dimension
	 * @param x the x coordinate of the GameSpace's location
	 * @param y the y coordinate of the GameSpace's location
	 * @param width The width of the GameSpace
	 * @param height The height of the GameSpace
	 */
	public GameSpace(final int x, final int y, final int width, final int height) {
		this(x,y,new Dimension(width, height));
	}
	/**
	 * Creates a new Empty GameSpace with location and dimension given by the inputed rectangle
	 * @param r The rectangle representing the location/size of the gameSpace
	 */
	public GameSpace(final Rectangle r) {
		this((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
	}
	/**
	 * Creates a new GameSpace at the specified location using the size and image of the inputed game space
	 * @param g The GameSpace to copy size/image from
	 * @param x the x coordinate of the GameSpace's location
	 * @param y the y coordinate of the GameSpace's location
	 */
	public GameSpace(final GameSpace g, final int x, final int y) {
		this.setBounds(g.getBounds());
		this.setLocation(x,y);
		this.setImage(g.imageAtSpace);
	}
	/**
	 * @param image image to set space to
	 * Changes Image of box AND updates the size
	 */
	public void setImage(final Image image) {
		if (image == null) {
			removeImage();
			return;
		}
		this.setSize(image.getWidth(null), image.getHeight(null));
		this.imageAtSpace = image;
	}
	/**
	 * Removes the current image and sets the size back to the default size
	 */
	public void removeImage() {
		this.setSize(emptyDimension);
		this.imageAtSpace = null;
	}
	/**
	 * returns the image on this gamespace
	 * @return  the image on this gamespace
	 */
	public Image getImage() {
		return imageAtSpace;
	}

	/** 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (!isEmpty())  {
			if (resizeImage)
				g.drawImage(GuiUtils.getScaledImage(imageAtSpace, getSize()), 0, 0, null);
			else
				g.drawImage(imageAtSpace, 0, 0, null);
		}
	}
	/**
	 * @return true if there is an image at this GameSpace, false otherwise
	 */
	public boolean isEmpty() {
		return imageAtSpace == null;
	}
	/** 
	 * @see java.awt.Component#setSize(java.awt.Dimension)
	 */
	@Override
	public void setSize(final Dimension d) {
		super.setSize(d);
		super.setPreferredSize(d);
	}
	/** 
	 * @see java.awt.Component#setSize(int, int)
	 */
	@Override
	public void setSize(final int x, final int y) {
		super.setSize(x,y);
		super.setPreferredSize(new Dimension(x, y));
		
	}
}
