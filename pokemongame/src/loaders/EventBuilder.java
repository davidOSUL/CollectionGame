package loaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import effects.Event;
import gameutils.GameUtils;
import loaders.eventbuilder.generatedevents.TypicalEventFactory;

/**
 * Generates Events by taking in a path to a file where Thing names are mapped to events that correspond to them
 * @author David O'Sullivan
 */
public class EventBuilder implements Loader  {

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
	private final String[] paths;
	private static final int EVENT_NAME_LOC = 0;
	private static final int START_OF_EVENTS_LOC = 2;
	
	private static final int THING_NAME_LOC = 0;
	/**
	 * Constructs a new EventBuilder with no paths and no thingMap
	 */
	public EventBuilder() {
		paths = new String[] {};
	}
	/**
	 * Creates a new EventBuilder, creates "default items" and creates events for items in .csv file located at path
	 * @param paths the paths to get the events from
	 */
	public EventBuilder(final String... paths) {
		this.paths = paths;
	}
	/** 
	 * @see loaders.Loader#load()
	 */
	@Override
	public void load() {
		for (final String path : paths)
			loadEventsFromPath(path);
	}
	private void loadEventsFromPath(final String path) {
		List<String[]> CSV = null;
		try {
			CSV = CSVReader.readCSV(path);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String eventTypeName = CSV.get(EVENT_NAME_LOC)[EVENT_NAME_LOC];
		for (int i = START_OF_EVENTS_LOC; i < CSV.size(); i++) {
			final String[] values = CSV.get(i);
			final String name = values[THING_NAME_LOC];
			 final TypicalEventFactory eventFactory = TypicalEventFactory.getTypicalEventFactory(eventTypeName, values);
			 mapEvents.merge(name, GameUtils.toArrayList(eventFactory.generateEvent()), (o, v) -> {o.addAll(v); return o;});
			 eventNameToDescription.merge(name, eventFactory.getDescription(), (o, v) -> o + "\n" + v);
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

	/**
	 * Get the verbal description of all events built for the specified thing
	 * @param thingName the Name of thing to get description for
	 * @return the String describing those events. null if none
	 */
	public String getEventDescription(final String thingName) {
		return eventNameToDescription.get(thingName);
	}

}
