package loaders.eventbuilder.generatedevents.globalmodification;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import attributes.AttributeManager;
import attributes.AttributeName;
import attributes.AttributeValueParser;
import attributes.DisplayStringSetting;
import attributes.ParseType;
import effects.Event;
import effects.GlobalModifierEvent;
import effects.GlobalModifierOption;
import gui.guiutils.GuiUtils;
import interfaces.SerializableBiFunction;
import interfaces.SerializableConsumer;
import interfaces.SerializablePredicate;
import loaders.eventbuilder.generatedevents.TypicalEventFactory;
import modifiers.Modifier;
import thingFramework.Thing;

/**
 * Generates event that applies a global Modifier to the ModelInterface
 * @author David O'Sullivan
 *
 * @param <T> the type of the attribute that is being modified
 */
public class GlobalModifierEventFactory<T> extends TypicalEventFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final SerializableBiFunction<T, T, T> action;
	private final SerializablePredicate<T> shouldRemoveAfter;
	
	private final SerializableBiFunction<T, T, T> reverseAction;
	private final SerializablePredicate<T> shouldRemoveReverseAfter;
	
	private final List<AttributeName<T>> attributesToModify;
	private final T amount;
	
	private final long timeToExist;
	
	private final GlobalModifierOption globalModifierOption;
	
	private final boolean removeWhenDone;
	private final boolean displayCountdown;
	private final DisplayStringSetting[] displayStringSettings;
	private ModificationType globalModificationType;
	
	private static final int MODIFICATION_TYPE_LOC = 1;
	private static final int ATTRIBUTE_NAME_LOC = 2;
	private static final int AMOUNT_LOCATION = 3;
	private static final int TIME_TO_EXIST_LOC= 4;
	private static final int GLOBAL_MODIFIER_OPTION_LOC = 5;
	private static final int REMOVE_WHEN_DONE_LOC = 6;
	private static final int DISPLAY_COUNTDOWN_LOC = 7;
	private static final int SPECIFY_FOR_ATTRIBUTE_VALUE_LOC= 8;
	private static final String MULTIPLE_ATTRIBUTE_DELIMITER = ":";
	/**
	 * Creates a new GlobalModifierEventFactory
	 * @param inputs the inputs to use to construct the GlobalModifierEventFactory
	 * @param action the modification to the attribute(s) the modifier will set the new value to action.apply(oldValue, amount)
	 * @param shouldRemoveAfter given the value of the attribute after performing the action, should it now be removed
	 * @param reverseAction undo the original action
	 * @param shouldRemoveReverseAfter given the value of the attribute after performing the reverse action, should it now be removed
	 * @param parseType the associated ParseType
	 * @param displayStringSettings the Settings for Display of the Attribute
	 */
	GlobalModifierEventFactory(final String[] inputs, final SerializableBiFunction<T, T, T> action, 
			final SerializablePredicate<T> shouldRemoveAfter, final SerializableBiFunction<T, T, T> reverseAction, 
			final SerializablePredicate<T> shouldRemoveReverseAfter, final ParseType<T> parseType, final DisplayStringSetting...displayStringSettings) {
		super(inputs);
		this.action = action;
		this.shouldRemoveAfter = shouldRemoveAfter;
		
		this.reverseAction = reverseAction;
		this.shouldRemoveReverseAfter = shouldRemoveReverseAfter;
		this.attributesToModify = new ArrayList<AttributeName<T>>();
		for (final String c : inputs[ATTRIBUTE_NAME_LOC].split(MULTIPLE_ATTRIBUTE_DELIMITER))
			attributesToModify.add(AttributeName.getAttributeName(c, parseType));
		this.amount = AttributeValueParser.getInstance().parseValue(inputs[AMOUNT_LOCATION], parseType);
		this.timeToExist = parseLong(inputs[TIME_TO_EXIST_LOC]);
		this.globalModifierOption = GlobalModifierOption.valueOf(inputs[GLOBAL_MODIFIER_OPTION_LOC].toUpperCase().trim());
		this.removeWhenDone = Boolean.parseBoolean(inputs[REMOVE_WHEN_DONE_LOC]);
		this.displayCountdown = Boolean.parseBoolean(inputs[DISPLAY_COUNTDOWN_LOC]);
		
		this.displayStringSettings = displayStringSettings;
	}
	/**
	 * Creates a new GlobalModifierEventFactory, with shouldRemoveReverseAfter set to shouldRemoveAfter
	 * @param inputs the inputs to use to construct the GlobalModifierEventFactory
	 * @param action the modification to the attribute(s) the modifier will set the new value to action.apply(oldValue, amount)
	 * @param shouldRemoveAfter given the value of the attribute after performing the action or reverse action, should it now be removed. 
	 * @param reverseAction undo the original action
	 * @param parseType the associated ParseType
	 * @param displayStringSettings the Settings for Display of the Attribute
	 */
	GlobalModifierEventFactory(final String[] inputs, final SerializableBiFunction<T, T, T> action, 
			final SerializablePredicate<T> shouldRemoveAfter, final SerializableBiFunction<T, T, T> reverseAction, final ParseType<T> parseType, final DisplayStringSetting...displayStringSettings) {
		this(inputs, action, shouldRemoveAfter, reverseAction, shouldRemoveAfter, parseType, displayStringSettings);
	}
	/**
	 * Creates a new GlobalModifierEventFactory by copying from an old GlobalModifierEventFactory
	 * @param template the old GlobalModifierEventFactory
	 * @param parseType the ParseType of the Attribute the event this factory produces modifies
	 */
	GlobalModifierEventFactory(final GlobalModifierEventFactory<T> template, final ParseType<T> parseType) {
		this(template.getInputs(), template.action, template.shouldRemoveAfter, template.reverseAction, template.shouldRemoveReverseAfter, parseType, template.displayStringSettings);
		this.globalModificationType = template.globalModificationType;
	}
	/** 
	 * @see loaders.eventbuilder.generatedevents.TypicalEventFactory#generateEvent()
	 */
	@Override
	public Event generateEvent() {
		return new GlobalModifierEvent(generateModifier(), removeWhenDone, displayCountdown, globalModifierOption);
	}
	/** 
	 * @see loaders.eventbuilder.generatedevents.TypicalEventFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getTimeDisplayDescription(timeToExist) + " ");
		sb.append(globalOptionToName(globalModifierOption));
		sb.append(" get ");
		String delimiter = "";
		for (int i = 0; i < attributesToModify.size(); i++) {
			sb.append(delimiter);
			sb.append(AttributeManager.displayAttribute(attributesToModify.get(i), amount, displayStringSettings));
			delimiter = " and ";
		}
		return sb.toString();
	
	}
	/**
	 * Generates the modifiers for the GlobalModifierEvent
	 * @param shouldModify the shouldModify predicate for the modifier 
	 * @return the generated Modifiers
	 */
	Modifier[] generateModifier(final SerializablePredicate<Thing> shouldModify) {
		final Modifier[] modifiers = new Modifier[attributesToModify.size()];
		for (int i = 0; i < attributesToModify.size(); i++) {
			final AttributeName<T> attributeToModify = attributesToModify.get(i);
			final SerializableConsumer<Thing> modification = createModification(attributeToModify, shouldRemoveAfter, false);
			final SerializableConsumer<Thing> reverseModification =  createModification(attributeToModify, shouldRemoveReverseAfter, true);
				
			modifiers[i] = new Modifier(timeToExist, shouldModify, modification, reverseModification);
		}
		return modifiers;
		
	}
	private SerializableConsumer<Thing> createModification(final AttributeName<T> attributeToModify, final Predicate<T> shouldRemoveAfter, final boolean performReverse) {
		final SerializableConsumer<Thing> modification = t -> {
			t.modifyOrCreateAttribute(attributeToModify, val -> {
				return performReverse? reverseAction.apply(val, amount) : action.apply(val, amount);
			}
			, shouldRemoveAfter);
		};
		return modification;
	}
	
	private void setGlobalModificationType(final ModificationType type) {
		this.globalModificationType = type;
	}
	/**
	 * Returns the modifiers to be applied to the ModelInterface by the event. Children should override this method to provide a shouldModify predicate
	 * @return the modifiers to applied to the ModelInterface by the event. 
	 */
	Modifier[] generateModifier() {
		return generateModifier(x -> true);
	}
	/**
	 * Generates a GlobalModifierEventFactory given the provided inputs
	 * @param inputs the inputs to generated a GlobalModifierEventFactory with 
	 * @return the generated GlobalModifierEventFactory
	 */
	public static GlobalModifierEventFactory<?> generateGlobalModifierEventFactory(final String[] inputs) {
		final GlobalModifierEventFactory<?> globalModification;
		final ModificationType type = ModificationType.valueOf(inputs[MODIFICATION_TYPE_LOC].toUpperCase().trim());
		final ParseType<?> parseType;
		switch(type) {
		case ADD_TO_ATTRIBUTE:
			globalModification = new GlobalModifierEventFactory<Integer>(inputs, (val, amt) -> val+amt, x -> x==0, (val, amt) -> val-amt, ParseType.INTEGER);
			parseType = ParseType.INTEGER;
			break;
		case MULTIPLY_ATTRIBUTE:
			globalModification = new GlobalModifierEventFactory<Integer>(inputs, (val, amt) -> val*amt, x -> x == 0, (val, amt) -> val/amt, x -> false, ParseType.INTEGER, DisplayStringSetting.CHANGE_PLUS_TO_TIMES);
			parseType = ParseType.INTEGER;
			break;
		default:
			throw new RuntimeException("Unexplored ModificationType");
		}
		globalModification.setGlobalModificationType(type);
		if (!Boolean.parseBoolean(inputs[SPECIFY_FOR_ATTRIBUTE_VALUE_LOC]))
			return globalModification;
		else 
			return new TargetedGlobalModifierEventFactory(globalModification, parseType);
	}
	/**
	 * @param option the type of global modification
	 * @return the text that represents that option. Children should override this to provide a more specific description.
	 */
	String globalOptionToName(final GlobalModifierOption option) { 
		switch(option) {
		case NO_PREFERENCE:
			return "things";
		case ONLY_ITEMS:
			return "items";
		case ONLY_CREATURES:
			return GuiUtils.getPluralCreatureName();
		default:
			throw new RuntimeException("Unexplored GlobalModifierOption");
		}
	}
	/**
	 * The Category of Modification. All GlobalModifierEvents must have a modifier of one of these types. Depending on the value, it determines 
	 * what action is carried out by the modifier in the event.
	 * @author David O'Sullivan
	 *
	 */
	public enum ModificationType {
		/**
		 * Add a value to the attribute
		 */
		ADD_TO_ATTRIBUTE, 
		/**
		 * Multiply the attribute by some value
		 */
		MULTIPLY_ATTRIBUTE
	}
	
}
