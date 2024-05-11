package reflecktion_another;

public class AnotherAnyClass {

    private int a;
    private int b;
    private Integer integerA;
    private Integer integerB;
    public AnotherAnyClass(int a, int b) {
        this.a = a;
        this.b = b;
        this.integerA  = a;
        this.integerB = b;
    }
    public AnotherAnyClass() {

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
        return "AnotherAnyClass{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
