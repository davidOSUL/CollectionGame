package thingFramework;

import java.io.Serializable;

import attributes.Attribute;
import attributes.AttributeCharacteristic;
import attributes.attributegenerators.AttributeGenerator;
import effects.Eventful;
import game.Board;
import interfaces.Imagable;

/**
 * A Creature is one of the two types of Things. They generally are found from spawning on the board, though it 
 * couldbe posssible to set them up as a ShopItem
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
	 * @param haveBoardAttributes if false, won't create a BoardAttributeManager
	 */
	public Creature(final String name, final String image, final boolean haveBoardAttributes) {
		super(name, image, haveBoardAttributes);
	}
	private Creature(final Creature p) {
		super(p);
	}


	/** 
	 * @see thingFramework.Thing#onPlace(game.Board)
	 */
	@Override
	public
	void onPlace(final Board board) {
		board.notifyCreatureAdded(this);
		
	}
	/** 
	 * @see thingFramework.Thing#onRemove(game.Board)
	 */
	@Override
	public
	void onRemove(final Board board) {
		board.notifyCreatureRemoved(this);
		
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
