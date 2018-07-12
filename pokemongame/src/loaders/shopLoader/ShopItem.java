package loaders.shopLoader;

import java.io.Serializable;

import gameutils.GameUtils;
import interfaces.Imagable;
import loaders.ThingLoader;

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
	private final int sellBackValue;
	public static final int INFINITY = -1;
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
	 */
	protected ShopItem(final String thingName, final int initialQuantity, final int cost, final int displayRank, final boolean sendBackToShopWhenRemoved, final int maxAllowed, final int sellBackValue) {
		this.setThingName(thingName);
		this.setQuantity(initialQuantity);
		this.setCost(cost);
		this.setDisplayRank(displayRank);
		setImage();
		setDescription();
		this.sendBackToShopWhenRemoved = sendBackToShopWhenRemoved;
		this.maxAllowed = maxAllowed;
		this.sellBackValue = sellBackValue;
	}
	/**
	 * Construct a new ShopItem using an array of ints for the last three values. Equivalent to <code>ShopItem(thingName, quantityCostAndRank[0], quantityCostAndRank[1], quantityCostAndRank[2])</code>
	 * Should only be called by ShopItemLoader
	 * @param thingName the name of the thing
	 * @param quantityCostAndRank an array where the first value is quantity, the second is cost, and the third is displayRank
	 * @param sendBackToShopWhenRemoved if set to true, signifies that this shop item should be sent back when it is removed by the user (even if not sold back)
	 * @param maxAllowed the maximum amount of these items allowed on the board at a time
	 * @param sellBackValue the amount to refund upon selling back
	 */
	protected ShopItem(final String thingName, final int[] quantityCostAndRank, final boolean sendBackToShopWhenRemoved, final int maxAllowed, final int sellBackValue) {
		this(thingName, quantityCostAndRank[0], quantityCostAndRank[1], quantityCostAndRank[2], sendBackToShopWhenRemoved, maxAllowed, sellBackValue);
	}
	/**
	 * Construct a new ShopItem using all the values of the passed in shopItem. Should only be called by ShopItemLoader
	 * @param shopItem the shop item to call
	 */
	protected ShopItem(final ShopItem shopItem) {
		this(shopItem.getThingName(), shopItem.getQuantity(), shopItem.getCost(), shopItem.getDisplayRank(), shopItem.shouldSendBackToShopWhenRemoved(), shopItem.getMaxAllowed(), shopItem.getSellBackValue());
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
	public String getDisplayQuantity() {
		return quantity == INFINITY ? GameUtils.infinitySymbol() : Integer.toString(quantity);
	}
	/**
	 * @param sets the quantity of this shopitem. If the shop items current quantity == INFINITY, then no change occurs
	 */
	public void setQuantity(final int quantity) {
		if (this.quantity != INFINITY)
			this.quantity = quantity;
	}
	/**
	 * @return the displayRank
	 */
	public int getDisplayRank() {
		return displayRank;
	}
	/**
	 * @param displayRank the displayRank to set
	 */
	public void setDisplayRank(final int displayRank) {
		this.displayRank = displayRank;
	}
	/**
	 * @return the image
	 */
	@Override
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	private void setImage() {
		this.image = ThingLoader.sharedInstance().getThingImage(thingName);
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
	 * @return name/description of the associated thing;
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	private void setDescription() {
		this.description = ThingLoader.sharedInstance().getThingDescription(thingName);
	}
	/**
	 * @return the sendBackToShopWhenRemoved
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
	private int getMaxAllowed() {
		return maxAllowed;
	}
	/**
	 * @return DEFAULT if default sell back value (determined by board), it's sell back value otherwise
	 */
	public int getSellBackValue() {
		return sellBackValue;
	}
	

}
