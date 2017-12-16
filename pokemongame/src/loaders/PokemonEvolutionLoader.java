package loaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import thingFramework.Attribute;
import thingFramework.AttributeNotFoundException;

/**
 * Adds to each pokemon it's evolution if it has one, using data from evolution sheet
 * @author David O'Sullivan
 *
 */
public class PokemonEvolutionLoader {
	ThingLoader thingLoader;
	Path pathToEvolutions, pathToLevelsOfEvolve;
	public PokemonEvolutionLoader(ThingLoader tl, String pathToEvolutions, String pathToLevelsOfEvolve) {
		this.thingLoader = tl;
		this.pathToEvolutions = FileSystems.getDefault().getPath(pathToEvolutions);
		this.pathToLevelsOfEvolve = FileSystems.getDefault().getPath(pathToLevelsOfEvolve);
		load();
	}
	private void load() {
		try {
			List<String> lines = Files.readAllLines(pathToLevelsOfEvolve, StandardCharsets.UTF_8);
			for (String line: lines) {
				String[] values = line.split(",");
				loadLevel(values);
			}
			lines = Files.readAllLines(pathToEvolutions, StandardCharsets.UTF_8);
			for (String line: lines) {
				String[] values = line.split(",");
				loadEvolution(values);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void loadLevel(String[] values) {
		String name = values[0];
		String level = values[1];
		if (thingLoader.hasPokemon(name)) {
			thingLoader.getPokemon(name).addAttributes(Attribute.generateAttributes(new String[] {"level of evolution", "has evolution"}, new String[] {level, "true"}));
		}
	}
	private void loadEvolution(String[] values) {
		String firstPokemon = values[0];
		String secondPokemon = values[1];
		String thirdPokemon = values[2];
		boolean hasSecond = !secondPokemon.equals("");
		boolean hasThird = !thirdPokemon.equals("");
		String[] secondAsArray = {secondPokemon};
		try {
			if (hasSecond && thingLoader.getPokemon(firstPokemon).containsAttribute("has evolution") && (Boolean) thingLoader.getPokemon(firstPokemon).getAttributeVal("has evolution")) {
				if (secondPokemon.startsWith("\"")) { //if it has multiple second evolutions it will be of form \"Aaa\r\nBbb\r\nCcc\r\n...\" we want to convert to [Aaa, Bbb, Ccc]
					secondPokemon = secondPokemon.replace("\n", "").replace("\r", "").replace("\"", "");
					secondAsArray = secondPokemon.split("(?=\\p{Lu})"); //split by uppercase letters
					thingLoader.getPokemon(firstPokemon).addAttribute(Attribute.generateAttribute("next evolutions", Arrays.toString(secondAsArray)));
				} else {
					thingLoader.getPokemon(firstPokemon).addAttribute(Attribute.generateAttribute("next evolutions", secondPokemon));
				}
			}
			if (hasThird) {
				int i =0;
				for (String secPoke : secondAsArray) {
					if (thingLoader.getPokemon(secPoke).containsAttribute("has evolution") && (Boolean) thingLoader.getPokemon(secPoke).getAttributeVal("has evolution"))
						if (thirdPokemon.startsWith("\"")) {
							thirdPokemon = thirdPokemon.replace("\n", "").replace("\r", "").replace("\"", "");
							String[] thirdAsArray = thirdPokemon.split("(?=\\p{Lu})");
							if (secondAsArray.length == 1)
								thingLoader.getPokemon(secPoke).addAttribute(Attribute.generateAttribute("next evolutions", Arrays.toString(thirdAsArray)));
							else if(secondAsArray.length==thirdAsArray.length) 
								thingLoader.getPokemon(secPoke).addAttribute(Attribute.generateAttribute("next evolutions", thirdAsArray[i++]));
							else
								throw new Error("SPECIAL CASE NOT ACCOUNTED FOR");
						} else {
							thingLoader.getPokemon(secPoke).addAttribute(Attribute.generateAttribute("next evolutions", thirdPokemon));
						}
				}
			}
				
		} catch (AttributeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
