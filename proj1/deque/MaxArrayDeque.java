package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private final Comparator<T> _c;

    public MaxArrayDeque(Comparator<T> c) {
        _c = c;
    }

    public T max() {
        return max(_c);
    }

    public T max(Comparator<T> c) {
        if (this.size() == 0) {
            return null;
        }
        T res = this.get(0);
        Iterator<T> iterator = this.iterator();
        while (iterator.hasNext()) {
            T tem = iterator.next();
            res = c.compare(res, tem) > 0 ? res : tem;
        }
        return res;
    }

}
