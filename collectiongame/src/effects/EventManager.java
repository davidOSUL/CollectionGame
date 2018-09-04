package effects;

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

import game.Board;
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
	/**
	 * Creates a new EventManager for the provided board
	 * @param board the board that this EventManager is managing events for
	 */
	public EventManager(final Board board) {
		this.board = board;
	}
	/**
	 * Should be called whenever an Eventful is added to the board
	 * @param eventful the Eventful that was added
	 */
	public synchronized void notifyEventfulAdded(final Eventful eventful) {
		if (events.contains(eventful))
			throw new RuntimeException("EventManager already contains Eventful: " + eventful);
		events.add(eventful);
	}
	/**
	 * Should be called whenever an Eventful is removed from the board
	 * @param eventful  the Eventful that was removed
	 */
	public synchronized void notifyEventfulRemoved(final Eventful eventful) {
		if (!events.contains(eventful))
			throw new RuntimeException("Attempted to Remove Events From Eventful that Doesn't exist!");
		for (final Event e: eventful.getEvents())
			removalEvents.add(e.executeOnRemove(board));
		events.remove(eventful);
	}
	/**
	 * Should be called every game tick to call appropriate event consumers
	 */
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
