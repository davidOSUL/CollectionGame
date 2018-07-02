package gui.displayComponents;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import gui.gameComponents.PictureButton;
import gui.guiutils.GuiUtils;
import gui.mvpFramework.GameView;
import shopLoader.ShopItem;

public class ShopWindow {
	private Map<PictureButton, ShopItem> items = new HashMap<PictureButton, ShopItem>();
	/**
	 * How big the pictures of the things should be
	 */
	private static final int PICTURE_DIM = 50;
	/**
	 * The Point on the sprite image template where the iamge begins
	 */
	private static final Point SPRITE_IMAGE_LOC = new Point(15, 15);
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
	 * The layout for the shopWindow
	 */
	private static final GridLayout LAYOUT = new GridLayout(0, NUM_COLS, HGAP, VGAP);
	private JPanel shopWindow;
	private JScrollPane scrollableShopWindow;
	private GameView gv;
	private static final Font DEFAULT_FONT = new Font("TimesRoman", Font.BOLD, 11);
	
	
	/**
	 * Creates a new ShopWindow with no items
	 * @param gv the gameView to interact with
	 */

	public ShopWindow(GameView gv) {
		setUpPanel();
		scrollableShopWindow = new JScrollPane(shopWindow);
		this.gv = gv;
	}
	private void setUpPanel() {
		shopWindow = new JPanel();
		shopWindow.setLayout(LAYOUT);
		shopWindow.setVisible(true);
		shopWindow.setOpaque(true);
	}
	public void updateItems(Set<ShopItem> shopItems) {
		setUpPanel();
		
		shopItems.forEach(shopItem -> {
			PictureButton pb = new PictureButton(generateImage(shopItem), p -> p.attemptPurchaseThing(shopItem), gv).disableBorder();
			DescriptionManager.getInstance().setDescription(pb, shopItem.toString());
			shopWindow.add(pb);
			shopWindow.revalidate();
			shopWindow.repaint();
		});
		scrollableShopWindow = new JScrollPane(shopWindow);
		scrollableShopWindow.revalidate();
		scrollableShopWindow.repaint();
		
		
	}
	private static Image generateImage(ShopItem item) {
		Image sprite = GuiUtils.readTrimAndScaleImage(item.getImage(), PICTURE_DIM, PICTURE_DIM);
		Image overlay =GuiUtils.overlayImage(ITEM_TEMPLATE, sprite, SPRITE_IMAGE_LOC); 
		Image overlayWithQuantity = GuiUtils.overlayText(overlay, item.getQuantity() + "x", QUANTITY_LOC, DEFAULT_FONT);
		Image result = GuiUtils.overlayText(overlayWithQuantity, Integer.toString(item.getCost()), COST_LOC, DEFAULT_FONT);
		return result;
	}
	public JComponent getShopWindow() {
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setSize(DEFAULT_SIZE);
		panel.add(scrollableShopWindow, BorderLayout.CENTER);
		panel.revalidate();
		panel.repaint();
		
		return panel;
	}

}
