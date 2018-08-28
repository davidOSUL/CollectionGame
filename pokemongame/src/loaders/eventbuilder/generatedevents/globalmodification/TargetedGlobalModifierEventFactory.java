package loaders.eventbuilder.generatedevents.globalmodification;

import attributes.ParseType;
import effects.GlobalModifierOption;
import modifiers.Modifier;
import thingFramework.Thing;

public class TargetedGlobalModifierEventFactory<T> extends GlobalModifierEventFactory<T> { 

	private final String attributeName;
	private final String[] acceptableAttributeValues;
	private final String verbalDescription;

	private static final int VERBAL_DESCRIPTION_LOC = 9;
	private static final int CATEGORY_NAME_LOC = 10;
	private static final int CATEGORY_VALUE_LOC = 11;
	private static final String ATTRIBUTE_VALUE_DELIMITER = ":";
	TargetedGlobalModifierEventFactory(final GlobalModifierEventFactory<T> template) {
		super(template);
		verbalDescription = getInputs()[VERBAL_DESCRIPTION_LOC];
		attributeName = getInputs()[CATEGORY_NAME_LOC];
		acceptableAttributeValues = getInputs()[CATEGORY_VALUE_LOC].split(ATTRIBUTE_VALUE_DELIMITER);

	}
	@Override
	Modifier[] generateModifier() {
		return super.generateModifier(t -> {
			if(!t.containsAttribute(attributeName))
				return false;
			return hasValidAttributeValue(t);
		});
	}
	@Override 
	String globalOptionToName(final GlobalModifierOption option) {
		return verbalDescription + " " + super.globalOptionToName(option);
	}
	private boolean hasValidAttributeValue(final Thing t) {
		boolean isValid = false;
		for (final String acceptableAttributeValue : acceptableAttributeValues) {
			if (attributeName.equals("type")) {
				isValid = isValid || t.getAttributeValue("type", ParseType.POKEMON_TYPES).containsValueParse(acceptableAttributeValue);
			}
			else {
				isValid = isValid || t.attributeValueEqualsParse(attributeName, acceptableAttributeValue);
			}
		}
		return isValid;
	}
}
