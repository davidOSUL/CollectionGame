package loaders.shopLoader;

import java.io.Serializable;

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
	/**
	 * Construct a new ShopItem with the given paramter
	 * @param thingName the thingName
	 * @param initialQuantity the quantity
	 * @param cost the cost
	 * @param displayRank the order that the ShopItem should be displayed relative to other shopItems (1 first, 2 second, etc.)
	 */
	protected ShopItem(String thingName, int initialQuantity, int cost, int displayRank) {
		this.setThingName(thingName);
		this.setQuantity(initialQuantity);
		this.setCost(cost);
		this.setDisplayRank(displayRank);
		setImage();
		setDescription();
	}
	/**
	 * Construct a new ShopItem using an array of ints for the last three values. Equivalent to <code>ShopItem(thingName, quantityCostAndRank[0], quantityCostAndRank[1], quantityCostAndRank[2])</code>
	 * Should only be called by ShopItemLoader
	 * @param thingName the name of the thing
	 * @param quantityCostAndRank an array where the first value is quantity, the second is cost, and the third is displayRank
	 */
	protected ShopItem(String thingName, int[] quantityCostAndRank) {
		this(thingName, quantityCostAndRank[0], quantityCostAndRank[1], quantityCostAndRank[2]);
	}
	/**
	 * Construct a new ShopItem using all the values of the passed in shopItem. Should only be called by ShopItemLoader
	 * @param shopItem the shop item to call
	 */
	protected ShopItem(ShopItem shopItem) {
		this(shopItem.getThingName(), shopItem.getQuantity(), shopItem.getCost(), shopItem.getDisplayRank());
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
	public void setThingName(String thingName) {
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
	public void setCost(int cost) {
		this.cost = cost;
	}
	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
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
	public void setDisplayRank(int displayRank) {
		this.displayRank = displayRank;
	}
	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	private void setImage() {
		this.image = ThingLoader.sharedInstance().getThingImage(thingName);
	}
	public void increaseQuantity() {
		quantity++;
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
	

}
