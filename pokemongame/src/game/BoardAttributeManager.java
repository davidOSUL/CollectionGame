package game;
import java.util.HashMap;
import java.util.Map;

import attributes.Attribute;
import attributes.AttributeManagerWatcher;
import effects.Event;
import effects.OnPeriodEventWithDisplay;
import thingFramework.Thing;
/**
 * Manages all the Board Attributes (attributes of the board as a whole rather than to a specific Thing)
 * @author David O'Sullivan
 */
public final class BoardAttributeManager implements AttributeManagerWatcher<Integer> {
	/*private static final SerializableTriConsumer<Thing, Event, Board> UPDATE_GPH = (t, e, b) -> {
		t.getAttribute("gph").setExtraDescription(" (" + e.getTimeToNextPeriod(b) + ")");
		t.updateDescription();
	};
	private static final SerializableTriConsumer<Thing, Event, Board> UPDATE_GPM = (t, e, b) -> {
		t.getAttribute("gpm").setExtraDescription(" (" + e.getTimeToNextPeriod(b) + ")");
		t.updateDescription();
	};*/
	private Thing holder;
	private final Map<Attribute<?>, Event> addedEvents = new HashMap<Attribute<?>, Event>();
	private BoardAttributeManager() {

	}
	public BoardAttributeManager(final Thing holder) {
		this.holder = holder;
	}
	/**
	 * Builds Board events based on input name
	 * @param nameOfEvent gph = gold Per Hour, gpm = Gold Per Minute, popularity boost = Increase popularity of board on place, decrease on remove
	 * @param valueOfEvent the amount to effect the attribute by
	 * @return the created event
	 */
	private Event eventBuilder(final String nameOfEvent, final int valueOfEvent) {
		Event event = null;
		switch (nameOfEvent) {
		case "gph":
			event = new OnPeriodEventWithDisplay<Thing>(board -> board.addGold( valueOfEvent), 60, "gph", holder);
			event.addToName("GPH: ");
			break;
		case "gpm":
			event =  new OnPeriodEventWithDisplay<Thing>(board -> board.addGold(valueOfEvent), 1, "gpm", holder);
			event.addToName("GPM: ");
			break;
		case "popularity boost":
			event = new Event(board -> board.addPopularity(valueOfEvent), board -> board.subtractPopularity(valueOfEvent));
			event.addToName("POP: ");
			break;
		default:
			break;
		}
		return event;
	}
	private void modifyBoardEvent(final String AttributeName, final Event e, final int newValue) {
		switch(AttributeName) {
		case "gph":
			e.setOnPeriod(board -> board.addGold(newValue));
			break;
		case "gpm":
			e.setOnPeriod(board -> board.addGold(newValue));
			break;
		case "popularity boost":
			e.setOnPlace(board -> board.addPopularity(newValue));
			e.markForReset( board -> board.subtractPopularity(newValue));
			break;
		default:
			break;
		}

	}
	
	/*
	 * 
	 
	
	 * Takes in a set of Attributes that effect the state of the board and generates the associated events for them. Creates new instances of the events. 
	 * @param boardAttributes A Set of Attributes that modify the state of the board. 
	 * Specifically, must be set of attributes that contain AttributeType Thing.BOARDTYPE
	 * @param the holder of these attributes
	 * @return The List of generated events

	public static Map<Attribute, Event> getEvents(final Set<Attribute> boardAttributes, final Thing creator) {
		final Map<Attribute, Event> events = new HashMap<Attribute, Event>(boardAttributes.size());
		for (final Attribute at: boardAttributes) {
			events.put(at, eventBuilder(at.getName(), at.getValue(), creator));
		}
		return events;
	}
	*/
	
	@Override
	public void onAttributeGenerated(final Attribute<Integer> addedAttribute) {
		final Event e = eventBuilder(addedAttribute.getName(), addedAttribute.getValue() == null ? 0 : addedAttribute.getValue());
		if (e != null) {
			holder.addToEventList(e);
			addedEvents.put(addedAttribute, e);
		}
		
	}
	@Override
	public void onAttributeRemoved(final Attribute<Integer> removedAttribute) {
		if (addedEvents.containsKey(removedAttribute)) {
			addedEvents.remove(removedAttribute).markForRemoval();
		}
		
	}
	@Override
	public void onAttributeModified(final Attribute<Integer> modifiedAttribute) {
		if (addedEvents.containsKey(modifiedAttribute)) {
			modifyBoardEvent(modifiedAttribute.getName(), addedEvents.get(modifiedAttribute), modifiedAttribute.getValue());
		}
		
	}
	
	
}
