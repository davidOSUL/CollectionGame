package thingFramework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import effects.Event;
import game.Board;
import game.BoardAttributeManager;
import gameutils.GameUtils;

public class Item extends Thing implements Serializable, Eventful {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Event> events = new ArrayList<Event>();
	protected Item() {}
	private Item(Event...events) {
		this();
		this.events.addAll(GameUtils.toArrayList(events));
	}
	public Item(String name, String image, Set<Attribute> attributes) {
		super(name, image, attributes);
		events.addAll(BoardAttributeManager.getEvents(getBoardAttributes()));
		
		
	}
	public Item(String name, String image, Set<Attribute> attributes, Event...events ) {
		this(name, image, attributes, GameUtils.toArrayList(events));
	}
	public Item(String name, String image, Set<Attribute> attributes, List<Event> events ) {
		this(name, image, attributes);
		this.events.addAll(events);
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
	@Override
	public List<Event> getEvents() {
		return events;
	}

	
}
