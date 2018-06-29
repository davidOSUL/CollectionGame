package gui.mvpFramework;
import static gameutils.Constants.DEBUG;
import static gameutils.Constants.PRINT_BOARD;
import static gui.guiutils.GUIConstants.SHOW_CONFIRM_ON_CLOSE;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import game.Board;
import gui.displayComponents.DescriptionManager;
import gui.displayComponents.InfoWindowBuilder;
import gui.gameComponents.GameSpace;
import gui.gameComponents.Grid.GridSpace;
import gui.guiutils.GuiUtils;
import thingFramework.Pokemon;
import thingFramework.Thing;

/**
 * The "Presenter" in the MVP model. Has a view (GameView) and a model (Board). 
 * Has responsibility of updating GUI, updating the board, and taking in all inputs/actions that may have an effect on both
 * the view and model and decides what to do. 
 * @author DOSullivan
 *
 */
public class Presenter {
	/**
	 * The "Model" of this presenter. Manages the game in memory
	 */
	private volatile Board board;
	/**
	 * The "View" of this presenter. Manages the GUI
	 */
	private GameView gameView;
	/**
	 * the Consumer that is triggered when the user clicks the notification button and decides to let the pokemon go
	 */
	private final static Consumer<Presenter> LET_POKE_GO = p -> p.board.confirmGrab();
	/**
	 * When in an add Attempt the Thing that the user wants to add
	 */
	private Thing thingToAdd = null;
	/**
	 * When in a delete attempt, this is the GameSpace that the user wants to delete
	 */
	private GridSpace gridSpaceToDelete = null;
	/**
	 * Map between GridSpaces and the Things that they represent
	 */
	private Map<GridSpace, Thing> allThings = new HashMap<GridSpace, Thing>();
	/**
	 * The current state of the game. By default this is GAMEPLAY
	 */
	private CurrentState state = CurrentState.GAMEPLAY;
	/**
	 * When an JPanel is opened, this will be set to that JPanel
	 */
	private JPanel currentWindow = null;
	/**
	 * The background of the JPanel that pops up when the notification button is pressed
	 */
	private final static Image INFO_WINDOW_BACKGROUND = GuiUtils.readImage("/sprites/ui/pikabackground.jpg");
	private String oldString; //TODO: Remove this or add debug feature
	private String newString;
	private volatile boolean toolTipsEnabled = true;
	private volatile boolean saved = false;
	public Presenter() {
	};
	/**
	 * Creates a new Presenter with the provided Board and GameView
	 * @param b the Board (or "model" in MVP)
	 * @param gv the Board (or "view" in MVP)
	 */
	public Presenter(Board b, GameView gv) {
		this();
		setBoard(b);
		setGameView(gv);
		oldString = board.toString();
	}
	/**
	 * Checks if the GridSpace is present
	 * @param gs the GridSpace to check
	 * @return true if the space is present
	 */
	public boolean containsGridSpace(GridSpace gs) {
		return allThings.containsKey(gs);
	}
	/**
	 * Removes the GridSpace from the GUI and removes the thing that it corresponds to from the board
	 * @param gs the GridSpace to remove
	 * @param removeFromBoard if true will remove the thing from board, otherwise just removes it from allThings map
	 * @return the mapEntry that was removed
	 */
	private  mapEntry removeGridSpace(GridSpace gs, boolean removeFromBoard) {
		if (!allThings.containsKey(gs))
			throw new RuntimeException("Attempted To Remove Non-Existant GridSpace");
		if (removeFromBoard)
			board.removeThing(allThings.get(gs));
		allThings.get(gs);
		return new mapEntry(gs, allThings.remove(gs));
	}
	/**
	 * Updates the notification counter of the notifaction button and updates the display of the GUI
	 */
	public void updateGUI() {
		if (board == null || gameView == null)
			return;
		gameView.setWildPokemonCount(board.numPokemonWaiting());
		gameView.setBoardAttributes(board.getGold(), board.getPopularity());
		gameView.updateDisplay();
		updateToolTips();
	}
	private void updateToolTips() {
		//TODO: Make time based events have countdown timer
		if (toolTipsEnabled)
			allThings.forEach((gs, thing) -> DescriptionManager.getInstance().setDescription(gs, thing));
	}
	private void stopToolTips() {
		toolTipsEnabled = false;
		allThings.forEach((gs, thing) -> {
			DescriptionManager.getInstance().removeDescription(gs);
		});
	}
	private void resumeToolTips() {
		toolTipsEnabled = true;
	}
	/**
	 * Calls the boards update method
	 */
	public void updateBoard() {
		if (board == null || gameView == null)
			return;
		board.update();
		if (DEBUG || PRINT_BOARD) {
			newString = board.toString();
			if (!newString.equals(oldString)) {
				System.out.println("\n---GAME TIME---: "+ board.getTotalGameTime() + "\n" + board +   "\n-------");
				oldString = newString;
			}
		}
	}
	/**
	 * Sets the board of this Presenter
	 * @param b the board to set 
	 */
	public void setBoard(Board b) {
		this.board = b;
	}
	/**
	 * Sets the GameView of this Presenter
	 * @param gv the GameView to set
	 */
	public void setGameView(GameView gv) {
		this.gameView = gv;
		if (SHOW_CONFIRM_ON_CLOSE) {
			gameView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			gameView.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent windowEvent) {
					if (JOptionPane.showConfirmDialog(gameView, 
							"Are you sure to quit without saving?", "", 
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION && !saved){
								System.exit(0);
					}
				}
			});
		}
		
	}
	/**
	 * To be called whenever the notification button is clicked. Displays the PopUp JPanel with the next pokemon in the wild pokemon queue in the board
	 *@sets CurrentState.NOTIFICATION_WINDOW
	 */
	public void NotificationClicked() {
		if (!board.wildPokemonPresent() || state != CurrentState.GAMEPLAY)
			return;
		setState( CurrentState.NOTIFICATION_WINDOW);
		JPanel wildPokemonWindow = wildPokemonWindow(board.grabWildPokemon());
		setCurrentWindow(wildPokemonWindow);									
	}
	/**
	 * To be called whenever the shop button is clicked
	 */
	public void shopClicked() {
		System.out.println("SHOP CLICK");
		//TODO: implement
	}

	private void setCurrentWindow(JPanel window) {
		currentWindow = window;
		gameView.displayPanelCentered(window);
	}
	/**
	 * Called when a GridSpace is sucessfully added to the board. Adds the provided GridSpace to <GridSpace, Thing> map and adds the thing (thingToAdd) to the board if
	 * AddType == POKE_FROM_QUEUE
	 * @param gs
	 */
	private void addGridSpace(GridSpace gs, AddType type) {
		if (thingToAdd == null)
			return;
		if (type == AddType.POKE_FROM_QUEUE)
			board.addThing(thingToAdd);
		allThings.put(gs, thingToAdd);
		finishAddAttempt();
	}
	/**
	 * Sets the state and updates the tool tip manager accordingly
	 * @param state The new state of the game
	 */
	private void setState(CurrentState state) {
		if (toolTipsEnabled && state != CurrentState.GAMEPLAY) {
			stopToolTips();
		}
		if (state == CurrentState.GAMEPLAY && !toolTipsEnabled) {
			resumeToolTips();
		}
		this.state = state;


	}
	/**
	 * Finalizes the add attempt by getting rid of thingToAdd and changing the state of the game back to GAMEPLAY
	 * Will be called whether or not the GridSpace was actually added to the board
	 * @sets CurrentState.GAMEPLAY
	 */
	private void finishAddAttempt() {
		thingToAdd = null;
		setState(CurrentState.GAMEPLAY);
	}
	/**
	 * To be called when the provided GridSpace is succesfully added  to the board
	 * @param gs the GridSpace that was added
	 * @param type the type of add (from queue, moving, etc.)
	 */
	public void notifyAdded(GridSpace gs, AddType type) {
		addGridSpace(gs, type);
		if (type == AddType.POKE_FROM_QUEUE)
			board.confirmGrab();
	}

	/**
	 * To be called when the provided GridSpace was being added, but the user decided to cancel the add (hit escape).
	 * If this was a AddType.POKE_FROM_QUEUE, will place the pokemon back in the queue
	 * @param gs the GridSpace that was being added 
	 * @param type
	 */
	public void notifyAddCanceled(GridSpace gs, AddType type) {
		if (type == AddType.POKE_FROM_QUEUE)
		{
			board.undoGrab();
		}
		finishAddAttempt();
	}
	/**
	 * To be called when the user initializes an add Attempt with a Thing that doesn't yet have a created GridSpace
	 * @param t The Thing that the user wants to add
	 * @param type the context of the add\
	 * @sets CurrentState.PLACING_SPACE
	 */
	public void attemptAddThing(Thing t, AddType type) {
		GameSpace gs = new GameSpace(GuiUtils.readAndTrimImage(t.getImage()), t.getName());
		setState(CurrentState.PLACING_SPACE);
		thingToAdd = t;
		gameView.attemptNewGridSpaceAdd(gs, type);
	}
	/**
	 * To be called when the user initalizes an add Attempt for a GridSpace that has already been created
	 * @param entry the mapping between the GridSpace and the Thing
	 * @param type the context of the add
	 * @sets CurrentState.PLACING_SPACE
	 */
	private void attemptAddExistingThing(mapEntry entry, AddType type) {
		setState(CurrentState.PLACING_SPACE);
		thingToAdd = entry.thing;
		gameView.attemptExistingGridSpaceAdd(entry.gridSpace, type);
	}
	/**
	 * To be called when the user attempts to move a GridSpace.
	 * @param gs the GridSpace that the user wants to move
	 * @return false if the user is not allowed to move the GridSpace (they are currently in the Notification Window for example)
	 *@sets CurrentState.PLACING_SPACE if doesn't return false, otherwise keeps state the same
	 */
	public boolean attemptMoveGridSpace(GridSpace gs) {
		if (!containsGridSpace(gs))
			throw new IllegalArgumentException("GridSpace " + gs + "Not found on board");
		if (state != CurrentState.GAMEPLAY)
			return false;
		setState(CurrentState.PLACING_SPACE);
		gs.removeFromGrid();
		mapEntry entry = removeGridSpace(gs, false);
		attemptAddExistingThing(entry, AddType.PRIOR_ON_BOARD);
		return true;
	}
	/**
	 * To be called when the user attempts to delete a GridSpace
	 * @param gs the GridSpace that the user wants to delete
	 * @return false if the user is not allowed to delete the GridSpace (they are currently in the Notification Window for example)
	 *@sets CurrentState.DELETE_CONFIRM_WINDOW if doesn't return false, otherwise keeps state the same
	 */
	public boolean attemptDeleteGridSpace(GridSpace gs) {
		if (!containsGridSpace(gs))
			throw new IllegalArgumentException("GridSpace " + gs + "Not found on board");
		if (state != CurrentState.GAMEPLAY)
			return false;
		setState(CurrentState.DELETE_CONFIRM_WINDOW);
		Thing thingToDelete = allThings.get(gs);
		JPanel deleteWindow = attemptToDeleteWindow(thingToDelete);
		setCurrentWindow(deleteWindow);
		gridSpaceToDelete = gs;
		return true;

	}
	private void confirmDelete() {
		if (state != CurrentState.DELETE_CONFIRM_WINDOW || gridSpaceToDelete == null) 
			throw new RuntimeException("No Delete to Confirm");
		this.removeGridSpace(gridSpaceToDelete, true);
		gridSpaceToDelete.removeFromGrid();
		finishDeleteAttempt();
	}
	/**
	 * @sets CurrentState.GAMEPLAY
	 */
	private void finishDeleteAttempt() {
		gridSpaceToDelete = null;
		setState(CurrentState.GAMEPLAY);
	}
	/**
	 * To be called when the User Clicks the notification button and then clicks cancel. Undos the board grab, and sets the state of the game back to GamePlay
	 * @sets CurrentState.GAMEPLAY
	 */
	private void undoNotificationClicked() {
		board.undoGrab();
		setState(CurrentState.GAMEPLAY);
	}
	/**
	 * To be called when the currentWindow's enter button is pressed
	 */
	public void Entered() {
		switch(state) {
		case NOTIFICATION_WINDOW:
			attemptAddThing(board.getGrabbed(), AddType.POKE_FROM_QUEUE);	
			break;
		case DELETE_CONFIRM_WINDOW:
			confirmDelete();
			break;
		}
	}
	/**
	 * To be called when the currentWindow's cancel button is pressed
	 */
	public void Canceled() {
		switch(state) {
		case NOTIFICATION_WINDOW:
			undoNotificationClicked();
			break;
		case DELETE_CONFIRM_WINDOW:
			finishDeleteAttempt();
			break;
		}

	}
	/**
	 * Gets rid of the currentWindow
	 */
	public void CleanUp() {
		gameView.removeDisplay(currentWindow);
		currentWindow = null;	
	}
	/**
	 * to be called when the currentWindow is no longer being used
	 * @sets CurrentState.GAMEPLAY
	 */
	public void Finish() {
		setState(CurrentState.GAMEPLAY);
	}
	/**
	 * Generates a new JPanel corresponding to the next pokemon in the queue, and giving the user the option to add it, set it free or cancel the request and place it back in the queue
	 * @param p the next pokemon in the queue
	 * @return the JPanel
	 */
	private JPanel wildPokemonWindow(Pokemon p) {
		return  new InfoWindowBuilder()
				.setPresenter(this)
				.setInfo("A wild " + p.getName() + " appeared!")
				.setThing(p)
				.addEnterButton("Place")
				.addButton("Set Free", LET_POKE_GO, true, false, true)
				.addCancelButton()
				.setBackgroundImage(INFO_WINDOW_BACKGROUND)
				.createWindow();

	}
	/**
	 * Generates a new JPanel confirming if the user actually wants to delete the passed in thing
	 * @param t The thing that the user needs to confirm deletion of
	 * @return the created JPanel
	 */
	private JPanel attemptToDeleteWindow(Thing t) {
		return new InfoWindowBuilder()
				.setPresenter(this)
				.setInfo("Are you sure you want to set " + t.getName() + " free?")
				.setThing(t)
				.addEnterButton("Yes")
				.addCancelButton()
				.setBackgroundImage(INFO_WINDOW_BACKGROUND)
				.createWindow();

	}
	/**
	 * The CurrentState of GamePlay
	 * @author DOSullivan
	 *
	 */
	private enum CurrentState {
		GAMEPLAY, NOTIFICATION_WINDOW, PLACING_SPACE, DELETE_CONFIRM_WINDOW
	}
	/**
	 * The context of the Add Attempt
	 * POKE_FROM_QUEUE == notifcation butotn was pressed
	 * PRIOR_ON_BOARD == moving around a thing that was already placed
	 * @author DOSullivan
	 *
	 */
	public enum AddType{
		POKE_FROM_QUEUE, PRIOR_ON_BOARD
	}
	/**
	 * A mapping between a thing and a GridSpace
	 * @author DOSullivan
	 *
	 */
	private static class mapEntry{
		public Thing thing;
		public GridSpace gridSpace;
		public mapEntry(GridSpace gs, Thing t) {
			gridSpace = gs;
			thing = t;
		}
	}

}
