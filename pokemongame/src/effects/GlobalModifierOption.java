package effects;

/**
 * Used with a GlobalModifierEvent to specify what type of things the modifiers should be applied to
 * @author David O'Sullivan
 *
 */
public enum GlobalModifierOption {
	/**
	 * Apply to all things on the board regardless of type
	 */
	NO_PREFERENCE, 
	/**
	 * Apply only to creatures on the board
	 */
	ONLY_CREATURES, 
	/**
	 * Apply only to items on the board
	 */
	ONLY_ITEMS;
}

