package com.snakegame.controller;

import com.snakegame.model.*;
import com.snakegame.view.GamePanel;
import com.snakegame.utils.SoundManager;
import javax.swing.*;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;   // 添加这一行解决 LinkedList 无法解析的问题

public class GameController {
    private final Snake snake;
    private final Food food;
    private final GameState gameState;
    private final GamePanel panel;
    private Timer timer;
    private static final int DEFAULT_DELAY = 100;
    private int currentDelay;
    private boolean isSpeedBoostActive;
    private Timer speedBoostTimer;
    private Timer effectTimer;
    private String activeEffect;
    private Point effectPosition;
    private boolean isInvincible;
    private Timer invincibleTimer;

    public GameController(Snake snake, Food food, GameState gameState, GamePanel panel) {
        this.snake = snake;
        this.food = food;
        this.gameState = gameState;
        this.panel = panel;
        this.isSpeedBoostActive = false;
        this.activeEffect = "";
        this.isInvincible = false;
    }

    public void startGame(int width, int height, int unitSize) {
        int startX = width / 2 / unitSize * unitSize;
        int startY = height / 2 / unitSize * unitSize;
        snake.init(startX, startY, unitSize);
        gameState.reset();
        food.spawn(width, height, unitSize, snake.getBody());

        currentDelay = DEFAULT_DELAY;
        startTimer();
        SoundManager.getInstance().playBackgroundMusic("/com/snakegame/resources/bgm.wav");
    }

    private void startTimer() {
        if (timer != null) timer.stop();
        timer = new Timer(currentDelay, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameLoop(GamePanel.WIDTH, GamePanel.HEIGHT, GamePanel.UNIT_SIZE);
            }
        });
        timer.start();
    }

    public void activateInvincible(int durationMs) {
        if (isInvincible) return;
        isInvincible = true;
        if (invincibleTimer != null) invincibleTimer.stop();
        invincibleTimer = new Timer(durationMs, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isInvincible = false;
                invincibleTimer.stop();
            }
        });
        invincibleTimer.setRepeats(false);
        invincibleTimer.start();
    }

    public void activateSpeedBoost(int durationMs) {
        if (isSpeedBoostActive) return;
        isSpeedBoostActive = true;
        final int originalDelay = currentDelay;
        currentDelay = Math.max(30, currentDelay / 2);
        timer.setDelay(currentDelay);
        if (speedBoostTimer != null) speedBoostTimer.stop();
        speedBoostTimer = new Timer(durationMs, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentDelay = originalDelay;
                timer.setDelay(currentDelay);
                isSpeedBoostActive = false;
                speedBoostTimer.stop();
            }
        });
        speedBoostTimer.setRepeats(false);
        speedBoostTimer.start();
    }

    public void triggerEffect(String effectType, Point position) {
        activeEffect = effectType;
        effectPosition = position;
        panel.repaint();
        if (effectTimer != null) effectTimer.stop();
        effectTimer = new Timer(300, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                activeEffect = "";
                effectPosition = null;
                panel.repaint();
                effectTimer.stop();
            }
        });
        effectTimer.setRepeats(false);
        effectTimer.start();
    }

    public String getActiveEffect() { return activeEffect; }
    public Point getEffectPosition() { return effectPosition; }
    public boolean isInvincible() { return isInvincible; }

    private void gameLoop(int width, int height, int unitSize) {
        if (gameState.isRunning() && !gameState.isPaused()) {
            move(width, height, unitSize);
            if (!isInvincible && checkCollisions(width, height)) {
                gameState.setRunning(false);
                timer.stop();
                if (speedBoostTimer != null) speedBoostTimer.stop();
                if (effectTimer != null) effectTimer.stop();
                if (invincibleTimer != null) invincibleTimer.stop();
            }
            panel.repaint();
        }
    }

    private void move(int width, int height, int unitSize) {
        snake.setDirection(snake.getNextDirection());
        Point newHead = calculateNewHead(unitSize);
        FoodItem eatenFood = food.checkFoodCollision(newHead);
        boolean ate = (eatenFood != null);

        if (ate) {
            FoodType type = eatenFood.getType();
            Point foodPos = eatenFood.getPosition();
            gameState.addScore(type.getScoreValue());

            if (type.getGrowthValue() > 0) {
                snake.addGrowth(type.getGrowthValue());
                if (type == FoodType.GOLDEN) {
                    triggerEffect("GOLDEN", foodPos);
                    activateInvincible(3000);
                }
            } else if (type.getGrowthValue() < 0) {
                triggerEffect("BOMB", foodPos);
                for (int i = 0; i < -type.getGrowthValue(); i++) {
                    snake.removeTail();
                }
            }

            if (type == FoodType.STAR) {
                activateSpeedBoost(3000);
            }

            food.removeFood(eatenFood);
            if (food.getRemainingFoodCount() == 0) {
                food.spawn(width, height, unitSize, snake.getBody());
            }
        }
        snake.moveAndGrow(newHead, ate);
    }

    private Point calculateNewHead(int unitSize) {
        Point head = snake.getHead();
        int newX = head.x;
        int newY = head.y;
        switch (snake.getDirection()) {
            case 'U': newY -= unitSize; break;
            case 'D': newY += unitSize; break;
            case 'L': newX -= unitSize; break;
            case 'R': newX += unitSize; break;
            default: break;
        }
        return new Point(newX, newY);
    }

    private boolean checkCollisions(int width, int height) {
        Point head = snake.getHead();
        if (head.x < 0 || head.x >= width || head.y < 0 || head.y >= height) {
            return true;
        }
        LinkedList<Point> body = snake.getBody();   // 现在 LinkedList 已导入，可以正常解析
        for (int i = 1; i < snake.getLength(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void restart(int width, int height, int unitSize) {
        if (speedBoostTimer != null) speedBoostTimer.stop();
        if (effectTimer != null) effectTimer.stop();
        if (invincibleTimer != null) invincibleTimer.stop();
        isSpeedBoostActive = false;
        isInvincible = false;
        activeEffect = "";
        startGame(width, height, unitSize);
    }

    public void togglePause() {
        if (gameState.isRunning()) {
            gameState.setPaused(!gameState.isPaused());
            panel.repaint();
        }
    }

    public void changeDirection(char newDir) {
        if (gameState.isRunning() && !gameState.isPaused()) {
            char currentDir = snake.getDirection();
            if ((newDir == 'U' && currentDir != 'D') ||
                    (newDir == 'D' && currentDir != 'U') ||
                    (newDir == 'L' && currentDir != 'R') ||
                    (newDir == 'R' && currentDir != 'L')) {
                snake.setNextDirection(newDir);
            }
        }
    }

    public boolean isSpeedBoostActive() { return isSpeedBoostActive; }
}