package gui.mvpFramework.presenter;

import java.io.IOException;
import java.io.Serializable;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import userIO.GameSaver;

/**
 * Uses GameSaver to save the current state of the game
 * @author David O'Sullivan
 *
 */
class CurrentGamestateSaver implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GameSaver gameSaver;
	/**
	 * Creates a new CurrentGamestateSaver
	 * @param gameSaver the GameSaver to use to save the game
	 */
	CurrentGamestateSaver(final GameSaver gameSaver) {
		this.gameSaver = gameSaver;
	}
	/**
	 * saves the current game
	 * @param toSave the presenter to save
	 * @param showDisplayOn the parent for the save success dialog
	 * @return true if was able to save
	 */
	boolean saveGame(final Presenter toSave, final JFrame showDisplayOn) {
		try {
			if (!gameSaver.hasSave())
				gameSaver.createSaveFile();
			gameSaver.save(toSave);
			saveSuccessDialog(showDisplayOn);
			return true;
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		}
	}
	private void saveSuccessDialog(final JFrame showDisplayOn) {
		final JOptionPane pane = new JOptionPane("Game Saved!", JOptionPane.INFORMATION_MESSAGE);
		final JDialog dialog = pane.createDialog(showDisplayOn, "Success!");	
		SwingUtilities.invokeLater( () -> {
			final Timer timer = new Timer(500, e -> dialog.dispose());
			timer.setRepeats(false);
			timer.start();
			dialog.setVisible(true);
		});

	}
}
