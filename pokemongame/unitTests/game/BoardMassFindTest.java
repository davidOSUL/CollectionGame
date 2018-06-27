package game;

import thingFramework.Pokemon;

public class BoardMassFindTest {

	public static void main (String...strings) {
		/*Board b = new Board();
		for (int i =0; i < 100000; i++) {
			b.lookForPokemon(true);
			if (b.wildPokemonPresent())
				b.addThing(b.grabAndConfirm());
		}
		System.out.println("hi");*/
		Board b = new Board();
		int popVal = 0;
		b.update();
		try {
			Thread.sleep(20);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		b.update();
		while (b.wildPokemonPresent()) {
			Pokemon p = null;
			while (p == null) {
			b.update();
			p = b.grabWildPokemon();
			}
			b.confirmGrab();
			popVal+= (Integer) p.getAttributeVal("popularity boost");
			System.out.println(popVal);
			b.addThing(p);
			b.update();
			if (popVal != b.getPopularity())
				System.out.println("WHAT");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
