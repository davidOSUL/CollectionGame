package game;
import thingFramework.Attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import effects.Event;
public final class BoardAttributeManager {

	private BoardAttributeManager() {
		
	}
	private static Event eventBuilder(String nameOfEvent, Object valueOfEvent) {
		Event event;
		
		switch (nameOfEvent) {
			case "gph":
				event = new Event(board -> board.addGold((Integer) valueOfEvent), 60);
				break;
			case "gpm":
				event =  new Event(board -> board.addGold((Integer) valueOfEvent), 1);
				break;
			case "popularity boost":
				event = new Event(board -> board.addPopularity((Integer) valueOfEvent), board -> board.subtractPopularity((Integer) valueOfEvent));
				break;
			default:
				throw new Error("EVENT NOT FOUND");
		}
		return event;
	}
	public static List<Event> getEvents(Set<Attribute> set) {
		List<Event> events = new ArrayList<Event>(set.size());
		int i =0;
		for (Attribute at: set) {
			events.add(eventBuilder(at.getName(), at.getValue()));
		}
		return events;
		
	}
	
}
