package usr.cxh.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtils {
    public static <T, U> List<T> map(final Collection<U> c, final Function<U, T> f) {
        return c.stream().map(f).collect(Collectors.toList());
    }
}
