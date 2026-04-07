package com.snakegame.view;

import com.snakegame.model.*;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel {
    public static final int WIDTH = 600, HEIGHT = 600, UNIT_SIZE = 20;
    private final Snake snake;
    private final Food food;
    private final GameState gameState;
    private Image backgroundImage;

    public GamePanel(Snake snake, Food food, GameState gameState) {
        this.snake = snake;
        this.food = food;
        this.gameState = gameState;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        loadBackground();
    }

    private void loadBackground() {
        try {
            URL imgUrl = getClass().getResource("/com/snakegame/resources/background.jpg");
            if (imgUrl != null) {
                backgroundImage = ImageIO.read(imgUrl);
            }
        } catch (Exception e) {
            System.err.println("背景加载失败，使用默认背景");
        }
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
        // 绘制背景
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, null);
        } else {
            // 渐变背景
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(0, 0, new Color(20, 30, 50), 0, HEIGHT, new Color(10, 20, 40));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        }

        drawGrid(g);
        drawFoods(g);
        drawSnake(g);
        drawScoreAndLength(g);
        if (gameState.isPaused()) {
            drawPaused(g);
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(new Color(100, 100, 100, 50));
        for (int i = 0; i <= WIDTH / UNIT_SIZE; i++) {
            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, HEIGHT);
            g.drawLine(0, i * UNIT_SIZE, WIDTH, i * UNIT_SIZE);
        }
    }

    private void drawFoods(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Point pos : food.getFoodList()) {
            // 食物光晕效果
            g2d.setColor(new Color(255, 100, 100));
            g2d.fillOval(pos.x, pos.y, UNIT_SIZE, UNIT_SIZE);
            g2d.setColor(Color.RED);
            g2d.fillOval(pos.x + 2, pos.y + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(pos.x + 6, pos.y + 6, 4, 4);
        }
    }

    private void drawSnake(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int length = snake.getLength();
        for (int i = 0; i < length; i++) {
            Point p = snake.getBody().get(i);

            // 使用 Snake 类的 getBodyColor 方法获取颜色
            g2d.setColor(snake.getBodyColor(i, length));

            g2d.fillRoundRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE, 10, 10);
            g2d.setColor(new Color(20, 80, 20));
            g2d.drawRoundRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE, 10, 10);

            // 蛇头眼睛
            if (i == 0) {
                drawEyes(g2d, p);
            }
        }
    }

    private void drawEyes(Graphics2D g2d, Point head) {
        g2d.setColor(Color.WHITE);
        int eyeSize = 5;
        switch (snake.getDirection()) {
            case 'U':
                g2d.fillOval(head.x + 4, head.y + 3, eyeSize, eyeSize);
                g2d.fillOval(head.x + 11, head.y + 3, eyeSize, eyeSize);
                g2d.setColor(Color.BLACK);
                g2d.fillOval(head.x + 5, head.y + 4, 2, 2);
                g2d.fillOval(head.x + 12, head.y + 4, 2, 2);
                break;
            case 'D':
                g2d.fillOval(head.x + 4, head.y + 12, eyeSize, eyeSize);
                g2d.fillOval(head.x + 11, head.y + 12, eyeSize, eyeSize);
                g2d.setColor(Color.BLACK);
                g2d.fillOval(head.x + 5, head.y + 13, 2, 2);
                g2d.fillOval(head.x + 12, head.y + 13, 2, 2);
                break;
            case 'L':
                g2d.fillOval(head.x + 3, head.y + 4, eyeSize, eyeSize);
                g2d.fillOval(head.x + 3, head.y + 11, eyeSize, eyeSize);
                g2d.setColor(Color.BLACK);
                g2d.fillOval(head.x + 4, head.y + 5, 2, 2);
                g2d.fillOval(head.x + 4, head.y + 12, 2, 2);
                break;
            case 'R':
                g2d.fillOval(head.x + 12, head.y + 4, eyeSize, eyeSize);
                g2d.fillOval(head.x + 12, head.y + 11, eyeSize, eyeSize);
                g2d.setColor(Color.BLACK);
                g2d.fillOval(head.x + 13, head.y + 5, 2, 2);
                g2d.fillOval(head.x + 13, head.y + 12, 2, 2);
                break;
        }
    }

    private void drawScoreAndLength(Graphics g) {
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + gameState.getScore(), 10, 25);
        g.drawString("Length: " + snake.getLength(), 10, 50);
    }

    private void drawPaused(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        String text = "PAUSED";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (WIDTH - fm.stringWidth(text)) / 2, HEIGHT / 2);
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        String text = "GAME OVER";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (WIDTH - fm.stringWidth(text)) / 2, HEIGHT / 2 - 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 25));
        String scoreText = "Score: " + gameState.getScore() + "  Length: " + snake.getLength();
        fm = g.getFontMetrics();
        g.drawString(scoreText, (WIDTH - fm.stringWidth(scoreText)) / 2, HEIGHT / 2 + 20);

        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
        String restartMsg = "Press SPACE to restart";
        fm = g.getFontMetrics();
        g.drawString(restartMsg, (WIDTH - fm.stringWidth(restartMsg)) / 2, HEIGHT / 2 + 70);
    }
}