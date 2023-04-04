package gitlet;

import java.util.Map;
import java.util.TreeMap;

import static gitlet.Repository.GITLET_HEAD;
import static gitlet.Repository.GITLET_OBJECTS_DIR;


public class FileTests {
    public static void main(String[] args) {
        String s = Utils.readContentsAsString(GITLET_HEAD);
        System.out.println(s);
        System.out.println(s.substring(s.lastIndexOf("\\")+1));
    }
}
