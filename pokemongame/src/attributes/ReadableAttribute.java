package attributes;

import java.util.EnumSet;

import attributes.AttributeFactories.AttributeFactory;
import gui.guiutils.GuiUtils;
import interfaces.SerializableFunction;

class ReadableAttribute<T> extends Attribute<T> {
	private String displayName = "";
	private boolean isVisible;
	private final EnumSet<Setting> settings;
	private int displayRank = -1;
	private SerializableFunction<String, String> formatDisplay = x -> {return x;};
	/**
	 * An extra string that can be added on at the end of this toString's method
	 */
	private String extraDescription = "";
	public ReadableAttribute(final AttributeFactory<T> creator) {
		super(creator);
		settings = EnumSet.noneOf(Setting.class);
	}
	public ReadableAttribute(final AttributeFactory<T> creator, final String displayName) {
		this(creator);
		this.displayName = displayName;
	}
	protected ReadableAttribute(final ReadableAttribute<T> attribute) {
		super(attribute);
		this.displayName = attribute.displayName;
		this.isVisible =  attribute.isVisible;
		this.settings = EnumSet.copyOf(attribute.settings);
		this.displayRank = attribute.displayRank;
		this.extraDescription = attribute.extraDescription;
		
		
	}
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("<span>" +displayName);
		if (!displayName.isEmpty())
			sb.append(": ");
		sb.append(getFormattedValue());
		return sb.toString();
		//return getValue() + " " + extraDescription;
	}
	private String getFormattedValue() {
		final StringBuilder sb = new StringBuilder();
		if (settings.contains(Setting.ITALICS));
			sb.append("<i>");
		if (settings.contains(Setting.COLOR_BASED_ON_SIGN)) {
			sb.append(GuiUtils.getSignedColorFormat(isPositive(), settings.contains(Setting.PLUS_FOR_POSITIVE) ? "+" : ""));
		}
		sb.append(formatDisplay.apply(getValue().toString()));
		
		if (settings.contains(Setting.DISPLAY_OUT_OF_10))
			sb.append("/10");
		if (settings.contains(Setting.COLOR_BASED_ON_SIGN))
			sb.append("</font>");
		if (settings.contains(Setting.ITALICS))
			sb.append("</i>");
		sb.append(extraDescription);
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
	@Override
	Attribute<T> makeCopy() {
		return new ReadableAttribute<T>(this);
	}
}
