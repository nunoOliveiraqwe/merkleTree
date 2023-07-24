package com.nunoOliveiraqwe.merkleTree;

import java.util.List;

/**
 * An hash builder that creates an hash out of a set of hashes
 */
@FunctionalInterface
public interface HashBuilder<V extends Comparable<V>> {

    /**
     * Creates an hash out of a set of hashes
     * @param hashes - The hashes to hash
     * @return - A single hash value
     */
    V hash(List<V> hashes);

}
