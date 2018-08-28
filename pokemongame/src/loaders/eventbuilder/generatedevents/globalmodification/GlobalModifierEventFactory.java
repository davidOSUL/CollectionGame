package loaders.eventbuilder.generatedevents.globalmodification;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import attributes.AttributeManager;
import attributes.AttributeValueParser;
import attributes.DisplayStringSetting;
import attributes.ParseType;
import effects.Event;
import effects.GlobalModifierEvent;
import effects.GlobalModifierOption;
import interfaces.SerializableConsumer;
import interfaces.SerializablePredicate;
import loaders.eventbuilder.generatedevents.TypicalEventFactory;
import modifiers.Modifier;
import thingFramework.Thing;

public class GlobalModifierEventFactory<T> extends TypicalEventFactory {

	private final ParseType<T> parseType;
	private final BiFunction<T, T, T> action;
	private final Predicate<T> shouldRemoveAfter;
	
	private final BiFunction<T, T, T> reverseAction;
	private final Predicate<T> shouldRemoveReverseAfter;
	
	private final String[] attributesToModify;
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
	GlobalModifierEventFactory(final String[] inputs, final BiFunction<T, T, T> action, 
			final Predicate<T> shouldRemoveAfter, final BiFunction<T, T, T> reverseAction, 
			final Predicate<T> shouldRemoveReverseAfter, final ParseType<T> parseType, final DisplayStringSetting...displayStringSettings) {
		super(inputs);
		this.action = action;
		this.parseType = parseType;
		this.shouldRemoveAfter = shouldRemoveAfter;
		
		this.reverseAction = reverseAction;
		this.shouldRemoveReverseAfter = shouldRemoveReverseAfter;
		
		this.attributesToModify = inputs[ATTRIBUTE_NAME_LOC].split(MULTIPLE_ATTRIBUTE_DELIMITER);
		this.amount = AttributeValueParser.getInstance().parseValue(inputs[AMOUNT_LOCATION], parseType);
		this.timeToExist = parseLong(inputs[TIME_TO_EXIST_LOC]);
		this.globalModifierOption = GlobalModifierOption.valueOf(inputs[GLOBAL_MODIFIER_OPTION_LOC].toUpperCase().trim());
		this.removeWhenDone = Boolean.parseBoolean(inputs[REMOVE_WHEN_DONE_LOC]);
		this.displayCountdown = Boolean.parseBoolean(inputs[DISPLAY_COUNTDOWN_LOC]);
		
		this.displayStringSettings = displayStringSettings;
	}
	GlobalModifierEventFactory(final String[] inputs, final BiFunction<T, T, T> action, 
			final Predicate<T> shouldRemoveAfter, final BiFunction<T, T, T> reverseAction, final ParseType<T> parseType, final DisplayStringSetting...displayStringSettings) {
		this(inputs, action, shouldRemoveAfter, reverseAction, shouldRemoveAfter, parseType, displayStringSettings);
	}
	GlobalModifierEventFactory(final GlobalModifierEventFactory template) {
		this(template.getInputs(), template.action, template.shouldRemoveAfter, template.reverseAction, template.shouldRemoveReverseAfter, template.parseType, template.displayStringSettings);
		this.globalModificationType = template.globalModificationType;
	}
	@Override
	public Event generateEvent() {
		return new GlobalModifierEvent(generateModifier(), removeWhenDone, displayCountdown, globalModifierOption);
	}
	@Override
	public String getDescription() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getTimeDisplayDescription(timeToExist) + " ");
		sb.append(globalOptionToName(globalModifierOption));
		sb.append(" get ");
		String delimiter = "";
		for (int i = 0; i < attributesToModify.length; i++) {
			sb.append(delimiter);
			final String plusVal = null;
			sb.append(AttributeManager.displayAttribute(attributesToModify[i], amount, parseType, displayStringSettings));
			delimiter = " and ";
		}
		return sb.toString();
	
	}
	Modifier[] generateModifier(final SerializablePredicate<Thing> shouldModify) {
		final Modifier[] modifiers = new Modifier[attributesToModify.length];
		for (int i = 0; i < attributesToModify.length; i++) {
			final String attributeToModify = attributesToModify[i];
			final SerializableConsumer<Thing> modification = t -> {
				t.modifyOrCreateAttribute(attributeToModify, val -> action.apply(val, amount), shouldRemoveAfter, parseType);
			};
			final SerializableConsumer<Thing> reverseModification =  t -> {
				t.modifyOrCreateAttribute(attributeToModify, val -> reverseAction.apply(val, amount), shouldRemoveReverseAfter, parseType);
			};
			modifiers[i] = new Modifier(timeToExist, shouldModify, modification, reverseModification);
		}
		return modifiers;
		
	}
	private void setGlobalModificationType(final ModificationType type) {
		this.globalModificationType = type;
	}
	/**
	 * @return the modifier to applied to the board by the event. Children should override this method to provide a shouldModify predicate
	 */
	Modifier[] generateModifier() {
		return generateModifier(x -> true);
	}
	public static GlobalModifierEventFactory<?> generateGlobalModifierEventFactory(final String[] inputs) {
		final GlobalModifierEventFactory<?> globalModification;
		final ModificationType type = ModificationType.valueOf(inputs[MODIFICATION_TYPE_LOC].toUpperCase().trim());
		switch(type) {
		case ADD_TO_ATTRIBUTE:
			globalModification = new GlobalModifierEventFactory<Integer>(inputs, (val, amt) -> val+amt, x -> x==0, (val, amt) -> val-amt, ParseType.INTEGER);
			break;
		case MULTIPLY_ATTRIBUTE:
			globalModification = new GlobalModifierEventFactory<Integer>(inputs, (val, amt) -> val*amt, x -> x == 0, (val, amt) -> val/amt, x -> false, ParseType.INTEGER, DisplayStringSetting.CHANGE_PLUS_TO_TIMES);
			break;
		default:
			throw new RuntimeException("Unexplored ModificationType");
		}
		globalModification.setGlobalModificationType(type);
		if (!Boolean.parseBoolean(inputs[SPECIFY_FOR_ATTRIBUTE_VALUE_LOC]))
			return globalModification;
		else
			return new TargetedGlobalModifierEventFactory(globalModification);
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
		case ONLY_POKEMON:
			return "Pokemon";
		default:
			throw new RuntimeException("Unexplored GlobalModifierOption");
		}
	}
	/**
	 * The Category of Modification. All GlobalModifierEvents must have a modifier of one of these types. Depending on the value, it determines 
	 * what action is carried out by the modifier in the event.
	 * @author DOSullivan
	 *
	 */
	public enum ModificationType {
		ADD_TO_ATTRIBUTE, MULTIPLY_ATTRIBUTE
	}
}
