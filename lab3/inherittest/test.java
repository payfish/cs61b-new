package inherittest;

public class test {
    public static void main(String[] args) {
        Animal animal = new Dog();
        Dog d = new Dog();
        animal.greet(d);
        animal.sniff(d);
        d.praise(d);
        animal.praise(d);
    }
}
