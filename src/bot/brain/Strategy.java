package bot.brain;

import java.util.List;

public class Strategy {

    public Strategy() {
    }

    public int getCountOfRushers(List<Ant> ants) {
        int overall = ants.size();
        int harvesters = 0;
        int rushers = 0;
        int lazy = 0;
        for (Ant ant: ants) {
            if (ant.isRushing()) {
                rushers++;
            }
            else if (ant.isHarvesting()) {
                harvesters++;
            }
            else {
                lazy++;
            }
        }
        if (overall < 30) {
            return -rushers;
        }
        else {
            return Math.max(20 - rushers, 0);
        }
    }

    public int getCountOfGuards(List<Ant> ants) {
        return ants.size() / 10;
    }
}
