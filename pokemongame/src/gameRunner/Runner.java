package gameRunner;

import javax.swing.SwingUtilities;

import game.Board;
import gui.GameView;
import gui.Presenter;

public class Runner implements Runnable {
	Presenter p;
	public Runner(Presenter p) {
		this.p = p;
	}
	private void gameloop() {
		while (true) {
			p.update();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void run() {
		gameloop();		
	}
	public static void main(String... args) {
		Presenter p = new Presenter();
		Runner runner = new Runner(p);
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		        GameView gv = new GameView("Pokemon Collection Game V. Alpha");
		        Board board = new Board();
		        p.setBoard(board);
		        p.setGameView(gv);
		        gv.setPresenter(p);
		        gv.setVisible(true);
		    }
		});	
		new Thread(runner).start();
		
	}

}
