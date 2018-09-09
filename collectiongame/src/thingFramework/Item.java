package thingFramework;

import java.io.Serializable;

import attributes.Attribute;
import attributes.AttributeCharacteristic;
import attributes.attributegenerators.AttributeGenerator;
import effects.Event;
import effects.Eventful;
import interfaces.Imagable;
import model.ThingObserver;

/**
 * Items are one of the two types of Things. They are generally purchased in the Shop, usually have events
 * associated with them, and have fewer attributes than Creatures.
 * @author David O'Sullivan
 *
 */
public class Item extends Thing implements Serializable, Eventful, Imagable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constructs a new item with no name or image
	 */
	protected Item() {}
	private Item(final Event...events) {
		super(events);
	}
	/**
	 * Constructs a new Item with the provided name and iamge
	 * @param name the name of the Item
	 * @param image the path to the image
	 */
	public Item(final String name, final String image) {
		super(name, image);
	}
	/**
	 * Creates a new Creature with the provided name and image
	 * @param name the name of the Item
	 * @param image the path to the image
	 * @param haveModelAttributes if false, won't create a ModelAttributeManager
	 */
	public Item(final String name, final String image, final boolean haveModelAttributes) {
		super(name, image, haveModelAttributes);
	}
	private Item(final Item i) {
		super(i);
	}



	/** 
	 * @see thingFramework.Thing#onPlace(model.ThingObserver)
	 */
	@Override
	public void onPlace(final ThingObserver observer) {
		observer.notifyItemAdded(this);
	}

	/** 
	 * @see thingFramework.Thing#onRemove(model.ThingObserver)
	 */
	@Override
	public void onRemove(final ThingObserver observer) {
		observer.notifyItemRemoved(this);
	}
	/**
	 * Creates a new Item with no name and image
	 * @return a new Item with no name and image
	 */
	public static Item generateBlankItem() {
		return new Item();
	}
	/**
	 * Creates a new Item with no name and image and with the provided events
	 * @param events the events to add to the created items event list
	 * @return a new Item with no name and image and with the provided events
	 */
	public static Item generateBlankItemWithEvents(final Event ...events) {
		return new Item(events);
	}
	/** 
	 * @see thingFramework.Thing#makeCopy()
	 */
	@Override
	public Item makeCopy() {
		return new Item(this);
	}
	/** 
	 * @see thingFramework.Thing#validateAttribute(attributes.Attribute)
	 */
	@Override boolean validateAttribute(final Attribute<?> attribute) {
		return !attribute.hasCharacteristic(AttributeCharacteristic.CREATUREONLY);
	}
	/** 
	 * @see thingFramework.Thing#addGeneratedAttributes(attributes.attributegenerators.AttributeGenerator)
	 */
	@Override
	public void addGeneratedAttributes(final AttributeGenerator generator) {
		generator.addAttributes(this);
	}


	
}
