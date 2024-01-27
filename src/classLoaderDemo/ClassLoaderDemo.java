package classLoaderDemo;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static classLoaderDemo.ECBUtils.decryption;
import static classLoaderDemo.ECBUtils.encryption;

class ECBUtils {
    static final String transformation = "AES/ECB/PKCS5Padding";
    static final String ALGORITHM = "AES";

    public static byte[] decryption(byte[] code, String key) {
        try {
            Cipher cipher = Cipher.getInstance(transformation);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return cipher.doFinal(code);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
    public static byte[] encryption(String path, String key) {
        Path byteCodePath = Path.of(path);
        try(var input = new FileInputStream(byteCodePath.toFile())) {
            byte[] bytecode = input.readAllBytes();
            Cipher cipher = Cipher.getInstance(transformation);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(bytecode);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}

class ECBClassloader extends ClassLoader {
    String key;
    List<String> searchPath;
    public ECBClassloader(String key) {
        this.key = key;
        searchPath = new ArrayList<>(20);
    }
    public ECBClassloader addSearchPath(String path) {
        searchPath.add(path);
        return this;
    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            Path clazzFile = searchPath.stream()
                    .map(path->Path.of(path, name.replace(".", "/") + ".ecb_class"))
                    .filter(file->{
                        System.out.println(file);
                        return Files.exists(file);
                    })
                    .findAny()
                    .orElseThrow(()->new RuntimeException(new ClassNotFoundException()));
            byte[] bytes = Files.readAllBytes(clazzFile);
            bytes = decryption(bytes, key);
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class ClassLoaderDemo {
    static final Logger logger = Logger.getLogger(ClassLoaderDemo.class.getName());
    public static void exit() {
        logger.log(Level.SEVERE, String.format(
                "Usage: \n\tjava %s secretKey -f fileName...\n\tjava %s secretKey -s string\n\tjava %s secretKey -l searchPath className args...",
                ClassLoaderDemo.class.getName(),
                ClassLoaderDemo.class.getName()
        ));
        System.exit(-1);
    }
    public static void encryptClassFile(String[] files, String key) {
        Arrays.stream(files)
                .map(f->f.replace(".java", ".class"))
                .forEach(f->{
                    try {
                        Files.write(Path.of(f.replace(".class", ".ecb_class")), encryption(f, key),
                                StandardOpenOption.CREATE,
                                StandardOpenOption.WRITE,
                                StandardOpenOption.TRUNCATE_EXISTING);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
    public static void compileFiles(String[] files) {
        if(files.length < 1) exit();
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        int result = javaCompiler.run(
                null, null, null,
                files
        );
        if(result == 0) {

        } else {
            System.exit(-2);
        }
    }
    public static void loadClass(String clazzName, String searchPath, String key, Object[] args) {
        ClassLoader classLoader = new ECBClassloader(key)
                .addSearchPath(searchPath);
        try {
            Class<?> clazz = classLoader.loadClass(clazzName);
            clazz.getMethod("main", String[].class).invoke(null, args);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        if(args.length < 2) exit();
        switch (args[1]) {
            case "-c":
                compileFiles(Arrays.stream(args).skip(2).toArray(String[]::new));
            case "-f":
                encryptClassFile(Arrays.stream(args).skip(2).toArray(String[]::new), args[0]);
                break;
            case "-l":
                if(args.length < 4) exit();
                loadClass(args[3], args[2], args[0],
                        new Object[]{Arrays.stream(args).skip(4).toArray(String[]::new)});
                break;
            default:
                exit();
                break;
        }
    }
}