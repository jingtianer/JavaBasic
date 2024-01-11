package common;

import java.util.Arrays;
import java.util.Objects;

public class Man extends Person {
    public int getPenisLength() {
        return penisLength;
    }

    public void setPenisLength(int penisLength) {
        this.penisLength = penisLength;
    }

    private int penisLength;

    public Man(String info, int penisLength) {
        super(info);
        this.penisLength = penisLength;
    }

    @Override
    public String toString() {
        return "Man{" +
                "penisLength=" + penisLength +
                ", Person=" + super.toString() +
                '}';
    }
}
