package loaders;

import thingFramework.Item;
import thingFramework.Pokemon;

interface ThingLoaderAdder {
	void addNewPokemonTemplate(Pokemon template);
	void addNewItemTemplate(Item template);
}
