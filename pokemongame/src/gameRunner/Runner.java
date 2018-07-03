package gameRunner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	private Runner() {
		
	}
	public static void main(String... args) {
		//Runner runner = new Runner();
		//runner.startGame();
		Presenter p = new Presenter();

		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		    	
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

	private void startGame() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame startScreen = StartScreenBuilder.getFrame(title, false, x-> x.notifyPressedNewGame(), x-> x.notifyPressedContinueGame(), Runner.this );
				startScreen.setVisible(true);
			}
		});
	}
	public synchronized void notifyPressedNewGame() {
		System.out.println("New game");
	}
	public synchronized void notifyPressedContinueGame() {
		System.out.println("Continue game");
	}

}
