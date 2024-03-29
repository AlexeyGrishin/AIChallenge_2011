package bot2.ai.areas.distribution;

import bot2.ai.areas.AreasPathHelper;
import bot2.ai.areas.FieldArea;
import bot2.ai.areas.FieldAreaComparators;
import bot2.ai.areas.FieldAreaKind;
import common.Compare;

import java.util.*;

public class RankedFieldArea implements DistributableArea {

    private AreaWrapper<FieldArea> wrapper;
    private FieldArea area;
    private AreasPathHelper pathHelper;
    private static Map<FieldAreaKind, Comparator<FieldArea>> comparators = new HashMap<FieldAreaKind, Comparator<FieldArea>>();
    private static Map<FieldAreaKind, Integer> priorities = new HashMap<FieldAreaKind, Integer>();
    private static Comparator<FieldArea> priorityComparator;
    private List<DistributableArea> nearestAreas = null;

    public RankedFieldArea(AreaWrapper<FieldArea> wrapper, FieldArea area, AreasPathHelper pathHelper) {
        this.wrapper = wrapper;
        this.area = area;
        this.pathHelper = pathHelper;
    }

    public Collection<DistributableArea> getNearestAreas() {
        if (nearestAreas == null) {
            nearestAreas = new ArrayList<DistributableArea>(4);
            for (FieldArea nearestArea: pathHelper.getNearestCells(area)) {
                if (pathHelper.isReachable(nearestArea)) {
                    nearestAreas.add(wrapper.wrap(nearestArea));
                }
            }
        }
        return nearestAreas;
    }

    public int getRequiredAmount(int defaultAmount) {
        return getRequiredAmount(defaultAmount, area);
    }

    protected int getRequiredAmount(int defaultAmount, FieldArea area) {
        switch (area.getKind()) {
            case ENEMY_HILL:
                return 999; //=)
            case JUST_ATTACKED:
                return 2;
            case JUST_VISITED:
                return 0;
            default:
                return 1;
        }
    }

    public int compareTo(DistributableArea anotherDArea) {
        FieldArea anotherArea = ((RankedFieldArea)anotherDArea ).area;
        Compare c = new Compare();
        return c.values(
                c.use(priorityComparator.compare(area, anotherArea))
                        || c.use(getKindComparator(area).compare(area, anotherArea))
        ).result();
    }

    public FieldArea getArea() {
        return area;
    }

    public String toString() {
        return area.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RankedFieldArea that = (RankedFieldArea) o;

        if (!area.equals(that.area)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return area.hashCode();
    }

    private static Comparator<FieldArea> getKindComparator(FieldArea area) {
        return comparators.get(area.getKind());
    }

    static {
        define(FieldAreaKind.NOT_VISITED, 0, FieldAreaComparators.NULL_COMPARATOR);
        define(FieldAreaKind.VISITED_TIME_AGO, 1, FieldAreaComparators.VISITED_TIME_COMPARATOR);
        define(FieldAreaKind.JUST_ATTACKED, 2, FieldAreaComparators.DISTANCE_COMPARATOR);
        define(FieldAreaKind.ENEMY_HILL, 3, FieldAreaComparators.NULL_COMPARATOR);
        define(FieldAreaKind.JUST_VISITED, Integer.MAX_VALUE, FieldAreaComparators.EMPTINESS_COMPARATOR);
    }

    static void define(FieldAreaKind kind, int priority, Comparator<FieldArea> comparator) {
        comparators.put(kind, comparator);
        priorities.put(kind, priority);
    }

    static {
        priorityComparator = new Comparator<FieldArea>() {
            public int compare(FieldArea o1, FieldArea o2) {
                Compare c = new Compare();
                return c.values(c.upIfSmaller(
                        priorities.get(o1.getKind()),
                        priorities.get(o2.getKind())
                )).result();
            }
        };
    }
}
