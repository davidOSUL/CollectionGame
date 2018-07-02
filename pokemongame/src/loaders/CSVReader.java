package loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CSVReader {

	private CSVReader() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Reads in a CSV file, returning a list of String[], where each String[] is a given line of the file, split by ","
	 * @param path the path to the file
	 * @return the List of inputs read
	 * @throws IOException if file can't be found
	 */
	public static List<String[]> readCSV(String path) throws IOException {
		return readCSV(path, null);
	}
	/**
	 * Reads in a CSV file, returning a list of String[], where each String[] is a given line of the file, split by ","
	 * @param path the path to the file
	 * @param ignoreFirstLine if true, doesn't read the first line of the file
	 * @return the List of inputs read
	 * @throws IOException if file can't be found
	 */
	public static List<String[]> readCSV(String path, boolean ignoreFirstLine) throws IOException {
		return readCSV(path, null, ",", 0, ignoreFirstLine);
	}
	/**
	 * Reads in a CSV file, returning a list of String[], where each String[] is a given line of the file, split by ",", and with the specified splitlimt
	 * @param path the path to the file
	 * @param splitLimit the limit to split by in split(",", splitLimit)
	 * @return the List of inputs read
	 * @throws IOException if file can't be found
	 */
	public static List<String[]> readCSV(String path, int splitLimit) throws IOException {
		return readCSV(path, null, ",", splitLimit);	
	}
	/**
	 * Reads in a CSV file, returning a list of String[], where each String[] is a given line of the file, split by ","
	 * @param path the path to the file
	 * @param modifyLineBy Before splitting the inputs on the line, replace the line with the output of this function on that line.
	 * If null, do nothing to modify the line
	 * @return the List of inputs read
	 * @throws IOException if file can't be found
	 */
	public static List<String[]> readCSV(String path, Function<String, String> modifyLineBy) throws IOException {
		return readCSV(path, modifyLineBy, ",");
	}
	/**
	 * Reads in a CSV file, returning a list of String[], where each String[] is a given line of the file, split by splitBy
	 * @param path the path to the file
	 * @param modifyLineBy Before splitting the inputs on the line, replace the line with the output of this function on that line.
	 * If null, do nothing to modify the line
	 * @param splitBy the delimiter to split the file by (instead of the default ",")
	 * @return the List of inputs read
	 * @throws IOException if file can't be found
	 */
	public static List<String[]> readCSV(String path, Function<String, String> modifyLineBy, String splitBy) throws IOException {
		return readCSV(path, modifyLineBy, splitBy, 0);
	}
	/**
	 * Reads in a CSV file, returning a list of String[], where each String[] is a given line of the file, split by splitBy with the specified limit
	 * (that is line.split(splitBy, splitLimit) is called on each line of the CSV)
	 * @param path the path to the file
	 * @param modifyLineBy Before splitting the inputs on the line, replace the line with the output of this function on that line.
	 * If null, do nothing to modify the line
	 * @param splitBy the delimiter to split the file by (instead of the default ",")
	 * @param splitLimit the limit to call in split(splitBy, splitLimit)
	 * @return the List of inputs read
	 * @throws IOException if file can't be found
	 */
	public static List<String[]> readCSV(String path, Function<String, String> modifyLineBy, String splitBy, int splitLimit) throws IOException{
		return readCSV(path, modifyLineBy, splitBy, splitLimit, false);
	}
	/**
	 * Reads in a CSV file, returning a list of String[], where each String[] is a given line of the file, split by splitBy with the specified limit
	 * (that is line.split(splitBy, splitLimit) is called on each line of the CSV)
	 * @param path the path to the file
	 * @param modifyLineBy Before splitting the inputs on the line, replace the line with the output of this function on that line.
	 * If null, do nothing to modify the line
	 * @param splitBy the delimiter to split the file by (instead of the default ",")
	 * @param splitLimit the limit to call in split(splitBy, splitLimit)
	 * @param ignoreFirstLine don't parse the first line
	 * @return the List of inputs read
	 * @throws IOException if file can't be found
	 */
	public static List<String[]> readCSV(String path, Function<String, String> modifyLineBy, String splitBy, int splitLimit, boolean ignoreFirstLine) throws IOException{
		if (modifyLineBy == null)
			modifyLineBy = x -> {return x;};
			List<String> doc;
			try (InputStream resource = CSVReader.class.getResourceAsStream(path)) {
				  doc =
				      new BufferedReader(new InputStreamReader(resource,
				          StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
				}
		List<String> lines = doc;
		List<String[]> valueList = new ArrayList<String[]>();
		int start = ignoreFirstLine ? 1 : 0;
		for (int i = start; i < lines.size(); i++) {
			String line = modifyLineBy.apply(lines.get(i));
			valueList.add(line.split(splitBy, splitLimit));
		}
		return valueList;
	}
	
	

}
