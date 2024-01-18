package stream;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static common.KotlinUtils.dfs;

public class StreamDemo {
    final static Logger logger = Logger.getLogger(StreamDemo.class.getName());
    public static void main(String[] args) {
        if(args.length < 2) return;
        Path path = Path.of(args[0]);
        Random random = new Random();
        var randomStream = Stream.generate(random::nextDouble);
        var sequenceStream = Stream.iterate(BigInteger.ONE, BigInteger.ONE::add);
        var sequenceIte = sequenceStream.iterator();
        var randomIte = randomStream.iterator();
        dfs(Paths.get(args[1]), dir->{
            if(dir == null) {
                var rootDir = FileSystems.getDefault().getRootDirectories();
                return StreamSupport.stream(rootDir.spliterator(), true);
            } else {
                System.out.println(dir);
                var files = dir.toFile().listFiles();
                if(files != null) {
                    return StreamSupport.stream(
                            Arrays.stream(files).map(File::toPath).spliterator(), true);
                }
            }
            return null;
        });

        try (var lines = Files.lines(path)) {
            lines.forEach(s -> {
                String out = String.format("w:%s,%s:\n%s\n", randomIte.next(), sequenceIte.next().toString(), s);
                System.out.println(out);
            });
        } catch (IOException e) {
            logger.warning(e.toString());
        }
    }
}
