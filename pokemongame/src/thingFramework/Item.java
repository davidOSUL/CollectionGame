package thingFramework;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

import attributes.Attribute;
import attributes.AttributeCharacteristic;
import effects.Event;
import effects.Eventful;
import game.Board;
import gameutils.GameUtils;
import interfaces.Imagable;
import modifiers.Modifier;

public class Item extends Thing implements Serializable, Eventful, Imagable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Collection<Modifier<Item>> itemModifiers = new HashSet<Modifier<Item>>(); 
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
	public Item(final Item i) {
		super(i);
	}

	@Override
	protected
	EnumSet<ThingType> setThingType() {
		return EnumSet.of(ThingType.ITEM);
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
	
	public boolean addModifierIfShould(final Modifier<Item> mod) {
		return Thing.addModifierIfShould(mod, itemModifiers, this);
	}
	
	public boolean removeModifierIfPresent(final Modifier<Item> mod) {
		return Thing.removeModifierIfPresent(mod, itemModifiers, this);
	}
	@Override
	protected boolean validateAttribute(final Attribute<?> attribute) {
		return !attribute.hasCharacteristic(AttributeCharacteristic.POKEONLY);
	}


	
}
