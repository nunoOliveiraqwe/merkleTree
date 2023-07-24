package com.nunoOliveiraqwe.merkleTree;

/**
 * An printer interface that allows to abstract away here the content is being printed on
 * @param <V> - The info that will be printed
 */
public interface Printer<V extends Comparable<V>> {

    void print(V hash);

    void newLine();

}
