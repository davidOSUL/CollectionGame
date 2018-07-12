package gameRunner;

import static gameutils.Constants.CHEAT_MODE;
import static gui.guiutils.GUIConstants.SKIP_LOAD_SCREEN;

import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import game.Board;
import gui.displayComponents.StartScreenBuilder;
import gui.guiutils.GuiUtils;
import gui.mvpFramework.Presenter;
import userIO.GameSaver;

/**
 * Starts the game with a screen where users can select new or continue game 
 * @author David O'Sullivan
 */
public class Runner  {
	private static final String title = "Pokemon Collection Game V. Alpha";
	private final GameSaver saver;
	private JFrame startScreen;
	private final Timer updateStartPanel;
	private Presenter p;
	private Board board;
	private Runner() {
		saver = new GameSaver();
		try {
			startScreen = StartScreenBuilder.getFrame(title, saver.hasSave(), x-> x.notifyPressedNewGame(), x-> x.notifyPressedContinueGame(), this );
		} catch (final IOException e) {
			//TODO
			e.printStackTrace();
		}
		final ActionListener updateStartScreen = evt -> {
			startScreen.revalidate();
			startScreen.repaint();
		};
		updateStartPanel = new Timer(10, updateStartScreen);
	}
	public static void main(final String... args) {
		SwingUtilities.invokeLater(() -> {
			final Runner runner = new Runner();
			if (SKIP_LOAD_SCREEN)
				runner.notifyPressedNewGame();
			else 
				runner.displayStartWindow();
		});
	}

	private void displayStartWindow() {
		SwingUtilities.invokeLater(() -> {
			updateStartPanel.start();
			startScreen.setVisible(true);
		});
	}
	private void exitStartWindow() {
		startScreen.dispose();
		updateStartPanel.stop();
	}
	public synchronized void notifyPressedNewGame() {
		if (!SwingUtilities.isEventDispatchThread())
			setUpError("Should be on the the EDT");
		try {
			if (saver.hasSave()) {
				final Object[] options = {"Yes", "Cancel"};
				if (JOptionPane.showOptionDialog(startScreen, "Are you sure you want to start a new game? This will delete your current save file", "Delete current save?", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]) == JOptionPane.YES_OPTION) {
					saver.deleteSave();
				}
				else {
					return;
				}
			}
		} catch (HeadlessException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		exitStartWindow();
		if (CHEAT_MODE)
			board = new Board(100000000, 100000000);
		else
			board = new Board(100, 0);
		p = new Presenter(board, title, saver);
		displayPrimaryGameWindow();
		new Thread(() -> startPresenterUpdate()).start();
	}
	private void displayPrimaryGameWindow() {	
		if (!SwingUtilities.isEventDispatchThread()) {
			setUpError("Should be on the the EDT");	
		}
		final ActionListener updateGUI = evt -> p.updateGUI();
		new Timer(10, updateGUI).start();
		p.getGameView().setVisible(true);
		setupGlobalExceptionHandling(p);


	}
	private static void setupGlobalExceptionHandling(final Presenter p) {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> GuiUtils.displayError(e, p.getGameView()));
	}
	private void startPresenterUpdate() {
		if (SwingUtilities.isEventDispatchThread())
			setUpError("Board updates should not be on the EDT");
		final ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
		es.scheduleAtFixedRate(() -> {
			try {
				p.updateBoard();
			} catch (final Exception e) {
				GuiUtils.displayError(e, startScreen);
			}
		}, 0, 10, TimeUnit.MILLISECONDS);
	}
	public synchronized void notifyPressedContinueGame() {
		if (!SwingUtilities.isEventDispatchThread())
			setUpError("Should be on the the EDT");
		exitStartWindow();
		try {
			final ObjectInputStream ois = saver.readSave();
			p = (Presenter) ois.readObject();
			displayPrimaryGameWindow();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		new Thread(() -> startPresenterUpdate()).start();
	}
	private void setUpError(final String message) {
		GuiUtils.displayError(new IllegalStateException(message), startScreen);
	}

}
