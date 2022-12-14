package aoc.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Readers {

    private Readers() {
    }

    public static CharSequence toCharSequence(IntStream stream) {
        return stream.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
    }

    public static CharSequence toCharSequence(Stream<Character> stream) {
        return stream.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
    }

    public static CharSequence toCharSequence(String string) {
        return toCharSequence(string.chars());
    }

    /**
     * Id reader.
     * ID
     */
    public static final LineReader<String> id = Optional::ofNullable;

    /**
     * Integer reader.
     * INT
     */
    public static final LineReader<Integer> integer = l -> {
        try {
            return Optional.of(Integer.parseInt(l));
        } catch (Exception x) {
            return Optional.empty();
        }
    };

    /**
     * Split line reader (version that splits in 2)
     * V ::= <T>:t REGEX <U>:u {f(t,u)}
     */
    public static <T, U, V> LineReader<V> split(String regex, LineReader<T> x,
                                                LineReader<U> y, BiFunction<T, U, V> f) {
        return l -> {
            if (regex == null || x == null || y == null || f == null || l == null)
                return Optional.empty();
            String[] parts = l.split(regex);
            if (parts.length == 2) {
                Optional<T> t = x.apply(parts[0]);
                Optional<U> u = y.apply(parts[1]);
                if (t.isPresent() && u.isPresent()) {
                    return Optional.ofNullable(f.apply(t.get(), u.get()));
                }
            }
            return Optional.empty();
        };
    }

    /**
     * Split line reader (version that splits in n)
     * T ::= t1 REGEX t2 REGEX ... REGEX tn {f(t1,t2,...,tn)}
     */
    public static <T> LineReader<T> splitN(String regex, ListCreator<T> f) {
        return l -> {
            if (regex == null || f == null || l == null)
                return Optional.empty();
            return f.fromList(List.of(l.split(regex)));
        };
    }

    /**
     * Regex reader.
     * V ::= REGEX with groups g1 ... gn in a structure t:T, {f(t)}
     */
    public static <T> LineReader<T> regex(String regex, ListCreator<T> f) {
        return l -> {
            if (regex == null || regex.equals("") || f == null) {
                return Optional.empty();
            }
            try {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(l);
                if (matcher.matches()) {
                    List<String> groups = new ArrayList<>();
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        groups.add(matcher.group(i));
                    }
                    return f.fromList(groups);
                } else {
                    return Optional.empty();
                }
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

    public static <T> LineReader<T> findAll(String regex, ListCreator<T> f) {
        return l -> {
            if (regex == null || regex.equals("") || f == null) {
                return Optional.empty();
            }
            try {
                List<String> instances = new ArrayList<>();
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(l);
                while(matcher.find()) {
                    instances.add(matcher.group());
                }
                return f.fromList(instances);
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

    public static FileReader<Pair<List<String>, List<String>>> splitUntil(Predicate<String> p, boolean mandatory,
                                                                          boolean keep) {
        return ls -> {
            if (p == null || ls == null)
                return Optional.empty();
            List<String> before = new ArrayList<>();
            List<String> after = new ArrayList<>();
            int i = 0;
            boolean found = false;
            while (i < ls.size()) {
                if (p.test(ls.get(i))) {
                    found = true;
                    if (keep) {
                        before.add(ls.get(i));
                    }
                    i++;
                    break;
                }
                before.add(ls.get(i));
                i++;
            }
            while (i < ls.size()) {
                after.add(ls.get(i));
                i++;
            }
            if (mandatory && !found) {
                return Optional.empty();
            } else {
                return Optional.of(Pair.of(before, after));
            }
        };
    }

}
