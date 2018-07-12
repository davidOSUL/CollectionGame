package effects;

import game.Board;
import modifiers.Modifier;
import thingFramework.Pokemon;
import thingFramework.Thing;

public class GlobalPokemonModifierEvent<C extends Thing> extends GlobalModifierEvent<Pokemon, C> {

	public GlobalPokemonModifierEvent(final Modifier<Pokemon> mod, final boolean removeWhenDone, final boolean displayCountdown) {
		super(mod, removeWhenDone, displayCountdown);
	}
	private GlobalPokemonModifierEvent(final GlobalPokemonModifierEvent<C> copy) {
		this(new Modifier<Pokemon>(copy.getMod()), copy.getRemoveCreatorWhenDone(), copy.getDisplayCountdown());
	}
	@Override
	public void addModToBoard(final Board b) {
		b.addGlobalPokemonModifier(getMod());
	}

	@Override
	public void removeModFromBoard(final Board b) {
		b.removeGlobalPokemonModifier(getMod());
		
	}

	@Override
	public HeldEvent<C> makeCopy() {
		return new GlobalPokemonModifierEvent<C>(this);
	}

}
