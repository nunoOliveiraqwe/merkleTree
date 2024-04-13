package pt.shorty.merkleTree;

import java.util.*;

/**
 * A DS that represents a merkle tree. Merkle trees are specially usefully for conflict detection and for
 * detecting missing info.
 * This Merkel Tree does not provide leak safeness, if a node is requested, the actual is returned,
 * not a copy of the node.
 * @param <T> - The type of data to use with the instance of the tree
 */
public class MerkleTree<V extends Comparable<V>, T extends Hashable<V>> {
    private Node<V> root = null;

    /**
     * Constructs this tree out of a list of sorted data elements
     *
     * @param dataElements - The data that will constitute the leafs of this tree.
     *                     Data is expected to be sorted
     * @param hashBuilder - A builder of hashes that will be used by other nodes other than the leafs,
     *                    this builder will create hashes out of the hashes of the leafs,
     *                    and out of the hashes of intermediate node, while it takes in a list, this
     *                    Merkel Tree implementation is expected to call with hashing Pairs
     */
    public MerkleTree(List<T> dataElements, HashBuilder<V> hashBuilder) {
        initTree(dataElements, hashBuilder);
    }

    /**
     * Initializes the leaf nodes out of the data elements
     * @param dataElements - The data elements that will constitute the leafs of this tree.
     * @param hashBuilder - A builder of hashes that will be used by other nodes other than the leafs,
     *                    this builder will create hashes out of the hashes of the leafs,
     *                    and out of the hashes of intermediate node, while it takes in a list, this
     *                    Merkel Tree implementation is expected to call with hashing Pairs
     *
     */
    private void initTree(List<T> dataElements, HashBuilder<V> hashBuilder) {
        Queue<AbstractNode<V>> leafs = new ArrayDeque<>();
        for (T data : dataElements) {
            LeafNode<V, T> leafNode = new LeafNode<>(data);
            leafs.add(leafNode);
        }
        condenseBranches(1, leafs, hashBuilder);
    }

    /**
     * Constructs the branches of this tree given the initial leaf nodes
     * @param level - The current level of the nodes
     * @param nodes - The queue of nodes to add
     * @param hashBuilder - The hash builder
     */
    private void condenseBranches(int level, Queue<AbstractNode<V>> nodes, HashBuilder<V> hashBuilder) {
        if (nodes.size() == 0) {
            return;
        }
        Queue<AbstractNode<V>> upperNodes = new ArrayDeque<>();
        while (nodes.size() > 0) {
            Node<V> localRootNode = new Node<V>(level, hashBuilder);
            AbstractNode<V> leftNode = nodes.poll();
            localRootNode.setLeftChild(leftNode);
            if (nodes.size() > 0) {
                AbstractNode<V> rightNode = nodes.poll();
                localRootNode.setRightChild(rightNode);
            }
            upperNodes.add(localRootNode);
        }
        if (upperNodes.size() == 1 || upperNodes.size() == 2) {
            root = new Node<V>(level + 1, hashBuilder);
            root.setLeftChild(upperNodes.poll());
            if (upperNodes.size() == 1) {
                root.setRightChild(upperNodes.poll());
            }
            return;
        }
        condenseBranches(++level, upperNodes, hashBuilder);
    }

    /**
     * Gets the root node of this tree. It's possible this is null.
     * Additionally, the actual node is returned not a copy of the node
     * @return - The root node
     */
    public Node<V> getRoot() {
        return root;
    }

    /**
     * Gets the current depth of this tree.
     * If no node is preset in this tree, then -1 is returned
     * @return The current depth or -1 if not root is found
     */
    public int getTreeDepth(){
        if (root != null){
            return root.getLevel();
        }
        return -1;
    }

    /**
     * Prints the tree into the printer
     * @param printer - The printer interface
     */
    public void printTreeToPrinter(Printer<V> printer) {
        if (root == null) {
            return;
        }
        //using bfs level order transversal
        Queue<AbstractNode<V>> nodeQueue = new ArrayDeque<>();
        nodeQueue.add(root);
        int currentLevel = root.getLevel();
        while(!nodeQueue.isEmpty()){
            AbstractNode<V> currentNode = nodeQueue.poll();
            if(currentNode.getLeftChild() != null){
                nodeQueue.add(currentNode.getLeftChild());
            }
            if(currentNode.getRightChild() != null){
                nodeQueue.add(currentNode.getRightChild());
            }
            if(currentLevel != currentNode.getLevel()){
                printer.newLine();
                currentLevel = currentNode.getLevel();
            }
            printer.print(currentNode.hash());
        }
    }

    /**
     * Gets all the leaf nodes of this tree, ordered from left to right
     * @return A set of leaf nodes
     */
    public Queue<AbstractNode<V>> getLeafNodes(){
        return getNodesAtNLevel(0);
    }


    /**
     * Gets all nodes at the nth level, ordered from left to right
     * @param level - The level of the nodes
     * @return A set of nodes
     */
    public Queue<AbstractNode<V>> getNodesAtNLevel(int level){
        if(root == null){
            return new ArrayDeque<>();
        }
        if (level > root.getLevel() || level<0){
            throw new IllegalArgumentException("Invalid level supplied. Level cannot be greater that current max level or le 0");
        }
        Queue<AbstractNode<V>> nodeSet = new ArrayDeque<>();
        transverse(nodeSet,root,level);
        return nodeSet;
    }

    /**
     * Diffs this tree with the supplied one. Diffs are made using this
     * tree as the reference, in practise this means that nodes that are different
     * (same level, index, but different hashes) will be counted as a diff and nodes that exist
     * in this instance and not of other (this.node != null && other.node == null).
     * This implementation supports comparing trees of different depths,
     * returning only the different leaf nodes between them, even if they sit
     * at different depth (requires good hash collision avoidance on V)
     * @param otherTree - The tree to compare with
     * @return - A queue of differences
     */
    public Queue<LeafNode<V,T>> diff(MerkleTree<V, T> otherTree) {
        Node<V> otherRoot = otherTree.getRoot();
        if (this.root.hash().compareTo(otherRoot.hash()) == 0){
            //no difference
            return new ArrayDeque<>();
        }
        //Keeps track of visited leaf nodes, both for this instance and for
        //the other tree
        Map<V,LeafNode<V,T>> diffMap = new HashMap<>();
        Queue<LeafNode<V,T>> returnQueue = new ArrayDeque<>();
        diffNodes(diffMap,returnQueue,this.root,otherRoot);
        return returnQueue;
    }


    private void diffNodes(Map<V,LeafNode<V,T>> diffMap,Queue<LeafNode<V,T>> returnQueue,AbstractNode<V> localNode, AbstractNode<V> otherNode){
        if(localNode instanceof Node
                && otherNode instanceof LeafNode
                && !diffMap.containsKey(otherNode.hash())){
            diffMap.put(otherNode.hash(), (LeafNode<V, T>) otherNode);
        } else if(localNode instanceof Node
                && otherNode instanceof LeafNode
                && diffMap.containsKey(otherNode.hash())){
            //we added this node from this instance, however, it seems
            //that the other tree contains a node with the same hash,
            //so we remove it since it's not a diff
            returnQueue.remove(otherNode);
        }

        if(localNode == null){
            //even if the otherNode is not null we still return
            //only this instance is used to calculate diffs
            return;
        }
        if(otherNode == null){
            //all leafs starting from local node are diff
            addAllLeafNodeFromStartingFromNode(diffMap,returnQueue,localNode);
            return;
        }

        if (localNode.hash().compareTo(otherNode.hash()) != 0){
            if (localNode instanceof LeafNode<?,?>
                    && !diffMap.containsKey(localNode.hash())){
                diffMap.put(localNode.hash(),(LeafNode<V, T>) localNode);
                returnQueue.add((LeafNode<V, T>) localNode);
                return;
            }
            diffNodes(diffMap, returnQueue,localNode.getLeftChild(), otherNode.getLeftChild());
            diffNodes(diffMap, returnQueue,localNode.getRightChild(), otherNode.getRightChild());
        }
    }

    private void addAllLeafNodeFromStartingFromNode(Map<V,LeafNode<V,T>> diffMap,
                                                    Queue<LeafNode<V,T>> returnQueue,
                                                    AbstractNode<V> node){
        if(node == null){
            return;
        }
        if (node instanceof LeafNode<?,?>) {
            if (!diffMap.containsKey(node.hash())){
                diffMap.put(node.hash(), (LeafNode<V, T>) node);
                returnQueue.add((LeafNode<V, T>) node);
            } else {
                returnQueue.remove(node);
            }
            return;
        }
        addAllLeafNodeFromStartingFromNode(diffMap,returnQueue,node.getLeftChild());
        addAllLeafNodeFromStartingFromNode(diffMap,returnQueue,node.getRightChild());
    }



    private void transverse(Queue<AbstractNode<V>> resultSet,AbstractNode<V> currentNode, int searchLevel){
        if(currentNode == null){
            return;
        }
        if (currentNode.getLevel() == searchLevel){
            resultSet.add(currentNode);
            return;
        }
        transverse(resultSet,currentNode.getLeftChild(),searchLevel);
        transverse(resultSet,currentNode.getRightChild(),searchLevel);
    }



}
