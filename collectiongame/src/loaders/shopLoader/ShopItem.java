package loaders.shopLoader;

import java.io.Serializable;

import gameutils.GameUtils;
import interfaces.Imagable;
import loaders.ThingFactory;

/**
 * A singular item in the shop. This represents the "data" behind the item, that is the name, the quantity,
 * the cost, etc.
 * @author David O'Sullivan
 *
 */
public class ShopItem implements Serializable, Imagable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String thingName;
	private int quantity;
	private int cost;
	private int displayRank;
	private String image;
	private String description;
	private int sellBackValue;
	private int minPopularityToPurchase;
	/**
	 * The value representing an infinite quantity of the item
	 */
	public static final int INFINITY = -1;
	/**
	 * If sellBackValue is equal to DEFAULT then there is no custom sellBackValue, and board should determine
	 * sellBackValue itself
	 */
	public static final int DEFAULT = -1;
	/**
	 * if the user removes this item (doesn't sell it back), should it be sent back to the shop
	 */
	private final boolean sendBackToShopWhenRemoved;
	/**
	 * the maximum that are allowed to be on the board at a time
	 */
	private final int maxAllowed;
	/**
	 * Construct a new ShopItem with the given paramter
	 * @param thingName the thingName
	 * @param initialQuantity the quantity
	 * @param cost the cost
	 * @param displayRank the order that the ShopItem should be displayed relative to other shopItems (1 first, 2 second, etc.)
	 * @param sendBackToShopWhenRemoved if set to true, signifies that this shop item should be sent back when it is removed by the user (even if not sold back)
	 * @param maxAllowed the maximum amount of these items allowed on the board at a time
	 ** @param sellBackValue the amount to refund upon selling back
	 * @param minPopularityToPurchase the minimum popularity required to purchase this item
	 */
	protected ShopItem(final String thingName, final int initialQuantity, final int cost, final int displayRank, final boolean sendBackToShopWhenRemoved, final int maxAllowed, final int sellBackValue, final int minPopularityToPurchase) {
		this.setThingName(thingName);
		this.setQuantity(initialQuantity);
		this.setCost(cost);
		this.setDisplayRank(displayRank);
		setImage();
		this.sendBackToShopWhenRemoved = sendBackToShopWhenRemoved;
		this.maxAllowed = maxAllowed;
		this.sellBackValue = sellBackValue;
		this.minPopularityToPurchase = minPopularityToPurchase;
		setDescription();
	}
	/**
	 * Construct a new ShopItem using an array of ints for the last three values. Equivalent to <code>ShopItem(thingName, quantityCostAndRank[0], quantityCostAndRank[1], quantityCostAndRank[2])</code>
	 * Should only be called by ShopItemLoader
	 * @param thingName the name of the thing
	 * @param quantityCostAndRank an array where the first value is quantity, the second is cost, and the third is displayRank
	 * @param sendBackToShopWhenRemoved if set to true, signifies that this shop item should be sent back when it is removed by the user (even if not sold back)
	 * @param maxAllowed the maximum amount of these items allowed on the board at a time
	 * @param sellBackValue the amount to refund upon selling back
	 * @param minPopularityToPurchase the minimum popularity required to purchase this item
	 */
	ShopItem(final String thingName, final int[] quantityCostAndRank, final boolean sendBackToShopWhenRemoved, final int maxAllowed, final int sellBackValue, final int minPopularityToPurchase) {
		this(thingName, quantityCostAndRank[0], quantityCostAndRank[1], quantityCostAndRank[2], sendBackToShopWhenRemoved, maxAllowed, sellBackValue, minPopularityToPurchase);
	}
	/**
	 * Construct a new ShopItem using all the values of the passed in shopItem. Should only be called by ShopItemLoader
	 * @param shopItem the shop item to call
	 */
	protected ShopItem(final ShopItem shopItem) {
		this(shopItem.getThingName(), shopItem.getQuantity(), shopItem.getCost(), shopItem.getDisplayRank(), shopItem.shouldSendBackToShopWhenRemoved(), shopItem.getMaxAllowed(), shopItem.getSellBackValue(), shopItem.getMinPopularityToPurchase());
	}
	/**
	 * Returns the minimum popularity required to purchase this item
	 * @return the minimum popularity required to purchase this item
	 */
	public int getMinPopularityToPurchase() {
		return minPopularityToPurchase;
	}
	/**
	 * @return the thingName
	 */
	public String getThingName() {
		return thingName;
	}
	/**
	 * @param thingName the thingName to set
	 */
	public void setThingName(final String thingName) {
		this.thingName = thingName;
	}
	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}
	/**
	 * @param cost the cost to set
	 */
	public void setCost(final int cost) {
		this.cost = cost;
	}
	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}
	/**
	 * Return the string representing the quantity of this item
	 * @return the string representing the quantity of this item
	 */
	public String getDisplayQuantity() {
		return quantity == INFINITY ? GameUtils.infinitySymbol() : Integer.toString(quantity);
	}
	/**
	 * Sets the quantity of this ShopItem. If the shop items current quantity == INFINITY, then no change occurs
	 * @param quantity the quantity to set
	 */
	public void setQuantity(final int quantity) {
		if (this.quantity != INFINITY)
			this.quantity = quantity;
	}
	/**
	 * Returns the displayRank
	 * @return the displayRank
	 */
	public int getDisplayRank() {
		return displayRank;
	}
	/**
	 * Sets the displayRank
	 * @param displayRank the displayRank to set
	 */
	public void setDisplayRank(final int displayRank) {
		this.displayRank = displayRank;
	}
	/**
	 * Returns the image
	 * @return the image
	 */
	@Override
	public String getImage() {
		return image;
	}
	/**
	 * Sets the image
	 * @param image the image to set
	 */
	private void setImage() {
		this.image = ThingFactory.getInstance().getThingImage(thingName);
	}
	/**
	 * Increases the quantity of this shop item by one (if it is infinite, will stay at infinite)
	 */
	public void increaseQuantity() {
		setQuantity(quantity+1);
	}
	/**
	 * @return name/description of the associated thing
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescription();
	}
	/**
	 * Returns the description of the associated thing
	 * @return the description of the associated thing;
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * Sets the description using the description of the associated thing
	 */
	private void setDescription() {
		final StringBuilder sb = new StringBuilder(ThingFactory.getInstance().getThingDescription(thingName));
		if (getMinPopularityToPurchase() > 0) {
			sb.append("\n");
			sb.append("Requires at least " + getMinPopularityToPurchase() + " popularity to purchase");
		}
		this.description = sb.toString();
				
	}
	/**
	 * Return true if when this item is removed it should be sent back to the shop
	 * @return true if when this item is removed it should be sent back to the shop
	 */
	public boolean shouldSendBackToShopWhenRemoved() {
		return sendBackToShopWhenRemoved;
	}
	/**
	 * return true if you can place another of this shop item
	 * @param currentPresent the number that are current present
	 * @return true if there is no limit to number allowed or if currentPresent < maxAllowed
	 */
	public boolean allowedToPlaceAnother(final int currentPresent) {
		return maxAllowed == INFINITY || currentPresent < maxAllowed; 
	}
	/**
	 * Returns the maximum amount of these items that can be on the board at a time
	 * @return the maximum amount of these items that can be on the board at a time
	 */
	private int getMaxAllowed() {
		return maxAllowed;
	}
	/**
	 * @return DEFAULT if default sell back value (determined by board), it's sell back value otherwise
	 */
	public int getSellBackValue() {
		return sellBackValue;
	}
	/**
	 * Updates the cost, displayRank and sellBackValue, min popularity to purchase of this shop item to match the values of the provided shop item
	 * @param reference the reference shop item
	 */
	public void updateValues(final ShopItem reference) {
		if (!reference.getThingName().equals(getThingName()))
			throw new IllegalArgumentException("Invalid ShopItem reference " + reference.getThingName() + " must refer to same thing (" +  getThingName() + ")");
		cost = reference.cost;
		displayRank = reference.displayRank;
		sellBackValue = reference.sellBackValue;
		minPopularityToPurchase = reference.getMinPopularityToPurchase();
		setDescription();
	}
	

}
