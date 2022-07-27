package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomSizeTest {
    private static final int []tests = {4, 5, 15, 30};
    @Test
    public void testThreeAddThreeRemove(){
        LinkedListDeque<Integer> LLD = new LinkedListDeque<>();
        ArrayDeque<Integer> AD = new ArrayDeque<>();
        for(int i: tests){
            LLD.addLast(i);
            AD.addLast(i);
        }
        assertEquals(LLD.removeLast(), AD.removeLast());
        assertEquals(LLD.removeLast(), AD.removeLast());
        assertEquals(LLD.removeLast(), AD.removeLast());
    }

    @Test
    public void randomizedTest(){
        LinkedListDeque<Integer> LLD = new LinkedListDeque<>();
        ArrayDeque<Integer> AD = new ArrayDeque<>();
        int N = 5000000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                LLD.addLast(randVal);
                AD.addLast(randVal);
            } else if (operationNumber == 1 && AD.size() > 0 ){
                assertEquals(LLD.removeLast(), AD.removeLast());
            } else {
                assertEquals(LLD.size(), AD.size());
            }
        }
    }
}
