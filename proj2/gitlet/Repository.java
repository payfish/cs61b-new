package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
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

    public static final File GITLET_REFS_HEADS = join(GITLET_REFS_DIR, "heads");

    public static final File GITLET_HEAD = join(GITLET_DIR, "HEAD");

    public static final File GITLET_AUTHOR = join(GITLET_DIR, "AUTHOR");




    public void init()  {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system " +
                    "already exists in the current directory.");
            return;
        } else {
            initialize();
        }
    }

    /** Helper method to initial a repository */
    private void initialize() {
        GITLET_COMMITS_DIR.mkdirs();
        GITLET_REFS_HEADS.mkdirs();
        Commit commit = new Commit(dateToTimeStamp(new Date(0)),
                "initial commit", getAuthor(), null);
        String sha1Id = output(GITLET_COMMITS_DIR, commit);
        File branchMaster = join(GITLET_REFS_HEADS, "master");
        writeContents(branchMaster, sha1Id); // crate a new branch named master
        writeContents(GITLET_HEAD, branchMaster.toString());
        writeContents(GITLET_AUTHOR, "PayFish");
        refreshStage();
    }

    private String getAuthor() {
        return readContentsAsString(GITLET_AUTHOR);
    }


    public void add(String fileName)  {
        checkIfInit();
        if (!join(CWD, fileName).exists()) {
            message("File does not exist.");
            return;
        }
        if (fileName.equals("*")) {
            addAll();
        } else {
            Blob blob = makeBlob(fileName);
            String blobId = output(GITLET_OBJECTS_DIR, blob);
            Tree tree = getCommitTree(getHeadCommitId());
            Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
            Stage rm_stage = readObject(GITLET_STAGE_REMOVAL, Stage.class);
            if (tree.containsKey(fileName) && tree.get(fileName).equals(blobId)) {
                if (add_stage.containsKey(fileName) && add_stage.get(fileName).equals(blobId)) {
                    add_stage.remove(fileName);
                }
                if (rm_stage.containsKey(fileName) && rm_stage.get(fileName).equals(blobId)) {
                    rm_stage.remove(fileName);
                }
                writeObject(GITLET_STAGE_ADDITION, add_stage);
                writeObject(GITLET_STAGE_REMOVAL, rm_stage);
                return;
            }
            add_stage.put(fileName, blobId);
            writeObject(GITLET_STAGE_ADDITION, add_stage);
        }
    }

    /** Helper method for add() to add all files which were modified or new added at once. */
    private void addAll() {
    }

    public void commit(String message)  {
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
        /* Add HEAD commits' tree to the new tree */
        String headCommitId = getHeadCommitId();
        Tree tree = getCommitTree(headCommitId);
        /* Add the blobs staged for addition or removal to new tree */
        staging(tree, add_stage, rm_stage);
        /* Save the tree and commit to the repo */
        String treeId = output(GITLET_OBJECTS_DIR, tree);
        commit.setTreeId(treeId);
        commit.setParentID(headCommitId);
        String commitId = output(GITLET_COMMITS_DIR, commit);
        setHeadCommitId(commitId);
        //clear the staging area after the commit
        refreshStage();
    }

    /** Add files on the add_stage to the tree and remove files on the rm_stage from the tree */
    private void staging(Tree tree, Stage add_stage, Stage rm_stage) {
        Iterator<String> iterator_add = add_stage.iterator();
        Iterator<String> iterator_rm = rm_stage.iterator();
        while (iterator_rm.hasNext()) {
            String nextKey = iterator_rm.next();
            tree.remove(nextKey);
        }
        while (iterator_add.hasNext()) {
            String nextKey = iterator_add.next();
            tree.put(nextKey, add_stage.get(nextKey));
        }
    }


    public void rm(String filename)  {
        Tree headTree = getCommitTree(getHeadCommitId());
        Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
        Stage rm_stage = readObject(GITLET_STAGE_REMOVAL, Stage.class);
        File file = join(CWD, filename);
        /* Unstage the file if it is currently staged for addition */
        if (!headTree.containsKey(filename) && add_stage.containsKey(filename)) {
            add_stage.remove(filename);
        }
        /* Stage the file tracked in the current commit
          for removal and remove it from the working dir */
        else if (headTree.containsKey(filename) && file.exists()) {
            rm_stage.put(filename, headTree.get(filename));
            restrictedDelete(file);
        } else if (headTree.containsKey(filename) && !file.exists()) {
            rm_stage.put(filename, headTree.get(filename));
            writeObject(GITLET_STAGE_REMOVAL, rm_stage);
        } else if (!headTree.containsKey(filename) && !add_stage.containsKey(filename)) {
            message("No reason to remove the file.");
        }
        writeObject(GITLET_STAGE_REMOVAL, rm_stage);
        writeObject(GITLET_STAGE_ADDITION, add_stage);
    }


    public void log() {
        String headID = getHeadCommitId();
        Commit headCommit = getCommit(headID);
        assert headCommit != null;
        printCommit(headCommit, headID);
    }

    /** Recursive helper method for log to print commits. */
    private void printCommit(Commit commit, String id) {
        List<String> parentList = commit.getParentList();
        if (parentList.size() <= 1) {
            System.out.println("===" + "\n" + "commit " + id + "\n" +
                    "Date: " + commit.getTimeStamp() + "\n" +
                    commit.getMessage() + "\n");
            if (parentList.isEmpty()) {
                return;
            }
        } else {
            printCommitsWith2Parents(id, commit, parentList);
        }
        String parentId = parentList.get(0);
        Commit parentCommit = getCommit(parentId);
        printCommit(parentCommit, parentId);
    }


    public void global_log() {
        List<String> ids =  plainFilenamesIn(GITLET_COMMITS_DIR);
        assert ids != null;
        for (String s : ids) {
            Commit commit = getCommit(s);
            List<String> parentList = commit.getParentList();
            if (parentList.size() == 2) {
                printCommitsWith2Parents(s, commit, parentList);
            } else {
                System.out.println("===" + "\n" + "commit " + s + "\n" +
                        "Date: " + commit.getTimeStamp() + "\n" +
                        commit.getMessage() + "\n");
            }
        }
    }

    /** Print the merge commits' info. */
    private void printCommitsWith2Parents(String s, Commit commit, List<String> parentList) {
        String parent_1 = parentList.get(0).substring(0, 7);
        String parent_2 = parentList.get(1).substring(0, 7);
        System.out.println("===" + "\n" + "commit " + s + "\n" +
                "Merge: " + parent_1 + " " + parent_2 + "\n" +
                "Date: " + commit.getTimeStamp() + "\n" +
                commit.getMessage() + "\n");
    }


    public void find(String message) {
        List<String> ids =  plainFilenamesIn(GITLET_COMMITS_DIR);
        boolean idx = true;
        assert ids != null;
        for (String s : ids) {
            Commit commit = getCommit(s);
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
        checkIfInit();
        List<String> branchList = plainFilenamesIn(GITLET_REFS_HEADS);
        String currBranchName = getCurrBranchName();
        System.out.println("=== Branches ===" + "\n" + "*" + currBranchName);
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

    /** Return the current branch' name */
    private String getCurrBranchName() {
        String currBranch = readContentsAsString(GITLET_HEAD);
        List<String> names = plainFilenamesIn(GITLET_REFS_HEADS);
        //return currBranch.substring(currBranch.lastIndexOf(System.getProperty("file.separator")) + 1);
        //file.separator couldn't work well on the autograder
        for (String name : names) {
            if (currBranch.endsWith(name)) {
                return name;
            }
        }
        return null;
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
            if (!rm_stage.containsKey(fileName) && !join(CWD, fileName).exists()) {
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
            if (!join(CWD, fileName).exists()) {
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


    public void branch(String branchName)  {
        File newBranch = join(GITLET_REFS_HEADS, branchName);
        if (newBranch.exists()) {
            message("A branch with that name already exists.");
            return;
        }
        writeContents(newBranch, getHeadCommitId());
    }


    public void rm_branch(String branchName) {
        File branch2del = join(GITLET_REFS_HEADS, branchName);
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


    public void reset(String id)  {
        if (!findUntrackedFiles().isEmpty()) {
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        String realID = expandID(id);
        if (realID == null) {
            message("No commit with that id exists.");
            return;
        }
        replace(realID);
        setHeadCommitId(realID);
        refreshStage();
    }


    public void checkout(String []args) {
        int n = args.length;
        if (n > 2 && !args[n - 2].equals("--")) {
            message("Incorrect operands.");
            return;
        }
        switch (n) {
            case 3 -> {
                String fileName = args[2];
                Tree tree = getCommitTree(getHeadCommitId());
                if (!tree.containsKey(fileName)) {
                    message("File does not exist in that commit.");
                } else {
                    writeFile(fileName, tree.get(fileName));
                }
            }
            case 4 -> {
                String commitId = args[1];
                String fileName = args[3];
                Tree tree = getCommitTree(commitId);
                if (tree.isEmpty()) {
                    message("No commit with that id exists.");
                } else if (!tree.containsKey(fileName)) {
                    message("File does not exist in that commit.");
                } else {
                    writeFile(fileName, tree.get(fileName));
                }
            }
            case 2 -> {
                String branchName = args[1];
                File branch = join(GITLET_REFS_HEADS, branchName);
                if (!branch.exists()) {
                    message("No such branch exists.");
                    return;
                } else if (readContentsAsString(GITLET_HEAD).equals(branch.toString())) {
                    message("No need to checkout the current branch.");
                    return;
                } else if (!findUntrackedFiles().isEmpty()) {
                    message("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                    return;
                } else {
                    String id = readContentsAsString(branch);//CHECKED-OUT branch's head commit id
                    replace(id);
                }
                writeContents(GITLET_HEAD, branch.toString());
            }
            default -> message("Incorrect Operand");
        }
    }


    /** Helper method for checkout and reset to replace old version with new version */
    private void replace(String newId) {
        Tree newTree = getCommitTree(newId);
        Iterator<String> new_iter = newTree.iterator();
        List<String> list = plainFilenamesIn(CWD);
        for (String l : list) {
            restrictedDelete(join(CWD, l));
        }
        while (new_iter.hasNext()) {
            String s1 = new_iter.next();
            writeFile(s1, newTree.get(s1));
        }
    }


    public void merge(String branchName)  {
        File mer_branch = join(GITLET_REFS_HEADS, branchName);
        Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
        Stage rm_stage = readObject(GITLET_STAGE_REMOVAL, Stage.class);
        String headID = getHeadCommitId();
        if (!add_stage.isEmpty() || !rm_stage.isEmpty()) {
            message("You have uncommitted changes.");
            return;
        }
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
        String mergeID = readContentsAsString(mer_branch);
        String split = findSplit(headID, mergeID);
        if (split.equals(headID)) {
            String []args = {" ", branchName};
            checkout(args);
            message("Current branch fast-forwarded.");
            return;
        }
        if (split.equals(mergeID)) {
            message("Given branch is an ancestor of the current branch.");
            return;
        }
        Tree cur_tree = getCommitTree(headID);
        Tree giv_tree = getCommitTree(mergeID);
        Tree spl_tree = getCommitTree(split);
        Set<String> set = new HashSet<>(cur_tree.keySet());
        set.addAll(giv_tree.keySet());
        set.addAll(spl_tree.keySet());
        Boolean index = false;
        for (String s : set) {
            if (spl_tree.containsKey(s) && giv_tree.containsKey(s) && cur_tree.containsKey(s)) {
                String spl = spl_tree.get(s);
                String cur = cur_tree.get(s);
                String giv = giv_tree.get(s);
                if (spl.equals(cur) && !spl.equals(giv)) {
                    writeFile(s, giv);
                    add_stage.put(s, giv);
                } else if (!spl.equals(cur) && !spl.equals(giv) && !giv.equals(cur)) {
                    conflict(s, cur, giv);
                    rm_stage.put(s, cur);
                    add_stage.put(s, sha1(serialize(makeBlob(s))));
                    index = true;
                }
            } else if (!spl_tree.containsKey(s) && giv_tree.containsKey(s) && !cur_tree.containsKey(s)) {
                String giv = giv_tree.get(s);
                writeFile(s, giv);
                add_stage.put(s, giv);
            } else if (spl_tree.containsKey(s) && !giv_tree.containsKey(s) && cur_tree.containsKey(s)) {
                String spl = spl_tree.get(s);
                String cur = cur_tree.get(s);
                if (spl.equals(cur)) {
                    deleteFile(s);
                    rm_stage.put(s, spl);
                } else {
                    conflict(s, cur, null);
                    rm_stage.put(s, cur);
                    add_stage.put(s, sha1(serialize(makeBlob(s))));
                    index = true;
                }
            } else if (spl_tree.containsKey(s) && giv_tree.containsKey(s) && !cur_tree.containsKey(s)) {
                String spl = spl_tree.get(s);
                String giv = giv_tree.get(s);
                if (!spl.equals(giv)) {
                    conflict(s, null, giv);
                    add_stage.put(s, sha1(serialize(makeBlob(s))));
                    index = true;
                }
            } else if (!spl_tree.containsKey(s) && giv_tree.containsKey(s) && cur_tree.containsKey(s)) {
                String giv = giv_tree.get(s);
                String cur = cur_tree.get(s);
                if (!cur.equals(giv)) {
                    conflict(s, cur, giv);
                    rm_stage.put(s, cur);
                    add_stage.put(s, sha1(serialize(makeBlob(s))));
                    index = true;
                }
            }
        }
        if (add_stage.isEmpty() && rm_stage.isEmpty()) {
            message("No changes added to the commit!");
            return;
        }
        if (index) {
            message("Encountered a merge conflict.");
        }
        String currBranchName = getCurrBranchName();
        Commit mergeCommit = new Commit(dateToTimeStamp(new Date()),
                "Merged " + branchName + " into " + currBranchName + ".", getAuthor(), null);
        staging(cur_tree, add_stage, rm_stage);
        String treeId = output(GITLET_OBJECTS_DIR, cur_tree);
        mergeCommit.setTreeId(treeId);
        mergeCommit.setParentID(headID);
        mergeCommit.setParentID(mergeID);
        String commitId = output(GITLET_COMMITS_DIR, mergeCommit);
        setHeadCommitId(commitId);
        refreshStage();
    }

    /** Replace the conflict file with specific contents.*/
    private void conflict(String fileName, String curID, String givID) {
        File file = join(CWD, fileName);
        writeContents(file, "<<<<<<< HEAD" + "\n");
        if (curID != null) {
            writeContents(file, readContentsAsString(file),
                    new String(readObject(join(GITLET_OBJECTS_DIR, curID), Blob.class).getContents(),
                            StandardCharsets.UTF_8));
        }
        writeContents(file, readContentsAsString(file), "=======" + "\n");
        if (givID != null) {
            writeContents(file, readContentsAsString(file),
                    new String(readObject(join(GITLET_OBJECTS_DIR, givID), Blob.class).getContents(),
                            StandardCharsets.UTF_8));
        }
        writeContents(file, readContentsAsString(file) + ">>>>>>>" + "\n");
    }

    private void deleteFile(String fileName) {
        restrictedDelete(join(CWD, fileName));
    }

    /** Search the split point of current branch and given branch and return its id */
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

    /** Recursive helper method to find a map of the given commits' parent commits */
    private void getParentMap(String id, Map<String, Integer> mp, int depth) {
        Commit commit = getCommit(id);
        assert commit != null;
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

    /** Find out is there still any untracked files in the working dir and staging area */
    private List<String> findUntrackedFiles() {
        Stage add_stage = readObject(GITLET_STAGE_ADDITION, Stage.class);
        Stage rm_stage = readObject(GITLET_STAGE_REMOVAL, Stage.class);
        List<String> fileList = plainFilenamesIn(CWD);
        List<String> untrackedFiles = new ArrayList<>();
        Tree currentTree = getCommitTree(getHeadCommitId());
        if (fileList != null) {
            for (String fileName : fileList) {
                if (!currentTree.containsKey(fileName) && !add_stage.containsKey(fileName)
                        || rm_stage.containsKey(fileName)) {
                    untrackedFiles.add(fileName);
                }
            }
        }
        return untrackedFiles;
    }

    /** Helper method for replace */
    private void writeFile(String fileName, String id) {
        Blob blob = readObject(join(GITLET_OBJECTS_DIR, id), Blob.class);
        File f = join(CWD, fileName);
        writeContents(f, new String(blob.getContents(), StandardCharsets.UTF_8));
    }

    /**
     * Helping method to make a blob from filename
     * @param filename of a file
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


    /** Return the head commits' ID of the current branch */
    private String getHeadCommitId() {
        String head = readContentsAsString(GITLET_HEAD);
        File headFile = new File(head);
        return readContentsAsString(headFile);
    }

    /** Save the head commits' id into the GITLET_HEAD file */
    private void setHeadCommitId(String id)  {
        String head = readContentsAsString(GITLET_HEAD);
        File headFile = new File(head);
        writeContents(headFile, id);
    }

    /** Lengthen the commit id and return the normal length commit id or null if it doesn't exist */
    private String expandID(String id) {
        List<String> ids =  plainFilenamesIn(GITLET_COMMITS_DIR);
        if (id.length() < 40) {
            assert ids != null;
            for (String _id : ids) {
                if (_id.contains(id)) {
                    return _id;
                }
            }
        } else {
            if (!ids.contains(id)) {
                return null;
            } else {
                return id;
            }
        }
        return null;
    }

    /**
     * Takes a commit id and returns the specific commit
     * @param id
     * @return Commit
     */
    private Commit getCommit(String id) {
        String realID = expandID(id);
        if (realID == null) {
            return null;
        }
        return readObject(join(GITLET_COMMITS_DIR, realID), Commit.class);
    }

    /** Return the tree of the given commit */
    private Tree getCommitTree(String id) {
        Commit commit = getCommit(id);
        if (commit == null) {
            return new Tree();
        }
        String treeId = commit.getTreeId();
        if (treeId == null) {
            return new Tree();
        }
        return readObject(join(GITLET_OBJECTS_DIR, treeId), Tree.class);
    }

    /**
     * Transfer the date to String format to satisfy the autograder
     * @param date
     * @return String format of date
     */
    private static String dateToTimeStamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    /** Refresh the staging area */
    private void refreshStage() {
        writeObject(GITLET_STAGE_ADDITION, new Stage());
        writeObject(GITLET_STAGE_REMOVAL, new Stage());
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

    /**
     * Checker to check if there's a gitlet repo in the working directory
     */
    private void checkIfInit() {
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
