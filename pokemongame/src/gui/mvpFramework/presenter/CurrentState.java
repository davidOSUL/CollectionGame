package gui.mvpFramework.presenter;

/**
 * The CurrentState of the Game
 * @author David O'Sullivan
 *
 */
enum CurrentState {
	/**
	 * Regular gameplay, the user is not actively doing anything
	 */
	GAMEPLAY, 
	/**
	 * The user has clicked the notification button, and is currently making a decision 
	 */
	NOTIFICATION_WINDOW, 
	/**
	 * The user is placing a GridSpace
	 */
	PLACING_SPACE, 
	/**
	 * The user is in the window that appears to confirm the removal of a GridSpace
	 */
	DELETE_CONFIRM_WINDOW, 
	/**
	 * The user is in the shop 
	 */
	IN_SHOP, 
	/**
	 * The user is in the window that appears to confirm the purchase of an item from the shop
	 */
	PURCHASE_CONFIRM_WINDOW, 
	/**
	 * The user is in the window that appears to confirm the selling back of an item to the shop
	 */
	SELL_BACK_CONFIRM_WINDOW, 
	/**
	 * The user is in the Advanced stats window
	 */
	ADVANCED_STATS_WINDOW;
}