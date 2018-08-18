package attributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gameutils.GameUtils;
import interfaces.SerializableConsumer;
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
	private static final int IS_READABLE_LOC = 4;
	private static final int DISPLAY_NAME_LOC = 5;
	private static final int DISPLAY_SETTINGS_LOC = 6;
	private static final int IGNORE_VALUE_LOC = 7;
	private static final int DISPLAY_RANK_LOC = 8;
	
	private static final String FINAL_DISPLAY_RANK = "END";
	private static final AttributeFactories INSTANCE = new AttributeFactories();
	
 	private final AttributeFactory<Integer> INTEGER_FACTORY;
	private final AttributeFactory<Double> DOUBLE_FACTORY;
	private final AttributeFactory<String> STRING_FACTORY;
	private final AttributeFactory<Boolean> BOOLEAN_FACTORY;
	private final AttributeFactory<PokemonTypeSet> POKEMON_TYPES_FACTORY;
	private final AttributeFactory<ExperienceGroup> EXPERIENCE_GROUP_FACTORY;
	private final AttributeFactory<List<?>> LIST_FACTORY;

	private final Map<String, AttributeFactory<?>> factoryMapByParseType;
	private final Map<String, AttributeFactory<?>> factoryMapByNameOfAttributeTemplate;
	private final List<AttributeFactory<?>> factoryList;
	private AttributeFactories() {
		factoryMapByParseType = new HashMap<String, AttributeFactory<?>>();
		factoryList = new ArrayList<AttributeFactory<?>>();
		factoryMapByNameOfAttributeTemplate = new HashMap<String, AttributeFactory<?>>();
		
		INTEGER_FACTORY = new AttributeFactory<Integer>(ParseType.INTEGER, x -> x >= 0);
		DOUBLE_FACTORY = new AttributeFactory<Double>(ParseType.DOUBLE, x -> x >= 0);
		STRING_FACTORY = new AttributeFactory<String>(ParseType.STRING);
		BOOLEAN_FACTORY = new AttributeFactory<Boolean>(ParseType.BOOLEAN, x -> x);
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
			factoryMapByParseType.get(values[TYPE_LOC].trim().toLowerCase()).createNewAttributeTemplate(name, values);
		}
	}
	List<AttributeFactory<?>> getFactoryList() {
		return factoryList;
	}
	AttributeFactory<?> getCreatorFactory(final String attributeName) {
		return factoryMapByNameOfAttributeTemplate.get(attributeName);
	}
	final class AttributeFactory<T> {
		private final ParseType<T> parseType;
		private final Map<String, Attribute<T>> attributeTemplates = new HashMap<String, Attribute<T>>();
		private final Map<AttributeManager, Map<String, Attribute<T>>> associatedAttributeManagers = new HashMap<AttributeManager, Map<String, Attribute<T>>>();
		private final Map<AttributeManager, SerializableConsumer<Attribute<T>>> doOnGenerations = new HashMap<AttributeManager, SerializableConsumer<Attribute<T>>>();
		private final Map<AttributeManager, List<AttributeManagerWatcher<T>>> attributeWatchers = new HashMap<AttributeManager, List<AttributeManagerWatcher<T>>>();
		private SerializableFunction<T, Boolean> isPositive = x -> false;
		private AttributeFactory(final ParseType<T> parseType) {
			this.parseType = parseType;
			parseType.setAssociatedFactory(this);
			factoryMapByParseType.put(parseType.getAssociatedEnum().toString().toLowerCase(), this);
			factoryList.add(this);
		}	
		private AttributeFactory(final ParseType<T> parseType, final SerializableFunction<T, Boolean> isPositive) {
			this(parseType);
			this.isPositive = isPositive;
			
		}
		Attribute<T> getAttributeTemplate(final String attributeName) {
			throwIfInvalidTemplate(attributeName);
			return attributeTemplates.get(attributeName);
		}
		
		void addNewManager(final AttributeManager manager) {
			associatedAttributeManagers.put(manager, new HashMap<String, Attribute<T>>());
			attributeWatchers.put(manager, new ArrayList<AttributeManagerWatcher<T>>());
		}
		void copyManagerToNewManager(final AttributeManager oldManager, final AttributeManager newManager) {
			for (final Map.Entry<String, Attribute<T>> entry : associatedAttributeManagers.get(oldManager).entrySet()) {
				addAttributeForManager(newManager, entry.getKey(), entry.getValue().makeCopy());
			}
		}
		void addNewWatcherForManager(final AttributeManager manager, final AttributeManagerWatcher<T> watcher) {
			attributeWatchers.get(manager).add(watcher);
		}
		Attribute<T> generateAttributeForManager(final AttributeManager manager, final String name) {
			throwIfInvalidTemplate(name);
			if (associatedAttributeManagers.get(manager).containsKey(name))
				throw new IllegalArgumentException(name + "attribute already exists for manager:" + manager);
			final Attribute<T> attribute = attributeTemplates.get(name).makeCopy();
			addAttributeForManager(manager, name, attribute);
			return attribute;
		}
		private void addAttributeForManager(final AttributeManager manager, final String name, final Attribute<T> attribute) {
			associatedAttributeManagers.get(manager).put(name, attribute);
			attributeWatchers.get(manager).forEach(amw -> amw.onAttributeGenerated(attribute));
		}
		Attribute<T> generateAttributeForManager(final AttributeManager manager, final String name, final T value) {
			throwIfInvalidTemplate(name);
			final Attribute<T> attribute = attributeTemplates.get(name).makeCopy();
			if (associatedAttributeManagers.get(manager).containsKey(name))
				throw new IllegalArgumentException(name + "attribute already exists for manager:" + manager);
			associatedAttributeManagers.get(manager).put(name, attribute);
			attributeWatchers.get(manager).forEach(amw -> amw.onAttributeGenerated(attribute));
			return attribute;
		}
		Attribute<T> getAttributeForManager(final AttributeManager manager, final String name) {
			throwIfInvalidTemplate(name);
			if (!associatedAttributeManagers.get(manager).containsKey(name)) {
				throw new AttributeNotFoundException(name + " is a valid attribute, however it has not been generated for this manager (" + manager + ")");
			}
			return associatedAttributeManagers.get(manager).get(name);
		}
		void removeAttributeForManager(final AttributeManager manager, final String name) {
			throwIfInvalidTemplate(name);
			if (!associatedAttributeManagers.get(manager).containsKey(name)) {
				throw new AttributeNotFoundException(name + " is a valid attribute, however it has not been generated for this manager (" + manager + ")");
			}
			final Attribute<T> removedAttribute = associatedAttributeManagers.get(manager).remove(name);
			attributeWatchers.get(manager).forEach(amw -> amw.onAttributeRemoved(removedAttribute));
		}
		void setAttributeValueForManager(final AttributeManager manager, final String name, final T value) {
			final Attribute<T> attribute = getAttributeForManager(manager, name);
			attribute.setValue(value);
			attributeWatchers.get(manager).forEach(amw -> amw.onAttributeModified(attribute));
		}
		void setAttributeValueForManager(final AttributeManager manager, final String name, final String value) {
			final Attribute<T> attribute = getAttributeForManager(manager, name);
			attribute.setValueParse(value);
			attributeWatchers.get(manager).forEach(amw -> amw.onAttributeModified(attribute));
		}
		void setDoOnGenerationForManager(final AttributeManager manager, final SerializableConsumer<Attribute<T>> consumer) {
			doOnGenerations.put(manager, consumer);
		}
		Collection<Attribute<T>> getAllAttributesForManager(final AttributeManager manager) {
			return Collections.unmodifiableCollection(associatedAttributeManagers.get(manager).values());
		}
		boolean containsAttributeForManager(final AttributeManager manager, final String name) {
			return attributeTemplates.containsKey(name) && associatedAttributeManagers.get(manager).containsKey(name);
		}
		
		private void throwIfInvalidTemplate(final String attributeName) {
			if (!attributeTemplates.containsKey(attributeName))
				throw new AttributeNotFoundException(attributeName + "is not a valid attribute");
		}
		ParseType<T> getParseType() {
			return parseType;
		}
		private void createNewAttributeTemplate(final String name, final String[] values) {
			final Attribute<T> attribute;
			if (values[IS_READABLE_LOC].equalsIgnoreCase("yes")) {
				attribute = generateReadableAttribute(values);
			}
			else {
				attribute = generateBasicAttribute(values);
			}
			attribute.setName(name);
			attributeTemplates.put(name, attribute);
			factoryMapByNameOfAttributeTemplate.put(name, this);
		}
		
		private Attribute<T> generateBasicAttribute(final String[] values) {
			final Attribute<T> attribute = new Attribute<T>(parseType);
			addBasicAttributeDetails(values, attribute);
			return attribute;
		}
		private ReadableAttribute<T> generateReadableAttribute(final String[] values) {
			final ReadableAttribute<T> attribute = new ReadableAttribute<T>(parseType);
			addBasicAttributeDetails(values, attribute);
			addReadableAttributeDetails(values, attribute);
			return attribute;
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
		}
		private void addReadableAttributeDetails(final String[] values, final ReadableAttribute<T> attribute) {
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
	

