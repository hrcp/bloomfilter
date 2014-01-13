package hr.fer.bioinformatika.projekt.hash;



/**
 * Simple factory class for creating new hash functions.
 * 
 * @see HashFunction
 * @see MurmurHash
 * @see FNV1HashFunction
 * @see FNV1aHashFunction
 * @author Ivan Kraljević
 * 
 */
public class HashFunctionFactory {

	/**
	 * Returns an array of all implemented hash functions.
	 * 
	 * @return array which contains all implemented hash functions.
	 */
	public static HashFunction[] getAll() {
		HashFunction[] functions = new HashFunction[] { new FNV1HashFunction(),
				new FNV1aHashFunction(), new MurmurHash() };
		return functions;
	}

	/**
	 * Returns the MurmurHash3 and Fowler–Noll–Vo-1a implementations.
	 * 
	 * @return array which contains the MurmurHash3 and Fowler-Noll-Vo-1a
	 *         implementations.
	 */
	public static HashFunction[] getBloomFilterHashes() {
		HashFunction[] functions = new HashFunction[] {
				new FNV1aHashFunction(), new MurmurHash() };
		return functions;
	}
	/**
	 * Returns the specified hash function.
	 * 
	 * @param functionName
	 *            the name of the requested algorithm.
	 * @return the requested algorithm or {@code null} if the specified
	 *         algorithm isn't implemented.
	 */
	public static HashFunction get(String functionName) {
		functionName = functionName.trim().toLowerCase();
		HashFunction function = null;
		if (functionName.startsWith("murmur")) {
			function = new MurmurHash();
		} else if (functionName.startsWith("fnv1a")) {
			function = new FNV1aHashFunction();
		} else if (functionName.startsWith("fnv1")) {
			function = new FNV1HashFunction();
		}
		return function;
	}
}
