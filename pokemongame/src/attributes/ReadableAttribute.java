package attributes;

import java.util.EnumSet;

import gui.guiutils.GuiUtils;
import interfaces.SerializableFunction;

class ReadableAttribute<T> extends Attribute<T> {
	private String displayName = "";
	private boolean isVisible;
	private final EnumSet<Setting> settings;
	private int displayRank = -1;
	private SerializableFunction<String, String> formatDisplay = x -> {return x;};
	/**
	 * The value at which if the input value to generateAttribute has this value, it should not be printed
	 * (Will not stop you from setting it however) (e.g. -1 for an attribute that should always be >=0)
	 */
	private  T objectToIgnoreValueAt = null;
	public ReadableAttribute(final ParseType<T> parseType) {
		super(parseType);
		settings = EnumSet.noneOf(Setting.class);
	}
	public ReadableAttribute(final ParseType<T> parseType, final String displayName) {
		this(parseType);
		this.displayName = displayName;
	}
	protected ReadableAttribute(final ReadableAttribute<T> attribute) {
		super(attribute);
		this.displayName = attribute.displayName;
		this.isVisible =  attribute.isVisible;
		this.settings = EnumSet.copyOf(attribute.settings);
		this.displayRank = attribute.displayRank;
		
		
	}
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("<span>" +displayName);
		if (!displayName.isEmpty())
			sb.append(": ");
		sb.append(getFormattedValue());
		return sb.toString();
	}
	private String getFormattedValue() {
		if (getValue() == null)
			return "";
		final StringBuilder sb = new StringBuilder();
		if (settings.contains(Setting.ITALICS));
			sb.append("<i>");
		if (settings.contains(Setting.COLOR_BASED_ON_SIGN)) {
			sb.append(GuiUtils.getSignedColorFormat(isPositive(), settings.contains(Setting.PLUS_FOR_POSITIVE) ? "+" : ""));
		}
		sb.append(formatDisplay.apply(getValue().toString()));
		
		if (settings.contains(Setting.OUT_OF_TEN))
			sb.append("/10");
		if (settings.contains(Setting.COLOR_BASED_ON_SIGN))
			sb.append("</font>");
		if (settings.contains(Setting.ITALICS))
			sb.append("</i>");
		sb.append(getExtraDescription());
		sb.append("</span>");
		return sb.toString();
	}
	public void setDisplayFormat(final SerializableFunction<String, String> formatDisplay) {
		this.formatDisplay = formatDisplay;
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
	@Override
	public boolean shouldDisplay() {
		return isVisible && !getValue().equals(objectToIgnoreValueAt);
	}
	public void parseAndSetSettings(final String settings, final String delimeter) {
		final String[] settingsList = settings.split(delimeter);
		for (final String setting : settingsList) {
			this.settings.add(Setting.valueOf(setting.trim().toUpperCase()));
		}
	}
	@Override
	public int getDisplayRank() {
		return displayRank;
	}
	public void setDisplayRank(final int displayRank) {
		this.displayRank = displayRank;
	}
	void setIgnoreValue(final T ignoreValue) {
		this.objectToIgnoreValueAt = ignoreValue;
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
		OUT_OF_TEN;
	}
	@Override
	Attribute<T> makeCopy() {
		return new ReadableAttribute<T>(this);
	}
}
