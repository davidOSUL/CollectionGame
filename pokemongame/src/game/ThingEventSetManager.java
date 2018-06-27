package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import effects.Event;
import thingFramework.EventfulItem;
import thingFramework.Thing;
import thingFramework.Thing.ThingType;
//TODO: This still doesn't work for duplicates because items are like unique bro, and have unique events
public class ThingEventSetManager {
	private Set<Event> events = new HashSet<Event>();
	private List<Event> eventsAsList = new ArrayList<Event>();
	private List<Event> currentEvents = new ArrayList<Event>();
	int quantity;
	private boolean quantityUpdated = false;
	private int oldQuantity = quantity;
	public ThingEventSetManager(Thing thing, int defaultQuantity) {
		if (isEventfulItem(thing)) {
			events.addAll(((EventfulItem) thing).getEvents());
		}
		events.addAll(BoardAttributeManager.getEvents(thing.getBoardAttributes()));
		eventsAsList.addAll(events);
		quantity = defaultQuantity;
	}
	public void increaseQuantity() {
		if (!quantityUpdated)
		oldQuantity = quantity;
		quantity++;
		quantityUpdated = true;
	}
	public void decreaseQuantity() {
		if (!quantityUpdated)
		oldQuantity = quantity;
		quantity--;
		quantityUpdated = true;
		if (quantity < 0)
			throw new RuntimeException("quantity cannot be less than 0");
	}
	public int getQuantity() {
		return quantity;
	}
	public boolean isEmpty() {
		return getQuantity() == 0;
	}
	public List<Event> getEvents() {
		if (quantity == 0)
			return new ArrayList<Event>();
		if (quantity == 1)
			return eventsAsList;
		if (!quantityUpdated)
			return currentEvents;
		for (int i =1; i < quantity; i++)
			currentEvents.addAll(getNewEvents()); //if i ==1 save copying over
		return currentEvents;
	}
	private static boolean isEventfulItem(Thing t) {
		return t.getThingTypes().contains(ThingType.EVENTFULITEM);
	}
	private  List<Event> getNewEvents() {
		List<Event> newEvents = new ArrayList<Event>();
		for (Event e: events) {
			newEvents.add(e.createNewEventCopy());
		}
		return newEvents;
	}

}
