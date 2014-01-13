package hr.fer.bioinformatika.projekt.hash;

/**
 * Implementation of the MurmurHash3 algorithm.
 * 
 * @see HashFunction
 * @see <a href="http://code.google.com/p/smhasher/">MurmurHash project on
 *      Google Code</a>
 * @see <a href="http://en.wikipedia.org/wiki/MurmurHash">MurmurHash on
 *      Wikipedia</a>
 * @author Ivan Kraljević
 * 
 */
public class MurmurHash implements HashFunction {
	/** MurmurHash constant used for bit mixing */
	protected static int c1 = 0xcc9e2d51;

	/** MurmurHash constant used for bit mixing */
	protected static int c2 = 0x1b873593;

	/** MurmurHash constant used for bit mixing */
	protected static int r1 = 15;

	/** MurmurHash constant used for bit mixing */
	protected static int r2 = 13;

	/** MurmurHash constant used for bit mixing */
	protected static int m = 5;

	/** MurmurHash constant used for bit mixing */
	protected static int n = 0xe6546b64;

	/** Initial seed */
	protected long seed = 0;

	/**
	 * Constructs a new {@code MurmurHash} where the initial seed is set to
	 * zero.
	 */
	public MurmurHash() {
	}

	/**
	 * Constructs a new {@code MurmurHash} with the specified initial seed.
	 * 
	 * @param seed
	 *            initial seed.
	 */
	public MurmurHash(int seed) {
		this.seed = seed;
	}

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
	 * Uses the MurmurHash3 algorithm.
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
	public long calculateHash(byte[] data, int offset, int len, long seed) {
		long hash = seed;
		int roundedLen = offset + (len & 0xfffffffc);

		for (int i = offset; i < roundedLen; i += 4) {
			// little endian order
			int k = (data[i] & 0xff) | ((data[i + 1] & 0xff) << 8)
					| ((data[i + 2] & 0xff) << 16) | (data[i + 3] << 24);
			k *= c1;
			k = (k << r1) | (k >>> (32 - r1));
			k *= c2;
			hash ^= k;
			hash = (hash << r2) | (hash >>> r2);
			hash = hash * m + n;
		}

		// remaining bytes
		int k = 0;
		switch (len & 0x03) {
		case 3:
			k = (data[roundedLen + 2] & 0xff) << 16;
		case 2:
			k |= (data[roundedLen + 1] & 0xff) << 8;
		case 1:
			k |= (data[roundedLen] & 0xff);
		}
		k *= c1;
		k = (k << 15) | (k >>> 17);
		k *= c2;
		hash ^= k;

		// mixing
		hash ^= len;
		hash ^= hash >>> 16;
		hash *= 0x85ebca6b;
		hash ^= hash >>> 13;
		hash *= 0xc2b2ae35;
		hash ^= hash >>> 16;
		return hash & 0x00000000ffffffffL;
	}

}
