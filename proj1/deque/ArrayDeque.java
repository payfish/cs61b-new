package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private final int ZOOM_FACTOR = 2;

    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;


    public ArrayDeque() {

        int initialSize = 8;
        items = (T[]) new Object[initialSize];
        size = 0;
        nextFirst = initialSize - 1;
        nextLast = 0;

    }

    /**
     * Adds one item of type T to the front of the deque.
     * @param item
     */
    @Override
    public void addFirst(T item) {

        int n = items.length;
        items[nextFirst] = item;
        nextFirst = (nextFirst + n - 1) % n;
        size += 1;
        if (size == items.length) {
            /* at least size + 2 once a time */
            resize(size * ZOOM_FACTOR);
        }

    }

    /**
     * Resize the deque to capacity's length.
     * @param capacity
     */
    private void resize(int capacity) {

        T[] a = (T[]) new Object[capacity];
        int n = items.length;
        int f1 = (nextFirst + 1) % n;
        int l1 = (nextLast - 1 + n) % n;
        if (f1 >= l1) {
            System.arraycopy(items, f1, a, 0, n - f1);
            System.arraycopy(items, 0, a, n - f1, l1 + 1);
        } else { //
            System.arraycopy(items, f1, a, 0, size());
        }

        nextFirst = capacity - 1;
        nextLast = size;
        items = a;

    }

    /**
     * Adds an item to the back of the deque.
     * @param item
     */
    @Override
    public void addLast(T item) {

        int n = items.length;
        items[nextLast] = item;
        nextLast = (nextLast + 1) % n;
        size += 1;
        if (size == items.length) {
            resize(size * ZOOM_FACTOR);
        }
    }


    /**
     * Get the size of the deque.
     * @return
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Print out the deque with space between each item.
     */
    @Override
    public void printDeque() {
        int i = nextFirst + 1;
        while (i != nextLast) {
            System.out.print(items[i] + " ");
            i = (i + 1) % items.length;
        }
        System.out.println(" ");
    }

    /**
     * Remove the first item of the deque.
     * @return
     */
    @Override
    public T removeFirst() {

        if (size == 0) {
            return null;
        }
        nextFirst = (nextFirst + 1) % items.length;
        T x = items[nextFirst];
        items[nextFirst] = null;

        size = size - 1;
        if (size == 0) {
            nextLast = 0;
            nextFirst = items.length - 1;
        }
        if ((size < items.length / 4) && (size > 4)) {
            /** Replace the size with the (items.length) since
             * size / 4 may cause the IndexOutOfBounds Exception*/
            resize(items.length / ZOOM_FACTOR);
        }
        return x;
    }

    /**
     * Remove the last item of the deque.
     * @return
     */
    @Override
    public T removeLast() {

        if (size == 0) {
            return null;
        }
        nextLast = (nextLast + items.length - 1) % items.length;
        T x = items[nextLast];
        items[nextLast] = null;
        size = size - 1;
        if (size == 0) {
            nextLast = 0;
            nextFirst = items.length - 1;
        }
        if ((size < items.length / 4) && (size > 4)) {
            /** Replace the size with the (items.length) since
             * size / 4 may cause the IndexOutOfBounds Exception*/
            resize(items.length / ZOOM_FACTOR);
        }
        return x;
    }

    /**
     * Get the item of the deque at the exact position "index".
     * @param index
     * @return
     */
    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        return items[(index + nextFirst + 1) % items.length];
    }

    /**
     * Nested class for generating iterator
     */
    private class ArrayDequeIterator implements Iterator<T> {

        private int index;

        ArrayDequeIterator() {
            index = (nextFirst + 1) % items.length;
        }

        @Override
        public boolean hasNext() {
            return index != nextLast;
        }

        @Override
        public T next() {
            T returnItem = items[index];
            index = (index + 1) % items.length;
            return returnItem;
        }
    }
    /**
     * Iterator for the deque
     */
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }


    /**
     * Returns whether Object o equals the deque.
     * "o" is considered equal if it is a deque and
     * if it contains the same items as this deque in
     * the same order.
     * @param o
     */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (this.size != ((Deque) o).size()) {
            return false;
        }

        if (!(o instanceof ArrayDeque)) {
            Deque<T> other = (Deque<T>) o;
            for (int i = 0; i < this.size(); i += 1) {
                T t1 = other.get(i);
                T t2 = this.get(i);
                if (t2 == null) {
                    if (t1 != null) {
                        return false;
                    }
                    continue;
                }
                if (!t2.equals(t1)) {
                    return false;
                }
            }
            return true;
        }

        ArrayDeque other = (ArrayDeque) o;

        Iterator<T> iterator = this.iterator();
        Iterator<T> iterator1 = other.iterator();
        while (iterator.hasNext()) {
            T t1 = iterator.next();
            T t2 = iterator1.next();
            if (t1 == null) {
                if (t2 != null) {
                    return false;
                }
                continue;
            }
            if (!t1.equals(t2)) {
                return false;
            }
        }
        return true;

    }

}
