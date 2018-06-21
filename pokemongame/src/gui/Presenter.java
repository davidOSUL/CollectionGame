package gui;

import java.io.IOException;
import java.util.function.Consumer;

import javax.swing.JPanel;

import game.Board;
import guiutils.GuiUtils;
import thingFramework.Pokemon;
import thingFramework.Thing;

public class Presenter {
	Board board;
	GameView gameView;
	private static Consumer<Presenter> letPokeGo = p -> p.board.getWildPokemon();
	Thing thingToAdd = null;
	int currCount =0;
	public Presenter(Board b, GameView gv) {
		board = b;
		gameView = gv;
	}
	public void update() {
		
	}
	public void NotificationClicked() {
		if (!board.wildPokemonPresent())
			return;
		InfoWindow iw = wildPokemonWindow(board.peekWildPokemon());
		gameView.displayPanelCentered(iw);
		while (iw.isDone()) {
		}
		if (iw.isEntered()) {
			addThing(board.getWildPokemon());
		}
		
		
						
										
	}
	public synchronized void notifyAdded(Grid g) {
		if (thingToAdd == null)
			return;
		board.addThing(currCount++, thingToAdd);
		thingToAdd = null;
	}
	public void addThing(Thing t) {
		GameSpace gs = null;
		try {
			gs = new ThingSpace(t);
		} catch (IOException e) {
			e.printStackTrace();
		}
		thingToAdd = t;
		gameView.attemptThingAdd(gs);
	}
	private InfoWindow wildPokemonWindow(Pokemon p) {
		InfoWindow iw = new InfoWindow().setTitle("Pokemon Found!")
				.setInfo("A wild " + p.getName() + "appeared!")
				.setItem(p)
				.addEnterButton("Place")
				.addButton("Set Free", letPokeGo, this, true, false)
				.addCancelButton()
				.Create();
		return iw;
	}
	
	
}
