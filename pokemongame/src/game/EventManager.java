package game;

import static gameutils.Constants.DEBUG;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import effects.Event;
import effects.Eventful;
/**
 * Manages the events of all things on the board
 * @author David O'Sullivan
 *
 */
public class EventManager implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Set<Eventful> events = new HashSet<Eventful>();
	private final Queue<Runnable> removalEvents = new ConcurrentLinkedQueue<Runnable>();
	private final Map<Eventful, List<Event>> markedForRemovalEvents = new HashMap<Eventful, List<Event>>();
	private final Board board;
	public EventManager(final Board board) {
		this.board = board;
	}
	public synchronized void addThing(final Eventful thing) {
		if (events.contains(thing))
			throw new RuntimeException("EventManager already contains thing: " + thing);
		events.add(thing);
	}
	public synchronized void removeThing(final Eventful thing) {
		if (!events.contains(thing))
			throw new RuntimeException("Attempted to Remove Events From Thing that Doesn't exist!");
		for (final Event e: thing.getEvents())
			removalEvents.add(e.executeOnRemove(board));
		events.remove(thing);
	}
	public synchronized void runEvents() {
		events.forEach(eventful -> eventful.getEvents().forEach((event) ->
		{
			if (!event.onPlaceExecuted()) {
				if (DEBUG) {
					System.out.println("running event: " + eventful.getName());
					event.addToName("EVENT FROM: " + eventful.getName());
				}
				event.executeOnPlace(board).run();
			}
			event.executePeriod(board).run();
			event.executeOnTick(board).run();
			if (event.wasMarkedForRemoval()) { // if the event was removed by the Thing itself
				final List<Event> removalList = new ArrayList<Event>();
				removalList.add(event);
				markedForRemovalEvents.merge(eventful, removalList, (o, v) -> {o.addAll(v); return o;});
				removalEvents.add(event.executeOnRemove(board));
			}
			if (event.shouldBeReset()) {
				event.executeOnReset(board).run();
			}
		})); 
		removalEvents.forEach((runnable) -> runnable.run());
		removalEvents.clear();
		markedForRemovalEvents.forEach((eventful, list) -> eventful.confirmEventRemovals(list));
		markedForRemovalEvents.clear();
	}

	

}
