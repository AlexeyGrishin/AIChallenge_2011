package bot.brain.teams;

import bot.Ants;
import bot.brain.Ant;
import bot.brain.AntListener;
import bot.brain.Team;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTeam implements Team, AntListener {

    private List<Ant> managedAnts = new ArrayList<Ant>();
    private Ants field;

    protected List<Ant> getAnts() {
        return managedAnts;
    }

    protected Ants getField() {
        return field;
    }

    public void onDie(Ant ant) {
        managedAnts.remove(ant);
    }

    public void setField(Ants ants) {
        this.field = ants;
    }

    public void removeManagedFrom(List<Ant> ants) {
        ants.removeAll(managedAnts);
    }

    public void assign(Ant ant) {
        if (!managedAnts.contains(ant)) {
            managedAnts.add(ant);
            ant.addListener(this);
        }
    }

    public int getCount() {
        return managedAnts.size();
    }

    public void detach(int targetCount, List<Ant> detachedAnts) {
        //may be overriden
        while (managedAnts.size() > targetCount) {
            Ant removed = managedAnts.remove(0);
            removed.removeListener(this);
            detachedAnts.add(removed);
        }
        field.log("Reduce count of group " + this.getClass().getSimpleName() + " to " + targetCount + ": detached " + detachedAnts);
    }
}
