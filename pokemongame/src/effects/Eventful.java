package effects;

import java.util.ArrayList;
import java.util.List;

/**
 * Anything that is said to have events associated with it, should implement this interface
 * @author David O'Sullivan
 *
 */
public interface Eventful {
 public List<Event> getEvents();
 public default String getName() {
	 return "";
 }
}
