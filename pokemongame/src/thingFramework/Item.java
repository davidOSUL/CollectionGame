package thingFramework;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public Item(final String name, final String image, final Set<Attribute> attributes) {
		super(name, image, attributes);
	}
	public Item(final String name, final String image, final Set<Attribute> attributes, final Event...events ) {
		super(name, image, attributes, GameUtils.toArrayList(events));
	}
	public Item(final String name, final String image, final Set<Attribute> attributes, final List<Event> events ) {
		super(name, image, attributes, events);
	}
	public Item(final Item i) {
		this(i.getName(), i.getImage(), Thing.makeAttributeCopy(i.getAttributes()), i.getEvents());
	}
	@Override
	protected
	boolean vallidateAttributes(final Set<Attribute> attributes) {
		return Attribute.validateItem(attributes);
	
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


	
}
