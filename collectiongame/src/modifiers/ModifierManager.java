package modifiers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import effects.GlobalModifierOption;
import model.ModelInterface;

/**
 * Manages the Global Modifiers applied to a model
 * @author David O'Sullivan
 *
 */
public class ModifierManager implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Map<GlobalModifierOption, Set<Modifier>> modifiers = new HashMap<GlobalModifierOption, Set<Modifier>>();
	private final Map<Modifier, GlobalModifierOption> modifierToOption = new HashMap<Modifier, GlobalModifierOption>();
	private final ModelInterface model;
	/**
	 * Creates a new ModifierManaager
	 * @param model the model that this ModifierManager manages the global modifiers event of
	 */
	public ModifierManager(final ModelInterface model) {
		this.model = model;
		for (final GlobalModifierOption option: GlobalModifierOption.values())
			modifiers.put(option, new HashSet<Modifier>());
	}
	/**
	 * should be called whenever a global modifier is added
	 * @param mod the modifier that was added
	 * @param option the Option used 
	 */
	public void addGlobalModifier(final Modifier mod, final GlobalModifierOption option) {
		modifiers.get(option).add(mod);
		modifierToOption.put(mod, option);
	}
	/**
	 * Should be called whenever a global modifier is removed
	 * @param mod the modifier that was removed
	 */
	public void notifyGlobalModifierRemoved(final Modifier mod) {
		if (modifierToOption.containsKey(mod))
			modifiers.get(modifierToOption.remove(mod)).remove(mod);
	}
	/**
	 * Should be called every game tick to to removed modifiers whose time is up
	 */
	public void update() {
		final List<Modifier> modifiersToRemove = new ArrayList<Modifier>();
		modifierToOption.forEach( (mod, option) -> {
			if (mod.isDone(model.getTotalInGameTime())) {
				modifiersToRemove.add(mod);
			}
		});
		modifiersToRemove.forEach(mod -> model.removeGlobalModifier(mod));
	}
	/**
	 * Returns the modifiers of the specified option
	 * @param option the option of the modifier
	 * @return a set of modifiers of that option
	 */
	public Set<Modifier> getModifiersOfOption(final GlobalModifierOption option) {
		return modifiers.get(option);
	}
	
	
}
