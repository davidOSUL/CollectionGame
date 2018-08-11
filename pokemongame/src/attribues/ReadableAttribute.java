package attribues;

import java.util.EnumSet;

public class ReadableAttribute<T> extends Attribute<T> {
	private String displayName;
	private boolean isVisible;
	private EnumSet<Setting> settings;
	private int displayRank = -1;
	public ReadableAttribute() {
		this.displayName = "";
		settings = EnumSet.noneOf(Setting.class);
	}
	public ReadableAttribute(final String displayName) {
		this.displayName = displayName;
	}
	protected ReadableAttribute(final ReadableAttribute<T> attribute) {
		super(attribute);
	}
	@Override
	public String toString() {
		return "";
	}
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}
	public void setIsVisible(final boolean isVisible) {
		this.isVisible = isVisible;
		if (this.isVisible && this.displayRank < 0) {
			throw new IllegalStateException("Visible Readable Attribute Must have valid displayRank (>=0), instead has: " + displayRank);
		}
	}
	public boolean isVisible() {
		return isVisible;
	}
	public void parseAndSetSettings(final String settings, final String delimeter) {
		final String[] settingsList = settings.split(delimeter);
		for (final String setting : settingsList) {
			this.settings.add(Setting.valueOf(setting.trim().toUpperCase()));
		}
	}
	public int getDisplayRank() {
		return displayRank;
	}
	public void setDisplayRank(final int displayRank) {
		this.displayRank = displayRank;
	}
	public enum Setting {
		/**
		 * An attribute that should have a "+" sign in front of it when positive
		 */
		PLUS_FOR_POSITIVE,
		/**
		 * Signifies that should have different colors based on it's value.
		 */
		COLOR_BASED_ON_SIGN,
		/**
		 * if the attribute should be displayed with italics
		 */
		ITALICS,
		/**
		 * If the attribute should be displayed out of 10
		 */
		DISPLAY_OUT_OF_10;
	}
}
