package com.snakegame.controller;

import com.snakegame.model.*;
import com.snakegame.view.GamePanel;
import javax.swing.*;
import java.awt.Point;

public class GameController {
    private final Snake snake;
    private final Food food;
    private final GameState gameState;
    private final GamePanel panel;
    private Timer timer;

    private static final int DELAY = 100;

    // 唯一构造函数，接收 GamePanel 引用
    public GameController(Snake snake, Food food, GameState gameState, GamePanel panel) {
        this.snake = snake;
        this.food = food;
        this.gameState = gameState;
        this.panel = panel;
    }

    public void startGame(int width, int height, int unitSize) {
        int startX = width / 2 / unitSize * unitSize;
        int startY = height / 2 / unitSize * unitSize;
        snake.init(startX, startY, unitSize);
        gameState.reset();
        food.spawn(width, height, unitSize, snake.getBody());

        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, _ -> gameLoop(width, height, unitSize));
        timer.start();
    }

    private void gameLoop(int width, int height, int unitSize) {
        if (gameState.isRunning() && !gameState.isPaused()) {
            move(width, height, unitSize);
            if (checkCollisions(width, height)) {
                gameState.setRunning(false);
                timer.stop();
            }
            panel.repaint();   // 刷新画面
        }
    }

    private void move(int width, int height, int unitSize) {
        snake.setDirection(snake.getNextDirection());
        Point newHead = calculateNewHead(unitSize);
        boolean ate = newHead.equals(food.getPosition());

        snake.addHead(newHead);
        if (!ate) {
            snake.removeTail();
        } else {
            gameState.addScore();
            food.spawn(width, height, unitSize, snake.getBody());
        }
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
        }
        return new Point(newX, newY);
    }

    private boolean checkCollisions(int width, int height) {
        Point head = snake.getHead();

        if (head.x < 0 || head.x >= width || head.y < 0 || head.y >= height) {
            return true;
        }

        for (int i = 1; i < snake.getLength(); i++) {
            if (head.equals(snake.getBody().get(i))) {
                return true;
            }
        }
        return false;
    }

    public void restart(int width, int height, int unitSize) {
        startGame(width, height, unitSize);
    }

    public void togglePause() {
        if (gameState.isRunning()) {
            gameState.setPaused(!gameState.isPaused());
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
}