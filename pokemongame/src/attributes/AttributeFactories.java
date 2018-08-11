package attributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loaders.CSVReader;
import thingFramework.AttributeCharacteristicSet;
import thingFramework.AttributeNotFoundException;
final class AttributeFactories {
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
	
	private static final AttributeFactories INSTANCE = new AttributeFactories();
	
	final AttributeFactory<Integer> INTEGER_FACTORY = new AttributeFactory<Integer>(ParseType.INTEGER);
	final AttributeFactory<Double> DOUBLE_FACTORY = new AttributeFactory<Double>(ParseType.DOUBLE);
	final AttributeFactory<String> STRING_FACTORY = new AttributeFactory<String>(ParseType.STRING);
	
	private final Map<String, AttributeFactory<?>> factoryMapByName = new HashMap<String, AttributeFactory<?>>();
	private final List<AttributeFactory<?>> factoryList = new ArrayList<AttributeFactory<?>>();
	private final Map<String, Attribute<?>> allAttributeTemplates = new HashMap<String, Attribute<?>>();
	private AttributeFactories() {
		loadAttributeTemplates();
	}
	
	static AttributeFactories getInstance() {
		return INSTANCE;
	}
	private static boolean arrayContainsValue(final String[] values, final int location) {
		return values.length > location && !values[location].trim().equals("");
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
			factoryMapByName.get(values[TYPE_LOC].trim().toLowerCase()).createNewReadableAttributeTemplate(name, values);
		}
	}
	List<AttributeFactory<?>> getFactoryList() {
		return factoryList;
	}
	AttributeFactory<?> getCreatorFactory(final String attributeName) {
		return allAttributeTemplates.get(attributeName).getCreatorFactory();
	}
	final class AttributeFactory<T> {
		private final ParseType<T> parseType;
		private final Map<String, Attribute<T>> attributeTemplates = new HashMap<String, Attribute<T>>();
		private final Map<AttributeManager, Map<String, Attribute<T>>> associatedAttributeManagers = new HashMap<AttributeManager, Map<String, Attribute<T>>>();
		private AttributeFactory(final ParseType parseType) {
			this.parseType = parseType;
			factoryMapByName.put(parseType.getAssociatedEnum().toString().toLowerCase(), this);
			factoryList.add(this);
		}	
		void addNewManager(final AttributeManager manager) {
			associatedAttributeManagers.put(manager, new HashMap<String, Attribute<T>>());
		}
		void generateAttributeForManager(final AttributeManager manager, final String name) {
			throwIfInvalidTemplate(name);
			associatedAttributeManagers.get(manager).put(name, attributeTemplates.get(name).makeCopy());
		}
		private void throwIfInvalidTemplate(final String attributeName) {
			if (!attributeTemplates.containsKey(attributeName))
				throw new AttributeNotFoundException(attributeName + "is not a valid attribute");
		}
		T getAttributeValueForManager(final AttributeManager manager, final String name) {
			throwIfInvalidTemplate(name);
			if (!associatedAttributeManagers.get(manager).containsKey(name)) {
				throw new AttributeNotFoundException(name + " is a valid attribute, however it has not been generated for this manager (" + manager + ")");
			}
			return associatedAttributeManagers.get(manager).get(name).getValue();
		}
		void setAttributeValueForManager(final AttributeManager manager, final String name, final T value) {
			associatedAttributeManagers.get(manager).get(name).setValue(value);
		}
		void setAttributeValueForManagerFromParse(final AttributeManager manager, final String name, final String value) {
			associatedAttributeManagers.get(manager).get(name).setValue(AttributeValueParser.getInstance().parseValue(value, parseType));
		}
		ParseType<T> getParseType() {
			return parseType;
		}
		private void createNewReadableAttributeTemplate(final String name, final String[] values) {
			final ReadableAttribute<T> attribute = new ReadableAttribute<T>(this);
			addBasicAttributeDetails(values, attribute);
			addReadableAttributeDetails(values, attribute);
			attributeTemplates.put(name, attribute);
			allAttributeTemplates.put(name, attribute);
		}
		
		private void addBasicAttributeDetails(final String[] values, final Attribute<T> attribute) {
			attribute.setDefaultValue(AttributeValueParser.getInstance().parseValue(values[DEF_VAL_LOC], parseType));
			
			if (!arrayContainsValue(values, ATTRIBUTE_TYPES_LOC)) {
				attribute.setAttributeTypeSet(new AttributeCharacteristicSet());
			}
			else {
				attribute.setAttributeTypeSet(AttributeValueParser.getInstance().parseAttributeTypeSet(values[ATTRIBUTE_TYPES_LOC], ATTRIBUTE_TYPES_DELIM));
			}
			
			if (arrayContainsValue(values, IGNORE_VALUE_LOC)) {
				attribute.setIgnoreValue(AttributeValueParser.getInstance().parseValue(values[IGNORE_VALUE_LOC], parseType));
			}
		}
		private void addReadableAttributeDetails(final String[] values, final ReadableAttribute<T> attribute) {
			if (arrayContainsValue(values, DISPLAY_SETTINGS_LOC)) {
				attribute.parseAndSetSettings(values[DISPLAY_SETTINGS_LOC], DISPLAY_SETTINGS_DELIM);
			}
			
			attribute.setDisplayName(values[DISPLAY_NAME_LOC]);
			
			if (arrayContainsValue(values, DISPLAY_RANK_LOC)) {
				attribute.setDisplayRank(Integer.parseInt(values[DISPLAY_RANK_LOC]));
			}
			
			attribute.setIsVisible(values[IS_VISIBLE_LOC].equalsIgnoreCase("yes"));
		}

	}
	
}
	

/*
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
