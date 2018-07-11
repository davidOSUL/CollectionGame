/**
 * 
 */
package effects;

import game.Board;
import modifiers.Modifier;
import thingFramework.Item;
import thingFramework.Thing;

/**
 * @author David O'Sullivan
 *
 */
public class GlobalItemModifierEvent<C extends Thing> extends GlobalModifierEvent<Item, C> {

	public GlobalItemModifierEvent(final Modifier<Item> mod, final boolean removeWhenDone, final boolean displayCountdown) {
		super(mod, removeWhenDone, displayCountdown);
	}
	private GlobalItemModifierEvent(final GlobalItemModifierEvent<C> copy) {
		this(new Modifier<Item>(copy.getMod()), copy.getRemoveCreatorWhenDone(), copy.getDisplayCountdown());
	}
	@Override
	public void addModToBoard(final Board b) {
		b.addGlobalItemModifier(getMod());	
	}
	@Override
	public void removeModFromBoard(final Board b) {
		b.removeGlobalItemModifier(getMod());
		
	}
	@Override
	public HeldEvent<C> makeCopy() {
		return new GlobalItemModifierEvent(this);
	}

	
}
