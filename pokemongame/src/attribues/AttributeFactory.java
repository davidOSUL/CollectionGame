package attribues;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loaders.CSVReader;
import thingFramework.AttributeTypeSet;

public class AttributeFactory {
	private static final AttributeFactory INSTANCE = new AttributeFactory();
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
	private final Map<String, IntegerAttribute> integerAttributes = new HashMap<String, IntegerAttribute>();
	private final Map<String, StringAttribute> stringAttributes = new HashMap<String, StringAttribute>();
	private final Map<String, DoubleAttribute> doubleAttributes = new HashMap<String, DoubleAttribute>();
	private AttributeFactory() {
		loadAttributeTemplates();
	}
	private void loadAttributeTemplates() {
		List<String[]> output = null;
		try {
			output = CSVReader.readCSV(ATTRIBUTE_LIST_PATH, true);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		for (final String[] values : output) {
			final String name = values[NAME_LOC];
			ReadableAttribute<?> attribute = null;
			switch (values[TYPE_LOC].trim().toLowerCase()) {
				case "integer": {
					final IntegerAttribute integerAttribute = new IntegerAttribute();
					addAttributeDetails(values, integerAttribute, ParseType.INTEGER);
					integerAttributes.put(name, integerAttribute);
					attribute = integerAttribute;
					break;
				}
				
				case "double": {
					final DoubleAttribute doubleAttribute = new DoubleAttribute();
					addAttributeDetails(values, doubleAttribute, ParseType.DOUBLE);
					doubleAttributes.put(name, doubleAttribute);
					attribute = doubleAttribute;
					break;
				}
				case "string": {
					final StringAttribute stringAttribute = new StringAttribute();
					addAttributeDetails(values, stringAttribute, ParseType.STRING);
					stringAttributes.put(name, stringAttribute);
					attribute = stringAttribute;
					break;
				}
			}
			if (arrayContainsValue(values, DISPLAY_SETTINGS_LOC))
				attribute.parseAndSetSettings(values[DISPLAY_SETTINGS_LOC], DISPLAY_SETTINGS_DELIM);
			attribute.setDisplayName(values[DISPLAY_NAME_LOC]);
			if (arrayContainsValue(values, DISPLAY_RANK_LOC))
				attribute.setDisplayRank(Integer.parseInt(values[DISPLAY_RANK_LOC]));
			attribute.setIsVisible(values[IS_VISIBLE_LOC].equalsIgnoreCase("yes"));
			
		}
	}
	private static boolean arrayContainsValue(final String[] values, final int location) {
		return values.length > location && !values[location].trim().equals("");
	}
	private static <T> void addAttributeDetails(final String[] values, final Attribute<T> attribute, final ParseType parseType) {
		attribute.setDefaultValue(AttributeValueParser.getInstance().parseValue(values[DEF_VAL_LOC], parseType));
		if (!arrayContainsValue(values, ATTRIBUTE_TYPES_LOC))
				attribute.setAttributeTypeSet(new AttributeTypeSet());
		else
			attribute.setAttributeTypeSet(AttributeValueParser.getInstance().parseAttributeTypeSet(values[ATTRIBUTE_TYPES_LOC], ATTRIBUTE_TYPES_DELIM));
		if (arrayContainsValue(values, IGNORE_VALUE_LOC))
			attribute.setIgnoreValue(AttributeValueParser.getInstance().parseValue(values[IGNORE_VALUE_LOC], parseType));
		 
	}
	public static AttributeFactory getInstance() {
		return INSTANCE;
	}
	
	public IntegerAttribute generateIntegerAttribute(final String name, final int value) {
		final IntegerAttribute newAttribute = integerAttributes.get(name).makeCopy();
		newAttribute.setValue(value);
		return newAttribute;
	}
	public DoubleAttribute generateDoubleAttribute(final String name, final double value) {
		final DoubleAttribute newAttribute = doubleAttributes.get(name).makeCopy();
		newAttribute.setValue(value);
		return newAttribute;
	}
	public StringAttribute generateStringAttribute(final String name, final String value) {
		final StringAttribute newAttribute = stringAttributes.get(name).makeCopy();
		newAttribute.setValue(value);
		return newAttribute;
	}
	
}
