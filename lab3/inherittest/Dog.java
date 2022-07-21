package inherittest;

import static edu.princeton.cs.algs4.StdOut.print;

public class Dog implements Animal{

    @Override
    public void sniff(Animal a) {
        print("Dog sniff animal");
    }

    void praise(Dog d) {
        print("u r cool dog");
    }
}
