package reflektion;

import reflecktion_another.AnotherAnyClass;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

class AnyClass {
    private int a;
    private int b;
    private Integer integerA;
    private Integer integerB;
    public AnyClass(int a, int b) {
        this.a = a;
        this.b = b;
        this.integerA  = a;
        this.integerB = b;
    }
    public AnyClass() {

    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public void setA(int a) {
        this.a = a;
        this.integerA = a;
    }

    public void setB(int b) {
        this.b = b;
        this.integerB = b;
    }

    public void setIntegerA(Integer integerA) {
        this.integerA = integerA;
        this.a = integerA;
    }

    public void setIntegerB(Integer integerB) {
        this.integerB = integerB;
        this.b = integerB;
    }

    @Override
    public String toString() {
        return "AnyClass{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}

public class ReflectDemo {
    public static void main(String[] args) {
        try {
            AnyClass anyClass = AnyClass.class.getConstructor(new Class[0]).newInstance(new Object[0]);
            System.out.println(anyClass);
            AnyClass anyClass1 = AnyClass.class.getConstructor(int.class, int.class).newInstance(1, 2);
            System.out.println(anyClass1);
            System.out.println(int.class);
            System.out.println(Integer.class);
            try {
                System.out.println(int.class.getConstructor(new Class[0]).newInstance(new Object[0]));
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            System.out.println(Integer.class.getConstructor(int.class).newInstance(1));
            AnyClass anyClass2 = AnyClass.class.getConstructor().newInstance();
            System.out.println(anyClass2);
            Class<AnyClass> anyClazz = AnyClass.class;
            Field fieldA = anyClazz.getDeclaredField("a");
            fieldA.setAccessible(true);
            fieldA.set(anyClass, 1);
            System.out.println(anyClass);

            Class<AnotherAnyClass> anotherAnyClassClass = AnotherAnyClass.class;
            AnotherAnyClass anotherAnyClass = anotherAnyClassClass.getConstructor(int.class, int.class).newInstance(2, 4);
            System.out.println(anotherAnyClass);
            Field fieldAOfAnotherClass = anotherAnyClassClass.getDeclaredField("a");
            fieldAOfAnotherClass.setAccessible(true);
            fieldAOfAnotherClass.set(anotherAnyClass, 8);
            System.out.println(anotherAnyClass);
        } catch (NoSuchFieldException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace(System.out);
        }
    }
}