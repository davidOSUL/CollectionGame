package game;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import effects.Event;
import effects.OnPeriodEventWithDisplay;
import thingFramework.Attribute;
import thingFramework.Thing;
/**
 * Manages all the Board Attributes (attributes of the board as a whole rather than to a specific Thing)
 * @author David O'Sullivan
 */
public final class BoardAttributeManager {
	/*private static final SerializableTriConsumer<Thing, Event, Board> UPDATE_GPH = (t, e, b) -> {
		t.getAttribute("gph").setExtraDescription(" (" + e.getTimeToNextPeriod(b) + ")");
		t.updateDescription();
	};
	private static final SerializableTriConsumer<Thing, Event, Board> UPDATE_GPM = (t, e, b) -> {
		t.getAttribute("gpm").setExtraDescription(" (" + e.getTimeToNextPeriod(b) + ")");
		t.updateDescription();
	};*/
	private BoardAttributeManager() {

	}
	/**
	 * Builds Board events based on input name
	 * @param nameOfEvent gph = gold Per Hour, gpm = Gold Per Minute, popularity boost = Increase popularity of board on place, decrease on remove
	 * @param valueOfEvent the amount to effect the attribute by
	 * @return the created event
	 */
	private static Event eventBuilder(final String nameOfEvent, final Object valueOfEvent, final Thing creator) {
		final Event event;

		switch (nameOfEvent) {
		case "gph":
			event = new OnPeriodEventWithDisplay<Thing>(board -> board.addGold((Integer) valueOfEvent), .1, "gph", creator);
			event.addToName("GPH: ");
			break;
		case "gpm":
			event =  new OnPeriodEventWithDisplay<Thing>(board -> board.addGold((Integer) valueOfEvent), 1, "gpm", creator);
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
	public static void modifyBoardEvent(final String AttributeName, final Event e, final Object newValue) {
		switch(AttributeName) {
		case "gph":
			e.setOnPeriod(board -> board.addGold((Integer) newValue));
			break;
		case "gpm":
			e.setOnPeriod(board -> board.addGold((Integer) newValue));
			break;
		case "popularity boost":
			e.setOnPlace(board -> board.addPopularity((Integer) newValue));
			e.markForReset( board -> board.subtractPopularity((Integer) newValue));
			break;
		default:
			throw new Error("EVENT NOT FOUND");
		}

	}
	/**
	 * Takes in a set of Attributes that effect the state of the board and generates the associated events for them. Creates new instances of the events. 
	 * @param boardAttributes A Set of Attributes that modify the state of the board. 
	 * Specifically, must be set of attributes that contain AttributeType Thing.BOARDTYPE
	 * @param the holder of these attributes
	 * @return The List of generated events
	 */
	public static Map<Attribute, Event> getEvents(final Set<Attribute> boardAttributes, final Thing creator) {
		final Map<Attribute, Event> events = new HashMap<Attribute, Event>(boardAttributes.size());
		for (final Attribute at: boardAttributes) {
			events.put(at, eventBuilder(at.getName(), at.getValue(), creator));
		}
		return events;
	}
	
	/**
	 * Takes in a board attribute and generates event that affect board
	 * @param at the board attribute
	 * @param the holder of these attributes
	 * @return the generated event
	 */
	public static Event getEvent(final Attribute at, final Thing creator) {
		return eventBuilder(at.getName(), at.getValue(), creator);
	}

}
