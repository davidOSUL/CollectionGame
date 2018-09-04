package loaders;

/**
 * An exception that is thrown if something goes wrong while loading in Things from CSV files
 * @author David O'Sullivan
 *
 */
public class ThingLoadException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Throws a new ThingLoadException with the provided message
	 * @param message the error message
	 */
	public ThingLoadException(final String message) {
		super(message);
	}
}
