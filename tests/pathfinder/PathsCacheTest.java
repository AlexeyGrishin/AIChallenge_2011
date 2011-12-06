package pathfinder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class PathsCacheTest {

    public static final int FIRST_POINT = 2;
    public static final int SECOND_POINT = 5;
    private @Mock SimplePathFinder<Integer> finder;
    private PathsCache<Integer> cache;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cache = new PathsCache<Integer>(finder);
        when(finder.find(any(Integer.class), any(Integer.class))).thenReturn(
                Arrays.<PathFinder.PathElement<Integer>>asList(
                        new PathFinder.PathElement<Integer>(
                            1, FIRST_POINT, 0, 0
                        ),
                        new PathFinder.PathElement<Integer>(
                            2, SECOND_POINT, 0, 0
                        )
                )
        );
    }

    @Test
    public void cachePath() {
        assertEquals(Arrays.asList(FIRST_POINT, SECOND_POINT), cache.getPath(2, 5));
        assertEquals(Arrays.asList(FIRST_POINT, SECOND_POINT), cache.getPath(2, 5));
        verify(finder, times(1)).find(2, 5);
    }

    @Test
    public void cacheBackPath() {
        assertEquals(Arrays.asList(FIRST_POINT, SECOND_POINT), cache.getPath(2, 5));
        assertEquals(Arrays.asList(SECOND_POINT, FIRST_POINT), cache.getPath(5, 2));
        verify(finder, times(1)).find(2, 5);
        verifyNoMoreInteractions(finder);
    }

    @Test
    public void resetCache() {
        assertEquals(Arrays.asList(FIRST_POINT, SECOND_POINT), cache.getPath(2, 5));
        cache.reset();
        assertEquals(Arrays.asList(FIRST_POINT, SECOND_POINT), cache.getPath(2, 5));
        verify(finder, times(2)).find(2, 5);

    }

}
