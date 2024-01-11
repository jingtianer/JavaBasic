package common;


import java.util.Objects;

public class Person {
    public static interface Aging {
        int incr();

        default void print(Person person) {
            System.out.printf("aging: %s\n", person);
        }
    }
    private int age;
    private String firstName;
    private int height;
    private String LastName;
    public Person(String info) {
        Objects.requireNonNull(info);
        if(!info.contains(" ")) throw new Error("<First-Name> <Last-Name>* <height> <age>");
        String[] splitInfo = info.split(" ");
        if(splitInfo.length < 3) throw new Error("<First-Name> <Last-Name>* <height> <age>");
        int index = 0;
        this.firstName = splitInfo[index++];
        this.LastName = splitInfo.length == 3 ? null : splitInfo[index++];
        this.height = Integer.parseInt(splitInfo[index++]);
        this.age = Integer.parseInt(splitInfo[index++]);
    }

    public int getAge() {
        return age;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getHeight() {
        return height;
    }

    public String getLastName() {
        return LastName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", height=" + height +
                ", firstName='" + firstName + '\'' +
                ((LastName == null) ? "" : ", LastName='" + LastName + '\'') +
                '}';
    }

    public void aging(Aging aging) {
        aging.print(this);
        this.age += aging.incr();
    }
}