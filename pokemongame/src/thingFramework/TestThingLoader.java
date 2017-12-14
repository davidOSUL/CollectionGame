package thingFramework;

import java.util.List;

import effects.Event;

public class TestThingLoader {

	public static void main(String[] args) {
		ThingLoader tl = new ThingLoader("resources/InputFiles/pokemonList (2).csv");
		System.out.println(tl.getThingSet());
		System.out.println(tl.getEventfulItem("Small Table"));
		List<Event> e = ((EventfulItem) tl.getThing("Small Table")).getEvents();
		System.out.println(e);

	}

}
