package gui.mvpFramework.presenter;

/**
 * The type of the Add Attempt
 * @author David O'Sullivan
 *
 */
public enum AddType{
	/**
	 * Notification button was pressed
	 */
	CREATURE_FROM_QUEUE(true), 
	/**
	 * moving around a thing that was already placed
	 */
	PRIOR_ON_BOARD(false), 
	/**
	 * purchasing an item from the shop
	 */
	ITEM_FROM_SHOP(true);
	/**
	 * true if the thing being added is new (was not previously generated and added to the board)
	 */
	final boolean isNewThing;
	private AddType(final boolean newThing) {
		this.isNewThing = newThing;
	}
}