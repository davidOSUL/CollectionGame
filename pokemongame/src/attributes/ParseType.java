package attributes;

import java.util.List;

import attributes.AttributeFactories.AttributeFactory;
import thingFramework.ExperienceGroup;
import thingFramework.PokemonTypeSet;

public final class ParseType<T> {
	
	public static final ParseType<Integer> INTEGER = new ParseType<Integer>(ParseTypeEnum.INTEGER);
	public static final ParseType<Double> DOUBLE = new ParseType<Double>(ParseTypeEnum.DOUBLE);
	public static final ParseType<String> STRING = new ParseType<String>(ParseTypeEnum.STRING);
	public static final ParseType<Boolean> BOOLEAN = new ParseType<Boolean>(ParseTypeEnum.BOOLEAN);
	public static final ParseType<PokemonTypeSet> POKEMON_TYPES = new ParseType<PokemonTypeSet>(ParseTypeEnum.POKEMON_TYPES);
	public static final ParseType<ExperienceGroup> EXPERIENCE_GROUP = new ParseType<ExperienceGroup>(ParseTypeEnum.EXPERIENCE_GROUP);
	public static final ParseType<List<?>> LIST = new ParseType<List<?>>(ParseTypeEnum.LIST);
	private final ParseTypeEnum associatedEnum;
	private AttributeFactory<T> associatedFactory;
	private ParseType(final ParseTypeEnum associatedEnum) {
		this.associatedEnum = associatedEnum;
	}
	AttributeFactory<T> getAssociatedFactory() {
		return associatedFactory;
	}
	void setAssociatedFactory(final AttributeFactory<T> associatedFactory) {
		this.associatedFactory = associatedFactory;
	}
	ParseTypeEnum getAssociatedEnum() {
		return associatedEnum;
	}
	@Override
	public String toString() {
		return "The ParseType For: " + getAssociatedEnum().toString();
	}

}
