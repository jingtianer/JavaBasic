package genericDemo;

import common.Man;
import common.Person;

import java.io.IOException;

// Generic class may not extend 'java.lang.Throwable'
//class Pair<T> extends Throwable {
class Pair<T> {
    private T first;

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public T getSecond() {
        return second;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    private T second;
    Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }
    Pair() {
        this(null, null);
    }

//     'equals(T)' in 'genericDemo.Pair' clashes with 'equals(Object)' in 'java.lang.Object'; both methods have same erasure, yet neither overrides the other
//    public boolean equals(T obj) {
//        if(obj == null) return false;
//        return ((Pair<?>) obj).first.equals(this.first)
//                || ((Pair<?>) obj).second.equals(this.second);
//    }

    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(this == obj) return true;
        if(obj.getClass() != getClass()) return false;
        return ((Pair<?>) obj).first.equals(this.first)
                && ((Pair<?>) obj).second.equals(this.second);
    }

}

public class GenericDemo {
    static <T extends Throwable> void throwAs(Throwable t) throws T {
        throw (T) t;
    }
    static <T> void setFirst(Pair<T> pair, T first) {
        pair.setFirst(first);
    }
    static <T> T getFirst(Pair<T> pair) {
        return pair.getFirst();
    }
    public static void main(String[] args) {
        new Thread() {
            @Override
            public void run() {
                var height2Weight = new Pair<>(165, 75);
                var nickName2Name = new Pair<>(new Pair<>("Meow", "Liu"), new Pair<>("Jingtian", "Liu"));
                var firstName2LastName = new Pair<>("Jingtian", "Liu");
                Pair<? super Man> manPair = new Pair<Man>();
                Pair<? extends Person> personnPair = new Pair<Man>();
                Person first = personnPair.getFirst();

                manPair.setFirst(new Man("", 18));
                System.out.println(height2Weight.getClass());
                System.out.println(nickName2Name.getClass());
                System.out.println(firstName2LastName.getClass());
                throwAs(new IOException());
            }
        }.start();
    }
}
