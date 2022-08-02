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
}
