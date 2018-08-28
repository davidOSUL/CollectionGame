package gameutils;

/**
 * Constants for all classes to make use of
 * @author David O'Sullivan
 */
public final class Constants {
	/**
	 * set to true to print out debug information
	 */
	public final static boolean DEBUG = false;
	/**
	 * Set to true to print board, even if DEBUG is false
	 */
	public final static boolean PRINT_BOARD = false;
	/**
	 * Change duplicate percentage to 100% and make Pokemon spawn automatically and rapidly
	 */
	public final static boolean RAPID_SPAWN = true;
	/**
	 * starts game with large amount of money/popularity
	 */
	public final static boolean CHEAT_MODE = true;
	/**
	 * The amount of money/popularity to start the game with when in cheat mode
	 */
	public final static int CHEAT_MODE_VALUE = 100000000;
	/**
	 * The amount of money to start the game with
	 */
	public final static int STARTING_GOLD = 100;
	/**
	 * The amount of popularity to start the game with
	 */
	public final static int STARTING_POP = 0;
	private Constants() {
		// TODO Auto-generated constructor stub
	}

}
