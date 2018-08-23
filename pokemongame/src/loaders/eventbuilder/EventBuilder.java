package loaders.eventbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import effects.ActOnHolderEvent;
import effects.Event;
import effects.HeldEvent;
import gameutils.GameUtils;
import gui.guiutils.GuiUtils;
import loaders.CSVReader;
import loaders.ThingLoadException;
import thingFramework.Thing;

/**
 * Generates Events by taking in a path to a file where Thing names are mapped to events that correspond to them
 * @author David O'Sullivan
 */
public class EventBuilder {

	/**
	 * The Map between the name of the Thing and all the non-held events associated with it
	 */
	private final Map<String, List<Event>> mapEvents = new HashMap<String, List<Event>>();
	private final Map<String, List<HeldEvent<Thing>>> mapHeldEvents = new HashMap<String, List<HeldEvent<Thing>>>();
	/**
	 * The Map between the name of the Thing and the description associated with it because it has a generated event
	 */
	private final Map<String, String> eventNameToDescription = new HashMap<String, String>();
	/**
	 * Creates a new EventBuilder and places all default items to corresponding events
	 */
	public EventBuilder() {
	//Put "Special Items" (items that are one-ofs and can't be described by generator functions) here
		final HeldEvent<Thing> explosivesEvent = new ActOnHolderEvent<Thing>(
				board -> {
					final int i = board.removeAllPokemon(); //remove all pokemon
					board.addGold(i*100); //add 100 for all pokemon removed
				}, 
				x->{}, 
				(t, e, b) -> {},
				(t, e, b) -> {
					b.addToRemoveRequest(t); //request to remove this object
				});
		mapHeldEvents.put("Explosives", GameUtils.toArrayList((explosivesEvent)));
		eventNameToDescription.put("Explosives", "Permanently Removes all Pokemon on the board.\n"
				+ "For each pokemon removed this way you get <font color=\"green\">+" + GuiUtils.getMoneySymbol() + 100  );
		
	}
	/**
	 * Creates a new EventBuilder, creates "default items" and creates events for items in .csv file located at path
	 * @param path
	 */
	public EventBuilder(final String path) {
		this();
		loadEventsFromPath(path);
	}
	/**
	 * Loads all the events at the provided path
	 * @param p the path of the eventMapList.csv file
	 */
	private void loadEventsFromPath(final String path) {			
		try {
			for (final String[] vals: CSVReader.readCSV(path)) {
				final StringBuilder description = new StringBuilder();
				String newline = "";
				final String name=vals[0]; //e.g. smalltable
				final List<Event> regEvents = new ArrayList<Event>();
				final List<HeldEvent<Thing>> heldEvents = new ArrayList<HeldEvent<Thing>>();
				for (int i = 1; i < vals.length; i++) {
					description.append(newline);
					final String[] inputs = vals[i].split(":"); //e.g. randomgold:3:4:5, where 3,4,5 are the inputs to the generate function
					final TypicalEvent typical = TypicalEvent.generateEvent(inputs);
					if (typical.isHeldEvent()) {
						if (typical.getHeldEvent() != null)
							heldEvents.add(typical.getHeldEvent());
						else
							throw new ThingLoadException("Issue adding event to: " + name);
					}
					else {
						if (typical.getRegularEvent() != null)
							regEvents.add(typical.getRegularEvent());
						else
							throw new ThingLoadException("Issue adding event to: " + name);
					}

					description.append(typical.getDescription());
					newline = "\n";
				}
				mapEvents.put(name, regEvents); //place the created event
				mapHeldEvents.put(name, heldEvents);
				eventNameToDescription.put(name, description.toString());
			}
		} catch (final NumberFormatException | IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * Get all held events built for the thing with the given name. Returns null if none are present
	 * @param name the name of the thing
	 * @return the list of events for that thing, null if none
	 */
	public List<HeldEvent<Thing>> getNewHeldEvents(final String name) {
		final List<HeldEvent<Thing>> newEvents = new ArrayList<HeldEvent<Thing>>();
		final List<HeldEvent<Thing>> templateEvents = mapHeldEvents.get(name);
		if (templateEvents != null) {
			templateEvents.forEach( e -> {
				if (e != null) 
					newEvents.add(e.makeCopy());
			});
		}
		return newEvents;
	}
	/**
	 * Get all non-held events built for the thing with the given name. Returns null if none are present
	 * @param name the name of the thing
	 * @return the list of events for that thing, null if none
	 */
	public List<Event> getNewRegularEvents(final String name) {
		final List<Event> newEvents = new ArrayList<Event>();
		final List<Event> templateEvents = mapEvents.get(name);
		if (templateEvents != null) {
			templateEvents.forEach( e -> {
				if (e != null) 
					newEvents.add(new Event(e));
			});
		}
		return newEvents;
	}
	/**
	 * Get the verbal description of all events built for the specified thing
	 * @param thingName the Name of thing to get description for
	 * @return the String describing those events. null if none
	 */
	public String getEventDescription(final String thingName) {
		return eventNameToDescription.get(thingName);
	}
	/**
	 * Generates an event that every periodInMinute minutes will with a percentChance chance add the specified amount of gold to the board
	 * @param percentChance the chance that gold is added
	 * @param gold the amount of gold to add
	 * @param periodInMinutes the frequency of checking if gold is added
	 * @return the created event
	 */
	public static Event generateRandomGoldEvent(final int percentChance, final int gold, final double periodInMinutes) {
		final Event randomGold = new Event( board -> {
			if (GameUtils.testPercentChance(percentChance))
				board.addGold(gold);
		}, periodInMinutes);
		return randomGold;
	}
	/**
	 * Generates an event that increases the % chance of legendary pokemon spawning by increase (% 0-100)
	 * @param increase the percentage to increase by (0-100)
	 * @return the created event
	 */
	public static Event generateLegendaryChanceIncreaseEvent(final int increase) {
		return new Event(board -> board.increaseLegendaryChance(increase), board -> board.decreaseLegendaryChance(increase));

	}

}
