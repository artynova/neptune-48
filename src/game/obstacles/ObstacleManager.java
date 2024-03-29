package game.obstacles;

import game.GamePanel;
import game.events.ObstacleEvent;
import game.events.ObstacleListener;
import game.gameobjects.Board;
import game.utils.WeightedRandom;
import misc.AudioManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that handles application of in-game obstacles.
 *
 * @author Artem Novak
 */
public class ObstacleManager {
    private final int minInterval;
    private final int maxInterval;
    private final Map<Obstacle, Integer> obstacleWeights = new HashMap<>();
    private final Map<Boolean, Integer> triggerLikelihood = new HashMap<>();
    private final WeightedRandom random = new WeightedRandom();
    private final List<ObstacleListener> obstacleListeners = new ArrayList<>();
    private final GamePanel gp;
    private final Board board;

    private int turnsElapsed = 0;
    private Obstacle latestObstacle;

    /**
     * Creates a new ObstacleManager with specified obstacles and their probability.
     *
     * @param obstacleWeights map of obstacle NameIDs to their weights (relative likelihood of occurrence when an abstract obstacle is triggered)
     * @param minInterval minimal interval in turns between two obstacles
     * @param maxInterval maximal interval in turns between two obstacles
     * @param gp {@link GamePanel}
     */
    public ObstacleManager(Map<String, Integer> obstacleWeights, int minInterval, int maxInterval, GamePanel gp) {
        this.gp = gp;
        this.board = gp.getBoard();
        for (String nameID : obstacleWeights.keySet()) {
            Integer weight = obstacleWeights.get(nameID);
            this.obstacleWeights.put(registerObstacle(nameID), weight);
        }
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        triggerLikelihood.put(true, 1);
        attemptObstacle();
        gp.getBoard().addTurnListener(() -> {
            turnsElapsed++;
            attemptObstacle();
        });
    }

    private Obstacle registerObstacle(String nameID) {
        return switch (nameID) {
            case "damageEntity" -> new DamageEntity(gp);
            case "downgrade" -> new Downgrade(gp);
            case "freeze" -> new Freeze(gp);
            case "garbageTile" -> new GarbageTile(gp);
            case "healEntity" -> new HealEntity(gp);
            case "randomDispose" -> new RandomDispose(gp);
            case "randomScramble" -> new RandomScramble(gp);
            case "randomSwap" -> new RandomSwap(gp);
            case "subtractTurns" -> new SubtractTurns(gp);
            default -> throw new IllegalArgumentException("Obstacle " + nameID + " does not exist");
        };
    }

    private void attemptObstacle() {
        if (turnsElapsed >= minInterval) {
            triggerLikelihood.put(false, maxInterval + 1 - turnsElapsed);
            if (random.weightedChoice(triggerLikelihood)) {
                Obstacle obstacle = null;
                Map<Obstacle, Integer> runtimeLikelihoods = new HashMap<>(obstacleWeights);
                do {
                    runtimeLikelihoods.remove(obstacle);
                    obstacle = random.weightedChoice(runtimeLikelihoods);
                    if (obstacle == null) break;
                } while (obstacle.getState() != Obstacle.APPLICABLE);
                if (runtimeLikelihoods.isEmpty()) return; // Happens when no obstacle could be applied. Turn counter is not reset, so if it exceeds max interval, attempt next turn is guaranteed.
                ObstacleEvent e = new ObstacleEvent(obstacle);
                for (ObstacleListener listener : new ArrayList<>(obstacleListeners)) listener.onObstacleSelected(e);
                latestObstacle = e.getObstacle();
                if (latestObstacle != null) {
                    latestObstacle.startApplication();
                    AudioManager.playSFX("obstacle");
                    board.addDamageHighlight();
                }
                else AudioManager.playSFX("obstacleBlocked");
                turnsElapsed = 0;
            }
        }
    }
    public void addObstacleListener(ObstacleListener listener) {
        obstacleListeners.add(listener);
    }

    public void removeObstacleListener(ObstacleListener listener) {
        obstacleListeners.remove(listener);
    }

    /**
     * Returns the latest applied obstacle. If it is equal to null, that means the obstacle was negated.
     *
     * @return last applied obstacle
     */
    public Obstacle getLatestObstacle() {
        return latestObstacle;
    }
}
