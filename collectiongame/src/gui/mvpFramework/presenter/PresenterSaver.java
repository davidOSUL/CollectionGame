package gui.mvpFramework.presenter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import gui.gameComponents.grid.GridSpace;
import gui.gameComponents.grid.GridSpace.GridSpaceData;
import gui.mvpFramework.view.ViewInterface;
import loaders.shopLoader.ShopItem;
import thingFramework.Thing;

/**
 * Utility class for a Presenter to save all of it's mapped data upon Serialization
 * @author David O'Sullivan
 *
 */
final class PresenterSaver {
	/**
	 * Writes the provided maps which represent the Presenter's stored data to the ObjectOutputStream
	 * @param oos the ObjectOutputStream to write to 
	 * @param allThings the map of all the Things that the Presenter manages
	 * @param soldThings the map of all the Sold Things that the Presenter manages
	 * @throws IOException
	 */
	static void writePresenterMaps(final ObjectOutputStream oos, final Map<GridSpace, Thing> allThings, final Map<GridSpace, ShopItem> soldThings) throws IOException {
		final Map<GridSpaceData, Thing> allThingsData = new LinkedHashMap<GridSpaceData, Thing>();
		final Map<Integer, ShopItem> soldThingsData = new LinkedHashMap<Integer, ShopItem>();
		final Iterator<Entry<GridSpace, Thing>> it = allThings.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			final Map.Entry<GridSpace, Thing> pair = it.next();
			final GridSpace gs = pair.getKey();
			final Thing t = pair.getValue();
			if (soldThings.containsKey(gs)) {
				soldThingsData.put(i, soldThings.get(gs));
			}
			allThingsData.put(gs.getData(), t);
			i++;
		}
		oos.writeObject(allThingsData);
		oos.writeObject(soldThingsData);
	}
	/**
	 * @param ois the ObjectInputStream to read from
	 * @param allThings the map of all Things that should be added to
	 * @param soldThings the map of sold things that should be added to
	 * @param presenter the presenter to read the save to
	 * @param view the view that the presenter is using
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@SuppressWarnings("unchecked")
	static void readPresenterMaps(final ObjectInputStream ois,final Map<GridSpace, Thing> allThings, final Map<GridSpace, ShopItem> soldThings, final Presenter presenter, final ViewInterface view) throws ClassNotFoundException, IOException {
		final Map<GridSpaceData, Thing> allThingsData = (Map<GridSpaceData, Thing>) ois.readObject();
		final Map<Integer, ShopItem> soldThingsData = (Map<Integer, ShopItem>) ois.readObject();
		final Iterator<Entry<GridSpaceData, Thing>> it = allThingsData.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			final Map.Entry<GridSpaceData, Thing> pair = it.next();
			final GridSpaceData data = pair.getKey();
			final Thing thing = pair.getValue();
			it.remove(); 
			final GridSpace gridSpace = view.addNewGridSpaceFromSave(presenter.generateGameSpaceFromThing(thing), data);
			allThings.put(gridSpace, thing);
			if (soldThingsData.containsKey(i))
				soldThings.put(gridSpace, soldThingsData.get(i));
			presenter.updateListener(gridSpace);
			i++;
		}
	}
}
