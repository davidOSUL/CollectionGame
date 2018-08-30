package gui.mvpFramework.presenter;

/**
 * The context of the Add Attempt
 * POKE_FROM_QUEUE == notifcation butotn was pressed
 * PRIOR_ON_BOARD == moving around a thing that was already placed
 * @author David O'Sullivan
 *
 */
public enum AddType{
	POKE_FROM_QUEUE(true), PRIOR_ON_BOARD(false), ITEM_FROM_SHOP(true);
	boolean isNewThing;
	private AddType(final boolean newThing) {
		this.isNewThing = newThing;
	}
}