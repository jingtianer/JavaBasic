package lambdaDemo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import common.Person;




public class FunctionalInterfaceTest {
    public static String randomName(Random random) {
        StringBuilder stringBuilder = new StringBuilder();
        int length = random.nextInt(10, 20);
        while (--length >= 0) {
            stringBuilder.append((char) (random.nextInt(26) + 'a'));
        }
        return stringBuilder.toString();
    }
    public static void main(String[] args) {
        Random random = new Random();
        String[] personInfoArray = new String[100];
        personInfoArray = Arrays.stream(personInfoArray).map((str)->
                random.nextInt(10) < 2
                        ? String.format("%s %s %d %d", randomName(random), randomName(random), random.nextInt(300), random.nextInt(100))
                        : String.format("%s %s %d", randomName(random), random.nextInt(300), random.nextInt(100))
        ).toArray(String[]::new);
        Arrays.stream(personInfoArray).forEach(System.out::println);
        Person[] personArray =  Arrays
                .stream(personInfoArray)
                .map(Person::new) // 自动选择合适的
                .toArray(Person[]::new);
        System.out.println("Before Sort:");
        Arrays.stream(personArray).forEach(System.out::println);
        Arrays.sort(personArray,
                Comparator
                        .comparing(Person::getAge)
                        .thenComparing(Person::getLastName, Comparator.nullsLast(Comparator.naturalOrder())) // null的排在后面，且避免报错
                        .thenComparing(Person::getFirstName)
        );
        System.out.println("After Sort:");
        Arrays.stream(personArray).forEach(System.out::println);
        System.out.println("Elderly People:");
        Arrays.stream(personArray).filter(person -> person.getAge() > 60).forEach(System.out::println);
        Arrays.stream(personArray).forEach(person -> person.aging(()->2));
        System.out.println("Elderly People 2 years later:");
        Arrays.stream(personArray).filter(person -> person.getAge() > 60).forEach(System.out::println);
    }
}
