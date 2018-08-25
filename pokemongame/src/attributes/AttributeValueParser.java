package attributes;

import java.util.Arrays;

import thingFramework.ExperienceGroup;
import thingFramework.PokemonTypeSet;

public class AttributeValueParser {
	private final static AttributeValueParser INSTANCE = new AttributeValueParser();
	
	private AttributeValueParser() {
		
	}
	public static AttributeValueParser getInstance() {
		return INSTANCE;
	}
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
			case POKEMON_TYPES:
				newVal = new PokemonTypeSet(value.split(" "));
				break;
			}
			return (T) newVal;
	}
	AttributeCharacteristicSet parseAttributeTypeSet(final String value, final String delimiter) {
		final String[] types = value.split(delimiter);
		final AttributeCharacteristicSet atTypes = new AttributeCharacteristicSet();
		for (final String type: types)
			atTypes.addValue(AttributeCharacteristic.valueOf(type.trim().toUpperCase()));
		return atTypes;
	}
		
}

