package attributeTypes.enumAttributes;

import thingFramework.ExperienceGroup;

public class ExperienceGroupAttribute extends EnumAttribute<ExperienceGroup> {
	public ExperienceGroupAttribute() {}
	private ExperienceGroupAttribute(final ExperienceGroupAttribute attribute) {
		super(attribute);
	}
	@Override
	public EnumAttribute<ExperienceGroup> makeCopy() {
		return new ExperienceGroupAttribute(this);
	}
	
}
