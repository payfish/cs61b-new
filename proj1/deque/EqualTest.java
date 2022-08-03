package deque;

import org.junit.Test;

import static org.junit.Assert.*;

public class EqualTest {
    @Test
    public void testTwoDequeEqual() {
        Deque<Integer> ad = new ArrayDeque<>();
        Deque<Integer> lld = new LinkedListDeque<>();

        assertTrue(ad.equals(lld));
        assertTrue(lld.equals(lld));
        assertTrue(lld.equals(ad));

        ad.addLast(1);
        lld.addLast(1);
        assertTrue(lld.equals(ad));

        ad.removeFirst();
        assertFalse(ad.equals(lld));

        lld.removeLast();
        assertTrue(lld.equals(ad));

        for (int i = 0; i < 100; i += 1) {
            ad.addFirst(i);
            lld.addFirst(i);
        }
        assertTrue(ad.equals(lld));
    }

    @Test
    public void testNullItem() {
        Deque<Integer> ad = new ArrayDeque<>();
        Deque<Integer> lld = new LinkedListDeque<>();

        ad.addFirst(1);
        ad.addLast(2);

        lld.addFirst(1);
        lld.addLast(2);

        assertTrue(ad.equals(lld));
        assertTrue(lld.equals(ad));
    }

    @Test
    public void testSameDeque() {
        Deque<Integer> lld = new LinkedListDeque<>();
        Deque<Integer> ad = new ArrayDeque<>();
        assertTrue(ad.equals(ad));
        assertTrue(lld.equals(lld));

        lld.addLast(1);
        assertTrue(lld.equals(lld));
    }

    @Test
    public void testDiffDeque() {
        Deque<Integer> lld = new LinkedListDeque<>();
        Deque<Integer> ad = new ArrayDeque<>();

        lld.addLast(1);
        lld.addFirst(1);

        ad.addLast(1);
        ad.addFirst(1);

        assertTrue(lld.equals(ad));

    }

    @Test
    public void testDiffTypes() {
        Deque<String> lld1 = new LinkedListDeque<>();
        Deque<String> ad1 = new ArrayDeque<>();
        Deque<Integer> lld2 = new LinkedListDeque<>();
        Deque<Integer> ad2 = new ArrayDeque<>();

        lld1.addFirst("1");
        lld2.addFirst(1);
        ad1.addFirst("1");
        ad2.addFirst(1);
        assertFalse(lld1.equals(ad2));
        assertFalse(ad1.equals(lld2));
        assertFalse(lld1.equals(lld2));
    }
}
