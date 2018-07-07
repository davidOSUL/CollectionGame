package gui.displayComponents;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import gui.guiutils.GuiUtils;
import thingFramework.Thing;

/**
 * Manages Tooltips for all components
 * @author David O'Sullivan
 *
 */
public final class DescriptionManager {
	private final static DescriptionManager INSTANCE = new DescriptionManager();
	/**
	 * @return The instance of Description Manager that all classess should use
	 */
	public final static DescriptionManager getInstance() {
		return INSTANCE;
	}
	private DescriptionManager() {
		ToolTipManager.sharedInstance().setInitialDelay(100);
		 ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		UIManager.put("ToolTip.background", Color.WHITE);
	}
	public void setEnabled(boolean enabled) {
		ToolTipManager.sharedInstance().setEnabled(enabled);
	}
	/**
	 * Set the ToolTip Description for a given component. Automatically formats text to work with newline characters
	 * @param comp the component to add a JToolTip to
	 * @param description the description
	 */
	public void setDescription(JComponent comp, String description) {
		if (comp == null)
			throw new NullPointerException("Cant add description to null component");
		String formattedText = GuiUtils.createNewLines(description);
		comp.setToolTipText(formattedText);
	}
	/**
	 * Set the description of a component, using thing.toString() as the description. Automatically formats text to work with newline characters
	 * @param comp the component to add a JToolTip to
	 * @param thing the thing w/ description to use
	 */
	public void setDescription(JComponent comp, Thing thing) {
		setDescription(comp, thing.toString());
	}
	/**
	 * Removes the components description
	 * @param comp
	 */
	public void removeDescription(JComponent comp) {
		comp.setToolTipText(null);
	}
	/**
	 * Set the delay for alll tooltips
	 * @param milliseconds how long the delay should be
	 */
	public void setInitialDelay(int milliseconds) {
		ToolTipManager.sharedInstance().setInitialDelay(milliseconds);
	}
	/**
	 * Set the background color for all tooltips
	 * @param c the new background color
	 */
	public void setBackgroundColor(Color c) {
		UIManager.put("ToolTip.background", c);
	}
	

}
