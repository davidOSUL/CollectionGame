package gui.guiutils;

/**
 * Set of Common GUI Constants that multiple different classes might use.
 * @author David O'Sullivan
 */
public final class GUIConstants {
	/**
	 * The max allowable distance that the mouse can move in between pressing and releasing to count as a click
	 */
	public static final int CLICK_DIST_THRESH = 20;
	/**
	 * Show the dialog to the user that confirms that they want to quit, and asks if they want to save
	 */
	public static final boolean SHOW_CONFIRM_ON_CLOSE = true;
	/**
	 * Skips the loading screen, and goes straight to Start new game
	 */
	public static final boolean SKIP_LOAD_SCREEN = false;
	private GUIConstants() {}

}
