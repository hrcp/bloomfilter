package hr.fer.bioinformatika.projekt.hash;

public class MurmurHash implements HashFunction {
	protected static int c1 = 0xcc9e2d51;
	protected static int c2 = 0x1b873593;
	protected static int r1 = 15;
	protected static int r2 = 13;
	protected static int m = 5;
	protected static int n = 0xe6546b64;
	protected int seed = 0;

	@Override
	public int getHash(String s) {
		if (s == null) {
			throw new NullPointerException("Input string must not be null!");
		}
		byte[] data = null;
		try {
			data = s.getBytes();
		} catch (Exception e) {
			data = s.getBytes();
		}
		return calculate(data, 0, data.length, seed);
	}

	@Override
	public int additionalStep(int value) {
		// dodatno raspršivanje
		int hash = value;
		hash ^= 4;
		hash ^= hash >>> 16;
		hash *= 0x85ebca6b;
		hash ^= hash >>> 13;
		hash *= 0xc2b2ae35;
		hash ^= hash >>> 16;
		return hash;
	}

	public int calculate(byte[] data, int offset, int len, int seed) {
		int hash = seed;
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

		// raspršivanje?
		hash ^= len;
		hash ^= hash >>> 16;
		hash *= 0x85ebca6b;
		hash ^= hash >>> 13;
		hash *= 0xc2b2ae35;
		hash ^= hash >>> 16;
		return hash & 0x7fffffff;
	}

}
