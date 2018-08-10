package attribues;

import java.util.Arrays;
import java.util.EnumSet;

import thingFramework.AttributeType;
import thingFramework.AttributeTypeSet;
import thingFramework.ExperienceGroup;
import thingFramework.PokemonType;

public class AttributeValueParser {
	private final static AttributeValueParser INSTANCE = new AttributeValueParser();
	
	private AttributeValueParser() {
		
	}
	public static AttributeValueParser getInstance() {
		return INSTANCE;
	}
	public <T> T parseValue(final String value, final ParseType parseType) {
		Object newVal = null;
			switch (parseType) {
			case INTEGER:
				newVal = Integer.parseInt(value);
				break;
			case DOUBLE:
				newVal = Double.parseDouble(value);
				break;
			case STRING:
				newVal = value;
				break;
			case ENUMSETPOKEMONTYPE:
				final String[] types = value.split(" ");
				PokemonType firstType;
				final PokemonType[] poketypes = new PokemonType[types.length-1];
				firstType = PokemonType.valueOf(types[0].toUpperCase().trim());
				for (int i = 1; i < types.length; i++) {
					poketypes[i-1] = PokemonType.valueOf(types[i].toUpperCase().trim());
					
				}
				newVal = EnumSet.of(firstType, poketypes);
				break;
			case EXPERIENCEGROUP:
				newVal = ExperienceGroup.valueOf(value.toUpperCase().replaceAll("\\s", ""));
				break;
			case BOOLEAN:
				newVal = Boolean.parseBoolean(value);
				break;
			case LISTSTRING:
				if (value.startsWith("["))
					newVal = Arrays.asList(value.substring(1, value.length()-1).split("\\s*,\\s*"));
				else
					newVal = Arrays.asList(value);
				break;
			
			}
			return (T) newVal;
	}
	public AttributeTypeSet parseAttributeTypeSet(final String value, final String delimiter) {
		final String[] types = value.split(delimiter);
		final AttributeTypeSet atTypes = new AttributeTypeSet();
		for (final String type: types)
			atTypes.addAttributeType(AttributeType.valueOf(type.trim().toUpperCase()));
		return atTypes;
	}
		
}

