package attributes.attributegenerators;

/**
 * Contains the set of all AttributeGenerators
 * @author David O'Sullivan
 *
 */
public class AttributeGenerators {
	private static final AttributeGenerator RARITY_GEN_INSTANCE = new AttributeGeneratorFromRarity();
	/**
	 * Gets the generator with the specified name
	 * @param generatorName the name of the AttributeGenerator
	 * @return the AttributeGenerator with the specified name
	 * @throws IllegalArgumentException if the name is invalid
	 */
	public static final AttributeGenerator getGenerator(final String generatorName) {
		switch(generatorName.toUpperCase()) {
		case "RARITY_GEN":
			return RARITY_GEN_INSTANCE;
		default:
			throw new IllegalArgumentException("Unrecognized attribute generator name: " + generatorName);
		}
	}
	
}
