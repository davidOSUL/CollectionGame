package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MainGamePanel extends JPanel {
	private static final int DEFAULT_NUM_SPOTS = 24;
	private static final int SPOTS_PER_ROW = 4;
	private static final int SPOTS_PER_COLUMN = 6;
	private static final int DEFAULT_WIDTH = GameSpace.DEFAULT_WIDTH;
	private static final int DEFAULT_HEIGHT = GameSpace.DEFAULT_HEIGHT;
	private static final int NUM_PLACES = 3;
	private static final Rectangle[] validPlaces = new Rectangle[NUM_PLACES];
	
	static {
		int[] xLocations = {29,273,331,805,78,224};
		int[] yLocations = {354,544,162,469,193,252};
		for (int i = 0; i < NUM_PLACES; i++) {
				Rectangle r = new Rectangle(new Point(xLocations[i*2], yLocations[i*2]));
				r.add(new Point(xLocations[1+(i*2)], yLocations[1+(i*2)]));
				validPlaces[i] = r;
				
		
		}
	}
	private Map<Integer, GameSpace> gameSpaces = new HashMap<Integer, GameSpace>();
	private List<Rectangle> gameSpaceLocations = new ArrayList<Rectangle>();
	private static final Image background = new ImageIcon(MainGamePanel.class.getResource("/sprites/ui/background.png")).getImage();
	private static final long serialVersionUID = 1L;
	public MainGamePanel() {
		//GridLayout layoutManager = new GridLayout(SPOTS_PER_ROW, SPOTS_PER_COLUMN);
		//setLayout(layoutManager);
		setSize(843,549);
        setLayout(null);
        
		setFocusable(true);
		//setLocation(XLOCATION,YLOCATION);
		setOpaque(false);
		/*int startX =0;
		int startY = 0;	
		for (int i = 0; i < DEFAULT_NUM_SPOTS; i++) {
			int xLocation = startX + (i % SPOTS_PER_ROW)*DEFAULT_WIDTH;
			int yLocation = startY + (i / SPOTS_PER_COLUMN)*DEFAULT_HEIGHT;
			GameSpace gs = new GameSpace(xLocation,yLocation);
			gs.setImage(new ImageIcon(this.getClass().getResource("/sprites/pokemon/" + i + ".png")).getImage());
			add(gs);
		}*/
		for (int i = 0; i < NUM_PLACES; i++) {
			GameSpace gs = new GameSpace(validPlaces[i]);
			//gs.setImage(new ImageIcon(this.getClass().getResource("/sprites/pokemon/" + i + ".png")).getImage());
			add(gs);
		}
		for (int i =0; i < 1; i++) {
			GameSpace gs = createRandomSpace();
			try {
				BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream("/sprites/pokemon/" + (i+1) + ".png"));
				gs.setImage(image);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//gs.setImage(new ImageIcon(this.getClass().getResource("/sprites/pokemon/" + (i+1) + ".png")).getImage());
			add(gs);
			gameSpaces.put(i, gs);
		}
		revalidate();
		repaint();
		setVisible(true);
	}
	private GameSpace createRandomSpace() {
		GameSpace gs;
		Rectangle bounds;
		boolean foundValidRect = true;
		List<Integer> shuffledRectanglesToChoose = new ArrayList<Integer>();
		for (int i =0; i < NUM_PLACES; i++) {
			shuffledRectanglesToChoose.add(i);
		}	
		Collections.shuffle(shuffledRectanglesToChoose);
		int i =0;
		do {
			if (i >= NUM_PLACES)
				i=0;
			int rectangleToChoose = shuffledRectanglesToChoose.get(i++);
			Rectangle rectangle = validPlaces[rectangleToChoose];
			int x = (int) ThreadLocalRandom.current().nextDouble(rectangle.getMinX(), rectangle.getMaxX()-DEFAULT_WIDTH);
			int y = (int) ThreadLocalRandom.current().nextDouble(rectangle.getMinY(), rectangle.getMaxY()-DEFAULT_HEIGHT);
			gs = new GameSpace(x,y);
			bounds = gs.getBounds();
			for (Rectangle r: gameSpaceLocations) {
				if (recsOverlap(bounds,r));
					foundValidRect = false;
			}
		} while (!foundValidRect);
		gameSpaceLocations.add(bounds);
		return gs;
		
		
	}
	private boolean recsOverlap(Rectangle rec1, Rectangle rec2) {
		Point l1 = new Point((int)rec1.getX(), (int)rec1.getY());
		Point r1 = new Point((int)rec1.getMaxX(), (int)rec1.getMaxY());
		Point l2 = new Point((int)rec2.getX(), (int)rec2.getY());
		Point r2 = new Point((int)rec2.getMaxX(), (int)rec2.getMaxY());
		
		if (l1.x > r2.x || l2.x > r1.x)
	        return false;
	 
	    // If one rectangle is above other
	    if (l1.y < r2.y || l2.y < r1.y)
	        return false;
	 
	    return true;
	}
	
	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage)
	    {
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}

	
}

	

