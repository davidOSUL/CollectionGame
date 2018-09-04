package attributes;

/**
 * Generates Attribute's of a specified type as "templates" which can then be used to make new attributes with the 
 * newCopy() method
 * @author David O'Sullivan
 *
 * @param <T> the type of the attribute
 */
interface AttributeCreator<T> {

	/**
	 * Returns the AttributeTemplate with the specified name. This is just a "template", and so to make use of it,
	 * users should call .newCopy() on the attribute
	 * @param attributeName the attribute name
	 * @return If a valid attributeName, an AttributeTemplate<T> with the specified attribute.
	 */
	AttributeTemplate<T> getAttributeTemplate(String attributeName);


	/**
	 * Returns true if the attributeName is a valid Attribute<T> that this AttributeCreator<T> can create
	 * @param attributeName the attribute name to lookup
	 * @return true if this is a valid, creatable attribute for this AttributeCreator<T>
	 */
	boolean containsAttributeTemplate(String attributeName);
	/**
	 * A wrapper for an Attribute<T> that only allows one to create new copies, leaving the original template unchanged
	 * @author David O'Sullivan
	 *
	 * @param <T> the type of the attribute
	 */
	public static class AttributeTemplate<T> {
		private final Attribute<T> attribute;
		private AttributeTemplate(final Attribute<T> attribute) {
			this.attribute = attribute;
		}
		/**
		 * Generates a new AttributeTemplate<T> with the input attribute to be used as the template
		 * @param <T> the type of the attribute
		 * @param attribute the attribute to use as a template for future copies
		 * @return the generated template
		 */
		public static <T> AttributeTemplate<T> generateTemplate(final Attribute<T> attribute) {
			return new AttributeTemplate<T>(attribute);
		}
		/**
		 * @return a new copy of the Attribute<T> that this AttributeTemplate<T> has
		 */
		public Attribute<T> newCopy() {
			return attribute.makeCopy();
		}
		
	}

}