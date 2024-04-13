package pt.shorty.merkleTree;

import java.util.Objects;

/**
 * A leaf node is the lowest level node in a merkle tree. It's the only node
 * type that contains actual data
 */
public class LeafNode<V extends Comparable<V>,T extends Hashable<V>> extends AbstractNode<V> {

    /**
     * Represents the parent of this leaf
     */
    private Node<V> parent;

    /**
     * The underlying data of this leaf. Usually merkle tree implementations will only
     * save the hash as part of the leaf or lazily calculate the hash at the retrieve moment.
     * The approach followed here is somewhat on the lazy side, however the calculation of the
     * hash is delegated to the data object itself, which can then decide on the algo and if
     * it should cache the value or not. A proper implementation of {@link Hashable} will
     * always use the same algo for every instance of the same type, but this class implementation makes
     * no guarantees of consistency of hashing functions between all leaf nodes.
     */
    private final T hashableData;

    /**
     * Creates a new instance of a LeafNode
     * @param parent - The parent of this node
     * @param hashableData - The data this node will hold
     */
    public LeafNode(Node<V> parent, T hashableData) {
        super(0);
        this.parent = parent;
        this.hashableData = hashableData;
    }


    /**
     * Creates a new instance of a LeafNode
     * @param hashableData - The data this node will hold
     */
    public LeafNode(T hashableData) {
        super(0);
        this.hashableData = hashableData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V hash() {
        return hashableData.hash();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isLeaf() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRight() {
        return parent.isRightNode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setParent(Node node) {
        this.parent = node;
    }

    /**
     * {@inheritDoc}
     *
     * Leaf node has no childs
     */
    @Override
    AbstractNode<V> getLeftChild() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    AbstractNode<V> getRightChild() {
        return null;
    }

    /**
     * Returns the data of this leaf
     * @return - The data
     */
    public T getData(){
        return hashableData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeafNode<V,T> leafNode;
        leafNode = (LeafNode<V,T>) o;
        return this.hash().compareTo(leafNode.hash()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.hash());
    }
}
