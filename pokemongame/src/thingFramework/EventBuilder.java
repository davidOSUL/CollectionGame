package thingFramework;

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
import game.Board;

public class EventBuilder {
	
	private Map<String, List<Event>> mapEvents = new HashMap<String, List<Event>>();
	public EventBuilder() {
		//TODO: Put "Special Items" (items that are one-ofs and can't be described by generator functions) here
		mapEvents.put("Small Table", new ArrayList<Event>(Arrays.asList(generateRandomGoldEvent(30, 20, 5))));
	}
	public EventBuilder(String path) {
		this();
		Path p = FileSystems.getDefault().getPath(path);
		loadEventsFromPath(p);
	}
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
				String type = inputs[0];
				TypicalEvents te = TypicalEvents.valueOf(type.toUpperCase().trim());
				int lower = te.getLower();
				int upper = te.getUpper();
				Event e = null;
				switch (te) {
				case RANDOMGOLD:
					int[] integerInputs = parseAllInRangeToInt(inputs, lower, upper-1);
					e = generateRandomGoldEvent(integerInputs[0], integerInputs[1], Double.parseDouble(inputs[upper]));
					break;
				}
				if (e != null)
					events.add(e);
				else
					throw new Error("ISSUE ADDING EVENT TO EVENTFULITEM: " + name);
			}
			mapEvents.put(name, events);
		}
		
	}
	public List<Event> getEvents(String name) {
		return mapEvents.get(name);
	}
	private int[] parseAllInRangeToInt(String[] input, int LowerIndex, int UpperIndex) {
		int[] output = new int[input.length];
		for (int i = LowerIndex; i <= UpperIndex; i++) {
			output[i] = Integer.parseInt(input[i]);
		}
		return output;
	}
	public static Event generateRandomGoldEvent(int percentChance, int gold, double periodInMinutes) {
		Event randomGold = new Event(board -> {
			int randomNum = ThreadLocalRandom.current().nextInt(1, 100+1); //num between 1, 100
			if (randomNum > (100-percentChance))
				board.addGold(gold);
		}, periodInMinutes);
		return randomGold;
	}
	
	
	private enum TypicalEvents {
		RANDOMGOLD(1, 3);
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
