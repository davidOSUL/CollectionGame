package game;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import effects.Event;
import thingFramework.*;
import thingFramework.Thing.ThingType;
@SuppressWarnings("rawtypes")
public class Board implements Serializable {
	/**
	 * 
	 */
	private static final int MINPOP = 0;
	private static final int MINGOLD = 0;
	private static final long serialVersionUID = 1L;
	private SessionTimeManager stm = new SessionTimeManager();
	private volatile int gold = 0;
	private volatile int popularity = 0;
	private Map<Integer, Thing> locationMap = new HashMap<Integer, Thing>();
	private Map<Integer, Set<Attribute>> boardAttributes = new HashMap<Integer, Set<Attribute>>();
	private List<Pokemon> pokemon = new ArrayList<Pokemon>();
	private List<Item> items = new ArrayList<Item>();
	private Map<Integer, List<Event>> events = new HashMap<Integer, List<Event>>();
	public Board() {
		
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
				Thread t = new Thread(x.executePeriod(getTotalGameTime(), this));
				t.start();
			});
		}
		lookForPokemon();
	}
	private void lookForPokemon() {
		
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
		events.remove(location); //will remove events if any exist at that location
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
