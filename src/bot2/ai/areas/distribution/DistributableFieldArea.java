package bot2.ai.areas.distribution;

import bot2.ai.areas.FieldArea;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DistributableFieldArea implements DistributableArea {

    private FieldArea area;

    public DistributableFieldArea(FieldArea area) {
        this.area = area;
    }

    public Collection<DistributableArea> getNearestAreas() {
        List<DistributableArea> nearAreas = new ArrayList<DistributableArea>(4);
        for (FieldArea area: this.area.getNearAreas()) {
            nearAreas.add(new DistributableFieldArea(area));
        }
        return nearAreas;
    }

    public int getRequiredAmount(int defaultAmount) {
        return defaultAmount;
    }

    public int compareTo(DistributableArea area) {//
        //TODO: comparator
        DistributableFieldArea darea = (DistributableFieldArea)area;
        int res = -(this.area.getStat().getVisitedTurnsAgo() - darea.area.getStat().getVisitedTurnsAgo());
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DistributableFieldArea that = (DistributableFieldArea) o;

        if (!area.equals(that.area)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return area.hashCode();
    }

    public FieldArea getArea() {
        return area;
    }
}
