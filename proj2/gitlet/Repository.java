package gitlet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author payfish
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File GITLET_OBJECTS_DIR = join(GITLET_DIR, "objects");

    public static final File GITLET_REFS_DIR = join(GITLET_DIR, "refs");

    public static final File GITLET_STAGE = join(GITLET_DIR, "STAGE");

    public static final File GITLET_REFS_heads = join(GITLET_REFS_DIR, "heads");

    public static final File GITLET_HEAD = join(GITLET_DIR, "HEAD");


    /**
     * Helper method for putting an object into a file
     * @param path
     * @param obj commits/blobs/tree
     */
    public String output(File path, Serializable obj) {
        String sha1Id = sha1(obj);
        writeObject(path, obj);
        return sha1Id;
    }

    /**
     * Initialize the repository
     */
    public void init() throws IOException {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system " +
                    "already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            GITLET_OBJECTS_DIR.mkdir();
            GITLET_REFS_DIR.mkdir();
            GITLET_REFS_heads.mkdir();
            Commit commit = initCommit();
            String sha1Id = output(GITLET_OBJECTS_DIR, commit);
            File branchMaster = join(GITLET_REFS_heads, "master");
            writeObject(branchMaster, sha1Id); // crate a new branch named master
            writeObject(GITLET_HEAD, branchMaster.toString());
        }
    }

    /**
     * initial commit
     * @return
     */
    private Commit initCommit() {
        Commit commit = new Commit();
        commit.setTimeStamp(new Date(0));
        commit.setMessage("initial commit");
        return commit;
    }

    public Blob readFileAsBlob(File path) {
        Blob blob = new Blob();
        byte[] bytes = readContents(path);
        blob.setContents(bytes);
        blob.setRefs(path.toString());
        return blob;
    }

    /**
     * Add method -- New things should be added to repo: blob of the new file
     * or the modified file(to objects folder) /  to STAGE file
     * @param filename
     */
    public void add(String filename) throws IOException {
        if (!GITLET_STAGE.exists()) {
            GITLET_STAGE.createNewFile();
        }
        if (filename.equals("*")) {
            addAll();
        } else {
            File path = join(GITLET_DIR, filename);
            Blob blob = readFileAsBlob(path);
            String blobId = sha1(blob);
            //If gitlet can find an id same as blobId in the current commit,do not stage it to the STAGE
//            //First thing is to get the head commit.
//            String gitlet_head = readContentsAsString(GITLET_HEAD);
//            File file = new File(gitlet_head); //branch on which is working right now
//            String sha1 = readContentsAsString(file); //sha1 of the head commit of this branch
            List<String> objList = plainFilenamesIn(GITLET_OBJECTS_DIR.toString());
            for (String id : objList) {
                if (id == blobId) {
                    return;
                }
            }
            //This is a brand-new blob! Stage it to the staging area~
            

        }
    }

    /**
     * Helper method for add().
     */
    private void addAll() {

    }

    /**
     * Commit method
     */
    public void commit(String message) throws IOException {




        //clear the staging area after the commit
        rewirte(GITLET_STAGE, null);
    }

    /**
     * Helper method for rewriting a file
     */
    private void rewirte(File file, Serializable obj) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
        writeObject(file, obj);
    }

    /**
     * Validate the operands' numbers
     * @param cmd
     * @param args
     * @param n
     */
    public void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Incorrect operands.", cmd));
        }
    }
}
