package model.defaultimplementation;
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

import effects.EventManager;
import effects.Eventful;
import effects.GlobalModifierOption;
import gameutils.GameUtils;
import gui.guiutils.GuiUtils;
import loaders.ThingFactory;
import model.ModelInterface;
import model.SessionTimeManager;
import model.ShopWindow;
import model.ThingObserver;
import model.WildCreatureGeneratorInterface;
import modifiers.Modifier;
import modifiers.ModifierManager;
import thingFramework.Creature;
import thingFramework.Item;
import thingFramework.Thing;
/**
 * Main Implementation of Model Interface.
 * @author David O'Sullivan
 *
 */
public class Board implements Serializable, ModelInterface, ThingObserver {
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
	 * The maximum number of creatures that can be in the dequeue at a time
	 */
	private static final int MAX_CREATURES_IN_QUEUE = 100;

	/*
	 * Instance variables:
	 * 
	 */
	/**
	 * This is the currently Grabbed creature, it may be placed on the board and removed from foundCreatures or it may be put back
	 */
	private Creature grabbedCreature = null;


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
	private final WildCreatureGeneratorInterface creatureGenerator;
	/**
	 * The Shop Window for this board
	 */
	private final ShopWindow shopWindow;
	/**
	 * Creates a new board
	 */
	public Board() {
		creatureGenerator = new BoardWildCreatureGenerator(this);
		shopWindow = new ShopWindow(this);
		stm = new SessionTimeManager();
		modifierManager = new ModifierManager(this);
		events = createNewEventManager();
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
	 * @see model.ModelInterface#update()
	 */
	@Override
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
	 * @see model.ModelInterface#getLookForCreaturesPeriod()
	 */
	@Override
	public double getLookForCreaturesPeriod() {
		return creatureGenerator.getLookForCreaturesPeriod();
	}
	/**
	 * Returns the additional subtraction modifier that should be applied to the period at which new Creatures generate
	 * @return the additional subtraction modifier that should be applied to the period at which new Creatures generate
	 */
	double getPeriodDecreaseMod() {
		return periodDecreaseMod;
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
	 * @see model.ModelInterface#lookForCreature()
	 */
	@Override
	public void lookForCreature() {
		lookForAndAddCreature(RAPID_SPAWN ? true : false); 
	}
	

	/** 
	 * @see model.ModelInterface#lookForCreatureGuranteedFind()
	 */
	@Override
	public void lookForCreatureGuranteedFind() {
		lookForAndAddCreature(true);
	}
	/** 
	 * @see model.ModelInterface#addThing(thingFramework.Thing)
	 */
	@Override
	public synchronized void addThing(final Thing thing) {
		addElementToThingMap(thing);
		modifierManager.getModifiersOfOption(GlobalModifierOption.NO_PREFERENCE).forEach(mod -> thing.addModifierIfShould(mod));
		thing.onPlace(this);
	}
	/** 
	 * @see model.ModelInterface#removeThing(thingFramework.Thing)
	 */
	@Override
	public synchronized void removeThing(final Thing thing) {
		if (thing==null) {
			throw new RuntimeException("Attempted to Remove null");
		}
		removeElementFromThingMap(thing);
		modifierManager.getModifiersOfOption(GlobalModifierOption.NO_PREFERENCE).forEach(mod -> thing.removeModifierIfPresent(mod));
		thing.onRemove(this);

	}
	/** 
	 * @see model.ModelInterface#onStartUp()
	 */
	@Override
	public void onStartUp() {
		stm.signifyNewSession();
	}
	/** 
	 * @see model.ModelInterface#pause()
	 */
	@Override
	public void pause() {
		stm.pause();
	}
	/** 
	 * @see model.ModelInterface#unPause()
	 */
	@Override
	public void unPause() {
		stm.unPause();
	}
	/** 
	 * @see model.ModelInterface#getTimeStats()
	 */
	@Override
	public String getTimeStats() {
		return "TotalTimeSinceStart: " + getTotalTimeSinceStart() + "\n" + " SessionGameTime: " + getSessionGameTime() + "\n TotalInGameTime: " + getTotalInGameTime();
	}
	/** 
	 * @see model.ModelInterface#increaseLegendaryChance(int)
	 */
	@Override
	public synchronized void increaseLegendaryChance(final int increase) {
		legendaryChance = Math.min(100, Math.max(0, legendaryChance + increase));
	}
	/** 
	 * @see model.ModelInterface#decreaseLegendaryChance(int)
	 */
	@Override
	public synchronized void decreaseLegendaryChance(final int decrease) {
		increaseLegendaryChance(-decrease);
	}
	/** 
	 * @see model.ModelInterface#getTotalTimeSinceStart()
	 */
	@Override
	public synchronized long getTotalTimeSinceStart() {
		return stm.getTotalTimeSinceStart();
	}
	/** 
	 * @see model.ModelInterface#getSessionGameTime()
	 */
	@Override
	public synchronized long getSessionGameTime() {
		return stm.getSessionGameTime();
	}
	/** 
	 * @see model.ModelInterface#getTotalInGameTime()
	 */
	@Override
	public synchronized long getTotalInGameTime() {
		return stm.getTotalInGameTime();
	}

	/** 
	 * @see model.ModelInterface#getGold()
	 */
	@Override
	public synchronized int getGold() {
		return gold;
	}
	/** 
	 * @see model.ModelInterface#setGold(int)
	 */
	@Override
	public synchronized void setGold(final int gold) {
		this.gold = Math.max(MINGOLD, gold);
	}
	/** 
	 * @see model.ModelInterface#getPopularity()
	 */
	@Override
	public synchronized int getPopularity() {
		return popularity;
	}
	/** 
	 * @see model.ModelInterface#setPopularity(int)
	 */
	@Override
	public synchronized void setPopularity(final int popularity) {
		this.popularity = Math.max(MINPOP, popularity);
	}
	/** 
	 * @see model.ModelInterface#addGold(int)
	 */
	@Override
	public synchronized void addGold(final int gold) {
		setGold(getGold()+gold);
	}
	/** 
	 * @see model.ModelInterface#subtractGold(int)
	 */
	@Override
	public synchronized void subtractGold(final int gold) {
		addGold(-gold);
	}
	/** 
	 * @see model.ModelInterface#addPopularity(int)
	 */
	@Override
	public synchronized void addPopularity(final int popularity) {
		setPopularity(getPopularity()+popularity);
	}
	/** 
	 * @see model.ModelInterface#subtractPopularity(int)
	 */
	@Override
	public synchronized void subtractPopularity(final int popularity) {
		addPopularity(-popularity);
	}
	/** 
	 * @see model.ModelInterface#notifyCreatureAdded(thingFramework.Creature)
	 */
	@Override
	public synchronized void notifyCreatureAdded(final Creature creature) {
		numCreatures++;
		addToUniqueCreaturesLookup(creature);
		creaturesOnBoard.add(creature);
		modifierManager.getModifiersOfOption(GlobalModifierOption.ONLY_CREATURES).forEach(mod -> creature.addModifierIfShould(mod));

	}
	/** 
	 * @see model.ModelInterface#notifyCreatureRemoved(thingFramework.Creature)
	 */
	@Override
	public synchronized void notifyCreatureRemoved(final Creature creature) {
		numCreatures--;
		removeFromUniqueCreaturesLookup(creature);
		creaturesOnBoard.remove(creature);
		modifierManager.getModifiersOfOption(GlobalModifierOption.ONLY_CREATURES).forEach(mod -> creature.removeModifierIfPresent(mod));
	}
	/** 
	 * @see model.ModelInterface#notifyItemAdded(thingFramework.Item)
	 */
	@Override
	public synchronized void notifyItemAdded(final Item i) {
		itemsOnBoard.add(i);
		modifierManager.getModifiersOfOption(GlobalModifierOption.ONLY_ITEMS).forEach(mod -> i.addModifierIfShould(mod));
	}
	/** 
	 * @see model.ModelInterface#notifyItemRemoved(thingFramework.Item)
	 */
	@Override
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
	 * @see model.ModelInterface#wildCreaturePresent()
	 */
	@Override
	public boolean wildCreaturePresent() {
		return !foundCreatures.isEmpty();
	}
	/** 
	 * @see model.ModelInterface#getCreature(java.lang.String)
	 */
	@Override
	public Creature getCreature(final String name) {
		if (DEBUG)
			return ThingFactory.getInstance().generateNewCreature(name);
		else
			throw new RuntimeException("Method should not be called when not debugging");
	}
	/** 
	 * @see model.ModelInterface#toString()
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
	 * @see model.ModelInterface#grabWildCreature()
	 */
	@Override
	public Creature grabWildCreature() {
		if (grabbedCreature != null)
			throw new RuntimeException("Previous Grab Unconfirmed");
		final Creature grabbed = foundCreatures.poll();
		grabbedCreature = grabbed;
		return grabbed;
	}
	/** 
	 * @see model.ModelInterface#undoGrab()
	 */
	@Override
	public void undoGrab() {
		if (grabbedCreature == null) {
			throw new RuntimeException("No Creature Grabbed");
		}
		foundCreatures.addFirst(grabbedCreature);
		grabbedCreature = null;
	}
	/** 
	 * @see model.ModelInterface#confirmGrab()
	 */
	@Override
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
	 * @see model.ModelInterface#getGrabbed()
	 */
	@Override
	public Creature getGrabbed() {
		if (grabbedCreature == null) {
			throw new RuntimeException("No Creature Grabbed");
		}
		return grabbedCreature;
	}
	/** 
	 * @see model.ModelInterface#grabAndConfirm()
	 */
	@Override
	public Creature grabAndConfirm() {
		grabWildCreature();
		return confirmGrab();
	}
	/** 
	 * @see model.ModelInterface#numCreaturesWaiting()
	 */
	@Override
	public int numCreaturesWaiting() {
		return foundCreatures.size();
	}


	/** 
	 * @see model.ModelInterface#applyGlobalModifier(modifiers.Modifier)
	 */
	@Override
	public synchronized void applyGlobalModifier(final Modifier mod) {
		applyGlobalModifier(mod, GlobalModifierOption.NO_PREFERENCE);
	}
	/** 
	 * @see model.ModelInterface#applyGlobalModifier(modifiers.Modifier, effects.GlobalModifierOption)
	 */
	@Override
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
	 * @see model.ModelInterface#removeGlobalModifier(modifiers.Modifier)
	 */
	@Override
	public synchronized void removeGlobalModifier(final Modifier mod) {
		thingsOnBoard.forEach(t -> t.removeModifierIfPresent(mod));
		modifierManager.notifyGlobalModifierRemoved(mod);
	}
	/** 
	 * @see model.ModelInterface#addToRemoveRequest(thingFramework.Thing)
	 */
	@Override
	public synchronized void addToRemoveRequest(final Thing t) {
		removeRequests.add(t);
	}
	/** 
	 * @see model.ModelInterface#hasRemoveRequest()
	 */
	@Override
	public synchronized boolean hasRemoveRequest() {
		return !removeRequests.isEmpty();
	}
	/** 
	 * @see model.ModelInterface#getNextRemoveRequest()
	 */
	@Override
	public synchronized Thing getNextRemoveRequest() {
		return removeRequests.poll();
	}
	/** 
	 * @see model.ModelInterface#getAdvancedStats()
	 */
	@Override
	public String getAdvancedStats() {
		final StringBuilder sb = new StringBuilder("Advanced Stats:\n");
		final DecimalFormat dfDouble = new DecimalFormat("0.00"); 
		sb.append("Legendary Percent Chance: " + dfDouble.format(legendaryChance) + "%");
		sb.append("\n");
		sb.append("Look for New " + GuiUtils.getCreatureName() + " Period: " + dfDouble.format(getLookForCreaturesPeriod()) + " minutes");
		sb.append("\n");
		sb.append("Chance that on New " + GuiUtils.getCreatureName() + " Period, a " + GuiUtils.getCreatureName() + " is Found: " + dfDouble.format(creatureGenerator.getPercentChanceCreatureFound()) + "%");
		sb.append("\n");
		sb.append("Percent chance popularity increases rarity: " + dfDouble.format(creatureGenerator.getPercentChancePopularityModifies()));
		sb.append("\n");
		sb.append("Rarity boost metric from popularity: " + creatureGenerator.getPopularityModifier());
		return sb.toString();
	}
	/** 
	 * @see model.ModelInterface#addToPeriodDecrease(long)
	 */
	@Override
	public void addToPeriodDecrease(final long amountToDecreaseMillis) {
		periodDecreaseMod += GameUtils.millisAsMinutes(amountToDecreaseMillis);
	}
	/** 
	 * @see model.ModelInterface#removeAllCreatures()
	 */
	@Override
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
	 * Returns the total number of creatures on the board and in the queue
	 * @return the total number of creatures on the board and in the queue
	 */
	synchronized int getNumCreaturesOnBoardAndWaiting() {
		return numCreatures + foundCreatures.size();
	}
	/**
	 * Returns the total number of creatures in the queue
	 * @return the total number of creatures in the queue
	 */
	synchronized int getNumCreaturesWaiting() {
		return foundCreatures.size();
	}
	/** 
	 * @see model.ModelInterface#getShopWindow()
	 */
	@Override
	public ShopWindow getShopWindow() {
		return shopWindow;
	}
	
	
}
