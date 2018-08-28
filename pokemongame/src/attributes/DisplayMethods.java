package attributes;

import java.util.EnumSet;

public interface DisplayMethods {
	public default boolean shouldDisplay() {
		return false;
	}
	public default int getDisplayRank() {
		return -1;
	}
	public default String getDisplayString(final EnumSet<DisplayStringSetting> settings) {
		return toString();
	}
	public default String getDisplayString() {
		return getDisplayString(EnumSet.noneOf(DisplayStringSetting.class));
	}
}
