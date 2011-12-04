package util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Collection;

public class HasSize<T extends Collection> extends BaseMatcher<T> {
    private int expected;

    public HasSize(int expected) {
        this.expected = expected;
    }

    public boolean matches(Object o) {
        return ((Collection)o).size() == expected;
    }

    public void describeTo(Description description) {
        description.appendText("Collection expected to have size ").appendValue(expected);
    }

    public static <T extends Collection<?>> HasSize<T> hasSize(int expected) {
        return new HasSize<T>(expected);
    }
    public static <T extends Collection<?>> HasSize<T> empty() {
        return new HasSize<T>(0);
    }

}
