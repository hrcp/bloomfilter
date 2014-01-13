package hr.fer.bioinformatika.projekt.bloomfilter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

public class PartitionedBloomFilterTest {

	@Test
	public void testAdd() {
		BloomFilter<String> filter = new PartitionedBloomFilter<String>(100, 4);
		filter.add("testItem");
	}

	@Test
	public void testAddAll() {
		Collection<String> items = new ArrayList<String>();
		items.add("testItem1");
		items.add("testItem2");
		items.add("testItem3");
		BloomFilter<String> filter = new PartitionedBloomFilter<String>(100, 4);
		filter.addAll(items);
	}

	@Test
	public void testQuery() {
		String testItem = "testItem";
		BloomFilter<String> filter = new PartitionedBloomFilter<String>(100, 4);
		filter.add(testItem);
		assertTrue("Added item MUST be in a filter",
				filter.query(testItem));
		assertFalse("Random item must not be in this filter",
				filter.query("randomItem"));
	}

	@Test
	public void testPartitionedBloomFilterIntInt() {
		PartitionedBloomFilter<String> filter = new PartitionedBloomFilter<String>(
				20, 4);
		boolean[] testArray = new boolean[20];
		Arrays.fill(testArray, false);
		int[] partitionIndexes = new int[] { 5, 10, 15, 20 };
		assertEquals("The m-bit array size must be 20", 20, filter.array.length);
		assertTrue("All of the values in the m-bit array must be false",
				Arrays.equals(testArray, filter.array));
		assertEquals("The hash functions array length must be 2", 2,
				filter.hashFunctions.length);
		assertEquals(
				"The number of used hash functions (parameter k) must be 4", 4,
				filter.k);
		assertEquals("The number of slices must be equal to parameter k",
				filter.k, filter.partitionIndexes.length);
		assertArrayEquals(partitionIndexes, filter.partitionIndexes);
	}

	@Test
	public void testPartitionedBloomFilterIntDouble() {
		double falsePositiveProbability = 0.001;
		int estimatedNumOfItems = 100;
		BloomFilter<String> filter = new PartitionedBloomFilter<String>(
				estimatedNumOfItems, falsePositiveProbability);
		int expectedArraySize = (int) (estimatedNumOfItems
				* Math.abs(Math.log(falsePositiveProbability)) / Math.pow(
				Math.log(2), 2));
		int expectedK = (int) Math.ceil(Math.log(1. / falsePositiveProbability)
				/ Math.log(2.));
		assertEquals(expectedArraySize, filter.array.length);
		assertEquals(expectedK, filter.k);
	}

	@Test
	public void testPartitionedBloomFilterDoubleInt() {
		int arraySize = 100;
		double falsePositiveProbability = 0.001;
		BloomFilter<String> filter = new PartitionedBloomFilter<String>(
				falsePositiveProbability, arraySize);
		int expectedK = (int) Math.ceil(Math.log(1. / falsePositiveProbability)
				/ Math.log(2.));
		assertEquals("Array sizes must be equal", arraySize,
				filter.array.length);
		assertEquals(expectedK, filter.k);
	}

	@Test
	public void testCreatePartitionIndexes() {
		PartitionedBloomFilter<String> filter = new PartitionedBloomFilter<String>(
				20, 4);
		int[] partitionIndexes = new int[] { 5, 10, 15, 20 };
		assertEquals("The number of slices must be equal to parameter k",
				filter.k, filter.partitionIndexes.length);
		assertArrayEquals(partitionIndexes, filter.partitionIndexes);
	}

}
