package thingFramework;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import effects.Event;

public final class ThingLoader {
	private final Path path;
	private Path[] pathsToExtraAttributes = null;
	private final EventBuilder eb;
	private  Set<Thing> thingSet = new HashSet<Thing>();
	private Set<Pokemon> pokemonSet = new HashSet<Pokemon>();
	private  Set<Item> itemSet = new HashSet<Item>();
	private  Set<EventfulItem> eventfulItemSet = new HashSet<EventfulItem>();
	private final Map<String, Thing> thingMap = new HashMap<String, Thing>();
	private final Map<String, Item> itemMap = new HashMap<String, Item>();
	private final Map<String, Pokemon> pokemonMap = new HashMap<String, Pokemon>();
	private final Map<String, EventfulItem> eventfulItemMap = new HashMap<String, EventfulItem>();
	public ThingLoader(String pathToThings) {
		this.path = FileSystems.getDefault().getPath(pathToThings);
		eb = new EventBuilder();
		load();
	}
	public ThingLoader(String pathToThings, String pathToEvents) {
		this.path = FileSystems.getDefault().getPath(pathToThings);
		eb = new EventBuilder(pathToEvents);
		load();
	}
	public ThingLoader(String pathToThings, String pathToEvents, String... pathsToExtraAttributes) {
		this(pathToThings, pathToEvents);
		this.pathsToExtraAttributes = new Path[pathsToExtraAttributes.length];
		int i = 0;
		for (String path: pathsToExtraAttributes) {
			this.pathsToExtraAttributes[i++] = FileSystems.getDefault().getPath(path);
		}
		loadExtraAttributes();
	}
	/**
	 * <br> Assumes sheets of the form:</br>
	 * <br>POKEMON</br>
	 * <br>Name attribute val attribute val ...</br>
	 * <br>Name attribute val attribute val ...</br>
	 * <br>...</br>
	 * <br>ITEM</br>
	 * <br>Name attribute val attribute val ...</br>
	 * <br>Name attribute val attribute val ...</br>
	 * <br>Name can be mentioned on more than one line for different attributes</br>
	 */
	private void loadExtraAttributes() {
		for (Path pathToExtraAttributes: pathsToExtraAttributes) {
			CurrentIteratorValue civ = CurrentIteratorValue.UNKNOWN;
			try {
				List<String> lines = Files.readAllLines(pathToExtraAttributes, StandardCharsets.UTF_8);
				for (String line: lines) {
					String[] values = line.split(",");
					String potentialInput = values[0].toUpperCase().trim();
					if (potentialInput.equals("POKEMON"))
						civ = CurrentIteratorValue.POKEMON;
					else if (potentialInput.equals("ITEM")) 
						civ = CurrentIteratorValue.ITEM;
					else {
						switch(civ) {
						case POKEMON:
							loadExtraAttribute(values);
							break;
						case ITEM:
							loadExtraAttribute(values);
							break;
						case UNKNOWN:
							throw new Error("ISSUE LOADING EXTRA ATTRIBUTES");
						}
					}


				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void loadExtraAttribute(String[] values) {
		String name = values[0];
		for (int i =1 ; i < values.length; i+=2) {
			String attribute = values[i];
			String value = values[i+1];
			thingMap.get(name).addAttribute(Attribute.generateAttribute(attribute, value));
		}
	}
	
	/**
	 * <br> Assumes inputs of the form: </br> 
	 * <br> POKEMON Name, texture, attribute:val, attribute:val,...  </br> 
	 * <br> POKEMON Name, texture, attribute:val, attribute:val,... </br>  
	 * <br> ... </br> 
	 * <br> ITEM Name, texture, attribute:val, attribute:val,... </br> 
	 * <br> ITEM Name, texture, attribute:val, attribute:val,... </br> 
	 * <br> ... </br> 
	 * <br> ITEM EVENTFULITEM Name, texture, attribute:val, attribute:val,... </br> 
	 * <br> ITEM EVENTFULITEM Name, texture, attribute:val, attribute:val,... </br> 
	 * <br> ... </br>
	 * <br> Duplicates SHOULD NOT appear in list</br>
	 */
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
		thingSet.addAll(thingMap.values());
		thingSet = Collections.unmodifiableSet(thingSet);
		pokemonSet.addAll(pokemonMap.values());
		pokemonSet = Collections.unmodifiableSet(pokemonSet);
		itemSet.addAll(itemMap.values());
		itemSet = Collections.unmodifiableSet(itemSet);
		eventfulItemSet.addAll(eventfulItemMap.values());
		eventfulItemSet = Collections.unmodifiableSet(eventfulItemSet);
	}
	private void loadPokemon(String[] values) {
		String name = values[1];
		String texture = values[2];
		Set<Attribute> attributes = loadAttributes(values, 3, name);
		Pokemon pm = new Pokemon(name, texture, attributes);
		thingMap.put(name, pm);
		pokemonMap.put(name, pm);
		
	}
	private void loadItem(String[] values) {
		if (values[1].equals("EVENTFULITEM")) {
			String name = values[2];
			String texture = values[3];
			List<Event> e = eb.getEvents(name);
			Set<Attribute> attributes = loadAttributes(values, 4, name);
			EventfulItem ei = new EventfulItem(name, texture, attributes, e);
			thingMap.put(name, ei);
			itemMap.put(name, ei);
			eventfulItemMap.put(name, ei);
		}
		else {
			String name = values[1];
			String texture = values[2];
			Set<Attribute> attributes = loadAttributes(values, 3, name);
			Item i = new Item(name, texture, attributes);
			thingMap.put(name, i);
			itemMap.put(name, i);
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
		return new HashSet<Pokemon>(pokemonMap.values());
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
	public Pokemon getPokemon(String name) {
		return pokemonMap.get(name);
	}
	public Item getItem(String name) {
		return itemMap.get(name);
	}
	public EventfulItem getEventfulItem(String name) {
		return eventfulItemMap.get(name);
	}
	/**
	 * Currently serves no purpose in the logic past debugging
	 * @author David O'Sullivan
	 *
	 */
	private enum CurrentIteratorValue{
		POKEMON, ITEM, UNKNOWN
	}
	
}
