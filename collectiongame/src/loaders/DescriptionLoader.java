package loaders;

import java.io.IOException;

import attributes.AttributeName;
import thingFramework.Thing;

/**
 * Loads in all the Descriptions of Things
 * @author David O'Sullivan
 *
 */
public class DescriptionLoader implements Loader {
	
		private final String pathToDescriptions;
		private final ThingMap thingMap;
		private final EventBuilder eventBuilder;
		private static final int DESC_INPUT_LENGTH = 2;
		private static final int NAME_LOC = 0;
		private static final int DESC_LOC = 1;
		/**
		 * Creates a new Description Loader
		 * @param pathToDescriptions the path to the CSV with descriptions
		 * @param thingMap the thingMap to add description attributes to 
		 * @param eventBuilder the eventBuilder to get event descriptions from 
		 */
		public DescriptionLoader(final String pathToDescriptions,final ThingMap thingMap, final EventBuilder eventBuilder) {
			this.pathToDescriptions = pathToDescriptions;
			this.thingMap = thingMap;
			this.eventBuilder = eventBuilder;

		}
		/**
		 * <br>Assumes inputs of form:</br>
		 * <br>Name: description</br>
		 * <br>Name: description</br>
		 * <br>...</br>
		 */
		@Override
		public void load() {
			try {
				for (final String[] values : CSVReader.readCSV(pathToDescriptions, x -> x.replace("\"", ""), ":")) {
					if (values.length < DESC_INPUT_LENGTH)
						continue;
					final String name = values[NAME_LOC];
					if (!thingMap.hasThing(name)) 
						continue;
					final String description = values[DESC_LOC].trim();
					thingMap.getThing(name).addAttribute(AttributeName.FLAVOR_DESCRIPTION, description);
				}	
				for (final Thing t: thingMap.viewThings()) {
					final String name = t.getName();
					if (eventBuilder.getEventDescription(name) != null) {
						thingMap.getThing(name).addAttribute(AttributeName.EVENT_DESCRIPTION, eventBuilder.getEventDescription(name));
					}
				}
			
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	
}
