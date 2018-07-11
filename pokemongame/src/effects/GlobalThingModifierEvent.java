/**
 * 
 */
package effects;

import game.Board;
import modifiers.Modifier;
import thingFramework.Thing;

/**
 * @author David O'Sullivan
 *
 */
public class GlobalThingModifierEvent<C extends Thing> extends GlobalModifierEvent<Thing, C> {

	public GlobalThingModifierEvent(final Modifier<Thing> mod, final boolean removeWhenDone, final boolean displayCountdown) {
		super(mod, removeWhenDone, displayCountdown);
	}
	private GlobalThingModifierEvent(final GlobalThingModifierEvent<C> copy) {
		this(new Modifier<Thing>(copy.getMod()), copy.getRemoveCreatorWhenDone(), copy.getDisplayCountdown());
	}
	@Override
	public void addModToBoard(final Board b) {
		b.addGlobalThingModifier(getMod());	
	}
	@Override
	public void removeModFromBoard(final Board b) {
		b.removeGlobalThingModifier(getMod());
	}
	@Override
	public HeldEvent<C> makeCopy() {
		return new GlobalThingModifierEvent(this);
	}

}
