package loaders.eventbuilder.generatedevents;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import attributes.AttributeValueParser;
import attributes.ParseType;
import effects.Event;
import effects.GlobalModifierOption;
import thingFramework.Pokemon;

public class GlobalModification<T> extends TypicalEventFactory {

	private final ParseType<T> parseType;
	private final BiFunction<T, T, T> action;
	private final Predicate<T> shouldRemoveAfter;
	
	private final String attributeToModify;
	private final T amount;
	private final long timeToExist;
	private final GlobalModifierOption globalModifierOption;
	private static final int ATTRIBUTE_NAME_LOC = 1;
	private static final int AMOUNT_LOCATION = 2;
	private static final int TIME_TO_EXIST_LOC= 3;
	private static final int GLOBAL_MODIFIER_OPTION_LOC = 4;
	private GlobalModification(final String[] inputs, final BiFunction<T, T, T> action, final Predicate<T> shouldRemoveAfter, final ParseType<T> parseType) {
		super(inputs);
		this.action = action;
		this.parseType = parseType;
		this.shouldRemoveAfter = shouldRemoveAfter;
		
		this.attributeToModify = inputs[ATTRIBUTE_NAME_LOC];
		this.amount = AttributeValueParser.getInstance().parseValue(inputs[AMOUNT_LOCATION], parseType);
		this.timeToExist = parseLong(inputs[TIME_TO_EXIST_LOC]);
		this.globalModifierOption = GlobalModifierOption.valueOf(inputs[GLOBAL_MODIFIER_OPTION_LOC].toLowerCase().trim());
	}
	enum GlobalModificationType {
		ADD_TO_ATTRIBUTE, MULTIPLY_ATTRIBUTE
	}
	@Override
	public Event generateEvent() {
		new Pokemon(attributeToModify, attributeToModify).modifyOrCreateAttribute("s", x -> x - 3, x -> x == 0, -3, type);
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	GlobalModification<?> generateGlobalModification(final String[] inputs, final GlobalModificationType type) {
		
		switch(type) {
		case ADD_TO_ATTRIBUTE:
			final BiFunction<Integer, Integer, Integer> action = (x, y) -> x+y;
			final Predicate<Integer> shouldRemove = x -> x==0;
			return new GlobalModification(inputs, action, shouldRemove, ParseType.INTEGER);
			break;
		case MULTIPLY_ATTRIBUTE:
			break;
		default:
			break;
		
		}
	}
}
