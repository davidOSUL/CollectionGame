package attributes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import attributes.AttributeFactories.AttributeFactory;
import thingFramework.ExperienceGroup;
import thingFramework.PokemonTypeSet;

public final class ParseType<T> {
	private static final Map<ParseTypeEnum, ParseType<?>> parseTypeMap = new HashMap<ParseTypeEnum, ParseType<?>>();
	/*
	 * The goal with making them public static values is to have syntax similar to an enum (e.g. ParseType.INTEGER), but still carry generic properties 
	 * with them, allowing one to get a specific attribute with a certain type (e.g. an integer) at compile time without having to explicitly cast anything. 
	 */
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
		parseTypeMap.put(associatedEnum, this);
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
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ParseType<?>))
			return false;
		return ((ParseType<?>)obj).getAssociatedEnum().equals(getAssociatedEnum());
	}
	 @Override
	 public int hashCode() {
	    return getAssociatedEnum().hashCode();
	 }
	public void saveParseType(final ObjectOutputStream oos) throws IOException {
		oos.writeObject(getAssociatedEnum());
	}
	public static <T> ParseType<T> loadParseType(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
		/*This is a way to get around having to implement Serializable parsetypes and to be able to keep them as a "pseudo enum" with static values
		*In this way, the exact object that a parsetype is changes each time the game is saved and then reloaded, but this change is consistent 
		*across all the code. I don't want to make them serializable because then I'd have to make AttributeFactory serializable. This way I can keep 
		*AttributeFactories as a pure singleton with no need for serialization with readResolve(). 
		*/
		final ParseTypeEnum theEnum = (ParseTypeEnum) ois.readObject(); 
		return (ParseType<T>) parseTypeMap.get(theEnum);
	}

}



