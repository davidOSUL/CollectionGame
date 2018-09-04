package gui.displayComponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
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
		ToolTipManager.sharedInstance().setInitialDelay(50);
		 ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		UIManager.put("ToolTip.background", Color.WHITE);
		UIManager.put("ToolTip.font", new Font("TimesRoman", Font.PLAIN, 16));
	}
	/**
	 * Enables or disables tooltips
	 * @param enabled true to enable tooltips, false to disable
	 */
	public void setEnabled(final boolean enabled) {
		ToolTipManager.sharedInstance().setEnabled(enabled);
	}
	/**
	 * Set the ToolTip Description for a given component. Automatically formats text to work with newline characters
	 * @param comp the component to add a JToolTip to
	 * @param description the description
	 */
	public void setDescription(final JComponent comp, final String description) {
		if (comp == null)
			throw new NullPointerException("Cant add description to null component");
		final String formattedText = GuiUtils.createNewLines(description);
		 comp.setToolTipText(formattedText);
		 final Point locationOnScreen = MouseInfo.getPointerInfo().getLocation();
         final Point locationOnComponent = new Point(locationOnScreen);
         SwingUtilities.convertPointFromScreen(locationOnComponent, comp);
         if (comp.contains(locationOnComponent)) {
             ToolTipManager.sharedInstance().mouseMoved(
                     new MouseEvent(comp, -1, System.currentTimeMillis(), 0, locationOnComponent.x, locationOnComponent.y,
                             locationOnScreen.x, locationOnScreen.y, 0, false, 0));
         }
         
	}
	
	/**
	 * Set the description of a component, using thing.toString() as the description. Automatically formats text to work with newline characters
	 * @param comp the component to add a JToolTip to
	 * @param thing the thing w/ description to use
	 */
	public void setDescription(final JComponent comp, final Thing thing) {
		setDescription(comp, thing.toString());
	}
	/**
	 * Removes the components description
	 * @param comp
	 */
	public void removeDescription(final JComponent comp) {
		comp.setToolTipText(null);
	}
	/**
	 * Set the delay for alll tooltips
	 * @param milliseconds how long the delay should be
	 */
	public void setInitialDelay(final int milliseconds) {
		ToolTipManager.sharedInstance().setInitialDelay(milliseconds);
	}
	/**
	 * Set the background color for all tooltips
	 * @param c the new background color
	 */
	public void setBackgroundColor(final Color c) {
		UIManager.put("ToolTip.background", c);
	}
	/**
	 * disables then re-enables tooltips
	 */
	public void flashTooltips() {
		setEnabled(false); 
		setEnabled(true);
	}
	

}
