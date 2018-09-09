package effects;

/**
 * Used with a GlobalModifierEvent to specify what type of things the modifiers should be applied to
 * @author David O'Sullivan
 *
 */
public enum GlobalModifierOption {
	/**
	 * Apply to all things on the model regardless of type
	 */
	NO_PREFERENCE, 
	/**
	 * Apply only to creatures on the model
	 */
	ONLY_CREATURES, 
	/**
	 * Apply only to items on the model
	 */
	ONLY_ITEMS;
}

