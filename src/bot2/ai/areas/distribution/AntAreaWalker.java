package bot2.ai.areas.distribution;

import bot2.ai.Ant;
import bot2.ai.areas.Areas;
import bot2.ai.areas.FieldArea;

public class AntAreaWalker implements AreaWalker {

    private Ant ant;
    private FieldArea area;
    private AreaWrapper<FieldArea> wrapper;

    public AntAreaWalker(Ant ant, FieldArea area, AreaWrapper<FieldArea> wrapper) {
        this.ant = ant;
        this.area = area;
        this.wrapper = wrapper;
    }

    public DistributableArea getDestinationArea() {
        return wrapper.wrap(area);
    }

    public boolean isInMove() {
        return ant.isBusy();
    }

    public void moveTo(DistributableArea area) {
        ant.doReachArea(wrapper.unwrap(area));
    }

    public String toString() {
        return ant.toString();
    }
}
