package model.defaultimplementation;

import static gameutils.Constants.RAPID_SPAWN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import attributes.AttributeName;
import gameutils.GameUtils;
import loaders.ThingFactory;
import loaders.ThingType;
import model.WildCreatureGeneratorInterface;

/**
 * Main implementation of WildCreatureGeneratorInterface. Generates new random Creatures. The rarity of the creatures generated is based on multiple factors including the 
 * popularity of the Board. 
 * @author David O'Sullivan
 *
 */
class BoardWildCreatureGenerator implements Serializable, WildCreatureGeneratorInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The chance that a creature with a name of a creature already on the board will spawn
	 */
	private static final double PERCENT_CHANCE_DUPLICATE_SPAWNS = RAPID_SPAWN ? 100 : 5;
	/**
	 * The minimum percent chance that when checking for creatures, one is found
	 */
	private static final double MIN_PERCENT_CHANCE_CREATURE_FOUND = 50;
	/**
	 * The maximum percent chance that when checking for creatures, one is found
	 */
	private static final double MAX_PERCENT_CHANCE_CREATURE_FOUND = 90;

	/**
	 * The minimum percent chance that the rarity of a found creature will increase from a popularity boost
	 */
	private static final double MIN_PERCENT_CHANCE_POPULARITY_BOOSTS = 0;

	/**
	 * The maximum change in rarity value from the calculated rarity. (for example if the rarities are:
	 * 1,2,3,4,5), and the calculated rarity is 2, with a boost of 2, you will get a creature with rarity 4
	 */
	private static final double MAX_POPULARITY_BOOST = 10;
	/**
	 * The maximum percent chance that the rarity of a found creature will increase from a popularity boost
	 */
	private static final double MAX_PERCENT_CHANCE_POPULARITY_BOOSTS = 90;

	/**
	 * Maximum number of tries to generate a creature
	 */
	private static final int MAX_ATTEMPTS = 50;

	/**
	 * The minimum period in minutes at which new creatures are checked for
	 */
	private static final double MIN_CREATURE_PERIOD = 1.5;
	/**
	 * The popularity value that when reached signifies that the period for new creatures spawning can drop below
	 * MIN_CREATURE_PERIOD
	 */
	private static final int POP_TO_GET_BONUS_PERIOD_AT = 1000000;
	/**
	 * The period when popularity has reached POP_TO_GET_BONUS_PERIOD_AT
	 */
	private static final double BONUS_PERIOD = 1;
	/**
	 * The popularity value that when reached, causes the way popularity is calculated to shift 
	 * from a linear one to an exponential one
	 */
	private static final double MAX_POP_BEFORE_PERIOD_CALCULATION_SHIFT = 1000;
	/**
	 * The popularity value at which point the period will be calculated with a function
	 */
	private static final double MAX_POP_BEFORE_CALCULATE_PERIOD = 100;
	/**
	 * The period before periods are calculated with a function.
	 */
	private static final double PERIOD_BEFORE_CALCULATION = .5;
	private static final Set<String> legendaryCreatures = ThingFactory.getInstance().getThingsWithAttributeVal(AttributeName.IS_LEGENDARY, true, ThingType.CREATURE);
	private static final Set<String> nonLegendaryCreatures = ThingFactory.getInstance().getThingsWithAttributeVal(AttributeName.IS_LEGENDARY, false, ThingType.CREATURE);

	/**
	 * A map from the sum of the chance of a creature being found (out of RUNNING_TOTAL) and all chances
	 * of creatures loaded in before it, to the name of that creature. This will be in order, sorted by the sum.
	 */
	private final TreeMap<Long, String> creatureCumulativeChance = new TreeMap<Long, String>();
	/**
	 * A map from the RARITY (NOT CHANCE) to a list of all creatures with that rarity, in order of rarity
	 */
	private  final TreeMap<Integer, List<String>> creatureRaritiesInOrder = new TreeMap<Integer, List<String>>();
	/**
	 * Map from nonlegendary creature names to their RARITY (NOT CHANCE) value
	 */
	private final Map<String, Integer> creatureRarity;
	/**
	 * Legendaries that haven't been generated yet
	 */
	private final Set<String> unFoundLegendaries = new HashSet<String>(legendaryCreatures);
	/**
	 * This is the value of the total chance rarities of every creature. In other words,
	 * it is the denominator for determining the percent chance that a certain creature
	 * will show up (that is the probability will be: getRelativeChanceRarity(creature.rarity)/RUNNING_TOTAL)
	 */
	private final long RUNNING_TOTAL;
	private final Board  holder;
	/**
	 * Creates a new WildCreatureGenerator
	 * @param b the board that has this WildCreatureGenerator
	 */
	BoardWildCreatureGenerator(final Board b) { 
		this.holder = b;
		creatureRarity =
				ThingFactory.getInstance().<Integer>mapFromSetToAttributeValue(AttributeName.RARITY, ThingType.CREATURE)
				.entrySet().stream().filter(c -> nonLegendaryCreatures.contains(c.getKey()))
				.collect(Collectors.toMap(c-> c.getKey(), c-> c.getValue()));
		RUNNING_TOTAL = calcRunningTotal();
	}
	private long calcRunningTotal() {
		long runningTotal = 0; //running total

		for (final Map.Entry<String, Integer> entry: creatureRarity.entrySet()) {
			final int rarity = entry.getValue();
			final String name = entry.getKey();
			final int percentChance = getRelativeChanceRarity(rarity);
			runningTotal += percentChance;
			creatureCumulativeChance.put(runningTotal, name);
			if (!creatureRaritiesInOrder.containsKey(rarity)) {
				final List<String> list = new ArrayList<String>();
				list.add(name);
				creatureRaritiesInOrder.put(rarity, list);
			}
			else {
				creatureRaritiesInOrder.get(rarity).add(name);
			}
		}
		return runningTotal;
	}
	/** 
	 * @see model.WildCreatureGeneratorInterface#lookForCreature(boolean)
	 */
	@Override
	@SuppressWarnings("unused") //elements could be marked as such if PERCENT_CHANCE_DUPLIATE_SPAWNS != 0
	public String lookForCreature(final boolean automaticSpawn) {
		//first check if a Creature is even found
		int attempts = 0;
		if (!automaticSpawn && !GameUtils.testPercentChance(getPercentChanceCreatureFound()))
			return null;
		String name = null;
		do {
			attempts++;
			name = changeToLegendaryIfShould(modifyIfShould(findNextCreature()));
		} while(name != null && !holder.isUniqueCreature(name) && !GameUtils.testPercentChance(PERCENT_CHANCE_DUPLICATE_SPAWNS) && attempts < MAX_ATTEMPTS);
		if (name != null && (PERCENT_CHANCE_DUPLICATE_SPAWNS != 0 || holder.isUniqueCreature(name))) {
			return name;
		}
		return null;

	}
	private String changeToLegendaryIfShould(final String oldCreature) {
		String name = oldCreature;
		if (!unFoundLegendaries.isEmpty() && GameUtils.testPercentChance(holder.getLegendaryChance())) {
			name = returnSameIfNull(name, findLegendaryCreaturen());		
		}
		return name;
	}
	private String modifyIfShould(final String oldCreature) {
		String name = oldCreature;
		if (GameUtils.testPercentChance(getPercentChancePopularityModifies())) {
			final int modifier = getPopularityModifier();
			if (modifier !=0) {
				final String modifiedCreature = getModifiedCreature(creatureRarity.get(name), modifier);
				name = returnSameIfNull(name, modifiedCreature);
			}
		}
		return name;
	}
	private String getModifiedCreature(final int oldCreatureRarity, final int modifier) {
		//the set of all keys strictly greater than the rarity 
		//note that iterator will still work if tailmap is empty
		final Set<Integer> tailMap = creatureRaritiesInOrder.tailMap(oldCreatureRarity, false).keySet();
		int j = 1;
		for (final Integer rare: tailMap) {
			if (j==modifier || j==tailMap.size()) { //move up from original rarity by modifier ranks in rarity
				final List<String> creatures = creatureRaritiesInOrder.get(rare);
				return creatures.get(ThreadLocalRandom.current().nextInt(creatures.size()));
			}
			j++;
		}
		return null;
	}
	private static String returnSameIfNull(final String original, final String newString) {
		if (newString == null)
			return original;
		return newString;
	}
	/**
	 * Called by lookForCreature(), will find the next creature taking into account rarity
	 * @return
	 */
	private String findNextCreature() {
		final long randomNum = ThreadLocalRandom.current().nextLong(0, RUNNING_TOTAL);
		//note that chance != rarity, they are inversely proportional
		Entry<Long, String> entry = creatureCumulativeChance.higherEntry(randomNum);
		if (entry == null)
			entry = creatureCumulativeChance.ceilingEntry(randomNum);
		if (entry == null)
			entry = creatureCumulativeChance.floorEntry(randomNum);
		if (entry == null)
			return null;
		return entry.getValue();
	}
	/**
	 * @return the found creature, remove it from the unFound list. Returns null if none were found
	 */
	private String findLegendaryCreaturen() {
		final int size = unFoundLegendaries.size();
		if (size == 0)
			return null;
		final int item = ThreadLocalRandom.current().nextInt(size); 
		int i = 0;
		String toFind = null;
		for(final String creatureString : unFoundLegendaries)
		{
			if (i == item)
				toFind = creatureString;
			i++;
		}
		unFoundLegendaries.remove(toFind);

		return toFind;
	}
	/** 
	 * @see model.WildCreatureGeneratorInterface#getPercentChancePopularityModifies()
	 */
	@Override
	public double getPercentChancePopularityModifies() {
		final double A = 5;
		final double B = 1.3;
		final double C = 1;
		final double D = 10;
		final double E = .25;
		return Math.max(MIN_PERCENT_CHANCE_POPULARITY_BOOSTS, Math.min(MAX_PERCENT_CHANCE_POPULARITY_BOOSTS, A*Math.log(Math.pow(holder.getPopularity(), B)+C)+D*Math.pow(holder.getPopularity(), E)));
	}
	/** 
	 * @see model.WildCreatureGeneratorInterface#getPopularityModifier()
	 */
	@Override
	public int getPopularityModifier() {
		final double B = 1000;
		final double R= .001;
		final double C = 1;
		return (int) Math.floor((MAX_POPULARITY_BOOST/(1+B*Math.pow(Math.E, -R*holder.getPopularity())))+C);
	}
	/** 
	 * @see model.WildCreatureGeneratorInterface#getPercentChanceCreatureFound()
	 */
	@Override
	public double getPercentChanceCreatureFound() {
		final double A = .05;
		final double B = 10;
		final double C = 100;
		final double D =3;
		final double E = 1;
		final double F = 100;
		if (holder.getNumCreaturesOnBoardAndWaiting() <= 2)
			return 100;
		final double answer = Math.max(MIN_PERCENT_CHANCE_CREATURE_FOUND, Math.min(MAX_PERCENT_CHANCE_CREATURE_FOUND, (holder.getPopularity()*A)+(holder.getGold()/B)+(C/(D*holder.getNumCreaturesOnBoardAndWaiting()+E))-F));
		return answer;
	}
	/** 
	 * @see model.WildCreatureGeneratorInterface#getLookForCreaturesPeriod()
	 */
	@Override
	public double getLookForCreaturesPeriod() {
		if (RAPID_SPAWN)
			return 1.666e-5;
		double periodCalc;
		if (holder.getPopularity() <= MAX_POP_BEFORE_CALCULATE_PERIOD)
			periodCalc =  PERIOD_BEFORE_CALCULATION;
		else if (holder.getPopularity() <= MAX_POP_BEFORE_PERIOD_CALCULATION_SHIFT) {
			periodCalc = getBasicLookForCreaturePeriod();
		}
		else if (holder.getPopularity() > POP_TO_GET_BONUS_PERIOD_AT) {
			periodCalc = BONUS_PERIOD;
		}
		else
			periodCalc =  getAdvancedLookForCreaturePeriod();
		return periodCalc + getPeriodDemerit();
		
	}
	/**
	 * Returns the amount to increase the period by due to an excess of creatures waiting
	 * @return the amount to increase the period by due to an excess of creatures waiting
	 */
	private double getPeriodDemerit() {
		if (holder.getNumCreaturesWaiting() == 1)
			return 0;
		else
			return holder.getNumCreaturesWaiting()*2;
	}
	/**
	 * moves relatively quickly from 4 to 3.5 
	 */
	private double getBasicLookForCreaturePeriod() {
		final double A = 4;
		final double B = 2000;
		return Math.max(0, Math.max(MIN_CREATURE_PERIOD, A - holder.getPopularity()/B));
	}
	/**
	 * moves slowly from 3.5 all the way down to MIN_CREATURE_PERIOD
	 * value of the form A-(pop/B)^C
	 */
	private double getAdvancedLookForCreaturePeriod() {
		final double A=  3.65;
		final double B = 5000; //"length" of near-constant values
		final double C = 1.1; //steepness of drop
		
		return Math.max(0, (Math.max(MIN_CREATURE_PERIOD, A-Math.pow(holder.getPopularity()/B, C))-holder.getPeriodDecreaseMod()));
	}
	/**
	 * @return Percent chance of a rarity out of 100 w.r.t to the other creaturess
	 */
	private static int getRelativeChanceRarity(final int rarity) {
		return 100-rarity;
	}


}
