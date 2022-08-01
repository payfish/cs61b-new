package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {

    private final Comparator<Integer> c = Comparator.comparingInt(o -> o);

    /**
     * tests for method max without arguments.
     * tests if generic type works good.
     */
    @Test
    public void comparatorTest() {

        MaxArrayDeque<Integer> mad = new MaxArrayDeque<Integer>(c);
        mad.addFirst(100);
        mad.addFirst(2);
        mad.addFirst(3);
        assertEquals(100,(int)mad.max());



        Comparator<String> c1 = String::compareTo;

        MaxArrayDeque<String> mad1 = new MaxArrayDeque<String>(c1);

        mad1.addFirst("front");
        mad1.addLast("middle");
        mad1.addLast("back");

        System.out.println("Printing out deque mad1: ");
        mad1.printDeque();
        assertEquals("middle",mad1.max(c1));

    }

    /**
     * author @xiaotianxt
     */
    @Test
    public void testMaxAugument() {
        MaxArrayDeque<Integer> md = new MaxArrayDeque<>(c);
        Comparator<Integer> c2 = (o1, o2) -> {
            if (o1 % 2 == 0) {
                if (o2 % 2 == 0) {
                    return o1 - o2;
                } else {
                    return 1;
                }
            } else {
                if (o2 % 2 == 0) {
                    return -1;
                } else {
                    return o1 - o2;
                }
            }
        };

        md.addLast(2);
        md.addLast(1);
        md.addLast(3);
        md.addLast(5);
        md.addLast(7);
        assertEquals(2, (int) md.max(c2));
        md.removeFirst();
        assertEquals(7, (int) md.max(c2));
    }
}
