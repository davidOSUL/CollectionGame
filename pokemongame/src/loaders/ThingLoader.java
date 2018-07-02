package loaders;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import effects.Event;
import gameutils.GameUtils;
import thingFramework.Attribute;
import thingFramework.AttributeNotFoundException;
import thingFramework.AttributeType;
import thingFramework.Item;
import thingFramework.Pokemon;
import thingFramework.Thing;

public final class ThingLoader {
	/**
	 * Tell thing loader to generate random attributes for this thing
	 */
	private static final String GEN_CODE = "RANDOMATTRIBUTES";
	private static final String POKE_SPRITE_LOC = "/sprites/pokemon/";
	private static final String ITEM_SPRITE_LOC = "/sprites/items/";
	private final EventBuilder eb;
	private  Set<Thing> thingSet = new HashSet<Thing>();
	private Set<Pokemon> pokemonSet = new HashSet<Pokemon>();
	private  Set<Item> itemSet = new HashSet<Item>();
	private Set<String> pokemonToGenerateAttributesFor = new HashSet<String>();
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
	private static final String[] THING_LIST_LOCATIONS = {"/InputFiles/pokemonList.csv", "/InputFiles/itemList.csv"};
	/**
	 * The location of all pregenerated "basic" events to load into the game. I.E.
	 * items that have events that can be described by methods in the ThingLoader class
	 */
	private static final String EVENT_MAP_LOCATION = "/InputFiles/eventMapList.csv";
	/**
	 * Location of csv containing extra attributes for things. Format as specified in thingloader
	 */
	private static final String[] EXTRA_ATTRIBUTE_LOCATIONS = {"/InputFiles/extraAttributes.csv"};
	private static final String PATH_TO_DESCRIPTIONS = "/InputFiles/descriptionList.csv";
	private static final ThingLoader INSTANCE = new ThingLoader(THING_LIST_LOCATIONS, PATH_TO_DESCRIPTIONS, EVENT_MAP_LOCATION, EVOLUTIONS_LOCATION, LEVELS_OF_EVOLUTION_LOCATION, EXTRA_ATTRIBUTE_LOCATIONS);
	private ThingLoader(String[] pathToThings) {
		this(pathToThings, null);
	}
	private ThingLoader(String[] pathToThings, String pathToEvents) {
		if (pathToEvents == null)
			eb = new EventBuilder();
		else
			eb = new EventBuilder(pathToEvents);
		for (String path : pathToThings)
			load(path);
		thingSet.addAll(thingMap.values());
		thingSet = Collections.unmodifiableSet(thingSet);
		pokemonSet.addAll(pokemonMap.values());
		pokemonSet = Collections.unmodifiableSet(pokemonSet);
		itemSet.addAll(itemMap.values());
		itemSet = Collections.unmodifiableSet(itemSet);
	}
	private ThingLoader(String[] pathToThings, String pathToDescriptions, String pathToEvents, String pathToEvolutions, String pathToLevelsOfEvolve, String... pathsToExtraAttributes) {
		this(pathToThings, pathToEvents);
		for (String path: pathsToExtraAttributes) {
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
	private void loadExtraAttributes(String pathToExtraAttributes) {
			CurrentIteratorValue civ = CurrentIteratorValue.UNKNOWN;
			try {
				for (String[] values : CSVReader.readCSV(pathToExtraAttributes)) {
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
	private void loadExtraAttribute(String[] values) {
		String name = values[0];
		for (int i =1 ; i < values.length; i+=2) {
			String attribute = values[i];
			String value = values[i+1];
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
	private void load(String path) {
		try {
			for (String[] values : CSVReader.readCSV(path)) {
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
	//TODO: Change all getPokemons, to getOrignalPokemons
	private void loadPokemon(String[] values) {
		String name = values[1];
		String texture = POKE_SPRITE_LOC + values[2];
		List<Event> listOfEvents = eb.getEvents(name);
		Set<Attribute> attributes = loadAttributes(values, 3, name);
		Pokemon pm = new Pokemon(name, texture, attributes, listOfEvents);
		thingMap.put(name, pm);
		pokemonMap.put(name, pm);
		
	}
	private void loadItem(String[] values) {
			String name = values[1];
			String texture = ITEM_SPRITE_LOC + values[2];
			List<Event> listOfEvents = eb.getEvents(name);
			Set<Attribute> attributes = loadAttributes(values, 3, name);
			Item i = new Item(name, texture, attributes, listOfEvents);
			thingMap.put(name, i);
			itemMap.put(name, i);
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
	public String getThingDescription(String thingName) {
		return thingMap.get(thingName).toString();
	}
	public String getThingImage(String thingName) {
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
	public Thing generateNewThing(String name) {
		return thingMap.get(name).makeCopy();
	}
	/**
	 * Creates a new instance of the Pokemon with the given name with all the characterstics that were loaded in on the game start
	 * @param name the name of pokemon
	 * @return The new thing. Throws NullPointerException if not present
	 */
	public Pokemon generateNewPokemon(String name) {
		return new Pokemon(pokemonMap.get(name));
	}
	/**
	 * Creates a new instance of the item with the given name with all the characterstics that were loaded in on the game start
	 * @param name the name of thing 
	 * @return The new thing. Throws NullPointerException if not present
	 */
	public Item generateNewItem(String name) {
		return new Item(itemMap.get(name));
	}
	
	/**
	 * Returns the originally created thing with the given name
	 * @param name the name of the thing
	 * @return the thing
	 */
	private Thing getThing(String name) {
		return thingMap.get(name);
	}
	/**
	 * Returns the originally created pokemon with the given name
	 * @param name the name of the pokemon
	 * @return the pokemon
	 */
	private Pokemon getPokemon(String name) {
		return pokemonMap.get(name);
	}
	
	/**
	 * @param name the name of the thing to lookup
	 * @return true if that thing was loaded in
	 */
	public boolean hasThing(String name) {
		return thingMap.containsKey(name);
	}
	/**
	 * @param name the name of the pokemon to lookup
	 * @return true if that Pokemon was loaded in
	 */
	public boolean hasPokemon(String name) {
		return pokemonMap.containsKey(name);
	}
	
	/**
	 * Currently serves no purpose in the logic past debugging
	 * @author David O'Sullivan
	 *
	 */
	private enum CurrentIteratorValue{
		POKEMON, ITEM, UNKNOWN
	}
	private final class PokemonEvolutionLoader {
		String pathToEvolutions, pathToLevelsOfEvolve;
		/**
		 * There may be duplicate elements due to pokemon being weird, so we avoid this
		 */
		private Set<String> namesLoaded = new HashSet<String>();
		public PokemonEvolutionLoader(String pathToEvolutions, String pathToLevelsOfEvolve) {
			this.pathToEvolutions = pathToEvolutions;
			this.pathToLevelsOfEvolve = pathToLevelsOfEvolve;
		}
		private void load() {
			try {
				for (String[] values: CSVReader.readCSV(pathToLevelsOfEvolve)) {
					loadLevel(values);
				}
				for (String[] values : CSVReader.readCSV(pathToEvolutions, -1)) {
					loadEvolution(values);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		private void loadLevel(String[] values) {
			String name = values[0];
			String level = values[1];
			if (hasPokemon(name)) {
				if (!namesLoaded.contains(name)) {
					getPokemon(name).addAttributes(Attribute.generateAttributes(new String[] {"level of evolution", "has evolution"}, new String[] {level, "true"}));
					namesLoaded.add(name);
				}
			}
		}
		private void loadEvolution(String[] values) {
			String firstPokemon = values[0];
			String secondPokemon = values[1];
			String thirdPokemon = values[2];
			if (!hasPokemon(firstPokemon))
				return;
			boolean hasSecond = !secondPokemon.equals("");
			boolean hasThird = !thirdPokemon.equals("");
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
					for (String secPoke : secondAsArray) {
						if (getPokemon(secPoke).containsAttribute("has evolution") && (Boolean) getPokemon(secPoke).getAttributeVal("has evolution")) {
							if (thirdPokemon.startsWith("\"")) {
								thirdPokemon = thirdPokemon.replace("\n", "").replace("\r", "").replace("\"", "");
								String[] thirdAsArray = thirdPokemon.split("(?=\\p{Lu})");
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
					
			} catch (AttributeNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private final class DescriptionLoader {
		String pathToDescriptions;
		public DescriptionLoader(String pathToDescriptions) {
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
				Map<String, String> nameToDescription = new HashMap<String, String>();
				for (String[] values : CSVReader.readCSV(pathToDescriptions, x -> x.replace("\"", ""), ":")) {
					if (values.length < 2)
						continue;
					String name = values[0];
					if (!hasThing(name)) 
						continue;
					String description = values[1].trim();
					nameToDescription.put(name, description);
				}	
				for (Thing t: getThingSet()) {
					StringBuilder descriptionBuilder = new StringBuilder();
					String name = t.getName();
					if (nameToDescription.containsKey(name)) {  
						descriptionBuilder.append(nameToDescription.get(name));
					}
					if (hasPokemon(name)) {
						descriptionBuilder.append("\n" + generateStatDescriptions(getPokemon(name)));
					}
					if (eb.getEventDescription(name) != null) {
						descriptionBuilder.append("\n" + eb.getEventDescription(name));
					}
					getThing(name).addAttribute(Attribute.generateAttribute("description", descriptionBuilder.toString()));
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		private String generateStatDescriptions(Pokemon p) {
			StringBuilder description = new StringBuilder();
			TreeMap<Integer, String> orderedDisplays = new TreeMap<Integer, String>();
			for (Attribute at: p.getAttributesOfType(AttributeType.DISPLAYTYPE)) {
				
				orderedDisplays.put(at.getDisplayOrderVal(), at.toString());
			}
			boolean firstTime = true;
			int j = 0;
			for (Integer i : orderedDisplays.keySet()) {
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
			
		}
	}
	private final class GenerateAttributes {
		private void generate(Set<String> pokemon) {
			for (String name: pokemon) {
				Pokemon p = getPokemon(name);
				if (!p.containsAttribute("rarity"))
					throw new Error("Pokemon does not have a metric for rarity");
				int rarity = 0;
				try {
					rarity = (int) p.getAttributeVal("rarity");
				} catch (AttributeNotFoundException e) {
					e.printStackTrace();
				}
				String[] attributes = {"gpm", "gph", "popularity boost", "happiness"};
				//TODO: Change code so that I can allow myself to delete the below comment
				//Yes I know this is pretty fucking stupid that I'm converting it to string only to unconvert it to string again. I immensely regret setting up attributes the way I did, but I don't have the bandwidth to change it.
				String[] values = {Integer.toString(calcGPM(rarity)), Integer.toString(calcGPH(rarity)), Integer.toString(calcPopularity(rarity)), Integer.toString(calcHappiness(rarity))};
				p.addAttributes(Attribute.generateAttributes(attributes, values));
				
			}
		}
		private int calcGPM(int rarity) {
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
		
		private int calcGPH(int rarity) {
			if (rarity == 99 && GameUtils.testPercentChance(2))
				return 60;
			return 5*(int)Math.log(rarity);
			
		}
		private int calcPopularity(int rarity) {
			int modifier = rarity == 99 ? 10 : (GameUtils.testPercentChance(20) ? 3 : 0);
			return (int)Math.pow(rarity, .7) + modifier;
		}
		private int calcHappiness(int rarity) {
			return Math.max(2, rarity/10-2);
		}
	}
	
}
