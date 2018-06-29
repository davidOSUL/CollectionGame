package gui.displayComponents;

public final class DescriptionToolTipBuilder {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DescriptionToolTipBuilder() {}
	public static String getToolTipText(String description) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		String[] lines = description.split("\n");
		for (int i = 0; i < lines.length-1; i++ ) {
			sb.append(lines[i]);
			sb.append("<br>");
		}
		sb.append(lines[lines.length-1]);
		sb.append("</html>");
		return sb.toString();
		
	}

}
