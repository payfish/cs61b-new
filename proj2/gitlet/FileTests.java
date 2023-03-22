package gitlet;

import org.junit.Test;

import java.io.File;
import java.util.Date;

import static gitlet.Utils.join;

public class FileTests {
    @Test
    public static void main(String[] args) {
        File CWD = new File(System.getProperty("user.dir"));
        Commit commit = new Commit(new Date(0), "", "fred", null, "as213vv");
        Repository repository = new Repository();
        repository.output(CWD, commit);
        System.out.println(CWD.toString());
    }
}
