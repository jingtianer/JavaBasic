package classLoaderDemo;

class TestClass {
    String s;
    public TestClass(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return getClass() + ", s = " + s;
    }
}
public class ClassLoaderTestFile {
    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println(new TestClass(arg));
        }
    }
}
