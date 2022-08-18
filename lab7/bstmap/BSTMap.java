package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V> {

    int size = 0;

    private class BSTNode {

        private K key;
        private V value;
        private BSTNode left;
        private BSTNode right;

        BSTNode(K key, V value, BSTNode left, BSTNode right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
        }

        BSTNode get(BSTNode bst, K k) {
            if (bst == null) {
                return null;
            }
            if (k.equals(bst.key)) {
                return bst;
            } else if (k.compareTo(key) > 0) {
                return get(bst.right, k);
            } else {
                return get(bst.left, k);
            }
        }

        BSTNode insert(BSTNode bst, K k, V v) {
            if (bst == null) {
                size += 1;
                return new BSTNode(k, v, null, null);
            }
            if (k.compareTo(key) > 0) {
                bst.right = insert(bst.right, k, v);
            } else if (k.compareTo(key) < 0) {
                bst.left = insert(bst.left, k, v);
            } else {
                bst.value = v;
            }
            return bst;
        }

    }

    private BSTNode root;

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }
        return root.get(root, key) != null;
    }

    @Override
    public V get(K key) {
        if (root == null) {
            return null;
        }
        BSTNode bst = root.get(root, key);
        if (bst == null) {
            return null;
        }
        return bst.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            size += 1;
            root = new BSTNode(key, value, null, null);
        }
        root.insert(root, key, value);
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * prints out BSTMap in order of increasing key.
     */
    public void printInOrder() {
        if (root == null) {
            System.out.println("There's nothing in the BSTMap!");
        }
        preOrder(root);
    }

    /**
     * Helper method for printInOrder.
     * @return BSTNode
     */
    public void preOrder(BSTNode bst) {
        if (bst == null) {
            return;
        }
        preOrder(bst.left);
        System.out.println(bst.key + "-->" + bst.value);
        preOrder(bst.right);
    }


}
