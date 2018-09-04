package game;
import static gameutils.Constants.DEBUG;
import static gameutils.Constants.RAPID_SPAWN;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.swing.SwingUtilities;

import effects.CustomPeriodEvent;
import effects.Event;
import effects.EventManager;
import effects.Eventful;
import effects.GlobalModifierOption;
import gameutils.GameUtils;
import gui.guiutils.GuiUtils;
import loaders.ThingFactory;
import loaders.shopLoader.ShopItem;
import modifiers.Modifier;
import modifiers.ModifierManager;
import thingFramework.Creature;
import thingFramework.Item;
import thingFramework.Thing;
/**
 * The "Model" in the MVP model. Manages the game state in memory.
 * <br> NOTE: newSession should be called at the beginning of a new session. </br>
 * @author David O'Sullivan
 *
 */
public class Board implements Serializable {
	/*
	 * Static variables:
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The minimum amount of popularity a player can have
	 */
	private static final int MINPOP = 0;
	/**
	 * The minimum amount of gold a player can have
	 */
	private static final int MINGOLD = -9999999;
	/**
	 * Amount of money times original cost to discount on sellback of an item
	 */
	private final double sellBackPercent = .5;	
	/**
	 * The minimum period in minutes at which new creatures are checked for
	 */
	private static final double MIN_CREATURE_PERIOD = 1;
	/**
	 * The maximum number of creatures that can be in the dequeue at a time
	 */
	private static final int MAX_CREATURES_IN_QUEUE = 100;
	/*
	 * Transient instance variables:
	 * 
	 * 
	 */
	/**
	 * The event that checks for creatures on a certain period based on popularity
	 * Since it is static, it will be recreated on serialization, meaning it will have a new 
	 * time created, and the creatures will not be spawning while it is offline.
	 * The transient keyword, though unnecessary, is included to remind of this feature.
	 * Also, this is (as of now) redundant because keeptrackwhileoff for events is by default set to false.
	 * But regardless, making it static makes sense.
	 */
	private transient static final Event checkForCreatures = checkForCreaturesEvent(); 
	/**
	 * Blank item to store the checkForCreatures event
	 */
	private transient static final Item checkForCreaturesThing = Item.generateBlankItemWithEvents(checkForCreatures);

	/*
	 * Non-transient instance variables:
	 * 
	 */
	/**
	 * This is the currently Grabbed creature, it may be placed on the board and removed from foundCreatures or it may be put back
	 */
	private Creature grabbedCreature = null;
	/**
	 * This is the current ShopItem that may be purchased if the purchase is confirmed or it may be refunded if the purchase is canceled
	 */
	private ShopItem grabbedShopItem = null;


	/**
	 * The queue of found wild creatures (creatures generated from a lookForCreature() call)
	 */
	private final Deque<Creature> foundCreatures = new LinkedList<Creature>();
	/**
	 * The amount of money that the player currently posseses
	 */
	private volatile int gold = 0;
	/**
	 * The amount of popularity the player posseses
	 */
	private volatile int popularity = 0;
	/**
	 * Used to represent the state of the board. Contains all things on board.
	 */
	private final Set<Thing> thingsOnBoard = new LinkedHashSet<Thing>();
	/**
	 * Set of all creatures on board. This is a subset of thingsOnBoard
	 */
	private final Set<Creature> creaturesOnBoard = new HashSet<Creature>();
	/**
	 * Set of all items on board. This is a subset of thingsOnBoard
	 */
	private final Set<Item> itemsOnBoard = new HashSet<Item>();
	/**
	 * The number of creatures currently on the board
	 */
	private volatile int numCreatures = 0; 
	/**
	 * The creatures on the board or in the queue used so that duplicate creatures are
	 * signifigantly less likely to show up. Map from the name of the creature to the # present
	 */
	private volatile Map<String, Integer> uniqueCreatureLookup = new HashMap<String, Integer>();
	/**
	 * the shop associated with this board
	 */
	private final Shop shop;
	/**
	 * Manages all the events of the game
	 */
	private final EventManager events;
	/**
	 * Manages the gametime and session time of the current board. Note that newSession should be called to 
	 * update this when a new session is started.
	 */
	private final SessionTimeManager stm;
	/**
	 * Used for managing all the global modifiers on the board
	 */
	private final ModifierManager modifierManager;
	/**
	 * The queue of Things that have been requested to be removed from the board
	 */
	private final Queue<Thing> removeRequests = new LinkedList<Thing>();
	/**
	 * % chance that a legendary creature spawns
	 */
	private double legendaryChance = 0;
	/**
	 * Amount to decrease the calculated look for creatures period by (in minutes)
	 */
	private double periodDecreaseMod = 0;
	/**
	 * Used to generate new Creatures periodically
	 */
	private final WildCreatureGenerator creatureGenerator;
	/**
	 * Creates a new board
	 */
	public Board() {
		creatureGenerator = new WildCreatureGenerator(this);
		shop = new Shop();
		stm = new SessionTimeManager();
		modifierManager = new ModifierManager(this);
		events = new EventManager(this);
		events.notifyEventfulAdded(checkForCreaturesThing);
	}
	/**
	 * Creates a new board starting with the provided gold/popularity
	 * @param gold the starting gold
	 * @param popularity the starting popularity
	 */
	public Board(final int gold, final int popularity) {
		this();
		this.setGold(gold);
		this.setPopularity(popularity);
	}

	/**
	 * To be called on every game tick. Updates time and events
	 */
	public void update() {
		if (SwingUtilities.isEventDispatchThread())
			throw new IllegalStateException("Should not be on EDT");
		stm.updateGameTime();
		executeEvents();
		modifierManager.update();
	}
	/**
	 * Executes all the events in the events Set, goes through each of its entries and executes the list of events that the 
	 * ThingEventSetManager has. 
	 */
	private synchronized void executeEvents() {
		events.runEvents();
	}
	/**
	 * @return the "default" event that will check for new wild creatures
	 */
	private static Event checkForCreaturesEvent() {
		return new CustomPeriodEvent(board -> board.lookForCreatureGuranteedFind(), board -> {
			board.lookForCreature();
		}, board -> {
			return board.getLookForCreaturesPeriod();
		});
	}
	/**
	 * @return The period at which the game checks for new creatures.
	 *  value of the form A-(pop/B)^C, minimum of MIN_CREATURE_PERIOD
	 */
	private double getLookForCreaturesPeriod() {
		final double A=  4; //max value+1
		final double B = 60; //"length" of near-constant values
		final double C = 1.3; //steepness of drop
		return RAPID_SPAWN ? 1.666e-5 : Math.max(0, (Math.max(MIN_CREATURE_PERIOD, A-Math.pow(getPopularity()/B, C))-periodDecreaseMod));
	}


	/**
	 * @param Using thing loader creates a new instance of a creature with the given name.
	 * Adds the provided creature to the foundCreatures queue and updates the set of creature names in addToUniqueCreaturesLookup
	 */
	private void addToFoundCreatures(final String name) {
		if (foundCreatures.size() >= MAX_CREATURES_IN_QUEUE)
			return;
		final Creature creature = ThingFactory.getInstance().generateNewCreature(name);
		foundCreatures.addLast(creature);
		addToUniqueCreaturesLookup(creature);
	}
	/**
	 * Adds the provided creature to the unique creature lookup. Should be called when a creature is placed on the board
	 * or put into the foundCreatures queue. 
	 * @param creature the creature to add 
	 */
	private void addToUniqueCreaturesLookup(final Creature creature) {
		uniqueCreatureLookup.merge(creature.getName(), 1, (old, v) -> old+1);
	}
	/**
	 * Should be called when a  creature is removed from the foundCreature queue or the board.
	 * @param creature the creature that was removed
	 */
	private void removeFromUniqueCreaturesLookup(final Creature creature) {
		uniqueCreatureLookup.compute(creature.getName(), (k, v) -> (v-1 == 0) ? null : v-1);
	}
	/**
	 * Returns true if the creature with the provided name is not currently in the foundCreature queue nor on the board
	 * @param name the name of the creature
	 * @return true if the creature with the provided name is not currently in the foundCreature queue nor on the board
	 */
	boolean isUniqueCreature(final String name) { 
		return !uniqueCreatureLookup.containsKey(name);
	}

	/**
	 * Looks for a creature from the creatureGenerator, and if one is found, adds it the foundCreatures queue
	 * @param automaticSpawn whether or not the creatureGenerator should automatically try to spawn a creature
	 */
	private void lookForAndAddCreature(final boolean automaticSpawn) {
		final String newCreature = creatureGenerator.lookForCreature(automaticSpawn); 
		if (newCreature != null)
			addToFoundCreatures(newCreature);
	}
	/**
	 * To be called by an event on calculated period. Will check if a creature is even found using 
	 * getPercentChanceCreatureFound(), and if it is, will find a creature, with lower rarity creatures being
	 * more likely
	 */
	private void lookForCreature() {
		lookForAndAddCreature(RAPID_SPAWN ? true : false); 
	}
	/**
	 * Looks for a creature, but gurantees that one is found (as supposed to having a percent chance that none are found)
	 */
	private void lookForCreatureGuranteedFind() {
		lookForAndAddCreature(true);
	}
	/**
	 * @param thing the thing to add
	 */
	public synchronized void addThing(final Thing thing) {
		addElementToThingMap(thing);
		modifierManager.getModifiersOfOption(GlobalModifierOption.NO_PREFERENCE).forEach(mod -> thing.addModifierIfShould(mod));
		thing.onPlace(this);
	}
	/**
	 * @param thing The thing to add
	 */
	public synchronized void removeThing(final Thing thing) {
		if (thing==null) {
			throw new RuntimeException("Attempted to Remove null");
		}
		removeElementFromThingMap(thing);
		modifierManager.getModifiersOfOption(GlobalModifierOption.NO_PREFERENCE).forEach(mod -> thing.removeModifierIfPresent(mod));
		thing.onRemove(this);

	}
	/**
	 * To be called whenever the game is rebooted, regardless of time of last save
	 */
	public void onStartUp() {
		shop.checkForShopUpdates();
		stm.signifyNewSession();
	}
	/**
	 * Pause the game timer, will not resume until unPause() is called
	 */
	public void pause() {
		stm.pause();
	}
	/**
	 * Unpause game timer
	 */
	public void unPause() {
		stm.unPause();
	}
	/**
	 * Returns the time since start, the time of the current session, and the total in game time as a formatted string
	 * @return the time since start, the time of the current session, and the total in game time as a formatted string
	 */
	public String getTimeStats() {
		return "TotalTimeSinceStart: " + getTotalTimeSinceStart() + "\n" + " SessionGameTime: " + getSessionGameTime() + "\n TotalInGameTime: " + getTotalInGameTime();
	}
	/**
	 * Increase the % chance (0-100) of a legendary creature appearing
	 * @param increase the percentage (0-100) to increase by
	 */
	public synchronized void increaseLegendaryChance(final int increase) {
		legendaryChance = Math.min(100, Math.max(0, legendaryChance + increase));
	}
	/**
	 * Decrease the % chance (0-100) of a legendary creature  appearing
	 * @param decrease the percentage (0-100) to decrease by
	 */
	public synchronized void decreaseLegendaryChance(final int decrease) {
		increaseLegendaryChance(-decrease);
	}
	/**
	 * @return total time both on and offline
	 */
	public synchronized long getTotalTimeSinceStart() {
		return stm.getTotalTimeSinceStart();
	}
	/**
	 * @return total time in this game session
	 */
	public synchronized long getSessionGameTime() {
		return stm.getSessionGameTime();
	}
	/**
	 * @return total time throughout all game sessions
	 */
	public synchronized long getTotalInGameTime() {
		return stm.getTotalInGameTime();
	}

	/**
	 * Returns the amount of gold that the board has
	 * @return the amount of gold that the board has
	 */
	public synchronized int getGold() {
		return gold;
	}
	/**
	 * Sets the amount of gold that the board has
	 * @param gold the new amount of gold
	 */
	public synchronized void setGold(final int gold) {
		this.gold = Math.max(MINGOLD, gold);
	}
	/**
	 * Returns the amount of popularity that the board has
	 * @return the amount of popularity that the board has
	 */
	public synchronized int getPopularity() {
		return popularity;
	}
	/**
	 * Sets the amount of popularity that the board has
	 * @param popularity the new amount of popularity
	 */
	public synchronized void setPopularity(final int popularity) {
		this.popularity = Math.max(MINPOP, popularity);
	}
	/**
	 * Adds the amount of gold provided to the total amount of gold on the board
	 * @param gold the amount to add
	 */
	public synchronized void addGold(final int gold) {
		setGold(getGold()+gold);
	}
	/**
	 * Subtracts the amount of gold provided to the total amount of gold on the board. Equivalent to addGold(-gold)
	 * @param gold the amount to subtract
	 */
	public synchronized void subtractGold(final int gold) {
		addGold(-gold);
	}
	/**
	 * Adds the amount of popularity to the total amount of popularity on the board
	 * @param popularity the amount of popularity to add
	 */
	public synchronized void addPopularity(final int popularity) {
		setPopularity(getPopularity()+popularity);
	}
	/**
	 * Subtracts the amount of popularity provided to the total amount of popularity on the board. Equivalent to addPopularity(-popularity)
	 * @param popularity the amount of popularity to subtract
	 */
	public synchronized void subtractPopularity(final int popularity) {
		addPopularity(-popularity);
	}
	/**
	 * To be called whenever a creature is added to the board. Should be called by {@link Creature#onPlace(Board)}
	 * @param creature the creature that was added
	 * 
	 */
	public synchronized void notifyCreatureAdded(final Creature creature) {
		numCreatures++;
		addToUniqueCreaturesLookup(creature);
		creaturesOnBoard.add(creature);
		modifierManager.getModifiersOfOption(GlobalModifierOption.ONLY_CREATURES).forEach(mod -> creature.addModifierIfShould(mod));

	}
	/**
	 * To be called whenever a creature is removed from the board. Should be called by {@link Creature#onRemove(Board)}
	 * @param creature the creature that was removed
	 */
	public synchronized void notifyCreatureRemoved(final Creature creature) {
		numCreatures--;
		removeFromUniqueCreaturesLookup(creature);
		creaturesOnBoard.remove(creature);
		modifierManager.getModifiersOfOption(GlobalModifierOption.ONLY_CREATURES).forEach(mod -> creature.removeModifierIfPresent(mod));
	}
	/**
	 * To be called whenever an Item is added to the board. Should be called by {@link Item#onPlace(Board)}
	 * @param i the item that was added
	 */
	public synchronized void notifyItemAdded(final Item i) {
		itemsOnBoard.add(i);
		modifierManager.getModifiersOfOption(GlobalModifierOption.ONLY_ITEMS).forEach(mod -> i.addModifierIfShould(mod));
	}
	/**
	 * To be called whenever an Item is removed from the board. Should be called by {@link Item#onRemove(Board)}
	 * @param i the item that was removed
	 */
	public synchronized void notifyItemRemoved(final Item i) {
		itemsOnBoard.remove(i);
		modifierManager.getModifiersOfOption(GlobalModifierOption.ONLY_ITEMS).forEach(mod -> i.removeModifierIfPresent(mod));
	}
	/**
	 * Adds the element to the map from thing to # present, updating accordingly, and adding events if necessary. 
	 * @param thing the Thing to ADd
	 */
	private void addElementToThingMap(final Thing thing) {
		if (DEBUG)
			System.out.println("Adding: " + thing.toString() + "\n");
		thingsOnBoard.add(thing);
		addAssociatedEvents(thing);
	}
	/**
	 * Removes one quantity of this thing from the map (i.e. decrements the value and removes if newVal == 0)
	 * Will also call all associated events onRemove, and will permanently remove those events if newVal == 0
	 * @param thing the Thing to add
	 */
	private void removeElementFromThingMap(final Thing thing) {
		thingsOnBoard.remove(thing);
		removeAssociatedEvents(thing);  //execute onRemove and permanently remove if this is the last instance
	}
	/**
	 * If the thing hasn't been added to the board yet, update the event set
	 * @param eventful The eventful to get the events for
	 */
	private void addAssociatedEvents(final Eventful eventful) {
		events.notifyEventfulAdded(eventful);
	}
	/**
	 * calls executeOnRemove for all associated events. Permanetly removes events from event set if none left
	 * @param eventful the eventful to call the executeOnRemove events on, and remove future events from running
	 * @param permanentlyRemove if true will also remove those events from the set
	 */
	private void removeAssociatedEvents(final Eventful eventful) {
		events.notifyEventfulRemoved(eventful);
	}
	/**
	 * @return true if there is a creature in the foundCreatures Queue (that is, a wild creature spawned)
	 */
	public boolean wildCreaturePresent() {
		return !foundCreatures.isEmpty();
	}
	/**
	 * For debugging purposes only. Gets the creature with the given name
	 * @param name the name of the creature
	 * @return the new creature
	 */
	public Creature getCreature(final String name) {
		if (DEBUG)
			return ThingFactory.getInstance().generateNewCreature(name);
		else
			throw new RuntimeException("Method should not be called when not debugging");
	}
	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		for (final Thing entry : thingsOnBoard) {
			s.append("\n" + entry.toString() + "\n");
		}
		return s.append("GOLD: " + getGold() + "\nPOP:" +getPopularity()).toString();
	}
	/**
	 * @return the next wild creature in the queue, null if there is none, temporarily removes from queue. Call undoGrab() to place back and confirmGrab() to confirm removal
	 */
	public Creature grabWildCreature() {
		if (grabbedCreature != null)
			throw new RuntimeException("Previous Grab Unconfirmed");
		final Creature grabbed = foundCreatures.poll();
		grabbedCreature = grabbed;
		return grabbed;
	}
	/**
	 * Undo the effects of grabWildCreature() and place grabbed creature back at front of queue
	 */
	public void undoGrab() {
		if (grabbedCreature == null) {
			throw new RuntimeException("No Creature Grabbed");
		}
		foundCreatures.addFirst(grabbedCreature);
		grabbedCreature = null;
	}
	/**
	 * Confirm removal of creature from queue
	 * @return The Creature that was confirmed to be grabbed
	 */
	public Creature confirmGrab() {
		if (grabbedCreature == null) {
			throw new RuntimeException("No Creature Grabbed");
		}
		final Creature creature = grabbedCreature;
		grabbedCreature = null;
		removeFromUniqueCreaturesLookup(creature);
		return creature;
	}
	/**
	 * @return the currently Grabbed Creature
	 */
	public Creature getGrabbed() {
		if (grabbedCreature == null) {
			throw new RuntimeException("No Creature Grabbed");
		}
		return grabbedCreature;
	}
	/**
	 * Gets and removes creature from queue
	 * @return The next creature in the queue
	 */
	public Creature grabAndConfirm() {
		grabWildCreature();
		return confirmGrab();
	}
	/**
	 * @return the number of creatures waiting the queue
	 */
	public int numCreaturesWaiting() {
		return foundCreatures.size();
	}
	/**
	 * @return a queue (sorted by display rank) of all the items presently in the shop
	 */
	public Set<ShopItem> getItemsInShop() {
		return shop.itemsInOrder();
	}
	/**
	 * @param item the item in the shop to purchase
	 * @return true if have enough money to purchase, false otherwise
	 */
	public boolean canPurchase(final ShopItem item) {
		return (item.getCost() <= getGold());
	}
	/**
	 * If have enough money, start the purchase attempt
	 * @param item the ShopItem in the shop
	 * @return a Thing (for display purposes) that corresponds to the item
	 */
	public Thing startPurchase(final ShopItem item) {
		if (canPurchase(item)) {
			grabbedShopItem = item;
			return Shop.getThingCopy(item);
		}
		return null;

	}
	/**
	 * If have enough money, generate a new Thing corresponding to the thingName in the shop, and subtract the cost of that thing
	 * @return the newly generated thing
	 */
	public Thing confirmPurchase() {
		if (!canPurchase(grabbedShopItem))
			return null;
		subtractGold(grabbedShopItem.getCost());
		return shop.purchase(grabbedShopItem);

	}
	/**
	 * Cancel the purchase attempt
	 */
	public void cancelPurchase() {
		grabbedShopItem = null;
	}
	/**
	 * @param item the item to sell back
	 *  @return false if the item is not able to be added back to the shop
	 */
	public boolean canAddBackToShopStock(final ShopItem item) {
		return shop.isValidShopItem(item.getThingName());
	}
	/**
	 * Sell back the specified item, adding it back to the shop and adding that much gold to the user
	 * @param item the item to sell back
	 */
	public void sellBack(final ShopItem item) {
		addGold(getSellBackValue(item));
		sendItemBackToShop(item);
	}
	/**
	 * Sends the item back to shop without adding any gold
	 * @param item the item to send back
	 */
	public void sendItemBackToShop(final ShopItem item) {
		if (canAddBackToShopStock(item))
			shop.addToShopStock(item.getThingName());
	}
	/**
	 * @param item the item to sell back
	 * @return how much it would be sold back for
	 */
	public int getSellBackValue(final ShopItem item) {
		if (item.getSellBackValue() == ShopItem.DEFAULT)
			return Math.max(1 , (int)(item.getCost()*sellBackPercent));
		else
			return item.getSellBackValue();
	}

	/**
	 * Applies the given modifier to all things. All things added
	 * in the future will be affected as long as the modifier remains. Also starts mod.startCount(...).
	 * @param mod the Thing modifier to add
	 */
	public synchronized void applyGlobalModifier(final Modifier mod) {
		applyGlobalModifier(mod, GlobalModifierOption.NO_PREFERENCE);
	}
	/**
	 * Applies the given modifier to specified things based on options. All currently present Thing meeting those stipulations, as well as those added
	 * in the future will be affected as long as the modifier remains. Also starts mod.startCount(...).
	 * @param mod the Thing modifier to add
	 * @param option the preference for what types of Things should be affected by this modifier
	 */
	public synchronized void applyGlobalModifier(final Modifier mod, final GlobalModifierOption option) {
		switch(option) {
		case NO_PREFERENCE:
			thingsOnBoard.forEach(t -> t.addModifierIfShould(mod));
			break;
		case ONLY_ITEMS:
			itemsOnBoard.forEach(i -> i.addModifierIfShould(mod));
			break;
		case ONLY_CREATURES:
			creaturesOnBoard.forEach(c -> c.addModifierIfShould(mod));
			break;				
		}
		modifierManager.addGlobalModifier(mod, option);
		mod.startCount(this.getTotalInGameTime());
	}
	/**
	 * Removes the given Modifier from the board. All Things currently affected by the modifier will have the modifier removed
	 *@param mod the Modifier to remove
	 */
	public synchronized void removeGlobalModifier(final Modifier mod) {
		thingsOnBoard.forEach(t -> t.removeModifierIfPresent(mod));
		modifierManager.notifyGlobalModifierRemoved(mod);
	}
	/**
	 * Add the provided thing to queue of things that the board wants to remove
	 * @param t
	 */
	public synchronized void addToRemoveRequest(final Thing t) {
		removeRequests.add(t);
	}
	/**
	 * The board is requesting to remove something
	 * @return true if the board has at least one thing to remove
	 */
	public synchronized boolean hasRemoveRequest() {
		return !removeRequests.isEmpty();
	}
	/**
	 * @return the next thing the board is requesting to remove (null if none)
	 */
	public synchronized Thing getNextRemoveRequest() {
		return removeRequests.poll();
	}
	/**
	 * Returns a formatted string representation of the legendary percent chance, look for new creature period, chance that on a new creature period a creature is found
	 * @return the formatted string showing advanced board stats
	 */
	public String getAdvancedStats() {
		final StringBuilder sb = new StringBuilder("Advanced Stats:\n");
		final DecimalFormat dfDouble = new DecimalFormat("0.00"); 
		sb.append("Legendary Percent Chance: " + dfDouble.format(legendaryChance) + "%");
		sb.append("\n");
		sb.append("Look for New " + GuiUtils.getCreatureName() + " Period: " + dfDouble.format(getLookForCreaturesPeriod()) + " minutes");
		sb.append("\n");
		sb.append("Chance that on New " + GuiUtils.getCreatureName() + " Period, a " + GuiUtils.getCreatureName() + " is Found: " + dfDouble.format(creatureGenerator.getPercentChanceCreatureFound()) + "%");
		return sb.toString();
	}
	/**
	 * Adds to the total amount that whatever the calculated creature period is, it is decreased by that amount.
	 * In other words. if the period is currently 1 minute, calling amountToDecrease(1000) will make it 59 seconds, 
	 * whereas calling amountToDecrease(-1000) will make it 61 seconds. 
	 * @param amountToDecreaseMillis the amount to decrease the look for new creature period by
	 */
	public void addToPeriodDecrease(final long amountToDecreaseMillis) {
		periodDecreaseMod += GameUtils.millisAsMinutes(amountToDecreaseMillis);
	}
	/**
	 * Removes all creatures currently on the board
	 * @return the number of creatures removed this way
	 */
	public int removeAllCreatures() {
		final int i = creaturesOnBoard.size();
		for (final Creature c : creaturesOnBoard) {
			addToRemoveRequest(c);
		}
		return i;
	}
	/**
	 * gets the percent chance that legendary creature will spawn
	 * @return
	 */
	double getLegendaryChance() {
		return legendaryChance;
	}
	/**
	 * Returns the total number of creatures on the board
	 * @return the total number of creatures on the board
	 */
	synchronized int getNumCreatures() {
		return numCreatures;
	}
	
}
