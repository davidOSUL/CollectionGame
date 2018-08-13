package attributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gameutils.GameUtils;
import interfaces.SerializableFunction;
import loaders.CSVReader;
import thingFramework.ExperienceGroup;
import thingFramework.PokemonTypeSet;
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
	
 	private final AttributeFactory<Integer> INTEGER_FACTORY;
	private final AttributeFactory<Double> DOUBLE_FACTORY;
	private final AttributeFactory<String> STRING_FACTORY;
	private final AttributeFactory<Boolean> BOOLEAN_FACTORY;
	private final AttributeFactory<PokemonTypeSet> POKEMON_TYPES_FACTORY;
	private final AttributeFactory<ExperienceGroup> EXPERIENCE_GROUP_FACTORY;
	private final AttributeFactory<List<?>> LIST_FACTORY;
	
	private final Map<String, AttributeFactory<?>> factoryMapByName ;
	private final List<AttributeFactory<?>> factoryList;
	private final Map<String, Attribute<?>> allAttributeTemplates;
	private AttributeFactories() {
		factoryMapByName = new HashMap<String, AttributeFactory<?>>();
		factoryList = new ArrayList<AttributeFactory<?>>();
		allAttributeTemplates = new HashMap<String, Attribute<?>>();
		
		INTEGER_FACTORY = new AttributeFactory<Integer>(ParseType.INTEGER, x -> x >= 0);
		DOUBLE_FACTORY = new AttributeFactory<Double>(ParseType.DOUBLE, x -> x >= 0);
		STRING_FACTORY = new AttributeFactory<String>(ParseType.STRING);
		BOOLEAN_FACTORY = new AttributeFactory<Boolean>(ParseType.BOOLEAN);
		POKEMON_TYPES_FACTORY = new AttributeFactory<PokemonTypeSet>(ParseType.POKEMON_TYPES);
		EXPERIENCE_GROUP_FACTORY = new AttributeFactory<ExperienceGroup>(ParseType.EXPERIENCE_GROUP);
		LIST_FACTORY = new AttributeFactory<List<?>>(ParseType.LIST);
		
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
		private SerializableFunction<T, Boolean> isPositive = x -> false;
		private AttributeFactory(final ParseType<T> parseType) {
			this.parseType = parseType;
			parseType.setAssociatedFactory(this);
			factoryMapByName.put(parseType.getAssociatedEnum().toString().toLowerCase(), this);
			factoryList.add(this);
		}	
		private AttributeFactory(final ParseType<T> parseType, final SerializableFunction<T, Boolean> isPositive) {
			this(parseType);
			this.isPositive = isPositive;
			
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
		Attribute<T> getAttributeForManager(final AttributeManager manager, final String name) {
			throwIfInvalidTemplate(name);
			if (!associatedAttributeManagers.get(manager).containsKey(name)) {
				throw new AttributeNotFoundException(name + " is a valid attribute, however it has not been generated for this manager (" + manager + ")");
			}
			return associatedAttributeManagers.get(manager).get(name);
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
			attribute.setIsPositiveFunction(isPositive);
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
			switch(parseType.getAssociatedEnum()) {
				case BOOLEAN:
					attribute.setDisplayFormat(s -> s.equalsIgnoreCase("true") ? "yes" : "no");
					break;
				case POKEMON_TYPES:
					attribute.setDisplayFormat(s -> GameUtils.toTitleCase(s.replace("[", "").replace("]", "").toLowerCase()));
					break;
				default:
					break;
			}
		}
		@Override
		public String toString() {
			return "The Attribute Factory For: " + parseType.getAssociatedEnum().toString();
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
