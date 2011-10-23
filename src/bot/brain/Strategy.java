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
        if (overall < 20) {
            return -rushers;
        }
        else {
            return Math.max(ants.size() / 2 - rushers, 0);
        }
    }

    public int getCountOfGuards(List<Ant> ants) {
        int countOfTens = ants.size() / 10;
        return countOfTens == 1 ? 2 : countOfTens;
    }
}
