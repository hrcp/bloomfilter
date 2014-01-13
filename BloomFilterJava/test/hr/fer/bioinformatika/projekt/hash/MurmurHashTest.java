package hr.fer.bioinformatika.projekt.hash;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MurmurHashTest {

	@Test
	public void testMurmurHash() {
		MurmurHash murmurHash = new MurmurHash();
		assertEquals("Seed must be 0", 0, murmurHash.seed);
	}

	@Test
	public void testMurmurHashInt() {
		int seed = 100;
		MurmurHash murmurHash = new MurmurHash(seed);
		assertEquals("Seed must be 100", seed, murmurHash.seed);
	}

	@Test
	public void testGetHash() {
		String testString = "test";
		HashFunction murmurHash = new MurmurHash();
		long outputHash = murmurHash.getHash(testString.getBytes());
		assertEquals("The output hash must be 2056757994", 2056757994,
				outputHash);
	}
}
