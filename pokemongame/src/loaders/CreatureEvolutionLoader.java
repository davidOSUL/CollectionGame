package loaders;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import attributes.ParseType;
import thingFramework.Creature;
import thingFramework.Thing;

/**
 * Loads in all the evolution's of creatures
 * @author David O'Sullivan
 *
 */
public class CreatureEvolutionLoader implements Loader {
	private final String pathToEvolutions, pathToLevelsOfEvolve;
	/**
	 * To avoid duplicate elements
	 */
	private final Set<String> namesLoaded = new HashSet<String>();
	private final ThingMap thingMap;
	private static final int MAX_NUMBER_EVOLUTIONS = 3;
	/**
	 * Creates a new CreatureEvolutionLoader
	 * @param pathToEvolutions the path to the CSV with the evolutions
	 * @param pathToLevelsOfEvolve the path to the CSV with the levels that creatures evolve at
	 * @param thingMap the map of Things to add attributes to
	 */
	public CreatureEvolutionLoader(final String pathToEvolutions, final String pathToLevelsOfEvolve, final ThingMap thingMap) {
		this.pathToEvolutions = pathToEvolutions;
		this.pathToLevelsOfEvolve = pathToLevelsOfEvolve;
		this.thingMap = thingMap;
	}
	/** 
	 * @see loaders.Loader#load()
	 */
	@Override
	public void load() {
		try {
			for (final String[] values: CSVReader.readCSV(pathToLevelsOfEvolve)) {
				loadLevel(values);
			}
			for (final String[] values : CSVReader.readCSV(pathToEvolutions, -1)) {
				loadEvolution(values);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	private void loadLevel(final String[] values) {
		final String name = values[0];
		final String level = values[1];
		if (thingMap.viewMap().containsKey(name)) {
			if (!namesLoaded.contains(name)) {
				final Thing thing = thingMap.viewMap().get(name);
				thing.addAttribute("level of evolution", level);
				thing.addAttribute("has evolution", true, ParseType.BOOLEAN);
				namesLoaded.add(name);
			}
		}
	}
	private boolean checkIfHasEvolution(final Thing t) {
		return t.containsAttribute("has evolution") && t.getAttributeValue("has evolution", ParseType.BOOLEAN);
	}
	private String[] splitUppercase(final String input) {
		return input.split("(?=\\p{Lu})");
	}
	private boolean checkAllValidThings(final String[] names) {
		boolean valid = true;
		for(final String s : names) {
			valid = valid && thingMap.hasThing(s);
		}
		return valid;
	}
	private void loadEvolution(final String[] values) {
		final String[] creatureNames = new String[MAX_NUMBER_EVOLUTIONS];
		final boolean[] hasEvolution = new boolean[MAX_NUMBER_EVOLUTIONS-1];
		for (int i =0 ; i < MAX_NUMBER_EVOLUTIONS; i++) {
			creatureNames[i] = values[i];
			if (i < hasEvolution.length)
				hasEvolution[i] = !values[i+1].equals("");
		}
		if (!thingMap.hasThing(creatureNames[0]))
			return;
		String[] priorEvolutions = {creatureNames[0]};
		for (int i = 1; i < MAX_NUMBER_EVOLUTIONS && hasEvolution[i-1]; i++) { 
			final String currCreature = creatureNames[i];
			String[] newEvolutions = {currCreature};
			for (int j = 0; j < priorEvolutions.length; j++) {
				final String priorEvolve = priorEvolutions[j];
				final Creature creatureWithEvolve = thingMap.getCreature(priorEvolve);
				if (hasEvolution[i-1] && checkIfHasEvolution(creatureWithEvolve)) {
					if (currCreature.startsWith("\"")) { //if it has multiple evolutions it will be of form \"Aaa\r\nBbb\r\nCcc\r\n...\" we want to convert to [Aaa, Bbb, Ccc]
						newEvolutions = doIfMultipleEvolutions(newEvolutions, priorEvolutions, currCreature, creatureWithEvolve, j);
					} else {
						creatureWithEvolve.addAttribute("next evolutions", currCreature);
					}
				}
			}
			priorEvolutions = newEvolutions;
		}
		
	}
	private String[] doIfMultipleEvolutions(String[] newEvolutions, final String[] priorEvolutions, final String currCreature, final Creature creatureWithEvolve, final int j) {
		newEvolutions = splitUppercase(currCreature.replace("\n", "").replace("\r", "").replace("\"", ""));
		if (!checkAllValidThings(newEvolutions)) {
			throw new ThingLoadException("Invalid Creature in prior Evolutions for " + newEvolutions);
		}
		//accounts for two cases, one Creature with multiple possible evolutions, or multiple with multiple possible (one for each)
		if (priorEvolutions.length == 1)
			creatureWithEvolve.addAttribute("next evolutions", Arrays.toString(newEvolutions));
		else if (priorEvolutions.length == newEvolutions.length)
			creatureWithEvolve.addAttribute("next evolutions", newEvolutions[j]);
		else
			throw new ThingLoadException("Unaccounted for number of evolutions listed for " + currCreature);
		return newEvolutions;
	}
}
