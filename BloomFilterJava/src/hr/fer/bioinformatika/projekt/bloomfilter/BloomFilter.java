package hr.fer.bioinformatika.projekt.bloomfilter;

import hr.fer.bioinformatika.projekt.hash.HashFunction;
import hr.fer.bioinformatika.projekt.hash.HashFunctionFactory;

import java.util.Collection;

/**
 * Implementation of a basic Bloom Filter.
 * <p>
 * By definition, the filter consists of a <em>m</em>-bit array and <em>k</em>
 * hash functions.
 * </p>
 * <p>
 * Only two hash functions are used and the rest of the hashes are calculated
 * using the expression:<br>
 * h(i) = h(1) + i * (h2)
 * </p>
 * 
 * @see <a href=
 *      "http://citeseer.ist.psu.edu/viewdoc/download;jsessionid=4060353E67A356EF9528D2C57C064F5A?doi=10.1.1.152.579&rep=rep1&type=pdf"
 *      >Simulate <em>n</em> hash functions by <em>two</em> hash functions</a>
 * @see <a href="http://en.wikipedia.org/wiki/Bloom_filter">Bloom Filter on
 *      Wikipedia</a>
 * @author Ivan Kraljević
 * 
 */
public class BloomFilter<T> {
	/** m-bit array */
	protected boolean[] array;

	/** hash functions used for index calculation */
	protected HashFunction[] hashFunctions;

	/** number of used hash functions */
	protected int k;

	/**
	 * Default constructor.<br>
	 * Bloom filters that don't use the m-bit array but a different data
	 * structure for storing values should use this constructor.
	 * 
	 * @see ScalableBloomFilter
	 */
	protected BloomFilter() {

	}

	/**
	 * Returns a {@code BloomFilter} object with the specified array size and
	 * number of hash functions used.
	 * 
	 * @param m
	 *            array size.
	 * @param k
	 *            number of hash functions.
	 */
	public BloomFilter(int m, int k) {
		setArray(new boolean[m]);
		setHashFunctions(HashFunctionFactory.getBloomFilterHashes());
		setK(k);
	}

	/**
	 * Constructs a {@code BloomFilter} where the array size and number of hash
	 * functions are determined by the estimated number of items that will be
	 * added to the filter and the acceptable false positive probability.
	 * 
	 * @param estimatedNumOfItems
	 *            estimated number of items that will be added to the filter.
	 * @param falsePositiveProbability
	 *            acceptable false positive probability.
	 */
	public BloomFilter(int estimatedNumOfItems, double falsePositiveProbability) {
		this(falsePositiveProbability, (int) (estimatedNumOfItems
				* Math.abs(Math.log(falsePositiveProbability)) / Math.pow(
				Math.log(2), 2)));
	}

	/**
	 * Constructs a {@code BloomFilter} with the specified array size and where
	 * the number of hash functions is determined by the acceptable false
	 * positive probability.
	 * 
	 * @param falsePositiveProbability
	 *            acceptable false positive probability.
	 * @param m
	 *            array size.
	 */
	public BloomFilter(double falsePositiveProbability, int m) {
		this(m, (int) Math.ceil(Math.log(1. / falsePositiveProbability)
				/ Math.log(2.)));
	}

	/**
	 * Adds a item to the filter.
	 * 
	 * @param item
	 *            item to add.
	 */
	public void add(T item) {
		if (item == null) {
			throw new NullPointerException("Invalid input item: " + item);
		}
		byte[] itemBytes = getBytes(item);
		long h1 = hashFunctions[0].getHash(itemBytes);
		long h2 = hashFunctions[1].getHash(itemBytes);
		// calculate the first two indexes and set their array values to true
		int h1Index = (int) (h1 % array.length);
		int h2Index = (int) (h2 % array.length);
		array[h1Index] = true;
		array[h2Index] = true;
		
		// calculate the remaining indexes and set their array values to true
		for (int i = 2; i < k; i++) {
			int index = (int) ((h1 + i * h2) % array.length);
			// if the previous statement caused an overflow the index will be <0
			if (index < 0) {
				index += array.length;
			}
			array[index] = true;
		}
	}

	/**
	 * Adds a collections of items to the filer.
	 * 
	 * @throws NullPointerException
	 *             if the input collection is {@code null}.
	 * @param itemCollection
	 *            items to add.
	 */
	public void addAll(Collection<? extends T> itemCollection) {
		if (itemCollection == null) {
			throw new NullPointerException("Invalid item collection: "
					+ itemCollection);
		}
		for (T item : itemCollection) {
			add(item);
		}
	}

	/**
	 * Returns {@code true} if the specified item is in the filter,
	 * {@code false} otherwise.
	 * 
	 * @throws NullPointerException
	 *             it the input item is {@code null}.
	 * @param item
	 *            input item. Must not be {@code null}.
	 * @return {@code true}if the item is probably in the set, {@code false}
	 *         otherwise.
	 */
	public boolean query(T item) {
		if (item == null) {
			throw new NullPointerException("Invalid input item: " + item);
		}
		byte[] itemBytes = getBytes(item);
		long h1 = hashFunctions[0].getHash(itemBytes);
		long h2 = hashFunctions[1].getHash(itemBytes);
		int h1Index = (int) (h1 % array.length);
		int h2Index = (int) (h2 % array.length);

		if (!array[h1Index] || !array[h2Index]) {
			return false;
		}
		boolean isInFilter = true;
		for (int i = 2; i < k; i++) {
			int index = (int) ((h1 + i * h2) % array.length);
			if (index < 0) {
				index += array.length;
			}
			if (!array[index % array.length]) {
				isInFilter = false;
				break;
			}
		}
		return isInFilter;
	}

	/**
	 * Returns the input item represented as an array of bytes.
	 * 
	 * @throws NullPointerException
	 *             if the item is {@code null}.
	 * @param item
	 *            input item.
	 * @return the resultand byte array.
	 */
	protected byte[] getBytes(T item) {
		if (item == null) {
			throw new NullPointerException("Invalid input item: " + item);
		}
		String s = item.toString();
		byte[] itemBytes = null;
		try {
			itemBytes = s.getBytes("UTF-8");
		} catch (Exception e) {
			itemBytes = s.getBytes();
		}
		return itemBytes;
	}

	/**
	 * Returns the <em>m</em>-bit array.
	 * 
	 * @return <em>m</em>-bit array.
	 */
	protected boolean[] getArray() {
		return array;
	}

	/**
	 * Sets the <em>m</em>-bit array.
	 * 
	 * @throws NullPointerException
	 *             if the input array is {@code null}.
	 * @throws IllegalArgumentException
	 *             if the array size is 0.
	 * @param array
	 *            <em>m</em>-bit array.
	 */
	protected void setArray(boolean[] array) {
		if (array == null) {
			throw new NullPointerException("Invalid filter array: " + array);
		}
		if (array.length == 0) {
			throw new IllegalArgumentException("Invalid filter array size: "
					+ array.length);
		}
		this.array = array;
	}

	/**
	 * Returns the filters hash functions.
	 * 
	 * @return hash functions.
	 */
	protected HashFunction[] getHashFunctions() {
		return hashFunctions;
	}

	/**
	 * Sets the filters hash functions.
	 * 
	 * @throws NullPointerException
	 *             if the input array is {@code null}.
	 * @throws IllegalArgumentException
	 *             if the array size is less than 2.
	 * @param hashFunctions
	 *            hash functions.
	 */
	protected void setHashFunctions(HashFunction[] hashFunctions) {
		if (hashFunctions == null) {
			throw new NullPointerException("Invalid hash functions: "
					+ hashFunctions);
		}
		if (hashFunctions.length < 2) {
			throw new IllegalArgumentException("Invalid hash functions size: "
					+ hashFunctions.length);
		}
		this.hashFunctions = hashFunctions;
	}

	/**
	 * Returns the number of hash functions that are used when adding or
	 * querying an item.
	 * 
	 * @return number of hash functions.
	 */
	protected int getK() {
		return k;
	}

	/**
	 * Sets the number of hash functions that are used when adding or querying
	 * an item.
	 * 
	 * @throws if
	 *             k is less than 1.
	 * @param k
	 *            number of hash functions.
	 */
	protected void setK(int k) {
		if (k <= 0) {
			throw new IllegalArgumentException(
					"Number of hash functions must be larger than 0.");
		}
		this.k = k;
	}
}
