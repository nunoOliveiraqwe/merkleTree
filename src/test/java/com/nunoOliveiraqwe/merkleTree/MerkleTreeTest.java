package com.nunoOliveiraqwe.merkleTree;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MerkleTreeTest {

    private MerkleTree<ByteArray, HashableImpl> buildTreeForNElements(int n) {
        //if we create a tree out of 2 elements, then the depth of the tree is 2
        List<HashableImpl> elements = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            elements.add(new HashableImpl(i));
        }
        Collections.sort(elements);
        return new MerkleTree<>(elements, hashes -> {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            MessageDigest finalMd = md5;
            hashes.forEach(h -> finalMd.update(h.array));
            return new ByteArray(md5.digest());
        });
    }

    @Test
    public void testCorrectLevelFor2Records() {
        MerkleTree<ByteArray, HashableImpl> tree = buildTreeForNElements(2);
        Node<ByteArray> root = tree.getRoot();
        assertEquals(2, root.getLevel());
    }

    @Test
    public void testCorrectLevelFor4Records() {
        MerkleTree<ByteArray, HashableImpl> tree = buildTreeForNElements(4);
        Node<ByteArray> root = tree.getRoot();
        assertEquals(2, root.getLevel());
    }

    @Test
    public void testCorrectLevelFor5Records() {
        MerkleTree<ByteArray, HashableImpl> tree = buildTreeForNElements(5);
        Node<ByteArray> root = tree.getRoot();
        assertEquals(3, root.getLevel());
    }

    @Test
    public void testCorrectLevelFor64Records() {
        MerkleTree<ByteArray, HashableImpl> tree = buildTreeForNElements(64);
        Node<ByteArray> root = tree.getRoot();
        assertEquals(6, root.getLevel());
    }

    @Test
    public void testGetLeafNodes() {
        int numberOfLeafs = 8;
        MerkleTree<ByteArray, HashableImpl> tree = buildTreeForNElements(numberOfLeafs);
        Queue<AbstractNode<ByteArray>> leafNodes = tree.getLeafNodes();
        assertEquals(numberOfLeafs, leafNodes.size());
        for (int i = 0; i < numberOfLeafs; i++) {
            LeafNode<ByteArray, HashableImpl> node = (LeafNode<ByteArray, HashableImpl>) leafNodes.poll();
            assertEquals(i, node.getData().number);
        }
    }

    @Test
    public void testDiffEqualTrees() {
        MerkleTree<ByteArray, HashableImpl> tree = buildTreeForNElements(4);
        MerkleTree<ByteArray, HashableImpl> otherTree = buildTreeForNElements(4);
        Queue<LeafNode<ByteArray, HashableImpl>> diff = tree.diff(otherTree);
        assertEquals(0,diff.size());
    }

    @Test
    public void testDiffEqualTrees64Leafs() {
        MerkleTree<ByteArray, HashableImpl> tree = buildTreeForNElements(64);
        MerkleTree<ByteArray, HashableImpl> otherTree = buildTreeForNElements(64);
        Queue<LeafNode<ByteArray, HashableImpl>> diff = tree.diff(otherTree);
        assertEquals(0,diff.size());
    }

    @Test
    public void testDiffNotEqualTreesHalfNullLeafs() {
        MerkleTree<ByteArray, HashableImpl> tree = buildTreeForNElements(63);
        MerkleTree<ByteArray, HashableImpl> otherTree = buildTreeForNElements(33);
        Queue<LeafNode<ByteArray, HashableImpl>> diff = tree.diff(otherTree);
        assertEquals(30,diff.size());
    }

    @Test
    public void testDiffNotEqualWithDifferentDepths() {
        MerkleTree<ByteArray, HashableImpl> tree = buildTreeForNElements(64);
        MerkleTree<ByteArray, HashableImpl> otherTree = buildTreeForNElements(32); //-1 depth than tree
        Queue<LeafNode<ByteArray, HashableImpl>> diff = tree.diff(otherTree);
        assertEquals(32,diff.size());
    }


    @Test
    public void test1NodeHashDiffTree(){
        List<HashableImpl> elements = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            elements.add(new HashableImpl(i));
        }
        Collections.sort(elements);
        MerkleTree<ByteArray, HashableImpl> refTree = new MerkleTree<>(elements, hashes -> {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            MessageDigest finalMd = md5;
            hashes.forEach(h -> finalMd.update(h.array));
            return new ByteArray(md5.digest());
        });

        HashableImpl expectedDiffElement = elements.get(1);


        elements.clear();
        elements.add(new HashableImpl(0));
        elements.add(new HashableImpl(2));

        Collections.sort(elements);

        MerkleTree<ByteArray, HashableImpl> otherTree = new MerkleTree<>(elements, hashes -> {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            MessageDigest finalMd = md5;
            hashes.forEach(h -> finalMd.update(h.array));
            return new ByteArray(md5.digest());
        });

        Queue<LeafNode<ByteArray, HashableImpl>> diff = refTree.diff(otherTree);
        assertEquals(1,diff.size());
        assertEquals(expectedDiffElement, Objects.requireNonNull(diff.poll()).getData());
    }


    @Test
    public void test2NodeHashDiffTree(){
        List<HashableImpl> elements = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            elements.add(new HashableImpl(i));
        }
        Collections.sort(elements);
        MerkleTree<ByteArray, HashableImpl> refTree = new MerkleTree<>(elements, hashes -> {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            MessageDigest finalMd = md5;
            hashes.forEach(h -> finalMd.update(h.array));
            return new ByteArray(md5.digest());
        });

        elements.clear();
        elements.add(new HashableImpl(0));
        elements.add(new HashableImpl(2));

        Collections.sort(elements);

        MerkleTree<ByteArray, HashableImpl> otherTree = new MerkleTree<>(elements, hashes -> {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            MessageDigest finalMd = md5;
            hashes.forEach(h -> finalMd.update(h.array));
            return new ByteArray(md5.digest());
        });

        Queue<LeafNode<ByteArray, HashableImpl>> diff = refTree.diff(otherTree);
        assertEquals(3,diff.size());
        //first element expected is 1, then 2, 3, 0 is not included since it's the same in both trees
        assertEquals(1, Objects.requireNonNull(diff.poll()).getData().number);
        assertEquals(2, Objects.requireNonNull(diff.poll()).getData().number);
        assertEquals(3, Objects.requireNonNull(diff.poll()).getData().number);
    }


    @Test
    public void testNodeHashDiffOtherTreeHasRef(){
        List<HashableImpl> elements = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            elements.add(new HashableImpl(i));
        }
        Collections.sort(elements);
        MerkleTree<ByteArray, HashableImpl> refTree = new MerkleTree<>(elements, hashes -> {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            MessageDigest finalMd = md5;
            hashes.forEach(h -> finalMd.update(h.array));
            return new ByteArray(md5.digest());
        });



        elements.clear();
        elements.add(new HashableImpl(0));
        HashableImpl expectedDiffElement = new HashableImpl(2);
        elements.add(expectedDiffElement);

        Collections.sort(elements);

        MerkleTree<ByteArray, HashableImpl> otherTree = new MerkleTree<>(elements, hashes -> {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            MessageDigest finalMd = md5;
            hashes.forEach(h -> finalMd.update(h.array));
            return new ByteArray(md5.digest());
        });

        Queue<LeafNode<ByteArray, HashableImpl>> diff = otherTree.diff(refTree);
        assertEquals(1,diff.size());
        //first element expected is 1, then 2, 3, 0 is not included since it's the same in both trees
        assertEquals(expectedDiffElement, Objects.requireNonNull(diff.poll()).getData());
    }



    @Test
    public void testCorrectHashes() throws NoSuchAlgorithmException {
        MerkleTree<ByteArray, HashableImpl> tree = buildTreeForNElements(2);
        MessageDigest leafMD5 = null;
        List<ByteArray> leafHashes = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            leafMD5 = MessageDigest.getInstance("md5");
            leafHashes.add(new ByteArray(leafMD5.digest(BigInteger.valueOf(i).toByteArray())));
        }

        Queue<AbstractNode<ByteArray>> leafNodes = tree.getLeafNodes();
        final int size = leafNodes.size();

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < size; i++) {
            LeafNode<ByteArray, HashableImpl> node = (LeafNode<ByteArray, HashableImpl>) leafNodes.poll();
            assertArrayEquals(leafHashes.get(i).array, node.hash().array);
            md5.update(leafHashes.get(i).array);

        }

        Node<ByteArray> root = tree.getRoot();
        AbstractNode<ByteArray> leftChild = root.getLeftChild();

        assertArrayEquals(md5.digest(), leftChild.hash().array);
    }


    private static class ByteArray implements Comparable<ByteArray> {

        private final byte[] array;

        public ByteArray(byte[] array) {
            this.array = array;
        }

        @Override
        public int compareTo(ByteArray o) {
            return Arrays.compare(array, o.array);
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ByteArray byteArray = (ByteArray) o;
            return Arrays.equals(array, byteArray.array);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }
    }

    private static class HashableImpl implements Hashable<ByteArray>, Comparable<HashableImpl> {
        private Integer number;

        public HashableImpl(int number) {
            this.number = number;
        }

        @Override
        public ByteArray hash() {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("md5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            byte[] digest = md5.digest(BigInteger.valueOf(number).toByteArray());
            return new ByteArray(digest);
        }

        @Override
        public int compareTo(HashableImpl o) {
            return this.number.compareTo(o.number);
        }
    }

    private static class Printer implements com.nunoOliveiraqwe.merkleTree.Printer<ByteArray> {

        @Override
        public void print(ByteArray hash) {
            System.out.print(Arrays.hashCode(hash.array) + " ");
        }

        @Override
        public void newLine() {
            System.out.println();
        }
    }

}