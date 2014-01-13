package hr.fer.bioinformatika.projekt.bloomfilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Scalable Bloom Filter implementation.
 * <p>
 * From the official documentation:<br>
 * <ul>
 * <li>"A Scalable Bloom Filter is made up of a series of one or more Bloom
 * Filters. When a filter gets full due to the limit on the fill ratio, a new
 * one is added."</li>
 * <li>"Querying is made by testing for the presence in each filter."</li>
 * <li>
 * "Each successive bloom filter is created with a tighter maximum error probability on a geometric progression."
 * </li>
 * </p>
 * 
 * @see BloomFilter
 * @see <a href="http://gsd.di.uminho.pt/members/cbm/ps/dbloom.pdf">Paper about
 *      Scalable Bloom Filters</a>
 * @author Ivan Kraljević
 * 
 */
public class ScalableBloomFilter<T> extends BloomFilter<T> {

	/***/
	protected double falsePositiveProbability;
	/** maximum number of items that can be added to a filter */
	protected int fillRatioLimit;

	/** bloom filter list */
	protected List<BloomFilter<T>> filters;

	/** number of items in the current bloom filter */
	protected int itemsInCurrent;

	/** tighening ratio */
	protected double r;

	/**
	 * 
	 * @param fillRatioLimit
	 *            maximum number of items that can be added to a filter.
	 * @param falsePositiveProbability
	 *            acceptable false positive probability.
	 * @param r
	 *            tightening ratio
	 */
	public ScalableBloomFilter(int fillRatioLimit,
			double falsePositiveProbability, double r) {
		this(fillRatioLimit, falsePositiveProbability);
		setR(r);
	}

	/**
	 * 
	 * @param fillRatioLimit
	 *            maximum number of items that can be added to a filter.
	 * @param falsePositiveProbability
	 *            acceptable false positive probability.
	 */
	public ScalableBloomFilter(int fillRatioLimit,
			double falsePositiveProbability) {
		setFilters(new ArrayList<BloomFilter<T>>());
		setFillRatioLimit(fillRatioLimit);
		setFalsePositiveProbability(falsePositiveProbability);
		setItemsInCurrent(0);
		setR(0.9);
		filters.add(new PartitionedBloomFilter<T>(fillRatioLimit,
				falsePositiveProbability));
	}
	@Override
	public void add(T item) {
		BloomFilter<T> currentFilter = filters.get(filters.size() - 1);
		currentFilter.add(item);
		itemsInCurrent++;
		if (itemsInCurrent >= fillRatioLimit) {
			addNewFilter();
		}
	}

	@Override
	public boolean query(T item) {
		boolean isInFilter = false;
		for (int i = 0; i < filters.size(); i++) {
			isInFilter = queryFilter(i, item);
			if (isInFilter) {
				break;
			}
		}
		return isInFilter;
	}

	/**
	 * Returns {@code true} if the filter contains the input item.
	 * 
	 * @param index
	 *            filter index
	 * @param item
	 *            input item
	 * @return {@code true} if the filter contains the input item, {@code false}
	 *         otherwise.
	 */
	public boolean queryFilter(int index, T item) {
		return filters.get(index).query(item);
	}

	/**
	 * Adds a new {@code PartitionedBloomFilter} to the list.
	 * <p>
	 * The false-positive probability of the new filter is:
	 * <code>p(i)=p(i-1)*r</code>
	 * </p>
	 * The number of hash functions in the new filter is:
	 * <code>k(i)=k(0)+i</code>
	 * <p>
	 * The <em>m</em>-bit array size of the new filter is:
	 * <code>fillRatioLimit*|ln(p(i))|/(ln2)^2</code>
	 * </p>
	 */
	protected void addNewFilter() {
		falsePositiveProbability *= r;
		itemsInCurrent = 0;
		filters.add(new PartitionedBloomFilter<T>(fillRatioLimit,
				falsePositiveProbability));
	}

	/**
	 * Returns the current false positive probability.
	 * 
	 * @return current false positive probability.
	 */
	protected double getFalsePositiveProbability() {
		return falsePositiveProbability;
	}

	/**
	 * Sets the false positive probability.
	 * 
	 * @throws IllegalArgumentException
	 *             if the value is less than 0 or greater than 1.
	 * @param falsePositiveProbability
	 *            false positive probability.
	 */
	protected void setFalsePositiveProbability(double falsePositiveProbability) {
		if (falsePositiveProbability < 0 || falsePositiveProbability > 1) {
			throw new IllegalArgumentException(
					"False-positive probability must be between 0 and 1.");
		}
		this.falsePositiveProbability = falsePositiveProbability;
	}

	/**
	 * Returns the maximum number of items that can be stored in a filter.
	 * 
	 * @return maximum number of items that can be stored in a filter.
	 */
	protected int getFillRatioLimit() {
		return fillRatioLimit;
	}

	/**
	 * Sets the maximum number of items that can be stored in a filter.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified value is less or equal zero.
	 * @param fillRatioLimit
	 *            maximum number of items that can be stored in a filter.
	 */
	protected void setFillRatioLimit(int fillRatioLimit) {
		if (fillRatioLimit <= 0) {
			throw new IllegalArgumentException(
					"Fill ratio limit must be greater than 0.");
		}
		this.fillRatioLimit = fillRatioLimit;
	}

	/**
	 * Returns a list of {@code BloomFilter} that are used for querying.
	 * 
	 * @return list containing {@code BloomFilter}.
	 */
	protected List<BloomFilter<T>> getFilters() {
		return filters;
	}

	/**
	 * Sets the filters list.
	 * 
	 * @param filters
	 *            list for storing filters.
	 */
	protected void setFilters(List<BloomFilter<T>> filters) {
		if (filters == null) {
			throw new NullPointerException(
					"The filters array must not be null!");
		}
		this.filters = filters;
	}

	/**
	 * Returns the number of items in the last added filter.
	 * 
	 * @return number of items in the last filter.
	 */
	protected int getItemsInCurrent() {
		return itemsInCurrent;
	}

	/**
	 * Sets the number of items in the last added filter.<br>
	 * To avoid unwanted behavior, use only in constructors.
	 * 
	 * @param itemsInCurrent
	 *            number of items in the last added filter.
	 */
	protected void setItemsInCurrent(int itemsInCurrent) {
		this.itemsInCurrent = itemsInCurrent;
	}

	/**
	 * Returns the tightening ratio.
	 * 
	 * @return tightening ratio.
	 */
	protected double getR() {
		return r;
	}

	/**
	 * Sets the tightening ratio to the specified value.
	 * 
	 * @throws IllegalArgumentException
	 *             if the specified value is not between [0,1]
	 * @param r
	 *            tightening ratio.
	 */
	protected void setR(double r) {
		if (r < 0 || r > 1) {
			throw new IllegalArgumentException(
					"The tightening ratio must be between 0 and 1");
		}
		this.r = r;
	}
}
