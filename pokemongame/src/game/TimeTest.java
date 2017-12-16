package game;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import effects.CustomPeriodEvent;
import loaders.EventBuilder;
import thingFramework.Attribute;
import thingFramework.EventfulItem;
import thingFramework.Pokemon;

public class TimeTest {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Board b = new Board();
//		b.addThing(1, new EventfulItem("test", "test", new HashSet<Attribute>(), EventBuilder.generateRandomGoldEvent(100, 5, .5)));
//		b.addThing(2, new EventfulItem("test2", "test2", new HashSet<Attribute>(), new CustomPeriodEvent(((Board board) -> { board.addPopularity(1);}),
//		board -> {return 1.0-board.getPopularity()/2.0;})));
//
//		while (true) {
//			//System.out.println(1.0-b.getPopularity()/2.0);
//			b.update();
//			Thread.sleep(10);
//			System.out.println(b.getGold() + " " + b.getPopularity() + " " + b.getTotalGameTime());
//		}
		b.setPopularity(50);
		int i = 3;
		while (true) {
			b.update();
			if (b.wildPokemonPresent()) {
				Pokemon p = b.getWildPokemon();
				System.out.println(p + " " + b.getTotalGameTime());
				b.addThing(i++, p);
			}
			Thread.sleep(10);
		}
		//		TreeMap<Integer, Integer> set = new TreeMap<Integer, Integer>();
//		set.put(1, 1);
//		set.put(2, 2);
//		set.put(3, 3);
//		Set<Integer> sett = set.tailMap(4, false).keySet();
//		for (Integer i: sett) {
//			System.out.println(i);
//		}
//		Attribute at = Attribute.generateAttribute("gph", "5");
//		Set<Attribute> test = new HashSet<Attribute>();
//		Map<String, Attribute> test2 = new HashMap<String, Attribute>();
//		test.add(at);
//		test2.put(at.getName(), at);
//		for (Attribute att: test) {
//			System.out.println(att.getValue());
//		}
//		test = Collections.unmodifiableSet(test);
//		test2.get(at.getName()).setValue(new Integer(7));
//		for (Attribute att: test) {
//			System.out.println(att.getValue());
//		}
//		Board b = new Board();
//		Thread t = new Thread(new Runnable() {
//			public void run() {
//				while (true) {
//				b.update();
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				}
//			}
//		});
//		t.start();
//		System.out.println(b.getTotalGameTime());
//		System.out.println(b.getSessionGameTime());
//		b.pause();
//		Thread.sleep(5000);
//		System.out.println(b.getTotalGameTime());
//		System.out.println(b.getSessionGameTime());
//		Thread.sleep(5000);
//		b.unPause();
//		System.out.println(b.getTotalGameTime());
//		System.out.println(b.getSessionGameTime());
//		Thread.sleep(1000);
//		System.out.println(b.getTotalGameTime());
//		System.out.println(b.getSessionGameTime());

		//		Board b = new Board();
//		for (int j =0; j <3; j++) {
//			for (int i =0; i < 100; i++) {
//				b.update();
//				System.out.println(b.getTotalGameTime());
//				Thread.sleep(10);
//			}
//
//			System.out.println("sleeping");
//			Thread.sleep(5000);
//			File f = new File("saveinfo");
//			FileOutputStream fos = new FileOutputStream(f);
//			ObjectOutputStream oos = new ObjectOutputStream(fos);
//			oos.writeObject(b);
//			oos.flush();
//			oos.close();
//			FileInputStream fis = new FileInputStream(f);
//			ObjectInputStream ois = new ObjectInputStream(fis);
//			b = (Board) ois.readObject();
//			ois.close();
//			b.newSession(b.getTotalGameTime());
//		}
//		

	}

}
