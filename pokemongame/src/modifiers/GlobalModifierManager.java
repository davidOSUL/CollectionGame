package modifiers;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GlobalModifierManager<T> implements Serializable {
	private final Map<Modifier<T>, Set<T>> thingsModified = new HashMap<Modifier<T>, Set<T>>();
	public void addModifier(final Collection<T> currentPresent, final Modifier<T> modifier) {
		for (final T t : currentPresent) {
			modifyIfShould(t, modifier);
		}
	}
	public void notifyAdded(final T t) {
		for (final Modifier<T> modifier: thingsModified.keySet()) {
			modifyIfShould(t, modifier);
		}
	}
	public void notifyRemoved(final T t) {
		thingsModified.forEach((mod, set) -> {
			if (set.contains(t)) {
				set.remove(t);
				mod.performReverseModificationIfShould(t);
			}
		});
	}
	public void update() {
		thingsModified.entrySet().removeIf(e -> {
			final boolean isDone = e.getKey().isDone();
			if (isDone)
				e.getValue().forEach(t -> e.getKey().performReverseModificationIfShould(t));
			return isDone;
		});
	}
	private void modifyIfShould(final T t, final Modifier<T> modifier) {
		final boolean modified = modifier.performModificationIfShould(t);
		if (modified) {
			thingsModified.putIfAbsent(modifier, new HashSet<T>());
			thingsModified.get(modifier).add(t);
		}
	}
	
}
