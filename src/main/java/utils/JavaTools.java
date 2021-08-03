package utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaTools {
    @SafeVarargs
    public static  <T> List<T> concatLists(List<T>... lists) {
        return Stream.of(lists).flatMap(List::stream).collect(Collectors.toList());
    }

    public static String mergeStrings(String... strings) {
        return String.join("", strings);
    }
}
