package accessModifierTest.package_one;

public class PkgOneClassOne {
    public static void staticFunction() {
        System.out.println(PkgOneClassOne.class.getName() + ", public static void");
    }
    public void pubSVFunction() {
        System.out.println(PkgOneClassOne.class.getName() + ", public void");
    }
    protected void proSVFunction() {
        System.out.println(PkgOneClassOne.class.getName() + ", protected void");
    }
    void defSVFunction() {
        System.out.println(PkgOneClassOne.class.getName() + ", default void");
    }
    private void priSVFunction() {
        System.out.println(PkgOneClassOne.class.getName() + ", private void");
    }
}
