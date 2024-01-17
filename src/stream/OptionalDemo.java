package stream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OptionalDemo {
    private static final Logger logger = Logger.getLogger(OptionalDemo.class.getName());
    public static void main(String[] args) {
        Path path = Paths.get(args[0]);
        try (var lines = Files.lines(path)) {
            var lists = lines.filter((s)->s.contains("logger"))
                    .peek(System.out::println)
                    .toList();
            var line = lists.parallelStream().findAny();
            line.flatMap(s -> s.isEmpty() ? Optional.empty() : Optional.of(1.0/s.length()))
                    .ifPresentOrElse(s->{
                        System.out.printf("find, %s\n", s);
                    }, ()->{
                        System.out.println("not find");
                    });
        } catch (IOException e) {
            logger.warning(e.toString());
        }
        try (var lines = Files.lines(path)) {
            var summary = lines.collect(Collectors.summarizingInt(String::length));
            System.out.println(summary);
        } catch (IOException e) {
            logger.warning(e.toString());
        }

        try (var lines = Files.lines(path)) {
            var map = lines
                    .collect(Collectors.toMap(
                    String::length,
                    Function.identity(),
                    (o, n) -> String.format("%s\n%s", o, n), // key冲突
                    TreeMap::new
            ));
            map.forEach((k, v) -> System.out.println(k + ", " + v));
        } catch (IOException e) {
            logger.warning(e.toString());
        }
    }
}
