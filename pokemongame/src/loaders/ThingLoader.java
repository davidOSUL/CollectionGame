package loaders;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import attributes.AttributeNotFoundException;
import gameutils.GameUtils;
import loaders.eventbuilder.EventBuilder;
import thingFramework.Attribute;
import thingFramework.Item;
import thingFramework.Pokemon;
import thingFramework.Thing;
import thingFramework.Thing.ThingType;

/**
 * All things from the resources folder are loaded in using this class
 * @author David O'Sullivan
 *
 */
public final class ThingLoader {
	/**
	 * Tell thing loader to generate random attributes for this thing
	 */
	private static final String GEN_CODE = "RANDOMATTRIBUTES";
	private static final String POKE_SPRITE_LOC = "/sprites/pokemon/battlesprites/";
	private static final String ITEM_SPRITE_LOC = "/sprites/items/";
	private final EventBuilder eb;
	private  Set<Thing> thingSet = new HashSet<Thing>();
	private Set<Pokemon> pokemonSet = new HashSet<Pokemon>();
	private  Set<Item> itemSet = new HashSet<Item>();
	private final Set<String> pokemonToGenerateAttributesFor = new HashSet<String>();
	private final Map<String, Thing> thingMap = new HashMap<String, Thing>();
	private final Map<String, Item> itemMap = new HashMap<String, Item>();
	private final Map<String, Pokemon> pokemonMap = new HashMap<String, Pokemon>();	
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
	private static final ThingLoader INSTANCE = new ThingLoader(THING_LIST_LOCATIONS, PATH_TO_DESCRIPTIONS, EVENT_MAP_LOCATION, EVOLUTIONS_LOCATION, LEVELS_OF_EVOLUTION_LOCATION, EXTRA_ATTRIBUTE_LOCATIONS);
	private ThingLoader(final String[] pathToThings) {
		this(pathToThings, null);
	}
	private ThingLoader(final String[] pathToThings, final String pathToEvents) {
		if (pathToEvents == null)
			eb = new EventBuilder();
		else
			eb = new EventBuilder(pathToEvents);
		for (final String path : pathToThings)
			load(path);
		thingSet.addAll(thingMap.values());
		thingSet = Collections.unmodifiableSet(thingSet);
		pokemonSet.addAll(pokemonMap.values());
		pokemonSet = Collections.unmodifiableSet(pokemonSet);
		itemSet.addAll(itemMap.values());
		itemSet = Collections.unmodifiableSet(itemSet);
	}
	private ThingLoader(final String[] pathToThings, final String pathToDescriptions, final String pathToEvents, final String pathToEvolutions, final String pathToLevelsOfEvolve, final String... pathsToExtraAttributes) {
		this(pathToThings, pathToEvents);
		for (final String path: pathsToExtraAttributes) {
			 loadExtraAttributes(path);
		}
		new PokemonEvolutionLoader(pathToEvolutions, pathToLevelsOfEvolve).load();
		new GenerateAttributes().generate(pokemonToGenerateAttributesFor);
		new DescriptionLoader(pathToDescriptions).load();
	}
	public static ThingLoader sharedInstance() {
		return INSTANCE;
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
	 * <br>Names that don't exist can be mentioned, they will be ignored</br>
	 */
	private void loadExtraAttributes(final String pathToExtraAttributes) {
			ThingType civ = null;
			try {
				final Set<String> visitedNames = new HashSet<String>();
				for (final String[] values : CSVReader.readCSV(pathToExtraAttributes)) {
					final String potentialInput = values[0].toUpperCase().trim();
					if (potentialInput.equalsIgnoreCase("POKEMON")) {
						civ = ThingType.POKEMON;
						visitedNames.clear();
					}
					else if (potentialInput.equals("ITEM")) {
						civ = ThingType.ITEM;
						visitedNames.clear();
					}
					else if (potentialInput.equals("ALL OTHERS")) {
						loadAttributesForAllOthers(values, civ, visitedNames);
					}
					else {
						switch(civ) {
						case POKEMON:
							visitedNames.add(values[0]);
							loadExtraAttribute(values);
							break;
						case ITEM:
							visitedNames.add(values[0]);
							loadExtraAttribute(values);
							break;
						default:
							throw new Error("ISSUE LOADING EXTRA ATTRIBUTES");
						}
					}

				}
				
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	private void loadExtraAttribute(final String[] values) {
		final String name = values[0];
		for (int i =1 ; i < values.length; i+=2) {
			final String attribute = values[i];
			final String value = values[i+1];
			if (thingMap.containsKey(name))
				thingMap.get(name).addAttribute(Attribute.generateAttribute(attribute, value));
		}
	}
	private void loadAttributesForAllOthers(final String[] values, final ThingType civ, final Set<String> visitedNames) {
		final String name = values[0];
		for (int i =1 ; i < values.length; i+=2) {
			final String attribute = values[i];
			final String value = values[i+1];
			switch(civ) {
			case POKEMON:
				for (final Pokemon p: pokemonSet) 
					if (!visitedNames.contains(p.getName()))
						p.addAttribute(Attribute.generateAttribute(attribute, value));
				break;
			case ITEM:
				for (final Item item: itemSet)
					if (!visitedNames.contains(item.getName()))
						item.addAttribute(Attribute.generateAttribute(attribute, value));
				break;
			}
			if (thingMap.containsKey(name))
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
	 * <br> Duplicates SHOULD NOT appear in list</br>
	 */
	private void load(final String path) {
		try {
			for (final String[] values : CSVReader.readCSV(path)) {
				final String type = values[0];
				if (type.equals("POKEMON"))
					loadPokemon(values);
				else if (type.equals("ITEM"))
					loadItem(values);
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	//TODO: Change all getPokemons, to getOrignalPokemons
	private void loadPokemon(final String[] values) {
		final String name = values[1];
		final String texture = POKE_SPRITE_LOC + values[2];
		final Set<Attribute> attributes = loadAttributes(values, 3, name);
		final Pokemon pm = new Pokemon(name, texture, attributes);
		thingMap.put(name, pm);
		pokemonMap.put(name, pm);
		
	}
	private void loadItem(final String[] values) {
			final String name = values[1];
			final String texture = ITEM_SPRITE_LOC + values[2];
			final Set<Attribute> attributes = loadAttributes(values, 3, name);
			final Item i = new Item(name, texture, attributes);
			thingMap.put(name, i);
			itemMap.put(name, i);
	}
	
	private Set<Attribute> loadAttributes(final String[] values, final int startLocation, final String name) {
		final Set<Attribute> attributes = new HashSet<Attribute>();
		final Set<String> attributeNames = new HashSet<String>();
		for (int i = startLocation; i < values.length; i++) {
			final String atr = values[i];
			if (atr.equals("")) 
				continue;
			final String[] nameValuePair = atr.split(":");
			
			if (attributeNames.contains(nameValuePair[0]))
				throw new Error("DUPLICATE ATTRIBUTE: " + nameValuePair[0] + "FOR: " + name);
			else
				attributeNames.add(nameValuePair[0]);
			if (nameValuePair[0].equals(GEN_CODE)) //if we want to generate random attributes
				pokemonToGenerateAttributesFor.add(name);
			else if (nameValuePair.length == 2) 
				attributes.add(Attribute.generateAttribute(nameValuePair[0], nameValuePair[1]));
			else if (nameValuePair.length == 1) 
				attributes.add(Attribute.generateAttribute(nameValuePair[0]));
			else
				throw new Error("WRONG NUMBER OF ATTRIBUTE INFO : " + Arrays.toString(nameValuePair) + "FOR: " + name);
		}
		return attributes;
	}
	public String getThingDescription(final String thingName) {
		return thingMap.get(thingName).toString();
	}
	public String getThingImage(final String thingName) {
		return thingMap.get(thingName).getImage();
	}
	/**
	 * Get all things that exist in the game
	 * @return an unmodifiable set of all things
	 */
	public Set<Thing> getThingSet() {
			return thingSet;
	}
	/**
	 * Get all pokemon that exist in the game
	 * @return an unmodifiable set of all pokemon
	 */
	public Set<Pokemon> getPokemonSet() {
		return pokemonSet;
	}
	/**
	 * Get all things that exist in the game
	 * @return an unmodifiable set of all items
	 */
	public Set<Item> getItemSet() {
		return itemSet;
	}
	/**
	 * Creates a new instance of the thing with the given name with all the characterstics that were loaded in on the game start
	 * @param name the name of thing 
	 * @return The new thing. Throws NullPointerException if not present
	 */
	public Thing generateNewThing(final String name) {
		final Thing t = thingMap.get(name).makeCopy();
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
		final Pokemon p = new Pokemon(pokemonMap.get(name));
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
		final Item i = new Item(itemMap.get(name));
		i.addToEventList(eb.getNewRegularEvents(name));
		eb.getNewHeldEvents(name).forEach(he -> {
			he.setCreator(i);
			i.addToEventList(he);
		});
		return i;
	}
	
	/**
	 * Returns the originally created thing with the given name
	 * @param name the name of the thing
	 * @return the thing
	 */
	private Thing getThing(final String name) {
		return thingMap.get(name);
	}
	/**
	 * Returns the originally created pokemon with the given name
	 * @param name the name of the pokemon
	 * @return the pokemon
	 */
	private Pokemon getPokemon(final String name) {
		return pokemonMap.get(name);
	}
	
	/**
	 * @param name the name of the thing to lookup
	 * @return true if that thing was loaded in
	 */
	public boolean hasThing(final String name) {
		return thingMap.containsKey(name);
	}
	/**
	 * @param name the name of the pokemon to lookup
	 * @return true if that Pokemon was loaded in
	 */
	public boolean hasPokemon(final String name) {
		return pokemonMap.containsKey(name);
	}
	
	/**
	 * Returns a map from the names of things to the values of their attributes of the provided name
	 * @param <T> the ParseType of the attribute
	 * @param attributeName the name of the attribute to get the values for
	 * @return the map from names to attribute value for the provided attribute type
	 */
	public final <T> Map<String, T> mapFromSetToAttributeValue(final String attributeName) {
		return mapFromSetToAttributeValue(attributeName, thingSet);
	}
	private final <T> Map<String, T> mapFromSetToAttributeValue(final String attributeName, final Set<? extends Thing> setToIterate) {
		final Map<String, T> mapping = new HashMap<String, T>();
		for (final Thing t : setToIterate) {
			T o = null;
			try {
				if (!t.containsAttribute(attributeName))
					continue;
				o = (T) t.getAttributeVal(attributeName);
			} catch (final AttributeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mapping.put(t.getName(), o);
		}
		return mapping;
	}
	/**
	 * Returns a map from the names of things of the given type to the values of their attributes of the provided name
	 * @param <T> the ParseType of the attribute
	 * @param attributeName the name of the attribute to get the values for
	 * @return the map from names to attribute value for the provided attribute type
	 */
	public final <T> Map<String, T> mapFromSetToAttributeValue(final String attributeName, final ThingType type) {
		return mapFromSetToAttributeValue(attributeName, getAppropriateSet(type));
	}
	private final Set<String> getThingsWithAttributeVal(final String attributeName, final Object desiredValue, final Set<? extends Thing> setToIterate) {
		return mapFromSetToAttributeValue(attributeName, setToIterate).entrySet().stream()
				.filter(p -> p.getValue().equals(desiredValue)).
				collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).keySet();
	}
	/**
	 * Returns the set of things with the desired attribute value
	 * @param attributeName the name of the attribute
	 * @param desiredValue the desired value for the attribute
	 * @return the set of all things that have that attribute and have the desired value for that attribute
	 */
	public final Set<String> getThingsWithAttributeVal(final String attributeName, final Object desiredValue) {
		return getThingsWithAttributeVal(attributeName, desiredValue, thingSet);
	}
	/**
	 * Returns the set of things of the given type with the desired attribute value
	 * @param attributeName the name of the attribute
	 * @param desiredValue the desired value for the attribute
	 * @return the set of all things that have that attribute and have the desired value for that attribute
	 */
	public final Set<String> getThingsWithAttributeVal(final String attributeName, final Object desiredValue, final ThingType type) {
		return mapFromSetToAttributeValue(attributeName, getAppropriateSet(type)).entrySet().stream()
				.filter(p -> p.getValue().equals(desiredValue)).
				collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).keySet();
	}
	private Set<? extends Thing> getAppropriateSet(final ThingType type) {
		Set<? extends Thing> setToIterate = null;
		switch(type) {
		case ITEM:
			setToIterate = itemSet;
			break;
		case POKEMON:
			setToIterate = pokemonSet;
			break;
		}
		return setToIterate;
	}

	private final class PokemonEvolutionLoader {
		String pathToEvolutions, pathToLevelsOfEvolve;
		/**
		 * There may be duplicate elements due to pokemon being weird, so we avoid this
		 */
		private final Set<String> namesLoaded = new HashSet<String>();
		public PokemonEvolutionLoader(final String pathToEvolutions, final String pathToLevelsOfEvolve) {
			this.pathToEvolutions = pathToEvolutions;
			this.pathToLevelsOfEvolve = pathToLevelsOfEvolve;
		}
		private void load() {
			try {
				for (final String[] values: CSVReader.readCSV(pathToLevelsOfEvolve)) {
					loadLevel(values);
				}
				for (final String[] values : CSVReader.readCSV(pathToEvolutions, -1)) {
					loadEvolution(values);
				}
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		private void loadLevel(final String[] values) {
			final String name = values[0];
			final String level = values[1];
			if (hasPokemon(name)) {
				if (!namesLoaded.contains(name)) {
					getPokemon(name).addAttributes(Attribute.generateAttributes(new String[] {"level of evolution", "has evolution"}, new String[] {level, "true"}));
					namesLoaded.add(name);
				}
			}
		}
		private void loadEvolution(final String[] values) {
			final String firstPokemon = values[0];
			String secondPokemon = values[1];
			String thirdPokemon = values[2];
			if (!hasPokemon(firstPokemon))
				return;
			final boolean hasSecond = !secondPokemon.equals("");
			final boolean hasThird = !thirdPokemon.equals("");
			String[] secondAsArray = {secondPokemon};
			try {
				if (hasSecond && hasPokemon(secondPokemon) && getPokemon(firstPokemon).containsAttribute("has evolution") && (Boolean) getPokemon(firstPokemon).getAttributeVal("has evolution")) {
					if (secondPokemon.startsWith("\"")) { //if it has multiple second evolutions it will be of form \"Aaa\r\nBbb\r\nCcc\r\n...\" we want to convert to [Aaa, Bbb, Ccc]
						secondPokemon = secondPokemon.replace("\n", "").replace("\r", "").replace("\"", "");
						secondAsArray = secondPokemon.split("(?=\\p{Lu})"); //split by uppercase letters
						getPokemon(firstPokemon).addAttribute(Attribute.generateAttribute("next evolutions", Arrays.toString(secondAsArray)));
					} else {
						getPokemon(firstPokemon).addAttribute(Attribute.generateAttribute("next evolutions", secondPokemon));
					}
				}
				if (hasThird && hasPokemon(thirdPokemon)) {
					int i =0;
					for (final String secPoke : secondAsArray) {
						if (getPokemon(secPoke).containsAttribute("has evolution") && (Boolean) getPokemon(secPoke).getAttributeVal("has evolution")) {
							if (thirdPokemon.startsWith("\"")) {
								thirdPokemon = thirdPokemon.replace("\n", "").replace("\r", "").replace("\"", "");
								final String[] thirdAsArray = thirdPokemon.split("(?=\\p{Lu})");
								if (secondAsArray.length == 1)
									getPokemon(secPoke).addAttribute(Attribute.generateAttribute("next evolutions", Arrays.toString(thirdAsArray)));
								else if(secondAsArray.length==thirdAsArray.length) 
									getPokemon(secPoke).addAttribute(Attribute.generateAttribute("next evolutions", thirdAsArray[i++]));
								else
									throw new Error("SPECIAL CASE NOT ACCOUNTED FOR");
							} else {
								getPokemon(secPoke).addAttribute(Attribute.generateAttribute("next evolutions", thirdPokemon));
							}
						}
					}
				}
					
			} catch (final AttributeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private final class DescriptionLoader {
		String pathToDescriptions;
		public DescriptionLoader(final String pathToDescriptions) {
			this.pathToDescriptions = pathToDescriptions;

		}
		/**
		 * <br>Assumes inputs of form:</br>
		 * <br>Name: description</br>
		 * <br>Name: description</br>
		 * <br>...</br>
		 */
		private void load() {
			try {
				final Map<String, String> nameToDescription = new HashMap<String, String>();
				for (final String[] values : CSVReader.readCSV(pathToDescriptions, x -> x.replace("\"", ""), ":")) {
					if (values.length < 2)
						continue;
					final String name = values[0];
					if (!hasThing(name)) 
						continue;
					final String description = values[1].trim();
					getThing(name).addAttribute(Attribute.generateAttribute("flavor description", description));
					//nameToDescription.put(name, description);
				}	
				for (final Thing t: getThingSet()) {
					t.addAttribute(Attribute.generateAttribute("description"));
					//final StringBuilder descriptionBuilder = new StringBuilder();
					final String name = t.getName();
					if (nameToDescription.containsKey(name)) {  
						//descriptionBuilder.append(nameToDescription.get(name));
					}
					if (hasPokemon(name)) {
						//descriptionBuilder.append("\n" + generateStatDescriptions(getPokemon(name)));
					}
					if (eb.getEventDescription(name) != null) {
						getThing(name).addAttribute(Attribute.generateAttribute("event description", eb.getEventDescription(name)));
						//descriptionBuilder.append("\n" + eb.getEventDescription(name));
					}
					getThing(name).updateDescription();
					//getThing(name).addAttribute(Attribute.generateAttribute("description", descriptionBuilder.toString()));
				}
			
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		/*private String generateStatDescriptions(final Pokemon p) {
			final StringBuilder description = new StringBuilder();
			final TreeMap<Integer, String> orderedDisplays = new TreeMap<Integer, String>();
			for (final Attribute at: p.getAttributesOfType(AttributeType.DISPLAYTYPE)) {
				
				orderedDisplays.put(at.getDisplayOrderVal(), at.toString());
			}
			boolean firstTime = true;
			int j = 0;
			for (final Integer i : orderedDisplays.keySet()) {
				j++;
				if (firstTime) {
					if (j != orderedDisplays.keySet().size())
					description.append(orderedDisplays.get(i) + "\n");
					else
					description.append(orderedDisplays.get(i));
					firstTime = false;
				} else {
				if (j != orderedDisplays.keySet().size())
					description.append(orderedDisplays.get(i).toString() + "\n");
				else
					description.append(orderedDisplays.get(i).toString());
				}
				
			}
				
				
			return description.toString();
			
		}*/
	}
	private final class GenerateAttributes {
		private void generate(final Set<String> pokemon) {
			for (final String name: pokemon) {
				final Pokemon p = getPokemon(name);
				if (!p.containsAttribute("rarity"))
					throw new Error("Pokemon does not have a metric for rarity");
				int rarity = 0;
				try {
					rarity = (int) p.getAttributeVal("rarity");
				} catch (final AttributeNotFoundException e) {
					e.printStackTrace();
				}
				final String[] attributes = {"gpm", "gph", "popularity boost", "happiness"};
				//TODO: Change code so that I can allow myself to delete the below comment
				//Yes I know this is pretty fucking stupid that I'm converting it to string only to unconvert it to string again. I immensely regret setting up attributes the way I did, but I don't have the bandwidth to change it.
				final String[] values = {Integer.toString(calcGPM(rarity)), Integer.toString(calcGPH(rarity)), Integer.toString(calcPopularity(rarity)), Integer.toString(calcHappiness(rarity))};
				p.addAttributes(Attribute.generateAttributes(attributes, values));
				
			}
		}
		private int calcGPM(final int rarity) {
			if (rarity < 70)
				return 0;
			double percentChance = 0;
			if (rarity < 86 )
				percentChance = rarity-10;
			else
				percentChance = rarity-5;
			if (!GameUtils.testPercentChance(percentChance))
				return 0;
			if (GameUtils.testPercentChance(1) && rarity > 90)
				return 9;
			if (rarity < 86)
				return GameUtils.testPercentChance(30) ? 2 : 1;
			else if (rarity < 99)
				return GameUtils.testPercentChance(40) ? 3 : 2;
			else 
				return GameUtils.testPercentChance(50) ? 5 : 3;
		}
		
		private int calcGPH(final int rarity) {
			if (rarity == 99 && GameUtils.testPercentChance(2))
				return 60;
			return 5*(int)Math.log(rarity);
			
		}
		private int calcPopularity(final int rarity) {
			final int modifier = rarity == 99 ? 10 : (GameUtils.testPercentChance(20) ? 3 : 0);
			return (int)Math.pow(rarity, .7) + modifier;
		}
		private int calcHappiness(final int rarity) {
			return Math.max(2, rarity/10-2);
		}
	}
	
}
