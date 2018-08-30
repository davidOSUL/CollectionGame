package gui.mvpFramework;
import static gameutils.Constants.DEBUG;

import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.function.BiConsumer;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gui.displayComponents.ButtonBuilder;
import gui.gameComponents.BackgroundWithText;
import gui.gameComponents.GameSpace;
import gui.gameComponents.NotificationButton;
import gui.gameComponents.PictureButton;
import gui.gameComponents.grid.Grid;
import gui.gameComponents.grid.GridSpace;
import gui.gameComponents.grid.GridSpace.GridSpaceData;
import gui.guiutils.GuiUtils;
import gui.guiutils.KeyBindingManager;
import gui.mouseAdapters.MouseClickWithThreshold;
import gui.mvpFramework.presenter.AddType;

/**
 * Where most of the front end components lie. This is within the GameView
 * @author David O'Sullivan
 *
 */
public class MainGamePanel extends JPanel{
	/**
	 * The Total Number of Grids to have on the mainGamePanel
	 */
	private static final int NUM_GRIDS = 3;
	/**
	 * The width/height of a spot on the grid
	 */
	private static final int GRID_SPACE_DIM = 24;
	/**
	 * When moving objects around the screen, defer to this grid for the size to round the image to
	 */
	private static final int DEFAULT_GRID = 0;
	/**
	 * The Set of rectangles corresponding to layout of the grids
	 */
	private static final Rectangle[] gridLocs = new Rectangle[NUM_GRIDS];
	/**
	 * The set of grids on this panel
	 */
	private static final Grid[] grids = new Grid[NUM_GRIDS];
	//TODO: At some point put these (addingSomething, currentMoving, etc.) in their own manager object
	
	/**
	 * Signifies that an object is attempting to be added. This will be set whether it is a new object or an object that is moving
	 */
	private boolean addingSomething = false;
	
	/**
	 * The context of the current add (e.g. pokemon from queue, moving an existing GridSpace, etc.)
	 */
	private AddType typeOfAdd;
	/**
	 * The time that the current Add Attempt was started
	 */
	private long timeAddedTime; 
	/**
	 * The minimum time that the user must wait between starting the add attempt and placing the GridSpace in a grid in milliseconds
	 */
	private static final long MIN_WAIT_TO_ADD = 300; 
	/**
	 * The GameView that houses this GamePanel
	 */
	private final GameView gv;
	/**
	 * When in the process of an add Attempt, this is the GridSpace that the user moves around with their mouse
	 */
	private GridSpace currentMoving = null;
	/**
	 * When in the process of an add Attempt, and when that add Attempt is moving a previously existing GridSpace, this is the original image of the GridSpace before the user performs any rotations.
	 * It is stored so that if the user cancels the move, the image is returned in the orientation it was when it was placed
	 */
	private Image imageBeforeRotation = null;
	/**
	 * When in the process of an add Attempt, and when that add Attempt is moving a previously existing GridSpace, this corresponds to 
	 * the original location of the GridSpace before the user tried to move it. It is stored so that if the user cancels the move it goes back to 
	 * its original location.
	 */
	private Point oldPoint = null;
	/**
	 * When in the process of an add Attempt, and when the mouse is hovered over a Grid, and there is room for currentMoving where the mouse is in the hovered over grid, this is the grid that it is hovered over
	 */
	private Grid activeGrid = null;
	/**
	 * when in add attempt and the mouse has entered a grid and set its highlight, this will be set to true. It will be set back to false when the mouse leaves the grid
	 */
	private boolean setHighlight = false;
	
	/**
	 * The Image corresponding to the new wild pokemon NotificationButton
	 */
	private static final Image NOTIFICATION_LOGO = GuiUtils.getScaledImage(GuiUtils.readImage("/sprites/ui/pokeball.png"), 50, 50);
	/**
	 * The Location of the new wild pokemon NotificationButton
	 */
	private static final Point NOTIFICATION_LOCATION = new Point(627,44);
	/**
	 * the new wild pokemon NotificationButton
	 */
	private final NotificationButton notifications;
	
	/**
	 * Image for shop button
	 */
	private static final Image SHOP_BUTTON_LOGO = GuiUtils.getScaledImage(GuiUtils.readImage("/sprites/ui/pokemart.jpeg"), 50, 50);
	/**
	 * Location of the shop button
	 */
	private static final Point SHOP_BUTTON_LOCATION =  new Point(688, 44);
	
	/**
	 * Button that user can press to open up the item shop
	 */
	private final PictureButton<GameView> shopButton;
	/**
	 * Button that user can press to save an item
	 */
	private final PictureButton<GameView> saveButton;
	/**
	 * Image for the save button
	 */
	private static final Image SAVE_BUTTON_LOGO = GuiUtils.getScaledImage(GuiUtils.readImage("/sprites/ui/save_icon.png"), 50, 50);
	/**
	 * Location of the save button
	 */
	private static final Point SAVE_BUTTON_LOCATION = new Point(749,44);
	
	/**
	 * When KeyBindings should happen
	 */
	private static final int CONDITION = JComponent.WHEN_IN_FOCUSED_WINDOW; 
	/**
	 * The manager for all key stroke events in this panel
	 */
	private final KeyBindingManager keyBindings = new KeyBindingManager(getInputMap(CONDITION), getActionMap());
	/**
	 * Displays Current Board Attributes
	 */
	private final BackgroundWithText boardAttributesDisplay;
	/**
	 * The location to display the Board attributesu
	 */
	private final static Rectangle ATTRIBUTE_LABEL_LOCATION = new Rectangle(new Point(279, 458));
	/**
	 * Added to currentMoving so that clicks will trigger rotation
	 */
	private final MouseListener onClick =  new MouseAdapter() { //allow rotation
		 @Override
		 public void mouseClicked(final MouseEvent e) {
			onMouseClicked(e);
		 }
	};
	/**
	 * Added to currentMoving so that moving the mouse into the gamespace moves it instead of keeping it still
	 */
	private final MouseMotionListener onMove =  new MouseMotionAdapter() { 
		 @Override
		 public void mouseMoved(final MouseEvent e) {
			 final Point location = currentMoving.getLocation();
			currentMoving.setLocation(location.x + e.getPoint().x, location.y+e.getPoint().y);
		 }
	};
	static { //create the rectangles corresponding to the locations of all of the grids
		ATTRIBUTE_LABEL_LOCATION.add(new Point(814, 511));
		final int[] xLocations = {30,273,331,800,80,240};
		final int[] yLocations = {333,490,142,445,170,229};
		for (int i = 0; i < NUM_GRIDS; i++) {
				final Rectangle r = new Rectangle(new Point(xLocations[i*2], yLocations[i*2]));
				r.add(new Point(xLocations[1+(i*2)], yLocations[1+(i*2)]));
				gridLocs[i] = r;
				
		
		}
	}
	/**
	 * the image for the background of the panel that displays attributes
	 */
	private static final Image ATTRIBUTES_BACKGROUND_IMAGE = GuiUtils.readImage("/sprites/ui/attributelabel.png");
	/**
	 * the location of the pokecash attribute text
	 */
	private static final Point POKECASH_ATTRIBUTE_LOCATION = new Point(70, 29);
	/**
	 * the location of the popularity attribute text
	 */
	private static final Point POPULARITY_ATTRIBUTE_LOCATION = new Point(358, 29);
	private static final long serialVersionUID = 1L;
	/**
	 * Creates a new MainGamePanel
	 * @param gv the GameView that houses this panel
	 */
	//TODO: Pokemon get stuck moving around Grid when object is in the way
	
	public MainGamePanel(final GameView gv) {
		this.gv = gv;
		
		setSize(GameView.WIDTH,GameView.HEIGHT);
        setLayout(null);
		setFocusable(true);
		setOpaque(false);
		
		setUpGrids();
		addListeners();
		setKeyBindings();
		
		notifications = new NotificationButton(NOTIFICATION_LOGO, NOTIFICATION_LOCATION, gameView -> gameView.getPresenter().NotificationClicked(), gv, true).disableBorder();
		add(notifications);
		
		shopButton = ButtonBuilder.generatePictureButton("shop_button", gameView -> gameView.getPresenter().shopClicked(), gv, 50, 50);//new PictureButton<GameView>(SHOP_BUTTON_LOGO, SHOP_BUTTON_LOCATION, gameView -> gameView.getPresenter().shopClicked(), gv).disableBorder();
		shopButton.setLocation(SHOP_BUTTON_LOCATION);
		add(shopButton);
		
		saveButton = ButtonBuilder.generatePictureButton("save_button", gameView -> gameView.getPresenter().saveGame(), gv, 50 ,50);//new PictureButton<GameView>(SAVE_BUTTON_LOGO, SAVE_BUTTON_LOCATION, gameView -> gameView.getPresenter().saveGame(), gv);
		saveButton.setLocation(SAVE_BUTTON_LOCATION);
		add(saveButton);
		
		boardAttributesDisplay = new BackgroundWithText(ATTRIBUTES_BACKGROUND_IMAGE, new Point[] {POKECASH_ATTRIBUTE_LOCATION, POPULARITY_ATTRIBUTE_LOCATION});
		boardAttributesDisplay.setBounds(ATTRIBUTE_LABEL_LOCATION);
		add(boardAttributesDisplay);
		
		revalidate();
		repaint();
		setVisible(true);
	}
	/**
	 * If in the process of an add Attempt and the click was a right click, rotate the GridSpace
	 * @param e the mouse event of the user's mouse click
	 */
	private void onMouseClicked(final MouseEvent e) {
		if (DEBUG)
		System.out.println(e.getPoint());
		if (addingSomething) {
			if (SwingUtilities.isRightMouseButton(e)) {
				//save all old information that we will need
				final AddType oldType = typeOfAdd;
				final Point oldOldPoint = oldPoint;
				final Image oldImageBeforeRotation = imageBeforeRotation;
				final GridSpace oldGridSpace = endGameSpaceAdd(); //end the current add attempt
				oldGridSpace.rotateClockwise90();
				gridSpaceAdd(oldGridSpace, oldType); //restart the current add attempt with the new, rotated image
				imageBeforeRotation = oldImageBeforeRotation;
				oldPoint = oldOldPoint;
				if (activeGrid != null) { //reset the highlight
					activeGrid.setHighlight(currentMoving);
					setCurrentRelativeToActiveGrid(e);
					activeGrid.updateHighlight(e.getPoint());
					setHighlight = true;
				}
				 
			}
		}
	}
	/**
	 * Sets the location of the currentMoving GridSpace in the context of this MainGamePanel given a MouseEvent in the context of activeGrid
	 * @param e the mouse event provided by activeGrid
	 */
	private void setCurrentRelativeToActiveGrid(final MouseEvent e) {
		final Point snapPoint = activeGrid.getAbsoluteSnapPoint(e.getPoint());
		currentMoving.setLocation(snapPoint.x+activeGrid.getX(), snapPoint.y+activeGrid.getY());
	}
	/**
	 * Set the value of the notification button
	 * @param num the number of notifications
	 */
	public void updateNotifications(final int num) {
		notifications.setNumNotifications(num);
	}
	public GridSpace generateGridSpaceWithDefaultGrid(final GameSpace gs) {
		return grids[DEFAULT_GRID].generateGridSpace(gs);
	}
	/**
	 * Start a new Add Attempt
	 * @param gs the GridSpace to attempt to add
	 * @param type the type of add
	 */
	public void gridSpaceAdd(final GridSpace gs, final AddType type) {
		this.typeOfAdd = type;
		if (typeOfAdd == AddType.PRIOR_ON_BOARD)  {
			oldPoint = gs.getLocation();
			if (imageBeforeRotation == null)
				imageBeforeRotation = gs.getImage();
		}
		addingSomething = true;
		currentMoving = gs;
		currentMoving.addMouseListener(onClick);
		currentMoving.addMouseMotionListener(onMove);
		final Point p = MouseInfo.getPointerInfo().getLocation();
		if (p != null) {
			SwingUtilities.convertPointFromScreen(p, this);
			currentMoving.setLocation(p);
		}
		add(currentMoving);
		timeAddedTime = System.currentTimeMillis();
	}
	/**
	 * Stop the current Add Attempt, and undo any changes 
	 */
	public void cancelGridSpaceAdd() {
		switch(typeOfAdd) {
		case PRIOR_ON_BOARD:
			currentMoving.setImage(imageBeforeRotation);
			gridClick(currentMoving.getGrid(), oldPoint, true); //gridClick will call endGameSpaceAdd()
			break;
		default:
			gv.getPresenter().notifyAddCanceled(currentMoving, typeOfAdd);
			endGameSpaceAdd();
			break;
		}
		
		
		
	}
	/**
	 * Ends the Add Attempt. Resets all of the Add Attempt variables to a clean slate as they were before the Add Attempt. This called whenever an add is canceled or succeeds. 
	 * @return the old currentMoving GridSpace
	 */
	private GridSpace endGameSpaceAdd() {
		if (!addingSomething || currentMoving == null || typeOfAdd == null)
			throw new RuntimeException("Not currently Adding GameSpace");
		addingSomething = false;
		remove(currentMoving);
		final GridSpace oldGS = currentMoving;
		oldGS.removeMouseListener(onClick);
		oldGS.removeMouseMotionListener(onMove);
		currentMoving = null;
		setHighlight = false;
		if (activeGrid != null)
			activeGrid.removeHighlight();
		activeGrid = null;
		setHighlight = false;
		typeOfAdd = null;
		oldPoint = null;
		imageBeforeRotation = null;
		return oldGS;
	}
	/**
	 * Triggered when the grid is clicked. If in an add attempt, and there is room for 
	 * currentMoving, then add it to the grid and end the Add Attempt. Will only trigger if (byPassTime || time elapsed > MIN_WAIT_TO_ADD)
	 * @param currGrid the grid that was clicked
	 * @param p the absolute point at which it was clicked (relative to currGrid)
	 * @param byPassTime if set to true will ignore the requirement that there must be at least MIN_WAIT_TO_ADD milliseconds
	 * between starting an add attempt and adding something
	 */
	private void gridClick(final Grid currGrid, final Point p, final boolean byPassTime) {
		if (addingSomething && (byPassTime || System.currentTimeMillis()-timeAddedTime > MIN_WAIT_TO_ADD)) {
			final GridSpace result = currGrid.addGridSpaceSnapToGrid(currentMoving,p);
			if (result != null) { //if add is succesful (has room, etc.)
				gv.getPresenter().notifyAdded(result, typeOfAdd);
				currGrid.removeHighlight();
				endGameSpaceAdd();
			}
		}
	}
	/**
	 * Triggered when grid is clicked. If in an add attempt time passed > MIN_WAIT_TO_ADD and there is room for 
	 * currentMoving, then add it to the grid and end the Add Attempt.
	 * @param currGrid the grid that was clicked
	 * @param p the absolute point at which it was clicked (relative to currGrid)
	 */
	private void gridClick(final Grid currGrid, final Point p) {
		gridClick(currGrid, p, false);
	}
	/**
	 * To be added to every grid that is added to this game panel. Will trigger the gridClick function if a left click.
	 * Will also call this's onMouseClicked function so that object will rotate if its a right click.
	 * @param currGrid the grid that will contain this MouseAdapter
	 * @return the MouseAdapter
	 */
	private MouseClickWithThreshold<MainGamePanel> generateGridClickListener(final Grid currGrid) {
		final BiConsumer<MainGamePanel, MouseEvent> input = (mgp, e) -> {
			mgp.onMouseClicked(e);
			if (SwingUtilities.isLeftMouseButton(e))
				mgp.gridClick(currGrid, e.getPoint());
		};
		final MouseClickWithThreshold<MainGamePanel> mcwt = new MouseClickWithThreshold<MainGamePanel>(20, input, this, true); 
		return mcwt;
	}
	/**
	 * Update the Display to display the gold/popularity on the board
	 * @param gold The PokeCash value
	 * @param popularity the current popularity
	 */
	public void updateDisplayedAttributes(final int gold, final int popularity) {
		boardAttributesDisplay.updateText(0, Integer.toString(gold));
		boardAttributesDisplay.updateText(1, Integer.toString(popularity));
	}
	 /**
	 * Sets all key Bindings for this game panel
	 */
	private void setKeyBindings() {
	      keyBindings.addKeyBinding(KeyEvent.VK_ESCAPE, () -> {
	    	 gv.getPresenter().Canceled();
	      });
	      keyBindings.addKeyBinding(KeyEvent.VK_ENTER, () -> {
	    	  gv.getPresenter().Entered();
	      });
	      keyBindings.addKeyBinding(KeyEvent.VK_RIGHT, () -> {
	    	  //TODO: Implement
	      });
	      keyBindings.addKeyBinding(KeyEvent.VK_LEFT, () -> {
	    	  //TODO: Implement
	      });
	      keyBindings.addKeyBinding(KeyEvent.VK_I, () -> {
	    	  gv.getPresenter().displayAdvancedStats(); 
	      });
	      keyBindings.addKeyBinding(KeyEvent.VK_S, () -> {
	    	 gv.getPresenter().shopClicked(); 
	      });
	   }
	/**
	 * adds grids to board and adds their listeners
	 */
	private void setUpGrids() {
		for (int i = 0; i < NUM_GRIDS; i++) { //create the grids
			final Grid currGrid = new Grid(gridLocs[i], GRID_SPACE_DIM, GRID_SPACE_DIM, gv, i);
			currGrid.addMouseMotionListener(new MouseMotionAdapter() {
				 @Override
				 public void mouseMoved(final MouseEvent e) { //set highlights when mouse is moved
					if (addingSomething) {
						if (!setHighlight) {
							activeGrid = currGrid; //TODO: Fix this because setHighlight could be set even if currGrid doesn't if there isn't space
							currGrid.setHighlight(currentMoving);
							setHighlight = true;
						}
						/*
						 * Sets location of currentMoving. This is important in case when mouse is hovered over grid,
						 * but there isn't room so grid doesn't set highlight. We would still want the object to be visible
						 * and at the right location
						 */
						MainGamePanel.this.dispatchEvent(e); 
						currGrid.updateHighlight(e.getPoint());
					}
				}
			});
			currGrid.addMouseListener(generateGridClickListener(currGrid)); //listener for attempting to place
			currGrid.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(final MouseEvent e) { //remove highlight
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
	}
	private void addListeners() {
		this.addMouseMotionListener(new MouseMotionAdapter() { //move currentMoving around screen
			 @Override
			 public void mouseMoved(final MouseEvent e) {
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
		if (DEBUG) {
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					System.out.println(e.getPoint());
				}
			});
		}
	}
	/**
	 * Adds the gamespace to the specified grid. This is all UI (aka, doesn't notify board!)
	 * @param g the gamespace to add
	 * @param data the GridSpaceData to use to generate the GridSpace
	 * @return the newly generated grid space
	 */
	public GridSpace addSavedGridSpaceToGrid(final GameSpace g, final GridSpaceData data) {
		return grids[data.gridData.gridID].generateRotateAndAddGridSpaceFromData(g, data);
	}
	/**
	 * enables/Disables the shop/save/notification button
	 */
	public void setEnabledForButtons(final boolean enabled) {
		shopButton.setEnabled(enabled);
		notifications.setEnabled(enabled);
		saveButton.setEnabled(enabled);
	}

	

	
}

	

