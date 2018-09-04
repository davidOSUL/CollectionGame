package attributes;

/**
 * The Pure enum parallel to ParseType.
 * Useful for performing switch statements on a ParseType object
 * @see ParseType
 * @author David O'Sullivan
 *
 */
enum ParseTypeEnum {
	/**
	 * The ParseTypeEnum for a ParseType with a type of Integer
	 */
	INTEGER,
	/**
	 * The ParseTypeEnum for a ParseType with a type of Double
	 */
	DOUBLE, 
	/**
	 * The ParseTypeEnum for a ParseType with a type of String
	 */
	STRING, 
	/**
	 * The ParseTypeEnum for a ParseType with a type of Boolean
	 */
	BOOLEAN, 
	/**
	 * /**
	 * The ParseTypeEnum for a ParseType with a type of Creature Types
	 */
	CREATURE_TYPES, 
	/**
	 * The ParseTypeEnum for a ParseType with a type of Experience Group
	 */
	EXPERIENCE_GROUP, 
	/**
	 * The ParseTypeEnum for a ParseType with a type of List
	 */
	LIST;
}
