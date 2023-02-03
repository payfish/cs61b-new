package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Fred
 */
public class MyHashMap<K, V> implements Map61B<K, V> {


    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (int i = 0; i < NOB; i += 1) {
            if (buckets[i] != null) {
                Collection<Node> collection = buckets[i];
                Iterator<Node> iterator = collection.iterator();
                while (iterator.hasNext()) {
                    Node node = iterator.next();
                    set.add(node.getKey());
                }
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        int index = getIndex(key);
        if (buckets[index] == null) {
            return null;
        } else {
            Collection collection = buckets[index];
            Iterator<Node> iterator = collection.iterator();
            while (iterator.hasNext()) {
                Node node = iterator.next();
                if (node.getKey().equals(key)) {
                    collection.remove(node);
                    return node.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int index = getIndex(key);
        if (buckets[index] == null) {
            return null;
        } else {
            Collection collection = buckets[index];
            Iterator<Node> iterator = collection.iterator();
            while (iterator.hasNext()) {
                Node node = iterator.next();
                if (node.getKey().equals(key) && node.getValue().equals(value)) {
                    collection.remove(node);
                    return value;
                }
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new MHMiterator();
    }
    @Override
    public void clear() {
        size = 0;
        buckets = createTable(NOB);
    }

    @Override
    public boolean containsKey(K key) {
        return _get(key) != null;
    }

    @Override
    public V get(K key) {
        Node node = _get(key);
        if (node != null) {
            return node.getValue();
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
         if (containsKey(key)) {
            replace(key, value);
        } else if (ifMax(size + 1, LOADFACTOR)) {
            resize(RESIZEFACTOR);
            Node node = createNode(key, value);
            _put(node, buckets);
            size += 1;
        } else {
            Node node = createNode(key, value);
            _put(node, buckets);
            size += 1;
        }
    }

    private class MHMiterator implements Iterator<K> {

        Set<K> keyset = keySet();
        Iterator<K> iterator = keyset.iterator();

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }


        @Override
        public K next() {
            return iterator.next();
        }
    }
    /**
     * Helper method for get().
     * @param key
     * @return
     */
    private Node _get(K key) {
        int index = getIndex(key);
        if (buckets[index] == null) {
            return null;
        } else {
            Collection collection = buckets[index];
            Iterator<Node> iterator = collection.iterator();
            while (iterator.hasNext()) {
                Node node = iterator.next();
                if (node.getKey().equals(key)) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Helper method for put().
     * @param node
     * @param Buckets
     */
    private void _put(Node node, Collection<Node>[] Buckets) {
        int index = getIndex(node.getKey());
        if (Buckets[index] == null) {
            Buckets[index] = createBucket();
        }
        Collection<Node> collection = Buckets[index];
        collection.add(node);
    }

    /**
     * Resize the hashmap by the resize factor when overloaded.
     * @param resizeFactor
     */
    private void resize(int resizeFactor) {
        Collection<Node>[] newBuckets = createTable((NOB * resizeFactor));
        for (int i = 0; i < NOB; i += 1) {
            if (buckets[i] != null) {
                Collection<Node> collection = buckets[i];
                Iterator<Node> iterator = collection.iterator();
                while (iterator.hasNext()) {
                    Node node = iterator.next();
                    _put(node, newBuckets);
                }
            }
        }
        buckets = newBuckets;
        NOB *= resizeFactor;
    }


    private boolean ifMax(int n, Double loadfactor) {
        return (double) n / NOB > loadfactor;
    }

    /**
     * Replace the same key's value when putting an entry.
     * @param key
     * @param value
     */
    private void replace(K key, V value) {
        int index = getIndex(key);
        Collection collection = buckets[index];
        Iterator<Node> iterator = collection.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node.getKey().equals(key)) {
                node.setValue(value);
                break;
            }
        }
    }

    /**
     * Return a key's hashcode.
     * @param key
     * @return
     */
    private int getIndex(K key) {
        int hashcode = key.hashCode();
        int index = hashcode % NOB;
        if (index < 0) {
            index = Math.floorMod(index, NOB);
        }
        return index;
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }

        private K getKey() {
            return key;
        }

        private V getValue() {
            return value;
        }

        private void setValue(V value) {
            this.value = value;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;

    private int size;

    private static int NOB = 16;

    private static double LOADFACTOR = 0.75;

    private static int RESIZEFACTOR = 2;

    private HashSet<K> hashSet;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(NOB);
    }

    public MyHashMap(int initialSize) {
        NOB = initialSize;
        buckets = createTable(NOB);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        NOB = initialSize;
        buckets = createTable(NOB);
        LOADFACTOR = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

}
