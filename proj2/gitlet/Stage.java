package gitlet;

import java.util.ArrayList;
import java.util.List;

public class Stage {
    private List<Blob> list;

    public Stage() {
        list = new ArrayList<>();
    }

    public void add2stage(Blob blob) {
        list.add(blob);
    }

    public void add2stage(List<Blob> blobs) {
        list.addAll(blobs);
    }

    public List<Blob> getStagedBlobs() {
        return list;
    }
}
