package bot2.ai;

import bot2.GameSettings;

import java.io.File;

import static org.mockito.Mockito.when;

public class RealMaps77Test extends RealMapsTest {
    public RealMaps77Test(File mapFile) {
        super(mapFile);
    }

    protected void setupRanges(GameSettings settings) {
        when(settings.getViewRadius()).thenReturn(8);
        when(settings.getViewRadius2()).thenReturn(77);
    }


}
