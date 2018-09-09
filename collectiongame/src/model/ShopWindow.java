package model;

import java.io.Serializable;
import java.util.Set;

import loaders.shopLoader.ShopItem;
import thingFramework.Thing;

/**
 * A class that has a Shop. Used to provide an interface to this shop for the Presenter.
 * This has a ModelInterface so that it can add the appropriate gold, etc.
 * @author David O'Sullivan
 *
 */
public class ShopWindow implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * the shop associated with this ModelInterface
	 */
	private final Shop shop;
	private final ModelInterface model;
	private final double sellBackPercent = .5;
	/**
	 * This is the current ShopItem that may be purchased if the purchase is confirmed or it may be refunded if the purchase is canceled
	 */
	private ShopItem grabbedShopItem = null;
	/**
	 * Creates a new ShopWindow for the provided model.
	 * @param model the model to interact with, add/remove gold from, etc.
	 * @param sellBackPercent the default sell back percantage value 
	 */
	public ShopWindow(final ModelInterface model) {
		shop = new Shop();
		this.model = model; 
	}
	/**
	 * @return a queue (sorted by display rank) of all the items presently in the shop
	 */
	public Set<ShopItem> getItemsInShop() {
		return shop.itemsInOrder();
	}

	/**
	 * @param item the item in the shop to purchase
	 * @return true if have enough money to purchase, false otherwise
	 */
	public boolean canPurchase(final ShopItem item) {
		return (item.getCost() <= model.getGold()) && (model.getPopularity() >= item.getMinPopularityToPurchase());
	}

	/**
	 * If have enough money, start the purchase attempt
	 * @param item the ShopItem in the shop
	 * @return a Thing (for display purposes) that corresponds to the item
	 */
	public Thing startPurchase(final ShopItem item) {
		if (canPurchase(item)) {
			grabbedShopItem = item;
			return Shop.getThingCopy(item);
		}
		return null;

	}

	/**
	 * If have enough money, generate a new Thing corresponding to the thingName in the shop, and subtract the cost of that thing
	 * @return the newly generated thing
	 */
	public Thing confirmPurchase() {
		if (!canPurchase(grabbedShopItem))
			return null;
		model.subtractGold(grabbedShopItem.getCost());
		return shop.purchase(grabbedShopItem);

	}

	/**
	 * Cancel the purchase attempt
	 */
	public void cancelPurchase() {
		grabbedShopItem = null;
	}

	/**
	 * @param item the item to sell back
	 *  @return false if the item is not able to be added back to the shop
	 */
	public boolean canAddBackToShopStock(final ShopItem item) {
		return shop.isValidShopItem(item.getThingName());
	}

	/**
	 * Sell back the specified item, adding it back to the shop and adding that much gold to the user
	 * @param item the item to sell back
	 */
	public void sellBack(final ShopItem item) {
		model.addGold(getSellBackValue(item));
		sendItemBackToShop(item);
	}

	/**
	 * Sends the item back to shop without adding any gold
	 * @param item the item to send back
	 */
	public void sendItemBackToShop(final ShopItem item) {
		if (canAddBackToShopStock(item))
			shop.addToShopStock(item.getThingName());
	}

	/**
	 * @param item the item to sell back
	 * @return how much it would be sold back for
	 */
	public int getSellBackValue(final ShopItem item) {
		if (item.getSellBackValue() == ShopItem.DEFAULT)
			return Math.max(1 , (int)(item.getCost()*sellBackPercent));
		else
			return item.getSellBackValue();
	}
	/**
	 * To be called on start up. Checks the shop for shop updates
	 */
	public void onStartUp() {
		shop.checkForShopUpdates();
	}
}
