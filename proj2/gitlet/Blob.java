package gitlet;

import java.io.Serializable;

public class Blob implements Serializable {

    private String filename;
    /** Byte array of the file */
    private byte[] contents;

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getContents() {
        return contents;
    }

    public String getSha1() {
        return Utils.sha1(filename, contents);
    }
}
