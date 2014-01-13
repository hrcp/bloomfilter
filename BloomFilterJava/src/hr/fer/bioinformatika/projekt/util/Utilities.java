package hr.fer.bioinformatika.projekt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class whose static methods do various simple jobs like file
 * import/export.
 * 
 * @author Ivan
 * 
 */
public class Utilities {

	/**
	 * Reads the file from the specified location and returns a collections
	 * containing the file's content.
	 * 
	 * @param path
	 *            path of the input text file.
	 * @return {@code Collection} containing the content of the input text file.
	 */
	public static Collection<String> loadStrings(String path) {
		List<String> words = new ArrayList<String>();
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(new File(path)));
			String l = null;
			while ((l = r.readLine()) != null) {
				words.add(l);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return words;
	}

	/**
	 * Reads the file from the specified location and returns a collection
	 * containing the file's content.
	 * <p>
	 * The content must be in FASTA format.
	 * </p>
	 * <p>
	 * A sequence in FASTA format begins with a single-line description,
	 * followed by lines of sequence data. The description line is distinguished
	 * from the sequence data by a greater-than (">") symbol in the first
	 * column.
	 * </p>
	 * 
	 * @param path
	 * @return
	 */
	public static Collection<String> loadFasta(String path) {
		List<String> words = new ArrayList<String>();
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(new File(path)));
			String l = null;
			String currentString = "";
			while ((l = r.readLine()) != null) {
				if (l.trim().startsWith(">")) {
					words.add(currentString);
					currentString = "";
				} else {
					currentString += l;
				}
			}
			words.add(currentString);
			words.remove(0);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return words;
	}
}
