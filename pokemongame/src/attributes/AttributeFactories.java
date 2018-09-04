package attributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gameutils.GameUtils;
import interfaces.SerializableFunction;
import loaders.CSVReader;
import thingFramework.CreatureTypeSet;
import thingFramework.ExperienceGroup;
/**
 * Singleton class which houses all the AttributeFactories. These have public interfaces as AttributeCreator and ManagerMapCreator
 * @author David O'Sullivan
 *
 */
@SuppressWarnings("unused") 
final class AttributeFactories {
	/**
	 * the CSV file which contains all the types of Attributes that can be created
	 */
	private static final String ATTRIBUTE_LIST_PATH = "/InputFiles/attributeList - 1.csv";
	/**
	 * The delimiter used between multiple Attribute Characteristics a particular attribute
	 */
	private static final String ATTRIBUTE_TYPES_DELIM = ":";
	/**
	 * The delimiter used between multiple Display Settings for a particular attribute
	 */
	private static final String DISPLAY_SETTINGS_DELIM = ":";
	
	/*
	 * The location of the various Attribute values that will be loaded in from the CSV:
	 */
	private static final int NAME_LOC = 0;
	private static final int TYPE_LOC = 1;
	private static final int DEF_VAL_LOC = 2;
	private static final int ATTRIBUTE_TYPES_LOC = 3;
	private static final int IS_READABLE_LOC = 4;
	private static final int DISPLAY_NAME_LOC = 5;
	private static final int DISPLAY_SETTINGS_LOC = 6;
	private static final int IGNORE_VALUE_LOC = 7;
	private static final int DISPLAY_RANK_LOC = 8;
	
	/**
	 * Every Attribute has a displayRank, marking an Attribute's displayRank as FINAL_DISPLAY_RANK will 
	 * gurantee  its display rank is lower than any other
	 */
	private static final String FINAL_DISPLAY_RANK = "END";
	private static final AttributeFactories INSTANCE = new AttributeFactories();
	/*
	 * The various AttributeFactories  that this class houses
	 */
	private final AttributeFactory<Integer> INTEGER_FACTORY;
	private final AttributeFactory<Double> DOUBLE_FACTORY;
	private final AttributeFactory<String> STRING_FACTORY;
	private final AttributeFactory<Boolean> BOOLEAN_FACTORY;
	private final AttributeFactory<CreatureTypeSet> CREATURE_TYPES_FACTORY;
	private final AttributeFactory<ExperienceGroup> EXPERIENCE_GROUP_FACTORY;
	private final AttributeFactory<List<?>> LIST_FACTORY;

	/**
	 * Map between the name of a parse type, and the factory that uses it
	 */
	private final Map<String, AttributeFactory<?>> factoryMapByParseType;
	/**
	 *Map between the name of an Attribute, and the factory that created it
	 */
	private final Map<String, ManagerMapCreator<?>> factoryMapByNameOfAttributeTemplate;
	/**
	 * List of all factories
	 */
	private final List<ManagerMapCreator<?>> factoryList;
	private AttributeFactories() {
		factoryMapByParseType = new HashMap<String, AttributeFactory<?>>();
		factoryList = new ArrayList<ManagerMapCreator<?>>();
		factoryMapByNameOfAttributeTemplate = new HashMap<String, ManagerMapCreator<?>>();
		
		INTEGER_FACTORY = new AttributeFactory<Integer>(ParseType.INTEGER, x -> x >= 0);
		DOUBLE_FACTORY = new AttributeFactory<Double>(ParseType.DOUBLE, x -> x >= 0);
		STRING_FACTORY = new AttributeFactory<String>(ParseType.STRING);
		BOOLEAN_FACTORY = new AttributeFactory<Boolean>(ParseType.BOOLEAN, x -> x);
		CREATURE_TYPES_FACTORY = new AttributeFactory<CreatureTypeSet>(ParseType.CREATURE_TYPES);
		EXPERIENCE_GROUP_FACTORY = new AttributeFactory<ExperienceGroup>(ParseType.EXPERIENCE_GROUP);
		LIST_FACTORY = new AttributeFactory<List<?>>(ParseType.LIST);
		loadAttributeTemplates();
	}
	
	/**
	 * @return the AttributeFactories instance.
	 */
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
			factoryMapByParseType.get(values[TYPE_LOC].trim().toLowerCase()).createNewAttributeTemplate(name, values);
		}
	}
	/**
	 * Get the List of all ManagerMapCreators.
	 * @return the list of all ManagerMapCreators.
	 */
	List<ManagerMapCreator<?>> getManagerMapCreatorList() {
		return factoryList;
	}
	/**
	 * Returns the ManagerMapCreator that has attributes of the type of the specified attributeName
	 * @param attributeName the name of the Attribute 
	 * @return the ManagerMapCreator that can contain an Attribute of the specified attributeName
	 */
	ManagerMapCreator<?> getManagerMapCreatorOfAttribute(final String attributeName) {
		return factoryMapByNameOfAttributeTemplate.get(attributeName);
	}
	private void finishFactorySetup(final AttributeFactory<?> factory) {
		factoryMapByParseType.put(factory.parseType.getAssociatedEnum().toString().toLowerCase(), factory);
		factoryList.add(factory);
	}
	/**
	 * A parameterized class that serves both to create new Attribute templates (AttributeCreator<T>), and also to 
	 * create a AttributeManagerMap<T> of the specified type (ManagerMapCreator<T>). 
	 * This doubling up of uses isn't great, however the alternative led to a lack of type-safety, so I elected for
	 * this instead.
	 * @author David O'Sullivan
	 *
	 * @param <T> the type of the attributes of this AtttributeFactory<T>
	 */
	private final class AttributeFactory<T> implements AttributeCreator<T>, ManagerMapCreator<T> {
		private final ParseType<T> parseType;
		private final Map<String, Attribute<T>> attributeTemplates = new HashMap<String, Attribute<T>>();
		private final AttributeManagerMap<T> attributeManagerMap;
		private SerializableFunction<T, Boolean> isPositive = x -> false;
		
		private AttributeFactory(final ParseType<T> parseType) {
			this.parseType = parseType;
			parseType.setAssociatedMapCreator(this);
			attributeManagerMap = new AttributeManagerMap<T>(parseType, this);
			finishFactorySetup(this);
		}	
		private AttributeFactory(final ParseType<T> parseType, final SerializableFunction<T, Boolean> isPositive) {
			this(parseType);
			this.isPositive = isPositive;
			
		}
		
		@Override
		public String toString() {
			return "The Attribute Factory For: " + parseType.getAssociatedEnum().toString();
		}
		
		/*
		 * Interface methods
		 */
		
		/** 
		 * @see attributes.ManagerMapCreator#getManagerMap()
		 */
		@Override
		public AttributeManagerMap<T> getManagerMap() {
			return attributeManagerMap;
		}
		/** 
		 * @see attributes.AttributeCreator#getAttributeTemplate(java.lang.String)
		 */
		@Override
		public AttributeTemplate<T> getAttributeTemplate(final String attributeName) {
			throwIfInvalidTemplate(attributeName);
			return AttributeTemplate.generateTemplate(attributeTemplates.get(attributeName));
		}
		/** 
		 * @see attributes.AttributeCreator#containsAttributeTemplate(java.lang.String)
		 */
		@Override
		public boolean containsAttributeTemplate(final String attributeName) {
			return attributeTemplates.containsKey(attributeName);
		}
		
		/*
		 * Private methods
		 */
		
		private void throwIfInvalidTemplate(final String attributeName) {
			if (!attributeTemplates.containsKey(attributeName))
				throw new AttributeNotFoundException(attributeName + "is not a valid Attribute");
		}
		private void createNewAttributeTemplate(final String name, final String[] values) {
			final Attribute<T> attribute;
			if (values[IS_READABLE_LOC].equalsIgnoreCase("yes")) {
				attribute = generateReadableAttributeTemplate(values);
			}
			else {
				attribute = generateBasicAttributeTemplate(values);
			}
			attribute.setName(name);
			attributeTemplates.put(name, attribute);
			factoryMapByNameOfAttributeTemplate.put(name, this);
		}
		
		private Attribute<T> generateBasicAttributeTemplate(final String[] values) {
			final Attribute<T> attribute = new Attribute<T>(parseType);
			addBasicAttributeTemplateDetails(values, attribute);
			return attribute;
		}
		private ReadableAttribute<T> generateReadableAttributeTemplate(final String[] values) {
			final ReadableAttribute<T> attribute = new ReadableAttribute<T>(parseType);
			addBasicAttributeTemplateDetails(values, attribute);
			addReadableAttributeTemplateDetails(values, attribute);
			return attribute;
		}
		private void addBasicAttributeTemplateDetails(final String[] values, final Attribute<T> attribute) {
			attribute.setDefaultValue(AttributeValueParser.getInstance().parseValue(values[DEF_VAL_LOC], parseType));
			attribute.setIsPositiveFunction(isPositive);
			if (!arrayContainsValue(values, ATTRIBUTE_TYPES_LOC)) {
				attribute.setAttributeTypeSet(new AttributeCharacteristicSet());
			}
			else {
				attribute.setAttributeTypeSet(AttributeValueParser.getInstance().parseAttributeTypeSet(values[ATTRIBUTE_TYPES_LOC], ATTRIBUTE_TYPES_DELIM));
			}
		}
		private void addReadableAttributeTemplateDetails(final String[] values, final ReadableAttribute<T> attribute) {
			if (arrayContainsValue(values, DISPLAY_SETTINGS_LOC)) {
				attribute.parseAndSetSettings(values[DISPLAY_SETTINGS_LOC], DISPLAY_SETTINGS_DELIM);
			}
			
			attribute.setDisplayName(values[DISPLAY_NAME_LOC]);
			
			if (arrayContainsValue(values, DISPLAY_RANK_LOC)) {
				final Integer displayRank = values[DISPLAY_RANK_LOC].equals(FINAL_DISPLAY_RANK) ? Integer.MAX_VALUE : Integer.parseInt(values[DISPLAY_RANK_LOC]);
				attribute.setDisplayRank(displayRank);
			}
			
			if (arrayContainsValue(values, IGNORE_VALUE_LOC)) {
				attribute.setIgnoreValue(AttributeValueParser.getInstance().parseValue(values[IGNORE_VALUE_LOC], parseType));
			}
			setDisplayFormat(attribute);
			attribute.setIsVisible(true);
			
		}
		private void setDisplayFormat(final ReadableAttribute<T> attribute) {
			switch(parseType.getAssociatedEnum()) {
			case BOOLEAN:
				attribute.setDisplayFormat(s -> s.equalsIgnoreCase("true") ? "yes" : "no");
				break;
			case CREATURE_TYPES:
				attribute.setDisplayFormat(s -> GameUtils.toTitleCase(s.replace("[", "").replace("]", "").toLowerCase()));
				break;
			default:
				break;
			}
		}
		

	}
	
}
	

