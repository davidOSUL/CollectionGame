package game;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import effects.Event;
import thingFramework.Attribute;
/**
 * Manages all the Board Attributes (attributes of the board as a whole rather than to a specific Thing)
 * @author David O'Sullivan
 */
public final class BoardAttributeManager {

	private BoardAttributeManager() {
		
	}
	/**
	 * Builds Board events based on input name
	 * @param nameOfEvent gph = gold Per Hour, gpm = Gold Per Minute, popularity boost = Increase popularity of board on place, decrease on remove
	 * @param valueOfEvent the amount to effect the attribute by
	 * @return the created event
	 */
	private static Event eventBuilder(String nameOfEvent, Object valueOfEvent) {
		Event event;
		
		switch (nameOfEvent) {
			case "gph":
				event = new Event(board -> board.addGold((Integer) valueOfEvent), 60);
				event.addToName("GPH: ");
				break;
			case "gpm":
				event =  new Event(board -> board.addGold((Integer) valueOfEvent), 1);
				event.addToName("GPM: ");
				break;
			case "popularity boost":
				event = new Event(board -> board.addPopularity((Integer) valueOfEvent), board -> board.subtractPopularity((Integer) valueOfEvent));
				event.addToName("POP: ");
				break;
			default:
				throw new Error("EVENT NOT FOUND");
		}
		return event;
	}
	/**
	 * Takes in a set of Attributes that effect the state of the board and generates the associated events for them. Creates new instances of the events. 
	 * @param boardAttributes A Set of Attributes that modify the state of the board. 
	 * Specifically, must be set of attributes that contain AttributeType Thing.BOARDTYPE
	 * @return The List of generated events
	 */
	public static List<Event> getEvents(Set<Attribute> boardAttributes) {
		List<Event> events = new ArrayList<Event>(boardAttributes.size());
		int i =0;
		for (Attribute at: boardAttributes) {
			events.add(eventBuilder(at.getName(), at.getValue()));
		}
		return events;
		
	}
	
}
