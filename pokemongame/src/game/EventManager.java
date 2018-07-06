package game;

import static gameutils.Constants.DEBUG;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import effects.Event;
import effects.Eventful;
import thingFramework.Thing;
public class EventManager implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Set<Eventful> events = new HashSet<Eventful>();
	private Queue<Runnable> removalEvents = new ConcurrentLinkedQueue<Runnable>();
	private Board board;
	public EventManager(Board board) {
		this.board = board;
	}
	public synchronized void addThing(Eventful thing) {
		if (events.contains(thing))
			throw new RuntimeException("EventManager already contains thing: " + thing);
		events.add(thing);
	}
	public synchronized void removeThing(Eventful thing) {
		if (!events.contains(thing))
			throw new RuntimeException("Attempted to Remove Events From Thing that Doesn't exist!");
		for (Event e: thing.getEvents())
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
		})); 
		removalEvents.forEach((runnable) -> runnable.run());
		removalEvents.clear();
	}

	

}
