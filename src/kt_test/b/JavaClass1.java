package kt_test.b;


public class JavaClass1 {
    public static void main(String[] args) {
        System.out.println(KtClass.Companion.getInstance());
        System.out.println(KtObj.INSTANCE);
        System.out.println(KtObj.Companion.getInstance());

    }
    public static void test(KtClass kt) {
        System.out.println(kt);
    }
}
