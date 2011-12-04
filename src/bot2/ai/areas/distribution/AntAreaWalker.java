package bot2.ai.areas.distribution;

import bot2.ai.Ant;
import bot2.ai.areas.AreaHelper;
import bot2.ai.areas.Areas;
import bot2.ai.areas.AreasMapper;
import bot2.ai.areas.FieldArea;

import java.util.*;

public class AntAreaWalker implements AreaWalker {

    private Ant ant;
    private FieldArea location;
    private AreaWrapper<FieldArea> wrapper;
    private Set<FieldArea> destinationAreas;

    public AntAreaWalker(Ant ant, AreaWrapper<FieldArea> wrapper, AreasMapper mapper) {
        this.ant = ant;
        this.wrapper = wrapper;
        destinationAreas = new HashSet<FieldArea> (3);
        if (ant.getTargetArea() != null) {
            destinationAreas.add(ant.getTargetArea());
        }
        if (ant.isBusy()) {
            destinationAreas.add(mapper.get(ant.getTargetPoint()));
        }
        this.location = mapper.get(ant.getLocation());
        if (this.location != null)
            destinationAreas.add(this.location);
    }

    public Collection<DistributableArea> getDestinationAreas() {
        return wrapper.wrap(destinationAreas);
    }

    public DistributableArea getLocation() {
        return wrapper.wrap(location);
    }

    public boolean isInMove() {
        return ant.isBusy();
    }

    public void moveTo(DistributableArea area) {
        ant.doReachArea(wrapper.unwrap(area));
    }

    public void setWrapper(AreaWrapper<FieldArea> wrapper) {
        this.wrapper = wrapper;
    }

    public String toString() {
        return ant.toString() + " is on " + location.getNumber() + ", going to " + destinationAreas;
    }
}
