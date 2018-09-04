package attributes;

/**
 * A RuntimeException To be thrown when an attribute is looked for but is not present
 * @author David O'Sullivan
 *
 */
public class AttributeNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Creates a new AttributeNotFoundException with the specified message
	 * @param message
	 */
	public AttributeNotFoundException(final String message) {
		super(message);
	}

}
