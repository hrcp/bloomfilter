package hr.fer.bioinformatika.projekt.bloomfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

public class ScalableBloomFilterTest {

	@Test
	public void testAdd() {
		BloomFilter<String> filter = new ScalableBloomFilter<String>(100, 0.01);
		filter.add("testItem");
	}

	@Test
	public void testAddAll() {
		Collection<String> items = new ArrayList<String>();
		items.add("testItem1");
		items.add("testItem2");
		items.add("testItem3");
		BloomFilter<String> filter = new ScalableBloomFilter<String>(100, 0.01);
		filter.addAll(items);
	}

	@Test
	public void testQuery() {
		String testItem = "testItem";
		BloomFilter<String> filter = new ScalableBloomFilter<String>(100, 0.01);
		filter.add(testItem);
		assertTrue("Added item MUST be in a filter",
				filter.query(testItem));
		assertFalse("Random item must not be in this filter",
				filter.query("randomItem"));
	}

	@Test
	public void testScalableBloomFilterIntDoubleDouble() {
		int fillRatioLimit = 100;
		double falsePositiveProbability = 0.01;
		double tighteningRatio = 0.9;
		double delta = 1e-8;
		ScalableBloomFilter<String> filter = new ScalableBloomFilter<String>(
				fillRatioLimit, falsePositiveProbability, tighteningRatio);
		assertEquals(
				"The user defined and bloom filter fill ratio limits must be equal.",
				fillRatioLimit, filter.fillRatioLimit);
		assertEquals(
				"The user defined and bloom filter acceptable false positive probabilities must be equal",
				falsePositiveProbability,
				filter.falsePositiveProbability, delta);
		assertEquals(
				"The user defined and bloom filter tightening ratios must be equal",
				tighteningRatio, filter.r, delta);
		assertNotNull("The filter list must not be null", filter.filters);
		assertEquals("", 1, filter.getFilters().size());
	}

	@Test
	public void testScalableBloomFilterIntDouble() {
		int fillRatioLimit = 100;
		double falsePositiveProbability = 0.01;
		double delta = 1e-8;
		ScalableBloomFilter<String> filter = new ScalableBloomFilter<String>(
				fillRatioLimit, falsePositiveProbability);
		assertEquals(
				"The user defined and bloom filter fill ratio limits must be equal.",
				fillRatioLimit, filter.fillRatioLimit);
		assertEquals(
				"The user defined and bloom filter acceptable false positive probabilities must be equal",
				falsePositiveProbability, filter.falsePositiveProbability,
				delta);
		assertEquals("The default tightening ratio must be 0.9",
				0.9, filter.r, delta);
		assertNotNull("The filter list must not be null", filter.filters);
		assertEquals("", 1, filter.getFilters().size());
	}

	@Test
	public void testQueryFilter() {
		String testItem = "testItem";
		ScalableBloomFilter<String> filter = new ScalableBloomFilter<String>(
				100, 0.01);
		filter.add(testItem);
		Assert.assertTrue("Added item MUST be in a filter",
				filter.filters.get(0).query(testItem));
		Assert.assertFalse("Random item must not be in this filter",
				filter.query("randomItem"));
	}

	@Test
	public void testAddNewFilter() {
		ScalableBloomFilter<String> filter = new ScalableBloomFilter<String>(
				100, 0.01);
		Assert.assertEquals("The starting number of filters must be 1", 1,
				filter.getFilters().size());
		filter.addNewFilter();
	}
}
