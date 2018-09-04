package attributes;

import java.util.EnumSet;

/**
 * Methods to be implemented by Attribute that determine how the Attribute should be displayed on the gui
 * @author David O'Sullivan
 *
 */
public interface DisplayMethods {
	/**
	 * Returns whether or not this Attribute should be displayed
	 * @return true if this attribute should be displayed, false otherwise
	 * @default false
	 */
	public default boolean shouldDisplay() {
		return false;
	}
	/**
	 * Returns the rank of display for this attribute. Attributes with lesser rank want to be displayed earlier
	 * @return the display rank for this attribute
	 * @default -1
	 */
	public default int getDisplayRank() {
		return -1;
	}
	/**
	 * Gets this attribute as a "display string." That is a string that represents this attribute
	 * and can be put into a gramatically correct sentence.
	 * This generally means reversing the order of value/name.
	 * For example instead of $/hour: +5, it would be +5 $/hour
	 * @param settings any number of settings for how the Attribute should be displayed
	 * @return the display string
	 * @default calls toString()
	 */
	public default String getDisplayString(final EnumSet<DisplayStringSetting> settings) {
		return toString();
	}
	/**
	 * Gets this attribute as a "display string." That is a string that represents this attribute
	 * and can be put into a gramatically correct sentence.
	 * This generally means reversing the order of value/name.
	 * For example instead of $/hour: +5, it would be +5 $/hour
	 * @return the display string
	 * @default calls getDisplayString with no settings.
	 */
	public default String getDisplayString() {
		return getDisplayString(EnumSet.noneOf(DisplayStringSetting.class));
	}
}
