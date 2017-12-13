package thingFramework;

import java.util.Map;

public class Pokemon extends Thing {
	
	public Pokemon(String name, String image, Map<Attribute, Object> attributes) {
		super(name, image, attributes);
		
	}

	@Override
	boolean vallidateAttributes(Map<Attribute, Object> attributes) {
		// TODO Auto-generated method stub
		return Attribute.validatePokemon(attributes.keySet());
	}
}
