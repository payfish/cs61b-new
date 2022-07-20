package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    private static final int []tests = {4, 5, 15, 30};
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> ANR = new AListNoResizing<>();
        BuggyAList<Integer> BA = new BuggyAList<>();
        for(int i: tests){
            ANR.addLast(i);
            BA.addLast(i);
        }
        assertEquals(ANR.removeLast(), BA.removeLast());
        assertEquals(ANR.removeLast(), BA.removeLast());
        assertEquals(ANR.removeLast(), BA.removeLast());
    }

    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> BL = new BuggyAList<>();
        int N = 50000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                BL.addLast(randVal);
            } else if (operationNumber == 1 && BL.size() > 0) {
                //getLast
                assertEquals(L.getLast(), BL.getLast());
            } else if (operationNumber == 2 && BL.size() > 0 ){
                assertEquals(L.removeLast(), BL.removeLast());
            } else {
                assertEquals(L.size(), BL.size());
            }
        }
    }

}
