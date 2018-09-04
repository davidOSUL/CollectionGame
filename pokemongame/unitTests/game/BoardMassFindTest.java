package game;

import attributes.ParseType;
import thingFramework.Creature;

public class BoardMassFindTest {

	public static void main (final String...strings) {
		/*Board b = new Board();
		for (int i =0; i < 100000; i++) {
			b.lookForCreature(true);
			if (b.wildCreaturePresent())
				b.addThing(b.grabAndConfirm());
		}
		System.out.println("hi");*/
		final Board b = new Board();
		int popVal = 0;
		b.update();
		try {
			Thread.sleep(20);
		} catch (final InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		b.update();
		while (b.wildCreaturePresent()) {
			Creature p = null;
			while (p == null) {
			b.update();
			p = b.grabWildCreature();
			}
			b.confirmGrab();
			popVal+= p.getAttributeValue("popularity boost", ParseType.INTEGER);
			System.out.println(popVal);
			b.addThing(p);
			b.update();
			if (popVal != b.getPopularity())
				System.out.println("WHAT");
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
