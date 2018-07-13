package gui.displayComponents;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javax.swing.GrayFilter;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import gameutils.GameUtils;
import gui.gameComponents.GameSpace;
import gui.gameComponents.PictureButton;
import gui.guiutils.GuiUtils;
import gui.mvpFramework.GameView;
import gui.mvpFramework.Presenter;
import loaders.shopLoader.ShopItem;

/**
 * The UI for the shop
 * @author David O'Sullivan
 *
 */
//TODO: Make this two seperate classes, one with scroll, other with buttons
public class ShopWindow {
	/**
	 * How big the pictures of the things should be
	 */
	private static final int PICTURE_DIM = 48;
	/**
	 * The Point on the sprite image template where the iamge begins
	 */
	private static final Point SPRITE_IMAGE_LOC = new Point(16, 16);
	/**
	 * The Point on the sprite image template where the quantity should be written
	 */
	private static final Point QUANTITY_LOC = new Point(62, 11);
	/**
	 * The Point on the sprite image template where the cost should be written
	 */
	private static final Point COST_LOC = new Point(23, 75);
	/**
	 * The template that all shop items are overlayed onto
	 */
	private static final Image ITEM_TEMPLATE = GuiUtils.readImage("/sprites/ui/shopItemTemplate.png");
	/**
	 * Number of columns of items
	 */
	private static final int NUM_COLS = 5;
	/**
	 * gap (in pixels) between items horizontally
	 */
	private static final int HGAP = 10;
	/**
	 * Gap (in pixels) between items vertically
	 */
	private static final int VGAP = 10;
	/**
	 * Default size of JPanel
	 */
	private static final Dimension DEFAULT_SIZE = new Dimension(NUM_COLS*(ITEM_TEMPLATE.getWidth(null)+2*HGAP), 2*(ITEM_TEMPLATE.getHeight(null)+2*VGAP));
	/**
	 * The layout for the shopWindow when using scrollable version
	 */
	private static final GridLayout SCROLL_LAYOUT = new GridLayout(0, NUM_COLS, HGAP, VGAP);
	private JPanel shopWindowForScrolling;
	private JScrollPane scrollableShopWindow;
	private JPanel cardLayoutShopWindow;
	private final GameView gv;
	private static final Font DEFAULT_FONT = new Font("TimesRoman", Font.BOLD, 11);
	private static final int CARD_NUM_ROWS_THINGS = 4;
	private static final int CARD_NUM_COLS_THINGS = 4;
	private static final int ITEMS_PER_CARD = CARD_NUM_ROWS_THINGS * CARD_NUM_COLS_THINGS;
	private static final int TOTAL_NUM_ROWS = 4;
	private static final int TOTAL_NUM_COLS = 6;
	private static final int INITIAL_COL_OFFSET = 1;
	private static final int END_COL_OFFSET = 1;
	private static final int INITIAL_ROW_OFFSET = 0;
	private static final int END_ROW_OFFSET = 0;
	private static final Image CARD_BACKGROUND = GuiUtils.readTrimAndScaleImage("/sprites/ui/sand_template.jpg", 80*TOTAL_NUM_COLS, 80*TOTAL_NUM_ROWS);
	private int currentIndex = 0;
	/**
	 * Creates a new ShopWindow with no items
	 * @param gv the gameView to interact with
	 */

	public ShopWindow(final GameView gv) {
		setUpPanel();
		scrollableShopWindow = new JScrollPane(shopWindowForScrolling);
		this.gv = gv;
	}
	private void setUpPanel() {
		shopWindowForScrolling = new JPanel();
		shopWindowForScrolling.setLayout(SCROLL_LAYOUT);
		shopWindowForScrolling.setVisible(true);
		shopWindowForScrolling.setOpaque(true);
		
		cardLayoutShopWindow = new JPanel(new CardLayout());
		cardLayoutShopWindow.setSize(CARD_BACKGROUND.getWidth(null), CARD_BACKGROUND.getHeight(null));
		cardLayoutShopWindow.setVisible(true);
		cardLayoutShopWindow.setOpaque(true);
	}
	public void updateItems(final Set<ShopItem> shopItems) {
		setUpPanel();
		final Iterator<ShopItem> it = shopItems.iterator();
		int i = 0; //number of iterations
		final int j = 0; //number of times a new card is made
		final PictureButton allItems[] = new PictureButton[shopItems.size()];
		while (it.hasNext()) {
			final ShopItem shopItem = it.next();
			final PictureButton<Presenter> pb = new PictureButton<Presenter>(generateImage(shopItem), p -> p.attemptPurchaseThing(shopItem), gv.getPresenter()).disableBorder();
			DescriptionManager.getInstance().setDescription(pb, shopItem.toString());
			allItems[i++] = pb;
			shopWindowForScrolling.add(pb);
			shopWindowForScrolling.revalidate();
			shopWindowForScrolling.repaint();
		}
		generateCardShopWindows(allItems);
		scrollableShopWindow = new JScrollPane(shopWindowForScrolling);
		scrollableShopWindow.revalidate();
		scrollableShopWindow.repaint();
		
		
	}
	private Image generateImage(final ShopItem item) {
		Image sprite = null;
		sprite = GuiUtils.readAndTrimImage(item.getImage());
		final Image overlay;
		if (sprite.getWidth(null) > PICTURE_DIM || sprite.getHeight(null) > PICTURE_DIM) {
			sprite = GuiUtils.getScaledImage(sprite, PICTURE_DIM, PICTURE_DIM); 
		}
		
		if (sprite.getWidth(null) < PICTURE_DIM/2 && sprite.getHeight(null) < PICTURE_DIM/2) {
			overlay = GuiUtils.overlayImage(ITEM_TEMPLATE, sprite, 
					GuiUtils.addPoints(new Point(PICTURE_DIM/2, PICTURE_DIM/2), SPRITE_IMAGE_LOC));
		}
		else
			overlay =GuiUtils.overlayImage(ITEM_TEMPLATE, sprite, SPRITE_IMAGE_LOC); 
		final Image overlayWithQuantity = GuiUtils.overlayText(overlay, item.getDisplayQuantity() + "x", QUANTITY_LOC, DEFAULT_FONT);
		Image result = GuiUtils.overlayText(overlayWithQuantity, Integer.toString(item.getCost()), COST_LOC, DEFAULT_FONT);
		if (gv.getPresenter().shouldGreyOut(item))
			result = GrayFilter.createDisabledImage(result);
		return result;
	}
	public JComponent getShopWindowAsScrollable() {
		
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setSize(DEFAULT_SIZE);
		panel.add(scrollableShopWindow, BorderLayout.CENTER);
		panel.revalidate();
		panel.repaint();
		
		return panel;
	}
	public JComponent getShopWindowAtCurrentLocation() {
		return getShopWindowAsCardLayout(currentIndex);
	}
	public JComponent getShopWindowAsCardLayout() {
		return getShopWindowAsCardLayout(0);
		
	}
	private JComponent getShopWindowAsCardLayout(int i) {
		currentIndex = i;
		((CardLayout) cardLayoutShopWindow.getLayout()).show(cardLayoutShopWindow, Integer.toString(i));
		cardLayoutShopWindow.revalidate();
		cardLayoutShopWindow.repaint();
		return GuiUtils.componentWithBorder(cardLayoutShopWindow);
		
	}
	private void generateCardShopWindows(final PictureButton[] allItems) {
		final int numWindows = GameUtils.roundToMultiple(allItems.length, ITEMS_PER_CARD)/ITEMS_PER_CARD;
		for (int i = 0; i < numWindows; i++) {
			final PictureButton[] itemsForWindow = Arrays.copyOfRange(allItems, i*ITEMS_PER_CARD, Math.min(allItems.length, (i+1)*ITEMS_PER_CARD));
			cardLayoutShopWindow.add(getWindow(itemsForWindow, i != 0, i != numWindows-1, i), Integer.toString(i));
		}
	}
	private GameSpace getWindow(final PictureButton[] items, final boolean addPrev, final boolean addNext, final int windowIndex) {
		final GameSpace gs = new GameSpace(CARD_BACKGROUND);
		final GridLayout layout = new GridLayout(TOTAL_NUM_ROWS, TOTAL_NUM_COLS);
		gs.setLayout(layout);
		gs.setOpaque(true);
		gs.setVisible(true);
		final JPanel[][] holder = getNewHolder(gs);
		holder[0][0].add(ButtonBuilder.generatePictureButton("cancel_button", p -> p.Canceled(), gv.getPresenter()));
		if (addPrev) {
			final PictureButton<CardLayout> prevButton = ButtonBuilder.generatePictureButton("prev_button",cl -> {cl.show(cardLayoutShopWindow, Integer.toString(windowIndex-1)); currentIndex = windowIndex-1;}, (CardLayout) cardLayoutShopWindow.getLayout());
			holder[TOTAL_NUM_ROWS-1][0].setLayout(new GridBagLayout());
			holder[TOTAL_NUM_ROWS-1][0].add(prevButton);
		}
		if (addNext) {
			final PictureButton<CardLayout> nextButton = ButtonBuilder.generatePictureButton("next_button", cl -> {cl.show(cardLayoutShopWindow, Integer.toString(windowIndex+1)); currentIndex = windowIndex+1;}, (CardLayout) cardLayoutShopWindow.getLayout());
			holder[TOTAL_NUM_ROWS-1][TOTAL_NUM_COLS-1].setLayout(new GridBagLayout());
			holder[TOTAL_NUM_ROWS-1][TOTAL_NUM_COLS-1].add(nextButton);
		}
		addItems(holder, items);
		gs.revalidate();
		gs.repaint();
		return gs;
	}
	private JPanel[][] getNewHolder(final GameSpace gs) {
		final JPanel[][] holder = new JPanel[TOTAL_NUM_ROWS][TOTAL_NUM_COLS];
		for (int i =0 ; i < TOTAL_NUM_ROWS; i++)
			for (int j = 0; j < TOTAL_NUM_COLS; j++) {
				holder[i][j] = new JPanel();
				holder[i][j].setOpaque(false);
				gs.add(holder[i][j]);

			}
		return holder;
	}
	private void addItems(final JPanel[][] holder, final PictureButton[] items) {
		int k = 0;
		for (int i =INITIAL_ROW_OFFSET; i < TOTAL_NUM_ROWS-END_ROW_OFFSET; i++) {
			for (int j = INITIAL_COL_OFFSET; j < TOTAL_NUM_COLS-END_COL_OFFSET; j++) {
				if (k == items.length || items[k] == null)
					return;
				holder[i][j].add(items[k++]);
			}
		}
	}


}
