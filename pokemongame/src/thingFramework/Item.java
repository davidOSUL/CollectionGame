package thingFramework;

import java.io.Serializable;

import attributes.Attribute;
import attributes.AttributeCharacteristic;
import attributes.attributegenerators.AttributeGenerator;
import effects.Event;
import effects.Eventful;
import game.Board;
import interfaces.Imagable;

public class Item extends Thing implements Serializable, Eventful, Imagable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Item() {}
	private Item(final Event...events) {
		super(events);
	}
	public Item(final String name, final String image) {
		super(name, image);
	}
	public Item(final String name, final String image, final boolean haveBoardAttributes) {
		super(name, image, haveBoardAttributes);
	}
	private Item(final Item i) {
		super(i);
	}



	@Override
	public void onPlace(final Board board) {
		board.notifyItemAdded(this);
	}

	@Override
	public void onRemove(final Board board) {
		board.notifyItemRemoved(this);
	}
	public static Item generateBlankItem() {
		return new Item();
	}
	public static Item generateBlankItemWithEvents(final Event ...events) {
		return new Item(events);
	}
	@Override
	public Item makeCopy() {
		return new Item(this);
	}
	@Override
	protected boolean validateAttribute(final Attribute<?> attribute) {
		return !attribute.hasCharacteristic(AttributeCharacteristic.POKEONLY);
	}
	@Override
	public void addGeneratedAttributes(final AttributeGenerator generator) {
		generator.addAttributes(this);
	}


	
}
