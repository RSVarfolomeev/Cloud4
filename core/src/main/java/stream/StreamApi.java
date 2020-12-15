package stream;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamApi {

    public static void main(String[] args) throws URISyntaxException, IOException {

//        IntStream.range(1, 10)
//                .boxed()
//                .filter(x -> x % 2 == 0)
//                .map("a"::repeat)
//                .forEach(System.out::println);

        Random rnd = new Random();
        Optional<Integer> result = rnd.ints(1000, 10000)
                .limit(20)
                .boxed()
                .distinct()
                .filter(x -> x % 7 == 0 && x > 3000 && x % 3 != 0)
                .findFirst();

        result.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Value not present")
        );

        List<String> words = Files.lines(Path.of(StreamApi.class.getResource("data.txt").toURI()))
                .flatMap(line -> Stream.of(line.split(" +")))
                .filter(str -> !str.isBlank())
                .sorted()
                .distinct()
                .collect(Collectors.toList());
        System.out.println(words);

        TreeMap<String, Integer> map = Files.lines(Path.of(StreamApi.class.getResource("data.txt").toURI()))
                .flatMap(line -> Stream.of(line.split(" +")))
                .filter(str -> !str.isBlank())
                .collect(Collectors.toMap(Function.identity(), x -> 1, Integer::sum, TreeMap::new));

        System.out.println(map);
    }
}
