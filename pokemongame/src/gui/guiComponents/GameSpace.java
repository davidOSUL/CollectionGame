package gui.guiComponents;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;


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
	public GameSpace(Point p) {
		this(p.x, p.y);
	}
	public GameSpace(Image i) {
		this(i, new Point(0,0));
	}
	public GameSpace(Image i, Point p) {
		this(p.x, p.y);
		this.setImage(i);
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
	public GameSpace(GameSpace g, int x, int y) {
		this.setBounds(g.getBounds());
		this.setLocation(x,y);
		this.setImage(g.imageAtSpace);
	}
	/**
	 * @param image image to set space to
	 * Changes Image of box AND update size
	 */
	public void setImage(Image image) {
		if (image == null) {
			removeImage();
			return;
		}
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
		//GuiUtils.drawCenteredString(g, Integer.toString(5), getBounds(), new Font("TimesRoman", Font.PLAIN, 16), Color.black);
		if (!isEmpty()) {}
			g.drawImage(imageAtSpace, 0, 0, null);
	}
	public boolean isEmpty() {
		return imageAtSpace == null;
	}

	
}
