package gitlet;

import gitlet.Utils.*;

import java.io.File;

import static gitlet.Repository.CWD;
import static gitlet.Repository.GITLET_HEAD;
import static gitlet.Utils.*;

public class filetest {
    public static void main(String[] args) {
        File file = join(CWD, "1.txt");
        String currBranch = readContentsAsString(GITLET_HEAD);
        System.out.println(currBranch.substring(currBranch.lastIndexOf(System.getProperty("file.separator")) + 1));
    }
}
