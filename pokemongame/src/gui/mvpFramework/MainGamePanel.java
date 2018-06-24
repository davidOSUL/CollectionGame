package gui.mvpFramework;

import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.BiConsumer;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gui.guiComponents.GameSpace;
import gui.guiComponents.Grid;
import gui.guiComponents.Grid.GridSpace;
import gui.guiComponents.NotificationButton;
import gui.guiutils.GuiUtils;
import gui.mouseAdapters.MouseClickWithThreshold;

public class MainGamePanel extends JPanel{
	private static final int NUM_GRIDS = 3;
	private static final int GRID_SPACE_DIM = 24;
	/**
	 * When moving objects around the screen, defer to this grid for the size to round the image to
	 */
	private static final int DEFAULT_GRID = 0;
	private static final Rectangle[] gridLocs = new Rectangle[NUM_GRIDS];
	private static final Grid[] grids = new Grid[NUM_GRIDS];
	//TODO: At some point put these (addingSomething, currentMoving, etc.) in their own manager object
	private boolean addingSomething = false;
	private long timeAddedTime; 
	private static final long MIN_WAIT_TO_ADD = 300; //Wait after clicking before can add to board
	private GameView gv;
	private GridSpace currentMoving = null;
	private Grid activeGrid = null;
	private boolean setHighlight = false;
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
				Grid currGrid = new Grid(gridLocs[i], GRID_SPACE_DIM, GRID_SPACE_DIM, this);
				currGrid.addMouseMotionListener(new MouseMotionAdapter() {
					 @Override
					 public void mouseMoved(MouseEvent e) {
						if (addingSomething) {
							if (!setHighlight) {
								activeGrid = currGrid;
								currGrid.setHighlight(currentMoving);
								setHighlight = true;
							}
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
							setHighlight = false;
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
						setCurrentRelativeToActiveGrid(e);
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
	private void setCurrentRelativeToActiveGrid(MouseEvent e) {
		Point snapPoint = activeGrid.getAbsoluteSnapPoint(e.getPoint());
		currentMoving.setLocation(snapPoint.x+activeGrid.getX(), snapPoint.y+activeGrid.getY());
	}
	public void updateNotifications(int num) {
		notifications.setNumNotifications(num);
	}
	public void thingAdd(GameSpace gs){
		addingSomething = true;
		currentMoving = grids[DEFAULT_GRID].generateGridSpace(gs);
		Point p = MouseInfo.getPointerInfo().getLocation();
		if (p != null) {
			SwingUtilities.convertPointFromScreen(p, this);
			currentMoving.setLocation(p);
		}
		add(currentMoving);
		timeAddedTime = System.currentTimeMillis();
	}
	private void gridClick(Grid currGrid, Point p) {
		if (addingSomething && System.currentTimeMillis()-timeAddedTime > MIN_WAIT_TO_ADD) {
			GameSpace result = currGrid.addGridSpaceSnapToGrid(currentMoving,p);
			if (result != null) {
				gv.getPresenter().notifyAdded(result);
				currGrid.removeHighlight();
				addingSomething = false;
				remove(currentMoving);
				currentMoving = null;
				setHighlight = false;
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
	public void movePlacedObject(GameSpace gs) {
		gv.attemptThingMove(gs);
	}


	
}

	

