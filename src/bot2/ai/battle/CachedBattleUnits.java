package bot2.ai.battle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CachedBattleUnits<P> implements Iterable<List<BattleUnitStep<P>>>, Iterator<List<BattleUnitStep<P>>> {

    private List<List<BattleUnitStep<P>>> cachedSteps = new ArrayList<List<BattleUnitStep<P>>> ();
    private BattleUnits<P> units;
    private Iterator<List<BattleUnitStep<P>>> iterator;
    private boolean fromCache = false;


    public CachedBattleUnits(BattleUnits<P> units) {
        this.units = units;
        this.iterator = units.iterator();
    }

    public Iterator<List<BattleUnitStep<P>>> iterator() {
        return fromCache ? cachedSteps.iterator() : this;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public List<BattleUnitStep<P>> next() {
        List<BattleUnitStep<P>> next = iterator.next();
        cachedSteps.add(next);
        if (!iterator.hasNext()) {
            fromCache = true;
        }
        return next;
    }

    public void remove() {
        iterator.remove();
    }

    public List<BattleUnitStep<P>> currentSet() {
        return units.getCurrentSet();
    }
}
