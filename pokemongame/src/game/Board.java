package game;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import effects.CustomPeriodEvent;
import effects.Event;
import gameutils.GameUtils;
import loaders.ThingLoader;
import thingFramework.EventfulItem;
import thingFramework.Item;
import thingFramework.Pokemon;
import thingFramework.Thing;
import thingFramework.Thing.ThingType;
/**
 * The "Model" in the MVP model. Manages the game state in memory.
 * <br> NOTE: newSession should be called at the beggining of a new session. </br>
 * @author David O'Sullivan
 *
 */
//TODO: Add Debug mode instead of randomly commenting things lol
public class Board implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * The location of the levels of evolution
	 */
	private static final String LEVELS_OF_EVOLUTION_LOCATION = "resources/InputFiles/levelsOfEvolve.csv";
	/**
	 * Location of which pokemon evolve to what
	 */
	private static final String EVOLUTIONS_LOCATION = "resources/InputFiles/evolutions.csv";
	/**
	 * The location of the csv of all the thing to import into the game
	 */
	private static final String THING_LIST_LOCATION = "resources/InputFiles/pokemonList.csv";
	/**
	 * The location of all pregenerated "basic" events to load into the game. I.E.
	 * items that have events that can be described by methods in the ThingLoader class
	 */
	private static final String EVENT_MAP_LOCATION = "resources/InputFiles/eventMapList.csv";
	/**
	 * Location of csv containing extra attributes for things. Format as specified in thingloader
	 */
	private static final String[] EXTRA_ATTRIBUTE_LOCATIONS = {"resources/InputFiles/extraAttributes.csv"};
	private static final String PATH_TO_DESCRIPTIONS = "resources/InputFiles/descriptionList.csv";
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
	private static final double PERCENT_CHANCE_DUPLICATE_SPAWNS = 0;//TODO: Once duplicate is fixed change back to 5
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
	 * Loads all things into the game
	 */
	private static final ThingLoader thingLoader = new ThingLoader(THING_LIST_LOCATION, PATH_TO_DESCRIPTIONS, EVENT_MAP_LOCATION, EVOLUTIONS_LOCATION, LEVELS_OF_EVOLUTION_LOCATION, EXTRA_ATTRIBUTE_LOCATIONS);
	/**
	 * Set of all the pokemon in the game, gotten from thingLoader
	 */
	private static final Set<Pokemon> allPokemon = thingLoader.getPokemonSet();
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
	private static final Map<String, Integer> pokeRarity = Thing.mapFromSetToAttributeValue(allPokemon, "rarity");  
	/**
	 * The queue of found wild pokemon (pokemon generated from a lookForPokemon() call)
	 */
	private final Deque<Pokemon> foundPokemon = new LinkedList<Pokemon>();
	/**
	 * This is the currently Grabbed pokemon, it may be placed on the board and removed from foundPokemon or it may be put back
	 */
	private Pokemon grabbedPokemon = null;
	/**
	 * This is the value of the total chance rarities of every pokemon. In other words,
	 * it is the denominator for determining the percent chance that a certain pokemon
	 * will show up (that is the probability will be: getRelativeChanceRarity(pokemon.rarity)/RUNNING_TOTAL)
	 */
	private static final long RUNNING_TOTAL;
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
	 * Manages the gametime and session time of the current board. Note that newSession should be called to 
	 * update this when a new session is started
	 */
	private SessionTimeManager stm = new SessionTimeManager();
	private volatile int gold = 0;
	private volatile int popularity = 0;
	/**
	 * A map from board location to a list of events that that thing at that location has.
	 * Note that the checkForPokemon event is mapped to -1
	 */
	private Map<Integer, List<Event>> events = new ConcurrentHashMap<Integer, List<Event>>();
	/**
	 * Used to represent the state of the board. Contains all unique things on the board and the number that are present. 
	 */
	private Map<Thing, Integer> elementsToQuantity = new HashMap<Thing, Integer>();
	private Map<Thing, List<Event>> events2 = new ConcurrentHashMap<Thing, List<Event>>();
	/**
	 * All pokemon currently on the board
	 */
	private int numPokemon = 0;
	/**
	 * The names of all pokemon on the board (no duplicates) used so that duplicate pokemon are
	 * signifigantly less likely to show up
	 */
	private Set<String> pokemonAsASet = new HashSet<String>();
	/**
	 * The event that checks for pokemon on a certain period based on popularity
	 * This is considered a "default" event and hence is mapped to a negative value of -1.
	 * Since it is static, it will be recreated on serialization, meaning it will have a new 
	 * time created, and the pokemon will not be spawning while it is offline.
	 * The transient keyword, though unnecessary, is included to remind of this feature.
	 * Also, this is probably redundant because keeptrackwhileoff for events is by default set to false.
	 * But regardless, making it static makes sense.
	 */
	private transient static final Event checkForPokemon = checkForPokemonEvent(); 
	private transient ExecutorService eventExecutorService = Executors.newFixedThreadPool(5);
	/**
	 * Blank item to store the checkForPokemon event
	 */
	private transient static final Thing checkForPokemonThing = Item.generateBlankItem();
	public Board() {
		events2.put(checkForPokemonThing, Arrays.asList(checkForPokemon));
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
		stm.updateGameTime();
		for (int k : events.keySet()) {
			events.get(k).forEach((Event x) ->
			{
				if (!x.onPlaceExecuted()) {
					eventExecutorService.execute(x.executeOnPlace(this));
				}
				eventExecutorService.execute(x.executePeriod(this));
			});
		}
		

	}
	private void executeEvents() {
		events2.forEach((k, v) -> v.forEach((event) ->
		{
			if (!event.onPlaceExecuted()) {
				eventExecutorService.execute(event.executeOnPlace(this));
			}
			eventExecutorService.execute(event.executePeriod(this));
		}));
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
		return .1;
		//TODO: Uncomment this
		//return Math.max(MIN_POKEPERIOD, A-Math.pow(getPopularity()/B, C));
	}
	
	/**
	 * helper method for lookForPokemon, for purposes of regenning if a duplicate pokemon is generated and
	 * testPercentChance(PERCENT_CHANCE_DUPLICATE_SPAWNS) is false
	 * @param automaticSpawn if true will automatically generate a pokemon regardless of percent chance
	 */
	private void lookForPokemon(boolean automaticSpawn) {
		//first check if a pokemon is even found
		if (automaticSpawn || testPercentChance(getPercentChancePokemonFound())) {
			long randomNum = ThreadLocalRandom.current().nextLong(0, RUNNING_TOTAL);
			//note that chance != rarity, they are inversely proportional
			String name = pokemonCumulativeChance.higherEntry(randomNum).getValue();
			if (testPercentChance(getPercentChancePopularityModifies())) {
				int modifier = getPopularityModifier();
				if (modifier !=0) {
					int rarity = pokeRarity.get(name);
					//the set of all keys strictly greater than the rarity 
					//note that iterator will still work if tailmap is empty
					Set<Integer> headMap = pokemonRaritiesInOrder.tailMap(rarity, false).keySet();
					int j = 1;
					for (Integer rare: headMap) {
						if (j==modifier || j==headMap.size()) {
							List<String> pokemon = pokemonRaritiesInOrder.get(rare);
							name = pokemon.get(ThreadLocalRandom.current().nextInt(pokemon.size()));
							break;
						}
						j++;
					}
				}


			}
			if (pokemonAsASet.contains(name)) {
				if (!testPercentChance(PERCENT_CHANCE_DUPLICATE_SPAWNS))
					lookForPokemon(true);
				else
					foundPokemon.add(thingLoader.getPokemon(name));
				//TODO: fix the absolute mess that is duplicate handling
			}
			else
				foundPokemon.add(thingLoader.getPokemon(name));
					
		}
	}
	/**
	 * To be called by an event on calculated period. Will check if a pokemon is even found using 
	 * getPercentChancePokemonFound(), and if it is, will find a pokemon, with lower rarity pokemons being
	 * more likely
	 */
	private void lookForPokemon() {
		//TODO: change this back to false
		lookForPokemon(true); 
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
	 * @throws Error "Sets out of sync" if the location is null or failed to remove item
	 */
	public synchronized void removeThing(Thing thing) {
		if (thing==null) {
			throw new RuntimeException("Attempted to Remove a Pokemon Not Presently on Board");
		}
		removeElementFromThingMap(thing);
		thing.onRemove(this);
		
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
	public synchronized long getTotalGameTime() {
		return stm.getTotalGameTime();
	}
	public synchronized long getSessionGameTime() {
		return stm.getSessionGameTime();
	}
	/**
	 * Start a new session using a board
	 * @param totalGameTime the old totalGameTime
	 */
	public void newSession(long totalGameTime) {
		this.stm = new SessionTimeManager(totalGameTime);
		
	}
	private static boolean isEventfulItem(Thing t) {
		return t.getThingTypes().contains(ThingType.EVENTFULITEM);
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
	public synchronized void addPopularity(int popularity) {
		setPopularity(Math.max(MINPOP, getPopularity()+popularity));
	}
	public synchronized void subtractPopularity(int popularity) {
		addPopularity(-popularity);
	}
	public void notifyPokemonAdded(Pokemon p) {
		numPokemon++;
		pokemonAsASet.add(p.getName());
	}
	public void notifyPokemonRemoved(Pokemon p) {
		//TODO: fix the absolute mess that is duplicate handling
		numPokemon--;
	    pokemonAsASet.remove(p.getName());
	}
	/**
	 * Adds the element to the map from thing to # present, updating accordingly, and adding events if necessary. 
	 * @param thing the Thing to ADd
	 */
	private void addElementToThingMap(Thing thing) {
		elementsToQuantity.merge(thing, 1, (old,v) -> old+1 ); //set value to 1 if not present, increment if is
		addAssociatedEvents(thing);
	}
	/**
	 * Removes one quantity of this thing from the map (i.e. decrements the value and removes if newVal == 0)
	 * Will also call all associated events onRemove, and will permanently remove those events if newVal == 0
	 * @param thing the Thing to add
	 */
	private void removeElementFromThingMap(Thing thing) {
		elementsToQuantity.compute(thing, (k, v) -> (v-1 == 0) ? null : v-1); //if removing the last instance, remove from map, else subtract
		removeAssociatedEvents(thing);  //execute onRemove and permanently remove if this is the last instance
	}
	/**
	 * If the thing hasn't been added to the board yet, update the event set
	 * @param thing The thing to get the events for
	 */
	private void addAssociatedEvents(Thing thing) {
		if (isEventfulItem(thing)) {
			events2.merge(thing, ((EventfulItem) thing).getEvents(), GameUtils::union);
		}
		events2.merge(thing, BoardAttributeManager.getEvents(thing.getBoardAttributes()), GameUtils::union);
	}
	/**
	 * calls executeOnRemove for all associated events. Permanetly removes events from event set if none left
	 * @param thing the Thing to call the executeOnRemove events on
	 * @param permanentlyRemove if true will also remove those events from the set
	 */
	private void removeAssociatedEvents(Thing thing) {
		events2.compute(thing, (k, v) -> {
			final List<Event> removedEvents = GameUtils.removeOneInstanceOfEachElement(v); //get one set of events
			removedEvents.forEach(event -> eventExecutorService.execute(event.executeOnRemove(Board.this)));
			return v.isEmpty() ? null : v;
		});
		
	}
	/**
	 * @return true if there is a pokemon in the foundPokemon Queue (that is, a wild pokemon spawned)
	 */
	public boolean wildPokemonPresent() {
		return !foundPokemon.isEmpty();
	}
	public Pokemon getPokemon(String name) {
		return thingLoader.getPokemon(name);
	}
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Entry<Thing, Integer> entry : elementsToQuantity.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++)
			s.append("\n" + entry.getKey().toString() + "\n");
		}
		return s.toString();
		//return s.append("Gold: " + getGold() + "\n" + "POP: " + getPopularity()).toString();
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
	
	
	
}
