package gameutils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * General purpose utility functions (generally used by backend only)
 * @author David O'Sullivan
 *
 */
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
	public static int[] parseAllInRangeToInt(final String[] input, final int LowerIndex, final int UpperIndex) {
		final int[] output = new int[UpperIndex-LowerIndex+1];
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
	public static boolean testPercentChance(final double percentChance) {
		
				final double randomNum = ThreadLocalRandom.current().nextDouble(0, 100); //num between [0, 100)
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
	public static int roundToMultiple(final int val, final int MULT) {
		return val < MULT ? MULT : ((val + MULT-1) / MULT)*MULT;
	}
	/**
	 * Returns a new set consisting of the passed in list with all the duplicates removed (i.e. {a,a,b,b,c,c,c,d} -> {a,b,c,d})
	 * @param list the list to remove duplicates from
	 * @return the new list
	 */
	public static <T> List<T> removeDuplicateElementsOfList(final List<? extends T> list) {
		return list.stream().distinct().collect(Collectors.toList());
	}
	/**
	 * Removes one occurence of each uniqiue element in the list (i.e. {a,a,b,b,c,c,c,d} -> {a,b,c,c}, and then returns
	 * the set of unique elements (the items that were removed) (in this case {a,b,c,d})
	 * @param list the list to remove items from.
	 * @return Returns the list of items that was removed
	 */
	public static <T> List<T> removeOneInstanceOfEachElement(final List<T> list) {
		final List<T> uniqueList = removeDuplicateElementsOfList(list);
		for (final T item : uniqueList) {
			list.remove(item);
		}
		return uniqueList;
	}
	public static <T> ArrayList<T> toArrayList(final T[] elements) {
		final ArrayList<T> newList = new ArrayList<T>();
		for (final T t: elements) {
			newList.add(t);
		}
		return newList;
		
	}
	/**
	 * Converts the given string to "Title Case" ("Each Word Is Capitalized Like This")
	 * @param givenString the input
	 * @return the Title cased string
	 */
	public static String toTitleCase(final String givenString) {
	    final String[] arr = givenString.split(" ");
	    final StringBuffer sb = new StringBuffer();

	    for (final String element : arr) {
	        sb.append(Character.toUpperCase(element.charAt(0)))
	            .append(element.substring(1)).append(" ");
	    }          
	    return sb.toString().trim();
	}  
	/**
	 * Converts the given milliseconds to a display time (e.g. 3:54)
	 * @param milliseconds the milliseconds 
	 * @return the milliseconds as a displayed string
	 */
	public static String millisecondsToTime(final long milliseconds) {
	    if (milliseconds < 0)
	    	return "0:00";
		final long minutes = (milliseconds / 1000) / 60;
	    final long seconds = (milliseconds / 1000) % 60;
	    final String secondsStr = Long.toString(seconds);
	    String secs;
	    if (secondsStr.length() >= 2) {
	        secs = secondsStr.substring(0, 2);
	    } else {
	        secs = "0" + secondsStr;
	    }

	    return minutes + ":" + secs;
	}
	/**
	 * returns given milliseconds to verbally described time, providing as little info as possible.
	 * (e.g. 1:30 is "1 minute and 30 seconds". :34 is "34 seconds" and 3:00 is "3 minutes"
	 * @param milliseconds
	 * @return
	 */
	public static String millisecondsToWrittenOutTime(final long milliseconds) {
		if (milliseconds < 0)
	    	return "0 minutes";
		final long minutes = (milliseconds / 1000) / 60;
	    final long seconds = (milliseconds / 1000) % 60;
	    final String secondsStr = Long.toString(seconds);
	    String secs;
	    if (secondsStr.length() >= 2) {
	        secs = secondsStr.substring(0, 2);
	    }
	    else
	    	secs = secondsStr;
	    if (seconds == 0)
	    	return minutes + " minutes";
	    if (minutes == 0)
	    	return secs + " seconds";
	    return minutes + " minutes and " + secs + " seconds";
	    			
	}
	public static String infinitySymbol() {
		String infinitySymbol;

		try {

		    infinitySymbol = new String(String.valueOf(Character.toString('\u221E')).getBytes("UTF-8"), "UTF-8");

		} catch (final UnsupportedEncodingException ex) {

		    infinitySymbol = "inf";
		    //ex.printStackTrace(); //print the unsupported encoding exception.

		} 
		return infinitySymbol;
	}
	
}
