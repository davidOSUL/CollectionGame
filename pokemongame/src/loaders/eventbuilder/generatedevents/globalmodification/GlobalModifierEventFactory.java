package loaders.eventbuilder.generatedevents.globalmodification;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import attributes.AttributeManager;
import attributes.AttributeValueParser;
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
	
	private final String attributeToModify;
	private final T amount;
	
	private final long timeToExist;
	
	private final GlobalModifierOption globalModifierOption;
	
	private final boolean removeWhenDone;
	private final boolean displayCountdown;
	
	private ModificationType globalModificationType;
	
	private static final int MODIFICATION_TYPE_LOC = 1;
	private static final int ATTRIBUTE_NAME_LOC = 2;
	private static final int AMOUNT_LOCATION = 3;
	private static final int TIME_TO_EXIST_LOC= 4;
	private static final int GLOBAL_MODIFIER_OPTION_LOC = 5;
	private static final int REMOVE_WHEN_DONE_LOC = 6;
	private static final int DISPLAY_COUNTDOWN_LOC = 7;
	private static final int SPECIFY_FOR_ATTRIBUTE_VALUE_LOC= 8;
	GlobalModifierEventFactory(final String[] inputs, final BiFunction<T, T, T> action, 
			final Predicate<T> shouldRemoveAfter, final BiFunction<T, T, T> reverseAction, 
			final Predicate<T> shouldRemoveReverseAfter, final ParseType<T> parseType) {
		super(inputs);
		this.action = action;
		this.parseType = parseType;
		this.shouldRemoveAfter = shouldRemoveAfter;
		
		this.reverseAction = reverseAction;
		this.shouldRemoveReverseAfter = shouldRemoveReverseAfter;
		
		this.attributeToModify = inputs[ATTRIBUTE_NAME_LOC];
		this.amount = AttributeValueParser.getInstance().parseValue(inputs[AMOUNT_LOCATION], parseType);
		this.timeToExist = parseLong(inputs[TIME_TO_EXIST_LOC]);
		this.globalModifierOption = GlobalModifierOption.valueOf(inputs[GLOBAL_MODIFIER_OPTION_LOC].toLowerCase().trim());
		this.removeWhenDone = Boolean.parseBoolean(inputs[REMOVE_WHEN_DONE_LOC]);
		this.displayCountdown = Boolean.parseBoolean(inputs[DISPLAY_COUNTDOWN_LOC]);
	}
	GlobalModifierEventFactory(final String[] inputs, final BiFunction<T, T, T> action, final Predicate<T> shouldRemoveAfter, final BiFunction<T, T, T> reverseAction, final ParseType<T> parseType) {
		this(inputs, action, shouldRemoveAfter, reverseAction, shouldRemoveAfter, parseType);
	}
	GlobalModifierEventFactory(final GlobalModifierEventFactory template) {
		this(template.getInputs(), template.action, template.shouldRemoveAfter, template.reverseAction, template.shouldRemoveReverseAfter, template.parseType);
		this.globalModificationType = template.globalModificationType;
	}
	@Override
	public Event generateEvent() {
		return new GlobalModifierEvent(generateModifier(), removeWhenDone, displayCountdown, globalModifierOption);
	}
	@Override
	public String getDescription() {
		return getTimeDisplayDescription(timeToExist) + globalOptionToName(globalModifierOption) + " get" 
				+ AttributeManager.displayAttribute(attributeToModify, amount, parseType);
	}
	Modifier generateModifier(final SerializablePredicate<Thing> shouldModify) {
		final SerializableConsumer<Thing> modification = t -> {
			t.modifyIfContainsAttribute(attributeToModify, val -> action.apply(val, amount), shouldRemoveAfter, parseType);
		};
		final SerializableConsumer<Thing> reverseModification =  t -> {
			t.modifyIfContainsAttribute(attributeToModify, val -> reverseAction.apply(val, amount), shouldRemoveReverseAfter, parseType);
		};
		return new Modifier(timeToExist, shouldModify, modification, reverseModification);
	}
	private void setGlobalModificationType(final ModificationType type) {
		this.globalModificationType = type;
	}
	Modifier generateModifier() {
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
			globalModification = new GlobalModifierEventFactory<Integer>(inputs, (val, amt) -> val*amt, x -> x == 0, (val, amt) -> val/amt, x -> false, ParseType.INTEGER);
			break;
		default:
			throw new RuntimeException("Unexplored ModificationType");
		}
		globalModification.setGlobalModificationType(type);
		if (!Boolean.parseBoolean(inputs[SPECIFY_FOR_ATTRIBUTE_VALUE_LOC]))
			return globalModification;
		else
			return new GlobalModifierForAttributeValueEventFactory(globalModification);
	}
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
	public enum ModificationType {
		ADD_TO_ATTRIBUTE, MULTIPLY_ATTRIBUTE
	}
}
