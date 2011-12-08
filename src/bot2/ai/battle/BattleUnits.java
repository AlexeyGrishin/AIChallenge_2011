package bot2.ai.battle;

import java.util.*;

public class BattleUnits<P> implements Iterable<List<BattleUnitStep<P>>> {

    private List<BattleUnit<P>> units;
    private List<LinkedList<Integer>> steps;
    private List<BattleUnitStep<P>> currentSet;
    private boolean lastSet = false;

    public BattleUnits(List<BattleUnit<P>> units, Collection<P> underAttack) {
        this.units = new ArrayList<BattleUnit<P>>(units);
        this.steps = new ArrayList<LinkedList<Integer>>(units.size());
        for (BattleUnit<P> unit: units) {
            LinkedList<Integer> unitSteps = new LinkedList<Integer>();
            boolean oneNotUnderAttackFound = false;
            int ordinalNr = 0;
            for (BattleUnitStep<P> step: unit.getPossibleSteps()) {
                boolean thisStepUnderAttack = underAttack.contains(step.getPoint());
                if (step.isStand() || underAttack.contains(step.getPoint()) || (!thisStepUnderAttack && !oneNotUnderAttackFound)) {
                    unitSteps.add(ordinalNr);
                }
                if (!thisStepUnderAttack) {
                    oneNotUnderAttackFound = true;
                }
                ordinalNr++;
            }
            steps.add(unitSteps);
        }
        iterate();
    }

    public List<BattleUnitStep<P>> getCurrentSet() {
        return currentSet;
    }

    public void iterate() {
        if (currentSet == null) {
            currentSet = new ArrayList<BattleUnitStep<P>>(units.size());
        }
        else {
            lastSet = true;
            for (int listNr = steps.size()-1; listNr >= 0; listNr--) {
                LinkedList<Integer> step = steps.get(listNr);
                if (step.size() == 1) continue;
                Integer last = step.removeFirst();
                step.addLast(last);
                if (step.getFirst() >= last) {  //no switching
                    lastSet = false;
                    break;
                }
            }
        }

        currentSet.clear();
        int unit = 0;
        for (LinkedList<Integer> list: steps) {
            BattleUnitStep<P> step = units.get(unit).getPossibleSteps().get(list.getFirst());
            for (BattleUnitStep<P> exStep: currentSet) {
                if (!step.isCompatibleWith(exStep)) {
                    iterate();
                    return;
                }
            }
            currentSet.add(step);
            unit++;
        }

    }

    public boolean isLastSet() {
        return lastSet;
    }

    public Iterator<List<BattleUnitStep<P>>> iterator() {
        return new Iterator<List<BattleUnitStep<P>>>() {
            public boolean hasNext() {
                return !isLastSet();
            }

            public List<BattleUnitStep<P>> next() {
                List<BattleUnitStep<P>> curentSet = new ArrayList<BattleUnitStep<P>>(currentSet);
                iterate();
                return curentSet;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
