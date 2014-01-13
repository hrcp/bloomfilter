package hr.fer.bioinformatika.projekt;

import hr.fer.bioinformatika.projekt.bloomfilter.BloomFilter;
import hr.fer.bioinformatika.projekt.bloomfilter.ScalableBloomFilter;
import hr.fer.bioinformatika.projekt.util.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

/**
 * {@code Main} contains the main function that starts a simple interactive
 * application where the user can query if a element is a bloom filter.
 * 
 * @author Ivan Kraljević
 * 
 */
public class Main {

	/**
	 * Simple argument parser.
	 * 
	 * @param args
	 *            user defined values
	 * @return user defined or default values
	 */
	private static String[] parseArgs(String[] args) {
		String[] a = new String[] { "keyList.txt", "0" };
		if (args == null || args.length == 0) {
			return a;
		}
		File f = new File(args[0]);
		if (f.exists()) {
			a[0] = args[0];
		}
		if (args.length > 1 && args[1].startsWith("1")) {
			a[1] = "1";
		}
		return a;
	}

	/**
	 * Loads the dictionary from the specified location.
	 * 
	 * @param path
	 *            file path
	 * @param isFasta
	 *            1 if the data is stored in FASTA format
	 * @return data collection
	 */
	public static Collection<String> getData(String path, String isFasta) {
		if (isFasta.startsWith("1")) {
			return Utilities.loadFasta(path);
		} else {
			return Utilities.loadStrings(path);
		}
	}

	/**
	 * Loads the dictionary from the specified location and adds it to a bloom
	 * filter.<br>
	 * Then the user can query the bloom filter via the system console.
	 * <p>
	 * ARGS:
	 * <ul>
	 * <li>"file path"</li>
	 * <li>"1" - if the data is stored in FASTA format</li>
	 * </ul>
	 * </p>
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		args = parseArgs(args);
		Collection<String> words = getData(args[0], args[1]);
		BloomFilter<String> filter = new ScalableBloomFilter<String>(50000,
				0.001);
		filter.addAll(words);
		double loadTime = (System.currentTimeMillis() - startTime) / 1000.;
		System.out.println("Key List loaded in: " + loadTime + " seconds.");
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(
					System.in));
			do {
				System.out.print("Query: ");
				String l = r.readLine();
				if (l == null || l.length() == 0) {
					break;
				}
				System.out.println("Is '" + l + "' in the filter: "
						+ filter.query(l));
			} while (true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
