package thingFramework;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import effects.Event;

public class EventfulItem extends Item implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Event> events;
	private EventfulItem(Event... e) {
		super();
		events = Arrays.asList(e);
	}
	public EventfulItem(String name, String image, Set<Attribute> attributes, List<Event> events) {
		super(name, image, attributes);
		this.events = events;
	}
	public EventfulItem(String name, String image, Set<Attribute> attributes, Event... e) {
		this(name, image, attributes, Arrays.asList(e));
	
	}
	public EventfulItem(EventfulItem ei) {
		this(ei.getName(), ei.getImage(),ei.getAttributes(), ei.getEvents());
	}
	public List<Event> getEvents() {
		return events;
	}
	@Override
	protected EnumSet<ThingType> setThingType() {
		return EnumSet.of(ThingType.ITEM, ThingType.EVENTFULITEM);
	}
	public static Item generateBlankEventItem(Event... e) {
		return new EventfulItem(e);
	}
	@Override
	public Thing makeCopy() {
		return new EventfulItem(this);
	}
}
