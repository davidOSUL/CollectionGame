package thingFramework;

import java.io.Serializable;

import attributes.Attribute;
import attributes.AttributeCharacteristic;
import attributes.attributegenerators.AttributeGenerator;
import effects.Eventful;
import interfaces.Imagable;
import model.ThingObserver;

/**
 * A Creature is one of the two types of Things. They generally are found from spawning on the model, though it 
 * could be possible to set them up as a purchasable Thing in the shop as well
 * @author David O'Sullivan
 *
 */
public class Creature extends Thing implements Serializable, Eventful, Imagable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Creates a new Creature with the provided name and image
	 * @param name the name of the Creature
	 * @param image the path to the image
	 */
	public Creature(final String name, final String image) {
		super(name, image);
		
	}
	/**
	 * Creates a new Creature with the provided name and image
	 * @param name the name of the Creature
	 * @param image the path to the image
	 * @param haveModelAttributes if false, won't create a ModelAttributeManager
	 */
	public Creature(final String name, final String image, final boolean haveModelAttributes) {
		super(name, image, haveModelAttributes);
	}
	private Creature(final Creature creature) {
		super(creature);
	}


	/** 
	 * @see thingFramework.Thing#onPlace(model.ThingObserver)
	 */
	@Override
	public
	void onPlace(final ThingObserver observer) {
		observer.notifyCreatureAdded(this);
		
	}
	/** 
	 * @see thingFramework.Thing#onRemove(model.ThingObserver)
	 */
	@Override
	public
	void onRemove(final ThingObserver observer) {
		observer.notifyCreatureRemoved(this);
		
	}
	/** 
	 * @see thingFramework.Thing#makeCopy()
	 */
	@Override
	public Creature makeCopy() {
		return new Creature(this);
	}
	/** 
	 * @see thingFramework.Thing#getDiscardText()
	 */
	@Override
	public String getDiscardText() {
		return "Set " + getName() + " free";
	}
	/** 
	 * @see thingFramework.Thing#validateAttribute(attributes.Attribute)
	 */
	@Override boolean validateAttribute(final Attribute<?> attribute) {
		return !attribute.hasCharacteristic(AttributeCharacteristic.ITEMONLY);
	}
	/** 
	 * @see thingFramework.Thing#addGeneratedAttributes(attributes.attributegenerators.AttributeGenerator)
	 */
	@Override
	public void addGeneratedAttributes(final AttributeGenerator generator) {
		generator.addAttributes(this);
	}
	

	
}
