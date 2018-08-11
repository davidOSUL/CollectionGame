package attribues;

import java.util.HashMap;
import java.util.Map;

public final class AttributeFactory<T> {
	private static final String ATTRIBUTE_LIST_PATH = "/InputFiles/attributeList - 1.csv";
	private static final String ATTRIBUTE_TYPES_DELIM = ":";
	private static final String DISPLAY_SETTINGS_DELIM = ":";
	private static final int NAME_LOC = 0;
	private static final int TYPE_LOC = 1;
	private static final int DEF_VAL_LOC = 2;
	private static final int ATTRIBUTE_TYPES_LOC = 3;
	private static final int IS_VISIBLE_LOC = 4;
	private static final int DISPLAY_NAME_LOC = 5;
	private static final int DISPLAY_SETTINGS_LOC = 6;
	private static final int IGNORE_VALUE_LOC = 7;
	private static final int DISPLAY_RANK_LOC = 8;
	public static final AttributeFactory<Integer> INTEGER = new AttributeFactory<Integer>(ParseType.INTEGER);
	public static final AttributeFactory<Double> DOUBLE = new AttributeFactory<Double>(ParseType.DOUBLE);
	public static final AttributeFactory<String> STRING = new AttributeFactory<String>(ParseType.STRING);
	private final ParseType enumVersion;
	private final Map<String, Attribute<T>> attributes = new HashMap<String, Attribute<T>>();
	private AttributeFactory(final ParseType enumVersion) {
		this.enumVersion = enumVersion;
	}
	private static void load() {
		
	}
	
	
}/*
public enum ParseType{
	INTEGER(IntegerAttribute.class), DOUBLE(DoubleAttribute.class), STRING(StringAttribute.class), POKEMONTYPE(PokemonTypeAttribute.class), EXPERIENCEGROUP(ExperienceGroupAttribute.class), BOOLEAN(BooleanAttribute.class), LISTSTRING(ListStringAttribute.class);
	private Class<? extends ReadableAttribute<?>> associatedClass;
	private ParseType(Class<? extends ReadableAttribute<?>> associatedClass) {
		this.associatedClass = associatedClass;
	}
	public Class<? extends ReadableAttribute<?>> getAssociatedClass() {
		return associatedClass;
	}
	
}*/
