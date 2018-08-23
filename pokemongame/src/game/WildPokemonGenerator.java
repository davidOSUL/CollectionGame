package game;

import static gameutils.Constants.RAPID_SPAWN;

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

class WildPokemonGenerator {
	/**
	 * The chance that a pokemon with a name of a pokemon already on the board will spawn
	 */
	private static final double PERCENT_CHANCE_DUPLICATE_SPAWNS = RAPID_SPAWN ? 100 : 5;
	/**
	 * The minimum percent chance that when checking for pokemon, one is found
	 */
	private static final double MIN_PERCENT_CHANCE_POKEMON_FOUND = 20;
	/**
	 * The maximum percent chance that when checking for pokemon, one is found
	 */
	private static final double MAX_PERCENT_CHANCE_POKEMON_FOUND = 90;

	/**
	 * The minimum percent chance that the rarity of a found pokemon will increase from a popularity boost
	 */
	private static final double MIN_PERCENT_CHANCE_POPULARITY_BOOSTS = 0;

	/**
	 * The maximum delta in rarity value from the calculated rarity. (for example if the rarities are:
	 * 1,2,3,4,5), and the calculated rarity is 2, with a boost of 2, you will get a pokemon with rarity 4
	 */
	private static final double MAX_POPULARITY_BOOST = 10;
	/**
	 * The maximum percent chance that the rarity of a found pokemon will increase from a popularity boost
	 */
	private static final double MAX_PERCENT_CHANCE_POPULARITY_BOOSTS = 90;

	/**
	 * Maximum number of tries to generate a pokemon
	 */
	private static final int MAX_ATTEMPTS = 50;

	private static final Set<String> legendaryPokemon = ThingFactory.sharedInstance().getThingsWithAttributeVal("legendary", true, ThingType.POKEMON, ParseType.BOOLEAN);
	private static final Set<String> nonLegendaryPokemon = ThingFactory.sharedInstance().getThingsWithAttributeVal("legendary", false, ThingType.POKEMON, ParseType.BOOLEAN);

	/**
	 * A map from the sum of the chance of a pokemon being found (out of RUNNING_TOTAL) and all chances
	 * of pokemon loaded in before it, to the name of that pokemon. This will be in order, sorted by the sum.
	 */
	private final TreeMap<Long, String> pokemonCumulativeChance = new TreeMap<Long, String>();
	/**
	 * A map from the RARITY (NOT CHANCE) to a list of all pokemon with that rarity, in order of rarity
	 */
	private  final TreeMap<Integer, List<String>> pokemonRaritiesInOrder = new TreeMap<Integer, List<String>>();
	/**
	 * Map from nonlegendary pokemon names to their RARITY (NOT CHANCE) value
	 */
	private final Map<String, Integer> pokeRarity;
	/**
	 * Legendaries that haven't been generated yet
	 */
	private final Set<String> unFoundLegendaries = new HashSet<String>(legendaryPokemon);
	/**
	 * This is the value of the total chance rarities of every pokemon. In other words,
	 * it is the denominator for determining the percent chance that a certain pokemon
	 * will show up (that is the probability will be: getRelativeChanceRarity(pokemon.rarity)/RUNNING_TOTAL)
	 */
	private final long RUNNING_TOTAL;
	private final Board holder;
	public WildPokemonGenerator(final Board b) { 
		this.holder = b;
		pokeRarity =
				ThingFactory.sharedInstance().<Integer>mapFromSetToAttributeValue("rarity", ThingType.POKEMON, ParseType.INTEGER)
				.entrySet().stream().filter(p -> nonLegendaryPokemon.contains(p.getKey()))
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
			pokemonCumulativeChance.put(runningTotal, name);
			if (!pokemonRaritiesInOrder.containsKey(rarity)) {
				final List<String> list = new ArrayList<String>();
				list.add(name);
				pokemonRaritiesInOrder.put(rarity, list);
			}
			else {
				pokemonRaritiesInOrder.get(rarity).add(name);
			}
		}
		return runningTotal;
	}
	/**
	 * @param automaticSpawn if true will automatically generate a pokemon regardless of percent chance
	 *@return the name of the generated pokemon, null if none are found
	 */
	String lookForPokemon(final boolean automaticSpawn) {
		//first check if a pokemon is even found
		int attempts = 0;
		if (!automaticSpawn && !GameUtils.testPercentChance(getPercentChancePokemonFound()))
			return null;
		String name = null;
		do {
			attempts++;
			name = changeToLegendaryIfShould(modifyIfShould(findNextPokemon()));
		} while(name != null && !holder.isUniquePokemon(name) && !GameUtils.testPercentChance(PERCENT_CHANCE_DUPLICATE_SPAWNS) && attempts < MAX_ATTEMPTS);
		if (name != null && (PERCENT_CHANCE_DUPLICATE_SPAWNS != 0 || holder.isUniquePokemon(name))) {
			return name;
		}
		return null;

	}
	private String changeToLegendaryIfShould(final String oldPokemon) {
		String name = oldPokemon;
		if (!unFoundLegendaries.isEmpty() && GameUtils.testPercentChance(holder.getLegendaryChance())) {
			name = returnSameIfNull(name, findLegendaryPokemon());		
		}
		return name;
	}
	private String modifyIfShould(final String oldPokemon) {
		String name = oldPokemon;
		if (GameUtils.testPercentChance(getPercentChancePopularityModifies())) {
			final int modifier = getPopularityModifier();
			if (modifier !=0) {
				final String modifiedPokemon = getModifiedPokemon(pokeRarity.get(name), modifier);
				name = returnSameIfNull(name, modifiedPokemon);
			}
		}
		return name;
	}
	private String getModifiedPokemon(final int oldPokemonRarity, final int modifier) {
		//the set of all keys strictly greater than the rarity 
		//note that iterator will still work if tailmap is empty
		final Set<Integer> tailMap = pokemonRaritiesInOrder.tailMap(oldPokemonRarity, false).keySet();
		int j = 1;
		for (final Integer rare: tailMap) {
			if (j==modifier || j==tailMap.size()) { //move up from original rarity by modifier ranks in rarity
				final List<String> pokemon = pokemonRaritiesInOrder.get(rare);
				return pokemon.get(ThreadLocalRandom.current().nextInt(pokemon.size()));
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
	 * Called by lookForPokemon(), will find the next pokemon taking into account rarity
	 * @return
	 */
	private String findNextPokemon() {
		final long randomNum = ThreadLocalRandom.current().nextLong(0, RUNNING_TOTAL);
		//note that chance != rarity, they are inversely proportional
		Entry<Long, String> entry = pokemonCumulativeChance.higherEntry(randomNum);
		if (entry == null)
			entry = pokemonCumulativeChance.ceilingEntry(randomNum);
		if (entry == null)
			entry = pokemonCumulativeChance.floorEntry(randomNum);
		if (entry == null)
			return null;
		return entry.getValue();
	}
	/**
	 * @return the found pokemon, remove it from the unFound list. Returns null if none were found
	 */
	private String findLegendaryPokemon() {
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
	 * @return the percent chance that the randomNum generated by lookForPokemon will be modified 
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
	 * @return The amount by which we will move up in rarity ranking for a pokemon
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
	 * @return Percent chance that a pokemon is found. 
	 * Will be value of the form pop*A+Gold/B+C/D*pokeMapSize+E, D!=0
	 * range modified to [MIN_PERCENT_CHANCE, MAX_PERCENT_CHANCE]
	 */
	double getPercentChancePokemonFound() {
		final double A = 5;
		final double B = 50;
		final double C = 100;
		final double D =3;
		final double E = 1;
		if (holder.getNumPokemon() <= 2)
			return 100;
		final double answer = Math.max(MIN_PERCENT_CHANCE_POKEMON_FOUND, Math.min(MAX_PERCENT_CHANCE_POKEMON_FOUND, (holder.getPopularity()*A)+(holder.getGold()/B)+(C/(D*holder.getNumPokemon()+E))));
		return answer;
	}
	/**
	 * @return Percent chance of a rarity out of 100 w.r.t to the other pokes
	 */
	private static int getRelativeChanceRarity(final int rarity) {
		return 100-rarity;
	}


}
