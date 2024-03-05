package classLoaderDemo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassLoaderTest {
    public static void main(String[] args) {
        ClassLoaderDemo
                .main(new String[]{
                        "1234561234560000",
                        "-c",
                        "/Users/jingtian/IdeaProjects/java/JavaBasic/src/classLoaderDemo/ClassLoaderTestFile.java"
                });
        ClassLoaderDemo
                .main(new String[]{
                        "1234561234560000",
                        "-f",
                        "/Users/jingtian/IdeaProjects/java/JavaBasic/src/classLoaderDemo/TestClass.class"
                });
        try {
            try(var fileStream = Files.walk(Path.of("/Users/jingtian/IdeaProjects/java/JavaBasic/src/classLoaderDemo/"), 1)) {
                fileStream.forEach(classFile->{
                    try {
                        if (classFile.getFileName().toString().endsWith(".class")) {
                            Files.delete(classFile);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            try (var fileStream = Files.walk(Path.of("/Users/jingtian/IdeaProjects/java/JavaBasic/target/classes/classLoaderDemo"))) {
                fileStream.filter((classFile)-> {
                    return switch (classFile.getFileName().toString()) {
                        case "ClassLoaderTestFile.class", "TestClass.class" -> true;
                        default -> false;
                    };
                }).forEach(classFile-> {
                    try {
                        Files.delete(classFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ClassLoaderDemo.main(new String[]{
                "1234561234560000",
                "-l",
                "/Users/jingtian/IdeaProjects/java/JavaBasic/src/",
                "classLoaderDemo.ClassLoaderTestFile",
                "a", "b", "c"
        });

        try(var fileStream = Files.walk(Path.of("/Users/jingtian/IdeaProjects/java/JavaBasic/src/classLoaderDemo/"), 1)) {
            fileStream.forEach(classFile->{
                try {
                    if (classFile.getFileName().toString().endsWith(".ecb_class")) {
                        Files.delete(classFile);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
