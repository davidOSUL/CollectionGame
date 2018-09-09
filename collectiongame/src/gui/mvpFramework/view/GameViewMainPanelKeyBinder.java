package gui.mvpFramework.view;

import java.awt.event.KeyEvent;

import javax.swing.JComponent;

import gui.guiutils.KeyBindingManager;

/**
 * Specific implementation of ViewKeyBinder for GameView/MainGamePanel ViewInterface implementation
 * @author David O'Sullivan
 *
 */
class GameViewMainPanelKeyBinder implements ViewKeyBinder {
	/**
	 * When KeyBindings should happen
	 */
	private static final int CONDITION = JComponent.WHEN_IN_FOCUSED_WINDOW; 
	/**
	 * The manager for all key stroke events in this panel
	 */
	private final KeyBindingManager keyBindings;
	/**
	 * Creates a new KeyBinder for the provided MainGamePanel
	 * @param panel the panel to add keyBindings to 
	 */
	GameViewMainPanelKeyBinder(final MainGamePanel panel) {
		keyBindings = new KeyBindingManager(panel.getInputMap(CONDITION), panel.getActionMap());
	}
	/** 
	 * @see gui.mvpFramework.view.ViewKeyBinder#setKeyBindings()
	 */
	@Override
	public void setKeyBindings(final ViewInterface vi) {
		keyBindings.addKeyBinding(KeyEvent.VK_ESCAPE, () -> {
	    	 vi.getPresenter().Canceled();
	      });
	      keyBindings.addKeyBinding(KeyEvent.VK_ENTER, () -> {
	    	  vi.getPresenter().Entered();
	      });
	      keyBindings.addKeyBinding(KeyEvent.VK_RIGHT, () -> {
	    	  //TODO: Implement
	      });
	      keyBindings.addKeyBinding(KeyEvent.VK_LEFT, () -> {
	    	  //TODO: Implement
	      });
	      keyBindings.addKeyBinding(KeyEvent.VK_I, () -> {
	    	  	vi.getPresenter().toggleAdvancedStats(); 
	      });
	      keyBindings.addKeyBinding(KeyEvent.VK_S, () -> {
	    	  	vi.getPresenter().shopClicked(); 
	      });
		
	}

}
