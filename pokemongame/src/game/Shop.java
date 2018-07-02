package game;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import shopLoader.ShopItem;
import shopLoader.ShopItemLoader;
import thingFramework.Thing;

//TODO
public class Shop implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Map between the name of an item and the corresponding shop item
	 */
	private Map<String, ShopItem> itemsInShop = new HashMap<String, ShopItem>();
	/**
	 * Mantains priorty queue of all values in "itemsInShop" map, sorted by displayrank
	 */
	private Set<ShopItem> itemsInOrder;
	/**
	 * Creates a new Shop with the default initial itmes from ShopItemLoader added
	 */
	public Shop() {
		ShopItemLoader.sharedInstance().generateInitialShopItems().forEach(shopItem -> itemsInShop.put(shopItem.getThingName(), shopItem));
		itemsInOrder = new TreeSet<ShopItem>(new Comparator<ShopItem>() {
		    @Override
		    public int compare(ShopItem s1, ShopItem s2) {
		        return Integer.compare(s1.getDisplayRank(), s2.getDisplayRank());
		    }
		});
		itemsInOrder.addAll(itemsInShop.values());
	}
	/**
	 * @return a queue sorted in order of the shop items display rank
	 */
	public Set<ShopItem> itemsInOrder() {
		return itemsInOrder;
	}
	/**
	 * Adds a new ShopItem to the shop with quantity one if not present. Or if present increases its quantity by 1
	 * @param name the name of the shop item to add
	 */
	public void addToShopStock(String name) {
		if (!ShopItemLoader.sharedInstance().hasShopItem(name))
			throw new IllegalArgumentException(name + " is not a valid shop item");
		if (itemsInShop.containsKey(name)) {
			itemsInShop.get(name).increaseQuantity();
			return;
		}
		ShopItem si = ShopItemLoader.sharedInstance().generateShopItem(name);
		si.setQuantity(1);
		itemsInShop.put(name, si);
		itemsInOrder.add(si);
		

	}
	
	private void throwIfNotPresent(String name) {
		if (!hasThingForPurchase(name))
			throw new IllegalArgumentException(name + " is not currently in the shop");

	}
	/**
	 * @param name the ShopItem
	 * @return true if exists in shop
	 */
	public boolean hasThingForPurchase(String name) {
		return itemsInShop.containsKey(name);
	}
	/**
	 * Create a new Thing for the given name and update the shop quantity
	 * @param name the Name of the shop item
	 * @return the thing
	 */
	public Thing purchase(String name) {
		throwIfNotPresent(name);
		Thing thing = ShopItemLoader.sharedInstance().generateNewThing(name);
		removeOne(name);
		return thing;
	}
	public Thing purchase(ShopItem item) {
		if (!itemsInOrder.contains(item))
			throw new IllegalArgumentException(item.getThingName() + " is not currently in the shop");
		return purchase(item.getThingName());
	}
	public Thing getThingCopy(ShopItem item) {
		return ShopItemLoader.sharedInstance().generateNewThing(item.getThingName());
	}
	public int getCost(String name) {
		throwIfNotPresent(name);
		return itemsInShop.get(name).getCost();
	}
	private void updateQuantity(String name, int newQuantity) {
		if (newQuantity <= 0) {
			itemsInOrder.remove(itemsInShop.get(name));
			itemsInShop.remove(name);
		}
		else
			itemsInShop.get(name).setQuantity(newQuantity);
	}
	private void removeOne(String name) {
		updateQuantity(name, itemsInShop.get(name).getQuantity()-1);
	}

}
