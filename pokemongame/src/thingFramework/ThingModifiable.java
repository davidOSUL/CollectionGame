package thingFramework;

import modifiers.Modifier;

public interface ThingModifiable{
	public boolean addModifier(final Modifier<Thing> mod);
	public boolean removeModifier(final Modifier<Thing> mod);
}
