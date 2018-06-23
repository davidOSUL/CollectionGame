package gameRunner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import game.Board;
import gui.GameView;
import gui.Presenter;

public class Runner  {
	
	public static void main(String... args) {
		Presenter p = new Presenter();
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		        GameView gv = new GameView("Pokemon Collection Game V. Alpha");
		        Board board = new Board(); 
		        p.setBoard(board);
		        p.setGameView(gv);
		        gv.setPresenter(p);
		        ActionListener updateGUI = new ActionListener() {
		            public void actionPerformed(ActionEvent evt) {
		            	p.updateGUI();
		            	p.updateBoard();
		            }
		        };
		        new Timer(10, updateGUI).start();
		        gv.setVisible(true);
		    }
		});	
		

		
	}

}
