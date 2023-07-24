package com.nunoOliveiraqwe.merkleTree;

import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic node in the merkel tree. This node holds both a left and right child and can
 * be at any level. With the exception of the leaf node, all other nodes in a merkel tree are of
 * type {@link Node}
 * @param <V> - The hash type
 */
public class Node<V extends Comparable<V>> extends AbstractNode<V> {

    private Node<V> parent;
    private AbstractNode<V> rightChild;
    private AbstractNode<V> leftChild;

    private HashBuilder<V> hashBuilder;

    /**
     * Creates a new node at the specified level with the supplied hashbuilder
     * @param level - The level that this node is at
     * @param hashBuilder - The builder of hashes
     */
    public Node(int level,HashBuilder<V> hashBuilder) {
        super(level);
        this.setHashBuilder(hashBuilder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    V hash() {
       List<V> hashList = new ArrayList<>();
       if (leftChild != null){
            hashList.add(leftChild.hash());
       }
       if (rightChild != null){
            hashList.add(rightChild.hash());
        }
        return hashBuilder.hash(hashList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean isLeaf() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRight() {
        if (parent == null){
            //we are groot
            return false;
        }
        return parent.isRightNode(this);
    }

    /**
     * Checks if this node is the root node
     * @return - True if this node is the root node
     */
    public boolean isRootNode(){
        return this.parent == null;
    }


    /**
     * Checks if a given node is the right node of this node
     * @param node - The node to check
     * @return - True if it's the right node, false otherwise
     */
    protected boolean isRightNode(AbstractNode<V> node){
        return this.rightChild.equals(node);
    }

    /**
     * Sets the parent of this node
     * @param parent - The parent node
     */
    public void setParent(Node<V> parent) {
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    AbstractNode<V> getLeftChild() {
        return leftChild;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    AbstractNode<V> getRightChild() {
        return rightChild;
    }

    /**
     *Sets the right child of this node
     * @param rightChild - the right child to set
     */
    public void setRightChild(AbstractNode<V> rightChild) {
        this.rightChild = rightChild;
        this.rightChild.setParent(this);
    }

    /**
     *Sets the left child of this node
     * @param leftChild - the left child to set
     */
    public void setLeftChild(AbstractNode<V> leftChild) {
        this.leftChild = leftChild;
        this.leftChild.setParent(this);
    }

    /**
     * Sets the hashbuilder for this node
     * @param hashBuilder - The hash builder to set
     */
    public void setHashBuilder(HashBuilder<V> hashBuilder){
        this.hashBuilder = hashBuilder;
    }
}
