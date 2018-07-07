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
	private static final Path pathToXp = FileSystems.getDefault().getPath("resources/InputFiles/XPLookup.csv");
	/**
	 * Map From The Level to an array of integers containing both the total XP you'd have to be to be at that level as well as the amount of additional XP needed to get to the next level
	 */
	private static Map<Integer, Integer[]> levelToXP = new HashMap<Integer, Integer[]>();
	/**
	 * Map from the ExperienceGroup (as an ordinal) to a Map mapping XP to level
	 */
	private static Map<Integer, TreeMap<Integer, Integer>> xpToLevels = new HashMap<Integer, TreeMap<Integer, Integer>>();
	static {
		try {
			List<String> lines = Files.readAllLines(pathToXp, StandardCharsets.UTF_8);
			int j =0;
			for (String line: lines) {
				String[] values = line.split(","); //the current line
				Integer level = Integer.parseInt(values[6]); //the level associated with that xp
				Integer[] xpVals = new Integer[values.length];
				for (int i =0; i < values.length; i++) {
					if (i!=6 && j!=99)
					xpVals[i]= Integer.parseInt(values[i]);
					if (j == 99 && i <6)
					xpVals[i]= Integer.parseInt(values[i]); //the last line doesn't contain "next xp" as once you are at the last level there is no where else to go

				}
				levelToXP.put(level, xpVals);
				for (ExperienceGroup eg : ExperienceGroup.values()) {
					int XP = Integer.parseInt(values[eg.ordinal()]);
					if (!xpToLevels.containsKey(eg.ordinal())) {
						TreeMap<Integer, Integer> newMap = new TreeMap<Integer, Integer>();
						newMap.put(XP, level);
						xpToLevels.put(eg.ordinal(), newMap);
					}
					else {
						xpToLevels.get(eg.ordinal()).put(XP, level);
					}
				}
				j++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 *
	 * @return the minimum amount of xp to be considered a member of the given level
	 */
	public static int getMinXPAtLevel(ExperienceGroup eg, int level) {
		return levelToXP.get(level)[eg.ordinal()];
	}
	/**
	 * 
	 * @return the amount of XP required to advance to the next level, given the current level and the current TOTAL Xp
	 */
	public static int getAmountOfXPToNextLevel(ExperienceGroup eg, int level, int currentTotalXP) {
		int minXP = getMinXPAtLevel(eg, level);
		if (level == 100)
			return 0;
		int minXpNextLevel = getMinXPAtLevel(eg, level+1);
		if (currentTotalXP < minXP || currentTotalXP >=minXpNextLevel)
			throw new Error("LEVEL AND XP DON'T MATCH");
		else
			return minXpNextLevel-currentTotalXP;
		
	}
	/**
	 *
	 * @return the minimum XP required to be at the pokemon's current level, which is calculated from value currentTotalXP
	 */
	public static int getMinXPAtLevelGivenXP(ExperienceGroup eg, int currentTotalXP) {
		return getMinXPAtLevel(eg, getLevelFromCurrentXP(eg, currentTotalXP));
	}
	/**
	
	 * @return the amount of XP required to advance to the next level given the pokemon's current TOTAL XP
	 */
	public static int getAmountOfXPToNextLevel(ExperienceGroup eg, int currentTotalXP) {
		return getAmountOfXPToNextLevel(eg, getLevelFromCurrentXP(eg, currentTotalXP), currentTotalXP);
	}
	
	/**
	
	 * @return The current level of the pokemon given it's current TOTAL xp
	 */
	public static int getLevelFromCurrentXP(ExperienceGroup eg, int currentTotalXP) {
		if (xpToLevels.get(eg.ordinal()).containsKey(currentTotalXP))
			return xpToLevels.get(eg.ordinal()).get(currentTotalXP);
		else return xpToLevels.get(eg.ordinal()).lowerEntry(currentTotalXP).getValue();
	}
	/**

	 * @return the amount of xp required to advance to the level after the given level assuming the current XP 
	 * is at the bare minimum for the given level
	 */
	public static int getAmountOfXPToNextLevelFromBase(ExperienceGroup eg, int level) {
		if (level == 100)
			return 0;
		else return levelToXP.get(level)[eg.ordinal()+7];
	}
	/**
	 * @return aka the experience level at level 100 (the maximum amount of experience a pokemon can have)
	 */
	public static int getLevel100XP(ExperienceGroup eg) {
		return getMinXPAtLevel(eg, 100);
	}
	private RequiredXPLookup() {
		
	}
}
