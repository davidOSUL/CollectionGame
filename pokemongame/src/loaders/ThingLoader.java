package loaders;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import attributes.attributegenerators.AttributeGenerator;
import attributes.attributegenerators.AttributeGenerators;
import thingFramework.Creature;
import thingFramework.Item;
import thingFramework.Thing;

/**
 * Creates all the Thing Templates for the ThingFactory
 * @author David O'Sullivan
 *
 */
class ThingLoader implements Loader {
	private final String[] pathToThings;
	private final ThingFactory factory;
	private final ExtraAttributeLoader extraAttributeLoader;
	private final boolean hasExtraAttributeLoader;
	private final Map<Thing, AttributeGenerator> genAttributeThings = new HashMap<Thing, AttributeGenerator>();
	private static final int NAME_LOC = 1;
	private static final int IMAGE_LOC = 2;
	private static final int ATTRIBUTE_LOC = 3;
	private static final String GEN_CODE = "GEN_ATTRIBUTES";
	private static final String CREATURE_SPRITE_LOC = "/sprites/creatures/battlesprites/";
	private static final String ITEM_SPRITE_LOC = "/sprites/items/";
	private static final String CREATURE_SIGNIFY = "CREATURE";
	private static final String ITEM_SIGNIFY = "ITEM";
	/**
	 * Creates a new ThingLoader
	 * @param factory the factory to generate templates for
	 * @param pathToThings the paths to the Things to generate
	 */
	ThingLoader(final ThingFactory factory, final String[] pathToThings) {
		this.pathToThings = pathToThings;
		this.factory = factory;
		extraAttributeLoader = null;
		hasExtraAttributeLoader = false;
	}
	/**
	 * Creates a new ThingLoader
	 * @param factory the factory to generate templates for
	 * @param pathToThings the paths to Things to generate
	 * @param extraAttributeLoader the ExtraAttributeLoader to call once things are loaded
	 */
	ThingLoader(final ThingFactory factory, final String[] pathToThings, final ExtraAttributeLoader extraAttributeLoader) {
		this.pathToThings = pathToThings;
		this.factory = factory;
		this.extraAttributeLoader = extraAttributeLoader; //TODO: Construct own extraAttributeLoader rather than have it passed in
		hasExtraAttributeLoader = true;
	}
	/** 
	 * @see loaders.Loader#load()
	 */
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
	 * <br> CREATURE Name, texture, attribute:val, attribute:val,...  </br> 
	 * <br> CREATURE Name, texture, attribute:val, attribute:val,... </br>  
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
				if (type.equals(CREATURE_SIGNIFY))
					loadCreature(values);
				else if (type.equals(ITEM_SIGNIFY))
					loadItem(values);
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void loadCreature(final String[] values) {
		final String name = values[NAME_LOC];
		final String texture = CREATURE_SPRITE_LOC + values[IMAGE_LOC];
		final Creature pm = new Creature(name, texture);
		loadAttributes(values, name, pm);
		factory.addNewCreatureTemplate(pm);

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
				genAttributeThings.put(thing, AttributeGenerators.getGenerator(nameValuePair[1]));
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
