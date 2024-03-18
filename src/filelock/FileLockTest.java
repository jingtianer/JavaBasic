package filelock;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileLockTest {
    public static void main(String[] args) {
        if(args.length < 5) {
            System.out.println("Usage: fileName lockStart lockEnd share sleep");
            System.exit(-1);
        }
        boolean share = Boolean.parseBoolean(args[3]);
        long sleep = Long.parseLong(args[4]);
        System.out.println(Arrays.toString(args));
        try (FileChannel channel = share ?
                        FileChannel.open(Path.of(args[0]), StandardOpenOption.READ) :
                        FileChannel.open(Path.of(args[0]), StandardOpenOption.READ, StandardOpenOption.WRITE);) {
            long fileSize = channel.size();
            long lockStart = Math.max(0, Long.parseLong(args[1]));
            long lockEnd = Math.min(fileSize, Long.parseLong(args[2]));
            System.out.println("lock!");
            FileLock lock = channel.lock(lockStart, lockEnd, share);
            System.out.println(lock);
            Thread.sleep(sleep*1000);
            lock.release();
            System.out.println("release!");
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }
    }
}
