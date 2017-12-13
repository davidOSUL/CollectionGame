package thingFramework;

import java.util.Map;

import effects.Event;

public class EventfulItem extends Item {
	private Event event;
	public EventfulItem(String name, String image, Map<Attribute, Object> attributes, Event e) {
		super(name, image, attributes);
		this.event = e;
	}
	public Event getEvent() {
		return event;
	}

}
