package effects;

import java.util.Collection;
import java.util.List;

/**
 * Anything that is said to have events associated with it, should implement this interface
 * @author David O'Sullivan
 *
 */
public interface Eventful {
 /**
 * After events are marked for removal, the owner of those events should remove them, and then call this method with 
 * the list events it removed.
 * This implementor of this method will finalize this removal by removing those events from it's own list of events
 * @param events the events whose removal should be finalized
 */
public void confirmEventRemovals(final Collection<Event> events);
 /**
 * Returns the list of events that this Eventful possess 
 * @return the list of events that this Eventful possess 
 */
public List<Event> getEvents();
 /**
 * Adds all of the provided events to this Eventful's event list
 * @param events the events to add
 */
public default void addToEventList(final Collection<Event> events) {
	 if (events == null)
		return;
	 events.forEach(e -> addToEventList(e));
 }
 /**
 * Adds the provided event to this Eventful's event list
 * @param event
 */
public void addToEventList(final Event event);
 /**
 * Returns the name of this Eventful
 * @return the name of this Eventful
 */
public default String getName() {
	 return "";
 }
 /**
 * Copies all the events that this Eventful has to a new Eventful
 * @param eventful the Eventful to copy this Eventful's events to
 */
public default void copyEventsTo(final Eventful eventful) {
	 for (final Event e : getEvents())
		 eventful.addToEventList(e.makeCopy());
 }

}
