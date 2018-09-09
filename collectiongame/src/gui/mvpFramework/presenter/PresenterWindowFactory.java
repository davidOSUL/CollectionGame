package gui.mvpFramework.presenter;

import java.awt.Image;
import java.io.Serializable;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import gui.displayComponents.InfoWindowBuilder;
import gui.gameComponents.grid.GridSpace;
import gui.guiutils.GuiUtils;
import loaders.shopLoader.ShopItem;
import model.ModelInterface;
import model.ShopWindow;
import thingFramework.Creature;
import thingFramework.Thing;

/**
 * Used to create common windows that popup for display
 * @author David O'Sullivan
 *
 */
public class PresenterWindowFactory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Presenter presenter;
	private final ModelInterface model;
	private final ShopWindow shopWindow;
	
	/**
	 * The background of the JPanel that pops up when the notification button is pressed
	 */
	private final static Image INFO_WINDOW_BACKGROUND =GuiUtils.changeOpacity(GuiUtils.readImage("/sprites/ui/pikabackground.jpg"), .5f);
	/**
	 * Creates a new PresenterWindowFactory
	 * @param presenter the presenter of the current game
	 * @param model the model of the current game
	 */
	public PresenterWindowFactory(final Presenter presenter, final ModelInterface model, final ShopWindow shopWindow) {
		this.presenter = presenter;
		this.model = model;
		this.shopWindow = shopWindow;
	}
	/**
	 * Generates a new JPanel confirming if the user actually wants to delete the passed in thing
	 * @param t The thing that the user needs to confirm deletion of
	 * @return the created JPanel
	 */
	public JComponent attemptToDeleteWindow(final Thing t) {
		return new InfoWindowBuilder()
				.setPresenter(presenter)
				.setInfo("Are you sure you want to \n" + GuiUtils.decapitalize(t.getDiscardText()) + "?")
				.setImagable(t)
				.setScale(96, 96)
				.addEnterButton("Yes")
				.addCancelButton()
				.setBackgroundImage(INFO_WINDOW_BACKGROUND)
				.createWindow();

	}
	/**
	 * The window that should appear when the user wants to purchase an item
	 * @param item the item that the user is attempting to purchase
	 * @return a confirmation window to purchase the provided item
	 */
	public JComponent confirmPurchaseWindow(final ShopItem item) {
		return new InfoWindowBuilder()
				.setPresenter(presenter)
				.setInfo("Are you sure you want to purchase \n" + item.getThingName() + "\nfor " + item.getCost() + GuiUtils.getMoneySymbol() + "?")
				.setImagable(item)
				.setScale(96, 96)
				.addEnterButton("Yes")
				.addCancelButton()
				.setBackgroundImage(INFO_WINDOW_BACKGROUND)
				.createWindow();
	}
	/**
	 * The window that should appear when the user attempts to sell back an item
	 * @param gs the GridSpace containing the item the user is selling back
	 * @param item the ShopItem the user is selling back
	 * @return the window that should appear when the user attempts to sell back an item
	 */
	public JComponent attemptToSellBackWindow(final GridSpace gs, final ShopItem item) {
		final StringBuilder info = new StringBuilder("Are you sure you want to " + presenter.getSellBackString(gs) + "?");
		if (!shopWindow.canAddBackToShopStock(item))
			info.append("WARNING: this item is no longer available for sale in the shop. \n It will not be added back to the shop stock after selling");
		return new InfoWindowBuilder()
				.setPresenter(presenter)
				.setInfo(info.toString())
				.setImagable(item)
				.setScale(96, 96)
				.addEnterButton("Yes")
				.addCancelButton()
				.setBackgroundImage(INFO_WINDOW_BACKGROUND)
				.createWindow();
	}
	/**
	 * Returns a window showing the model's advanced stats
	 * @return a window showing the model's advanced stats
	 */
	public JComponent advancedStatsWindow() {
		final JPanel panel = new JPanel();
		final JTextArea area = new JTextArea(model.getAdvancedStats());

		area.setEditable(false);
		panel.setSize(area.getPreferredSize().width+20, area.getPreferredSize().height+20);
		panel.add(area);
		return panel;

	}
	/**
	 * Generates a new JPanel corresponding to the next Creature in the queue, and giving the user the option to add it, set it free or cancel the request and place it back in the queue
	 * @param creature the next Creature in the queue
	 * @param letCreatureGo what should happen if the user wants to let the found creature go
	 * @return the JPanel
	 */
	public JComponent wildCreatureWindow(final Creature creature, final Consumer<Presenter> letCreatureGo) {
		return  new InfoWindowBuilder()
				.setPresenter(presenter)
				.setInfo("A wild " + creature.getName() + " appeared!")
				.setImagable(creature)
				.addEnterButton("Place")
				.addButton("Set Free", letCreatureGo, true, false, true)
				.addCancelButton()
				.setBackgroundImage(INFO_WINDOW_BACKGROUND)
				.createWindow();

	}
}
