package bot2.ai.battle;

import bot2.Logger;
import bot2.map.FieldPoint;

import java.util.List;

public class BattleStrategy {




    public static BattleCase<FieldPoint> maxDamage(List<BattleCase<FieldPoint>> cases) {
        Logger.log(cases.toString());
        BattleCase<FieldPoint> res = cases.get(0);
        for (BattleCase<FieldPoint> bCase: cases) {
            switch (compare(bCase.resolution.maxEnemiesLost, res.resolution.maxEnemiesLost)) {
                case FIRST_LARGER:
                    res = bCase;
                    break;
                case EQUAL:
                    switch (compare(bCase.resolution.minEnemiesLost, res.resolution.minEnemiesLost)) {
                        case FIRST_LARGER:
                        case EQUAL:
                            res = bCase;
                            break;
                            //if (bCase.resolution.minLost < res.resolution.minLost) {
                            //    res = bCase;
                            //}
                    }
                    break;
            }
        }
        return res;
    }

    static enum CMP {
        FIRST_SMALLER,
        EQUAL,
        FIRST_LARGER
    }

    private static CMP compare(int nr1, int nr2) {
        if (nr1 > nr2) return CMP.FIRST_LARGER;
        if (nr1 < nr2) return CMP.FIRST_SMALLER;
        return CMP.EQUAL;
    }

    public static BattleCase<FieldPoint> minLost(List<BattleCase<FieldPoint>> cases) {
        BattleCase<FieldPoint> res = cases.get(0);
        for (BattleCase<FieldPoint> bCase: cases) {
            if (bCase.resolution.maxLost < res.resolution.maxLost) {
                res = bCase;
            }
            if (bCase.resolution.maxLost == res.resolution.maxLost && bCase.resolution.maxEnemiesLost > res.resolution.maxEnemiesLost) {
                res = bCase;
            }
        }
        return res;
    }

}
