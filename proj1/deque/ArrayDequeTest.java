package deque;


import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigADequeTest() {

        Deque<Integer> ad = new ArrayDeque<>();
        for (int i = 0; i < 1000000; i++) {
            ad.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) ad.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) ad.removeLast(), 0.0);
        }
    }

    @Test
    /** Adds a few things to the Array, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {

        ArrayDeque<String> ad = new ArrayDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", ad.isEmpty());
        ad.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, ad.size());
        assertFalse("lld1 should now contain 1 item", ad.isEmpty());

        ad.addLast("middle");
        assertEquals(2, ad.size());

        ad.addLast("back");
        assertEquals(3, ad.size());

        System.out.println("Printing out deque: ");
        ad.printDeque();

    }


    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {

        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", ad.isEmpty());

        ad.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", ad.isEmpty());

        ad.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", ad.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        ad.addFirst(3);

        ad.removeLast();
        ad.removeFirst();
        ad.removeLast();
        ad.removeFirst();

        int size = ad.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {

        ArrayDeque<String> ad1 = new ArrayDeque<String>();
        ArrayDeque<Double> ad2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> ad3 = new ArrayDeque<Boolean>();

        ad1.addFirst("string");
        ad2.addFirst(3.14159);
        ad3.addFirst(true);

        String s = ad1.removeFirst();
        double d = ad2.removeFirst();
        boolean b = ad3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, ad.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, ad.removeLast());

    }


    @Test
    public void iteratorTest() {

        ArrayDeque<Integer> arrd1 = new ArrayDeque<>();
        arrd1.addFirst(3);
        arrd1.addFirst(4);
        arrd1.addLast(5);
        arrd1.addLast(6);
        StringBuffer sb = new StringBuffer("{");
        Iterator<Integer> lls = arrd1.iterator();
        while (lls.hasNext()) {
            sb.append(lls.next());
            sb.append(",");
        }
        sb.append("}");
        System.out.println(sb.toString());

    }
    @Test
    public void equalsTest() {

        ArrayDeque<Integer> arrd1 = new ArrayDeque<>();
        ArrayDeque<Integer> arrd2 = new ArrayDeque<>();
        ArrayDeque<String> arrd3 = new ArrayDeque<>();

        arrd1.addFirst(3);
        arrd1.addFirst(4);

        arrd2.addFirst(3);
        arrd2.addFirst(4);
        arrd2.addFirst(7);

        arrd3.addFirst("3");
        arrd3.addFirst("4");

        assertFalse(arrd1.equals(arrd2));
        assertFalse(arrd1.equals(arrd3));

    }
}
