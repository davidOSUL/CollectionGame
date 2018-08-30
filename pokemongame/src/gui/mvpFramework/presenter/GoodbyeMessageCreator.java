package gui.mvpFramework.presenter;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import loaders.CSVReader;

class GoodbyeMessageCreator {
	private static final String pathToMessages = "/InputFiles/GoodbyeMessages - 1.csv";
	private final String message;
	GoodbyeMessageCreator() throws IOException {
		final List<String[]> messages = CSVReader.readCSV(pathToMessages);
		message = messages.get(new Random().nextInt(messages.size()))[0];
	}
	public String getMessage() {
		return message;
	}
}
