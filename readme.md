# Merkle Tree 


## Introduction

This repository contains a Java implementation of a Merkle tree, designed to facilitate conflict identification across network bounds, especially when nodes go down. 
The Merkle tree is a data structure widely used in distributed systems and blockchain technologies to efficiently verify the integrity of large datasets. 
I created this project because I couldn't find a suitable straightforward implementation that met my specific requirements.

## Features

- **Customizable Hashing**: This Merkle tree implementation allows users to choose any type of hash for any type of data. Both the hash function and the object are template parameters and the hashing is flexible enough so that it's possible to provide a hashing implementation for the hashing of D1+D2 where D1...n are data nodes.

- **Multi level comparison**: This Merkle tree diffs trees of different levels. In practise this means one tree will have a data node count higher than the other, so nodes that don't exist on lower level tree are returned. 
  

 **NOTE**: the tree that calls diffs is used as pivot, so calling  tree.diff(anotherTree) will produce different results than calling anotherTree.diff(tree) 
    - if levels don't match. In practise this tree is meant to tell if something changed but not what changed. It's primary purpose is to identify if any change occurred(inserts or updates) 
    - and do so fast without comparing data blocks.
 

Take the following trees as an example, with the caveat that Level2.hash(A) == Level3.hash(A)

<table>
<tr>
<th>Level 3 Merkle Tree</th>
<th>Level 2 Merkle Tree</th>
</tr>
<tr>
<td>
<pre>

                Root Hash
                 /    \
             H12       H34
             / \       / 
         H1       H2  H3  
         / \      / \  / 
      A      B  C   D  E 
</pre>
</td>
<td>
<pre>

            Root Hash
             /  \       
          H1     H2   
         / \    / \  
        A   B  Z   D 
</pre>
</td>
</tr>
</table>

If the diff call is from the level 3 tree against the level 2, then the diff set will be C,D,E; If the diff call is the reverse
then, the result set will be A,B,Z,D due to level 3 being longer, meaning during diff H1 will compare against H1, 
which will cause all data under the branch to be marked as a diff. 

See unit test:
```
 pt.shorty.merkleTree.MerkleTreeTest#testStringMultiLevelTree
```


## Getting Started

Follow these steps to get started with using the Merkle tree in your projects:

1. **Add the following dependency to your gradle.build**:

```
<dependency>
  <groupId>pt.shorty</groupId>
  <artifactId>merkletree</artifactId>
  <version>1.0.1-SNAPSHOT</version>
</dependency>
```


## Example Usage

```java
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {


  private static final class HashableEntity<E> implements Hashable<String> {
    private E entityInstance;
    private String hash; //cache hash

    public HashableEntity(E entityInstance) {
      this.entityInstance = entityInstance;
    }

    @Override
    public String hash() {
      if (hash != null){
        return hash;
      }

      MessageDigest md5 = null;
      try {
        md5 = MessageDigest.getInstance("md5");
      } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
      }
      try {
        byte[] byteArray = toByteArray();
        byte[] digest = md5.digest(byteArray);
        hash =  Base64.getEncoder().encodeToString(digest);
        return hash;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }


    private byte[] toByteArray() throws IOException {
      ByteArrayOutputStream baos = null;
      ObjectOutputStream oos = null;
      try {
        baos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(baos);
        oos.writeObject(entityInstance);
        return baos.toByteArray();
      } finally {
        oos.close();
        baos.close();
      }
    }
  }

    public static void main(String[] args) {
        // Create a list of HashableImpl objects
        List<Hashable> elements = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            elements.add(new HashableEntity<Integer>(i));
        }

        // Sort the elements (required for Merkle tree construction)
        Collections.sort(elements);

        // Create the Merkle tree using a custom hash function
        MerkleTree<ByteArray, HashableImpl> refTree = new MerkleTree<>(elements, hashes -> {
            //used to hash the parent, hashes contain the hash of the left and right child
            // here we just create a md5(hash(a),hash(b)9
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
    

}
```


## License
This project is licensed under the [MIT License](https://opensource.org/license/mit/). Feel free to use, modify, and distribute the code as per the terms of the license.

