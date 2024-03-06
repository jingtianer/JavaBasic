import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NumberDemo {
    private final static Logger logger = Logger.getLogger(NumberDemo.class.getName());
    public static void main(String[] args) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String str = scanner.next();
            try {
                Number number = numberFormat.parse(str);
                if(number instanceof Integer n) {
                    System.out.println("Integer: " + ++n);
                } else if (number instanceof Double n) {
                    System.out.println("Double: " + ++n);
                } else if(number instanceof Long n) {
                    System.out.println("Long: " + ++n);
                } else if (number instanceof Byte n) {
                    System.out.println("Byte: " + ++n);
                } else if(number instanceof Float n) {
                    System.out.println("Float: " + ++n);
                } else if(number instanceof Short n) {
                    System.out.println("Short: " + ++n);
                }
            } catch (ParseException e) {
                logger.log(Level.SEVERE, e.toString());
            }
        }
    }
}
