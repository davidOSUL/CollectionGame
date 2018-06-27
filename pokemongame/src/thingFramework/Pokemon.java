package thingFramework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import effects.Event;
import game.Board;
import game.BoardAttributeManager;
import gameutils.GameUtils;


public class Pokemon extends Thing implements Serializable, Eventful {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Event> events = new ArrayList<Event>();
	public Pokemon(String name, String image, Set<Attribute> attributes) {
		super(name, image, attributes);
		events.addAll(BoardAttributeManager.getEvents(getBoardAttributes()));
		
	}
	public Pokemon(String name, String image, Set<Attribute> attributes, Event ...events) {
		this(name, image, attributes, GameUtils.toArrayList(events));
	}
	public Pokemon(String name, String image, Set<Attribute> attributes, List<Event> events) {
		this(name, image, attributes);
		this.events.addAll(events);
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
	public List<Event> getEvents() {
		return events;
	}
	
}
