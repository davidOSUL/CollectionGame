package game;

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

import attributes.ParseType;
import gameutils.GameUtils;
import loaders.ThingFactory;
import loaders.ThingType;

/**
 * Generates new random Creatures. The rarity of the creatures generated is based on multiple factors including the 
 * popularity of the board. 
 * @author David O'Sullivan
 *
 */
class WildCreatureGenerator implements Serializable{
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
	private static final double MIN_PERCENT_CHANCE_CREATURE_FOUND = 20;
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

	private static final Set<String> legendaryCreatures = ThingFactory.getInstance().getThingsWithAttributeVal("legendary", true, ThingType.CREATURE, ParseType.BOOLEAN);
	private static final Set<String> nonLegendaryCreatures = ThingFactory.getInstance().getThingsWithAttributeVal("legendary", false, ThingType.CREATURE, ParseType.BOOLEAN);

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
	private final Map<String, Integer> pokeRarity;
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
	private final Board holder;
	/**
	 * Creates a new WildCreatureGenerator
	 * @param b the board that has this WildCreatureGenerator
	 */
	public WildCreatureGenerator(final Board b) { 
		this.holder = b;
		pokeRarity =
				ThingFactory.getInstance().<Integer>mapFromSetToAttributeValue("rarity", ThingType.CREATURE, ParseType.INTEGER)
				.entrySet().stream().filter(p -> nonLegendaryCreatures.contains(p.getKey()))
				.collect(Collectors.toMap(p-> p.getKey(), p -> p.getValue()));
		RUNNING_TOTAL = calcRunningTotal();
	}
	private long calcRunningTotal() {
		long runningTotal = 0; //running total

		for (final Map.Entry<String, Integer> entry: pokeRarity.entrySet()) {
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
	 * @param automaticSpawn if true will automatically generate a creature regardless of percent chance
	 *@return the name of the generated creature, null if none are found
	 */
	@SuppressWarnings("unused") //elements could be marked as such if PERCENT_CHANCE_DUPLIATE_SPAWNS != 0
	String lookForCreature(final boolean automaticSpawn) {
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
				final String modifiedCreature = getModifiedCreature(pokeRarity.get(name), modifier);
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
		for(final String p : unFoundLegendaries)
		{
			if (i == item)
				toFind = p;
			i++;
		}
		unFoundLegendaries.remove(toFind);

		return toFind;
	}
	/**
	 * @return the percent chance that the randomNum generated by lookForCreature will be modified 
	 *by a value. This value increases as popularity increases. 
	 *In particular this will return a value of the form:
	 *<br> Aln(pop^B+C)+Dpop^E
	 */
	private double getPercentChancePopularityModifies() {
		final double A = 5;
		final double B = 1.3;
		final double C = 1;
		final double D = 10;
		final double E = .25;
		return Math.max(MIN_PERCENT_CHANCE_POPULARITY_BOOSTS, Math.min(MAX_PERCENT_CHANCE_POPULARITY_BOOSTS, A*Math.log(Math.pow(holder.getPopularity(), B)+C)+D*Math.pow(holder.getPopularity(), E)));
	}
	/**
	 * @return The amount by which we will move up in rarity ranking for a creature
	 * Will be in the form of logistic growth: MAX_MODIFIER/1+Be^-r*pop + C
	 * C is implicitly minimum popularity boost
	 */
	private int getPopularityModifier() {
		final double B = 1000;
		final double R= .15;
		final double C = 1;
		return (int) Math.floor((MAX_POPULARITY_BOOST/(1+B*Math.pow(Math.E, -R*holder.getPopularity())))+C);
	}
	/**
	 * @return Percent chance that a creature is found. 
	 * Will be value of the form pop*A+Gold/B+C/D*pokeMapSize+E, D!=0
	 * range modified to [MIN_PERCENT_CHANCE, MAX_PERCENT_CHANCE]
	 */
	double getPercentChanceCreatureFound() {
		final double A = 5;
		final double B = 50;
		final double C = 100;
		final double D =3;
		final double E = 1;
		if (holder.getNumCreatures() <= 2)
			return 100;
		final double answer = Math.max(MIN_PERCENT_CHANCE_CREATURE_FOUND, Math.min(MAX_PERCENT_CHANCE_CREATURE_FOUND, (holder.getPopularity()*A)+(holder.getGold()/B)+(C/(D*holder.getNumCreatures()+E))));
		return answer;
	}
	/**
	 * @return Percent chance of a rarity out of 100 w.r.t to the other pokes
	 */
	private static int getRelativeChanceRarity(final int rarity) {
		return 100-rarity;
	}


}
