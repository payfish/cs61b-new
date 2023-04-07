package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Stage implements Serializable {
    private final Map<String, String> map = new TreeMap<>();

    public void put(String filename, String id) {
        map.put(filename, id);
    }

    public void remove(String filename) {
        map.remove(filename);
    }

    public boolean containsKey(String filename) {
        return map.containsKey(filename);
    }

    public String get(String filename) {
        return map.get(filename);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Iterator<String> iterator() {
        return map.keySet().iterator();
    }
}
