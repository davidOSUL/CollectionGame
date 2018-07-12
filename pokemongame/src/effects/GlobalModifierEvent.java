package effects;

import game.Board;
import modifiers.Modifier;
import thingFramework.Attribute;
import thingFramework.Thing;

public abstract class GlobalModifierEvent<M extends Thing, C extends Thing> extends HeldEvent<C> {
	private C creator;
	private final Modifier<M> mod;
	private final boolean removeCreatorWhenDone;
	private final boolean displayCountdown;
	private boolean sentRequest = false;
	public GlobalModifierEvent(final Modifier<M> mod, final boolean removeCreatorWhenDone, final boolean displayCountdown) {
		this.mod = mod;
		this.removeCreatorWhenDone = removeCreatorWhenDone;
		this.displayCountdown = displayCountdown;
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
				getCreator().setAttributeVal("time left", mod.timeLeft(board.getTotalInGameTime()));
			if (!sentRequest && removeCreatorWhenDone && mod.isDone(board.getTotalInGameTime())) {
				board.addToRemoveRequest(getCreator()); //request to remove the creator. Note that the removal of the modification itself is handled by the board
				sentRequest = true;
			}
		});
	}
	@Override
	public void setCreator(final C creator) {
		super.setCreator(creator);
		if (displayCountdown && !getCreator().containsAttribute("time left"))
			getCreator().addAttribute(Attribute.generateAttribute("time left"));
	}
	public Modifier<M> getMod() {
		return mod;
	}
	/**
	 * @return the removeCreatorWhenDone
	 */
	protected boolean getRemoveCreatorWhenDone() {
		return removeCreatorWhenDone;
	}
	/**
	 * @return the displayCountdown
	 */
	protected boolean getDisplayCountdown() {
		return displayCountdown;
	}
	public abstract void addModToBoard(Board b);
	public abstract void removeModFromBoard(Board b);

}
