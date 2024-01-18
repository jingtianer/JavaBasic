package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;

public class FilePathDemo {
    final static private Logger logger = Logger.getLogger(FilePathDemo.class.getName());
    static private void usage() {
        logger.log(Level.SEVERE,
                String.format("\nUsage:\njava %s delete path...\n", FilePathDemo.class.getName()) +
                        String.format("java %s list path\n", FilePathDemo.class.getName()) +
                        String.format("java %s tree path\n", FilePathDemo.class.getName()) +
                        String.format("java %s showzip zipfile\n", FilePathDemo.class.getName()) +
                    String.format("java %s copy source target\n", FilePathDemo.class.getName())
        );
    }
    public static boolean isPosix() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("nix") || osName.contains("mac");
    }

    public static String getAttrStr(Path path) {
        StringBuilder regAttr = new StringBuilder();
        if(Files.isDirectory(path)) {
            regAttr.append("D");
        }
        if(Files.isRegularFile(path)) {
            regAttr.append("F");
        }
        if(Files.isExecutable(path)) {
            regAttr.append("X");
        }
        try {
            if(Files.isHidden(path)) {
                regAttr.append("H");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        if(Files.isReadable(path)) {
            regAttr.append("R");
        }
        if(Files.isWritable(path)) {
            regAttr.append("W");
        }
        if(Files.isSymbolicLink(path)) {
            regAttr.append("S");
        }
        return regAttr.toString();
    }

    public static String getSysAttrStr(Path path) {
        StringBuilder attrs = new StringBuilder();
        BasicFileAttributes basicFileAttributes = null;
        try {
            if(isPosix()) {
                PosixFileAttributes attributes = Files.readAttributes(path, PosixFileAttributes.class);
                basicFileAttributes = attributes;
                var permissionEnumSet = attributes.permissions();
                EnumSet<PosixFilePermission> allPermissionEnumSet = EnumSet.allOf(PosixFilePermission.class);
                StringBuilder permissionStr = new StringBuilder();
                for(var permission : allPermissionEnumSet) {
                    if(permissionEnumSet.contains(permission)) {
                        switch (permission.toString().split("_")[1]) {
                            case "READ":
                                permissionStr.append("r");
                            case "WRITE":
                                permissionStr.append("w");
                                break;
                            case "EXECUTE":
                                permissionStr.append("x");
                                break;
                            default:
                                throw new RuntimeException(String.format("\"unknown POSIX permission enum: %s\"", permission));
                        }
                    } else {
                        permissionStr.append("_");
                    }
                }
                attrs.append(String.format("posix_permission: %s\n", permissionStr));
                attrs.append(String.format("posix_owner: %s\n", attributes.owner()));
                attrs.append(String.format("posix_group: %s\n", attributes.group()));

            } else {
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                basicFileAttributes = attributes;
            }
            attrs.append(String.format("create: %s\n", basicFileAttributes.creationTime()));
            attrs.append(String.format("access: %s\n", basicFileAttributes.lastAccessTime()));
            attrs.append(String.format("modify: %s\n", basicFileAttributes.lastModifiedTime()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        return attrs.toString();
    }

    public static void pathInfoStr(Path path) {
        String regAttr = getAttrStr(path);
        long size = -1;
        try {
            size = Files.size(path);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
        }
        String systemAttrString = getSysAttrStr(path);
        System.out.printf("File: %s\n", path);
        System.out.printf("Attr: %s\n", regAttr);
        System.out.printf("size: %s\n", size);
        System.out.printf(isPosix() ? "Posix" : "Base" + " Attr: \n\t%s\n", systemAttrString.replace("\n", "\n\t"));
    }
    public static void list(String path) {
        Path listPath = Paths.get(path);
        try (Stream<Path> pathStream = Files.list(listPath)){
            pathStream.forEach(FilePathDemo::pathInfoStr);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
            System.exit(-2);
        }
    }
    public static void showZip(String path) {
        Path listPath = Paths.get(path);
        try (FileSystem fileSystem = FileSystems.newFileSystem(listPath)) {
            Files.walkFileTree(fileSystem.getPath("/"), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println(file);
                    try (var fout = fileSystem.provider().newInputStream(file)) {
                        System.out.println(Arrays.toString(fout.readAllBytes()));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
            throw new RuntimeException(e);
        }
    }
    public static void tree(String path) {
        Path listPath = Paths.get(path);
        try (Stream<Path> pathStream = Files.walk(listPath)){
            pathStream.forEach(FilePathDemo::pathInfoStr);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
            System.exit(-2);
        }
    }
    public static void copy(String src, String dest) {

        Path target = Paths.get(dest);
        Path source = Paths.get(src);
        try (Stream<Path> pathStream = Files.walk(source)){
            pathStream.forEach(path -> {
                Path toPath = target.resolve(source.relativize(path));
                System.out.println(toPath);
                try {
                    if(Files.isDirectory(path)) {
                        Files.createDirectories(toPath);
                    } else {
                        Files.copy(path, toPath);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString());
            System.exit(-2);
        }
    }
    public static void delete(String[] files) {
        Arrays.stream(files).forEach((file) -> {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(Paths.get(file))) {
                entries.forEach(System.out::println);
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.toString());
            }
        });
        Arrays.stream(files).forEach((file) -> {
            try {
                Files.walkFileTree(Paths.get(file), new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        System.out.println("dir" + file);
                        try {
                            if(Files.isWritable(file)) Files.delete(file);
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, e.toString());
                            throw e;
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        System.out.println("dir" + dir);
                        if(exc != null) {
                            logger.log(Level.SEVERE, exc.toString());
                            throw exc;
                        }
                        try (var subFiles = Files.list(dir)) {

                            if(Files.isWritable(dir) && subFiles.findAny().isEmpty()) {
                                Files.delete(dir);
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.toString());
            }
        });
    }
    public static void main(String[] args) {
        if(args.length < 1) {
            usage();
            System.exit(-1);
        }
        switch (args[0]) {
            case "delete" -> {
                if (args.length < 2) {
                    usage();
                    System.exit(-1);
                }
                delete(Arrays.stream(args).skip(1).toArray(String[]::new));
            }
            case "copy" -> {
                if (args.length != 3) {
                    usage();
                    System.exit(-1);
                }
                copy(args[1], args[2]);
            }
            case "tree" -> {
                if (args.length != 2) {
                    usage();
                    System.exit(-1);
                }
                tree(args[1]);
            }
            case "list" -> {
                if (args.length != 2) {
                    usage();
                    System.exit(-1);
                }
                list(args[1]);
            }
            case "showzip" -> {
                if (args.length != 2) {
                    usage();
                    System.exit(-1);
                }
                showZip(args[1]);
            }
            default -> {
                usage();
                System.exit(-1);
            }
        }
    }
}
