package thingFramework;

import java.io.Serializable;

import attributes.Attribute;
import attributes.AttributeCharacteristic;
import attributes.attributegenerators.AttributeGenerator;
import effects.Eventful;
import game.Board;
import interfaces.Imagable;

public class Pokemon extends Thing implements Serializable, Eventful, Imagable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Pokemon(final String name, final String image) {
		super(name, image);
		
	}
	public Pokemon(final String name, final String image, final boolean haveBoardAttributes) {
		super(name, image, haveBoardAttributes);
	}
	private Pokemon(final Pokemon p) {
		super(p);
	}


	@Override
	public
	void onPlace(final Board board) {
		board.notifyPokemonAdded(this);
		
	}
	@Override
	public
	void onRemove(final Board board) {
		board.notifyPokemonRemoved(this);
		
	}
	@Override
	public Pokemon makeCopy() {
		return new Pokemon(this);
	}
	@Override
	public String getDiscardText() {
		return "Set " + getName() + " free";
	}
	@Override
	protected boolean validateAttribute(final Attribute<?> attribute) {
		return !attribute.hasCharacteristic(AttributeCharacteristic.ITEMONLY);
	}
	@Override
	public void addGeneratedAttributes(final AttributeGenerator generator) {
		generator.addAttributes(this);
	}
	

	
}
