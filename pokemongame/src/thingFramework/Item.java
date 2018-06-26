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
	private Item() {}
	public Item(String name, String image, Set<Attribute> attributes) {
		super(name, image, attributes);
		
		
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

	
}
