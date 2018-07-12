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

public class Pokemon extends Thing implements Serializable, Eventful, Imagable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Collection<Modifier<Pokemon>> pokemonModifiers = new HashSet<Modifier<Pokemon>>(); 
	public Pokemon(final String name, final String image, final Set<Attribute> attributes) {
		super(name, image, attributes);
		
	}
	public Pokemon(final String name, final String image, final Set<Attribute> attributes, final Event ...events) {
		super(name, image, attributes, GameUtils.toArrayList(events));
	}
	public Pokemon(final String name, final String image, final Set<Attribute> attributes, final List<Event> events) {
		super(name, image, attributes);
	}
	public Pokemon(final Pokemon p) {
		this(p.getName(), p.getImage(), Thing.makeAttributeCopy(p.getAttributes()), p.getEvents());
	}

	@Override
	protected
	boolean vallidateAttributes(final Set<Attribute> attributes) {
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
	void onPlace(final Board board) {
		board.notifyPokemonAdded(this);
		
	}
	@Override
	public
	void onRemove(final Board board) {
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
	public boolean addModifierIfShould(final Modifier<Pokemon> mod) {
		return Thing.addModifierIfShould(mod, pokemonModifiers, this);
	}
	
	public boolean removeModifierIfPresent(final Modifier<Pokemon> mod) {
		return Thing.removeModifierIfPresent(mod, pokemonModifiers, this);
	}

	

	
}
