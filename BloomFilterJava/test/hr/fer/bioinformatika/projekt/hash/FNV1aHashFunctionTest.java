package hr.fer.bioinformatika.projekt.hash;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FNV1aHashFunctionTest {

	@Test
	public void testGetHash() {
		String testString = "test";
		HashFunction fnv1a = new FNV1aHashFunction();
		long outputHash = fnv1a.getHash(testString.getBytes());
		assertEquals("The output hash must be 2949673445", 2949673445L,
				outputHash);
	}

}
