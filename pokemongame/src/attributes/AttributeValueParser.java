package attributes;

import java.util.Arrays;

import thingFramework.CreatureTypeSet;
import thingFramework.ExperienceGroup;

/**
 * Singleton class used to parse values for attributes
 * @author David O'Sullivan
 *
 */
public class AttributeValueParser {
	private final static AttributeValueParser INSTANCE = new AttributeValueParser();
	
	private AttributeValueParser() {
		
	}
	/**
	 * Returns the single instance of AttributeValueParser
	 * @return The single instance of AttributeValueParser
	 */
	public static AttributeValueParser getInstance() {
		return INSTANCE;
	}
	/**
	 * Parses the provided string to the appropriate type of object given the ParseType
	 * @param <T> the return type
	 * @param value the String representation of the value
	 * @param parseType the associated ParseType
	 * @return the parsed value
	 */
	@SuppressWarnings("unchecked") //type safety provided by parseType
	public <T> T parseValue(final String value, final ParseType<T> parseType) {
		Object newVal = null;
			switch (parseType.getAssociatedEnum()) {
			case INTEGER:
				newVal = Integer.parseInt(value);
				break;
			case DOUBLE:
				newVal = Double.parseDouble(value);
				break;
			case STRING:
				newVal = value;
				break;
			case BOOLEAN:
				newVal = Boolean.parseBoolean(value);
				break;
			case EXPERIENCE_GROUP:
				newVal = ExperienceGroup.valueOf(value.toUpperCase().replaceAll("\\s", ""));
				break;
			case LIST:
				if (value.startsWith("[")) //if more than one element
					newVal = Arrays.asList(value.substring(1, value.length()-1).split("\\s*,\\s*"));
				else
					newVal = Arrays.asList(value);
				break;
			case CREATURE_TYPES:
				newVal = new CreatureTypeSet(value.split(" "));
				break;
			}
			return (T) newVal;
	}
	/**
	 * Creates a new AttributeCharacteristicSet by parsing in from an input
	 * @param value the String representation of the AttributeCharacteristicSet
	 * @param delimiter the delimiter between elements of the AttributeCharacteristicSet
	 * @return the parsed AttributeCharacteristicSet
	 */
	AttributeCharacteristicSet parseAttributeTypeSet(final String value, final String delimiter) {
		final String[] types = value.split(delimiter);
		final AttributeCharacteristicSet atTypes = new AttributeCharacteristicSet();
		for (final String type: types)
			atTypes.addValue(AttributeCharacteristic.valueOf(type.trim().toUpperCase()));
		return atTypes;
	}
		
}

