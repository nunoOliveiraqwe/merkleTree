package com.nunoOliveiraqwe.merkleTree;


/**
 * An abstract node of the merkel tree.
 * This abstract node provides the base impl for all other node types
 */
public abstract class AbstractNode<V extends Comparable<V>> {

    /**
     * The level at which this node resides.
     * For example, leaf node should be at 0
     */
    private final int level;

    /**
     * Constructor for AbstractNode. Takes
     * only one param
     * @param level - The depth of this node in the tree
     */
    public AbstractNode(int level) {
        this.level = level;
    }

    /**
     * Determines the hash of this node
     * @return - The hash of the node
     */
    abstract V hash();

    /**
     * Checks if this node is a leaf node
     * @return - True if this node is a leaf, false otherwise
     */
    abstract boolean isLeaf();

    /**
     * Checks if this node is the left node of it's parent
     * @return - True if it's the left, false if it's the right
     */
    public final boolean isLeft() {
        return !isRight();
    }

    /**
     * Checks if this node is the right node of it's parent
     * @return - True if it's the right, false if it's the left
     */
    public abstract boolean isRight();

    /**
     * Gets the current level of the node
     * @return - The level of the node
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the parent of this node
     * @param node - The parent node
     */
    abstract void setParent(Node<V> node);

    /**
     * Gets the left child of this node.
     * Implementation of this might return null!!
     * @return - The left child or null
     */
    abstract AbstractNode<V> getLeftChild();

    /**
     * Gets the right child of this node.
     * Implementation of this might return null!!
     * @return - The right child or null
     */
    abstract AbstractNode<V> getRightChild();
}
