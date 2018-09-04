package loaders;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import thingFramework.ExperienceGroup;

/**
 * Used as a way to lookup Creature XP
 * @author David O'Sullivan
 *
 */
public final class RequiredXPLookup {
	private final String pathToXp = "resources/InputFiles/XPLookup.csv";
	/**
	 * Map From The Level to an array of integers containing both the total XP you'd have to be to be at that level as well as the amount of additional XP needed to get to the next level
	 */
	private final Map<Integer, Integer[]> levelToXP = new HashMap<Integer, Integer[]>();
	/**
	 * Map from the ExperienceGroup (as an ordinal) to a Map mapping XP to level
	 */
	private final Map<Integer, TreeMap<Integer, Integer>> xpToLevels = new HashMap<Integer, TreeMap<Integer, Integer>>();
	private static final RequiredXPLookup INSTANCE = new RequiredXPLookup();
	private static final int LAST_LEVEL = 100;
	/**
	 * The location in the csv of the level itself
	 */
	private static final int LEVEL_LOC = 6;
	/**
	 * @return the RequiredXPLookup instance 
	 */
	public static RequiredXPLookup getInstance() {
		return INSTANCE;
	}
	/**
	 * Return the minimum amount of xp to be considered a member of the given level
	 * @param eg the ExperienceGroup
	 * @param level the level 
	 * @return the minimum amount of xp to be considered a member of the given level
	 */
	public int getMinXPAtLevel(final ExperienceGroup eg, final int level) {
		return levelToXP.get(level)[eg.ordinal()];
	}
	/**
	 * Returns the amount of XP required to advance to the next level, given the current level and the current TOTAL Xp
	 * @param eg the ExperienceGroup
	 * @param level the level to advance to 
	 * @param currentTotalXP the current total XP
	 * @return the amount of XP required to advance to the next level, given the current level and the current TOTAL Xp
	 */
	public  int getAmountOfXPToNextLevel(final ExperienceGroup eg, final int level, final int currentTotalXP) {
		final int minXP = getMinXPAtLevel(eg, level);
		if (level == 100)
			return 0;
		final int minXpNextLevel = getMinXPAtLevel(eg, level+1);
		if (currentTotalXP < minXP || currentTotalXP >=minXpNextLevel)
			throw new Error("LEVEL AND XP DON'T MATCH");
		else
			return minXpNextLevel-currentTotalXP;

	}
	/**
	 * Returns the minimum XP required to be at the Creature's current level, which is calculated from value currentTotalXP
	 * @param eg the ExperienceGroup
	 * @param currentTotalXP the current total experience
	 * @return the minimum XP required to be at the Creature's current level, which is calculated from value currentTotalXP
	 */
	public  int getMinXPAtLevelGivenXP(final ExperienceGroup eg, final int currentTotalXP) {
		return getMinXPAtLevel(eg, getLevelFromCurrentXP(eg, currentTotalXP));
	}
	/**
	 * Returns the amount of XP required to advance to the next level given the Creature's current TOTAL XP
	 * @param eg the Experience Group
	 * @param currentTotalXP the current total experience
	 * @return the amount of XP required to advance to the next level given the Creature's current TOTAL XP
	 */
	public  int getAmountOfXPToNextLevel(final ExperienceGroup eg, final int currentTotalXP) {
		return getAmountOfXPToNextLevel(eg, getLevelFromCurrentXP(eg, currentTotalXP), currentTotalXP);
	}

	/**
	 * Returns The current level of the Creature given it's current TOTAL xp
	 * @param eg the ExperienceGroup
	 * @param currentTotalXP the current total xp of the creature
	 * @return The current level of the Creature given it's current TOTAL xp
	 */
	public  int getLevelFromCurrentXP(final ExperienceGroup eg, final int currentTotalXP) {
		if (xpToLevels.get(eg.ordinal()).containsKey(currentTotalXP))
			return xpToLevels.get(eg.ordinal()).get(currentTotalXP);
		else return xpToLevels.get(eg.ordinal()).lowerEntry(currentTotalXP).getValue();
	}
	/**
	 * Returns the amount of xp required to advance to the level after the given level assuming the current XP 
	 * is at the bare minimum for the given level
	 * @param eg the ExperienceGroup
	 * @param level the current level
	 * @return the amount of xp required to advance to the level after the given level assuming the current XP 
	 * is at the bare minimum for the given level
	 */
	public  int getAmountOfXPToNextLevelFromBase(final ExperienceGroup eg, final int level) {
		if (level == 100)
			return 0;
		else return levelToXP.get(level)[eg.ordinal()+7];
	}
	/**
	 * Returns the experience level at level 100 (the maximum amount of experience a Creature can have)
	 * @param eg the ExperienceGroup
	 * @return the experience level at level 100 (the maximum amount of experience a Creature can have)
	 */
	public  int getLevel100XP(final ExperienceGroup eg) {
		return getMinXPAtLevel(eg, 100);
	}
	private RequiredXPLookup() {
		int j =0;
		try {
			for (final String[] values: CSVReader.readCSV(pathToXp)) {
				final Integer level = Integer.parseInt(values[LEVEL_LOC]); //the level associated with that xp
				final Integer[] xpVals = new Integer[values.length];
				for (int i =0; i < values.length; i++) {
					if ((i!=LEVEL_LOC && j!=LAST_LEVEL-1) || (j == LAST_LEVEL-1 && i <LEVEL_LOC))
						xpVals[i]= Integer.parseInt(values[i]);
				}
				levelToXP.put(level, xpVals);
				for (final ExperienceGroup eg : ExperienceGroup.values()) {
					final int XP = Integer.parseInt(values[eg.ordinal()]);
					if (!xpToLevels.containsKey(eg.ordinal())) {
						final TreeMap<Integer, Integer> newMap = new TreeMap<Integer, Integer>();
						newMap.put(XP, level);
						xpToLevels.put(eg.ordinal(), newMap);
					}
					else {
						xpToLevels.get(eg.ordinal()).put(XP, level);
					}
				}
				j++;
			}
		} catch (final NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
