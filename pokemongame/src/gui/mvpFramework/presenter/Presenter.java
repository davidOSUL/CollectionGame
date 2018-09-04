package gui.mvpFramework.presenter;
import static gameutils.Constants.DEBUG;
import static gameutils.Constants.PRINT_BOARD;
import static gui.guiutils.GUIConstants.SHOW_CONFIRM_ON_CLOSE;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import attributes.ParseType;
import game.Board;
import gui.displayComponents.DescriptionManager;
import gui.displayComponents.ShopWindow;
import gui.gameComponents.GameSpace;
import gui.gameComponents.grid.GridSpace;
import gui.gameComponents.grid.GridSpace.GridSpaceData;
import gui.guiutils.GuiUtils;
import gui.mvpFramework.GameView;
import loaders.shopLoader.ShopItem;
import thingFramework.Thing;
import userIO.GameSaver;

/**
 * The "Presenter" in the MVP model. Has a view (GameView) and a model (Board). 
 * Has responsibility of updating GUI, updating the board, and taking in all inputs/actions that may have an effect on both
 * the view and model and decides what to do. 
 * @author David O'Sullivan
 *
 */
public class Presenter implements Serializable {


	/*
	 * Static variables:
	 * 
	 */


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * the Consumer that is triggered when the user clicks the notification button and decides to let the creature go
	 */
	private final static Consumer<Presenter> LET_CREATURE_GO = p -> p.board.confirmGrab();	
	


	/*
	 * Transient Instance variables:
	 * 
	 * 
	 */

	/**
	 * The "View" of this presenter. Manages the GUI. This is made transient because want to restore the GUI manually. 
	 */
	private transient GameView gameView;
	/**
	 * The shopWindow that appears when the user clicks it. This is made transient because want to restore the GUI manually. 
	 */
	private transient ShopWindow shopWindow;
	/**
	 * When an JPanel is opened, this will be set to that JPanel
	 */
	private transient JComponent currentWindow = null;

	private transient String oldString;  //used for debugging only, represent board.toString(), print when that changes
	private transient String newString; //used for debugging only,  represent board.toString(), print when that changes
	/**
	 * Map of all things that are on board and were sold via the shop. This is made transient to manually set GUI.
	 * Note that although each gridspace will be unique multiple gridspaces can map to same shop item (if the item is purchased
	 * more than once for instance)
	 */
	private transient Map<GridSpace, ShopItem> soldThings; 
	/**
	 * Map between GridSpaces and the Things that they represent. This is made transient to manually set GUI
	 */
	private transient Map<GridSpace, Thing> allThings;
	/**
	 * When in an add Attempt the Thing that the user wants to add
	 */
	private transient Thing thingToAdd = null;
	/**
	 * When attempting to purchase an item this is ShopItem the user wants to purchase one thing from 
	 */
	private transient ShopItem itemToPurchase = null;
	/**
	 * When attempting to sell back an item, the SHopItem that the user wants to sell back
	 */
	private transient ShopItem itemToSellBack = null;
	/**
	 * When in a delete attempt, this is the GameSpace that the user wants to delete
	 */
	private transient GridSpace gridSpaceToDelete = null;
	/*
	 * Non-transient Instance Variables:
	 * 
	 * 
	 */


	/**
	 * The Saver to use to save the game
	 */
	private final CurrentGamestateSaver gameStateSaver;
	/**
	 * The "Model" of this presenter. Manages the game in memory
	 */
	private Board board;

	/**
	 * The current state of the game. By default this is GAMEPLAY
	 */
	private CurrentState state = CurrentState.GAMEPLAY;

	private final PresenterWindowFactory windowFactory;
	private volatile boolean toolTipsEnabled = true;
	private volatile boolean popupMenusEnabled = true;
	private final String title;
	private String goodbyeMessage = "Goodbye!";
	private final Queue<GridSpace> toBeDeleted = new ConcurrentLinkedQueue<GridSpace>();
	private boolean suggestShopUpdate = false;
	private int amountOfLastGold = -1;
	
	

	/**
	 * Get the GameView associated with this Presenter
	 * @return the GameView that this presenter has
	 */
	public JFrame getGameView() {
		return gameView;
	}
	/**
	 * Creates a new Presenter with the provided Board and GameView
	 * @param b the Board (or "model" in MVP)
	 * @param gameViewTitle the Title of the gameview (or "view" in MVP)
	 * @param gameSaver the gamesaver to use for saving
	 */
	public Presenter(final Board b, final String gameViewTitle, final GameSaver gameSaver) {
		this.gameStateSaver = new CurrentGamestateSaver(gameSaver);
		allThings = new HashMap<GridSpace, Thing>();
		soldThings = new HashMap<GridSpace, ShopItem>();
		title = gameViewTitle;
		setBoard(b);
		setGameView(gameViewTitle);
		windowFactory = new PresenterWindowFactory(this, board);

	}
	/**
	 * save the current game
	 * @return true if was able to save
	 */
	public boolean saveGame() {
		if (state == CurrentState.PLACING_SPACE) {
			JOptionPane.showMessageDialog(gameView, "Sorry! You can't save while holding onto something!");
			return false;
		}
		return gameStateSaver.saveGame(this, gameView);
	}
	/**
	 * Sets the board for this presenter
	 * @param b the board to set
	 */
	public void setBoard(final Board b) {
		this.board = b;
		oldString = b.toString();
	}
	/**
	 * Checks if the GridSpace is present
	 * @param gs the GridSpace to check
	 * @return true if the space is present
	 */
	public boolean containsGridSpace(final GridSpace gs) {
		return allThings.containsKey(gs);
	}

	/**
	 * Removes the GridSpace from the GUI and removes the thing that it corresponds to from the board. Also removes it
	 * from allThings.
	 * @param gs the GridSpace to remove
	 * @param removeFromBoard if true will remove the thing from board, otherwise just removes it from allThings map/soldThings map
	 * @return the mapEntry that was removed
	 */
	private AllThingsMapEntry removeGridSpace(final GridSpace gs, final boolean removeFromBoard) {
		if (!allThings.containsKey(gs))
			throw new RuntimeException("Attempted To Remove Non-Existant GridSpace");
		if (removeFromBoard)
			board.removeThing(allThings.get(gs));
		return new AllThingsMapEntry(gs, allThings.remove(gs));
	}
	/**
	 * Updates the notification counter of the notifaction button and updates the display of the GUI
	 */
	public void updateGUI() {
		if (board == null || gameView == null)
			return;
		gameView.setWildCreatureCount(board.numCreaturesWaiting());
		if (board.getGold() != amountOfLastGold) {
			amountOfLastGold = board.getGold();
			if (state == CurrentState.IN_SHOP)
				updateShop();
			else
				suggestShopUpdate();
		}
		gameView.setBoardAttributes(board.getGold(), board.getPopularity());
		gameView.updateDisplay();
		if (state != CurrentState.PLACING_SPACE && !toBeDeleted.isEmpty()) {
		
			deleteGridSpace(toBeDeleted.poll());
		}
		
	}
	private void updateToolTips() {
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
		if (board.hasRemoveRequest()) {
			final Thing toRemove = board.getNextRemoveRequest();
			SwingUtilities.invokeLater(() -> {
				final GridSpace[] toDelete = new GridSpace[1];
				allThings.forEach((gs, t) -> {
					if (t == toRemove)
						toDelete[0] = gs;
				});
				toDelete[0].removeListeners();
				toBeDeleted.add(toDelete[0]);
			});	
		}
		if (PRINT_BOARD) {
			newString = board.toString();
			if (!newString.equals(oldString)) {
				System.out.println("\n---IN GAME TIME---: "+ board.getTotalInGameTime() + "\n" + board +   "\n-------");
				oldString = newString;
				System.out.println("\n----Time Stats--- \n" + board.getTimeStats() + "\n-----");
			}
		}
	}

	/**
	 * Sets the GameView of this Presenter, initializes shopWindow
	 * @param title the Title of the GameView
	 * @param gv the GameView to set
	 */
	public void setGameView(final String title) {
		this.gameView = new GameView(title);
		gameView.setPresenter(this);
		shopWindow = new ShopWindow(gameView);
		shopWindow.updateItems(board.getItemsInShop());
		if (SHOW_CONFIRM_ON_CLOSE) {
			gameView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			gameView.addWindowListener(new WindowAdapter() { //TODO: Move?
				Object[] options = {"Save And Quit", "Quit Without Saving", "Cancel"};
				@Override
				public void windowClosing(final WindowEvent windowEvent) {
					stopToolTips();
					final int n = JOptionPane.showOptionDialog(gameView, goodbyeMessage, "", 
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					resumeToolTips();
					if (n == JOptionPane.CANCEL_OPTION || n == JOptionPane.CLOSED_OPTION)
						return;
					if (n == JOptionPane.YES_OPTION) {
						final boolean saveSuccess = saveGame();
						if (!saveSuccess)
							return;
					}
					System.exit(0);
				}
			});
		}
		final Timer timer = new Timer(100, e -> updateToolTips());
		timer.setRepeats(true);
		timer.setInitialDelay(0);
		timer.start();

	}
	/**
	 * To be called whenever the notification button is clicked. Displays the PopUp JPanel with the next Creature in the wild Creature queue in the board
	 *@sets CurrentState.NOTIFICATION_WINDOW
	 */
	public void NotificationClicked() {
		if (!board.wildCreaturePresent() || state != CurrentState.GAMEPLAY)
			return;
		setState( CurrentState.NOTIFICATION_WINDOW);
		setCurrentWindow(windowFactory.wildCreatureWindow(board.grabWildCreature(), LET_CREATURE_GO));									
	}


	/**
	 * Sets the current window to the passed in window, and removes the currentWindow if any
	 * @param window the window to display
	 */
	private void setCurrentWindow(final JComponent window) {
		if (currentWindow != null)
			gameView.removeDisplay(currentWindow);
		currentWindow = window;
		gameView.displayComponentCentered(window);
	}

	private void stopPopupMenus() {
		popupMenusEnabled = false;
		allThings.forEach((gs, t) -> gs.removeListeners());
	}
	private void resumePopupMenus() {
		popupMenusEnabled = true;
		allThings.forEach((gs, t) -> updateListener(gs));
	}
	private void updateListener(final GridSpace gs) {
		int val = 0;
		if (soldThings.containsKey(gs))
			val = board.getSellBackValue(soldThings.get(gs));
		gs.updateListeners(soldThings.containsKey(gs), !isNotRemovable(gs), val > 0 || board.getGold() >= Math.abs(val));
	}
	private boolean isNotRemovable(final GridSpace gs) {
		return allThings.get(gs).containsAttribute("removable") && (!allThings.get(gs).getAttributeValue("removable", ParseType.BOOLEAN));
	}
	/**
	 * Sets the state and updates the tool tip manager accordingly
	 * @param state The new state of the game
	 */
	private void setState(final CurrentState state) {
		DescriptionManager.getInstance().flashTooltips(); //"flashes" the tooltips to prevent them from getting stuck when the state changes
		if (state != CurrentState.GAMEPLAY) {
			if (state == CurrentState.ADVANCED_STATS_WINDOW || state == CurrentState.PLACING_SPACE)
				DescriptionManager.getInstance().setEnabled(false);
			if (toolTipsEnabled)
				stopToolTips();
			if (popupMenusEnabled)
				stopPopupMenus();
			gameView.setEnabledForButtons(false);

		}
		else if (state == CurrentState.GAMEPLAY) {
			DescriptionManager.getInstance().setEnabled(true);
			if (!toolTipsEnabled)
				resumeToolTips();
			if (!popupMenusEnabled)
				resumePopupMenus();
			gameView.setEnabledForButtons(true);
		}
		this.state = state;


	}
	
	/**
	 * Get the text that should display in the popup menu for this GridSpace on the button that allows the user
	 * to delete a GridSpace
	 * @param gs the GridSpace to get the discard text for
	 * @return the discard text
	 */
	public String getDiscardText(final GameSpace gs) {
		return allThings.get(gs).getDiscardText();
	}
	/**
	 * Finalizes the add attempt by getting rid of thingToAdd, itemToPurchase, and changing the state of the game back to GAMEPLAY
	 * Will be called whether or not the GridSpace was actually added to the board
	 * @sets CurrentState.GAMEPLAY
	 */
	private void finishAddAttempt() {
		thingToAdd = null;
		itemToPurchase = null;
		setState(CurrentState.GAMEPLAY);
	}
	/**
	 * To be called when the provided GridSpace is succesfully added  to the board.
	 * @param gs the GridSpace that was added
	 * @param type the type of add (from queue, moving, etc.)
	 */
	public void notifyAdded(final GridSpace gs, final AddType type) {
		if (toBeDeleted.contains(gs)) {
			toBeDeleted.remove(gs);
			deleteGridSpace(gs);
			finishAddAttempt();
			return;
		}
		switch(type) {
		case POKE_FROM_QUEUE:
			board.confirmGrab();
			break;
		case ITEM_FROM_SHOP:
			soldThings.put(gs, itemToPurchase);
			thingToAdd = board.confirmPurchase();
			updateShop();
			break;
		default:
			break;
		}
		if (type.isNewThing)
			addGridSpace(gs, type);
		updateListener(gs);
		finishAddAttempt();
	}
	/**
	 * Adds the provided GridSpace to <GridSpace, Thing> map and also adds the thing (thingToAdd) to the board.
	 * @param gs
	 */
	private void addGridSpace(final GridSpace gs, final AddType type) {
		if (thingToAdd == null)
			return;
		board.addThing(thingToAdd);
		allThings.put(gs, thingToAdd);		
	}

	/**
	 * To be called when the provided GridSpace was being added, but the user decided to cancel the add (hit escape).
	 * If this was a AddType.POKE_FROM_QUEUE, will place the Creature back in the queue. This is NOT called when a move is canceled, as there is
	 * no need to update the game state
	 * @param gs the GridSpace that was being added 
	 * @param type
	 */
	public void notifyAddCanceled(final GridSpace gs, final AddType type) {
		switch (type) {
		case POKE_FROM_QUEUE:
			board.undoGrab();
			break;
		case ITEM_FROM_SHOP:
			board.cancelPurchase();
			break;
		default:
			break;
		}
		finishAddAttempt();
	}
	/**
	 * Creates a new game space with the name and image of the specified thing
	 * @param t the Thing to create a gamespace with
	 * @return the new GameSpace
	 */
	private GameSpace generateGameSpaceFromThing(final Thing t) {
		Image i = GuiUtils.readAndTrimImage(t.getImage());
		if (t.getName().equals("Small Table"))
			i = GuiUtils.getScaledImage(i, 40, 40);
		final GameSpace gs = new GameSpace(i, t.getName());
		return gs;
	}
	/**
	 * To be called when the user initializes an add Attempt with a Thing that doesn't yet have a created GridSpace
	 * @param t The Thing that the user wants to add
	 * @param type the context of the add\
	 * @sets CurrentState.PLACING_SPACE
	 */
	public void attemptAddThing(final Thing t, final AddType type) {
		setState(CurrentState.PLACING_SPACE);
		thingToAdd = t;
		gameView.attemptNewGridSpaceAdd(generateGameSpaceFromThing(t), type);
	}
	/**
	 * To be called when the user initalizes an add Attempt for a GridSpace that has already been created
	 * @param entry the mapping between the GridSpace and the Thing
	 * @param type the context of the add
	 * @sets CurrentState.PLACING_SPACE
	 */
	private void attemptAddExistingThing(final AllThingsMapEntry entry, final AddType type) {
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
	public boolean attemptMoveGridSpace(final GridSpace gs) {
		if (!containsGridSpace(gs))
			throw new IllegalArgumentException("GridSpace " + gs + "Not found on board");
		if (state != CurrentState.GAMEPLAY)
			return false;
		setState(CurrentState.PLACING_SPACE);
		gs.removeFromGrid();
		final AllThingsMapEntry entry = new AllThingsMapEntry(gs, allThings.get(gs));
		attemptAddExistingThing(entry, AddType.PRIOR_ON_BOARD);
		return true;
	}
	/**
	 * To be called when the user attempts to delete a GridSpace
	 * @param gs the GridSpace that the user wants to delete
	 * @return false if the user is not allowed to delete the GridSpace (they are currently in the Notification Window for example)
	 *@sets CurrentState.DELETE_CONFIRM_WINDOW if doesn't return false, otherwise keeps state the same
	 */
	public boolean attemptDeleteGridSpace(final GridSpace gs) {
		if (!containsGridSpace(gs))
			throw new IllegalArgumentException("GridSpace " + gs + "Not found on board");
		if (state != CurrentState.GAMEPLAY)
			return false;
		setState(CurrentState.DELETE_CONFIRM_WINDOW);
		final Thing thingToDelete = allThings.get(gs);
		setCurrentWindow(windowFactory.attemptToDeleteWindow(thingToDelete));
		gridSpaceToDelete = gs;
		return true;

	}
	private void deleteGridSpace(final GridSpace gs) {
		/*
		 * Note how this method is different from confirmSellBack in that it conditionally sends items back to the board,
		 * and doesn't refund money if it does
		 */
		if (soldThings.containsKey(gs)) {
			final ShopItem item = soldThings.remove(gs);
			if (item.shouldSendBackToShopWhenRemoved())
				board.sendItemBackToShop(item);
		}
		this.removeGridSpace(gs, true);
		gs.removeFromGrid();
		updateShop();
	}
	private void confirmDelete() {
		if (state != CurrentState.DELETE_CONFIRM_WINDOW || gridSpaceToDelete == null) 
			throw new RuntimeException("No Delete to Confirm");
		deleteGridSpace(gridSpaceToDelete);
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
	 * To be called when the user attempts to sell back a GridSpace. Essentially a modified delete attempt that also gives user
	 * back money
	 * @param gs the GridSpace that the user wants to sell back
	 * @return false if the user is not allowed to sell back the GridSpace (they are currently in the Notification Window for example)
	 *@sets CurrentState.SELL_BACK_CONFIRM_WINDOW if doesn't return false, otherwise keeps state the same
	 */
	public boolean attemptSellBackGridSpace(final GridSpace gs) {
		if (!containsGridSpace(gs))
			throw new IllegalArgumentException("GridSpace " + gs + "Not found on board");
		if (!soldThings.containsKey(gs))
			throw new IllegalArgumentException("GridSpace " + gs + "Not a sold item");
		if (state != CurrentState.GAMEPLAY)
			return false;
		setState(CurrentState.SELL_BACK_CONFIRM_WINDOW);
		itemToSellBack = soldThings.get(gs);
		setCurrentWindow(windowFactory.attemptToSellBackWindow(gs, itemToSellBack));
		gridSpaceToDelete = gs;
		return true;

	}
	private void confirmSellBack() {
		if (state != CurrentState.SELL_BACK_CONFIRM_WINDOW || gridSpaceToDelete == null || itemToSellBack == null) 
			throw new RuntimeException("No Delete to Confirm");
		board.sellBack(itemToSellBack);
		soldThings.remove(gridSpaceToDelete);
		this.removeGridSpace(gridSpaceToDelete, true);
		gridSpaceToDelete.removeFromGrid();
		updateShop();
		finishSellBackAttempt();
	}
	/**
	 * @sets CurrentState.GAMEPLAY
	 */
	private void finishSellBackAttempt() {
		itemToSellBack = null;
		finishDeleteAttempt();

	}
	/**
	 * Returns the amount of money that the Thing that the provided GridSpace represents could be sold back to the shop for
	 * @param gs the GridSpace to get the sell back value for
	 * @return the amount of money that the Thing that the provided GridSpace represents could be sold back to the shop for
	 */
	public int getGridSpaceSellBackValue(final GridSpace gs) {
		return board.getSellBackValue(soldThings.get(gs));
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
	 * To be called whenever the shop button is clicked
	 *@sets CurrentState.IN_SHOP
	 */
	public void shopClicked() {
		if (state != CurrentState.GAMEPLAY)
			return;
		if (suggestShopUpdate) {
			updateShop();
			suggestShopUpdate = false;
		}
		setState(CurrentState.IN_SHOP);
		final JComponent cards = shopWindow.getShopWindowAsCardLayout();
		setCurrentWindow(cards);


	}
	/**
	 * To be called when the user attempts to purchase a ShopItem from the SHop
	 * @param item the ShopItem that the user is attempting to purchase
	 */
	public void notifyAttemptPurchaseThing(final ShopItem item) {
		if (!board.canPurchase(item) || !item.allowedToPlaceAnother(numOfShopItemOnBoard(item)))
			return;
		closeShop();
		setState(CurrentState.PURCHASE_CONFIRM_WINDOW);
		setCurrentWindow(windowFactory.confirmPurchaseWindow(item));
		itemToPurchase = item;
	}
	/**
	 * Starts the purchase of the item. Will not subtract money till placed (so that if the user cancels, nothing happens)
	 */
	public void confirmWantToPurchase() {
		final Thing thing = board.startPurchase(itemToPurchase);
		attemptAddThing(thing, AddType.ITEM_FROM_SHOP);
	}
	/**
	 * Close the shop window
	 */
	public void closeShop() {
		if (state != CurrentState.IN_SHOP)
			throw new RuntimeException("not in shop!");
		setState(CurrentState.GAMEPLAY);
	}
	/**
	 * update the shop window
	 */
	private void updateShop() {
		SwingUtilities.invokeLater(() -> {
			shopWindow.updateItems(board.getItemsInShop());
			if (state == CurrentState.IN_SHOP)
				refreshShop();
		});
	}
	/**
	 * Refresh the shop window
	 */
	private void refreshShop() {
		final JComponent cards = shopWindow.getShopWindowAtCurrentLocation();
		setCurrentWindow(cards);
	}
	/**
	 * Inform that the next time the shop is clicked, it should update beforehand
	 */
	private void suggestShopUpdate() {
		suggestShopUpdate = true;
	}
	/**
	 * Returns the number of ShopItems on the board that are the provided ShopItem
	 * @param item the ShopItem
	 * @return the number of that ShopItem currently on board
	 */
	public int numOfShopItemOnBoard(final ShopItem item) {
		int count = 0;
		for (final Map.Entry<GridSpace, ShopItem> entry : soldThings.entrySet()) {
			if (entry.getValue() == item)
				count++;
		}
		return count;

	}
	/**
	 * To be called when the currentWindow's enter button is pressed
	 */
	public void Entered() {
		switch(state) {
		case NOTIFICATION_WINDOW:
			CleanUp();
			attemptAddThing(board.getGrabbed(), AddType.POKE_FROM_QUEUE);	
			break;
		case DELETE_CONFIRM_WINDOW:
			CleanUp();
			confirmDelete();
			break;
		case PURCHASE_CONFIRM_WINDOW:
			CleanUp();
			confirmWantToPurchase();
			break;
		case SELL_BACK_CONFIRM_WINDOW:
			CleanUp();
			confirmSellBack();
			break;
		default:
			break;
		}
	}
	/**
	 * To be called when the currentWindow's cancel button is pressed, or the user presses escape at any time
	 */
	public void Canceled() {
		switch(state) {
		case NOTIFICATION_WINDOW:
			CleanUp();
			undoNotificationClicked();
			break;
		case DELETE_CONFIRM_WINDOW:
			CleanUp();
			finishDeleteAttempt();
			break;
		case PURCHASE_CONFIRM_WINDOW:
			CleanUp();
			setState(CurrentState.GAMEPLAY);
			shopClicked();
			break;
		case SELL_BACK_CONFIRM_WINDOW:
			CleanUp();
			finishSellBackAttempt();
			break;
		case IN_SHOP:
			CleanUp();
			closeShop();
			break;
		case PLACING_SPACE:
			gameView.cancelGridSpaceAdd();
			break;
		case ADVANCED_STATS_WINDOW:
			CleanUp();
			Finish();
			break;
		case GAMEPLAY:
			break;
		default:
			break;
		}

	}
	/**
	 * Gets rid of the currentWindow
	 */
	public void CleanUp() {
		if (currentWindow != null)
			gameView.removeDisplay(currentWindow);
		currentWindow = null;	
	}
	/**
	 * to be called when the currentWindow is no longer being used. As of right now, Only called by custom buttons, not by default enter/cancel buttons
	 * @sets CurrentState.GAMEPLAY
	 */
	public void Finish() {
		setState(CurrentState.GAMEPLAY);
	}
	
	/**
	 * Displays the advanced stat window for this board
	 */
	public void displayAdvancedStats() {
		if (state != CurrentState.GAMEPLAY)
			return;
		setState(CurrentState.ADVANCED_STATS_WINDOW);
		setCurrentWindow(windowFactory.advancedStatsWindow());
	}
	/**
	 * A mapping between a thing and a GridSpace
	 * @author David O'Sullivan
	 *
	 */
	private static class AllThingsMapEntry{
		public Thing thing;
		public GridSpace gridSpace;
		public AllThingsMapEntry(final GridSpace gs, final Thing t) {
			gridSpace = gs;
			thing = t;
		}
	}

	/**
	 * To be called when a GridSpace is right clicked. Disables tooltips
	 */
	public void notifyRightClickedGridSpace() {
		DescriptionManager.getInstance().setEnabled(false);
	}
	/**
	 * To be called when a GridSpace is no longer being right clicked. Enables tooltips.
	 */
	public void notifyDoneWithRightClick() {
		DescriptionManager.getInstance().setEnabled(true);

	}
	/**
	 * Returns true if the provided ShopItem should be greyed out
	 * @param item the shop item
	 * @return true if the shop Item should be greyed out
	 */
	public boolean shouldGreyOut(final ShopItem item) {
		return (!item.allowedToPlaceAnother(numOfShopItemOnBoard(item)) || !board.canPurchase(item));
	}
	/**
	 * returns the String that should be displayed on the popup menu on the button to sell back this GridSpace
	 * @param gs the gridspace of interest
	 * @return the string that should be displayed to sell this gridspace back
	 */
	public String getSellBackString(final GridSpace gs) {
		final int sellBackValue = getGridSpaceSellBackValue(gs);
		final String sellBackString;
		if (sellBackValue >= 0) {
			sellBackString = "Sell " + gs.getName() + "\nback for " + GuiUtils.getMoneySymbol() + sellBackValue
					;
		}
		else {
			sellBackString = "Pay " + GuiUtils.getMoneySymbol() + Math.abs(sellBackValue) +  " to return\n" + gs.getName() + " to the shop";
		}
		return sellBackString;
	}
	@SuppressWarnings("unused")
	private void writeObject(final ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		final Map<GridSpaceData, Thing> allThingsData = new LinkedHashMap<GridSpaceData, Thing>();
		final Map<Integer, ShopItem> soldThingsData = new LinkedHashMap<Integer, ShopItem>();
		final Iterator<Entry<GridSpace, Thing>> it = allThings.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			final Map.Entry<GridSpace, Thing> pair = it.next();
			final GridSpace gs = pair.getKey();
			final Thing t = pair.getValue();
			if (soldThings.containsKey(gs)) {
				soldThingsData.put(i, soldThings.get(gs));
			}
			allThingsData.put(gs.getData(), t);
			i++;
		}
		oos.writeObject(allThingsData);
		oos.writeObject(soldThingsData);
		if (DEBUG || PRINT_BOARD)
			System.out.println(board.getTimeStats());
	}

	/**
	 * Read from save. Will also call board.onStartUp()
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject(); //will read in board, title, allThings, soldThings
		allThings = new HashMap<GridSpace, Thing>();
		soldThings = new HashMap<GridSpace, ShopItem>();
		final Map<GridSpaceData, Thing> allThingsData = (Map<GridSpaceData, Thing>) ois.readObject();
		final Map<Integer, ShopItem> soldThingsData = (Map<Integer, ShopItem>) ois.readObject();
		setGameView(title); //set up the game view as well as the shopwindow
		final Iterator<Entry<GridSpaceData, Thing>> it = allThingsData.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			final Map.Entry<GridSpaceData, Thing> pair = it.next();
			final GridSpaceData data = pair.getKey();
			final Thing thing = pair.getValue();
			it.remove(); 
			final GridSpace gridSpace = gameView.addNewGridSpaceFromSave(generateGameSpaceFromThing(thing), data);
			allThings.put(gridSpace, thing);
			if (soldThingsData.containsKey(i))
				soldThings.put(gridSpace, soldThingsData.get(i));
			updateListener(gridSpace);
			i++;
		}
		board.onStartUp();
		shopWindow.updateItems(board.getItemsInShop());
		goodbyeMessage = new GoodbyeMessageCreator().getMessage();
		switch(state) {
		case DELETE_CONFIRM_WINDOW:
		case NOTIFICATION_WINDOW: 
		case SELL_BACK_CONFIRM_WINDOW:
		case ADVANCED_STATS_WINDOW:
			Canceled();
			break;
		case PURCHASE_CONFIRM_WINDOW:
			Canceled(); //close purchase confirm
			Canceled(); //close shop
			break;
		case PLACING_SPACE:
			GuiUtils.displayError(new IllegalStateException("Should not have been able to save while placing"), gameView);
			break;
		default:
			setState(CurrentState.GAMEPLAY);
			break;
		}
		if (DEBUG || PRINT_BOARD)
			System.out.println(board.getTimeStats());

	}

}
