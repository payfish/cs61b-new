package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {

    /**
     * Good idea to set these parameters to final,learnt from @xiaotianxt
     */
    private final int INITIAL_SIZE = 8;
    private final int ZOOM_FACTOR = 2;

    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;


    public ArrayDeque() {

        items = (T[]) new Object[INITIAL_SIZE];
        size = 0;
        nextFirst = INITIAL_SIZE - 1;
        nextLast = 0;

    }

    /**
     * Adds one item of type T to the front of the deque.
     * @param item
     */
    @Override
    public void addFirst(T item) {

        if (nextFirst == nextLast) {
            /* at least size + 2 once a time */
            resize(size * ZOOM_FACTOR);
        }
        int n = items.length;
        items[nextFirst % items.length] = item;
        nextFirst = (nextFirst + n - 1) % n;
        size += 1;

    }

    /**
     * Resize the deque to i's length.
     * @param i
     */
    private void resize(int i) {

        T[] a = (T[]) new Object[i];
        int t = (nextFirst + 1) % items.length;

        System.arraycopy(items, 0, a, 0, nextLast);
        System.arraycopy(items, t, a, i - (size - nextLast), size - nextLast);
        nextFirst = i - size + nextLast - 1;
        items = a;

    }

    /**
     * Adds an item to the back of the deque.
     * @param item
     */
    @Override
    public void addLast(T item) {

        if (nextFirst == nextLast) {
            resize(size * ZOOM_FACTOR);
        }
        int n = items.length;
        items[nextLast % items.length] = item;
        nextLast = (nextLast + 1) % n;
        size += 1;

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
        if ((size < items.length / 4) && (size > 4)) {
            /** Replace the size with the items.length since
             * size / 4 may cause the IndexOutOfBounds Exception*/
            resize(items.length / ZOOM_FACTOR);
        }
        if (size == 0) {
            return null;
        }
        int i = (++nextFirst) % items.length;
        T x = items[i];
        items[i] = null;
        size = size - 1;
        return x;
    }

    /**
     * Remove the last item of the deque.
     * @return
     */
    @Override
    public T removeLast() {
        if ((size < items.length / 4) && (size > 4)) {
            /** Replace the size with the items.length since
             * size / 4 may cause the IndexOutOfBounds Exception*/
            resize(items.length / ZOOM_FACTOR);
        }
        if (size == 0) {
            return null;
        }
        int i = (--nextLast + items.length) % items.length;
        T x = items[i];
        items[i] = null;
        size = size - 1;
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
                if (!t1.equals(t2)) {
                    return false;
                }
            }
            return true;
        }

        ArrayDeque other = (ArrayDeque) o;

        if (this.getClass() != other.getClass()) {
            return false;
        }

        Iterator<T> iterator = this.iterator();
        Iterator<T> iterator1 = other.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() != iterator1.next()) {
                return false;
            }
        }
        return true;

    }

    /**
     * helper method for equals
     */


}
