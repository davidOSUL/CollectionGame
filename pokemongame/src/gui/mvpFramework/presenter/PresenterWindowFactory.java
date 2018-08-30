package gui.mvpFramework.presenter;

import java.awt.Image;
import java.io.Serializable;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import game.Board;
import gui.displayComponents.InfoWindowBuilder;
import gui.gameComponents.grid.GridSpace;
import gui.guiutils.GuiUtils;
import loaders.shopLoader.ShopItem;
import thingFramework.Pokemon;
import thingFramework.Thing;

public class PresenterWindowFactory implements Serializable {
	private final Presenter presenter;
	private final Board board;
	
	/**
	 * The background of the JPanel that pops up when the notification button is pressed
	 */
	private final static Image INFO_WINDOW_BACKGROUND =GuiUtils.changeOpacity(GuiUtils.readImage("/sprites/ui/pikabackground.jpg"), .5f);
	public PresenterWindowFactory(final Presenter presenter, final Board board) {
		this.presenter = presenter;
		this.board = board;
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
	public JComponent attemptToSellBackWindow(final GridSpace gs, final ShopItem item) {
		final StringBuilder info = new StringBuilder("Are you sure you want to " + presenter.getSellBackString(gs) + "?");
		if (!board.canAddBackToShopStock(item))
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
	public JComponent advancedStatsWindow() {
		final JPanel panel = new JPanel();
		final JTextArea area = new JTextArea(board.getAdvancedStats());

		area.setEditable(false);
		panel.setSize(area.getPreferredSize().width+20, area.getPreferredSize().height+20);
		panel.add(area);
		return panel;

	}
	/**
	 * Generates a new JPanel corresponding to the next pokemon in the queue, and giving the user the option to add it, set it free or cancel the request and place it back in the queue
	 * @param p the next pokemon in the queue
	 * @return the JPanel
	 */
	public JComponent wildPokemonWindow(final Pokemon p, final Consumer<Presenter> letPokeGo) {
		return  new InfoWindowBuilder()
				.setPresenter(presenter)
				.setInfo("A wild " + p.getName() + " appeared!")
				.setImagable(p)
				.addEnterButton("Place")
				.addButton("Set Free", letPokeGo, true, false, true)
				.addCancelButton()
				.setBackgroundImage(INFO_WINDOW_BACKGROUND)
				.createWindow();

	}
}
