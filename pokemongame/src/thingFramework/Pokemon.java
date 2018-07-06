package thingFramework;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import effects.Event;
import effects.Eventful;
import game.Board;
import gameutils.GameUtils;
import interfaces.Imagable;


public class Pokemon extends Thing implements Serializable, Eventful, Imagable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Pokemon(String name, String image, Set<Attribute> attributes) {
		super(name, image, attributes);
		
	}
	public Pokemon(String name, String image, Set<Attribute> attributes, Event ...events) {
		super(name, image, attributes, GameUtils.toArrayList(events));
	}
	public Pokemon(String name, String image, Set<Attribute> attributes, List<Event> events) {
		super(name, image, attributes);
	}
	public Pokemon(Pokemon p) {
		this(p.getName(), p.getImage(), p.getAttributes(), p.getEvents());
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
	@Override
	public String getDiscardText() {
		return "Set " + getName() + " free";
	}
	
}
