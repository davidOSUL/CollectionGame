package gui.guiutils;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import interfaces.Procedure;

/**
 * Manages key bindings by adding quicker method of adding Key Stroke actions
 * @author DOSullivan
 */
public class KeyBindingManager {
	private InputMap inputMap;
	private ActionMap actionMap;
	/**
	 * Create a new KeyBindingManager for the given input and action maps
	 * @param inputMap 
	 * @param actionMap
	 */
	public KeyBindingManager(InputMap inputMap, ActionMap actionMap) {
		this.inputMap = inputMap;
		this.actionMap = actionMap;
	}
	/**
	 * bind event.invoke() to the given keyCode
	 * @param keyCode the keyCode to bind the event to
	 * @param event the Procedure that should occur when the key is pressed
	 */
	public void addKeyBinding(int keyCode, Procedure event) {
	      addKeyBinding(KeyStroke.getKeyStroke(keyCode, 0), event);
	}
	/**
	 * bind event.invoke() to the given KeyStroke
	 * @param stroke the stroke to bind the event to
	 * @param event the Procedure that should occur when the key is pressed
	 */
	public void addKeyBinding(KeyStroke stroke, Procedure event) {
		String text = KeyEvent.getKeyText(stroke.getKeyCode()) + KeyEvent.getKeyModifiersText(stroke.getModifiers());
 	    inputMap.put(stroke, text);
	    actionMap.put(text, new KeyAction(text, event));
	}
	 /**
	 * Every new key binding creates a KeyAction. This just causes the event to be invoked whenever that key is pressed
	 * @author DOSullivan
	 */
	private class KeyAction extends AbstractAction {
	     /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Procedure event;
		 /**
		  * Create a New Key Action 
		 * @param actionCommand the name of the keyBinding
		 * @param event the event to occur
		 */
		public KeyAction(String actionCommand, Procedure event) {
			 this.event = event;
	         putValue(ACTION_COMMAND_KEY, actionCommand);
	      }

	
	    @Override
	      public void actionPerformed(ActionEvent actionEvt) {
				event.invoke();
	      }
	 }
}
