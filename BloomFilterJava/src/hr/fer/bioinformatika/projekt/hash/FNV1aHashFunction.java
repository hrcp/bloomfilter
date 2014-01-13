package hr.fer.bioinformatika.projekt.hash;


/**
 * Implementation of the Fowler–Noll-Vo-1a hash algorithm.
 * 
 * @see HashFunction
 * @see <a
 *      href="http://www.isthe.com/chongo/tech/comp/fnv/index.html#FNV-1a">Official
 *      documentation</a>
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function">FNV
 *      on Wikipedia</a>
 * @author Ivan Kraljević
 * 
 */
public class FNV1aHashFunction implements HashFunction {
	/** initial seed for the 32 bit hashes */
	long seed = 2166136261L;

	@Override
	public long getHash(byte[] input) {
		if (input == null) {
			throw new NullPointerException("Input array must not be null!");
		}
		return calculateHash(input, 0, input.length, seed);
	}

	/**
	 * Calculates the hash value of the input array of bytes starting at
	 * {@code offset}.<br>
	 * Uses the FNV-1a hash algorithm.
	 * 
	 * @param data
	 *            the array of bytes.
	 * @param offset
	 *            the offset to start from in the array of bytes.
	 * @param len
	 *            the number of bytes to use, starting at offset.
	 * @param seed
	 *            starting seed.
	 * @return hash value of the input data represented as a 32bit value.
	 */
	private long calculateHash(byte[] data, int offset, int len, long seed) {
		long hash = seed;
		for (int i = offset; i < offset + len; i++) {
			hash ^= data[i];
			hash += (hash << 1) + (hash << 4) + (hash << 7) + (hash << 8)
					+ (hash << 24);
		}
		return hash & 0x00000000ffffffffL;
	}
}
