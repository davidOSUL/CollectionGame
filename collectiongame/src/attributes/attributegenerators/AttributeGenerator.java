package attributes.attributegenerators;

import thingFramework.Creature;
import thingFramework.Item;

/**
 * Instead of manually specifing the attributes of creatures/items, they can be generated using an 
 * AttributeGenerator. Implementors of this interface, need to define how attributes should be generated given
 * a particular creature/item.
 * @author David O'Sullivan
 *
 */
public interface AttributeGenerator {
	/**
	 * Adds the generated attributes to this creature
	 * @param c the Creature to add the generated attributes to 
	 */
	public void addAttributes(Creature c);
	/**
	 * Adds the generated attributes to this item
	 * @param i the Item to add the generated attributes to
	 */
	public void addAttributes(Item i);
}
