package bot.brain;

import bot.Ants;

import java.util.List;

public interface Team {

    public void setField(Ants ants);

    public void doTurn();

    public void removeManagedFrom(List<Ant> ants);

    public void assign(Ant ant);

    public int getCount();

    public void detach(int targetCount, List<Ant> detachedAnts);
}
