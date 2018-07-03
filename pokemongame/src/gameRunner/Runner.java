package gameRunner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import game.Board;
import gui.displayComponents.StartScreenBuilder;
import gui.mvpFramework.GameView;
import gui.mvpFramework.Presenter;

/**
 * Starts the game and keeps it running
 * @author David O'Sullivan
 */
public class Runner  {
	private static final String title = "Pokemon Collection Game V. Alpha";
	JFrame startScreen;
	Timer updateStartPanel;
	private Runner() {
		startScreen = StartScreenBuilder.getFrame(title, false, x-> x.notifyPressedNewGame(), x-> x.notifyPressedContinueGame(), this );
		ActionListener updateStartScreen = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				startScreen.revalidate();
				startScreen.repaint();
			}
		};
		updateStartPanel = new Timer(10, updateStartScreen);
	}
	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Runner runner = new Runner();
				runner.startGame();
			}
		});
	}

	private void startGame() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateStartPanel.start();
				startScreen.setVisible(true);
			}
		});
	}
	private void exitStartWindow() {
		startScreen.dispose();
		updateStartPanel.stop();
	}
	public synchronized void notifyPressedNewGame() {
		Presenter p = new Presenter();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				exitStartWindow();
				GameView gv = new GameView(title);
				Board board = new Board(100000000, 0 ); 
				p.setBoard(board);
				p.setGameView(gv);
				gv.setPresenter(p);
				ActionListener updateGUI = new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						p.updateGUI();	
					}
				};
				new Timer(10, updateGUI).start();
				gv.setVisible(true);	
			}
		});
		ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
		es.scheduleAtFixedRate(() -> p.updateBoard(), 0, 10, TimeUnit.MILLISECONDS);
	}
	public synchronized void notifyPressedContinueGame() {
		System.out.println("Continue game");
	}

}
