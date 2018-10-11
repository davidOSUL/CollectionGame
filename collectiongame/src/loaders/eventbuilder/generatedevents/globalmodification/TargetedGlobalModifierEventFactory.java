package loaders.eventbuilder.generatedevents.globalmodification;

import attributes.AttributeName;
import attributes.ParseType;
import effects.GlobalModifierOption;
import modifiers.Modifier;
import thingFramework.Thing;

/**
 * Generates events that create Global Modifiers that only modify Things that have attribuets with particular value(s)
 * @author David O'Sullivan
 *
 * @param <T> the type of the attribute
 */
public class TargetedGlobalModifierEventFactory<T> extends GlobalModifierEventFactory<T> { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final AttributeName<?> attributeName;
	private final String[] acceptableAttributeValues;
	private final String verbalDescription;

	private static final int VERBAL_DESCRIPTION_LOC = 9;
	private static final int CATEGORY_NAME_LOC = 10;
	private static final int CATEGORY_VALUE_LOC = 11;
	private static final String ATTRIBUTE_VALUE_DELIMITER = ":";
	/**
	 * Generates a new TargetedGlobalModifierEventFactory using the provided GlobalModifierEventFactory as a template
	 * @param template the GlobalModifierEventFactory to use as a template
	 * @param parseType the type of the Attribute that the events that this TargetedGlobalModifierEventFactory produces modify
	 */
	TargetedGlobalModifierEventFactory(final GlobalModifierEventFactory<T> template, final ParseType<T> parseType) {
		super(template, parseType);
		verbalDescription = getInputs()[VERBAL_DESCRIPTION_LOC];
		attributeName = AttributeName.getAttributeName(getInputs()[CATEGORY_NAME_LOC]);
		acceptableAttributeValues = getInputs()[CATEGORY_VALUE_LOC].split(ATTRIBUTE_VALUE_DELIMITER);

	}
	/** 
	 * @see loaders.eventbuilder.generatedevents.globalmodification.GlobalModifierEventFactory#generateModifier()
	 */
	@Override
	Modifier[] generateModifier() {
		return super.generateModifier(t -> {
			if(!t.containsAttribute(attributeName))
				return false;
			return hasValidAttributeValue(t);
		});
	}
	/** 
	 * @see loaders.eventbuilder.generatedevents.globalmodification.GlobalModifierEventFactory#globalOptionToName(effects.GlobalModifierOption)
	 */
	@Override 
	String globalOptionToName(final GlobalModifierOption option) {
		return verbalDescription + " " + super.globalOptionToName(option);
	}
	private boolean hasValidAttributeValue(final Thing t) {
		boolean isValid = false;
		for (final String acceptableAttributeValue : acceptableAttributeValues) {
			if (attributeName.equals(AttributeName.TYPE)) {
				isValid = isValid || t.getAttributeValue(AttributeName.TYPE).containsValueParse(acceptableAttributeValue);
			}
			else {
				isValid = isValid || t.attributeValueEqualsParse(attributeName, acceptableAttributeValue);
			}
		}
		return isValid;
	}
}
