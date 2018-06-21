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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
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
import gui.Grid.GridSpace;
import guiutils.GuiUtils;
import thingFramework.Thing;

public class MainGamePanel extends JPanel{
	private static final int NUM_GRIDS = 3;
	private static final int GRID_SPACE_DIM = 24;
	/**
	 * When moving objects around the screen, defer to this grid for the size to round the image to
	 */
	private static final int DEFAULT_GRID = 0;
	private int currspace_num = 0;
	private static final Rectangle[] gridLocs = new Rectangle[NUM_GRIDS];
	private static final Grid[] grids = new Grid[NUM_GRIDS];
	private boolean addingSomething = false;
	private Presenter p;
	public GridSpace currentMoving = null;
	static {
		int[] xLocations = {30,273,331,800,80,240};
		int[] yLocations = {333,490,142,445,170,229};
		for (int i = 0; i < NUM_GRIDS; i++) {
				Rectangle r = new Rectangle(new Point(xLocations[i*2], yLocations[i*2]));
				r.add(new Point(xLocations[1+(i*2)], yLocations[1+(i*2)]));
				gridLocs[i] = r;
				
		
		}
	}
	private static final long serialVersionUID = 1L;
	public MainGamePanel(Presenter p) {
		setSize(GameView.WIDTH,GameView.HEIGHT);
        setLayout(null);
		setFocusable(true);
		setOpaque(false);
		this.p = p;
			for (int i = 0; i < NUM_GRIDS; i++) {
				Grid gs = new Grid(gridLocs[i], GRID_SPACE_DIM, GRID_SPACE_DIM);
				gs.addMouseMotionListener(new MouseMotionAdapter() {
					 @Override
					 public void mouseMoved(MouseEvent e) {
						if (addingSomething) {
							gs.setHighlight(e.getPoint(), currentMoving);
						}
					}
				});
				gs.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (addingSomething) {
							boolean success = gs.addGridSpaceSnapToGrid(currentMoving, e.getPoint());
							if (success) {
								p.notifyAdded(gs);
								addingSomething = false;
								currentMoving = null;
							}
						}
					}
					@Override
					public void mouseExited(MouseEvent e) {
						if (addingSomething) {
							gs.removeHighlight();
						}
					}
					@Override
					public void mouseEntered(MouseEvent e) {
						if (addingSomething) {
							gs.setHighlight(e.getPoint(), currentMoving);
						}
					}
				});
				add(gs);
				grids[i] = gs;
			}
		this.addMouseMotionListener(new MouseMotionAdapter() {
			 @Override
			 public void mouseMoved(MouseEvent e) {
				if (addingSomething) {
					currentMoving.setLocation(e.getPoint());
				}
			}
		});
		currspace_num = 0;
		revalidate();
		repaint();
		setVisible(true);
	}
	public void thingAdd(GameSpace gs){
		addingSomething = true;
		currentMoving = grids[DEFAULT_GRID].generateGridSpace(gs);
	}


	
}

	

