package gitlet;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Tree is a map, contains a tree or blobs
 */
public class Tree implements Serializable {
    private final Map<String, String> tree;

    public Tree() {
        tree = new TreeMap<>();
    }

    public void put(String fileName, String id) {
        tree.put(fileName, id);
    }

    public Iterator<String> iterator() {
        return tree.keySet().iterator();
    }

    public String get(String fileName) {
        return tree.get(fileName);
    }

    public boolean containsKey(String fileName) {
        return tree.containsKey(fileName);
    }

    public void remove(String fileName) {
        tree.remove(fileName);
    }

    public boolean isEmpty() {
        return tree.isEmpty();
    }

    public Set<String> keySet() {
        return tree.keySet();
    }
}
