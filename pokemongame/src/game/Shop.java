package game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import loaders.shopLoader.ShopItem;
import loaders.shopLoader.ShopItemLoader;
import thingFramework.Thing;

/**
 * Contains the data for the shop. This includes quantity, cost, available items, etc.
 * @author David O'Sullivan
 *
 */
public class Shop implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Set<String> itemsInitiallyInShop;
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
		itemsInOrder = new TreeSet<ShopItem>(ItemComparator.INSTANCE);
		itemsInOrder.addAll(itemsInShop.values());
		itemsInitiallyInShop = new HashSet<String>();
		itemsInitiallyInShop.addAll(itemsInShop.keySet());
	}
	/**
	 *Upon DeSerilization, this function is called by the JVM. This will allow us to update the shop inventory with new items in the future, keeping the old items
	 *and their quantitities/prices the same
	 */
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		//default serilization
		ois.defaultReadObject();		
	}
	/**
	 * Will remove any items from the shop that aren't present in the shopItems list any more and also add any new ones that are. 
	 */
	public void checkForShopUpdates() {
		ShopItemLoader.sharedInstance().generateInitialShopItems().forEach(shopItem -> { //TODO: Put this in a method "start of session" along with more stuff from board
			String name = shopItem.getThingName();
			if (!itemsInitiallyInShop.contains(name)) {
				itemsInitiallyInShop.add(name);
				itemsInShop.put(name, shopItem);
				itemsInOrder.add(shopItem);
			}
		});
		Iterator<String> it = itemsInitiallyInShop.iterator();
		while (it.hasNext()) {
			String name = it.next();
			if (!ShopItemLoader.sharedInstance().hasShopItem(name)) {
				it.remove();
				if (itemsInShop.containsKey(name)) {
					itemsInOrder.remove(itemsInShop.get(name));
					itemsInShop.remove(name);
				}
				
			}
		}
	}
	/**
	 * @return a queue sorted in order of the shop items display rank
	 */
	public Set<ShopItem> itemsInOrder() {
		return itemsInOrder;
	}
	/**
	 * @param name the name of the shop item
	 * @return true if that name corresponds to a valid shop item
	 */
	public boolean isValidShopItem(String name) {
		return ShopItemLoader.sharedInstance().hasShopItem(name);
	}
	/**
	 * Adds a new ShopItem to the shop with quantity one if not present. Or if present increases its quantity by 1
	 * @param name the name of the shop item to add
	 */
	public void addToShopStock(String name) {
		if (!isValidShopItem(name))
			throw new IllegalArgumentException(name + " is not a valid shop item"); //TODO: add warning that they won't be able to repurchase
		if (itemsInShop.containsKey(name)) {
			itemsInShop.get(name).increaseQuantity();
			return;
		}
		ShopItem si = ShopItemLoader.sharedInstance().generateShopItem(name);
		si.setQuantity(1);
		itemsInShop.put(name, si);
		itemsInOrder.add(si);


	}

	/**
	 * Throws an error if the shop item is <b>currently</b> not present in the shop (e.g. will throw an error for a valid shop itemn if it's
	 * not for sale right now)
	 * @param name
	 */
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
	private enum ItemComparator implements Comparator<ShopItem> {
		INSTANCE;
		@Override
		public int compare(ShopItem s1, ShopItem s2) {
			return Integer.compare(s1.getDisplayRank(), s2.getDisplayRank());
		}
	}

}
