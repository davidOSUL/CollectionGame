package attributes.attributegenerators;

public class AttributeGenerators {
	private static final AttributeGenerator RARITY_GEN_INSTANCE = new AttributeGeneratorFromRarity();
	public static final AttributeGenerator getGenerateFromRarity() {
		return RARITY_GEN_INSTANCE;
	}
	
}
