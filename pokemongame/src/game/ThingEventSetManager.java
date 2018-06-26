package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import effects.Event;
import thingFramework.EventfulItem;
import thingFramework.Thing;
import thingFramework.Thing.ThingType;

public class ThingEventSetManager {
	private Set<Event> events = new HashSet<Event>();
	private List<Event> eventsAsList = new ArrayList<Event>();
	int quantity = 0;
	public ThingEventSetManager(Thing thing) {
		if (isEventfulItem(thing)) {
			events.addAll(((EventfulItem) thing).getEvents());
		}
		events.addAll(BoardAttributeManager.getEvents(thing.getBoardAttributes()));
		eventsAsList.addAll(events);
	}
	public void increaseQuantity() {
		quantity++;
	}
	public void decreaseQuantity() {
		quantity--;
		if (quantity < 0)
			throw new RuntimeException("quantity cannot be less than 0");
	}
	public List<Event> getEvents() {
		if (quantity == 0)
			return new ArrayList<Event>();
		List<Event> currentEvents = eventsAsList;
		for (int i =1; i < quantity; i++)
			currentEvents.addAll(events); //if i ==1 save copying over
		return currentEvents;
	}
	private static boolean isEventfulItem(Thing t) {
		return t.getThingTypes().contains(ThingType.EVENTFULITEM);
	}

}
