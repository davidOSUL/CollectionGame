package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import modifiers.HeldModifier;
import modifiers.Modifier;
import thingFramework.Item;
import thingFramework.Pokemon;
import thingFramework.Thing;

public class ModifierManager implements Serializable{
	private final List<Modifier<Pokemon>> pokemonModifiers = new ArrayList<Modifier<Pokemon>>();
	private final List<Modifier<Item>> itemModifiers = new ArrayList<Modifier<Item>>();
	private final List<Modifier<Thing>> thingModifiers = new ArrayList<Modifier<Thing>>();
	private final List<HeldModifier<?>> removeWhenDoneModifiers = new ArrayList<HeldModifier<?>>();
	private final List<HeldModifier<?>> displayCountdownModifiers = new ArrayList<HeldModifier<?>>();
	private final Set<Modifier<?>> allModifiers = new HashSet<Modifier<?>>();
	private final Board b;
	public ModifierManager(final Board b) {
		this.b = b;
	}
	public Collection<Modifier<Thing>> getThingModifiers() {
		return Collections.unmodifiableList(thingModifiers);
	}
	public Collection<Modifier<Item>> getItemModifiers() {
		return Collections.unmodifiableList(itemModifiers);
	}
	public Collection<Modifier<Pokemon>> getPokemonModifiers() {
		return Collections.unmodifiableList(pokemonModifiers);
	}
	public void addGlobalPokemonModifier(final Modifier<Pokemon> mod) {
		pokemonModifiers.add(mod);
		allModifiers.add(mod);
	}
	public void addGlobalItemModifier(final Modifier<Item> mod) {
		itemModifiers.add(mod);
		allModifiers.add(mod);

	}
	public void addGlobalThingModifier(final Modifier<Thing> mod) {
		thingModifiers.add(mod);
		allModifiers.add(mod);

	}
	public void removeGlobalPokemonModifier(final Modifier<Pokemon> mod) {
		pokemonModifiers.remove(mod);
		allModifiers.remove(mod);
		removeWhenDoneModifiers.remove(mod);
		displayCountdownModifiers.remove(mod);
	}
	public void removeGlobalItemModifier(final Modifier<Item> mod) {
		itemModifiers.remove(mod);
		allModifiers.remove(mod);
		removeWhenDoneModifiers.remove(mod);
		displayCountdownModifiers.remove(mod);
	}
	public void removeGlobalThingModifier(final Modifier<Thing> mod) {
		thingModifiers.remove(mod);
		allModifiers.remove(mod);
		removeWhenDoneModifiers.remove(mod);
		displayCountdownModifiers.remove(mod);
	}
	public void update() {
		final List<Modifier<Pokemon>> pokeToRemove = new ArrayList<Modifier<Pokemon>>();
		pokemonModifiers.forEach((mod) -> {
			if (mod.isDone(b.getTotalInGameTime())) {
				pokeToRemove.add(mod);
			}
		});
		final List<Modifier<Item>> itemsToRemove = new ArrayList<Modifier<Item>>();
		itemModifiers.forEach((mod) -> {
			if (mod.isDone(b.getTotalInGameTime())) {
				itemsToRemove.add(mod);
			
			}
		});
		final List<Modifier<Thing>> thingsToRemove = new ArrayList<Modifier<Thing>>();
		thingModifiers.forEach((mod) -> {
			if (mod.isDone(b.getTotalInGameTime())) {
				thingsToRemove.add(mod);
				
			}
		});
		removeWhenDoneModifiers.forEach(mod -> {
			if (mod.isDone(b.getTotalInGameTime()) && mod.getCreator() != null) {
				b.addToRemoveRequest(mod.getCreator());
			}
		});
		displayCountdownModifiers.forEach(mod -> {
			mod.getCreator().setAttributeVal("time left", mod.timeLeft(b.getTotalInGameTime()));
		});
		pokeToRemove.forEach(mod -> b.removeGlobalPokemonModifier(mod));
		itemsToRemove.forEach(mod -> b.removeGlobalItemModifier(mod));
		thingsToRemove.forEach(mod -> b.removeGlobalThingModifier(mod));
	}
	public void addToRemoveWhenDoneList(final HeldModifier<?> mod) {
		if (!allModifiers.contains(mod))
			throw new IllegalStateException("Modifier not existant!");
		removeWhenDoneModifiers.add(mod);
	}
	public void addToDisplayCountdownList(final HeldModifier<?> mod) {
		if (!allModifiers.contains(mod))
			throw new IllegalStateException("Modifier not existant!");
		displayCountdownModifiers.add(mod);
	}
	
}
