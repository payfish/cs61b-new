package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomSizeTest {
    private static final int []TESTS = {4, 5, 15, 30};
    @Test
    public void testThreeAddThreeRemove() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        for (int i: TESTS) {
            lld.addLast(i);
            ad.addLast(i);
        }
        assertEquals(lld.removeLast(), ad.removeLast());
        assertEquals(lld.removeLast(), ad.removeLast());
        assertEquals(lld.removeLast(), ad.removeLast());
    }

    @Test
    public void randomizedTest() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        int N = 100000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                lld.addLast(randVal);
                ad.addLast(randVal);
            } else if (operationNumber == 1 && ad.size() > 0) {
                assertEquals(lld.removeFirst(), ad.removeFirst());
            } else if (operationNumber == 2 && ad.size() > 0) {
                assertEquals(lld.removeLast(), ad.removeLast());
            } else {
                int randVal = StdRandom.uniform(0, 100);
                lld.addFirst(randVal);
                ad.addFirst(randVal);
            }
        }
    }
}
