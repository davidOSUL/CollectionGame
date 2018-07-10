package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import modifiers.Modifier;
import thingFramework.Pokemon;

public class BoardModifierTest {
	public static void main(final String ...strings) {
		final Board b = new Board();
		final Modifier<Pokemon> mod = new Modifier<Pokemon>(10000, p -> p.addToIntegerAttribute("gpm", 300, false), p-> p.addToIntegerAttribute("gpm", -300, true));
		final Modifier<Pokemon> mod2 = new Modifier<Pokemon>(10000, p -> p.addToIntegerAttribute("popularity boost", 100, false), p-> p.addToIntegerAttribute("popularity boost", -100, true));
		final List<Pokemon> addedPokes = new ArrayList<Pokemon>();
		Pokemon lastRemoved = null;
		final Scanner c = new Scanner(System.in);
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> b.update(), 0, 100, TimeUnit.MILLISECONDS);
		while (true) {
			if (c.hasNextLine()) {
				final String input = c.nextLine();
				if (input.equals("p") && b.wildPokemonPresent()) {
					final Pokemon p = b.grabWildPokemon();
					b.confirmGrab();
					b.addThing(p);
					addedPokes.add(p);
					System.out.println(b);
				}
				else if (input.equals("a")) {
					b.addGlobalPokemonModifier(mod);
					System.out.println(b);
				}
				else if (input.equals("r")) {
					b.removeGlobalPokemonModifier(mod);
					System.out.println(b);
				}
				else if (input.equals("aa")) {
					b.addGlobalPokemonModifier(mod2);
					System.out.println(b);
				}
				else if (input.equals("rr")) {
					b.removeGlobalPokemonModifier(mod2);
					System.out.println(b);
				}
				else if (input.equals("print")) {
					System.out.println(b);
				}
				else if (input.equals("remove")) {
					final int i = new Scanner(System.in).nextInt();
					b.removeThing(addedPokes.get(i));
					lastRemoved = addedPokes.remove(i);
					System.out.println(b);
				}
				else if (input.equals("undo remove")) {
					b.addThing(lastRemoved);
					addedPokes.add(lastRemoved);
					System.out.println(b);
				}
			}
			
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
