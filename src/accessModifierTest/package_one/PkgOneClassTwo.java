package accessModifierTest.package_one;

public class PkgOneClassTwo extends PkgOneClassOne{
//    @Override
    public static void staticFunction() {
        System.out.println(PkgOneClassOne.class.getName() + ", public static void");
    }
    @Override
    public void pubSVFunction() {
        System.out.println(PkgOneClassTwo.class.getName() + ", public void");
        super.pubSVFunction();
    }
    @Override
    protected void proSVFunction() {
        System.out.println(PkgOneClassTwo.class.getName() + ", protected void");
        super.proSVFunction();
    }
    @Override
    void defSVFunction() {
        System.out.println(PkgOneClassTwo.class.getName() + ", default void");
        super.defSVFunction();
    }
//    @Override
    private void priSVFunction() {
        System.out.println(", private void");
    }
}
