package gui.mvpFramework.presenter;

import java.io.IOException;
import java.io.Serializable;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import userIO.GameSaver;

class CurrentGamestateSaver implements Serializable {
	private final GameSaver gameSaver;
	CurrentGamestateSaver(final GameSaver gameSaver) {
		this.gameSaver = gameSaver;
	}
	/**
	 * save the current game
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
