package thingFramework;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Set;

import game.Board;


public class Pokemon extends Thing implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Pokemon(String name, String image, Set<Attribute> attributes) {
		super(name, image, attributes);
		
	}
	public Pokemon(Pokemon p) {
		this(p.getName(), p.getImage(), p.getAttributes());
	}

	@Override
	protected
	boolean vallidateAttributes(Set<Attribute> attributes) {
		// TODO Auto-generated method stub
		return Attribute.validatePokemon(attributes);
	}

	@Override
	protected
	EnumSet<ThingType> setThingType() {
		return EnumSet.of(ThingType.POKEMON);
	}
	@Override
	public
	void onPlace(Board board) {
		board.notifyPokemonAdded(this);
		
	}
	@Override
	public
	void onRemove(Board board) {
		board.notifyPokemonRemoved(this);
		
	}
	@Override
	public Thing makeCopy() {
		return new Pokemon(this);
	}
}
