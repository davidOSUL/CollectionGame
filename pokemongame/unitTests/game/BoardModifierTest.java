package game;

import java.util.Scanner;

import modifiers.Modifier;
import thingFramework.Pokemon;

public class BoardModifierTest {
	public static void main(final String ...strings) {
		final Board b = new Board();
		final Modifier<Pokemon> mod = new Modifier<Pokemon>(p -> p.addToIntegerAttribute("gpm", 300, false), p-> p.addToIntegerAttribute("gpm", -300, true));
		final Modifier<Pokemon> mod2 = new Modifier<Pokemon>(p -> p.addToIntegerAttribute("popularity boost", 100, false), p-> p.addToIntegerAttribute("gpm", -100, true));
		final Scanner c = new Scanner(System.in);
		while (true) {
			b.update();
			if (c.hasNextLine()) {
				final String input = c.nextLine();
				if (input.equals("p") && b.wildPokemonPresent()) {
					final Pokemon p = b.grabWildPokemon();
					b.confirmGrab();
					b.addThing(p);
					System.out.println(b);
				}
				else if (input.equals("a")) {
					b.addGlobalPokemonModifier(mod);
					b.update();
					System.out.println(b);
				}
				else if (input.equals("r")) {
					b.removeGlobalPokemonModifier(mod);
					b.update();
					System.out.println(b);
				}
				else if (input.equals("aa")) {
					b.addGlobalPokemonModifier(mod2);
					b.update();
					System.out.println(b);
				}
				else if (input.equals("rr")) {
					b.removeGlobalPokemonModifier(mod2);
					b.update();
					System.out.println(b);
				}
				else if (input.equals("print")) {
					b.update();
					System.out.println(b);
				}
			}
			
			
		}
	}
}
