package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import effects.GlobalModifierOption;
import modifiers.Modifier;

class ModifierManager implements Serializable{
	private final Map<GlobalModifierOption, Set<Modifier>> modifiers = new HashMap<GlobalModifierOption, Set<Modifier>>();
	private final Map<Modifier, GlobalModifierOption> modifierToOption = new HashMap<Modifier, GlobalModifierOption>();
	private final Board b;
	public ModifierManager(final Board b) {
		this.b = b;
	}
	void addGlobalModifier(final Modifier mod, final GlobalModifierOption option) {
		modifiers.computeIfAbsent(option, k -> new HashSet<Modifier>()).add(mod);
		modifierToOption.put(mod, option);
	}
	void notifyGlobalModifierRemoved(final Modifier mod) {
		modifiers.get(modifierToOption.remove(mod)).remove(mod);
	}
	void update() {
		final List<Modifier> modifiersToRemove = new ArrayList<Modifier>();
		modifierToOption.forEach( (mod, option) -> {
			if (mod.isDone(b.getTotalInGameTime())) {
				modifiersToRemove.add(mod);
			}
		});
		modifiersToRemove.forEach(mod -> b.removeGlobalModifier(mod));
	}
	Set<Modifier> getModifiersOfOption(final GlobalModifierOption option) {
		return modifiers.get(option);
	}
	
	
}
