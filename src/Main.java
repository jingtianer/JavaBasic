// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
class StaticCanCallFromObj {
    public static void staticFunction() {
        System.out.println("im a static function");
    }
    public void memberFunction() {
        System.out.println("im a member function");
    }
}
public class Main {
    public static void main(String[] args) {
        StaticCanCallFromObj staticCanCallFromObj = new StaticCanCallFromObj();
        staticCanCallFromObj.memberFunction();
        staticCanCallFromObj.staticFunction();
    }
}