package leetcode;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Tools {
    static Runnable runCatching(ExceptionRunnable r) {
        return () -> {try {r.run();} catch (Exception e) { e.printStackTrace(); }};
    }
    static public String printf(String format, Object ... args) {
        ByteArrayOutputStream byteArrayOutputStream;
        PrintStream printStream = new PrintStream((byteArrayOutputStream = new ByteArrayOutputStream()));
        printStream.printf(format, args);
        return byteArrayOutputStream.toString();
    }
    interface ExceptionRunnable {
        void run() throws Exception;
    }
}
