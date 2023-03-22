package gitlet;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Tree
 */
public class Tree<T> implements Serializable {
    private Map<String, T> tree;

    public Tree() {
        tree = new TreeMap<>();
    }

    public void put(String id, T t) {
        tree.put(id, t);
    }
}
