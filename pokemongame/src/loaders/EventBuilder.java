package loaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import effects.Event;
import gameutils.GameUtils;

/**
 * Generates Events by taking in a path to a file where Thing names are mapped to events that correspond to them
 * @author DOSullivan
 */
public class EventBuilder {
	
	/**
	 * The Map between the name of the Thing and all the events associated with it
	 */
	private Map<String, List<Event>> mapEvents = new HashMap<String, List<Event>>();
	/**
	 * Creates a new EventBuilder and places all default items to corresponding events
	 */
	public EventBuilder() {
		//TODO: Put "Special Items" (items that are one-ofs and can't be described by generator functions) here
		mapEvents.put("Small Table", new ArrayList<Event>(Arrays.asList(generateRandomGoldEvent(30, 20, 5))));
	}
	/**
	 * Creates a new EventBuilder, creates "default items" and creates events for items in .csv file located at path
	 * @param path
	 */
	public EventBuilder(String path) {
		this();
		Path p = FileSystems.getDefault().getPath(path);
		loadEventsFromPath(p);
	}
	/**
	 * Loads all the events at the provided path
	 * @param p the path of the eventMapList.csv file
	 */
	private void loadEventsFromPath(Path p) {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(p, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String s: lines) {
			String[] vals = s.split(",");

			String name=vals[0]; //e.g. smalltable
			List<Event> events = new ArrayList<Event>();
			for (int i = 1; i < vals.length; i++) {
				String[] inputs = vals[i].split(":"); //e.g. randomgold:3:4:5, where 3,4,5 are the inputs to the generate function
				String type = inputs[0]; //the type of event
				TypicalEvents te = TypicalEvents.valueOf(type.toUpperCase().trim());
				int lower = te.getLower();
				int upper = te.getUpper();
				Event e = null;
				switch (te) {
				case RANDOMGOLD:
					int[] integerInputs = parseAllInRangeToInt(inputs, lower, upper-1); //upper-1 because the last input will be a double that we have to parse seperately
					e = generateRandomGoldEvent(integerInputs[0], integerInputs[1], Double.parseDouble(inputs[upper]));
					break;
				}
				if (e != null)
					events.add(e);
				else
					throw new Error("ISSUE ADDING EVENT TO EVENTFULITEM: " + name);
			}
			mapEvents.put(name, events); //place the created event
		}
		
	}
	public List<Event> getEvents(String name) {
		return mapEvents.get(name);
	}
	/**
	 * Returns a new array where the first index is Integer.parseInt(input[LowerIndex]), the last is Integer.parseInt(input[UpperIndex]),
	 * and in between is all the parsed values of the the values between those indices
	 * @param input the string array to parse
	 * @param LowerIndex the first index of the array to parse
	 * @param UpperIndex the last index of the array to the pase
	 * @return the parsed int array corresponding to the values between LowerIndex and UpperINdex
	 */
	private int[] parseAllInRangeToInt(String[] input, int LowerIndex, int UpperIndex) {
		int[] output = new int[UpperIndex-LowerIndex+1];
		int j = 0;
		for (int i = LowerIndex; i <= UpperIndex; i++) {
			output[j++] = Integer.parseInt(input[i]);
		}
		return output;
	}
	/**
	 * Generates an event that every periodInMinute minutes will with a percentChance chance add the specified amount of gold to the board
	 * @param percentChance the chance that gold is added
	 * @param gold the amount of gold to add
	 * @param periodInMinutes the frequency of checking if gold is added
	 * @return the created event
	 */
	public static Event generateRandomGoldEvent(int percentChance, int gold, double periodInMinutes) {
		Event randomGold = new Event(board -> {
			if (GameUtils.testPercentChance(percentChance))
				board.addGold(gold);
		}, periodInMinutes);
		return randomGold;
	}
	
	
	/**
	 * All TypicalEvents. Contains the lower index and the upper index of the parsed input line, where the inputs to the corresponding method that generates the event can be found
	 * @author DOSullivan
	 *
	 */
	private enum TypicalEvents {
		RANDOMGOLD(1, 3); //of the format randomgold:x:y:z, so x (the first) will be at 1 and y (the last) will be at 3
		/**
		 * The lower index of the set of parameters for the event's generator function
		 */
		private final int lower;
		/**
		 * The upper index of the set of parameters for the event's generator function
		 */
		private final int upper;
		private TypicalEvents(int lower, int upper) {
			this.lower = lower;
			this.upper = upper;
		}
		private int getLower() {
			return lower;
		}
		private int getUpper() {
			return upper;
		}
	}
}
