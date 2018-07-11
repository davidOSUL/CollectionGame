package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import effects.GlobalPokemonModifierEvent;
import modifiers.Modifier;
import thingFramework.Attribute;
import thingFramework.Item;
import thingFramework.Pokemon;

public class BoardModifierTest {
	public static void main(final String ...strings) {
		final Board b = new Board();
		final Modifier<Pokemon> mod = new Modifier<Pokemon>(60000, p -> p.addToIntegerAttribute("gpm", 300, false), p-> p.addToIntegerAttribute("gpm", -300, true));
		final Modifier<Pokemon> mod2 = new Modifier<Pokemon>(20000, p -> p.addToIntegerAttribute("popularity boost", 100, false), p-> p.addToIntegerAttribute("popularity boost", -100, true));
		final GlobalPokemonModifierEvent<Item> event1 = new GlobalPokemonModifierEvent<Item>(mod, true, true);
		final GlobalPokemonModifierEvent<Item> event2 = new GlobalPokemonModifierEvent<Item>(mod2, true, true);
		final Item test1 = new Item("test item", "", new HashSet<Attribute>(), event1);
		final Item test2 = new Item("test 2", "", new HashSet<Attribute>(), event2);
		event1.setCreator(test1);
		event2.setCreator(test2);
		test1.addAttribute(Attribute.generateAttribute("description"));
		test2.addAttribute(Attribute.generateAttribute("description"));
		final List<Pokemon> addedPokes = new ArrayList<Pokemon>();
		Pokemon lastRemoved = null;
		final Scanner c = new Scanner(System.in);
		boolean letRun = false;
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			try {
				b.update();
				if (b.hasRemoveRequest())
					b.removeThing(b.getNextRemoveRequest());
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
		while (true) {
			if (!letRun && c.hasNextLine()) {
				final String input = c.nextLine();
				if (input.equals("run")) {
					letRun = true;
				}
				if (input.equals("test1")) {
					b.addThing(test1);
					System.out.println(b);
				}
				if (input.equals("test2")) {
					b.addThing(test2);
					System.out.println(b);
				}
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
				if (letRun) {
					Thread.sleep(1000);
					System.out.println(b);
				}
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
