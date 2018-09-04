package attributes;

import java.util.EnumSet;

import gui.guiutils.GuiUtils;
import interfaces.SerializableFunction;

/**
 * An attribute that is meant to be displayed
 * @see Attribute
 * @author David O'Sullivan
 *
 * @param <T> the type of this attribute
 */
class ReadableAttribute<T> extends Attribute<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	/**
	 * @see attributes.Attribute#Attribute(attributes.ParseType)
	 */
	public ReadableAttribute(final ParseType<T> parseType) {
		super(parseType);
		settings = EnumSet.noneOf(Setting.class);
	}
	/**
	 * Creates a new Readable Attribute with the provided parseType and the provide displayName
	 * @param parseType the associated ParseType
	 * @param displayName the display name, this is the name the attribute will be displated on the gui, and
	 * does not necessarily equal that used for internal maps
	 */
	public ReadableAttribute(final ParseType<T> parseType, final String displayName) {
		this(parseType);
		this.displayName = displayName;
	}
	/**
	 * Creates a new ReadableAttribute by copying over from an old ReadableAttribute
	 * @param attribute the old ReadableAttribute
	 */
	protected ReadableAttribute(final ReadableAttribute<T> attribute) {
		super(attribute);
		this.displayName = attribute.displayName;
		this.isVisible =  attribute.isVisible;
		this.settings = EnumSet.copyOf(attribute.settings);
		this.displayRank = attribute.displayRank;
		this.objectToIgnoreValueAt = attribute.objectToIgnoreValueAt;
		this.formatDisplay = attribute.formatDisplay;
		
		
	}
	/** 
	 * @see attributes.Attribute#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("<span>" +displayName);
		if (!displayName.isEmpty())
			sb.append(": ");
		sb.append(getFormattedValue());
		sb.append("</span>");
		return sb.toString();
	}
	/** 
	 * @see attributes.DisplayMethods#getDisplayString(java.util.EnumSet)
	 */
	@Override
	public String getDisplayString(final EnumSet<DisplayStringSetting> displayStringSettings) {
		final StringBuilder sb = new StringBuilder("<span>");
		sb.append(getFormattedValue(displayStringSettings));
		sb.append(" ");
		sb.append(displayName);
		sb.append("</span>");
		return sb.toString();
	}
	private String getFormattedValue(final EnumSet<DisplayStringSetting> displayStringSettings) {
		if (getValue() == null)
			return "";
		final StringBuilder sb = new StringBuilder();
		if (settings.contains(Setting.ITALICS))
			sb.append("<i>");
		if (settings.contains(Setting.COLOR_BASED_ON_SIGN)) {
			String plusChar = "";
			if (settings.contains(Setting.PLUS_FOR_POSITIVE) && !displayStringSettings.contains(DisplayStringSetting.CHANGE_PLUS_TO_TIMES)) {
					plusChar = "+";
			}
			sb.append(GuiUtils.getSignedColorFormat(isPositive(), plusChar));
		}
		sb.append(formatDisplay.apply(getValue().toString()));
		if (settings.contains(Setting.PLUS_FOR_POSITIVE) && displayStringSettings.contains(DisplayStringSetting.CHANGE_PLUS_TO_TIMES))
			sb.append("x");
		if (settings.contains(Setting.OUT_OF_TEN))
			sb.append("/10");
		if (settings.contains(Setting.COLOR_BASED_ON_SIGN))
			sb.append("</font>");
		if (settings.contains(Setting.ITALICS))
			sb.append("</i>");
		sb.append(getExtraDescription());
		return sb.toString();
	}
	private String getFormattedValue() {
		return getFormattedValue(EnumSet.noneOf(DisplayStringSetting.class));
	}
	/**
	 * Sets the format Display for this ReadableAttribute. Whenever this ReadableAttribute's value is displayed, 
	 * it will first apply the formatDisplay to the string representation of the value of this ReadableAttribute
	 * @param formatDisplay the function to apply to this ReadableAttribute's value when displaying
	 */
	public void setDisplayFormat(final SerializableFunction<String, String> formatDisplay) {
		this.formatDisplay = formatDisplay;
	}
	/**
	 * Sets the displayName for this ReadableAttribute. This is the name that will actually be displayed on the GUI
	 * and does not necessarily equal that used internally for maps, etc. 
	 * @param displayName the displayName for this ReadableAttribute
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}
	/**
	 * Sets the visibility of this ReadableAttribute. an invisible ReadableAttribute, will always return false when
	 * shouldDisplay() is called
	 * @param isVisible
	 */
	public void setIsVisible(final boolean isVisible) {
		this.isVisible = isVisible;
		if (this.isVisible && this.displayRank < 0) {
			throw new IllegalStateException("Visible Readable Attribute Must have valid displayRank (>=0), instead has: " + displayRank);
		}
	}
	/** 
	 * returns true if isVisible, the value isn't null, and the value doesn't equal the objectToIgnoreValueAt
	 * @see attributes.DisplayMethods#shouldDisplay()
	 */
	@Override
	public boolean shouldDisplay() {
		return isVisible && !(getValue() == null) && !getValue().equals(objectToIgnoreValueAt);
	}
	/**
	 * Parses in a series of settings for this ReadableAttribute, using the provided delimiter
	 * @param settings the series of settings to be parsed (of the format "setting1DELIMsetting2DELIM...")
	 * @param delimeter the delimiter between settings
	 * @see Setting
	 */
	public void parseAndSetSettings(final String settings, final String delimeter) {
		final String[] settingsList = settings.split(delimeter);
		for (final String setting : settingsList) {
			this.settings.add(Setting.valueOf(setting.trim().toUpperCase()));
		}
	}
	/** 
	 * @see attributes.DisplayMethods#getDisplayRank()
	 */
	@Override
	public int getDisplayRank() {
		return displayRank;
	}
	/**
	 * sets the display rank for this ReadableAttribute. This value determines the order in which ReadableAttribute's
	 * want to be displayed. Attributes with lesser rank want to be displayer earlier.
	 * @param displayRank the new display rank for this ReadableAttribute.
	 */
	public void setDisplayRank(final int displayRank) {
		this.displayRank = displayRank;
	}
	/**
	 * sets the ignore value for this ReadableAttribute. if this.getValue().equals(ignoreValue), then shouldDisplay() will return false.
	 * @param ignoreValue the new ignoreValue for this Attribute
	 */
	void setIgnoreValue(final T ignoreValue) {
		this.objectToIgnoreValueAt = ignoreValue;
	}
	/**
	 * Settings that can be used when a ReadableAttribute is displayed
	 * @author David O'Sullivan
	 *
	 */
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
	/** 
	 * @see attributes.Attribute#makeCopy()
	 */
	@Override
	Attribute<T> makeCopy() {
		return new ReadableAttribute<T>(this);
	}
}
