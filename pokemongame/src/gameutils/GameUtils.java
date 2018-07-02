package gameutils;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class GameUtils {
	private GameUtils() {
		
	}
	/**
	 * Returns a new array where the first index is Integer.parseInt(input[LowerIndex]), the last is Integer.parseInt(input[UpperIndex]),
	 * and in between is all the parsed values of the the values between those indices
	 * @param input the string array to parse
	 * @param LowerIndex the first index of the array to parse
	 * @param UpperIndex the last index of the array to the pase
	 * @return the parsed int array corresponding to the values between LowerIndex and UpperINdex
	 */
	public static int[] parseAllInRangeToInt(String[] input, int LowerIndex, int UpperIndex) {
		int[] output = new int[UpperIndex-LowerIndex+1];
		int j = 0;
		for (int i = LowerIndex; i <= UpperIndex; i++) {
			output[j++] = Integer.parseInt(input[i]);
		}
		return output;
	}
	/**
	 * @param percentChance the percent chance of an event occuring
	 * @return whether or not that event occurs
	 */
	public static boolean testPercentChance(double percentChance) {
		
				double randomNum = ThreadLocalRandom.current().nextDouble(0, 100); //num between [0, 100)
				if (randomNum >= (100-percentChance))
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
	/**
	 * @param val the value to round
	 * @param MULT the multiple to round to
	 * @return val rounded up to a multiple of MULT
	 */
	public static int roundToMultiple(int val, int MULT) {
		return val < MULT ? MULT : ((val + MULT-1) / MULT)*MULT;
	}
	/**
	 * Returns a new set consisting of the passed in list with all the duplicates removed (i.e. {a,a,b,b,c,c,c,d} -> {a,b,c,d})
	 * @param list the list to remove duplicates from
	 * @return the new list
	 */
	public static <T> List<T> removeDuplicateElementsOfList(List<? extends T> list) {
		return list.stream().distinct().collect(Collectors.toList());
	}
	/**
	 * Removes one occurence of each uniqiue element in the list (i.e. {a,a,b,b,c,c,c,d} -> {a,b,c,c}, and then returns
	 * the set of unique elements (the items that were removed) (in this case {a,b,c,d})
	 * @param list the list to remove items from.
	 * @return Returns the list of items that was removed
	 */
	public static <T> List<T> removeOneInstanceOfEachElement(List<T> list) {
		List<T> uniqueList = removeDuplicateElementsOfList(list);
		for (T item : uniqueList) {
			list.remove(item);
		}
		return uniqueList;
	}
	public static <T> ArrayList<T> toArrayList(T[] elements) {
		ArrayList<T> newList = new ArrayList<T>();
		for (T t: elements) {
			newList.add(t);
		}
		return newList;
		
	}
	
}
