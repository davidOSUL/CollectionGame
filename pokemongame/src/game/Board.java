package game;
import static gameutils.Constants.DEBUG;
import static gameutils.Constants.RAPID_SPAWN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import effects.CustomPeriodEvent;
import effects.Event;
import effects.Eventful;
import gameutils.GameUtils;
import loaders.ThingLoader;
import loaders.shopLoader.ShopItem;
import thingFramework.Item;
import thingFramework.Pokemon;
import thingFramework.Thing;
/**
 * The "Model" in the MVP model. Manages the game state in memory.
 * <br> NOTE: newSession should be called at the beggining of a new session. </br>
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
	private static final int MINGOLD = 0;
	/**
	 * The chance that a pokemon with a name of a pokemon already on the board will spawn
	 */
	private static final double PERCENT_CHANCE_DUPLICATE_SPAWNS = RAPID_SPAWN ? 100 : 5;
	/**
	 * The minimum period in minutes at which new pokemon are checked for
	 */
	private static final double MIN_POKEPERIOD = 1;
	/**
	 * The minimum percent chance that when checking for pokemon, one is found
	 */
	private static final double MIN_PERCENT_CHANCE_POKEMON_FOUND = 20;
	/**
	 * The maximum percent chance that when checking for pokemon, one is found
	 */
	private static final double MAX_PERCENT_CHANCE_POKEMON_FOUND = 90;

	/**
	 * The minimum percent chance that the rarity of a found pokemon will increase from a popularity boost
	 */
	private static final double MIN_PERCENT_CHANCE_POPULARITY_BOOSTS = 0;

	/**
	 * The maximum delta in rarity value from the calculated rarity. (for example if the rarities are:
	 * 1,2,3,4,5), and the calculated rarity is 2, with a boost of 2, you will get a pokemon with rarity 4
	 */
	private static final double MAX_POPULARITY_BOOST = 10;
	/**
	 * The maximum percent chance that the rarity of a found pokemon will increase from a popularity boost
	 */
	private static final double MAX_PERCENT_CHANCE_POPULARITY_BOOSTS = 90;
	/**
	 * A map from the sum of the chance of a pokemon being found (out of RUNNING_TOTAL) and all chances
	 * of pokemon loaded in before it, to the name of that pokemon. This will be in order, sorted by the sum.
	 */
	private static final TreeMap<Long, String> pokemonCumulativeChance = new TreeMap<Long, String>();
	/**
	 * A map from the RARITY (NOT CHANCE) to a list of all pokemon with that rarity, in order of rarity
	 */
	private static final TreeMap<Integer, List<String>> pokemonRaritiesInOrder = new TreeMap<Integer, List<String>>();
	/**
	 * Map from pokemon names to their RARITY (NOT CHANCE) value
	 */
	private static final Map<String, Integer> pokeRarity = Thing.mapFromSetToAttributeValue(ThingLoader.sharedInstance().getPokemonSet(), "rarity");  
	/**
	 * This is the value of the total chance rarities of every pokemon. In other words,
	 * it is the denominator for determining the percent chance that a certain pokemon
	 * will show up (that is the probability will be: getRelativeChanceRarity(pokemon.rarity)/RUNNING_TOTAL)
	 */
	private static final long RUNNING_TOTAL;
	private static final int MAX_ATTEMPTS = 50;
	private double sellBackPercent = .5;
	static {
		long rt = 0; //running total

		for (Map.Entry<String, Integer> entry: pokeRarity.entrySet()) {
			int rarity = entry.getValue();
			String name = entry.getKey();
			int percentChance = getRelativeChanceRarity(rarity);
			rt += percentChance;
			pokemonCumulativeChance.put(rt, name);
			if (!pokemonRaritiesInOrder.containsKey(rarity)) {
				List<String> list = new ArrayList<String>();
				list.add(name);
				pokemonRaritiesInOrder.put(rarity, list);
			}
			else {
				pokemonRaritiesInOrder.get(rarity).add(name);
			}
		}
		RUNNING_TOTAL = rt;
	}
	/**
	 * The event that checks for pokemon on a certain period based on popularity
	 * Since it is static, it will be recreated on serialization, meaning it will have a new 
	 * time created, and the pokemon will not be spawning while it is offline.
	 * The transient keyword, though unnecessary, is included to remind of this feature.
	 * Also, this is probably redundant because keeptrackwhileoff for events is by default set to false.
	 * But regardless, making it static makes sense.
	 */
	private transient static final Event checkForPokemon = checkForPokemonEvent(); 
	/**
	 * Blank item to store the checkForPokemon event
	 */
	private transient static final Item checkForPokemonThing = Item.generateBlankItemWithEvents(checkForPokemon);
	
	/*
	 * Transient instance variables:
	 * 
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
	/*
	 * Non-transient instance variables
	 * 
	 */
	
	/**
	 * The queue of found wild pokemon (pokemon generated from a lookForPokemon() call)
	 */
	private final Deque<Pokemon> foundPokemon = new LinkedList<Pokemon>();
	
	private volatile int gold = 0;
	private volatile int popularity = 0;
	/**
	 * Used to represent the state of the board. Contains all things on board.
	 */
	private final Set<Thing> thingsOnBoard = new LinkedHashSet<Thing>();
	/**
	 * The number of pokemon currently on the board
	 */
	private volatile int numPokemon = 0;
	/**
	 * The pokemon on the board or in the queue used so that duplicate pokemon are
	 * signifigantly less likely to show up. Map from the name of the pokemon to the # present
	 */
	private volatile Map<String, Integer> uniquePokemonLookup = new HashMap<String, Integer>();
	private final Shop shop;
	private final EventManager events; 
	/**
	 * Manages the gametime and session time of the current board. Note that newSession should be called to 
	 * update this when a new session is started.
	 */
	private final SessionTimeManager stm;
	//TODO: Put all possible things with their associated events in a manager of its own, should be able to grab events with quantity > 0 
	
	
	
	public Board() {
		shop = new Shop();
		stm = new SessionTimeManager();
		events = new EventManager(this);
		events.addThing(checkForPokemonThing);
	}
	public Board(int gold, int popularity) {
		this();
		this.setGold(gold);
		this.setPopularity(popularity);
	}

	/**
	 * To be called on every game tick. Updates time and events
	 */
	public void update() {
		if (SwingUtilities.isEventDispatchThread())
			System.out.println("WHAT");
		stm.updateGameTime();
		executeEvents();


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
		double A=  4; //max value+1
		double B = 60; //"length" of near-constant values
		double C = 1.3; //steepness of drop
		return RAPID_SPAWN ? 1.666e-5 : Math.max(MIN_POKEPERIOD, A-Math.pow(getPopularity()/B, C));
	}
	
	/**
	 * helper method for lookForPokemon, for purposes of regenning if a duplicate pokemon is generated and
	 * testPercentChance(PERCENT_CHANCE_DUPLICATE_SPAWNS) is false
	 * @param automaticSpawn if true will automatically generate a pokemon regardless of percent chance
	 */
	private void lookForPokemon(boolean automaticSpawn) {
		//first check if a pokemon is even found
		int attempts = 0;
		if (!automaticSpawn && !testPercentChance(getPercentChancePokemonFound()))
			return;
		String name = null;
		do {
			attempts++;
			name = findNextPokemon();
			if (testPercentChance(getPercentChancePopularityModifies())) {
				int modifier = getPopularityModifier();
				if (modifier !=0) {
					int rarity = pokeRarity.get(name);
					//the set of all keys strictly greater than the rarity 
					//note that iterator will still work if tailmap is empty
					Set<Integer> headMap = pokemonRaritiesInOrder.tailMap(rarity, false).keySet();
					int j = 1;
					for (Integer rare: headMap) {
						if (j==modifier || j==headMap.size()) { //move up from original rarity by modifier ranks in rarity
							List<String> pokemon = pokemonRaritiesInOrder.get(rare);
							name = pokemon.get(ThreadLocalRandom.current().nextInt(pokemon.size()));
							break;
						}
						j++;
					}
				}


			}
		} while(name != null && !isUniquePokemon(name) && !testPercentChance(PERCENT_CHANCE_DUPLICATE_SPAWNS) && attempts < MAX_ATTEMPTS);
		if (name != null && (PERCENT_CHANCE_DUPLICATE_SPAWNS != 0 || isUniquePokemon(name))) {
			addToFoundPokemon(name);
		}

	}
	/**
	 * @param Using thing loader creates a new instance of a pokemon with the given name.
	 * Adds the provided pokemon to the foundPokemon queue and updates the set of pokemon names in addToUniquePokemonLookup
	 */
	private void addToFoundPokemon(String name) {
		Pokemon p = ThingLoader.sharedInstance().generateNewPokemon(name);
		foundPokemon.addLast(p);
		addToUniquePokemonLookup(p);
	}
	private void addToUniquePokemonLookup(Pokemon p) {
		uniquePokemonLookup.merge(p.getName(), 1, (old, v) -> old+1);
	}
	private void removeFromUniquePokemonLookup(Pokemon p) {
		uniquePokemonLookup.compute(p.getName(), (k, v) -> (v-1 == 0) ? null : v-1);
	}
	private boolean isUniquePokemon(String name) {
		return !uniquePokemonLookup.containsKey(name);
	}
	/**
     * Called by lookForPokemon(), will find the next pokemon taking into account rarity
	 * @return
	 */
	private String findNextPokemon() {
		long randomNum = ThreadLocalRandom.current().nextLong(0, RUNNING_TOTAL);
		//note that chance != rarity, they are inversely proportional
		Entry<Long, String> entry = pokemonCumulativeChance.higherEntry(randomNum);
		if (entry == null)
			entry = pokemonCumulativeChance.ceilingEntry(randomNum);
		if (entry == null)
			entry = pokemonCumulativeChance.floorEntry(randomNum);
		if (entry == null)
			return null;
		return entry.getValue();
	}
	/**
	 * To be called by an event on calculated period. Will check if a pokemon is even found using 
	 * getPercentChancePokemonFound(), and if it is, will find a pokemon, with lower rarity pokemons being
	 * more likely
	 */
	private void lookForPokemon() {
		lookForPokemon(RAPID_SPAWN ? true : false); 
	}
	/**
	 * Looks for pokemon, but gurantees that one is found (as supposed to having a percent chance that none are found)
	 */
	private void lookForPokemonGuranteedFind() {
		lookForPokemon(true);
	}
	/**
	 * @return the percent chance that the randomNum generated by lookForPokemon will be modified 
	 *by a value. This value increases as popularity increases. 
	 *In particular this will return a value of the form:
	 *<br> Aln(pop^B+C)+Dpop^E
	 */
	private double getPercentChancePopularityModifies() {
		final double A = 5;
		final double B = 1.3;
		final double C = 1;
		final double D = 10;
		final double E = .25;
		return Math.max(MIN_PERCENT_CHANCE_POPULARITY_BOOSTS, Math.min(MAX_PERCENT_CHANCE_POPULARITY_BOOSTS, A*Math.log(Math.pow(getPopularity(), B)+C)+D*Math.pow(getPopularity(), E)));
	}
	/**
	 * @return The amount by which we will move up in rarity ranking for a pokemon
	 * Will be in the form of logistic growth: MAX_MODIFIER/1+Be^-r*pop + C
	 * C is implicitly minimum popularity boost
	 */
	private int getPopularityModifier() {
		final double B = 1000;
		final double R= .15;
		final double C = 1;
		return (int) Math.floor((MAX_POPULARITY_BOOST/(1+B*Math.pow(Math.E, -R*getPopularity())))+C);
	}
	/**
	 * @return Percent chance that a pokemon is found. 
	 * Will be value of the form pop*A+Gold/B+C/D*pokeMapSize+E, D!=0
	 * range modified to [MIN_PERCENT_CHANCE, MAX_PERCENT_CHANCE]
	 */
	private double getPercentChancePokemonFound() {
		double A = 5;
		double B = 50;
		double C = 100;
		double D =3;
		double E = 1;
		if (numPokemon <= 2)
			return 100;
		double answer = Math.max(MIN_PERCENT_CHANCE_POKEMON_FOUND, Math.min(MAX_PERCENT_CHANCE_POKEMON_FOUND, (getPopularity()*A)+(getGold()/B)+(C/(D*numPokemon+E))));
		return answer;
	}
	/**
	 * @return Percent chance of a rarity out of 100 w.r.t to the other pokes
	 */
	private static int getRelativeChanceRarity(int rarity) {
		return 100-rarity;
	}
	/**
	 * @param percentChance the percent chance of an event occuring
	 * @return whether or not that event occurs
	 */
	private static boolean testPercentChance(double percentChance) {
		return GameUtils.testPercentChance(percentChance);

	}
	/**
	 * @param thing the thing to add
	 */
	public synchronized void addThing(Thing thing) {
		addElementToThingMap(thing);
		thing.onPlace(this);
	}
	/**
	 * @param thing The thing to add
	 */
	public synchronized void removeThing(Thing thing) {
		if (thing==null) {
			throw new RuntimeException("Attempted to Remove null");
		}
		removeElementFromThingMap(thing);
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
	public synchronized void setGold(int gold) {
		this.gold = gold;
	}
	public synchronized int getPopularity() {
		return popularity;
	}
	public synchronized void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	public synchronized void addGold(int gold) {
		setGold(Math.max(MINGOLD, getGold()+gold));
	}
	public synchronized void subtractGold(int gold) {
		addGold(-gold);
	}
	public synchronized void addPopularity(int popularity) {
		setPopularity(Math.max(MINPOP, getPopularity()+popularity));
	}
	public synchronized void subtractPopularity(int popularity) {
		addPopularity(-popularity);
	}
	public synchronized void notifyPokemonAdded(Pokemon p) {
		numPokemon++;
		addToUniquePokemonLookup(p);
	}
	public synchronized void notifyPokemonRemoved(Pokemon p) {
		numPokemon--;
		removeFromUniquePokemonLookup(p);
	}
	/**
	 * Adds the element to the map from thing to # present, updating accordingly, and adding events if necessary. 
	 * @param thing the Thing to ADd
	 */
	private void addElementToThingMap(Thing thing) {
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
	private void removeElementFromThingMap(Thing thing) {
		thingsOnBoard.remove(thing);
		removeAssociatedEvents(thing);  //execute onRemove and permanently remove if this is the last instance
	}
	/**
	 * If the thing hasn't been added to the board yet, update the event set
	 * @param eventful The eventful to get the events for
	 */
	private void addAssociatedEvents(Eventful eventful) {
		events.addThing(eventful);
	}
	/**
	 * calls executeOnRemove for all associated events. Permanetly removes events from event set if none left
	 * @param eventful the eventful to call the executeOnRemove events on, and remove future events from running
	 * @param permanentlyRemove if true will also remove those events from the set
	 */
	private void removeAssociatedEvents(Eventful eventful) {
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
	public Pokemon getPokemon(String name) {
		if (DEBUG)
			return ThingLoader.sharedInstance().generateNewPokemon(name);
		else
			throw new RuntimeException("Method should not be called when not debugging");
	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Thing entry : thingsOnBoard) {
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
		Pokemon grabbed = foundPokemon.poll();
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
		Pokemon p = grabbedPokemon;
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
	public boolean canPurchase(ShopItem item) {
		return (item.getCost() <= getGold());
	}
	/**
	 * If have enough money, start the purchase attempt
	 * @param item the ShopItem in the shop
	 * @return a Thing (for display purposes) that corresponds to the item
	 */
	public Thing startPurchase(ShopItem item) {
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
	public boolean canAddBackToShopStock(ShopItem item) {
		return shop.isValidShopItem(item.getThingName());
	}
	/**
	 * Sell back the specified item, adding it back to the shop and adding that much gold to the user
	 * @param item the item to sell back
	 */
	public void sellBack(ShopItem item) {
		addGold(getSellBackValue(item));
		if (canAddBackToShopStock(item))
			shop.addToShopStock(item.getThingName());
	}
	public int getSellBackValue(ShopItem item) {
		return Math.max(1 , (int)(item.getCost()*sellBackPercent));
	}
	
	


}
