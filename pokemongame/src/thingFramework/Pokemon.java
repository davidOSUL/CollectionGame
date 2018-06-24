package thingFramework;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;


public class Pokemon extends Thing implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Pokemon(String name, String image, Set<Attribute> attributes) {
		super(name, image, attributes);
		
	}

	@Override
	boolean vallidateAttributes(Set<Attribute> attributes) {
		// TODO Auto-generated method stub
		return Attribute.validatePokemon(attributes);
	}

	@Override
	EnumSet<ThingType> setThingType() {
		return EnumSet.of(ThingType.POKEMON);
	}
}
