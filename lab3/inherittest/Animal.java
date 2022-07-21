package inherittest;

import static edu.princeton.cs.algs4.StdOut.print;

public interface Animal {
    default void greet (Animal a) {
        print("hello animal");
    }

    default void sniff (Animal a) {
        print("sniff animal");
    }

    default void praise (Animal a) {
        print("u r cool Animal");
    }
}
