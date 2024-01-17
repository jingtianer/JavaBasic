package common;

import multithreading.FutureDemo;

import java.util.function.Function;
import java.util.stream.Stream;

public class KotlinUtils {

    public interface WithInterface<T> {
        void call(T obj) throws Exception;
    }

    public interface LetInterface<T, U> {
        U call(T obj) throws Exception;
    }
    public static <T> T with(T obj, WithInterface<T> callable) {
        if(obj != null) {
            try {
                callable.call(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return obj;
    }

    public static <T, U> U let(T obj, LetInterface<T, U> callable) {
        if(obj != null) {
            try {
                return callable.call(obj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }


    public static <T> void dfs(T val, LetInterface<T, Stream<T>> f) {
        try {
            with(f.call(val), (stream) -> {
                stream.forEach(sub -> {
//                    System.out.println(sub);
                    dfs(sub, f);
                });
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
