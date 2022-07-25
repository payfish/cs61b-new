package deque;

public class ArrayDeque<T> implements Deque<T> {

    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    /**
     * constructor
     */
    public ArrayDeque() {

        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 7;
        nextLast = 0;

    }

    @Override
    public void addFirst(T item) {

        if(nextFirst == nextLast) {
            /* at least size + 2 once a time */
            resize(size * 2);
        }
        int n = items.length;
        items[nextFirst] = item;
        nextFirst = (nextFirst + n - 1) % n;
        size += 1;

    }

    /**
     * resize method for add operation
     * @param i
     */
    public void resize(int i) {

        T[] a = (T[]) new Object[i];
        int t = (nextFirst + 1 + items.length) % items.length;

        System.arraycopy(items, 0, a, 0, nextLast);
        System.arraycopy(items, t, a, i - (size - nextLast), size - nextLast);
        nextFirst = i - size + nextLast - 1;
        items = a;

    }

    @Override
    public void addLast(T item) {

        if(nextFirst == nextLast) {
            resize(size * 2);
        }
        int n = items.length;
        items[nextLast] = item;
        nextLast = (nextLast + n + 1) % n;
        size += 1;

    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int i = nextFirst + 1;
        while(i != nextLast) {
            System.out.print(items[i] + " ");
            i = (i + 1 + items.length) % items.length;
        }
        System.out.println(" ");
    }

    @Override
    public T removeFirst() {
        if ((size < items.length / 4) && (size > 4)) {
            /** Replace the size with the items.length since size / 4 may cause the IndexOutOfBounds Exception*/
            resize(items.length / 4);
        }
        if(size == 0)
            return null;
        int i = (++nextFirst  + items.length) % items.length;
        T x = get(i);
        items[i] = null;
        size = size - 1;
        return x;
    }

    @Override
    public T removeLast() {
        if ((size < items.length / 4) && (size > 4)) {
            /** Replace the size with the items.length since size / 4 may cause the IndexOutOfBounds Exception*/
            resize(items.length / 4);
        }
        if(size == 0)
            return null;
        int i = (--nextLast  + items.length) % items.length;
        T x = get(i);
        items[i] = null;
        size = size - 1;
        return x;
    }

    @Override
    public T get(int index) {
        if(index >= items.length || index < 0)
            return null;
        return items[index];
    }
}
