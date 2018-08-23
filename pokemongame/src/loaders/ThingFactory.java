package loaders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import attributes.ParseType;
import loaders.eventbuilder.EventBuilder;
import thingFramework.Item;
import thingFramework.Pokemon;
import thingFramework.Thing;

/**
 * All things from the resources folder are loaded in using this class
 * @author David O'Sullivan
 *
 */
public final class ThingFactory {
	private final EventBuilder eb;
	private final ThingMap thingTemplates;
	private final List<Loader> loaders;
	/**
	 * The location of the levels of evolution
	 */
	private static final String LEVELS_OF_EVOLUTION_LOCATION = "/InputFiles/levelsOfEvolve.csv";
	/**
	 * Location of which pokemon evolve to what
	 */
	private static final String EVOLUTIONS_LOCATION = "/InputFiles/evolutions.csv";
	/**
	 * The location of the csv of all the thing to import into the game
	 */
	private static final String[] THING_LIST_LOCATIONS = {"/InputFiles/pokemonList.csv", "/InputFiles/itemList - 1.csv"};
	/**
	 * The location of all pregenerated "basic" events to load into the game. I.E.
	 * items that have events that can be described by methods in the ThingLoader class
	 */
	private static final String EVENT_MAP_LOCATION = "/InputFiles/eventMapList - 1.csv";
	/**
	 * Location of csv containing extra attributes for things. Format as specified in thingloader
	 */
	private static final String[] EXTRA_ATTRIBUTE_LOCATIONS = {"/InputFiles/extraAttributes - 1.csv", "/InputFiles/extraAttributes - 2.csv",
			"/InputFiles/extraAttributes - 3.csv"};
	private static final String PATH_TO_DESCRIPTIONS = "/InputFiles/descriptionList.csv";
	private static final ThingFactory INSTANCE = new ThingFactory(THING_LIST_LOCATIONS, PATH_TO_DESCRIPTIONS, EVENT_MAP_LOCATION, EVOLUTIONS_LOCATION, LEVELS_OF_EVOLUTION_LOCATION, EXTRA_ATTRIBUTE_LOCATIONS);
	private ThingFactory(final String pathToEvents) {
		thingTemplates = new ThingMap();
		loaders = new ArrayList<Loader>();
		if (pathToEvents == null)
			eb = new EventBuilder();
		else
			eb = new EventBuilder(pathToEvents);
	}
	private ThingFactory(final String[] pathToThings, final String pathToEvents) {
		this(pathToEvents);
		loaders.add(new ThingLoader(this, pathToThings));
		loadAllLoaders();
	}
	private ThingFactory(final String[] pathToThings, final String pathToDescriptions, final String pathToEvents, final String pathToEvolutions, final String pathToLevelsOfEvolve, final String... pathsToExtraAttributes) {
		this(pathToEvents);
		loaders.add(new ThingLoader(this, pathToThings, new ExtraAttributeLoader(thingTemplates, pathsToExtraAttributes)));
		loaders.add(new PokemonEvolutionLoader(pathToEvolutions, pathToLevelsOfEvolve, thingTemplates));
		loaders.add(new DescriptionLoader(pathToDescriptions, thingTemplates, eb));
		loadAllLoaders();
	}
	void addNewPokemonTemplate(final Pokemon template) {
		thingTemplates.addPokemon(template);
	}
	void addNewItemTemplate(final Item template) {
		thingTemplates.addItem(template);
	}
	private void loadAllLoaders() {
		loaders.forEach(l -> l.load());
	}
	public static ThingFactory sharedInstance() {
		return INSTANCE;
	}
	public String getThingDescription(final String thingName) {
		return thingTemplates.get(thingName).toString();
	}
	public String getThingImage(final String thingName) {
		return thingTemplates.get(thingName).getImage();
	}
	/**
	 * Creates a new instance of the thing with the given name with all the characterstics that were loaded in on the game start
	 * @param name the name of thing 
	 * @return The new thing. Throws NullPointerException if not present
	 */
	public Thing generateNewThing(final String name) {
		final Thing t = thingTemplates.get(name).makeCopy();
		t.addToEventList(eb.getNewRegularEvents(name));
		eb.getNewHeldEvents(name).forEach(he -> {
			he.setCreator(t);
			t.addToEventList(he);
		});
		return t;
	}
	/**
	 * Creates a new instance of the Pokemon with the given name with all the characterstics that were loaded in on the game start
	 * @param name the name of pokemon
	 * @return The new thing. Throws NullPointerException if not present
	 */
	public Pokemon generateNewPokemon(final String name) {
		final Pokemon p = new Pokemon(thingTemplates.getPokemon(name));
		p.addToEventList(eb.getNewRegularEvents(name));
		eb.getNewHeldEvents(name).forEach(he -> {
			he.setCreator(p);
			p.addToEventList(he);
		});
		return p;
	}
	/**
	 * Creates a new instance of the item with the given name with all the characterstics that were loaded in on the game start
	 * @param name the name of thing 
	 * @return The new thing. Throws NullPointerException if not present
	 */
	public Item generateNewItem(final String name) {
		final Item i = new Item(thingTemplates.getItem(name));
		i.addToEventList(eb.getNewRegularEvents(name));
		eb.getNewHeldEvents(name).forEach(he -> {
			he.setCreator(i);
			i.addToEventList(he);
		});
		return i;
	}
	
	/**
	 * @param name the name of the thing to lookup
	 * @return true if that thing was loaded in
	 */
	public boolean hasThing(final String name) {
		return thingTemplates.hasThing(name);
	}
	
	/**
	 * Returns a map from the names of things to the values of their attributes of the provided name
	 * @param <T> the ParseType of the attribute
	 * @param attributeName the name of the attribute to get the values for
	 * @return the map from names to attribute value for the provided attribute type
	 */
	public final <T> Map<String, T> mapFromSetToAttributeValue(final String attributeName, final ParseType<T> type) {
		return mapFromSetToAttributeValue(attributeName, thingTemplates.viewThings(), type);
	}
	private final <T> Map<String, T> mapFromSetToAttributeValue(final String attributeName, final Collection<? extends Thing> collectionToIterate, final ParseType<T> type) {
		final Map<String, T> mapping = new HashMap<String, T>();
		for (final Thing t : collectionToIterate) {
			if (!t.containsAttribute(attributeName))
				continue;
			final T value = t.getAttributeValue(attributeName, type);
			mapping.put(t.getName(), value);
		}
		return mapping;
	}
	/**
	 * Returns a map from the names of things of the given type to the values of their attributes of the provided name
	 * @param <T> the ParseType of the attribute
	 * @param attributeName the name of the attribute to get the values for
	 * @return the map from names to attribute value for the provided attribute type
	 */
	public final <T> Map<String, T> mapFromSetToAttributeValue(final String attributeName, final ThingType thingType, final ParseType<T> parseType) {
		return mapFromSetToAttributeValue(attributeName, thingTemplates.viewThings(thingType), parseType);
	}
	private final <T> Set<String> getThingsWithAttributeVal(final String attributeName, final T desiredValue, final Collection<? extends Thing> collectionToIterate, final ParseType<T> parseType) {
		return mapFromSetToAttributeValue(attributeName, collectionToIterate, parseType).entrySet().stream()
				.filter(p -> p.getValue().equals(desiredValue)).
				collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).keySet();
	}
	/**
	 * Returns the set of things with the desired attribute value
	 * @param attributeName the name of the attribute
	 * @param desiredValue the desired value for the attribute
	 * @return the set of all things that have that attribute and have the desired value for that attribute
	 */
	public final <T> Set<String> getThingsWithAttributeVal(final String attributeName, final T desiredValue, final ParseType<T> parseType) {
		return getThingsWithAttributeVal(attributeName, desiredValue, thingTemplates.viewThings(), parseType);
	}
	/**
	 * Returns the set of things of the given type with the desired attribute value
	 * @param attributeName the name of the attribute
	 * @param desiredValue the desired value for the attribute
	 * @return the set of all things that have that attribute and have the desired value for that attribute
	 */
	public final <T> Set<String> getThingsWithAttributeVal(final String attributeName, final T desiredValue, final ThingType type, final ParseType<T> parseType) {
		return mapFromSetToAttributeValue(attributeName, thingTemplates.viewThings(type), parseType).entrySet().stream()
				.filter(p -> p.getValue().equals(desiredValue)).
				collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).keySet();
	}
	
	
}