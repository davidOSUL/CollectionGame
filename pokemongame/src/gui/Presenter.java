package gui;

import java.awt.Image;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JPanel;

import game.Board;
import guiutils.GuiUtils;
import thingFramework.Pokemon;
import thingFramework.Thing;

public class Presenter {
	private volatile Board board;
	private GameView gameView;
	private final static Consumer<Presenter> LET_POKE_GO = p -> p.board.getWildPokemon();
	private Thing thingToAdd = null;
	private int currCount =0;
	private Map<GameSpace, Thing> allThings = new HashMap<GameSpace, Thing>();
	private currentState state = currentState.GAMEPLAY;
	private InfoWindow currentWindow = null;
	private static Image notificationBackground = GuiUtils.readImage("/sprites/ui/pikabackground.jpg");
	public Presenter() {};
	
	public Presenter(Board b, GameView gv) {
		board = b;
		gameView = gv;
	}
	public void updateGUI() {
		if (board == null || gameView == null)
			return;
		gameView.setWildPokemonCount(board.numPokemonWaiting());
		gameView.updateDisplay();
	}
	public void updateBoard() {
		if (board == null || gameView == null)
			return;
		board.update();
	}
	public void setBoard(Board b) {
		this.board = b;
	}
	public void setGameView(GameView gv) {
		this.gameView = gv;
	}
	public void NotificationClicked() {
		if (!board.wildPokemonPresent() || state == currentState.NOTIFICATION_WINDOW)
			return;
		state =  currentState.NOTIFICATION_WINDOW;
		InfoWindow iw = wildPokemonWindow(board.peekWildPokemon());
		currentWindow = iw;
		gameView.displayPanelCentered(iw);										
	}
	public void notifyAdded(GameSpace gs) {
		if (thingToAdd == null)
			return;
		board.addThing(currCount++, thingToAdd);
		allThings.put(gs, thingToAdd);
		thingToAdd = null;
	}
	public void addThing(Thing t) {
		GameSpace gs = new GameSpace(GuiUtils.readAndTrimImage(t.getImage()));
		thingToAdd = t;
		gameView.attemptThingAdd(gs);
	}
	public void Entered() {
		switch(state) {
			case NOTIFICATION_WINDOW:
				addThing(board.getWildPokemon());	
				break;
		}
	}
	public void Canceled() {
		switch(state) {
			case NOTIFICATION_WINDOW:
				
			break;
		}

	}
	public void Finished() {
		switch(state) {
			case NOTIFICATION_WINDOW:
				gameView.removeDisplay(currentWindow);
				currentWindow = null;
				break;
		}
		state = currentState.GAMEPLAY;
	}
	private InfoWindow wildPokemonWindow(Pokemon p) {
		InfoWindow iw = new InfoWindow()
				.setPresenter(this)
				.setInfo("A wild " + p.getName() + " appeared!")
				.setItem(p)
				.addEnterButton("Place")
				.addButton("Set Free", LET_POKE_GO, true, false)
				.addCancelButton()
				.setBackgroundImage(notificationBackground)
				.Create();
		return iw;
	}
	private enum currentState {
		GAMEPLAY, NOTIFICATION_WINDOW, PLACING_THING
	}
	
	
}
