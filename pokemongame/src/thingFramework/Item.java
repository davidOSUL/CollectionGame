package thingFramework;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import effects.Event;
import game.Board;
import gameutils.GameUtils;

public class Item extends Thing implements Serializable, Eventful{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Item() {}
	private Item(Event...events) {
		super(events);
	}
	public Item(String name, String image, Set<Attribute> attributes) {
		super(name, image, attributes);
	}
	public Item(String name, String image, Set<Attribute> attributes, Event...events ) {
		super(name, image, attributes, GameUtils.toArrayList(events));
	}
	public Item(String name, String image, Set<Attribute> attributes, List<Event> events ) {
		super(name, image, attributes, events);
	}
	public Item(Item i) {
		this(i.getName(), i.getImage(), i.getAttributes(), i.getEvents());
	}
	@Override
	protected
	boolean vallidateAttributes(Set<Attribute> attributes) {
		return Attribute.validateItem(attributes);
	
	}

	@Override
	protected
	EnumSet<ThingType> setThingType() {
		return EnumSet.of(ThingType.ITEM);
	}

	@Override
	public void onPlace(Board board) {
		//Nothing
	}

	@Override
	public void onRemove(Board board) {
		//Nothing
	}
	public static Item generateBlankItem() {
		return new Item();
	}
	public static Item generateBlankItemWithEvents(Event ...events) {
		return new Item(events);
	}
	@Override
	public Thing makeCopy() {
		return new Item(this);
	}


	
}
