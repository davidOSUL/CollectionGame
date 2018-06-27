package game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import effects.Event;
import gameutils.GameUtils;
import thingFramework.EventfulItem;
import thingFramework.Thing;
import thingFramework.Thing.ThingType;
public class EventManager {
	private Map<Thing, List<Event>> events = new HashMap<Thing, List<Event>>();
	private Queue<Runnable> removalEvents = new ConcurrentLinkedQueue<Runnable>();
	private Board board;
	public EventManager(Board board) {
		this.board = board;
	}
	public synchronized void addThing(Thing thing) {
		if (events.containsKey(thing))
			throw new RuntimeException("EventManager already contains thing: " + thing);
		if (isEventfulItem(thing)) {
			events.put(thing, ((EventfulItem) thing).getEvents());
		}
		events.merge(thing, BoardAttributeManager.getEvents(thing.getBoardAttributes()), GameUtils::union);
	}
	public synchronized void removeThing(Thing thing) {
		if (!events.containsKey(thing))
			throw new RuntimeException("Attempted to Remove Events From Thing that Doesn't exist!");
		for (Event e: events.get(thing))
			removalEvents.add(e.executeOnRemove(board));
		events.remove(thing);
	}
	public synchronized void runEvents() {
		events.forEach((k, v) -> v.forEach((event) ->
		{
			if (!event.onPlaceExecuted()) {
				event.executeOnPlace(board).run();
			}
			event.executePeriod(board).run();
		})); 
		removalEvents.forEach((runnable) -> runnable.run());
		removalEvents.clear();
	}
	private static boolean isEventfulItem(Thing t) {
		return t.getThingTypes().contains(ThingType.EVENTFULITEM);
	}
	

}
