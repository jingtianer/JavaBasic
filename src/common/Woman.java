package common;

public class Woman extends Person {
    public int getCup() {
        return cup;
    }

    private int cup;
    public Woman(String info, int cup) {
        super(info);
        if(cup <= 0) throw new IllegalArgumentException("cup >= 0");
        this.cup = cup;

    }
}
