package gui.mvpFramework;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import game.Board;
import gui.guiComponents.GameSpace;
import gui.guiComponents.InfoWindow;
import gui.guiutils.GuiUtils;
import thingFramework.Pokemon;
import thingFramework.Thing;

public class Presenter {
	private volatile Board board;
	private GameView gameView;
	private final static Consumer<Presenter> LET_POKE_GO = p -> p.board.confirmGrab();
	private Thing thingToAdd = null;
	private int currCount =0;
	private Map<GameSpace, Thing> allThings = new HashMap<GameSpace, Thing>();
	private CurrentState state = CurrentState.GAMEPLAY;
	private InfoWindow currentWindow = null;
	private static Image notificationBackground = GuiUtils.readImage("/sprites/ui/pikabackground.jpg");
	String oldString;
	String newString;
	public Presenter() {};
	
	public Presenter(Board b, GameView gv) {
		board = b;
		gameView = gv;
		oldString = board.toString();
	}
	public boolean containsGameSpace(GameSpace gs) {
		return allThings.containsKey(gs);
	}
	public Thing removeGameSpace(GameSpace gs) {
		board.removeThing(allThings.get(gs));
		return allThings.remove(gs);
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
		newString = board.toString();
		if (!newString.equals(oldString)) {
			System.out.println("\n---GAME TIME---: "+ board.getTotalGameTime() + "\n" + board.toString() + "GOLD: " + board.getGold() + "\nPOP:" + board.getPopularity() + "\n-------");
			oldString = newString;
		}
	}
	public void setBoard(Board b) {
		this.board = b;
	}
	public void setGameView(GameView gv) {
		this.gameView = gv;
	}
	public void NotificationClicked() {
		if (!board.wildPokemonPresent() || state != CurrentState.GAMEPLAY)
			return;
		state =  CurrentState.NOTIFICATION_WINDOW;
		InfoWindow iw = wildPokemonWindow(board.grabWildPokemon());
		currentWindow = iw;
		gameView.displayPanelCentered(iw);										
	}
	private void addGameSpace(GameSpace gs) {
		if (thingToAdd == null)
			return;
		board.addThing(currCount++, thingToAdd);
		allThings.put(gs, thingToAdd);
		thingToAdd = null;
		state = CurrentState.GAMEPLAY;
	}
	public void notifyAddedPokemonFromQueue(GameSpace gs) {
		addGameSpace(gs);
		board.confirmGrab();
	}
	public void notifyMovedGameSpace(GameSpace gs) {
		addGameSpace(gs);
	}
	public void attemptAddThing(Thing t, AddType type) {
		state = CurrentState.PLACING_SPACE;
		GameSpace gs = new GameSpace(GuiUtils.readAndTrimImage(t.getImage()));
		thingToAdd = t;
		gameView.attemptThingAdd(gs, type);
	}
	public boolean attemptMoveGameSpace(GameSpace gs) {
		if (!containsGameSpace(gs))
			throw new IllegalArgumentException("GameSpace " + gs + "Not found on board");
		if (state != CurrentState.GAMEPLAY)
			return false;
		Thing t = removeGameSpace(gs);
		attemptAddThing(t, AddType.PRIOR_ON_BOARD);
		return true;
	}
	private void undoNotificationClicked() {
		board.undoGrab();
		state = CurrentState.GAMEPLAY;
	}
	public void Entered() {
		switch(state) {
			case NOTIFICATION_WINDOW:
				attemptAddThing(board.getGrabbed(), AddType.POKE_FROM_QUEUE);	
				break;
		}
	}
	public void Canceled() {
		switch(state) {
			case NOTIFICATION_WINDOW:
				undoNotificationClicked();
			break;
		}

	}
	public void CleanUp() {
		switch(state) {
			case NOTIFICATION_WINDOW:
				gameView.removeDisplay(currentWindow);
				currentWindow = null;
				break;
		}
		
	}
	public void Finish() {
		state = CurrentState.GAMEPLAY;
	}
	private InfoWindow wildPokemonWindow(Pokemon p) {
		InfoWindow iw = new InfoWindow()
				.setPresenter(this)
				.setInfo("A wild " + p.getName() + " appeared!")
				.setItem(p)
				.addEnterButton("Place")
				.addButton("Set Free", LET_POKE_GO, true, false, true)
				.addCancelButton()
				.setBackgroundImage(notificationBackground)
				.Create();
		return iw;
	}
	private enum CurrentState {
		GAMEPLAY, NOTIFICATION_WINDOW, PLACING_SPACE
	}
	public enum AddType{
		POKE_FROM_QUEUE, PRIOR_ON_BOARD
	}
	
	
}
