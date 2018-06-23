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
import java.util.function.BiConsumer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
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
	private static final Rectangle[] gridLocs = new Rectangle[NUM_GRIDS];
	private static final Grid[] grids = new Grid[NUM_GRIDS];
	private boolean addingSomething = false;
	private GameView gv;
	public GridSpace currentMoving = null;
	private Grid activeGrid = null;
	private static final Image NOTIFICATION_LOGO = GuiUtils.getScaledImage(GuiUtils.readImage("/sprites/ui/pokeball.png"), 50, 50);
	private static final Point NOTIFICATION_LOCATION = new Point(749, 44);
	private NotificationButton notifications;
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

	public MainGamePanel(GameView gv) {
		this.gv = gv;
		notifications = new NotificationButton(NOTIFICATION_LOGO, NOTIFICATION_LOCATION, x -> {x.NotificationClicked();}, gv, true );
		setSize(GameView.WIDTH,GameView.HEIGHT);
        setLayout(null);
		setFocusable(true);
		setOpaque(false);
			for (int i = 0; i < NUM_GRIDS; i++) {
				Grid currGrid = new Grid(gridLocs[i], GRID_SPACE_DIM, GRID_SPACE_DIM);
				currGrid.addMouseMotionListener(new MouseMotionAdapter() {
					 @Override
					 public void mouseMoved(MouseEvent e) {
						if (addingSomething) {
							MainGamePanel.this.dispatchEvent(e);
							currGrid.updateHighlight(e.getPoint());
						}
					}
				});
				currGrid.addMouseListener(generateGridClickListener(currGrid));
				currGrid.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseExited(MouseEvent e) {
						if (addingSomething) {
							activeGrid = null;
							currGrid.removeHighlight();
						}
					}
					@Override
					public void mouseEntered(MouseEvent e) {
						if (addingSomething) {
							activeGrid = currGrid;
							currGrid.setHighlight(currentMoving);
						}
					}
				});
				add(currGrid);
				grids[i] = currGrid;
			}
		this.addMouseMotionListener(new MouseMotionAdapter() {
			 @Override
			 public void mouseMoved(MouseEvent e) {
				if (addingSomething) {
					if (activeGrid != null) {
						Point snapPoint = activeGrid.getAbsoluteSnapPoint(e.getPoint());
						currentMoving.setLocation(snapPoint.x+activeGrid.getX(), snapPoint.y+activeGrid.getY());
					}
					else {
						currentMoving.setLocation(e.getPoint());
					}
				}
			}
		});
		this.addMouseListener(new MouseAdapter() {
			 @Override
			 public void mouseClicked(MouseEvent e) {
				 System.out.println(e.getPoint());
			 }
		});
		add(notifications);
		revalidate();
		repaint();
		setVisible(true);
	}
	public void updateNotifications(int num) {
		notifications.setNumNotifications(num);
	}
	public void thingAdd(GameSpace gs){
		addingSomething = true;
		currentMoving = grids[DEFAULT_GRID].generateGridSpace(gs);
		add(currentMoving);
	}
	private void gridClick(Grid currGrid, Point p) {
		if (addingSomething) {
			GameSpace result = currGrid.addGridSpaceSnapToGrid(currentMoving,p);
			if (result != null) {
				gv.getPresenter().notifyAdded(result);
				currGrid.removeHighlight();
				addingSomething = false;
				remove(currentMoving);
				currentMoving = null;
			}
		}
	}
	private MouseClickWithThreshold<MainGamePanel> generateGridClickListener(Grid currGrid) {
		BiConsumer<MainGamePanel, MouseEvent> input = (g, e) -> {
			 g.gridClick(currGrid, e.getPoint());
		};
		MouseClickWithThreshold<MainGamePanel> mcwt = new MouseClickWithThreshold<MainGamePanel>(20, input, this); 
		return mcwt;
	}


	
}

	

