package reflektion;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

class MyObj {
    private int privateField;
    public int publicField;
    public MyObj(int pri, int pub) {
        this.privateField = pri;
        this.publicField = pub;
    }

    @Override
    public String toString() {
        return "MyObj{" +
                "privateField=" + privateField +
                ", publicField=" + publicField +
                '}';
    }
}

public class LookupDemo {
    public static Object getFieldValue(Object obj, String fieldName, MethodHandles.Lookup lookup)
            throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field field = clazz.getField(fieldName);
        VarHandle varHandle = MethodHandles.privateLookupIn(clazz, lookup).unreflectVarHandle(field);
        return varHandle.get(obj);
    }

    public static void main(String[] args) throws IllegalAccessException {
        MyObj myObj = new MyObj(1, 2);
        System.out.println(myObj);
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            System.out.println(lookup.hasFullPrivilegeAccess());
            Field field = MyObj.class.getField("privateField");
            field.setAccessible(true);
            System.out.println("privateField="+field.get(myObj));
            System.out.println("privateField=" + getFieldValue(myObj, "privateField", lookup));
        } catch (NoSuchFieldException|IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("publicField=" + getFieldValue(myObj, "publicField", lookup));
        } catch (NoSuchFieldException|IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
