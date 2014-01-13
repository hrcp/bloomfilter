package hr.fer.bioinformatika.projekt.hash;

/**
 * The {@code HashFunction} interface provides the {@code getHash} method to
 * create a hash code of the input array of bytes.
 * 
 * @author Ivan Kraljević
 * 
 */
public interface HashFunction {
	/**
	 * Returns a hash code of the input array of bytes.
	 * 
	 * @param input
	 *            the input array of bytes. Must not be {@code null}.
	 * 
	 * @return hash value of the input represented as a 32bit value.
	 */
	public long getHash(byte[] input);
}
