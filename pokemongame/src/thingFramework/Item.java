package thingFramework;

import java.util.Map;

import effects.Event;

public class Item extends Thing {
	
	public Item(String name, String image, Map<Attribute, Object> attributes) {
		super(name, image, attributes);
		
		
	}

	@Override
	boolean vallidateAttributes(Map<Attribute, Object> attributes) {
		return Attribute.validateItem(attributes.keySet());
	
	}
}
