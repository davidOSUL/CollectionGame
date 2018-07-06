package effects;

import java.util.ArrayList;
import java.util.List;

public interface Eventful {
 public List<Event> getEvents();
 public default String getName() {
	 return "";
 }
}
