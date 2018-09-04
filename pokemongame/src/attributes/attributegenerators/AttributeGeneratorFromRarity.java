package attributes.attributegenerators;

import attributes.AttributeNotFoundException;
import attributes.ParseType;
import gameutils.GameUtils;
import thingFramework.Creature;
import thingFramework.Item;

/**
 * Generates new Attributes for creatures based on rarities. Does not add any attributes to items
 * @author David O'Sullivan
 *
 */
class AttributeGeneratorFromRarity implements AttributeGenerator{

	/** 
	 * Sets the gpm, gph, popularity, and happiness using functions based solely on this creature's rarity
	 * @see attributes.attributegenerators.AttributeGenerator#addAttributes(thingFramework.Creature)
	 */
	@Override
	public void addAttributes(final Creature c) {
			if (!c.containsAttribute("rarity"))
				throw new IllegalArgumentException("Creature " + c +  "does not have a metric for rarity");
			int rarity = 0;
			try {
				rarity = c.getAttributeValue("rarity", ParseType.INTEGER);
			} catch (final AttributeNotFoundException e) {
				e.printStackTrace();
			}
			final String[] attributes = {"gpm", "gph", "popularity boost", "happiness"};
			final Integer[] values = {calcGPM(rarity), calcGPH(rarity),calcPopularity(rarity), calcHappiness(rarity)};
			c.addAttributes(attributes, values, ParseType.INTEGER);
		
	}

	/** 
	 * Does not add any attributes
	 * @see attributes.attributegenerators.AttributeGenerator#addAttributes(thingFramework.Item)
	 */
	@Override
	public void addAttributes(final Item i) {
		//Nothing
	}
	private int calcGPM(final int rarity) {
		if (rarity < 70)
			return 0;
		double percentChance = 0;
		if (rarity < 86 )
			percentChance = rarity-10;
		else
			percentChance = rarity-5;
		if (!GameUtils.testPercentChance(percentChance))
			return 0;
		if (GameUtils.testPercentChance(1) && rarity > 90)
			return 9;
		if (rarity < 86)
			return GameUtils.testPercentChance(30) ? 2 : 1;
		else if (rarity < 99)
			return GameUtils.testPercentChance(40) ? 3 : 2;
		else 
			return GameUtils.testPercentChance(50) ? 5 : 3;
	}
	
	private int calcGPH(final int rarity) {
		if (rarity == 99 && GameUtils.testPercentChance(2))
			return 60;
		return 5*(int)Math.log(rarity);
		
	}
	private int calcPopularity(final int rarity) {
		final int modifier = rarity == 99 ? 10 : (GameUtils.testPercentChance(20) ? 3 : 0);
		return (int)Math.pow(rarity, .7) + modifier;
	}
	private int calcHappiness(final int rarity) {
		return Math.max(2, rarity/10-2);
	}

}
