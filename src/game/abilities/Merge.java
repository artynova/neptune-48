package game.abilities;

import game.GamePanel;
import game.events.AbilityListener;
import game.events.CellSelectionListener;
import game.gameobjects.BoardCell;
import game.gameobjects.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Class that implements functionality of "merge" active ability along with tile selection for it.
 *
 * @author Artem Novak
 */
public class Merge extends ActiveAbility {
    public static final int DEFAULT_COOLDOWN = 5;

    public Merge(GamePanel gp, AbilityManager abilityManager) {
        super(gp, abilityManager, DEFAULT_COOLDOWN);
        determineApplicability();
    }

    @Override
    public String getNameID() {
        return "merge";
    }

    @Override
    public void startApplication() {
        super.startApplication();
        setState(APPLYING);
        Predicate<BoardCell> basePredicate = x -> board.getTileInCell(x) != null && cellTileHasPair(x);
        board.initSelection(basePredicate, 2);
        board.addCellSelectionListener(new CellSelectionListener() {
            @Override
            public void onSelectionUpdated(List<BoardCell> cells) {
                if (cells.size() == 0) board.changeSelectionPredicate(basePredicate);
                else if (cells.size() == 1) board.changeSelectionPredicate(x -> {
                    Tile tile = board.getTileInCell(x);
                    return tile != null && tile.getLevel() == board.getTileInCell(cells.get(0)).getLevel();
                });
            }

            @Override
            public void onSelectionCompleted(List<BoardCell> cells) {
                board.removeCellSelectionListener(this);
                board.fullMerge(cells.get(1), cells.get(0));
                currentCooldown = cooldown;
                for (AbilityListener listener : new ArrayList<>(abilityListeners)) listener.onAbilityApplied();
            }

            @Override
            public void onSelectionAborted() {
                updateApplicability();
                board.removeCellSelectionListener(this);
            }
        });
    }

    @Override
    protected boolean determineApplicability() {
        if (super.determineApplicability()) {
            List<BoardCell> cells = board.getCellsByPredicate(x -> board.getTileInCell(x) != null);
            for (BoardCell cell : cells) {
                if (cellTileHasPair(cell)) return true;
            }
        }
        return false;
    }

    /**
     * Determines whether the board has at least one more tile of the same level as this cell's tile.
     *
     * @return true if there is at least one other tile with the same level
     */
    private boolean cellTileHasPair(BoardCell cell) {
        Tile tile = board.getTileInCell(cell);
        List<BoardCell> cells = board.getCellsByPredicate(x -> {
            Tile otherTile = board.getTileInCell(x);
            return !x.equals(cell) && otherTile != null && otherTile.getLevel() == tile.getLevel();
        });
        return !cells.isEmpty();
    }
}
