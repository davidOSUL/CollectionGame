package model;

/**
 * Generates new random Creatures. The rarity of the creatures generated is based on multiple factors including the 
 * popularity of the ModelInterface. 
 * @author David O'Sullivan
 *
 */
public interface WildCreatureGeneratorInterface {

	/**
	 * @param automaticSpawn if true will automatically generate a creature regardless of percent chance
	 *@return the name of the generated creature, null if none are found
	 */
	String lookForCreature(boolean automaticSpawn);

	/**
	 * @return the percent chance that the randomNum generated by lookForCreature will be modified 
	 *by a value. This value increases as popularity increases. 
	 *In particular this will return a value of the form:
	 *<br> Aln(pop^B+C)+Dpop^E
	 */
	double getPercentChancePopularityModifies();

	/**
	 * @return The amount by which we will move up in rarity ranking for a creature
	 * Will be in the form of logistic growth: MAX_MODIFIER/1+Be^-r*pop + C
	 * C is implicitly minimum popularity boost
	 */
	int getPopularityModifier();

	/**
	 * @return Percent chance that a creature is found. 
	 * Will be value of the form pop*A+Gold/B+C/D*creatureMapSize+E - F, D!=0
	 * range modified to [MIN_PERCENT_CHANCE, MAX_PERCENT_CHANCE]
	 */
	double getPercentChanceCreatureFound();

	/**
	 * Returns the period at which the game checks for new creatures
	 * @return The period at which the game checks for new creatures.
	 *  
	 */
	double getLookForCreaturesPeriod();

}