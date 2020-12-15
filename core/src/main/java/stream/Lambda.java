package stream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lambda {

    public Lambda() {
        A a = System.out::println;
        // a.foo(b.call("123").toString());
    }

    public static void main(String[] args) {
        Consumer<String> consumer = System.out::println;
        List.of(1, 2, 3).forEach(((Consumer<Integer>) i -> {
            System.out.print(i * 2 + " ");
        }).andThen(i -> {
            System.out.println(i + 1);
        }));
        Predicate<Integer> predicate = (x -> x % 2 == 0);
        Function<String, Integer> foo = String::length;
        System.out.println(
                foo.andThen(x -> x * 2)
                        .andThen(x -> x + 1)
                        .apply("12314"));
        Supplier<ArrayList<Integer>> supplier = ArrayList::new;

        // System.out.println(foo.apply("123"));
        //consumer.andThen(System.out::println).accept("Hello");
    }

    @FunctionalInterface
    interface A {

        void foo(String arg);

        default void bar() {
            System.out.println("Hello world");
        }
    }

    @FunctionalInterface
    interface B<T extends CharSequence, R> {
        R call(T value);
    }

}
