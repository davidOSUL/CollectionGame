package loaders.shopLoader;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gameutils.GameUtils;
import loaders.CSVReader;
import loaders.ThingFactory;
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
	private static final int NAME = 0;
	private static final int QUANTITY = 1;
	private static final int COST = 2;
	private static final int RANK = 3;
	private static final int DEFAULT = 4;
	private static final int REMOVE_ON_DELETE = 5;
	private static final int MAX_ALLOWED = 6;
	private static final int SELL_BACK_VAL = 7;
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
	/**
	 * Returns the ShopItemLoader instance
	 * @return  the ShopItemLoader instance
	 */
	public static ShopItemLoader sharedInstance() {
		return INSTANCE;
	}
	/**
	 * Generates a ShopItem of the specified name
	 * @param name the name of the ShopItem
	 * @return the generated ShopItem
	 */
	public ShopItem generateShopItem(final String name) {
		return new ShopItem(shopItemMap.get(name));
	}
	/**
	 * Generates a new Thing of the specified name (uses {@link loaders.ThingFactory#generateNewThing(String)}) 
	 * @param name the name of the thing to generate
	 * @return the generated thing
	 * @throws IllegalArgumentException if the thing is not associated with a valid shop item
	 */
	public Thing generateNewThing(final String name) {
		if (!hasShopItem(name))
			throw new IllegalArgumentException(name + " not a valid shop item");
		return ThingFactory.getInstance().generateNewThing(name);
	}
	/**
	 * Return true if the provided name is a ShopItem that can be generated
	 * @param name the name of the shop item
	 * @return true if the provided name is a ShopItem that can be generated
	 */
	public boolean hasShopItem(final String name) {
		return shopItemMap.containsKey(name);
	}
	/**
	 * Return true if the provided ShopItem is a ShopItem that can be generated
	 * @param item the item 
	 * @return true if the provided ShopItem is a ShopItem that can be generated
	 */
	public boolean hasShopItem(final ShopItem item) {
		return shopItems.contains(item);
	}
	/**
	 * Generates the set of new ShopItems that a Shop should start with
	 * @return a Set of generated ShopItems 
	 */
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
					quantityCostAndRank = GameUtils.parseAllInRangeToInt(values, QUANTITY, RANK); //get range from quantity->cost->rank
				else {
					final int[] costAndRank = GameUtils.parseAllInRangeToInt(values, COST, RANK);
					quantityCostAndRank = new int[]{ShopItem.INFINITY, costAndRank[0], costAndRank[1]};
				}
				if (!ThingFactory.getInstance().isCreatableThing(values[NAME])) 
					throw new RuntimeException("attempted to parse ShopItem with Thing Name: " + values[NAME] + " which does not have a corresponding thing");
				final boolean removeWhenDeleted = values[REMOVE_ON_DELETE].equalsIgnoreCase("yes");
				final int maxAllowed= values[MAX_ALLOWED].equalsIgnoreCase("no limit") ? ShopItem.INFINITY :Integer.parseInt(values[MAX_ALLOWED]);
				final int sellBackVal = values[SELL_BACK_VAL].equalsIgnoreCase("default") ? ShopItem.DEFAULT : Integer.parseInt(values[SELL_BACK_VAL]);
				final ShopItem item = new ShopItem(values[NAME], quantityCostAndRank, removeWhenDeleted, maxAllowed, sellBackVal);
				shopItemMap.put(values[NAME], item);
				shopItems.add(item);
				if (values[DEFAULT].equalsIgnoreCase("yes"))
					initialShopItems.add(item);
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
