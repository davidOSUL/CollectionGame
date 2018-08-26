package loaders.eventbuilder.generatedevents.globalmodification;

import effects.GlobalModifierOption;
import modifiers.Modifier;

public class GlobalModifierForAttributeValueEventFactory<T> extends GlobalModifierEventFactory<T> {
	private final String attributeName;
	private final String targetAttributeValue;
	private final String verbalDescription;
	
	private static final int VERBAL_DESCRIPTION_LOC = 8;
	private static final int CATEGORY_NAME_LOC = 9;
	private static final int CATEGORY_VALUE_LOC = 10;
	GlobalModifierForAttributeValueEventFactory(final GlobalModifierEventFactory<T> parent) {
		super(parent);
		verbalDescription = getInputs()[VERBAL_DESCRIPTION_LOC];
		attributeName = getInputs()[CATEGORY_NAME_LOC];
		targetAttributeValue = getInputs()[CATEGORY_VALUE_LOC];
		
	}
	@Override
	Modifier generateModifier() {
		return super.generateModifier(t -> {
			return t.containsAttribute(attributeName) &&
					t.attributeValueEqualsParse(attributeName, targetAttributeValue);
		});
	}
	@Override 
	String globalOptionToName(final GlobalModifierOption option) {
		return verbalDescription + " " + super.globalOptionToName(option);
	}
}
