package bot2.ai.areas;

import common.Compare;

import java.util.Comparator;

public class FieldAreaComparators {
    public static Comparator<FieldArea> DEFAULT_COMPARATOR = new Comparator<FieldArea>() {
        public int compare(FieldArea o1, FieldArea o2) {
            return o1.hashCode() - o2.hashCode();
        }
    };
    public static Comparator<FieldArea> NULL_COMPARATOR = new Comparator<FieldArea>() {
        public int compare(FieldArea o1, FieldArea o2) {
            return 0;
        }
    };
    public static final Comparator<FieldArea> DISTANCE_COMPARATOR = new Comparator<FieldArea>() {
        public int compare(FieldArea o1, FieldArea o2) {
            Compare c = new Compare();
            return c.values(c.upIfSmaller(o1.getDistanceToHill(), o2.getDistanceToHill())).result();
        }
    };
    public static final Comparator<FieldArea> VISITED_TIME_COMPARATOR = new Comparator<FieldArea>() {
        public int compare(FieldArea o1, FieldArea o2) {
            Compare c = new Compare();
            return c.values(c.upIfLarger(o1.getStat().getVisitRank(), o2.getStat().getVisitRank())).result();
        }
    };
    public static final Comparator<FieldArea> REVERSE_DISTANCE_COMPARATOR = new Comparator<FieldArea>() {
        public int compare(FieldArea o1, FieldArea o2) {
            Compare c = new Compare();
            return c.values(c.upIfLarger(o1.getDistanceToHill(), o2.getDistanceToHill())).result();
        }
    };
    public static final Comparator<FieldArea> EMPTINESS_COMPARATOR = new Comparator<FieldArea>() {
        public int compare(FieldArea o1, FieldArea o2) {
            Compare c = new Compare();
            return c.values(c.upIfLarger(o1.getDistanceToHill(), o2.getDistanceToHill())
                    || c.upIfSmaller(o1.getStat().getAlies(), o2.getStat().getAlies())).result();
        }
    };
}
