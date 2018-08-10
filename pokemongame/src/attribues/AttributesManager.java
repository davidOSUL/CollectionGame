package attribues;

import java.util.HashMap;
import java.util.Map;

import attributeTypes.enumAttributes.ExperienceGroupAttribute;
import attributeTypes.listAttributes.ListStringAttribute;
import attributeTypes.regularAttributes.BooleanAttribute;
import attributeTypes.regularAttributes.DoubleAttribute;
import attributeTypes.regularAttributes.IntegerAttribute;
import attributeTypes.regularAttributes.StringAttribute;
import attributeTypes.setAttributes.PokemonTypeAttribute;

public class AttributesManager {
	private final Map<String, IntegerAttribute> integerAttributes = new HashMap<String, IntegerAttribute>();
	private final Map<String, StringAttribute> stringAttributes = new HashMap<String, StringAttribute>();
	private final Map<String, DoubleAttribute> doubleAttributes = new HashMap<String, DoubleAttribute>();
	private final Map<String, BooleanAttribute> booleanAttributes = new HashMap<String, BooleanAttribute>();
	private final Map<String, ListStringAttribute> listStringAttributes = new HashMap<String, ListStringAttribute>();
	private final Map<String, ExperienceGroupAttribute> experienceGroupAttributes = new HashMap<String, ExperienceGroupAttribute>();
	private final Map<String, PokemonTypeAttribute> pokemonTypeAttribute = new HashMap<String, PokemonTypeAttribute>();
	
	public <T> void addToAttributeMap(final ParseType type, ReadableAttribute<> attribute) {
		
	}
}
