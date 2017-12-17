package gameutils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class GameUtils {
	private GameUtils() {
		
	}
	/**
	 * @param percentChance the percent chance of an event occuring
	 * @return whether or not that event occurs
	 */
	public static boolean testPercentChance(double percentChance) {
		
				double randomNum = ThreadLocalRandom.current().nextDouble(1, 100); //num between 1, 100
				if (randomNum > (100-percentChance))
					return true;
			
			return false;
		
	}
	/**
	 * @param list1 the first list to unionize
	 * @param list2 the second list to unionize
	 * @return the union (that is the concatenation) of the two lists
	 */
	public static <E> List<E> union(final List<? extends E> list1, final List<? extends E> list2) {
		final ArrayList<E> result = new ArrayList<E>(list1);
		result.addAll(list2);
		return result;
	}
	
}
