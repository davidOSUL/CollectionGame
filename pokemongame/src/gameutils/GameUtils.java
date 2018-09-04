package gameutils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
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
	 * @param <E> the type of the list
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
	 * @param <T> the type of the list
	 * @param list the list to remove duplicates from
	 * @return the new list
	 */
	public static <T> List<T> removeDuplicateElementsOfList(final List<? extends T> list) {
		return list.stream().distinct().collect(Collectors.toList());
	}
	/**
	 * Removes one occurence of each uniqiue element in the list (i.e. {a,a,b,b,c,c,c,d} -> {a,b,c,c}, and then returns
	 * the set of unique elements (the items that were removed) (in this case {a,b,c,d})
	 * @param <T> the type of the list
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
	/**
	 * Converts an array to a mutable array list
	 * @param <T> the type of the array
	 * @param elements the array
	 * @return the new ArrayList with the provided elements
	 */
	public static <T> ArrayList<T> toArrayList(final T[] elements) {
		final ArrayList<T> newList = new ArrayList<T>();
		for (final T t: elements) {
			newList.add(t);
		}
		return newList;

	}
	/**
	 * Converts the provided element to a mutable array list
	 * @param <T> the type of the array
	 * @param element the element to populate the array list with
	 * @return a new array list with the provided element
	 */
	public static <T> ArrayList<T> toArrayList(final T element) {
		final ArrayList<T> newList = new ArrayList<T>();
		newList.add(element);
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
			if (element.length() > 0) {
				sb.append(Character.toUpperCase(element.charAt(0)))
				.append(element.substring(1)).append(" ");
			}

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
		final String minutesString = minutes == 1 ? " minute" : " minutes";
		final long seconds = (milliseconds / 1000) % 60;
		final String secondsStr = Long.toString(seconds);
		String secs;
		if (secondsStr.length() >= 2) {
			secs = secondsStr.substring(0, 2);
		}
		else
			secs = secondsStr;
		if (seconds == 0) {
			return minutes + minutesString;
		}
		if (minutes == 0)
			return secs + " seconds";
		return minutes + minutesString + " and " + secs + " seconds";

	}
	/**
	 * returns given minutes to verbally described time, providing as little info as possible.
	 * (e.g. 1:30 is "1 minute and 30 seconds". :34 is "34 seconds" and 3:00 is "3 minutes"
	 * @param minutes the amount of minutes
	 * @return the formatted string
	 */
	/**
	 * @param minutes
	 * @return
	 */
	public static String minutesToWrittenOutTime(final double minutes) {
		return millisecondsToWrittenOutTime(minutesToMillis(minutes));
	}
	/**
	 * Returns the unicode infinity symbol if the computer supports it, "inf" otherwise
	 * @return the infinity symbol
	 */
	public static String infinitySymbol() {
		String infinitySymbol;

		try {

			infinitySymbol = new String(String.valueOf(Character.toString('\u221E')).getBytes("UTF-8"), "UTF-8");

		} catch (final UnsupportedEncodingException ex) {

			infinitySymbol = "inf";

		} 
		return infinitySymbol;
	}
	/**
	 * Converts Minutes to milliseconds
	 * @param d the amount of minutes
	 * @return that minutes as milliseconds
	 */
	public static long minutesToMillis(final double d) {
		return (long) (d*60000);
	}
	/**
	 * Takes a given amount of milliseconds and gives the amount milliseconds
	 * @param x the milliseconds
	 * @return the minutes
	 */
	public static double millisAsMinutes(final long x) {
		double y = x/1000.0;
		y=y/60.0;
		return y;
	}
	/**
	 * Converts an array of enums to a an enumset of those enums
	 * @param <T> the type of the enum
	 * @param vals the enum array
	 * @param clazz the class of the enum
	 * @return the EnumSet of that array
	 */
	public static <T extends Enum<T>> EnumSet<T> arrayToEnumSet(final T[] vals, final Class<T> clazz) {
		final EnumSet<T> newSet = EnumSet.noneOf(clazz);
		newSet.addAll(Arrays.asList(vals));
		return newSet;
	}

}
