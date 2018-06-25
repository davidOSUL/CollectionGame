package gui.guiutils;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import functional.Procedure;

public class KeyBindingManager {
	private InputMap inputMap;
	private ActionMap actionMap;
	public KeyBindingManager(InputMap inputMap, ActionMap actionMap) {
		this.inputMap = inputMap;
		this.actionMap = actionMap;
	}
	public void addKeyBinding(int keyCode, Procedure event) {
	      addKeyBinding(KeyStroke.getKeyStroke(keyCode, 0), event);
	}
	public void addKeyBinding(KeyStroke stroke, Procedure event) {
		String text = KeyEvent.getKeyText(stroke.getKeyCode()) + KeyEvent.getKeyModifiersText(stroke.getModifiers());
 	    inputMap.put(stroke, text);
	    actionMap.put(text, new KeyAction(text, event));
	}
	 private class KeyAction extends AbstractAction {
	     private Procedure event;
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
