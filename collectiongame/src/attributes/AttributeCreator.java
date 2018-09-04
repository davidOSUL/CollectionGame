package attributes;

/**
 * Generates Attribute's of a specified type as AttributeTemplates which can then be used to make new Attributes with the 
 * newCopy() method
 * @author David O'Sullivan
 *
 * @param <T> the type of the templateAttribute
 */
interface AttributeCreator<T> {

	/**
	 * Returns the AttributeTemplate with the specified name. This is just a AttributeTemplate, and so to make use of it,
	 * users should call .newCopy() on the AttributeTemplate
	 * @param attributeName the templateAttribute name
	 * @return If a valid attributeName, an AttributeTemplate<T> with the specified templateAttribute.
	 */
	AttributeTemplate<T> getAttributeTemplate(String attributeName);


	/**
	 * Returns true if the attributeName is a valid Attribute<T> that this AttributeCreator<T> can create
	 * @param attributeName the templateAttribute name to lookup
	 * @return true if this is a valid, creatable templateAttribute for this AttributeCreator<T>
	 */
	boolean containsAttributeTemplate(String attributeName);
	/**
	 * A wrapper for an Attribute<T> that only allows one to create new copies, leaving the original template unchanged
	 * @author David O'Sullivan
	 *
	 * @param <T> the type of the templateAttribute
	 */
	public static class AttributeTemplate<T> {
		private final Attribute<T> templateAttribute;
		private AttributeTemplate(final Attribute<T> templateAttribute) {
			this.templateAttribute = templateAttribute;
		}
		/**
		 * Generates a new AttributeTemplate<T> with the input templateAttribute to be used as the template
		 * @param <T> the type of the templateAttribute
		 * @param templateAttribute the templateAttribute to use as a template for future copies
		 * @return the generated template
		 */
		public static <T> AttributeTemplate<T> generateTemplate(final Attribute<T> templateAttribute) {
			return new AttributeTemplate<T>(templateAttribute);
		}
		/**
		 * @return a new copy of the Attribute<T> that this AttributeTemplate<T> has
		 */
		public Attribute<T> newCopy() {
			return templateAttribute.makeCopy();
		}
		
	}

}