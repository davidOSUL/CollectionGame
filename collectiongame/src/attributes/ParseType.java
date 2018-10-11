package attributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thingFramework.CreatureTypeSet;
import thingFramework.ExperienceGroup;

/**
 * Acts as an enumeration, but with Generics. This allows for type safety and allows one to access attributes
 * of different values, using the same methods, but without the need for casting. 
 * <p> This also effectively serves as a list of all valid Attribute Types. In other words, there can never be an 
 * Attribute of type "T" that does not have a corresponding ParseType of type "T"
 * <p> In this way, when a new type of attribute wants to be added, all that needs to be updated is an additional instance of 
 * a ParseType, a new ParseTypeEnum, and an additional instance of an AttributeFactory. No changes need to be made to 
 * methods, other instance variables, etc. 
 * @author David O'Sullivan
 *
 * @param <T> the type of this ParseType
 */
public final class ParseType<T> {
	private static final Map<ParseTypeEnum, ParseType<?>> parseTypeMap = new HashMap<ParseTypeEnum, ParseType<?>>();
	/*
	 * The goal with making them public static values is to have syntax similar to an enum (e.g. ParseType.INTEGER), but still carry generic properties 
	 * with them, allowing one to get a specific attribute with a certain type (e.g. an integer) at compile time without having to explicitly cast anything. 
	 */
	/**
	 * The ParseType for Integer values
	 */
	public static final ParseType<Integer> INTEGER = new ParseType<Integer>(ParseTypeEnum.INTEGER);
	/**
	 * The ParseType for Double values
	 */
	public static final ParseType<Double> DOUBLE = new ParseType<Double>(ParseTypeEnum.DOUBLE);
	/**
	 * The ParseType for String values
	 */
	public static final ParseType<String> STRING = new ParseType<String>(ParseTypeEnum.STRING);
	/**
	 * The ParseType for Boolean values
	 */
	public static final ParseType<Boolean> BOOLEAN = new ParseType<Boolean>(ParseTypeEnum.BOOLEAN);
	/**
	 * The ParseType for CreatureTypeSet values
	 */
	public static final ParseType<CreatureTypeSet> CREATURE_TYPES = new ParseType<CreatureTypeSet>(ParseTypeEnum.CREATURE_TYPES);
	/**
	 * The ParseType for ExperienceGroup values
	 */
	public static final ParseType<ExperienceGroup> EXPERIENCE_GROUP = new ParseType<ExperienceGroup>(ParseTypeEnum.EXPERIENCE_GROUP);
	/**
	 * The ParseType for List values
	 */
	public static final ParseType<List<?>> LIST = new ParseType<List<?>>(ParseTypeEnum.LIST);
	private final ParseTypeEnum associatedEnum; //for performing switch statements
	private ManagerMapCreator<T> mapCreator;
	private ParseType(final ParseTypeEnum associatedEnum) {
		this.associatedEnum = associatedEnum;
		parseTypeMap.put(associatedEnum, this);
	}
	/**
	 * Returns the ManagerMapCreator of this ParseType
	 * @return the ManagerMapCreator of this ParseType
	 */
	ManagerMapCreator<T> getMapCreator() {
		return mapCreator;
	}
	/**
	 * Sets the the ManagerMapCreator for this ParseType
	 * @param mapCreator the the ManagerMapCreator for this ParseType
	 * @throws UnsupportedOperationException if this ParseType already has a the ManagerMapCreator
	 */
	void setAssociatedMapCreator(final ManagerMapCreator<T> mapCreator) {
		if (this.mapCreator != null)
			throw new UnsupportedOperationException("Parse type can only have one map creator");
		this.mapCreator = mapCreator;
	}
	/**
	 * Returns the ParseTypeEnum associated with this ParseType. This allows one to perform a switch statement
	 * on a ParseType
	 * @return the ParseTypeEnum associated with this ParseType
	 */
	ParseTypeEnum getAssociatedEnum() {
		return associatedEnum;
	}
	/** 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "The ParseType For: " + getAssociatedEnum().toString();
	}
	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof ParseType<?>))
			return false;
		return ((ParseType<?>)obj).getAssociatedEnum().equals(getAssociatedEnum());
	}
	 /** 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	 public int hashCode() {
	    return getAssociatedEnum().hashCode();
	 }
	

}



