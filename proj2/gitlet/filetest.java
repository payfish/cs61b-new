package gitlet;

import gitlet.Utils.*;

import java.io.File;

import static gitlet.Repository.CWD;
import static gitlet.Utils.*;

public class filetest {
    public static void main(String[] args) {
        File file = join(CWD, "1.txt");
        writeContents(file, "hahahah" + "\n");
        writeContents(file, readContentsAsString(file), "11111");
    }
}
