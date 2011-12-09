package bot2.ai.battle;

import bot2.AntLocator;
import bot2.GameSettings;
import bot2.Logger;
import bot2.Time;
import bot2.ai.Ant;
import bot2.ai.GameStrategy;
import bot2.map.Field;
import bot2.map.FieldPoint;
import bot2.map.Item;
import bot2.map.areas.CircleArea;
import pathfinder.MathCache;

import java.util.*;

public class Battle {

    private GameSettings settings;
    private Field field;
    private CircleArea dangerousArea;
    private BattlePredictor<FieldPoint> predictor = new BattlePredictor<FieldPoint>();
    private BattleAdvisor<FieldPoint> advisor;
    private GameStrategy strategy;

    public Battle(GameSettings settings, Field field, GameStrategy strategy) {
        this.settings = settings;
        this.field = field;
        this.strategy = strategy;
        dangerousArea = new CircleArea(field, (int) MathCache.square(settings.getAttackRadius() + 3));
        advisor = new AttackRadiusAdvisor(field, settings);
    }

    public void process(List<Ant> ants) {
        Time.time.completed("Before battle");
        Collection<FieldPoint> antsInBattle = new HashSet<FieldPoint> ();
        Collection<BattleCase<FieldPoint>> battles = new ArrayList<BattleCase<FieldPoint>>();
        Map<FieldPoint, Ant> pointsToAnts = new HashMap<FieldPoint, Ant>();
        for (Ant ant: ants) {
            pointsToAnts.put(ant.getLocation(), ant);
            if (antsInBattle.contains(ant.getLocation())) continue;
            if (ant.getVisibleView().getEnemiesCount() > 0) {
                battles.addAll(doProcessAnt(ant, antsInBattle));
            }
        }
        Time.time.completed("Befor execution");
        for (BattleCase<FieldPoint> bCase: battles) {
            if (bCase != null) {
                for (BattleUnitStep<FieldPoint> step: bCase.ourUnitsSteps) {
                    Ant ant = pointsToAnts.get(step.getUnit().getPoint());
                    ant.moveInBattle(step.getPoint(), bCase.flag);
                    ants.remove(ant);
                }
            }
        }
        Time.time.completed("Execution");
    }

    private Collection<BattleCase<FieldPoint>> doProcessAnt(Ant ant, Collection<FieldPoint> antsInBattle) {
        Collection<BattleCase<FieldPoint>> decisions = new ArrayList<BattleCase<FieldPoint>>(4);
        Time.time.completed("Before detection");
        List<FieldPoint> enemiesInBattle = new ArrayList<FieldPoint>();
        List<FieldPoint> ourAntsInBattle = new ArrayList<FieldPoint>();
        detectEnemies(ant.getLocation(), ourAntsInBattle, enemiesInBattle);
        if (enemiesInBattle.size() == 0) return decisions;
        Time.time.completed("Detection");
        SingleBattleStrategy str = strategy.getSingleBattleStrategy(ourAntsInBattle, enemiesInBattle);
        for (List<FieldPoint> ourAnts: resort(ourAntsInBattle, str.maxGroupSize)) {
            List<BattleCase<FieldPoint>> res = predictor.predictBattle(
                    toBattleUnits(ourAnts, str.allowOurAntsToStand, false),
                    toBattleUnits(enemiesInBattle, true, str.reviewOnlyStandEnemies), advisor);
            Time.time.completed("Prediction");
            if (isNotBattle(res)) {
                Logger.log("Not a battle - no danger for our ants");
                continue;
            }
            decisions.add(strategy.select(res, ourAnts, enemiesInBattle));
            antsInBattle.addAll(ourAnts);
        }
        //Logger.log(res.toString());

        Time.time.completed("Resolution");
        return decisions;
    }

    private boolean isNotBattle(List<BattleCase<FieldPoint>> res) {
        return res.size() == 1 && res.get(0).resolution.maxEnemiesLost == 0 && res.get(0).resolution.maxLost == 0;
    }

    private List<List<FieldPoint>> resort(List<FieldPoint> ourAntsInBattle, int maxGroupSize) {
        List<List<FieldPoint>> lists = new ArrayList<List<FieldPoint>>(ourAntsInBattle.size() / maxGroupSize + 1);
        for (int i = 0; i < ourAntsInBattle.size(); i+= maxGroupSize) {
            lists.add(ourAntsInBattle.subList(i, i+maxGroupSize >= ourAntsInBattle.size() ? ourAntsInBattle.size() : i+maxGroupSize));
        }
        return lists;
    }

    private List<BattleUnit<FieldPoint>> toBattleUnits(List<FieldPoint> points, boolean allowStand, boolean onlyStand) {
        List<BattleUnit<FieldPoint>> units = new ArrayList<BattleUnit<FieldPoint>>(points.size());
        for (FieldPoint point: points) {
            units.add(new AntBattleUnit(point, field, allowStand, onlyStand));
        }
        return units;
    }

    private void detectEnemies(FieldPoint point, Collection<FieldPoint> ourAnts, Collection<FieldPoint> enemies) {
        Item item = field.getItem(point);
        Item toSearch = item == Item.ANT ? Item.ENEMY_ANT : Item.ANT;
        Collection<FieldPoint> targetCollection = item == Item.ANT ? enemies : ourAnts;
        for (FieldPoint en: findEnemiesAround(point, toSearch)) {
            if (!targetCollection.contains(en)) {
                targetCollection.add(en);
                detectEnemies(en, ourAnts, enemies);
            }
        }

    }

    private Set<FieldPoint> findEnemiesAround(FieldPoint point, Item itemToSearch) {
        Set<FieldPoint> points = new HashSet<FieldPoint>();
        for (FieldPoint potPoint: dangerousArea.getPoints(point)) {
            if (field.getItem(potPoint) == itemToSearch) {
                points.add(potPoint);
            }
        }
        return points;
    }

}
