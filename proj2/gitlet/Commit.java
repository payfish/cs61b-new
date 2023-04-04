package gitlet;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author payfish
 */
public class Commit implements Serializable {
    /**
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */
    /** When the commit took place. */
    private String timeStamp;
    /** The message of this Commit. */
    private String message;
    /** Parent commit of this commit. */
    private String author;

    private String treeId;

    private List<String> parentList;

    public Commit(String timeStamp, String message, String author, String treeId) {
        this.timeStamp = timeStamp;
        this.message = message;
        this.author = author;
        this.treeId = treeId;
        this.parentList = new ArrayList<>();
    }

    public Commit() {}


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    public List<String> getParentList() {
        return parentList;
    }

    public void setParentID(String parentID) {
        this.parentList.add(parentID);
    }

    @Override
    public String toString() {
        return "Commit{" +
                "timeStamp=" + timeStamp +
                ", message='" + message + '\'' +
                ", author='" + author + '\'' +
                ", treeId='" + treeId + '\'' +
                ", parentId='" + parentList + '\'' +
                '}';
    }
}
