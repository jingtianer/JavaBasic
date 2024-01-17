package io;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static common.KotlinUtils.*;

public class ZipDemo {
    public static class Pair<K, V> {
        public K key;

        @Override
        public String toString() {
            return "Pair{" +
                    "key=" + key +
                    ", val=" + val +
                    '}';
        }

        public V val;
        public Pair(K key, V val) {
            this.key = key;
            this.val = val;
        }
        public Pair() {
            this.key = null;
            this.val = null;
        }
    }
    static final private Logger logger = Logger.getLogger(ZipDemo.class.getName());
    static void compress(String outName, String[] files) throws IOException {
        File outputZipFile = new File(outName);
        if(outputZipFile.exists()) {
            outputZipFile.delete();
        }
        outputZipFile.createNewFile();
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outName))) {
            for (String entry : files) {
                File entryFile = new File(entry);
//                zip(zipOutputStream, entryFile, entryFile.getName());
                dfs(new Pair<>(entryFile, entryFile.getName()), (data)->{
                    var entryFile1 = data.key;
                    var base = data.val;
                    System.out.println(base);
                    System.out.println(entryFile1);
                    if(entryFile1.isDirectory()) {
                        if(!base.isEmpty()) {
                            zipOutputStream.putNextEntry(new ZipEntry(base + "/"));
                        }
                        return let(entryFile1.listFiles(), (subFileArr) ->
                                Arrays.stream(subFileArr).map((subFile) ->
                                    new Pair<>(subFile, base + "/" + subFile.getName())
                        ));
                    } else {
                        try (FileInputStream fileInputStream = new FileInputStream(entryFile1)) {
                            zipOutputStream.putNextEntry(new ZipEntry(base));
                            zipOutputStream.write(fileInputStream.readAllBytes());
                        }
                    }
                    return null;
                });
            }
        }
    }
    private static void zip(ZipOutputStream zipOutputStream, File file, String base) throws IOException {
        System.out.println(base);
        System.out.println(file);
        if(file.isDirectory()) {
            if(!base.isEmpty()) zipOutputStream.putNextEntry(new ZipEntry(base + "/"));
            with(file.listFiles(), (subFileArr) -> {
                Arrays.stream(subFileArr).forEach((subFile) -> {
                    try {
                        zip(zipOutputStream, subFile, base + "/" + subFile.getName());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                zipOutputStream.putNextEntry(new ZipEntry(base));
                zipOutputStream.write(fileInputStream.readAllBytes());
            }
        }
    }
    static void extract(String outName, String fileName) throws IOException {
        File outDir = new File(outName);
        if(outDir.exists()) {
            logger.log(Level.SEVERE, "outputDir already exists");
            System.exit(-1);
        } else {
            outDir.mkdir();
        }
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fileName));
            ZipFile zipfile = new ZipFile(fileName)
        ) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                System.out.println(zipEntry);
                File outputFile = new File(outName + "/" + zipEntry.getName());
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                if(zipEntry.isDirectory()) {
                    outputFile.mkdir();
                } else {
                    outputFile.createNewFile();
                    try (FileOutputStream  fileOutputStream = new FileOutputStream(outputFile)) {
                        fileOutputStream.write(zipfile.getInputStream(zipEntry).readAllBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }
    static void usage() {
        logger.log(Level.SEVERE,
                String.format("Usage:\nCompress:\n\tjava %s c outfile fileList\nExtract:\n\tjava %s x outputDir zipFile"
                , ZipDemo.class.getName()
                , ZipDemo.class.getName()));
    }
    static void errorExit(int code) {
        usage();
        System.exit(code);
    }
    public static void main(String[] args) throws IOException {
        if(args.length < 1) {
            errorExit(-1);
        }
        if(args[0].equals("c")) {
            if(args.length <= 2) {
                errorExit(-2);
            }
            compress(args[1], Arrays.stream(args).skip(2).toArray(String[]::new));
        } else if(args[0].equals("x")) {
            if(args.length != 3) {
                errorExit(-3);
            }
            extract(args[1], args[2]);
        } else {
            errorExit(-4);
        }
    }
}
