package loaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import thingFramework.ExperienceGroup;

/**
 * Used as a way to lookup pokemon XP
 * @author David O'Sullivan
 *
 */
public final class RequiredXPLookup {
	private final Path pathToXp = FileSystems.getDefault().getPath("resources/InputFiles/XPLookup.csv");
	/**
	 * Map From The Level to an array of integers containing both the total XP you'd have to be to be at that level as well as the amount of additional XP needed to get to the next level
	 */
	private final Map<Integer, Integer[]> levelToXP = new HashMap<Integer, Integer[]>();
	/**
	 * Map from the ExperienceGroup (as an ordinal) to a Map mapping XP to level
	 */
	private final Map<Integer, TreeMap<Integer, Integer>> xpToLevels = new HashMap<Integer, TreeMap<Integer, Integer>>();
	public static final RequiredXPLookup INSTANCE = new RequiredXPLookup();
	private static final int LAST_LEVEL = 100;
	/**
	 * The location in the csv of the level itself
	 */
	private static final int LEVEL_LOC = 6;
	public static RequiredXPLookup getInstance() {
		return INSTANCE;
	}
	/**
	 *
	 * @return the minimum amount of xp to be considered a member of the given level
	 */
	public int getMinXPAtLevel(final ExperienceGroup eg, final int level) {
		return levelToXP.get(level)[eg.ordinal()];
	}
	/**
	 * 
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
	 *
	 * @return the minimum XP required to be at the pokemon's current level, which is calculated from value currentTotalXP
	 */
	public  int getMinXPAtLevelGivenXP(final ExperienceGroup eg, final int currentTotalXP) {
		return getMinXPAtLevel(eg, getLevelFromCurrentXP(eg, currentTotalXP));
	}
	/**

	 * @return the amount of XP required to advance to the next level given the pokemon's current TOTAL XP
	 */
	public  int getAmountOfXPToNextLevel(final ExperienceGroup eg, final int currentTotalXP) {
		return getAmountOfXPToNextLevel(eg, getLevelFromCurrentXP(eg, currentTotalXP), currentTotalXP);
	}

	/**

	 * @return The current level of the pokemon given it's current TOTAL xp
	 */
	public  int getLevelFromCurrentXP(final ExperienceGroup eg, final int currentTotalXP) {
		if (xpToLevels.get(eg.ordinal()).containsKey(currentTotalXP))
			return xpToLevels.get(eg.ordinal()).get(currentTotalXP);
		else return xpToLevels.get(eg.ordinal()).lowerEntry(currentTotalXP).getValue();
	}
	/**

	 * @return the amount of xp required to advance to the level after the given level assuming the current XP 
	 * is at the bare minimum for the given level
	 */
	public  int getAmountOfXPToNextLevelFromBase(final ExperienceGroup eg, final int level) {
		if (level == 100)
			return 0;
		else return levelToXP.get(level)[eg.ordinal()+7];
	}
	/**
	 * @return aka the experience level at level 100 (the maximum amount of experience a pokemon can have)
	 */
	public  int getLevel100XP(final ExperienceGroup eg) {
		return getMinXPAtLevel(eg, 100);
	}
	private RequiredXPLookup() {
		List<String> lines = null;
		try {
			lines = Files.readAllLines(pathToXp, StandardCharsets.UTF_8);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int j =0;
		for (final String line: lines) {
			final String[] values = line.split(","); //the current line
			final Integer level = Integer.parseInt(values[LEVEL_LOC]); //the level associated with that xp
			final Integer[] xpVals = new Integer[values.length];
			for (int i =0; i < values.length; i++) {
				if (i!=LEVEL_LOC && j!=LAST_LEVEL-1)
					xpVals[i]= Integer.parseInt(values[i]);
				if (j == LAST_LEVEL-1 && i <LEVEL_LOC)
					xpVals[i]= Integer.parseInt(values[i]); //the last line doesn't contain "next xp" as once you are at the last level there is no where else to go

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
	}
}
