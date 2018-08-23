package loaders;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import thingFramework.Thing;

public class ExtraAttributeLoader implements Loader {
	private final ThingMap thingMap;
	private final String[] pathsToExtraAttributes;
	private static final String POKEMON_SIGNIFY = "POKEMON";
	private static final String ITEM_SIGNIFY = "ITEM";
	private static final String ADD_FOR_REST_SIGNIFY = "ALL OTHERS";
	ExtraAttributeLoader(final ThingMap thingMap, final String[] pathsToExtraAttributes) {
		this.thingMap = thingMap;
		this.pathsToExtraAttributes = pathsToExtraAttributes;
	}
	@Override
	public void load() {
		for (final String path: pathsToExtraAttributes) {
			loadExtraAttributes(path);
		}
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
		try {
			final Set<String> visitedNames = new HashSet<String>();
			ThingType type = null;
			for (final String[] values : CSVReader.readCSV(pathToExtraAttributes)) {
				final String potentialInput = values[0].toUpperCase().trim();
				boolean onSignifyLine = false;
				if (potentialInput.equalsIgnoreCase(POKEMON_SIGNIFY)) {
					type = ThingType.POKEMON;
					onSignifyLine = true;
				}
				if (potentialInput.equalsIgnoreCase(ITEM_SIGNIFY)) {
					type = ThingType.ITEM;
					onSignifyLine = true;
				}
				if (onSignifyLine) {
					visitedNames.clear();
				}
				else if (potentialInput.equals(ADD_FOR_REST_SIGNIFY)) {
					loadAttributesForAllOthers(values, visitedNames, type);
				}
				else {
					visitedNames.add(values[0]);
					loadExtraAttribute(values);
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
			if (thingMap.viewMap().containsKey(name))
				thingMap.get(name).addAttribute(attribute, value);
		}
	}
	private void loadAttributesForAllOthers(final String[] values, final Set<String> visitedNames, final ThingType type) {
		for (int i =1 ; i < values.length; i+=2) {
			final String attribute = values[i];
			final String value = values[i+1];
			for (final Thing thing : thingMap.viewThings(type)) {
				if (!visitedNames.contains(thing.getName()))
					thing.addAttribute(attribute, value);
			}
		}
	}
}
