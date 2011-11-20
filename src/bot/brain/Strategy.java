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
        else if (overall > 60) {
            return Math.max(ants.size() / 2 - rushers, 0);
        }
        else {
            //return Math.max((ants.size() - 10) / 2 - rushers, 0);
            return Math.max(20 - rushers, 0);
        }
    }

    public int getCountOfGuards(List<Ant> ants, int hillsInDanger) {
        if (ants.size() == 1) return 0;
        int countOfTens = ants.size() / 12;
        return countOfTens + hillsInDanger * 4;
    }
}
