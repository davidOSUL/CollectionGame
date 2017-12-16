package loaders;

import java.util.Arrays;

public class TestParse {

	public static void main(String[] args) {
		String s = "\"Hitmonlee\r\n" + 
				"Hitmonchan\r\n" + 
				"Hitmontop\"";
		s = s.replace("\n", "").replace("\r", "").replace("\"", "");
		String[] r = s.split("(?=\\p{Lu})");
		System.out.println(Arrays.toString(r));
	}

}
