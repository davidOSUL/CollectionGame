package thingFramework;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import effects.Event;

public final class ThingLoader {
	private final Path path;
	private final EventBuilder eb;
	private final Set<Thing> thingSet = new HashSet<Thing>();
	private final Set<Pokemon> pokemonSet = new HashSet<Pokemon>();
	private final Set<Item> itemSet = new HashSet<Item>();
	private final Set<EventfulItem> eventfulItemSet = new HashSet<EventfulItem>();
	private final Map<String, Thing> thingMap = new HashMap<String, Thing>();
	public ThingLoader(String pathToItems) {
		this.path = FileSystems.getDefault().getPath(pathToItems);
		eb = new EventBuilder();
		load();
	}
	public ThingLoader(String pathToItems, String pathToEvents) {
		this.path = FileSystems.getDefault().getPath(pathToItems);
		eb = new EventBuilder(pathToEvents);
		load();
	}
	private void load() {
		try {
			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
			for (String line: lines) {
				String[] values = line.split(",");
				String type = values[0];
				if (type.equals("POKEMON"))
					loadPokemon(values);
				else if (type.equals("ITEM"))
					loadItem(values);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void loadPokemon(String[] values) {
		String name = values[1];
		String texture = values[2];
		Set<Attribute> attributes = loadAttributes(values, 3, name);
		Pokemon pm = new Pokemon(name, texture, attributes);
		thingMap.put(name, pm);
		thingSet.add(pm);
		pokemonSet.add(pm);
	}
	private void loadItem(String[] values) {
		if (values[1].equals("EVENTFULITEM")) {
			String name = values[2];
			String texture = values[3];
			List<Event> e = eb.getEvents(name);
			Set<Attribute> attributes = loadAttributes(values, 4, name);
			EventfulItem ei = new EventfulItem(name, texture, attributes, e);
			thingSet.add(ei);
			itemSet.add(ei);
			eventfulItemSet.add(ei);
			thingMap.put(name, ei);
		}
		else {
			String name = values[1];
			String texture = values[2];
			Set<Attribute> attributes = loadAttributes(values, 3, name);
			Item i = new Item(name, texture, attributes);
			thingSet.add(i);
			itemSet.add(i);
			thingMap.put(name, i);
		}
	}
	private Set<Attribute> loadAttributes(String[] values, int startLocation, String name) {
		Set<Attribute> attributes = new HashSet<Attribute>();
		Set<String> attributeNames = new HashSet<String>();
		for (int i = startLocation; i < values.length; i++) {
			String atr = values[i];
			if (atr.equals("")) 
				continue;
			String[] nameValuePair = atr.split(":");
			if (attributeNames.contains(nameValuePair[0]))
				throw new Error("DUPLICATE ATTRIBUTE: " + nameValuePair[0] + "FOR: " + name);
			else
				attributeNames.add(nameValuePair[0]);
			if (nameValuePair.length == 2) 
				attributes.add(Attribute.generateAttribute(nameValuePair[0], nameValuePair[1]));
			else if (nameValuePair.length == 1) 
				attributes.add(Attribute.generateAttribute(nameValuePair[0]));
			else
				throw new Error("WRONG NUMBER OF ATTRIBUTE INFO : " + Arrays.toString(nameValuePair) + "FOR: " + name);
		}
		return attributes;
	}
	public  Set<Thing> getThingSet() {
		return thingSet;
	}
	public  Set<Pokemon> getPokemonSet() {
		return pokemonSet;
	}
	public Set<Item> getItemSet() {
		return itemSet;
	}
	public Set<EventfulItem> getEventfulItemSet() {
		return eventfulItemSet;
	}
	public Thing getThing(String name) {
		return thingMap.get(name);
	}
	
}
