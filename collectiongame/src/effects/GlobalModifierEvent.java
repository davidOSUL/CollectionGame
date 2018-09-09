package effects;

import attributes.ParseType;
import model.ModelInterface;
import modifiers.Modifier;

/**
 * An event that applies a modifier to all Things (or all of a particular type of thing) present on the model
 * @author David O'Sullivan
 *
 */
public class GlobalModifierEvent extends Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Modifier firstModifier;
	private final Modifier[] mods;
	private final boolean removeCreatorWhenDone;
	private final boolean displayCountdown;
	private boolean sentRequest = false;
	private final GlobalModifierOption option;
	/**
	 * Creates a new GlobalModifierEvent
	 * @param mods the modifiers to apply to the model (they all must have the same life time)
	 * @param removeCreatorWhenDone whether or not the creator of this event should be removed from the model once the lifetime of this modifier is completed
	 * @param displayCountdown whether or not a countdown should be displayed showing the time left for this modifiers
	 * @param option an option that specifies what types of Things the modifiers should be applied to
	 */
	public GlobalModifierEvent(final Modifier[] mods, final boolean removeCreatorWhenDone, final boolean displayCountdown, final GlobalModifierOption option) {
		this.mods = mods;
		this.firstModifier = mods[0];
		verifyLifetimes();
		this.removeCreatorWhenDone = removeCreatorWhenDone;
		this.displayCountdown = displayCountdown;
		this.option = option;
		setOnPlace(model -> {
			if (getCreator() == null)
				throw new IllegalStateException("Held event has no creator!");
			if (displayCountdown && !getCreator().containsAttribute("time left"))
				getCreator().addAttribute("time left");
			addModsToModel(model); //will add the mod to model, model will also start mod.startCount(...)
		});
		setOnRemove(model -> {
			removeModsFromModel(model); //this does have the potential to double up with removal from running out of time, but that's ok, just nothing will happen the second time
			if (displayCountdown)
				getCreator().removeAttribute("time left");
		});
		setOnTick(model -> {
			if (displayCountdown)
				getCreator().setAttributeValue("time left", firstModifier.timeLeft(model.getTotalInGameTime()), ParseType.STRING);
			if (!sentRequest && removeCreatorWhenDone && firstModifier.isDone(model.getTotalInGameTime())) {
				model.addToRemoveRequest(getCreator()); //request to remove the creator. Note that the removal of the modification itself is handled by the model
				sentRequest = true;
			}
		});
	}
	/**
	 * Creates a new GlobalModifierEvent
	 * @param mod the modifier to apply to the model (they all must have the same life time)
	 * @param removeCreatorWhenDone whether or not the creator of this event should be removed from the model once the lifetime of this modifier is completed
	 * @param displayCountdown whether or not a countdown should be displayed showing the time left for this modifiers
	 * @param option an option that specifies what types of Things the modifiers should be applied to
	 */
	public GlobalModifierEvent(final Modifier mod, final boolean removeCreatorWhenDone, final boolean displayCountdown, final GlobalModifierOption option) {
		this(new Modifier[] {mod}, removeCreatorWhenDone, displayCountdown, option);
	}
	private GlobalModifierEvent(final Modifier[] modCopys, final GlobalModifierEvent copy) {
		this(modCopys, copy.getRemoveCreatorWhenDone(), copy.getDisplayCountdown(), copy.option);
	}

	/** 
	 * @see effects.Event#makeCopy()
	 */
	@Override
	public GlobalModifierEvent makeCopy() {
		final Modifier[] copyMods = new Modifier[mods.length];
		for (int i = 0; i < mods.length; i++)
			copyMods[i] = mods[i];
		return new GlobalModifierEvent(copyMods, this);
	}
	/**
	 * @return the removeCreatorWhenDone
	 */
	private boolean getRemoveCreatorWhenDone() {
		return removeCreatorWhenDone;
	}
	/**
	 * @return the displayCountdown
	 */
	private boolean getDisplayCountdown() {
		return displayCountdown;
	}
	private void addModsToModel(final ModelInterface model) {
		for (final Modifier mod : mods) {
			if (option == null) {
				model.applyGlobalModifier(mod);
			} else {
				model.applyGlobalModifier(mod, option);
			}
		}
	}
	private void removeModsFromModel(final ModelInterface model) { 
		for (final Modifier mod : mods)
			model.removeGlobalModifier(mod);
	}
	/**
	 * ensures that all modifiers have the same lifetime
	 */
	private void verifyLifetimes() {
		final long targetLifeTime = firstModifier.getLifetimeInMillis();
		for (final Modifier m : mods) {
			if (m.getLifetimeInMillis() != targetLifeTime)
				throw new IllegalArgumentException("All modifiers for global modifier event must have the same lifetime");
		}
	}

}
