package game;

import game.utils.GamePanelGraphics;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Set;

/**
 * Implementation of the panel in which the core game loop (2048 gameplay, attacking, triggering bonuses etc.) happens.
 *
 * @author Artem Novak
 */
public class GamePanel extends JPanel implements Runnable {
    // Game state
    public static final int PLAYING = 0, PAUSED = 1, ENDED = 2;

    public int state = PLAYING;
    public Board board;
    public GamePanelGraphics graphicsManager;
    public KeyHandler keyHandler;
    public boolean debug = false;

    private static final int FPS = 60;

    /**
     * Constructs a game panel with given graphics manager object.
     *
     * @param graphicsManager graphics manager, which contains all necessary settings
     */
    public GamePanel(int boardRows, int boardCols, Set<String> bonusNameIDs, Set<String> obstacleNameIDs, GamePanelGraphics graphicsManager) throws IOException {
        this.graphicsManager = graphicsManager;
        this.board = new Board(boardRows, boardCols, 1, this);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        Thread gameThread = new Thread(this);
        board.generateRandomTile();
        board.generateRandomTile();
        board.generateRandomTile();
        int preferredWidth = GamePanelGraphics.TILE_SIZE * boardCols + GamePanelGraphics.TILE_OFFSET * (boardCols + 1);
        int preferredHeight = GamePanelGraphics.TILE_SIZE * boardRows + GamePanelGraphics.TILE_OFFSET * (boardRows + 1);
        this.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
        graphicsManager.load(boardRows, boardCols);
        gameThread.start();
    }

    @Override
    public void run() {
        double frameInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        // Debug
        long debugTimer = 0;
        int frameCount = 0;

        while (state != ENDED) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime)/frameInterval;
            debugTimer += currentTime - lastTime; // DEBUG
            lastTime = currentTime;

            if (state == PLAYING && delta >= 1) {
                for (int i = 0; i < (int)delta; i++) {
                    update();
                }
                repaint();
                frameCount++; // DEBUG
                delta -= (int)delta;
            }

            // DEBUG
            if (debugTimer >= 1000000000) {
                if (debug) System.out.println("FPS: " + frameCount);
                frameCount = 0;
                debugTimer = 0;
            }

        }
    }

    /**
     * Updates all components of the panel.
     */
    public void update() {
        board.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        board.render((Graphics2D)g);
        g.dispose();
    }

    public void loseLevel() {
        //TODO
    }

    public void reactToTurn() {
        // TODO
    }

}
