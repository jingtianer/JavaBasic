package accessModifierTest.package_two;

import accessModifierTest.package_one.PkgOneClassOne;

public class PkgTwoClassOne extends PkgOneClassOne {
    //    @Override
    public static void staticFunction() {
        System.out.println(PkgOneClassOne.class.getName() + ", public static void");
    }
    @Override
    public void pubSVFunction() {
        System.out.println(PkgTwoClassOne.class.getName() + ", public void");
        super.pubSVFunction();
    }
    @Override
    protected void proSVFunction() {
        System.out.println(PkgTwoClassOne.class.getName() + ", protected void");
        super.proSVFunction();
    }
//    @Override
    void defSVFunction() {
        System.out.println(PkgTwoClassOne.class.getName() + ", default void");
//        super.defSVFunction();
    }
//    @Override
    private void priSVFunction() {
        System.out.println(PkgTwoClassOne.class.getName() + ", private void");
//        super.priSVFunction();
    }
}
