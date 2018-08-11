package attributes;

import thingFramework.AttributeCharacteristic;
import thingFramework.AttributeCharacteristicSet;

class AttributeValueParser {
	private final static AttributeValueParser INSTANCE = new AttributeValueParser();
	
	private AttributeValueParser() {
		
	}
	static AttributeValueParser getInstance() {
		return INSTANCE;
	}
	<T> T parseValue(final String value, final ParseType<T> parseType) {
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
			/*case POKEMONTYPE:
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
				break;*/
			
			}
			return (T) newVal;
	}
	AttributeCharacteristicSet parseAttributeTypeSet(final String value, final String delimiter) {
		final String[] types = value.split(delimiter);
		final AttributeCharacteristicSet atTypes = new AttributeCharacteristicSet();
		for (final String type: types)
			atTypes.addAttributeType(AttributeCharacteristic.valueOf(type.trim().toUpperCase()));
		return atTypes;
	}
		
}

