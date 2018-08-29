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
import effects.Eventful;
import effects.GlobalModifierOption;
import gameutils.GameUtils;
import loaders.ThingFactory;
import loaders.shopLoader.ShopItem;
import modifiers.Modifier;
import thingFramework.Item;
import thingFramework.Pokemon;
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
	 * The minimum period in minutes at which new pokemon are checked for
	 */
	private static final double MIN_POKEPERIOD = 1;
	/**
	 * The maximum number of pokemon that can be in the dequeue at a time
	 */
	private static final int MAX_POKEMON_IN_QUEUE = 100;
	/*
	 * Transient instance variables:
	 * 
	 * 
	 */
	/**
	 * The event that checks for pokemon on a certain period based on popularity
	 * Since it is static, it will be recreated on serialization, meaning it will have a new 
	 * time created, and the pokemon will not be spawning while it is offline.
	 * The transient keyword, though unnecessary, is included to remind of this feature.
	 * Also, this is (as of now) redundant because keeptrackwhileoff for events is by default set to false.
	 * But regardless, making it static makes sense.
	 */
	private transient static final Event checkForPokemon = checkForPokemonEvent(); 
	/**
	 * Blank item to store the checkForPokemon event
	 */
	private transient static final Item checkForPokemonThing = Item.generateBlankItemWithEvents(checkForPokemon);

	/*
	 * Non-transient instance variables:
	 * 
	 */
	/**
	 * This is the currently Grabbed pokemon, it may be placed on the board and removed from foundPokemon or it may be put back
	 */
	private Pokemon grabbedPokemon = null;
	/**
	 * This is the current ShopItem that may be purchased if the purchase is confirmed or it may be refunded if the purchase is canceled
	 */
	private ShopItem grabbedShopItem = null;


	/**
	 * The queue of found wild pokemon (pokemon generated from a lookForPokemon() call)
	 */
	private final Deque<Pokemon> foundPokemon = new LinkedList<Pokemon>();
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
	 * Set of all pokemon on board. This is a subset of thingsOnBoard
	 */
	private final Set<Pokemon> pokemonOnBoard = new HashSet<Pokemon>();
	/**
	 * Set of all items on board. This is a subset of thingsOnBoard
	 */
	private final Set<Item> itemsOnBoard = new HashSet<Item>();
	/**
	 * The number of pokemon currently on the board
	 */
	private volatile int numPokemon = 0;
	/**
	 * The pokemon on the board or in the queue used so that duplicate pokemon are
	 * signifigantly less likely to show up. Map from the name of the pokemon to the # present
	 */
	private volatile Map<String, Integer> uniquePokemonLookup = new HashMap<String, Integer>();
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
	//TODO: Put all possible things with their associated events in a manager of its own, should be able to grab events with quantity > 0 
	/**
	 * Used for managing all the global modifiers on the board
	 */
	private final ModifierManager modifierManager;
	/**
	 * The queue of Things that have been requested to be removed from the board
	 */
	private final Queue<Thing> removeRequests = new LinkedList<Thing>();
	/**
	 * % chance that a legendary pokemon spawns
	 */
	private double legendaryChance = 0;
	/**
	 * Amount to decrease the calculated look for pokemon period by (in minutes)
	 */
	private double periodDecreaseMod = 0;
	private final WildPokemonGenerator pokemonGenerator;
	public Board() {
		pokemonGenerator = new WildPokemonGenerator(this);
		shop = new Shop();
		stm = new SessionTimeManager();
		modifierManager = new ModifierManager(this);
		events = new EventManager(this);
		events.addThing(checkForPokemonThing);
	}
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
	 * @return the "default" event that will check for new wild pokemon
	 */
	private static Event checkForPokemonEvent() {
		return new CustomPeriodEvent(board -> board.lookForPokemonGuranteedFind(), board -> {
			board.lookForPokemon();
		}, board -> {
			return board.getLookPokemonPeriod();
		});
	}
	/**
	 * @return The period at which the game checks for new pokemon.
	 *  value of the form A-(pop/B)^C, minimum of MIN_POKEPERIOD
	 */
	private double getLookPokemonPeriod() {
		final double A=  4; //max value+1
		final double B = 60; //"length" of near-constant values
		final double C = 1.3; //steepness of drop
		return RAPID_SPAWN ? 1.666e-5 : Math.max(0, (Math.max(MIN_POKEPERIOD, A-Math.pow(getPopularity()/B, C))-periodDecreaseMod));
	}


	/**
	 * @param Using thing loader creates a new instance of a pokemon with the given name.
	 * Adds the provided pokemon to the foundPokemon queue and updates the set of pokemon names in addToUniquePokemonLookup
	 */
	private void addToFoundPokemon(final String name) {
		if (foundPokemon.size() >= MAX_POKEMON_IN_QUEUE)
			return;
		final Pokemon p = ThingFactory.sharedInstance().generateNewPokemon(name);
		foundPokemon.addLast(p);
		addToUniquePokemonLookup(p);
	}
	private void addToUniquePokemonLookup(final Pokemon p) {
		uniquePokemonLookup.merge(p.getName(), 1, (old, v) -> old+1);
	}
	private void removeFromUniquePokemonLookup(final Pokemon p) {
		uniquePokemonLookup.compute(p.getName(), (k, v) -> (v-1 == 0) ? null : v-1);
	}
	boolean isUniquePokemon(final String name) {
		return !uniquePokemonLookup.containsKey(name);
	}

	private void lookForAndAddPokemon(final boolean automaticSpawn) {
		final String newPokemon = pokemonGenerator.lookForPokemon(automaticSpawn); 
		if (newPokemon != null)
			addToFoundPokemon(newPokemon);
	}
	/**
	 * To be called by an event on calculated period. Will check if a pokemon is even found using 
	 * getPercentChancePokemonFound(), and if it is, will find a pokemon, with lower rarity pokemons being
	 * more likely
	 */
	private void lookForPokemon() {
		lookForAndAddPokemon(RAPID_SPAWN ? true : false); 
	}
	/**
	 * Looks for pokemon, but gurantees that one is found (as supposed to having a percent chance that none are found)
	 */
	private void lookForPokemonGuranteedFind() {
		lookForAndAddPokemon(true);
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
	public String getTimeStats() {
		return "TotalTimeSinceStart: " + getTotalTimeSinceStart() + "\n" + " SessionGameTime: " + getSessionGameTime() + "\n TotalInGameTime: " + getTotalInGameTime();
	}
	/**
	 * Increase the % chance (0-100) of a legendary pokemon appearing
	 * @param increase the percentage (0-100) to increase by
	 */
	public synchronized void increaseLegendaryChance(final int increase) {
		legendaryChance = Math.min(100, Math.max(0, legendaryChance + increase));
	}
	/**
	 * Decrease the % chance (0-100) of a legendary pokemon appearing
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

	public synchronized int getGold() {
		return gold;
	}
	public synchronized void setGold(final int gold) {
		this.gold = gold;
	}
	public synchronized int getPopularity() {
		return popularity;
	}
	public synchronized void setPopularity(final int popularity) {
		this.popularity = popularity;
	}
	public synchronized void addGold(final int gold) {
		setGold(Math.max(MINGOLD, getGold()+gold));
	}
	public synchronized void subtractGold(final int gold) {
		addGold(-gold);
	}
	public synchronized void addPopularity(final int popularity) {
		setPopularity(Math.max(MINPOP, getPopularity()+popularity));
	}
	public synchronized void subtractPopularity(final int popularity) {
		addPopularity(-popularity);
	}
	public synchronized void notifyPokemonAdded(final Pokemon p) {
		numPokemon++;
		addToUniquePokemonLookup(p);
		pokemonOnBoard.add(p);
		modifierManager.getModifiersOfOption(GlobalModifierOption.ONLY_POKEMON).forEach(mod -> p.addModifierIfShould(mod));

	}
	public synchronized void notifyPokemonRemoved(final Pokemon p) {
		numPokemon--;
		removeFromUniquePokemonLookup(p);
		pokemonOnBoard.remove(p);
		modifierManager.getModifiersOfOption(GlobalModifierOption.ONLY_POKEMON).forEach(mod -> p.removeModifierIfPresent(mod));
	}
	public synchronized void notifyItemAdded(final Item i) {
		itemsOnBoard.add(i);
		modifierManager.getModifiersOfOption(GlobalModifierOption.ONLY_ITEMS).forEach(mod -> i.addModifierIfShould(mod));
	}
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
		events.addThing(eventful);
	}
	/**
	 * calls executeOnRemove for all associated events. Permanetly removes events from event set if none left
	 * @param eventful the eventful to call the executeOnRemove events on, and remove future events from running
	 * @param permanentlyRemove if true will also remove those events from the set
	 */
	private void removeAssociatedEvents(final Eventful eventful) {
		events.removeThing(eventful);
	}
	/**
	 * @return true if there is a pokemon in the foundPokemon Queue (that is, a wild pokemon spawned)
	 */
	public boolean wildPokemonPresent() {
		return !foundPokemon.isEmpty();
	}
	/**
	 * For debugging purposes only. Gets the pokemon with the given name
	 * @param name the name of the pokemon
	 * @return the new pokemon
	 */
	public Pokemon getPokemon(final String name) {
		if (DEBUG)
			return ThingFactory.sharedInstance().generateNewPokemon(name);
		else
			throw new RuntimeException("Method should not be called when not debugging");
	}
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		for (final Thing entry : thingsOnBoard) {
			s.append("\n" + entry.toString() + "\n");
		}
		return s.append("GOLD: " + getGold() + "\nPOP:" +getPopularity()).toString();
	}
	/**
	 * @return the next wild pokemon in the queue, null if there is none, temporarily removes from queue. Call undoGrab() to place back and confirmGrab() to confirm removal
	 */
	public Pokemon grabWildPokemon() {
		if (grabbedPokemon != null)
			throw new RuntimeException("Previous Grab Unconfirmed");
		final Pokemon grabbed = foundPokemon.poll();
		grabbedPokemon = grabbed;
		return grabbed;
	}
	/**
	 * Undo the effects of grabWildPokemon() and place grabbed pokemon back at front of queue
	 */
	public void undoGrab() {
		if (grabbedPokemon == null) {
			throw new RuntimeException("No Pokemon Grabbed");
		}
		foundPokemon.addFirst(grabbedPokemon);
		grabbedPokemon = null;
	}
	/**
	 * Confirm removal of pokemon from queue
	 * @return The Pokemon that was confirmed to be grabbed
	 */
	public Pokemon confirmGrab() {
		if (grabbedPokemon == null) {
			throw new RuntimeException("No Pokemon Grabbed");
		}
		final Pokemon p = grabbedPokemon;
		grabbedPokemon = null;
		removeFromUniquePokemonLookup(p);
		return p;
	}
	/**
	 * @return the currently Grabbed Pokemon
	 */
	public Pokemon getGrabbed() {
		if (grabbedPokemon == null) {
			throw new RuntimeException("No Pokemon Grabbed");
		}
		return grabbedPokemon;
	}
	/**
	 * Instantally gets and removes pokemon from queue
	 * @return The next pokemon in the queue
	 */
	public Pokemon grabAndConfirm() {
		grabWildPokemon();
		return confirmGrab();
	}
	/**
	 * @return the number of pokemon waiting the queue
	 */
	public int numPokemonWaiting() {
		return foundPokemon.size();
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
			return shop.getThingCopy(item);
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
		if (item.getSellBackValue() == item.DEFAULT)
			return Math.max(1 , (int)(item.getCost()*sellBackPercent));
		else
			return item.getSellBackValue();
	}

	public synchronized void applyGlobalModifier(final Modifier mod) {
		applyGlobalModifier(mod, GlobalModifierOption.NO_PREFERENCE);
	}
	/**
	 * Applies the given modifier to specified things based on options. All currently present Thing meeting those stipulations, as well as those added
	 * in the future will be affected as long as the modifier remains. Also starts mod.startCount(...).
	 * @param mod the Thing modifier to add
	 */
	public synchronized void applyGlobalModifier(final Modifier mod, final GlobalModifierOption option) {
		switch(option) {
		case NO_PREFERENCE:
			thingsOnBoard.forEach(t -> t.addModifierIfShould(mod));
			break;
		case ONLY_ITEMS:
			itemsOnBoard.forEach(i -> i.addModifierIfShould(mod));
			break;
		case ONLY_POKEMON:
			pokemonOnBoard.forEach(p -> p.addModifierIfShould(mod));
			break;				
		}
		modifierManager.addGlobalModifier(mod, option);
		mod.startCount(this.getTotalInGameTime());
	}
	/**
	 * Removes the given Modifier from the board. All Things currently affected by the modifier will have the modifier removed
	 *@mod the Modifier to remove
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
	public String getAdvancedStats() {
		final StringBuilder sb = new StringBuilder("Advanced Stats:\n");
		final DecimalFormat dfDouble = new DecimalFormat("0.00"); 
		sb.append("Legendary Percent Chance: " + dfDouble.format(legendaryChance) + "%");
		sb.append("\n");
		sb.append("Look for New Pokemon Period: " + dfDouble.format(getLookPokemonPeriod()) + " minutes");
		sb.append("\n");
		sb.append("Chance that on New Pokemon Period, a Pokemon is Found: " + dfDouble.format(pokemonGenerator.getPercentChancePokemonFound()) + "%");
		return sb.toString();
	}
	/**
	 * Adds to the total amount that whatever the calculated pokemon period is, it is decreased by that amount.
	 * In other words. if the period is currently 1 minute, calling amountToDecrease(1000) will make it 59 seconds, 
	 * whereas calling amountToDecrease(-1000) will make it 61 seconds. 
	 * @param amountToDecrease in milliseconds
	 */
	public void addToPeriodDecrease(final long amountToDecreaseMillis) {
		periodDecreaseMod += GameUtils.millisAsMinutes(amountToDecreaseMillis);
	}
	/**
	 * Removes all pokemon currently on the board
	 * @return the number of pokemon removed this way
	 */
	public int removeAllPokemon() {
		final int i = pokemonOnBoard.size();
		for (final Pokemon p : pokemonOnBoard) {
			addToRemoveRequest(p);
		}
		return i;
	}
	double getLegendaryChance() {
		return legendaryChance;
	}
	synchronized int getNumPokemon() {
		return numPokemon;
	}
	
}
