package loaders;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import effects.Event;
import game.Board;
import gameutils.GameUtils;

/**
 * Generates Events by taking in a path to a file where Thing names are mapped to events that correspond to them
 * @author David O'Sullivan
 */
public class EventBuilder {
	
	/**
	 * The Map between the name of the Thing and all the events associated with it
	 */
	private Map<String, List<Event>> mapEvents = new HashMap<String, List<Event>>();
	/**
	 * The Map between the name of the Thing and the description associated with it because it has a generated event
	 */
	private Map<String, String> eventNameToDescription = new HashMap<String, String>();
	/**
	 * Creates a new EventBuilder and places all default items to corresponding events
	 */
	public EventBuilder() {
		//TODO: Put "Special Items" (items that are one-ofs and can't be described by generator functions) here
		//mapEvents.put("Small Table", new ArrayList<Event>(Arrays.asList(generateRandomGoldEvent(30, 20, 5))));
	}
	/**
	 * Creates a new EventBuilder, creates "default items" and creates events for items in .csv file located at path
	 * @param path
	 */
	public EventBuilder(String path) {
		this();
		//Path p = FileSystems.getDefault().getPath(path);
		loadEventsFromPath(path);
	}
	/**
	 * Loads all the events at the provided path
	 * @param p the path of the eventMapList.csv file
	 */
	private void loadEventsFromPath(String path) {			
		try {
			for (String[] vals: CSVReader.readCSV(path)) {
				StringBuilder description = new StringBuilder();
				String newline = "";
				String name=vals[0]; //e.g. smalltable
				List<Event> events = new ArrayList<Event>();
				for (int i = 1; i < vals.length; i++) {
					description.append(newline);
					String[] inputs = vals[i].split(":"); //e.g. randomgold:3:4:5, where 3,4,5 are the inputs to the generate function
					String type = inputs[0]; //the type of event
					TypicalEvents te = TypicalEvents.valueOf(type.toUpperCase().trim());
					int lower = te.getLower();
					int upper = te.getUpper();
					Event e = null;
					switch (te) {
					case RANDOMGOLD:
						int[] integerInputs = GameUtils.parseAllInRangeToInt(inputs, lower, upper-1); //upper-1 because the last input will be a double that we have to parse seperately
						e = generateRandomGoldEvent(integerInputs[0], integerInputs[1], Double.parseDouble(inputs[upper]));
						description.append(te.getDescription(integerInputs[0], integerInputs[1], Double.parseDouble(inputs[upper])));
						break;
					}
					if (e != null)
						events.add(e);
					else
						throw new Error("ISSUE ADDING EVENT TO EVENTFULITEM: " + name);
					newline = "\n";
				}
				mapEvents.put(name, events); //place the created event
				eventNameToDescription.put(name, description.toString());
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Get all events built for the thing with the given name. Returns null if none are present
	 * @param name the name of the thing
	 * @return the list of events for that thing, null if none
	 */
	public List<Event> getEvents(String name) {
		return mapEvents.get(name);
	}
	/**
	 * Get the verbal description of all events built for the specified thing
	 * @param thingName the Name of thing to get description for
	 * @return the String describing those events. null if none
	 */
	public String getEventDescription(String thingName) {
		return eventNameToDescription.get(thingName);
	}
	/**
	 * Generates an event that every periodInMinute minutes will with a percentChance chance add the specified amount of gold to the board
	 * @param percentChance the chance that gold is added
	 * @param gold the amount of gold to add
	 * @param periodInMinutes the frequency of checking if gold is added
	 * @return the created event
	 */
	public static Event generateRandomGoldEvent(int percentChance, int gold, double periodInMinutes) {
		Event randomGold = new Event( board -> {
			if (GameUtils.testPercentChance(percentChance))
				board.addGold(gold);
		}, periodInMinutes);
		return randomGold;
	}
	
	
	/**
	 * All TypicalEvents. Contains the lower index and the upper index of the parsed input line, where the inputs to the corresponding method that generates the event can be found
	 * @author David O'Sullivan
	 *
	 */
	private enum TypicalEvents {
		RANDOMGOLD(1, 3, "Has a %d%% Chance of Generating %d PokeCash Every %.2f Minutes"); //of the format randomgold:x:y:z, so x (the first) will be at 1 and y (the last) will be at 3
		/**
		 * The lower index of the set of parameters for the event's generator function
		 */
		private final int lower;
		/**
		 * The upper index of the set of parameters for the event's generator function
		 */
		private final int upper;
		private final String descriptionTemplate;
		private TypicalEvents(int lower, int upper, String descriptionTemplate) {
			this.lower = lower;
			this.upper = upper;
			this.descriptionTemplate = descriptionTemplate;
		}
		private int getLower() {
			return lower;
		}
		private int getUpper() {
			return upper;
		}
		private String getDescription(Object ...args) {
			return String.format(descriptionTemplate, args);
		}
	}
}
