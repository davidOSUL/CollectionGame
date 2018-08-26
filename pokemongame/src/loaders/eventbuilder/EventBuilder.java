package loaders.eventbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import effects.ActOnHolderEvent;
import effects.Event;
import gameutils.GameUtils;
import gui.guiutils.GuiUtils;
import loaders.CSVReader;
import loaders.eventbuilder.generatedevents.TypicalEventFactory;

/**
 * Generates Events by taking in a path to a file where Thing names are mapped to events that correspond to them
 * @author David O'Sullivan
 */
public class EventBuilder {

	/**
	 * The Map between the name of the Thing and all the non-held events associated with it
	 */
	private final Map<String, List<Event>> mapEvents = new HashMap<String, List<Event>>();
	/**
	 * The Map between the name of the Thing and the description associated with it because it has a generated event
	 */
	private final Map<String, String> eventNameToDescription = new HashMap<String, String>();
	/**
	 * Creates a new EventBuilder and places all default items to corresponding events
	 */
	public EventBuilder() {
	//Put "Special Items" (items that are one-ofs and can't be described by generator functions) here
		placeExplosionEvent();
		
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
				final List<Event> events = new ArrayList<Event>();
				for (int i = 1; i < vals.length; i++) {
					description.append(newline);
					final String[] inputs = vals[i].split(":"); //e.g. randomgold:3:4:5, where 3,4,5 are the inputs to the generate function
					final TypicalEventFactory eventFactory = TypicalEventFactory.getTypicalEventFactory(inputs);
					events.add(eventFactory.generateEvent());
					description.append(eventFactory.getDescription());
					newline = "\n";
				} 
				mapEvents.put(name, events); //place the created event
				eventNameToDescription.put(name, description.toString());
			}
		} catch (final NumberFormatException | IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * Get all non-held events built for the thing with the given name. Returns null if none are present
	 * @param name the name of the thing
	 * @return the list of events for that thing, null if none
	 */
	public List<Event> getNewEvents(final String name) {
		final List<Event> newEvents = new ArrayList<Event>();
		final List<Event> templateEvents = mapEvents.get(name);
		if (templateEvents != null) {
			templateEvents.forEach( e -> {
				if (e != null) 
					newEvents.add(e.makeCopy());
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
	private void placeExplosionEvent() {
		final Event explosivesEvent = new ActOnHolderEvent(
				board -> {
					final int i = board.removeAllPokemon(); //remove all pokemon
					board.addGold(i*100); //add 100 for all pokemon removed
				}, 
				x->{}, 
				(t, e, b) -> {},
				(t, e, b) -> {
					b.addToRemoveRequest(t); //request to remove this object
				});
		mapEvents.put("Explosives", GameUtils.toArrayList((explosivesEvent)));
		eventNameToDescription.put("Explosives", "Permanently Removes all Pokemon on the board.\n"
				+ "For each pokemon removed this way you get <font color=\"green\">+" + GuiUtils.getMoneySymbol() + 100 );
	}

}
