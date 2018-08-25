package thingFramework;

import java.io.Serializable;
import java.util.List;

import attributes.Attribute;
import attributes.AttributeCharacteristic;
import attributes.attributegenerators.AttributeGenerator;
import effects.Event;
import effects.Eventful;
import game.Board;
import gameutils.GameUtils;
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
	public Item(final String name, final String image, final Event ...events) {
		super(name, image, GameUtils.toArrayList(events));
	}
	public Item(final String name, final String image, final List<Event> events) {
		super(name, image, events);
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
	public Thing makeCopy() {
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
