package com.snakegame.view;

import com.snakegame.model.*;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    public static final int UNIT_SIZE = 20;

    private Snake snake;
    private Food food;
    private GameState gameState;

    public GamePanel(Snake snake, Food food, GameState gameState) {
        this.snake = snake;
        this.food = food;
        this.gameState = gameState;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState.isRunning()) {
            drawGame(g);
        } else {
            drawGameOver(g);
        }
    }

    private void drawGame(Graphics g) {
        drawGrid(g);
        drawFood(g);
        drawSnake(g);
        drawScore(g);
        if (gameState.isPaused()) {
            drawPaused(g);
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i < WIDTH / UNIT_SIZE; i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, HEIGHT);
            g.drawLine(0, i * UNIT_SIZE, WIDTH, i * UNIT_SIZE);
        }
    }

    private void drawFood(Graphics g) {
        g.setColor(Color.RED);
        Point pos = food.getPosition();
        g.fillOval(pos.x, pos.y, UNIT_SIZE, UNIT_SIZE);
    }

    private void drawSnake(Graphics g) {
        for (int i = 0; i < snake.getLength(); i++) {
            Point p = snake.getBody().get(i);
            g.setColor(i == 0 ? Color.GREEN : new Color(45, 180, 0));
            g.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
        }
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        String scoreText = "Score: " + gameState.getScore();
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(scoreText, WIDTH - metrics.stringWidth(scoreText) - 10, 30);
    }

    private void drawPaused(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 30));
        String pauseText = "PAUSED";
        FontMetrics fm = getFontMetrics(g.getFont());
        int x = (WIDTH - fm.stringWidth(pauseText)) / 2;
        int y = HEIGHT / 2;
        g.drawString(pauseText, x, y);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        String gameOverText = "GAME OVER";
        FontMetrics fm1 = getFontMetrics(g.getFont());
        g.drawString(gameOverText, (WIDTH - fm1.stringWidth(gameOverText)) / 2, HEIGHT / 2 - 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 30));
        String scoreText = "Score: " + gameState.getScore();
        FontMetrics fm2 = getFontMetrics(g.getFont());
        g.drawString(scoreText, (WIDTH - fm2.stringWidth(scoreText)) / 2, HEIGHT / 2 + 20);

        g.setFont(new Font("Monospaced", Font.PLAIN, 18));
        String restartMsg = "Press SPACE to restart";
        FontMetrics fm3 = getFontMetrics(g.getFont());
        g.drawString(restartMsg, (WIDTH - fm3.stringWidth(restartMsg)) / 2, HEIGHT / 2 + 80);
    }
}