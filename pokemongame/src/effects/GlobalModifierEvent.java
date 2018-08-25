package effects;

import attributes.ParseType;
import game.Board;
import modifiers.Modifier;
import thingFramework.Thing;

public class GlobalModifierEvent extends Event {
	private final Modifier mod;
	private final boolean removeCreatorWhenDone;
	private final boolean displayCountdown;
	private boolean sentRequest = false;
	private final GlobalModifierOption option;
	public GlobalModifierEvent(final Modifier mod, final boolean removeCreatorWhenDone, final boolean displayCountdown, final GlobalModifierOption option) {
		this.mod = mod;
		this.removeCreatorWhenDone = removeCreatorWhenDone;
		this.displayCountdown = displayCountdown;
		this.option = option;
		setOnPlace(board -> {
			if (getCreator() == null)
				throw new IllegalStateException("Held event has no creator!");
			addModToBoard(board); //will add the mod to board, board will also start mod.startCount(...)
		});
		setOnRemove(board -> {
			removeModFromBoard(board); //this does have the potential to double up with removal from running out of time, but that's ok, just nothing will happen the second time
			if (displayCountdown)
				getCreator().removeAttribute("time left");
		});
		setOnTick(board -> {
			if (displayCountdown)
				getCreator().setAttributeValue("time left", mod.timeLeft(board.getTotalInGameTime()), ParseType.STRING);
			if (!sentRequest && removeCreatorWhenDone && mod.isDone(board.getTotalInGameTime())) {
				board.addToRemoveRequest(getCreator()); //request to remove the creator. Note that the removal of the modification itself is handled by the board
				sentRequest = true;
			}
		});
	}
	private GlobalModifierEvent(final GlobalModifierEvent copy) {
		this(copy.getMod().makeCopy(), copy.getRemoveCreatorWhenDone(), copy.getDisplayCountdown(), copy.option);
	}
	@Override
	public void setCreator(final Thing creator) {
		super.setCreator(creator);
		if (displayCountdown && !getCreator().containsAttribute("time left"))
			getCreator().addAttribute("time left");
	}
	@Override
	public GlobalModifierEvent makeCopy() {
		return new GlobalModifierEvent(this);
	}
	public Modifier getMod() {
		return mod;
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
	private void addModToBoard(final Board b) {
		if (option == null) {
			b.applyGlobalModifier(mod);
		} else {
			b.applyGlobalModifier(mod, option);
		}
	}
	private void removeModFromBoard(final Board b) { 
		b.removeGlobalModifier(mod);
	}

}
