package thingFramework;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

public class Item extends Thing implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Item(String name, String image, Set<Attribute> attributes) {
		super(name, image, attributes);
		
		
	}

	@Override
	boolean vallidateAttributes(Set<Attribute> attributes) {
		return Attribute.validateItem(attributes);
	
	}

	@Override
	EnumSet<ThingType> setThingType() {
		return EnumSet.of(ThingType.ITEM);
	}

	
}
