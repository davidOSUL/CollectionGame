package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MainGamePanel extends JPanel {
	private static final int DEFAULT_NUM_SPOTS = 16;
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
	Map<Integer, GameSpace> gameSpaces = new HashMap<Integer, GameSpace>();
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
		for (int i = 0; i < 24; i++) {
			//int xLocation = startX + (i % 4)*100;
			//int yLocation = startY + (i / 4)*100;
			GameSpace gs = new GameSpace();
			gs.setImage(new ImageIcon(this.getClass().getResource("/sprites/pokemon/" + i + ".png")).getImage());
			add(gs);
		}*/
		revalidate();
		repaint();
		setVisible(true);
	}
	private GameSpace createRandomSpace() {
		int rectangleToChoose = ThreadLocalRandom.current().nextInt(0, NUM_PLACES);
		Rectangle rectangle = validPlaces[rectangleToChoose];
		int x = (int) ThreadLocalRandom.current().nextDouble(rectangle.getMinX(), rectangle.getMaxX());
		int y = (int) ThreadLocalRandom.current().nextDouble(rectangle.getMinY(), rectangle.getMaxY());
		GameSpace gs = new GameSpace(x,y);
		return gs;
		
	}
	
}

	

