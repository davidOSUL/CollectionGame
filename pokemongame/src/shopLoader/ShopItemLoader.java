package shopLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import gameutils.GameUtils;
import loaders.CSVReader;
import loaders.ThingLoader;
import thingFramework.Thing;

public class ShopItemLoader {
	private static final String SHOP_ITEM_PATH = "/InputFiles/shopItems.csv";
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
	private Map<String, ShopItem> shopItemMap = new HashMap<String, ShopItem>();
	private ShopItemLoader() {
		load(SHOP_ITEM_PATH);
		shopItems = Collections.unmodifiableSet(shopItems);
		initialShopItems = Collections.unmodifiableSet(initialShopItems);
	}
	public static ShopItemLoader sharedInstance() {
		return INSTANCE;
	}
	public ShopItem generateShopItem(String name) {
		return new ShopItem(shopItemMap.get(name));
	}
	public Thing generateNewThing(String name) {
		if (!hasShopItem(name))
			throw new IllegalArgumentException(name + " not a valid shop item");
		return ThingLoader.sharedInstance().generateNewThing(name);
	}
	public boolean hasShopItem(String name) {
		return shopItemMap.containsKey(name);
	}
	public boolean hasShopItem(ShopItem item) {
		return shopItems.contains(item);
	}
	public Set<ShopItem> generateInitialShopItems() {
		Set<ShopItem> generatedItems = new HashSet<ShopItem>();
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
	private void load(String path) {
		try {
			for (String[] values : CSVReader.readCSV(path, true)) {
				int[] quantityCostAndRank = GameUtils.parseAllInRangeToInt(values, 1, 3);
				if (!ThingLoader.sharedInstance().hasThing(values[0])) 
					throw new RuntimeException("attempted to parse ShopItem with Thing Name: " + values[0] + " which does not have a corresponding thing");
				ShopItem item = new ShopItem(values[0], quantityCostAndRank);
				shopItemMap.put(values[0], item);
				shopItems.add(item);
				if (values[4].equalsIgnoreCase("yes"))
					initialShopItems.add(item);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
