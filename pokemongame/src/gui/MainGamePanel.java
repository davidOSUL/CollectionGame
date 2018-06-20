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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import gameutils.GameUtils;

public class MainGamePanel extends JPanel implements MouseListener{
	private static final boolean SHOW_AREAS = true; //developer purpose, will show the large rectangles pokemon spawn in
	private static final int DEFAULT_NUM_SPOTS = 24;
	private static final int SPOTS_PER_ROW = 4;
	private static final int SPOTS_PER_COLUMN = 6;
	private static final int MAX_WIDTH = GameSpace.DEFAULT_WIDTH;
	private static final int MAX_HEIGHT = GameSpace.DEFAULT_HEIGHT;
	private static final int NUM_PLACES = 3;
	private static final int GRID_SIZE = 24;
	private int currspace_num = 1;
	private static final Rectangle[] validPlaces = new Rectangle[NUM_PLACES];
	
	static {
		int[] xLocations = {30,273,331,800,80,240};
		int[] yLocations = {333,490,142,445,170,229};
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
        addMouseListener(this);
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
		if (SHOW_AREAS) {
		for (int i = 0; i < NUM_PLACES; i++) {
			Grid gs = new Grid(validPlaces[i], GRID_SIZE, GRID_SIZE);
			//gs.setImage(new ImageIcon(this.getClass().getResource("/sprites/pokemon/" + i + ".png")).getImage());
			add(gs);
		}
		}
		for (int i =0; i < 24; i++) {
			try {
				BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream("/sprites/pokemon/" + (i+1) + ".png"));
				image = trimImage(image);
				boolean created = createAndAddRandomSpace(image);
				if (!created)
					System.out.println("Failed to create " + i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		revalidate();
		repaint();
		setVisible(true);
	}
	//returns false if failed to create space
	private boolean createAndAddRandomSpace(BufferedImage image) {
		GameSpace gs = createRandomSpace(image);
		if (gs == null)
			return false;
		//Grid g = (Grid) getComponent(1);
		//g.add(gs);
		gameSpaces.put(currspace_num, gs);
		return true;
	}
	private void removeSpace(int i) {
		remove(gameSpaces.get(i));
		gameSpaces.remove(i);
	}
	private GameSpace createRandomSpace(BufferedImage image) {
		int MAX_ATTEMPTS = 30;
		GameSpace gs= null;
		Rectangle bounds = null;
		boolean foundValidRect = true;
		List<Integer> shuffledRectanglesToChoose = new ArrayList<Integer>();
		for (int i =0; i < NUM_PLACES; i++) {
			shuffledRectanglesToChoose.add(i);
		}	
		Collections.shuffle(shuffledRectanglesToChoose);
		int i =0;
		int numAttempts =0;
		int rectangleToChoose = 0;
		do {
			foundValidRect = true;
			numAttempts++;
			if (i >= NUM_PLACES)
				i=0;
			rectangleToChoose = shuffledRectanglesToChoose.get(i++);
			
			Rectangle rectangle = validPlaces[rectangleToChoose];
			//TODO: CHange grid so it implements its own width/hheight and use these values for this. 
			if (rectangle.getMinX()+image.getWidth()+1 >= rectangle.getMaxX() || rectangle.getMinY()+image.getHeight()+1 >= rectangle.getMaxY()) {
				foundValidRect = false;
				continue;
			}
			int x = (int) ThreadLocalRandom.current().nextDouble(0, rectangle.getWidth());
			int y = (int) ThreadLocalRandom.current().nextDouble(0, rectangle.getHeight());
			gs = new GameSpace(GameUtils.roundToMultiple(x, GRID_SIZE),GameUtils.roundToMultiple(y, GRID_SIZE));
			bounds = gs.getBounds();
			for (Rectangle r: gameSpaceLocations) {
				if (bounds.intersects(r))
					foundValidRect = false;
			}
		} while (!foundValidRect && numAttempts < MAX_ATTEMPTS);
		if (numAttempts >= MAX_ATTEMPTS)
			return null;
		gameSpaceLocations.add(bounds);
		gs.setImage(image); 
		Grid g = (Grid) getComponent(rectangleToChoose);
		g.add(gs);
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
	
	private static BufferedImage trimImage(BufferedImage image) {
	    int width = image.getWidth();
	    int height = image.getHeight();
	    int top = height;
	    int bottom = 0;
	    int left = width;
	    int right = 0;
	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            if (image.getRGB(x, y) != 0){
	                top    = Math.min(top, y);
	                bottom = Math.max(bottom, y);
	                left   = Math.min(left, x);
	                right  = Math.max(right, x);
	            }
	        }
	    }
	    BufferedImage newImage = new BufferedImage(GameUtils.roundToMultiple(right-left, GRID_SIZE), GameUtils.roundToMultiple(bottom-top, GRID_SIZE), BufferedImage.TYPE_INT_ARGB);
	    BufferedImage cutImage = image.getSubimage(left, top, right-left, bottom-top);
	    Graphics2D bGr = newImage.createGraphics();
	    bGr.drawImage(cutImage, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return newImage;
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
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		 
        int y = e.getY();

        System.out.println("Mouse Pressed at X: " + x + " - Y: " + y);
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	
}

	

