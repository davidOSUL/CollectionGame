package loaders.eventbuilder.generatedevents;

import effects.ActOnCreatorEvent;
import effects.Event;
import gui.guiutils.GuiUtils;

/**
 * Constructs Events that remove all creatures on the model and then modify the model in some way based on
 * the number of creatures removed
 * @author David O'Sullivan
 *
 */
public class MassRemovalEventFactory extends TypicalEventFactory {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final StatToAdd statToAddTo;
	private final int amountToAdd;
	private static final int STAT_TO_ADD_TO_LOC = 1;
	private static final int AMOUNT_LOC = 2;
	/**
	 * Constructs a new MassRemovalEventFactory
	 * @param inputs the inputs to use to construct the MassRemovalEventFactory
	 */
	public MassRemovalEventFactory(final String[] inputs) {
		super(inputs);
		statToAddTo = StatToAdd.valueOf(inputs[STAT_TO_ADD_TO_LOC].toUpperCase().trim());
		amountToAdd = Integer.parseInt(inputs[AMOUNT_LOC]);
	};
	/** 
	 * @see loaders.eventbuilder.generatedevents.TypicalEventFactory#generateEvent()
	 */
	@Override
	public Event generateEvent() {
		return new ActOnCreatorEvent(
				model -> {
					final int i = model.removeAllCreatures(); 
					switch(statToAddTo) {
					case GOLD:
						model.addGold(i*amountToAdd);
					}
				}, 
				x->{}, 
				(t, e, b) -> {},
				(t, e, b) -> {
					b.addToRemoveRequest(t); //request to remove this object
				});
	}

	/** 
	 * @see loaders.eventbuilder.generatedevents.TypicalEventFactory#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Permanently removes all " + GuiUtils.getPluralCreatureName() + " on the board.\n"
				+ "For each " + GuiUtils.getCreatureName() + " removed this way you get <font color=\"green\">+" +
				getStatDescription();
	}
	private final String getStatDescription() {
		switch(statToAddTo) {
		case GOLD:
			return GuiUtils.getMoneySymbol() + amountToAdd; 
		default:
			throw new IllegalArgumentException("Unsupported stat to add");
		}
	}
	private enum StatToAdd {
		GOLD;
		
	}

}
