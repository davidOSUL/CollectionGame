package loaders;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import attributes.attributegenerators.AttributeGenerator;
import attributes.attributegenerators.AttributeGenerators;
import thingFramework.Item;
import thingFramework.Pokemon;
import thingFramework.Thing;

class ThingLoader implements Loader {
	private final String[] pathToThings;
	private final ThingFactory factory;
	private final ExtraAttributeLoader extraAttributeLoader;
	private final boolean hasExtraAttributeLoader;
	private final Map<Thing, AttributeGenerator> genAttributeThings = new HashMap<Thing, AttributeGenerator>();
	private static final int NAME_LOC = 1;
	private static final int IMAGE_LOC = 2;
	private static final int ATTRIBUTE_LOC = 3;
	private static final String GEN_CODE = "RANDOMATTRIBUTES";
	private static final String POKE_SPRITE_LOC = "/sprites/pokemon/battlesprites/";
	private static final String ITEM_SPRITE_LOC = "/sprites/items/";
	private static final String POKE_SIGNIFY = "POKEMON";
	private static final String ITEM_SIGNIFY = "ITEM";
	ThingLoader(final ThingFactory factory, final String[] pathToThings) {
		this.pathToThings = pathToThings;
		this.factory = factory;
		extraAttributeLoader = null;
		hasExtraAttributeLoader = false;
	}
	ThingLoader(final ThingFactory factory, final String[] pathToThings, final ExtraAttributeLoader extraAttributeLoader) {
		this.pathToThings = pathToThings;
		this.factory = factory;
		this.extraAttributeLoader = extraAttributeLoader;
		hasExtraAttributeLoader = true;
	}
	@Override
	public void load() {
		for (final String path : pathToThings)
			loadPath(path);
		if (hasExtraAttributeLoader) {
			extraAttributeLoader.load();
		}
		genAttributeThings.forEach((t, ag) -> t.addGeneratedAttributes(ag));
	}
	/**
	 * <br> Assumes inputs of the form: </br> 
	 * <br> POKEMON Name, texture, attribute:val, attribute:val,...  </br> 
	 * <br> POKEMON Name, texture, attribute:val, attribute:val,... </br>  
	 * <br> ... </br> 
	 * <br> ITEM Name, texture, attribute:val, attribute:val,... </br> 
	 * <br> ITEM Name, texture, attribute:val, attribute:val,... </br> 
	 * <br> ... </br> 
	 * <br> Duplicates SHOULD NOT appear in list</br>
	 */
	private void loadPath(final String path) {
		try {
			for (final String[] values : CSVReader.readCSV(path)) {
				final String type = values[0];
				if (type.equals(POKE_SIGNIFY))
					loadPokemon(values);
				else if (type.equals(ITEM_SIGNIFY))
					loadItem(values);
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void loadPokemon(final String[] values) {
		final String name = values[NAME_LOC];
		final String texture = POKE_SPRITE_LOC + values[IMAGE_LOC];
		final Pokemon pm = new Pokemon(name, texture);
		loadAttributes(values, name, pm);
		factory.addNewPokemonTemplate(pm);

	}
	private void loadItem(final String[] values) {
		final String name = values[NAME_LOC];
		final String texture = ITEM_SPRITE_LOC + values[IMAGE_LOC];
		final Item i = new Item(name, texture);
		loadAttributes(values, name, i);
		factory.addNewItemTemplate(i);
	}

	private void loadAttributes(final String[] values, final String name, final Thing thing) {
		final Set<String> attributeNames = new HashSet<String>();
		for (int i = ATTRIBUTE_LOC; i < values.length; i++) {
			final String atr = values[i];
			if (atr.equals("")) { 
				continue;
			}
			final String[] nameValuePair = atr.split(":");
			if (attributeNames.contains(nameValuePair[0])) {
				throw new ThingLoadException("Duplicate Attribute: " + nameValuePair[0] + "for: " + name);
			}
			else {
				attributeNames.add(nameValuePair[0]);
			}
			if (nameValuePair[0].equals(GEN_CODE)) {//if we want to generate random attributes
				genAttributeThings.put(thing, AttributeGenerators.getGenerateFromRarity());
			}
			else if (nameValuePair.length == 2)  {//have a name and a value
				thing.addAttribute(nameValuePair[0], nameValuePair[1]);
			}
			else if (nameValuePair.length == 1) {//just a name 
				thing.addAttribute(nameValuePair[0]);
			}
			else {
				throw new Error("Wrong number of attribute info : " + Arrays.toString(nameValuePair) + "for: " + name);
			}
		}

	}

}
