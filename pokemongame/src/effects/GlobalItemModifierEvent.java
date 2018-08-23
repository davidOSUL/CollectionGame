/**
 * 
 */
package effects;

import game.Board;
import modifiers.Modifier;
import thingFramework.Item;

/**
 * @author David O'Sullivan
 *
 */
public class GlobalItemModifierEvent extends GlobalModifierEvent<Item> {

	public GlobalItemModifierEvent(final Modifier<Item> mod, final boolean removeWhenDone, final boolean displayCountdown) {
		super(mod, removeWhenDone, displayCountdown);
	}
	private GlobalItemModifierEvent(final GlobalItemModifierEvent copy) {
		super(copy);
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
	public GlobalItemModifierEvent makeCopy() {
		return new GlobalItemModifierEvent(this);
	}

	
}
