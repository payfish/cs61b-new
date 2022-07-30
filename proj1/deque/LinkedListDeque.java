package deque;

import java.util.Iterator;

public class LinkedListDeque <T> implements Deque <T>  {

    private int size;
    private final listnode<T> sentinel;


    /**
     * class of each LinkedListDeque node
     * @param <T>
     */
    private class listnode <T> {
        private T item;
        private listnode<T> next;
        private listnode<T> pre;

        private listnode(T t) {
            this.item = t;
        }
    }

    public LinkedListDeque() {

        sentinel = new listnode<>(null);
        sentinel.next = sentinel;
        sentinel.pre = sentinel;
        size = 0;
    }

    /**
     * Adds one item of type T to the front of the deque.
     * @param item
     */
    @Override
    public void addFirst(T item) {
        listnode l = new listnode(item);

        sentinel.next.pre = l;
        l.next = sentinel.next;
        l.pre = sentinel;
        sentinel.next = l;

        size += 1;
    }

    /**
     * Adds one item of type T to the back of the deque.
     * @param item
     */
    @Override
    public void addLast(T item) {
        listnode l = new listnode(item);

        l.next = sentinel;
        sentinel.pre.next = l;
        l.pre = sentinel.pre;
        sentinel.pre = l;

        size += 1;
    }

    /**
     * Get the size of the deque.
     * @return size
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
        listnode print = sentinel.next;

        while(print != sentinel){
            System.out.print(print.item + " ");
            print = print.next;
        }

        System.out.println("");
    }

    /**
     * Remove the first item of the deque.
     * @return
     */
    @Override
    public T removeFirst() {
        if(sentinel.next == sentinel) {
            return null;
        }
        listnode<T> first = sentinel.next;
        sentinel.next = first.next;
        first.next.pre = sentinel;
        size -= 1;
        return  first.item;
    }

    /**
     * Remove the last item of the deque.
     * @return
     */
    @Override
    public T removeLast() {
        if(sentinel.next == sentinel) {
            return null;
        }
        listnode<T> last = sentinel.pre;
        sentinel.pre = last.pre;
        last.pre.next = sentinel;
        size -= 1;
        return last.item;
    }

    /**
     * Get the item of the deque at the exact position "index".
     * @param index
     * @return
     */
    @Override
    public T get(int index) {
        if(index > size) {
            return null;
        }
        listnode<T> idx = sentinel.next;
        while(index > 0) {
            idx = idx.next;
            index -= 1;
        }
        return idx.item;
    }

    /**
     * Nested class for generating iterator
     */
    private class LinkedListIterator implements Iterator<T> {

        private listnode<T> nextNode = sentinel.next;

        @Override
        public boolean hasNext() {
            return nextNode != sentinel;
        }

        @Override
        public T next() {
            T returnItem = nextNode.item;
            nextNode = nextNode.next;
            return returnItem;
        }
    }
    /**
     * iterator for LinkedListDeque
     * @return
     */
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }


    /**
     * Returns whether Object o equals the deque.
     * "o" is considered equal if it is a deque and
     * if it contains the same items as this deque in
     * the same order.
     * @param o
     */
    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        if (this == o || this == null && o == null) {
            return true;
        }

        LinkedListDeque<T> other = (LinkedListDeque<T>) o;

        if(this.getClass() != other.getClass()) {
            return false;
        }
        if (this.size != other.size()) {
            return false;
        }

        Iterator<T> iterator = this.iterator();
        Iterator<T> iterator1 = other.iterator();
        while (iterator.hasNext()) {
            if(iterator.next() != iterator1.next()) {
                return false;
            }
        }
        return true;
    }

    /**
     * same as get(),but using recursive
     * @param index
     * @return
     */
    public T getRecursive(int index) {
        if(index > size || index < 0) {
            return null;
        }
        return recursive(index, sentinel.next);
    }

    /**
     * helper method for getRecursive().
     * @param index
     * @param ln
     * @return
     */
    public T recursive(int index, listnode<T> ln) {
        if(index == 0) {
            return ln.item;
        }
        return recursive(index - 1, ln.next);
    }

}
