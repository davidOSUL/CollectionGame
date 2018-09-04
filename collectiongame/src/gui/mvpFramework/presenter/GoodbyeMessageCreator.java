package gui.mvpFramework.presenter;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import loaders.CSVReader;

/**
 * Generates a random goodbye message when the user quits
 * @author David O'Sullivan
 *
 */
class GoodbyeMessageCreator {
	private static final String pathToMessages = "/InputFiles/GoodbyeMessages - 1.csv";
	private final String message;
	/**
	 * Creates a new GoodbyeMessageCreator with a random goodbyeMessage
	 * @throws IOException
	 */
	GoodbyeMessageCreator() throws IOException {
		final List<String[]> messages = CSVReader.readCSV(pathToMessages);
		message = messages.get(new Random().nextInt(messages.size()))[0];
	}
	/**
	 * Returns the randomly generated goodbyeMessage for this GoodbyeMessageCreator
	 * @return the randomly generated goodbyeMessage for this GoodbyeMessageCreator
	 */
	public String getMessage() {
		return message;
	}
}
