package innerClass;

public class InnerClassDemo {
    private  int aaa;
    private class InnerClass {
        private int aaa;
        void fun() {
            InnerClassDemo.this.aaa++;
            aaa++;
        }
    }
    private static class InnerStaticClass {
        private int aaa;

    }

    public InnerClass newInnerClass() {
        return new InnerClass();
    }
    public InnerStaticClass newInnerStaticClass() {
        return new InnerStaticClass();
    }

    public static void main(String[] args) {

    }
}
