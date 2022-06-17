package game.obstacles;

import game.GameModifier;
import game.GamePanel;
import game.events.StateListener;
import game.events.TurnListener;
import game.gameobjects.Board;
import game.gameobjects.BoardCell;
import game.gameobjects.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Class that implements functionality of "freeze" obstacle.
 *
 * @author Artem Novak
 */
public class Freeze extends Obstacle {
    public static final int FREEZE_TURNS = 30;

    private final Board board;
    private final Random random = new Random();

    private List<BoardCell> lastCheckCells = new ArrayList<>();
    private int freezeCounter;

    public Freeze(GamePanel gp) {
        super(gp);
        this.board = gp.getBoard();
        board.addTurnListener(() -> {
            lastCheckCells = board.getCellsByPredicate(x -> board.getTileInCell(x) != null);
            if (lastCheckCells.isEmpty()) setState(GameModifier.UNAPPLICABLE);
            else setState(GameModifier.APPLICABLE);
        });
    }

    @Override
    public String getNameID() {
        return "freeze";
    }

    @Override
    public void startApplication() {
        BoardCell cell = lastCheckCells.get(random.nextInt(0, lastCheckCells.size()));
        Tile tile = board.getTileInCell(cell);
        tile.setLocked(true);
        board.addStateListener(new StateListener() {
            @Override
            public void onStateChanged(int oldState, int newState) {
                if (oldState == Board.ANIMATING && newState == Board.IDLE) {
                    board.removeStateListener(this);
                    tile.setVisuallyLocked(true);
                    board.addTurnListener(new TurnListener() {
                        @Override
                        public void onTurn() {
                            if (tile.isLocked()) {
                                freezeCounter++;
                                if (freezeCounter > FREEZE_TURNS) {
                                    tile.setLocked(false);
                                    tile.setVisuallyLocked(false);
                                }
                            }
                            if (!tile.isLocked()) {
                                freezeCounter = 0;
                                board.removeTurnListener(this);
                            }
                        }
                    });
                    board.setState(Board.ANIMATING);
                }
            }
        });
    }
}
