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
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import attributes.ParseType;
import gui.displayComponents.DescriptionManager;
import gui.displayComponents.ShopGUI;
import gui.gameComponents.GameSpace;
import gui.gameComponents.grid.GridSpace;
import gui.guiutils.GuiUtils;
import gui.mvpFramework.view.ViewInterface;
import gui.mvpFramework.view.defaultimplementation.GameView;
import loaders.shopLoader.ShopItem;
import model.ModelInterface;
import model.ShopWindow;
import thingFramework.Thing;
import userIO.GameSaver;

/**
 * The "Presenter" in the MVP model. Has a ViewInterface and a ModelInterface. 
 * Has responsibility of updating ViewInterface (GUI), updating the ModelInterface (data), and taking in all inputs/actions that may have an effect on both
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
	private final static Consumer<Presenter> LET_CREATURE_GO = p -> p.model.confirmGrab();	
	


	/*
	 * Transient Instance variables:
	 * 
	 * 
	 */

	/**
	 * The "View" of this presenter. Manages the GUI. This is made transient because want to restore the GUI manually. 
	 */
	private transient ViewInterface view;
	/**
	 * The shop that appears when the user clicks it. This is made transient because want to restore the GUI manually. 
	 */
	private transient ShopGUI shopGUI;
	/**
	 * When an JPanel is opened, this will be set to that JPanel
	 */
	private transient JComponent currentWindow = null;

	private transient String oldString;  //used for debugging only, represent board.toString(), print when that changes
	private transient String newString; //used for debugging only,  represent board.toString(), print when that changes
	/**
	 * Map of all things that are on model and were sold via the shop. This is made transient to manually set GUI.
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
	private ModelInterface model;
	/**
	 * The ShopWindow. This is the "model" of the shop GUI, and manages what is happening in memory in the shop.
	 */
	private ShopWindow shopWindow;
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
	/**
	 * The amount of gold that was present on the ModelInterface before the last GUI update.
	 * Used so that if the user is in the shop, and the amount of gold they have changes, the 
	 * shop knows it must update
	 */
	private int amountOfLastGold = -1;
	/**
	 * The amount of popularity that was present on the ModelInterface before the last GUI update.
	 * Used so that if the user is in the shop, and the amount of popularity they have changes, the 
	 * shop knows it must update
	 */
	private int amountOfLastPop = -1;
	
	

	/**
	 * Get the ViewInterface associated with this Presenter
	 * @return the ViewInterface  that this presenter has
	 */
	public ViewInterface getView() {
		return view;
	}
	/**
	 * Creates a new Presenter with the provided ModelInterface and ViewInterface 
	 * @param model the ModelInterface (or "model" in MVP)
	 * @param viewTitle the Title of the ViewInterface 
	 * @param gameSaver the gamesaver to use for saving
	 */
	public Presenter(final ModelInterface model, final String viewTitle, final GameSaver gameSaver) {
		this.gameStateSaver = new CurrentGamestateSaver(gameSaver);
		allThings = new HashMap<GridSpace, Thing>();
		soldThings = new HashMap<GridSpace, ShopItem>();
		title = viewTitle;
		setModel(model);
		setView(viewTitle);
		windowFactory = new PresenterWindowFactory(this, this.model, shopWindow);

	}
	/**
	 * save the current game
	 * @return true if was able to save
	 */
	public boolean saveGame() {
		if (state == CurrentState.PLACING_SPACE) {
			JOptionPane.showMessageDialog(view.getFrame(), "Sorry! You can't save while holding onto something!");
			return false;
		}
		return gameStateSaver.saveGame(this, view.getFrame());
	}
	/**
	 * Sets the model for this presenter
	 * @param model the model to set
	 */
	public void setModel(final ModelInterface model) {
		this.model = model;
		oldString = model.toString();
		shopWindow = new ShopWindow(model);
	}
	/**
	 * Checks if the GridSpace is present
	 * @param gridSpace the GridSpace to check
	 * @return true if the space is present
	 */
	public boolean containsGridSpace(final GridSpace gridSpace) {
		return allThings.containsKey(gridSpace);
	}

	/**
	 * Removes the GridSpace from the GUI and removes the thing that it corresponds to from the model. Also removes it
	 * from allThings.
	 * @param gridSpace the GridSpace to remove
	 * @param removeFromModel if true will remove the thing from Model, otherwise just removes it from allThings map/soldThings map
	 * @return the mapEntry that was removed
	 */
	private AllThingsMapEntry removeGridSpace(final GridSpace gridSpace, final boolean removeFromModel) {
		if (!allThings.containsKey(gridSpace))
			throw new RuntimeException("Attempted To Remove Non-Existant GridSpace");
		if (removeFromModel)
			model.removeThing(allThings.get(gridSpace));
		return new AllThingsMapEntry(gridSpace, allThings.remove(gridSpace));
	}
	/**
	 * Updates the notification counter of the notifaction button and updates the display of the GUI
	 */
	public void updateGUI() {
		if (model == null || view == null)
			return;
		view.setWildCreatureCount(model.numCreaturesWaiting());
		if (model.getGold() != amountOfLastGold || model.getPopularity() != amountOfLastPop) {
			amountOfLastGold = model.getGold();
			amountOfLastPop = model.getPopularity();
			if (state == CurrentState.IN_SHOP)
				updateShop();
			else
				suggestShopUpdate();
		}
		view.setModelAttributes(model.getGold(), model.getPopularity());
		view.updateDisplay();
		if (state != CurrentState.PLACING_SPACE && !toBeDeleted.isEmpty()) {
		
			deleteGridSpace(toBeDeleted.poll());
		}
		
	}
	private void updateToolTips() {
		if (toolTipsEnabled)
			allThings.forEach((gridSpace, thing) -> DescriptionManager.getInstance().setDescription(gridSpace, thing));
	}
	private void stopToolTips() {
		toolTipsEnabled = false;
		allThings.forEach((gridSpace, thing) -> {
			DescriptionManager.getInstance().removeDescription(gridSpace);
		});
	}
	private void resumeToolTips() {
		toolTipsEnabled = true;
	}
	/**
	 * Calls the model's update method
	 */
	public void updateModel() {
		if (model == null || view == null)
			return;
		model.update();
		if (model.hasRemoveRequest()) {
			final Thing toRemove = model.getNextRemoveRequest();
			SwingUtilities.invokeLater(() -> {
				final GridSpace[] toDelete = new GridSpace[1];
				allThings.forEach((gridSpace, t) -> {
					if (t == toRemove)
						toDelete[0] = gridSpace;
				});
				toDelete[0].removeListeners();
				toBeDeleted.add(toDelete[0]);
			});	
		}
		if (PRINT_BOARD) {
			newString = model.toString();
			if (!newString.equals(oldString)) {
				System.out.println("\n---IN GAME TIME---: "+ model.getTotalInGameTime() + "\n" + model +   "\n-------");
				oldString = newString;
				System.out.println("\n----Time Stats--- \n" + model.getTimeStats() + "\n-----");
			}
		}
	}

	/**
	 * Sets the ViewInterface of this Presenter, initializes shopWindow
	 * @param title the Title of the ViewInterface
	 */
	public void setView(final String title) {
		this.view = new GameView(title);
		view.setPresenter(this);
		shopGUI = new ShopGUI(view);
		shopGUI.updateItems(shopWindow.getItemsInShop());
		if (SHOW_CONFIRM_ON_CLOSE) {
			view.getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			view.getFrame().addWindowListener(new WindowAdapter() { //TODO: Move?
				Object[] options = {"Save And Quit", "Quit Without Saving", "Cancel"};
				@Override
				public void windowClosing(final WindowEvent windowEvent) {
					stopToolTips();
					final int n = JOptionPane.showOptionDialog(view.getFrame(), goodbyeMessage, "", 
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
	 * To be called whenever the notification button is clicked. Displays the PopUp JPanel with the next Creature in the wild Creature queue in the model
	 *@sets CurrentState.NOTIFICATION_WINDOW
	 */
	public void notificationClicked() {
		if (!model.wildCreaturePresent() || state != CurrentState.GAMEPLAY)
			return;
		setState( CurrentState.NOTIFICATION_WINDOW);
		setCurrentWindow(windowFactory.wildCreatureWindow(model.grabWildCreature(), LET_CREATURE_GO));									
	}


	/**
	 * Sets the current window to the passed in window, and removes the currentWindow if any
	 * @param window the window to display
	 */
	private void setCurrentWindow(final JComponent window) {
		if (currentWindow != null)
			view.removeDisplay(currentWindow);
		currentWindow = window;
		view.displayComponentCentered(window);
	}

	private void stopPopupMenus() {
		popupMenusEnabled = false;
		allThings.forEach((gridSpace, t) -> gridSpace.removeListeners());
	}
	private void resumePopupMenus() {
		popupMenusEnabled = true;
		allThings.forEach((gridSpace, t) -> updateListener(gridSpace));
	}
	/**
	 * Updates all the mouse listeners of the provided GridSpace
	 * @param gridSpace the GridSpace to update
	 */
	void updateListener(final GridSpace gridSpace) {
		int val = 0;
		if (soldThings.containsKey(gridSpace))
			val = shopWindow.getSellBackValue(soldThings.get(gridSpace));
		gridSpace.updateListeners(soldThings.containsKey(gridSpace), !isNotRemovable(gridSpace), val > 0 || model.getGold() >= Math.abs(val));
	}
	private boolean isNotRemovable(final GridSpace gridSpace) {
		return allThings.get(gridSpace).containsAttribute("removable") && (!allThings.get(gridSpace).getAttributeValue("removable", ParseType.BOOLEAN));
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
			view.setEnabledForButtons(false);

		}
		else if (state == CurrentState.GAMEPLAY) {
			DescriptionManager.getInstance().setEnabled(true);
			if (!toolTipsEnabled)
				resumeToolTips();
			if (!popupMenusEnabled)
				resumePopupMenus();
			view.setEnabledForButtons(true);
		}
		this.state = state;


	}
	
	/**
	 * Get the text that should display in the popup menu for this GridSpace on the button that allows the user
	 * to delete a GridSpace
	 * @param gridSpace the GridSpace to get the discard text for
	 * @return the discard text
	 */
	public String getDiscardText(final GameSpace gridSpace) {
		return allThings.get(gridSpace).getDiscardText();
	}
	/**
	 * Finalizes the add attempt by getting rid of thingToAdd, itemToPurchase, and changing the state of the game back to GAMEPLAY
	 * Will be called whether or not the GridSpace was actually added to the model
	 * @sets CurrentState.GAMEPLAY
	 */
	private void finishAddAttempt() {
		thingToAdd = null;
		itemToPurchase = null;
		setState(CurrentState.GAMEPLAY);
	}
	/**
	 * To be called when the provided GridSpace is succesfully added  to the model.
	 * @param gridSpace the GridSpace that was added
	 * @param type the type of add (from queue, moving, etc.)
	 */
	public void notifyAdded(final GridSpace gridSpace, final AddType type) {
		if (toBeDeleted.contains(gridSpace)) {
			toBeDeleted.remove(gridSpace);
			deleteGridSpace(gridSpace);
			finishAddAttempt();
			return;
		}
		switch(type) {
		case CREATURE_FROM_QUEUE:
			model.confirmGrab();
			break;
		case ITEM_FROM_SHOP:
			soldThings.put(gridSpace, itemToPurchase);
			thingToAdd = shopWindow.confirmPurchase();
			updateShop();
			break;
		default:
			break;
		}
		if (type.isNewThing)
			addGridSpace(gridSpace, type);
		updateListener(gridSpace);
		finishAddAttempt();
	}
	/**
	 * Adds the provided GridSpace to <GridSpace, Thing> map and also adds the thing (thingToAdd) to the model.
	 * @param gridSpace
	 */
	private void addGridSpace(final GridSpace gridSpace, final AddType type) {
		if (thingToAdd == null)
			return;
		model.addThing(thingToAdd);
		allThings.put(gridSpace, thingToAdd);		
	}

	/**
	 * To be called when the provided GridSpace was being added, but the user decided to cancel the add (hit escape).
	 * If this was a AddType.CREATURE_FROM_QUEUE, will place the Creature back in the queue. This is NOT called when a move is canceled, as there is
	 * no need to update the game state
	 * @param gridSpace the GridSpace that was being added 
	 * @param type
	 */
	public void notifyAddCanceled(final GridSpace gridSpace, final AddType type) {
		switch (type) {
		case CREATURE_FROM_QUEUE:
			model.undoGrab();
			break;
		case ITEM_FROM_SHOP:
			shopWindow.cancelPurchase();
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
	 GameSpace generateGameSpaceFromThing(final Thing t) {
		Image i = GuiUtils.readAndTrimImage(t.getImage());
		if (t.getName().equals("Small Table"))
			i = GuiUtils.getScaledImage(i, 40, 40);
		final GameSpace gameSpace = new GameSpace(i, t.getName());
		return gameSpace;
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
		view.attemptNewGridSpaceAdd(generateGameSpaceFromThing(t), type);
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
		view.attemptExistingGridSpaceAdd(entry.gridSpace, type);
	}
	/**
	 * To be called when the user attempts to move a GridSpace.  
	 * @param gridSpace the GridSpace that the user wants to move
	 * @return false if the user is not allowed to move the GridSpace (they are currently in the Notification Window for example)
	 *@sets CurrentState.PLACING_SPACE if doesn't return false, otherwise keeps state the same
	 */
	public boolean attemptMoveGridSpace(final GridSpace gridSpace) {
		if (!containsGridSpace(gridSpace))
			throw new IllegalArgumentException("GridSpace " + gridSpace + "Not found on board");
		if (state != CurrentState.GAMEPLAY)
			return false;
		setState(CurrentState.PLACING_SPACE);
		gridSpace.removeFromGrid();
		final AllThingsMapEntry entry = new AllThingsMapEntry(gridSpace, allThings.get(gridSpace));
		attemptAddExistingThing(entry, AddType.PRIOR_ON_BOARD);
		return true;
	}
	/**
	 * To be called when the user attempts to delete a GridSpace
	 * @param gridSpace the GridSpace that the user wants to delete
	 * @return false if the user is not allowed to delete the GridSpace (they are currently in the Notification Window for example)
	 *@sets CurrentState.DELETE_CONFIRM_WINDOW if doesn't return false, otherwise keeps state the same
	 */
	public boolean attemptDeleteGridSpace(final GridSpace gridSpace) {
		if (!containsGridSpace(gridSpace))
			throw new IllegalArgumentException("GridSpace " + gridSpace + "Not found on board");
		if (state != CurrentState.GAMEPLAY)
			return false;
		setState(CurrentState.DELETE_CONFIRM_WINDOW);
		final Thing thingToDelete = allThings.get(gridSpace);
		setCurrentWindow(windowFactory.attemptToDeleteWindow(thingToDelete));
		gridSpaceToDelete = gridSpace;
		return true;

	}
	private void deleteGridSpace(final GridSpace gridSpace) {
		/*
		 * Note how this method is different from confirmSellBack in that it conditionally sends items back to the board,
		 * and doesn't refund money if it does
		 */
		if (soldThings.containsKey(gridSpace)) {
			final ShopItem item = soldThings.remove(gridSpace);
			if (item.shouldSendBackToShopWhenRemoved())
				shopWindow.sendItemBackToShop(item);
		}
		this.removeGridSpace(gridSpace, true);
		gridSpace.removeFromGrid();
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
	 * @param gridSpace the GridSpace that the user wants to sell back
	 * @return false if the user is not allowed to sell back the GridSpace (they are currently in the Notification Window for example)
	 *@sets CurrentState.SELL_BACK_CONFIRM_WINDOW if doesn't return false, otherwise keeps state the same
	 */
	public boolean attemptSellBackGridSpace(final GridSpace gridSpace) {
		if (!containsGridSpace(gridSpace))
			throw new IllegalArgumentException("GridSpace " + gridSpace + "Not found on board");
		if (!soldThings.containsKey(gridSpace))
			throw new IllegalArgumentException("GridSpace " + gridSpace + "Not a sold item");
		if (state != CurrentState.GAMEPLAY)
			return false;
		setState(CurrentState.SELL_BACK_CONFIRM_WINDOW);
		itemToSellBack = soldThings.get(gridSpace);
		setCurrentWindow(windowFactory.attemptToSellBackWindow(gridSpace, itemToSellBack));
		gridSpaceToDelete = gridSpace;
		return true;

	}
	private void confirmSellBack() {
		if (state != CurrentState.SELL_BACK_CONFIRM_WINDOW || gridSpaceToDelete == null || itemToSellBack == null) 
			throw new RuntimeException("No Delete to Confirm");
		shopWindow.sellBack(itemToSellBack);
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
	 * @param gridSpace the GridSpace to get the sell back value for
	 * @return the amount of money that the Thing that the provided GridSpace represents could be sold back to the shop for
	 */
	public int getGridSpaceSellBackValue(final GridSpace gridSpace) {
		return shopWindow.getSellBackValue(soldThings.get(gridSpace));
	}

	/**
	 * To be called when the User Clicks the notification button and then clicks cancel. Undos the model's grab, and sets the state of the game back to GamePlay
	 * @sets CurrentState.GAMEPLAY
	 */
	private void undoNotificationClicked() {
		model.undoGrab();
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
		final JComponent cards = shopGUI.getShopWindowAsCardLayout();
		setCurrentWindow(cards);


	}
	/**
	 * To be called when the user attempts to purchase a ShopItem from the SHop
	 * @param item the ShopItem that the user is attempting to purchase
	 */
	public void notifyAttemptPurchaseThing(final ShopItem item) {
		if (!shopWindow.canPurchase(item) || !item.allowedToPlaceAnother(numOfShopItemOnBoard(item)))
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
		final Thing thing = shopWindow.startPurchase(itemToPurchase);
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
			shopGUI.updateItems(shopWindow.getItemsInShop());
			if (state == CurrentState.IN_SHOP)
				refreshShop();
		});
	}
	/**
	 * Refresh the shop window
	 */
	private void refreshShop() {
		final JComponent cards = shopGUI.getShopWindowAtCurrentLocation();
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
			attemptAddThing(model.getGrabbed(), AddType.CREATURE_FROM_QUEUE);	
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
			view.cancelGridSpaceAdd();
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
			view.removeDisplay(currentWindow);
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
	 * Displays or un-displays the advanced stat window for this board
	 */
	public void toggleAdvancedStats() {
		if (state == CurrentState.ADVANCED_STATS_WINDOW) { //close window if open
			Canceled();
			return;
		}
		if (state != CurrentState.GAMEPLAY) //if not in gameplay don't do anything
			return;
		setState(CurrentState.ADVANCED_STATS_WINDOW); //otherwise, display window
		setCurrentWindow(windowFactory.advancedStatsWindow());
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
		return (!item.allowedToPlaceAnother(numOfShopItemOnBoard(item)) || !shopWindow.canPurchase(item));
	}
	/**
	 * returns the String that should be displayed on the popup menu on the button to sell back this GridSpace
	 * @param gridSpace the gridspace of interest
	 * @return the string that should be displayed to sell this gridspace back
	 */
	public String getSellBackString(final GridSpace gridSpace) {
		final int sellBackValue = getGridSpaceSellBackValue(gridSpace);
		final String sellBackString;
		if (sellBackValue >= 0) {
			sellBackString = "Sell " + gridSpace.getName() + "\nback for " + GuiUtils.getMoneySymbol() + sellBackValue;
		}
		else {
			sellBackString = "Pay " + GuiUtils.getMoneySymbol() + Math.abs(sellBackValue) +  " to return\n" + gridSpace.getName() + " to the shop";
		}
		return sellBackString;
	}
	/**
	 * Writes to the save file
	 */
	@SuppressWarnings("unused")
	private void writeObject(final ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		PresenterSaver.writePresenterMaps(oos, allThings, soldThings);
		if (DEBUG || PRINT_BOARD)
			System.out.println(model.getTimeStats());
	}
	/**
	 * Read from save. Will also call appropriate start up methods
	 */
	@SuppressWarnings({"unused" })
	private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject(); //will read in board, title, allThings, soldThings
		allThings = new HashMap<GridSpace, Thing>();
		soldThings = new HashMap<GridSpace, ShopItem>();
		setView(title); //set up the game view as well as the shopwindow
		PresenterSaver.readPresenterMaps(ois, allThings, soldThings, this, view);
		onStartUp();
		shopGUI.updateItems(shopWindow.getItemsInShop());
		goodbyeMessage = new GoodbyeMessageCreator().getMessage();
		manageStateOnStartup();
		if (DEBUG || PRINT_BOARD)
			System.out.println(model.getTimeStats());

	}
	/**
	 * To be called when the game starts up again
	 */
	private void onStartUp() {
		model.onStartUp();
		shopWindow.onStartUp();
	}
	/**
	 * Depending on the current state upon restarting the game, gets the game back to the default GamePlay state,
	 * by closing all windows
	 */
	private void manageStateOnStartup() {
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
			GuiUtils.displayError(new IllegalStateException("Should not have been able to save while placing"), view.getFrame());
			break;
		default:
			setState(CurrentState.GAMEPLAY);
			break;
		}
	}
	/**
	 * A mapping between a thing and a GridSpace
	 * @author David O'Sullivan
	 *
	 */
	private static class AllThingsMapEntry{
		public Thing thing;
		public GridSpace gridSpace;
		public AllThingsMapEntry(final GridSpace gridSpace, final Thing t) {
			this.gridSpace = gridSpace;
			thing = t;
		}
	}

}
