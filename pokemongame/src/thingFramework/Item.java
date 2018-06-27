package thingFramework;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

import game.Board;

public class Item extends Thing implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Item() {}
	public Item(String name, String image, Set<Attribute> attributes) {
		super(name, image, attributes);
		
		
	}
	public Item(Item i) {
		this(i.getName(), i.getImage(), i.getAttributes());
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
	@Override
	public Thing makeCopy() {
		return new Item(this);
	}

	
}
