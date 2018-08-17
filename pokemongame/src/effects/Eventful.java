package effects;

import java.util.Collection;
import java.util.List;

/**
 * Anything that is said to have events associated with it, should implement this interface
 * @author David O'Sullivan
 *
 */
public interface Eventful {
 public void confirmEventRemovals(final Collection<Event> events);
 public List<Event> getEvents();
 public void addToEventList(final Collection<Event> events);
 public void addToEventList(final Event event);
 public default String getName() {
	 return "";
 }
}
