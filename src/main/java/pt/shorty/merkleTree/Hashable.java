package pt.shorty.merkleTree;


/***
 * A functional interface meant to be implemented by any class that can be hashed
 * @param <V> - The return type of the hashing function. Usually this will be byte[]
 */
@FunctionalInterface
public interface Hashable<V extends Comparable<V>> {

    /***
     * Returns an hash of the current object. A good hashing function
     * should be used, so that general properties of hashes are fulfilled
     * @return - The hash of the current object
     */
    V hash();

}
