package model;

import thingFramework.Creature;
import thingFramework.Item;

/**
 * A ThingObserver wants to be notified when things are added or removed. Things can then call the appropriate methods from
 * their onPlace and onRemove methods.
 * @author David O'Sullivan
 *
 */
public interface ThingObserver {
	/**
	 * To be called whenever a creature is added to the model. Should be called by {@link Creature#onPlace(Board)}
	 * @param creature the creature that was added
	 * 
	 */
	void notifyCreatureAdded(Creature creature);

	/**
	 * To be called whenever a creature is removed from the model. Should be called by {@link Creature#onRemove(Board)}
	 * @param creature the creature that was removed
	 */
	void notifyCreatureRemoved(Creature creature);

	/**
	 * To be called whenever an Item is added to the model. Should be called by {@link Item#onPlace(Board)}
	 * @param i the item that was added
	 */
	void notifyItemAdded(Item i);

	/**
	 * To be called whenever an Item is removed from the model. Should be called by {@link Item#onRemove(Board)}
	 * @param i the item that was removed
	 */
	void notifyItemRemoved(Item i);

}
