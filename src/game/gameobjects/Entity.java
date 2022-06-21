package game.gameobjects;

import game.GameLogicException;
import game.GamePanel;
import game.UIDataHolder;
import game.events.StateListener;
import game.events.UIDataListener;
import game.gameobjects.particles.ParticleManager;
import game.gameobjects.particles.TextParticle;
import game.utils.GamePanelGraphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that stores information about an abstract entity above the board.
 * As a {@link UIDataHolder} triggers corresponding methods whenever entity's health changes.
 *
 * @author Artem Novak
 */
public class Entity extends GameObject implements UIDataHolder {
    public static int IDLE = 0, ANIMATING = 1;

    private final long maxHealth;
    private final List<UIDataListener> uiDataListeners = new ArrayList<>();
    private final List<StateListener> stateListeners = new ArrayList<>();
    private final int gameMode;
    private final ParticleManager particleManager;

    private long health;
    private int state;
    private int animationFramesLeft;
    private BufferedImage animationImage;

    /**
     * Creates a new Entity.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param maxHealth max health
     * @param gp root GamePanel
     */
    public Entity(int x, int y, long maxHealth, GamePanel gp) {
        super(x, y, gp);
        this.gameMode = gp.getGameMode();
        this.particleManager = gp.getParticleManager();
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    @Override
    public void update() {
        if (state == ANIMATING) {
            animationFramesLeft--;
            if (animationFramesLeft <= 0) {
                animationFramesLeft = 0;
                setState(IDLE);
            }
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        if (state == ANIMATING) g2d.drawImage(animationImage, (int)x, (int)y, null);
        else g2d.drawImage(graphics.getTexture("entity"), (int)x, (int)y, null);
    }

    public void takeDamage(long damage) {
        if (damage < 0) throw new GameLogicException("Trying to damage by negative amount, use takeHealing() instead");
        long oldHealth = health;
        health -= damage;
        health = Math.max(health, 0);
        if (health == 0) {
            switch (gameMode) {
                case GamePanel.GAME_MODE_ATTACK -> gp.winLevel();
                case GamePanel.GAME_MODE_REPAIR -> gp.loseLevel();
            }
        }
    }

    public void takeHealing(long healing) {
        if (healing < 0) throw new GameLogicException("Trying to heal by negative amount, use takeDamage() instead");
        long oldHealth = health;
        health += healing;
        health = Math.min(health, maxHealth);
        if (health == maxHealth && gameMode == GamePanel.GAME_MODE_REPAIR) gp.winLevel();
    }

    public void animateDamage(long damage) {
        if (damage > 0) {
            animationImage = graphics.getTexture("entityDamaged");
            for (UIDataListener listener : new ArrayList<>(uiDataListeners)) listener.onUIDataChanged();
            particleManager.addHealthChangeParticle("-" + damage, new Rectangle((int)x + animationImage.getWidth() / 4, (int)y + animationImage.getHeight() / 4, animationImage.getWidth() / 2, animationImage.getHeight() / 2));
            startAnimationCycle();
            addStateListener(new StateListener() {
                @Override
                public void onStateChanged(int oldState, int newState) {
                    if (oldState == ANIMATING && newState == IDLE) {
                        removeStateListener(this);
                        animationImage = graphics.getTexture("entity");
                    }
                }
            });
        }
    }

    public void animateHealing(long healing) {
        if (healing > 0) {
            animationImage = graphics.getTexture("entityHealed");
            for (UIDataListener listener : new ArrayList<>(uiDataListeners)) listener.onUIDataChanged();
            particleManager.addHealthChangeParticle("+" + healing, new Rectangle((int)x + animationImage.getWidth() / 4, (int)y + animationImage.getHeight() / 4, animationImage.getWidth() / 2, animationImage.getHeight() / 2));
            startAnimationCycle();
            addStateListener(new StateListener() {
                @Override
                public void onStateChanged(int oldState, int newState) {
                    if (oldState == ANIMATING && newState == IDLE) {
                        removeStateListener(this);
                        animationImage = graphics.getTexture("entity");
                    }
                }
            });
        }
    }

    public long getMaxHealth() {
        return maxHealth;
    }

    public long getHealth() {
        return health;
    }

    public void setState(int state) {
        if (state < 0 || state > 2) throw new IllegalArgumentException("Entity does not support state " + state);
        if (this.state != state) {
            int oldState = this.state;
            this.state = state;
            for (StateListener listener : new ArrayList<>(stateListeners)) listener.onStateChanged(oldState, state);
        }
    }

    public int getState() {
        return state;
    }

    public void addUIDataListener(UIDataListener listener) {
        uiDataListeners.add(listener);
    }

    public void removeUIDataListener(UIDataListener listener) {
        uiDataListeners.remove(listener);
    }

    public void addStateListener(StateListener listener) {
        stateListeners.add(listener);
    }

    public void removeStateListener(StateListener listener) {
        stateListeners.remove(listener);
    }

    private void startAnimationCycle() {
        animationFramesLeft = GamePanelGraphics.ANIMATION_CYCLE;
        setState(ANIMATING);
    }
}
