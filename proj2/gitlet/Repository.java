package gitlet;

import afu.org.checkerframework.checker.igj.qual.I;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  @author payfish
 */
public class Repository {

    /* The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /* The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File GITLET_OBJECTS_DIR = join(GITLET_DIR, "objects");

    public static final File GITLET_COMMITS_DIR = join(GITLET_OBJECTS_DIR, "commits");

    public static final File GITLET_STAGE_ADDITION = join(GITLET_DIR, "ADD_STAGE");

    public static final File GITLET_STAGE_REMOVAL = join(GITLET_DIR, "REMOVE_STAGE");

    public static final File GITLET_REFS_DIR = join(GITLET_DIR, "refs");

    public static final File GITLET_REFS_heads = join(GITLET_REFS_DIR, "heads");

    public static final File GITLET_HEAD = join(GITLET_DIR, "HEAD");




    public void init() throws IOException {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system " +
                    "already exists in the current directory.");
        } else {
            GITLET_COMMITS_DIR.mkdirs();
            GITLET_REFS_heads.mkdirs();
            Commit commit = new Commit(dateToTimeStamp(new Date(0)),
                    "initial commit", "payfish", null);
            String sha1Id = output(GITLET_COMMITS_DIR, commit);
            File branchMaster = join(GITLET_REFS_heads, "master");
            writeContents(branchMaster, sha1Id); // crate a new branch named master
            writeContents(GITLET_HEAD, branchMaster.toString());
            GITLET_STAGE_ADDITION.createNewFile();
            GITLET_STAGE_REMOVAL.createNewFile();
            writeObject(GITLET_STAGE_ADDITION, new Stage());
            writeObject(GITLET_STAGE_REMOVAL, new Stage());
        }
    }

    public void add(String fileName) throws IOException {
        if (fileName.equals("*")) {
            addAll();
        } else {
            Blob blob = makeBlob(fileName);
            String blobId = output(GITLET_OBJECTS_DIR, blob);
            /* If gitlet can find an id same as blobId in the current commit, do not stage it to the STAGE */
            Tree tree = getCommitTree(getHeadCommitId());
            Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
            if (tree.containsKey(fileName) && tree.get(fileName).equals(blobId)) {
                if (add_stage.containsKey(fileName) && add_stage.get(fileName).equals(blobId)) {
                    add_stage.remove(fileName);
                }
                message("No need to add the file.");
                return;
            }
            add_stage.put(fileName, blobId);
            rewriteObj(GITLET_STAGE_ADDITION, add_stage);
        }
    }


    /**
     * Helper method for add().
     */
    private void addAll() {
    }


    public void commit(String message) throws IOException {
        Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
        Stage rm_stage = readObject(GITLET_STAGE_REMOVAL, Stage.class);
        if (add_stage.isEmpty() && rm_stage.isEmpty()) {
            message("No changes added to the commit!");
            return;
        }
        if (message == null || message.isBlank()) {
            message("Please enter a commit message.");
            return;
        }
        Commit commit = new Commit(dateToTimeStamp(new Date()), message, "PayFish", null);
        Tree tree = new Tree();
        /* Add HEAD commits' tree to the new tree */
        String headCommitId = getHeadCommitId();
        Tree headTree = getCommitTree(headCommitId);
        Iterator<String> T_iterator = headTree.iterator();
        while (T_iterator.hasNext()) {
            String nextKey = T_iterator.next();
            tree.put(nextKey, headTree.get(nextKey));
        }
        /* Add the blobs staged for addition or removal to new tree */
        Iterator<String> iterator_add = add_stage.iterator();
        Iterator<String> iterator_rm = rm_stage.iterator();
        while (iterator_add.hasNext()) {
            String nextKey = iterator_add.next();
            tree.put(nextKey, add_stage.get(nextKey));
        }
        while (iterator_rm.hasNext()) {
            String nextKey = iterator_rm.next();
            tree.remove(nextKey);
        }
        /* Save the tree and commit to the repo */
        String treeId = output(GITLET_OBJECTS_DIR, tree);
        commit.setTreeId(treeId);
        commit.setParentID(headCommitId);
        String commitId = output(GITLET_COMMITS_DIR, commit);
        setHeadCommitId(commitId);
        //clear the staging area after the commit
        rewriteObj(GITLET_STAGE_ADDITION, new Stage());
        rewriteObj(GITLET_STAGE_REMOVAL, new Stage());
    }

    public void rm(String filename) throws IOException {
        Tree headTree = getCommitTree(getHeadCommitId());
        Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
        if (!headTree.containsKey(filename) && !add_stage.containsKey(filename)) {
            message("No reason to remove the file.");
            return;
        }
        /* Unstage the file if it is currently staged for addition */
        if (add_stage.containsKey(filename)) {
            add_stage.remove(filename);
        }
        /* Stage the file tracked in the current commit
          for removal and remove it from the working dir */
        if (headTree.containsKey(filename)) {
            Stage rm_stage = new Stage();
            rm_stage.put(filename, headTree.get(filename));
            rewriteObj(GITLET_STAGE_REMOVAL, rm_stage);
            restrictedDelete(join(CWD, filename));
        }
    }


    public void log() {
        String headID = getHeadCommitId();
        Commit headCommit = readObject(join(GITLET_COMMITS_DIR, headID), Commit.class);
        printCommit(headCommit, headID);
    }

    /**
     * Recursive helper method for log to print commits
     */
    private void printCommit(Commit commit, String id) {
        List<String> parentList = commit.getParentList();
        if (parentList.size() <= 1) {
            System.out.println("===" + "\n" + "commit " + id + "\n" +
                    "Date: " + commit.getTimeStamp() + "\n" +
                    commit.getMessage() + "\n");
            if (parentList.isEmpty()) {
                return;
            }
            String parentId = parentList.get(0);
            Commit parentCommit = readObject(join(GITLET_COMMITS_DIR, parentId), Commit.class);
            printCommit(parentCommit, parentId);
        } else {
            String parent_1 = parentList.get(0).substring(0, 6);
            String parent_2 = parentList.get(1).substring(0, 6);
            System.out.println("===" + "\n" + "commit " + id + "\n" +
                    "Merge: " + parent_1 + " " + parent_2 + "\n" +
                    "Date: " + commit.getTimeStamp() + "\n" +
                    commit.getMessage() + "\n");
            String parentId = parentList.get(0);
            Commit parentCommit = readObject(join(GITLET_COMMITS_DIR, parentId), Commit.class);
            printCommit(parentCommit, parentId);
        }
    }

    public void global_log() {
        List<String> ids =  plainFilenamesIn(GITLET_COMMITS_DIR);
        assert ids != null;
        for (String s : ids) {
            Commit commit = readObject(join(GITLET_COMMITS_DIR, s), Commit.class);
            System.out.println("===" + "\n" + "commit " + s + "\n" +
                    "Date: " + commit.getTimeStamp() + "\n" +
                    commit.getMessage() + "\n");
        }
    }

    public void find(String message) {
        List<String> ids =  plainFilenamesIn(GITLET_COMMITS_DIR);
        boolean idx = true;
        assert ids != null;
        for (String s : ids) {
            Commit commit = readObject(join(GITLET_COMMITS_DIR, s), Commit.class);
            if (commit.getMessage().equals(message)) {
                idx = false;
                System.out.println(s);
            }
        }
        if (idx) {
            message("Found no commit with that message.");
        }
    }

    public void status() {
        String currBranch = readContentsAsString(GITLET_HEAD);
        String currBranchName = currBranch.substring(currBranch.lastIndexOf("\\") + 1);
        List<String> branchList = plainFilenamesIn(GITLET_REFS_heads);
        System.out.println("=== Branches ===" + "\n" + "*" + currBranchName);
        assert branchList != null;
        for (String s : branchList) {
            if (!s.equals(currBranchName)) {
                System.out.println(s);
            }
        }
        System.out.println("\n" + "=== Staged Files ===");
        Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
        Iterator<String> add_iterator = add_stage.iterator();
        while (add_iterator.hasNext()) {
            System.out.println(add_iterator.next());
        }
        System.out.println("\n" + "=== Removed Files ===");
        Stage rm_stage = readObject(GITLET_STAGE_REMOVAL, Stage.class);
        Iterator<String> rm_iterator = rm_stage.iterator();
        while (rm_iterator.hasNext()) {
            System.out.println(rm_iterator.next());
        }
        System.out.println("\n" + "=== Modifications Not Staged For Commit ===");
        printStatus();
        System.out.println("\n" + "=== Untracked Files ===");
        List<String> untrackedFiles = findUntrackedFiles();
        for (String s : untrackedFiles) {
            System.out.println(s);
        }
        System.out.println();
    }

    /** Helper method for printing out modified but not staged files */
    private void printStatus() {
        List<String> modified = new ArrayList<>();
        List<String> deleted = new ArrayList<>();
        Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
        Stage rm_stage = readObject(GITLET_STAGE_REMOVAL, Stage.class);
        Tree headCommitTree = getCommitTree(getHeadCommitId());
        Iterator<String> iterator = headCommitTree.iterator();
        while (iterator.hasNext()) {
            String fileName = iterator.next();
            //Tracked in the current commit, changed in
            // the working directory, but not staged
            if (join(CWD, fileName).exists() &&
                    !headCommitTree.get(fileName).equals(sha1(serialize(makeBlob(fileName)))) &&
                    !add_stage.containsKey(fileName)) {
                modified.add(fileName);
            }
            //Not staged for removal, but tracked in the current
            // commit and deleted from the working directory
            else if (!rm_stage.containsKey(fileName) && !join(CWD, fileName).exists()) {
                deleted.add(fileName);
            }
        }
        Iterator<String> s_iterator = add_stage.iterator();
        while (s_iterator.hasNext()) {
            String fileName = s_iterator.next();
            //Staged for addition, but with different contents than in the working directory
            if (join(CWD, fileName).exists() &&
                    !add_stage.get(fileName).equals(sha1(serialize(makeBlob(fileName))))) {
                modified.add(fileName);
            }
            //Staged for addition, but deleted in the working directory
            else if (!join(CWD, fileName).exists()) {
                deleted.add(fileName);
            }
        }
        for (String del : deleted) {
            System.out.println(del + " (deleted)");
        }
        for (String mdf : modified) {
            System.out.println(mdf + " (modified)");
        }
    }

    public void branch(String branchName) throws IOException {
        File newBranch = join(GITLET_REFS_heads, branchName);
        if (newBranch.exists()) {
            message("A branch with that name already exists.");
            return;
        }
        writeContents(newBranch, getHeadCommitId());
    }

    public void rm_branch(String branchName) {
        File branch2del = join(GITLET_REFS_heads, branchName);
        if (!branch2del.exists()) {
            message("A branch with that name does not exist.");
            return;
        }
        if (readContentsAsString(GITLET_HEAD).equals(branch2del.toString())) {
            message("Cannot remove the current branch.");
            return;
        }
        branch2del.delete();
    }

    public void reset(String id) throws IOException {
        List<String> ids =  plainFilenamesIn(GITLET_COMMITS_DIR);
        assert ids != null;
        if (!ids.contains(id)) {
            message("No commit with that id exists.");
            return;
        }
        if (!findUntrackedFiles().isEmpty()) {
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        replace(id, getHeadCommitId());
        setHeadCommitId(id);
    }

    public void checkout(int i, String branchName, String commitId
            , String fileName) throws IOException {
        switch (i) {
            case 3 -> {
                Tree tree = getCommitTree(getHeadCommitId());
                if (!tree.containsKey(fileName)) {
                        message("File does not exist in that commit.");
                } else {
                    writeFile(fileName, tree.get(fileName));
                }
            }
            case 4 -> {
                Tree tree = getCommitTree(commitId);
                if (!tree.containsKey(fileName)) {
                    throw error("File does not exist in that commit.");
                } else {
                    writeFile(fileName, tree.get(fileName));
                }
            }
            case 2 -> {
                File branch = join(GITLET_REFS_heads, branchName);
                if (!branch.exists()) {
                    message("No such branch exists.");
                } else if (readContentsAsString(GITLET_HEAD).equals(branch.toString())) {
                    message("No need to checkout the current branch.");
                } else if (!findUntrackedFiles().isEmpty()) {
                    message("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                    return;
                } else {
                    String id = readContentsAsString(branch);//CHECKED-OUT branch's head commit id
                    replace(id, getHeadCommitId());
                }
                rewrite(GITLET_HEAD, branch.toString());
            }
            default -> throw new RuntimeException("Incorrect operands.");
        }
    }

    public void merge(String branchName) throws IOException {
        Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
        Stage rm_stage = readObject(GITLET_STAGE_REMOVAL, Stage.class);
        if (!add_stage.isEmpty() || !rm_stage.isEmpty()) {
            message("You have uncommitted changes.");
            return;
        }
        File mer_branch = join(GITLET_REFS_DIR, branchName);
        if (!mer_branch.exists()) {
            message("A branch with that name does not exist.");
            return;
        }
        if (readContentsAsString(GITLET_HEAD).equals(mer_branch.toString())) {
            message("Cannot merge a branch with itself.");
            return;
        }
        if (!findUntrackedFiles().isEmpty()) {
            message("There is an untracked file in the way; " +
                    "delete it, or add and commit it first.");
            return;
        }
        String split = findSplit(getHeadCommitId(), readContentsAsString(mer_branch));
        if (split.equals(getHeadCommitId())) {
            checkout(2, branchName, null, null);
            message("Current branch fast-forwarded.");
            return;
        }
        if (split.equals(readContentsAsString(mer_branch))) {
            message("Given branch is an ancestor of the current branch.");
            return;
        }
        Commit cur_commit = readObject(join(GITLET_COMMITS_DIR, getHeadCommitId()), Commit.class);
        Commit giv_commit = readObject(join(GITLET_COMMITS_DIR,
                readContentsAsString(mer_branch)), Commit.class);
        Commit spl_commit = readObject(join(GITLET_COMMITS_DIR, split), Commit.class);
        Tree cur_tree = getCommitTree(getHeadCommitId());
        Tree giv_tree = getCommitTree(readContentsAsString(join(GITLET_REFS_DIR, branchName)));

    }

    private String findSplit(String id1, String id2) {
       Map<String, Integer> m1 = new HashMap<>();
       Map<String, Integer> m2 = new HashMap<>();
       String res = "";
       int minDep = Integer.MAX_VALUE;
       getParentMap(id1, m1, 0);
       getParentMap(id2, m2, 0);
       Set<String> keySet = m1.keySet();
       for (String s : keySet) {
           if (m2.containsKey(s) && m2.get(s) <= minDep) {
               minDep = m2.get(s);
               res = s;
           }
       }
       return res;
    }

    private void getParentMap(String id, Map<String, Integer> mp, int depth) {
        Commit commit = readObject(join(GITLET_COMMITS_DIR, id), Commit.class);
        List<String> list = commit.getParentList();
        mp.put(id, depth);
        if (list.isEmpty()) {
            return;
        }
        if (list.size() == 1) {
            getParentMap(list.get(0), mp, depth + 1);
        } else {
            getParentMap(list.get(0), mp, depth + 1);
            getParentMap(list.get(1), mp, depth + 1);
        }
    }


    /** Helper method for checkout and reset to replace old version with new version */
    private void replace(String newId, String oldId) {
        Tree oldTree = getCommitTree(oldId);
        Tree newTree = getCommitTree(newId);
        Iterator<String> new_iter = newTree.iterator();
        Iterator<String> old_iter = oldTree.iterator();
        while (new_iter.hasNext()) {
            String s1 = new_iter.next();
            writeFile(s1, newTree.get(s1));
        }
        while (old_iter.hasNext()) {
            String s2 = old_iter.next();
            if (!newTree.containsKey(s2)) {
                restrictedDelete(join(CWD, s2));
            }
        }
    }


    /** Find out is there still any untracked files in the working dir and staging area */
    private List<String> findUntrackedFiles() {
        Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
        Stage rm_stage = readObject(GITLET_STAGE_REMOVAL, Stage.class);
        List<String> fileList = plainFilenamesIn(CWD);
        List<String> untrackedFiles = new ArrayList<>();
        Tree currentTree = getCommitTree(getHeadCommitId());
        for (String fileName : fileList) {
            if (!currentTree.containsKey(fileName) && !add_stage.containsKey(fileName) ||
                    rm_stage.containsKey(fileName)) {
                untrackedFiles.add(fileName);
            }
        }
        return untrackedFiles;
    }

    /** Helper method for replace */
    private void writeFile(String fileName, String id) {
        Blob blob = readObject(join(GITLET_OBJECTS_DIR, id), Blob.class);
        File f = join(CWD, fileName);
        if (f.exists()) {
            restrictedDelete(f);
        }
        writeContents(f, blob.getContents());
    }

    /**
     * Helping method to make a blob from filename
     * @param filename
     * @return Blob
     */
    private Blob makeBlob(String filename) {
        Blob blob = new Blob();
        File path = join(CWD, filename);
        byte[] bytes = readContents(path);
        blob.setContents(bytes);
        blob.setFilename(filename);
        return blob;
    }

    /** Helper method for putting an object into a file */
    public String output(File path, Serializable obj) {
        byte []objBytes = serialize(obj);
        String sha1Id = sha1(objBytes);
        writeObject(join(path, sha1Id), obj);
        return sha1Id;
    }

    /** Helper method for rewriting String into a file */
    private void rewrite(File file, String id) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
        writeContents(file, id);
    }

    /** Helper method for rewriting an object into a file */
    private void rewriteObj(File file, Serializable obj) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.flush();
        fileWriter.close();
        writeObject(file, obj);
    }

    /** Return the head commits' ID of the current branch */
    private String getHeadCommitId() {
        String head = readContentsAsString(GITLET_HEAD);
        File headFile = new File(head);
        return readContentsAsString(headFile);
    }

    /** Save the head commits' id into the GITLET_HEAD file */
    private void setHeadCommitId(String id) throws IOException {
        String head = readContentsAsString(GITLET_HEAD);
        File headFile = new File(head);
        rewrite(headFile, id);
    }

    /** Return the tree of the given commit */
    private Tree getCommitTree(String CommitId) {
        Commit commit = readObject(join(GITLET_COMMITS_DIR, CommitId), Commit.class);
        if (commit == null) {
            throw error("No commit with that id exists.");
        }
        String treeId = commit.getTreeId();
        if (treeId == null) {
//            throw error("No tree exists in this commit.");
            return new Tree();
        }
        return readObject(join(GITLET_OBJECTS_DIR, treeId), Tree.class);
    }

    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }
    /**
     * Validate the operands' numbers
     * @param cmd The command that you input
     * @param args Arguments that you input
     * @param n Arguments' number should be n
     */
    public void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    String.format("Incorrect operands.", cmd));
        }
    }


}
