package game;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import effects.CustomPeriodEvent;
import effects.Event;
import thingFramework.*;
import thingFramework.Thing.ThingType;
public class Board implements Serializable {
	/**
	 * 
	 */
	//private static final String ITEM_LIST_LOCATION = "resources/InputFiles/pokemonList.csv";
	//private static final String EVENT_MAP_LOCATION = "resources/InputFiles/eventMapList.csv";
	private static final int MINPOP = 0;
	private static final int MINGOLD = 0;
	private static final long serialVersionUID = 1L;
	private static final double MIN_POKEPERIOD = .5;
	private static final double MIN_PERCENT_CHANCE = 20;
	private static final double MAX_PERCENT_CHANGE = 90;
	//private static final ThingLoader thingLoader = new ThingLoader(ITEM_LIST_LOCATION, EVENT_MAP_LOCATION);
	//private static final Set<Pokemon> allPokemon = thingLoader.getPokemonSet();
	private SessionTimeManager stm = new SessionTimeManager();
	private volatile int gold = 0;
	private volatile int popularity = 0;
	private Map<Integer, Thing> locationMap = new HashMap<Integer, Thing>();
	private Map<Integer, Set<Attribute>> boardAttributes = new HashMap<Integer, Set<Attribute>>();
	private List<Pokemon> pokemon = new ArrayList<Pokemon>();
	private List<Item> items = new ArrayList<Item>();
	private Map<Integer, List<Event>> events = new HashMap<Integer, List<Event>>();
	private static  Event checkForPokemon = checkForPokemonEvent(); //idea here is that everytime the game is opened, this will be reset to a new gameTime
	public Board() {
		events.put(-1, Arrays.asList(checkForPokemon));
	}
	public Board(int gold, int popularity) {
		this.setGold(gold);
		this.setPopularity(popularity);
	}
	
	public void update() {
		stm.updateGameTime();
		for (int k : events.keySet()) {
			events.get(k).forEach((Event x) ->
			{
				if (!x.onPlaceExecuted()) {
					Thread w = new Thread(x.executeOnPlace(this));
					w.start();
				}
				Thread t = new Thread(x.executePeriod(this));
				t.start();
			});
		}
		lookForPokemon();
	}
	private static Event checkForPokemonEvent() {
		return new CustomPeriodEvent(board -> {
			board.lookForPokemon();
		}, board -> {
			return board.getLookPokemonPeriod();
		});
	}
	/**
	 * @return value of the form A-(pop/B)^C, minimum of MIN_POKEPERIOD
	 */
	private double getLookPokemonPeriod() {
		double A=  4; //max value+1
		double B = 25; //"length" of near-constant values
		double C = 7; //steepness of drop
		return Math.min(MIN_POKEPERIOD, A-Math.pow(getPopularity()/B, C));
	}
	private void lookForPokemon() {
		//first check if a pokemon is even found
		if (testPercentChance(getPercentChancePokemonFound())) {
			
		}
	}
	/**
	 * @return Percent chance that a pokemon is found. 
	 * Will be value of the form pop*A+Gold/B+C/pokeMapSize+D, D!=0
	 * range modified to [MIN_PERCENT_CHANCE, MAX_PERCENT_CHANCE]
	 */
	private double getPercentChancePokemonFound() {
		double A = 5;
		double B = 50;
		double C = 50;
		double D =.1;
		return Math.max(MIN_PERCENT_CHANCE, Math.min(MAX_PERCENT_CHANGE, (getPopularity()*A)+(getGold()/B)+(C/(pokemon.size()+D))));
	}
	/**
	 * @return Percent chance of a rarity 
	 */
	private double getPercentChanceRarity() {
		
	}
	/**
	 * @param percentChance the percent chance of an event occuring
	 * @return whether or not that event occurs
	 */
	private static boolean testPercentChance(double percentChance) {
		
				double randomNum = ThreadLocalRandom.current().nextDouble(1, 100); //num between 1, 100
				if (randomNum > (100-percentChance))
					return true;
			
			return false;
		
	}
	public synchronized void addThing(int location, Thing thing) {
		locationMap.put(location, thing);
		boardAttributes.put(location, thing.getBoardAttributes());
		if (isPokemon(thing))
			pokemon.add((Pokemon) thing);
		if (isItem(thing))
			items.add((Item) thing);
		if (isEventfulItem(thing)) {
			events.put(location, ((EventfulItem) thing).getEvents());
		}
		if (events.containsKey(location)) {
			events.put(location, union(BoardAttributeManager.getEvents(thing.getBoardAttributes()), events.get(location)));
		}
		else {
			events.put(location, BoardAttributeManager.getEvents(thing.getBoardAttributes()));
		}
		
	}
	public synchronized void removeThing(int location) {
		boolean allGood = false;
		Thing t = locationMap.get(location);
		locationMap.remove(location);
		boardAttributes.remove(location);
		List<Event> removedEvents = events.remove(location); //will remove events if any exist at that location
		for (Event e: removedEvents) {
			Thread w = new Thread(e.executeOnRemove(this));
			w.start();
		}
		if (isPokemon(t))
			allGood = pokemon.remove(t);
		if (isItem(t))
			allGood = allGood && items.remove(t);
		if (!allGood || t==null) {
			throw new Error("SETS OUT OF SYNC");
		}
	}
	public long getTotalGameTime() {
		return stm.getTotalGameTime();
	}
	public long getSessionGameTime() {
		return stm.getSessionGameTime();
	}
	public void newSession(long totalGameTime) {
		this.stm = new SessionTimeManager(totalGameTime);
		
	}
	private static boolean isPokemon(Thing t) {
		return t.getThingTypes().contains(ThingType.POKEMON);
	}
	private static boolean isItem(Thing t) {
		return t.getThingTypes().contains(ThingType.ITEM);
	}
	private static boolean isEventfulItem(Thing t) {
		return t.getThingTypes().contains(ThingType.EVENTFULITEM);
	}
	public int getGold() {
		return gold;
	}
	public synchronized void setGold(int gold) {
		this.gold = gold;
	}
	public  int getPopularity() {
		return popularity;
	}
	public synchronized void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	public synchronized void addGold(int gold) {
		setGold(Math.min(MINGOLD, getGold()+gold));
	}
	public synchronized void addPopularity(int popularity) {
		setPopularity(Math.min(MINPOP, getPopularity()+popularity));
	}
	public synchronized void subtractPopularity(int popularity) {
		addPopularity(-popularity);
	}
	private void manageBoardAttributes() {
		for (Map.Entry<Integer, Set<Attribute>> entry: boardAttributes.entrySet()) {
			Set<Attribute> set = entry.getValue();
			
		}
	}
	public static <E> List<E> union(final List<? extends E> list1, final List<? extends E> list2) {
		final ArrayList<E> result = new ArrayList<E>(list1);
		result.addAll(list2);
		return result;
	}
	
}
