package hr.fer.bioinformatika.projekt.bloomfilter;

/**
 * A Partitioned Bloom Filter is a variant of the original Bloom Filter.
 * <p>
 * The only difference is that the <em>m</em>-bit array of a Partitioned Bloom
 * Filter is divided in to <em>k</em> slices (<em>k</em> is the number of hash
 * functions in use) so that every hash function produces an index in his own
 * splice.
 * </p>
 * <p>
 * Partitioned Bloom Filters are more robust than regular Bloom Filters and with
 * no elements specially sensitive to false positives.
 * </p>
 * 
 * @see BloomFilter
 * 
 * @author Ivan
 * 
 */
public class PartitionedBloomFilter<T> extends BloomFilter<T> {
	/** Array for storing upper bounds of the array splices */
	protected int[] partitionIndexes;

	/**
	 * Constructs a {@code PartitionedBloomFilter} object with the specified
	 * array size and number of hash functions used.
	 * 
	 * @param m
	 *            array size.
	 * @param k
	 *            number of hash functions.
	 */
	public PartitionedBloomFilter(int m, int k) {
		super(m, k);
		setPartitionIndexes(createPartitionIndexes(m, k));
	}

	/**
	 * Constructs a {@code PartitionedBloomFilter} where the array size and
	 * number of hash functions are determined by the estimated number of items
	 * that will be added to the filter and the acceptable false positive
	 * probability.
	 * 
	 * @param estimatedNumOfItems
	 *            estimated number of items that will be added to the filter.
	 * @param falsePositiveProbability
	 *            acceptable false positive probability.
	 */
	public PartitionedBloomFilter(int estimatedNumOfItems,
			double falsePositiveProbability) {
		super(estimatedNumOfItems, falsePositiveProbability);
		setPartitionIndexes(createPartitionIndexes(array.length, k));
	}

	/**
	 * Constructs a {@code PartitionedBloomFilter} with the specified array size
	 * and where the number of hash functions is determined by the acceptable
	 * false positive probability.
	 * 
	 * @param falsePositiveProbability
	 *            acceptable false positive probability.
	 * @param m
	 *            array size.
	 */
	public PartitionedBloomFilter(double falsePositiveProbability, int m) {
		super(falsePositiveProbability, m);
		setPartitionIndexes(createPartitionIndexes(array.length, k));
	}

	@Override
	public void add(T item) {
		if (item == null) {
			throw new NullPointerException("Invalid input item: " + item);
		}
		byte[] itemBytes = getBytes(item);
		long h1 = hashFunctions[0].getHash(itemBytes);
		long h2 = hashFunctions[1].getHash(itemBytes);

		// calculate the first two indexes and set their array values to true
		int partitionLength = partitionIndexes[1] - partitionIndexes[0];
		int h1Index = (int) (h1 % partitionIndexes[0]);
		int h2Index = (int) (h2 % partitionLength) + partitionIndexes[0];
		array[h1Index] = true;
		array[h2Index] = true;

		// calculate the remaining indexes and set their array values to true
		for (int i = 2; i < k; i++) {
			partitionLength = partitionIndexes[i] - partitionIndexes[i - 1];
			int index = (int) ((h1 + i * h2) % partitionLength);
			// if the previous statement caused an overflow the index will be <0
			if (index < 0) {
				index += partitionLength;
			}

			index += partitionIndexes[i - 1];
			array[index] = true;
		}
	}

	@Override
	public boolean query(T item) {
		if (item == null) {
			throw new NullPointerException("Invalid input item: " + item);
		}
		byte[] itemBytes = getBytes(item);
		long h1 = hashFunctions[0].getHash(itemBytes);
		long h2 = hashFunctions[1].getHash(itemBytes);

		// calculate indexes of the first two hashes and query the array
		int partitionLength = partitionIndexes[1] - partitionIndexes[0];
		int h1Index = (int) (h1 % partitionIndexes[0]);
		int h2Index = (int) (h2 % partitionLength) + partitionIndexes[0];
		if (!array[h1Index] || !array[h2Index]) {
			return false;
		}

		// calculate the remaining indexes and query the array
		boolean isInFilter = true;
		for (int i = 2; i < k; i++) {
			partitionLength = partitionIndexes[i] - partitionIndexes[i - 1];
			int index = (int) ((h1 + i * h2) % partitionLength);
			// if the previous statement caused an overflow the index will be <0
			if (index < 0) {
				index += partitionLength;
			}
			index += partitionIndexes[i - 1];

			if (!array[index]) {
				isInFilter = false;
				break;
			}
		}
		return isInFilter;
	}

	/**
	 * Returns the array with the upper bounds of <em>m-bit</em> array splices.
	 * 
	 * @param m
	 *            number of bits.
	 * @param k
	 *            number of splices/partitions.
	 * @return array with the upper bounds.
	 */
	public int[] createPartitionIndexes(int m, int k) {
		int[] indexes = new int[k];
		int bitsPerFunction = m / k;
		int remainder = m % k;
		int lastIndex = 0;
		for (int i = 0; i < k; i++) {
			int additionalBit = (remainder > 0) ? 1 : 0;
			remainder--;
			indexes[i] = lastIndex + bitsPerFunction + additionalBit;
			lastIndex = indexes[i];
		}
		return indexes;
	}

	/**
	 * Returns the upper bounds of the <em>m-bit</em> array splices.
	 * 
	 * @return array with the upper bounds of the <em>m-bit</em> array splices.
	 */
	protected int[] getPartitionIndexes() {
		return partitionIndexes;
	}

	/**
	 * Sets the upper bounds of the <em>m-bit</em> array splices.
	 * 
	 * @param partitionIndexes
	 *            array containing the upper bounds.
	 */
	protected void setPartitionIndexes(int[] partitionIndexes) {
		if (partitionIndexes == null) {
			throw new NullPointerException();
		}
		this.partitionIndexes = partitionIndexes;
	}
}
