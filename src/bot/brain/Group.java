package bot.brain;

import bot.Aim;
import bot.Ants;
import bot.Tile;

import javax.swing.text.Position;
import java.util.*;

public class Group {

    private Ants field;

    public void setField(Ants field) {
        this.field = field;
    }

    static int xes[] = {1, 1, -1, -1};
    static int yes[] = {1, -1, 1, -1};

    class EnemyVision {
        public final Ant ant;
        public final Tile enemy;
        public final Tile ourBoy;
        public final int ourDistance;
        public int minDistance;

        EnemyVision(Ant ant, Tile enemy, Tile ourBoy, int ourDistance) {
            this.enemy = enemy;
            this.ourBoy = ourBoy;
            this.ourDistance = ourDistance;
            this.ant = ant;
        }

        public BestMovement findBestMovement() {
            if (ourDistance == minDistance) {
                return new BestMovement(ant, null, minDistance);
            }
            Aim movement = null;
            int distance = ourDistance;
            Set<Aim> forbiddenAims = new HashSet<Aim>();
            //orthogonal check
            for (Aim aim: Aim.values()) {
                Tile target = field.getTile(ourBoy, aim);
                int newDistance = field.getDistance(target, enemy);
                if (field.getIlk(target).ourUnitMayMoveHere()) {
                    if (newDistance < distance && newDistance >= minDistance) {
                        distance = newDistance;
                        movement = aim;
                    }
                }
                else {
                    forbiddenAims.add(aim);
                }
            }
            //diagonal check
            for (int i = 0; i < 4; i++) {
                Tile target = field.normalize(ourBoy.getRow() + yes[i], ourBoy.getCol() + xes[i]);

                int newDistance = field.getDistance(target, enemy);
                if (field.getIlk(target).ourUnitMayMoveHere()) {
                    if (newDistance < distance && newDistance >= minDistance) {
                        for (Aim aim: field.getDirections(ourBoy, target)) {
                            if (!forbiddenAims.contains(aim)) {
                                distance = newDistance;
                                movement = aim;
                                break;
                            }
                        }
                    }
                }
            }

            return new BestMovement(ant, movement, distance);
        }
    }

    class BestMovement {
        public final Ant ant;
        public final Aim aim;
        public final int resDistance;

        BestMovement(Ant ant, Aim aim, int resDistance) {
            this.ant = ant;
            this.aim = aim;
            this.resDistance = resDistance;
        }

        public void doMovement() {
            if (aim != null)
                ant.move(aim);
        }
    }

    public void turnToEnemies(List<Ant> ants, List<Tile> enemies) {
        if (ants.isEmpty()) return;
        //2. find distances to enemies from any ant from group
        List<List<EnemyVision>> enemyVisions = getEnemyVisions(enemies, ants);
        //3. for each enemy ant (starting from nearest one)
        List<Ant> nonMovedAnts = new ArrayList<Ant>(ants);
        for (List<EnemyVision> list: enemyVisions) {
            List<BestMovement> movements = new ArrayList<BestMovement>();
            //3.1. for each our non-moved ant
            for (EnemyVision vision: list) {
                if (nonMovedAnts.contains(vision.ant)) {
                    //3.1.1 check all possible movements and their distances
                    movements.add(vision.findBestMovement());
                }
            }
            //3.1.2 get movement which will lead to distance closest to min one
            Collections.sort(movements, new Comparator<BestMovement>() {
                public int compare(BestMovement o1, BestMovement o2) {
                    return ((Integer)o1.resDistance).compareTo(o2.resDistance);
                }
            });
            int moved = 0;
            //3.2 select ant which will lead to distance closest to min one and move it
            for (int i = 0; i < movements.size() && moved < 2; i++) {
                BestMovement bestMovement = movements.get(i);
                bestMovement.doMovement();
                nonMovedAnts.add(bestMovement.ant);
                moved++;
            }
        }
    }

    private List<List<EnemyVision>> getEnemyVisions(List<Tile> enemies, List<Ant> ants) {
        List<List<EnemyVision>> visions = new ArrayList<List<EnemyVision>>();
        for (Tile enemy: enemies) {
            List<EnemyVision> enemyVision = new ArrayList<EnemyVision>();
            int minDistance = 999999;
            for (Ant ourBoy: ants) {
                int distance = this.field.getDistance(ourBoy.getPosition(), enemy);
                if (distance < minDistance) {
                    minDistance = distance;
                }
                enemyVision.add(new EnemyVision(ourBoy, enemy, ourBoy.getPosition(), distance));
            }
            for (EnemyVision v: enemyVision) {
                v.minDistance = minDistance;
            }
            visions.add(enemyVision);
        }
        Collections.sort(visions, new Comparator<List<EnemyVision>>() {
            public int compare(List<EnemyVision> o1, List<EnemyVision> o2) {
                return ((Integer)o1.get(0).minDistance).compareTo(o2.get(0).minDistance);
            }
        });
        return visions;
    }

    public List<Tile> findEnemiesInGroupVision(List<Ant> ants) {
        List<Tile> enemies = new ArrayList<Tile>();
        for (Tile enemy: field.getEnemyAnts()) {
            for (Ant ant: ants) {
                if (ant.see(enemy)) {
                    enemies.add(enemy);
                    break;
                }
            }
        }
        return enemies;
    }


    public void backToPositions(List<Ant> ants, List<Tile> positions) {
        List<Ant> freeAnts = new ArrayList<Ant>();
        List<Tile> unorderedPositions = new ArrayList<Tile>(positions);
        for (Ant ant: ants) {
            boolean alreadyOnPosition = unorderedPositions.remove(ant.getPosition());
            if (!ant.isBusy() && !alreadyOnPosition) {
                freeAnts.add(ant);
            }
            else {
                unorderedPositions.remove(ant.getTarget());
            }
        }
        for (Tile position: unorderedPositions) {
            int minDistance = 99999;
            Ant antToMove = null;
            for (Ant ant: freeAnts) {
                if (ant.isOn(position)) {
                    freeAnts.remove(ant);
                    antToMove = null;
                    break;
                }
                int distance = field.getDistance(ant.getPosition(), position);
                if (distance < minDistance) {
                    minDistance = distance;
                    antToMove = ant;
                }
            }
            if (antToMove != null) {
                antToMove.goAndSeeAround(position);
                freeAnts.remove(antToMove);
            }
        }
    }
}
