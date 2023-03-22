package gitlet;

public class Blob {

    /** Reference path. */
    private String refs;

    /** Byte array of the file */
    private byte[] contents;

    public void setRefs(String refs) {
        this.refs = refs;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public String getRefs() {
        return refs;
    }

    public byte[] getContents() {
        return contents;
    }

    private String getSha1() {
        return Utils.sha1(refs, contents);
    }
}
