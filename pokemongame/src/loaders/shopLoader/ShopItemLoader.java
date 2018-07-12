package loaders.shopLoader;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gameutils.GameUtils;
import loaders.CSVReader;
import loaders.ThingLoader;
import thingFramework.Thing;

/**
 * Loads in all shop items into the shop. THis will also update the shop, adding/removing items if new ones
 * are created (or removed)
 * @author David O'Sullivan
 *
 */
public class ShopItemLoader {
	private static final String SHOP_ITEM_PATH = "/InputFiles/shopItems - 1.csv";
	private static final ShopItemLoader INSTANCE = new ShopItemLoader();
	/**
	 * The set of all possible items for the shop to have
	 */
	private Set<ShopItem> shopItems = new HashSet<ShopItem>();
	/**
	 * What shop items the Shop should start with
	 */
	private Set<ShopItem> initialShopItems = new HashSet<ShopItem>();
	/**
	 * Map between shop item names and the shop item
	 */
	private final Map<String, ShopItem> shopItemMap = new HashMap<String, ShopItem>();
	private ShopItemLoader() {
		load(SHOP_ITEM_PATH);
		shopItems = Collections.unmodifiableSet(shopItems);
		initialShopItems = Collections.unmodifiableSet(initialShopItems);
	}
	public static ShopItemLoader sharedInstance() {
		return INSTANCE;
	}
	public ShopItem generateShopItem(final String name) {
		return new ShopItem(shopItemMap.get(name));
	}
	public Thing generateNewThing(final String name) {
		if (!hasShopItem(name))
			throw new IllegalArgumentException(name + " not a valid shop item");
		return ThingLoader.sharedInstance().generateNewThing(name);
	}
	public boolean hasShopItem(final String name) {
		return shopItemMap.containsKey(name);
	}
	public boolean hasShopItem(final ShopItem item) {
		return shopItems.contains(item);
	}
	public Set<ShopItem> generateInitialShopItems() {
		final Set<ShopItem> generatedItems = new HashSet<ShopItem>();
		initialShopItems.forEach(x -> generatedItems.add(generateShopItem(x.getThingName())));
		return generatedItems;
	}
	/**Loads in shop items from specified path 
	 *<br> Assumes input of form </br>
	 *<br> First Line with table headers </br>
	 *<br> ThingName, InitialQuantity, Cost, Rank, Default? </br>
	 *<br> ThingName, InitialQuantity, Cost, Rank, Default? </br>
	 *<br> ThingName, InitialQuantity, Cost, Rank, Default? </br>
	 *<br> ... </br>
	 * @param path the path to the CSV with the shop items
	 */
	private void load(final String path) {
		try {
			for (final String[] values : CSVReader.readCSV(path, true)) {
				final int[] quantityCostAndRank;
				if (!values[1].equalsIgnoreCase("infinite"))
					quantityCostAndRank = GameUtils.parseAllInRangeToInt(values, 1, 3);
				else {
					final int[] costAndRank = GameUtils.parseAllInRangeToInt(values, 2, 3);
					quantityCostAndRank = new int[]{ShopItem.INFINITY, costAndRank[0], costAndRank[1]};
				}
				if (!ThingLoader.sharedInstance().hasThing(values[0])) 
					throw new RuntimeException("attempted to parse ShopItem with Thing Name: " + values[0] + " which does not have a corresponding thing");
				final boolean removeWhenDeleted = values[4].equalsIgnoreCase("yes");
				final int maxAllowed;
				maxAllowed = values[6].equalsIgnoreCase("no limit") ? ShopItem.INFINITY :Integer.parseInt(values[6]);
				final ShopItem item = new ShopItem(values[0], quantityCostAndRank, removeWhenDeleted, maxAllowed);
				shopItemMap.put(values[0], item);
				shopItems.add(item);
				if (values[4].equalsIgnoreCase("yes"))
					initialShopItems.add(item);
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
