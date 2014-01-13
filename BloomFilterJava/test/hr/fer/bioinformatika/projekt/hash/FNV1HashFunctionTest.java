package hr.fer.bioinformatika.projekt.hash;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FNV1HashFunctionTest {

	@Test
	public void testGetHash() {
		String testString = "test";
		HashFunction fnv1 = new FNV1HashFunction();
		long outputHash = fnv1.getHash(testString.getBytes());
		assertEquals("The output hash must be 3157003241", 3157003241L,
				outputHash);
	}
}
