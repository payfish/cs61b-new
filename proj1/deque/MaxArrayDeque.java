package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    public MaxArrayDeque() {
        super();
    }


    @Override
    public void addFirst(T item) {
        super.addFirst(item);
    }

    @Override
    public void addLast(T item) {
        super.addLast(item);
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public void printDeque() {
        super.printDeque();
    }

    @Override
    public T removeFirst() {
        return super.removeFirst();
    }

    @Override
    public T removeLast() {
        return super.removeLast();
    }

    @Override
    public T get(int index) {
        return super.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return super.iterator();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public MaxArrayDeque(Comparator<T> c) {

    }

    public T max() {
        return null;
    }

    public T max(Comparator<T> c) {
        return null;
    }

}
