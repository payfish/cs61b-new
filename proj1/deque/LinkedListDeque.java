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

    @Override
    public void addFirst(T item) {
        listnode l = new listnode(item);

        sentinel.next.pre = l;
        l.next = sentinel.next;
        l.pre = sentinel;
        sentinel.next = l;

        size += 1;
    }

    @Override
    public void addLast(T item) {
        listnode l = new listnode(item);

        l.next = sentinel;
        sentinel.pre.next = l;
        l.pre = sentinel.pre;
        sentinel.pre = l;

        size += 1;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        listnode print = sentinel.next;

        while(print != sentinel){
            System.out.print(print.item + " ");
            print = print.next;
        }

        System.out.println("");
    }

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
     *
     * @return
     */
    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }


    /**
     *
     * @param o
     * @return
     */
    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        if (this == o || this == null && o == null) {
            return true;
        }

        LinkedListDeque<T> other = (LinkedListDeque<T>) o;
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
