package com.snakegame.view;

import com.snakegame.model.*;
import com.snakegame.controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.Random;

public class GamePanel extends JPanel {
    public static final int WIDTH = 600;
    public static final int HEIGHT = 600;
    public static final int UNIT_SIZE = 20;
    private static final String MONOSPACED_FONT = "Monospaced";

    private final Snake snake;
    private final Food food;
    private final GameState gameState;
    private GameController controller;
    private transient Image backgroundImage;   // 添加 transient
    private transient Image gameOverImage;     // 添加 transient
    private final Color backgroundColor1 = new Color(20, 30, 50);
    private final Color backgroundColor2 = new Color(10, 20, 40);
    private final Random random = new Random();

    public GamePanel(Snake snake, Food food, GameState gameState) {
        this.snake = snake;
        this.food = food;
        this.gameState = gameState;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        loadBackgroundImage();
        loadGameOverImage();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    private void loadBackgroundImage() {
        String[] imagePaths = {
                "/com/snakegame/resources/background.jpeg",
                "/com/snakegame/resources/background.png",
                "/com/snakegame/resources/bg.jpg"
        };
        for (String path : imagePaths) {
            try {
                URL imgUrl = getClass().getResource(path);
                if (imgUrl != null) {
                    backgroundImage = ImageIO.read(imgUrl);
                    backgroundImage = backgroundImage.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
                    return;
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void loadGameOverImage() {
        String[] imagePaths = {
                "/com/snakegame/resources/gameover.jpg",
                "/com/snakegame/resources/gameover.png",
                "/com/snakegame/resources/end.jpg",
                "/com/snakegame/resources/end.png",
                "/com/snakegame/resources/cover.jpg"
        };
        for (String path : imagePaths) {
            try {
                URL imgUrl = getClass().getResource(path);
                if (imgUrl != null) {
                    gameOverImage = ImageIO.read(imgUrl);
                    gameOverImage = gameOverImage.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
                    return;
                }
            } catch (Exception e) {
                // ignore
            }
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
        drawBackground(g);
        drawGrid(g);
        drawFoods(g);
        drawSnake(g);
        drawEffects(g);
        drawUI(g);
        if (gameState.isPaused()) {
            drawPaused(g);
        }
    }

    private void drawBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, null);
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
        } else {
            GradientPaint gradient = new GradientPaint(0, 0, backgroundColor1, 0, HEIGHT, backgroundColor2);
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            drawBackgroundDots(g2d);
        }
    }

    private void drawBackgroundDots(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 200; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g2d.fillOval(x, y, 2, 2);
        }
    }

    private void drawGrid(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(200, 200, 200, 40));
        for (int i = 0; i <= WIDTH / UNIT_SIZE; i++) {
            g2d.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, HEIGHT);
            g2d.drawLine(0, i * UNIT_SIZE, WIDTH, i * UNIT_SIZE);
        }
    }

    private void drawFoods(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        java.util.List<FoodItem> foodList = food.getFoodList();
        for (FoodItem item : foodList) {
            Point pos = item.getPosition();
            FoodType type = item.getType();
            Color typeColor = type.getColor();
            g2d.setColor(new Color(typeColor.getRed(), typeColor.getGreen(), typeColor.getBlue(), 100));
            g2d.fillOval(pos.x - 2, pos.y - 2, UNIT_SIZE + 4, UNIT_SIZE + 4);
            g2d.setColor(typeColor);

            if (type == FoodType.NORMAL) {
                g2d.fillOval(pos.x, pos.y, UNIT_SIZE, UNIT_SIZE);
                g2d.setColor(Color.WHITE);
                g2d.fillOval(pos.x + 6, pos.y + 6, 4, 4);
            } else if (type == FoodType.GOLDEN) {
                g2d.fillRect(pos.x + 2, pos.y + 2, UNIT_SIZE - 4, UNIT_SIZE - 4);
                g2d.setColor(new Color(255, 255, 200));
                g2d.drawLine(pos.x + UNIT_SIZE/2, pos.y + 2, pos.x + UNIT_SIZE/2, pos.y + UNIT_SIZE - 2);
                g2d.drawLine(pos.x + 2, pos.y + UNIT_SIZE/2, pos.x + UNIT_SIZE - 2, pos.y + UNIT_SIZE/2);
            } else if (type == FoodType.STAR) {
                g2d.fillRoundRect(pos.x, pos.y, UNIT_SIZE, UNIT_SIZE, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.drawLine(pos.x + UNIT_SIZE/2, pos.y + 3, pos.x + UNIT_SIZE/2, pos.y + UNIT_SIZE - 3);
                g2d.drawLine(pos.x + 3, pos.y + UNIT_SIZE/2, pos.x + UNIT_SIZE - 3, pos.y + UNIT_SIZE/2);
            } else if (type == FoodType.BOMB) {
                g2d.fillOval(pos.x, pos.y, UNIT_SIZE, UNIT_SIZE);
                g2d.setColor(Color.BLACK);
                g2d.drawLine(pos.x + 4, pos.y + 4, pos.x + UNIT_SIZE - 4, pos.y + UNIT_SIZE - 4);
                g2d.drawLine(pos.x + UNIT_SIZE - 4, pos.y + 4, pos.x + 4, pos.y + UNIT_SIZE - 4);
                g2d.setColor(Color.ORANGE);
                g2d.fillRect(pos.x + UNIT_SIZE/2 - 2, pos.y - 2, 4, 4);
            }

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 10));
            g2d.drawString(type.getSymbol(), pos.x + 6, pos.y + 14);
        }
    }

    private void drawEffects(Graphics g) {
        if (controller == null) return;
        String effect = controller.getActiveEffect();
        Point pos = controller.getEffectPosition();
        if (effect == null || pos == null) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (effect.equals("GOLDEN")) {
            drawGoldenEffect(g2d, pos);
        } else if (effect.equals("BOMB")) {
            drawBombEffect(g2d, pos);
        }
    }

    private void drawGoldenEffect(Graphics2D g2d, Point pos) {
        int centerX = pos.x + UNIT_SIZE / 2;
        int centerY = pos.y + UNIT_SIZE / 2;
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30 + System.currentTimeMillis() % 360);
            int x2 = centerX + (int) (Math.cos(angle) * 25);
            int y2 = centerY + (int) (Math.sin(angle) * 25);
            g2d.setColor(new Color(255, 215, 0, 150));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(centerX, centerY, x2, y2);
        }
        g2d.setColor(new Color(255, 215, 0, 200));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(centerX - 15, centerY - 15, 30, 30);
        g2d.drawOval(centerX - 22, centerY - 22, 44, 44);
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 + System.currentTimeMillis() % 360);
            int px = centerX + (int) (Math.cos(angle) * 18);
            int py = centerY + (int) (Math.sin(angle) * 18);
            g2d.setColor(new Color(255, 255, 100, 200));
            g2d.fillOval(px - 2, py - 2, 4, 4);
        }
        g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 16));
        g2d.setColor(new Color(255, 215, 0));
        String text = "+" + FoodType.GOLDEN.getGrowthValue() + " LENGTH!";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, centerX - fm.stringWidth(text) / 2, centerY - 28);
    }

    private void drawBombEffect(Graphics2D g2d, Point pos) {
        int centerX = pos.x + UNIT_SIZE / 2;
        int centerY = pos.y + UNIT_SIZE / 2;
        int radius = 15 + random.nextInt(10);
        for (int r = radius; r > 0; r -= 3) {
            int alpha = 200 - r * 10;
            if (alpha < 0) alpha = 0;
            if (alpha > 255) alpha = 255;
            g2d.setColor(new Color(255, 100, 0, alpha));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - r, centerY - r, r * 2, r * 2);
        }
        for (int i = 0; i < 20; i++) {
            double angle = Math.toRadians(random.nextInt(360));
            int distance = random.nextInt(30);
            int px = centerX + (int) (Math.cos(angle) * distance);
            int py = centerY + (int) (Math.sin(angle) * distance);
            int size = 3 + random.nextInt(4);
            g2d.setColor(new Color(255, 100 + random.nextInt(155), 0, 200));
            g2d.fillOval(px - size/2, py - size/2, size, size);
        }
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(random.nextInt(360));
            int distance = random.nextInt(40);
            int px = centerX + (int) (Math.cos(angle) * distance);
            int py = centerY + (int) (Math.sin(angle) * distance);
            g2d.setColor(new Color(80, 80, 80, 150));
            g2d.fillOval(px - 3, py - 3, 6, 6);
        }
        g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 16));
        g2d.setColor(Color.RED);
        String text = "-" + (-FoodType.BOMB.getGrowthValue()) + " LENGTH!";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, centerX - fm.stringWidth(text) / 2, centerY - 25);
        g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 24));
        g2d.setColor(Color.ORANGE);
        g2d.drawString("!", centerX - 5, centerY - 45);
    }

    private void drawSnake(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        java.util.LinkedList<Point> body = snake.getBody();
        int length = snake.getLength();
        for (int i = 0; i < length; i++) {
            Point p = body.get(i);
            g2d.setColor(snake.getBodyColor(i, length));
            g2d.fillRoundRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE, 10, 10);
            g2d.setColor(new Color(20, 80, 20));
            g2d.drawRoundRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE, 10, 10);
            if (i == 0) drawEyes(g2d, p);
        }
    }

    private void drawEyes(Graphics2D g2d, Point head) {
        g2d.setColor(Color.WHITE);
        int eyeSize = 5;
        char dir = snake.getDirection();
        if (dir == 'U') {
            g2d.fillOval(head.x + 4, head.y + 3, eyeSize, eyeSize);
            g2d.fillOval(head.x + 11, head.y + 3, eyeSize, eyeSize);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(head.x + 5, head.y + 4, 2, 2);
            g2d.fillOval(head.x + 12, head.y + 4, 2, 2);
        } else if (dir == 'D') {
            g2d.fillOval(head.x + 4, head.y + 12, eyeSize, eyeSize);
            g2d.fillOval(head.x + 11, head.y + 12, eyeSize, eyeSize);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(head.x + 5, head.y + 13, 2, 2);
            g2d.fillOval(head.x + 12, head.y + 13, 2, 2);
        } else if (dir == 'L') {
            g2d.fillOval(head.x + 3, head.y + 4, eyeSize, eyeSize);
            g2d.fillOval(head.x + 3, head.y + 11, eyeSize, eyeSize);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(head.x + 4, head.y + 5, 2, 2);
            g2d.fillOval(head.x + 4, head.y + 12, 2, 2);
        } else if (dir == 'R') {
            g2d.fillOval(head.x + 12, head.y + 4, eyeSize, eyeSize);
            g2d.fillOval(head.x + 12, head.y + 11, eyeSize, eyeSize);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(head.x + 13, head.y + 5, 2, 2);
            g2d.fillOval(head.x + 13, head.y + 12, 2, 2);
        }
    }

    private void drawUI(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(5, 5, 130, 70, 10, 10);
        g.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + gameState.getScore(), 12, 22);
        g.drawString("Length: " + snake.getLength(), 12, 42);
        if (controller != null && controller.isSpeedBoostActive()) {
            g2d.setColor(new Color(255, 200, 0, 200));
            g2d.fillRoundRect(WIDTH - 115, 5, 110, 30, 10, 10);
            g.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 12));
            g.setColor(Color.BLACK);
            g.drawString("⚡ SPEED BOOST!", WIDTH - 110, 25);
        }
        if (controller != null && controller.isInvincible()) {
            g2d.setColor(new Color(0, 255, 255, 180));
            g2d.fillRoundRect(WIDTH - 115, 40, 110, 25, 10, 10);
            g.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 11));
            g.setColor(Color.BLACK);
            g.drawString("🛡️ INVINCIBLE!", WIDTH - 110, 58);
        }
    }

    private void drawPaused(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 50));
        String text = "PAUSED";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(text, (WIDTH - fm.stringWidth(text)) / 2, HEIGHT / 2);
        g2d.setFont(new Font(MONOSPACED_FONT, Font.PLAIN, 16));
        String tip = "Press P to continue";
        fm = g2d.getFontMetrics();
        g2d.drawString(tip, (WIDTH - fm.stringWidth(tip)) / 2, HEIGHT / 2 + 50);
    }

    private void drawGameOver(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (gameOverImage != null) {
            g.drawImage(gameOverImage, 0, 0, WIDTH, HEIGHT, null);
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(new Color(255, 215, 0, 180));
            g2d.drawRoundRect(10, 10, WIDTH - 20, HEIGHT - 20, 20, 20);
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(new Color(255, 215, 0, 100));
            g2d.drawRoundRect(15, 15, WIDTH - 30, HEIGHT - 30, 15, 15);
            String gameOverText = "GAME OVER";
            g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 48));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (WIDTH - fm.stringWidth(gameOverText)) / 2;
            for (int i = 5; i > 0; i--) {
                g2d.setColor(new Color(255, 100, 50, 40 - i * 5));
                g2d.drawString(gameOverText, textX - i, HEIGHT / 2 - 55 - i);
                g2d.drawString(gameOverText, textX + i, HEIGHT / 2 - 55 + i);
            }
            g2d.setColor(new Color(255, 100, 80));
            g2d.drawString(gameOverText, textX, HEIGHT / 2 - 55);
            g2d.setColor(Color.WHITE);
            g2d.drawString(gameOverText, textX - 1, HEIGHT / 2 - 56);
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRoundRect(WIDTH / 2 - 140, HEIGHT / 2 - 15, 280, 120, 20, 20);
            g2d.setColor(new Color(255, 215, 0, 150));
            g2d.drawRoundRect(WIDTH / 2 - 140, HEIGHT / 2 - 15, 280, 120, 20, 20);
            g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 24));
            g2d.setColor(new Color(255, 215, 0));
            String scoreLabel = "SCORE";
            fm = g2d.getFontMetrics();
            g2d.drawString(scoreLabel, WIDTH / 2 - 50, HEIGHT / 2 + 15);
            g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 32));
            g2d.setColor(Color.WHITE);
            String scoreValue = String.valueOf(gameState.getScore());
            fm = g2d.getFontMetrics();
            g2d.drawString(scoreValue, WIDTH / 2 - fm.stringWidth(scoreValue) / 2, HEIGHT / 2 + 55);
            g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 18));
            g2d.setColor(new Color(200, 200, 255));
            String lengthText = "LENGTH: " + snake.getLength();
            fm = g2d.getFontMetrics();
            g2d.drawString(lengthText, WIDTH / 2 - fm.stringWidth(lengthText) / 2, HEIGHT / 2 + 90);
            String restartMsg = "▶ PRESS SPACE TO RESTART ◀";
            g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 16));
            boolean blink = (System.currentTimeMillis() / 500) % 2 == 0;
            g2d.setColor(blink ? new Color(100, 255, 150) : new Color(100, 200, 255));
            fm = g2d.getFontMetrics();
            g2d.drawString(restartMsg, (WIDTH - fm.stringWidth(restartMsg)) / 2, HEIGHT - 50);
        } else {
            drawDefaultGameOver(g2d);
        }
    }

    private void drawDefaultGameOver(Graphics2D g2d) {
        GradientPaint gameOverGradient = new GradientPaint(0, 0, new Color(10, 5, 20), 0, HEIGHT, new Color(30, 10, 40));
        g2d.setPaint(gameOverGradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        g2d.setColor(new Color(100, 50, 150, 50));
        for (int i = 0; i <= WIDTH / UNIT_SIZE; i++) {
            g2d.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, HEIGHT);
            g2d.drawLine(0, i * UNIT_SIZE, WIDTH, i * UNIT_SIZE);
        }
        g2d.setColor(new Color(255, 200, 100, 80));
        for (int i = 0; i < 150; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g2d.fillOval(x, y, 1 + random.nextInt(3), 1 + random.nextInt(3));
        }
        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(new Color(150, 50, 200, 150));
        g2d.drawRoundRect(10, 10, WIDTH - 20, HEIGHT - 20, 20, 20);
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(200, 100, 255, 100));
        g2d.drawRoundRect(15, 15, WIDTH - 30, HEIGHT - 30, 15, 15);
        String gameOverText = "GAME OVER";
        g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 52));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (WIDTH - fm.stringWidth(gameOverText)) / 2;
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(gameOverText, textX + 3, HEIGHT / 2 - 55 + 3);
        for (int i = 5; i > 0; i--) {
            g2d.setColor(new Color(200, 50, 100, 30 - i * 5));
            g2d.drawString(gameOverText, textX - i, HEIGHT / 2 - 55 - i);
            g2d.drawString(gameOverText, textX + i, HEIGHT / 2 - 55 + i);
        }
        GradientPaint textGradient = new GradientPaint(textX, HEIGHT / 2 - 65, new Color(255, 80, 120),
                textX + fm.stringWidth(gameOverText), HEIGHT / 2 - 45, new Color(255, 180, 80));
        g2d.setPaint(textGradient);
        g2d.drawString(gameOverText, textX, HEIGHT / 2 - 55);
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(WIDTH / 2 - 140, HEIGHT / 2 - 15, 280, 120, 20, 20);
        g2d.setColor(new Color(200, 100, 255, 100));
        g2d.drawRoundRect(WIDTH / 2 - 140, HEIGHT / 2 - 15, 280, 120, 20, 20);
        g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 24));
        g2d.setColor(new Color(255, 200, 100));
        String scoreLabel = "SCORE";
        fm = g2d.getFontMetrics();
        g2d.drawString(scoreLabel, WIDTH / 2 - 50, HEIGHT / 2 + 15);
        g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 32));
        g2d.setColor(Color.WHITE);
        String scoreValue = String.valueOf(gameState.getScore());
        fm = g2d.getFontMetrics();
        g2d.drawString(scoreValue, WIDTH / 2 - fm.stringWidth(scoreValue) / 2, HEIGHT / 2 + 55);
        g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 20));
        g2d.setColor(new Color(180, 180, 255));
        String lengthText = "LENGTH: " + snake.getLength();
        fm = g2d.getFontMetrics();
        g2d.drawString(lengthText, WIDTH / 2 - fm.stringWidth(lengthText) / 2, HEIGHT / 2 + 90);
        String restartMsg = "▶ PRESS SPACE TO RESTART ◀";
        g2d.setFont(new Font(MONOSPACED_FONT, Font.BOLD, 16));
        boolean blink = (System.currentTimeMillis() / 500) % 2 == 0;
        g2d.setColor(blink ? new Color(100, 255, 150) : new Color(100, 200, 255));
        fm = g2d.getFontMetrics();
        g2d.drawString(restartMsg, (WIDTH - fm.stringWidth(restartMsg)) / 2, HEIGHT - 50);
        drawGameOverSnakeIcon(g2d, WIDTH / 2 - 180, HEIGHT / 2 + 20);
        drawGameOverSnakeIcon(g2d, WIDTH / 2 + 140, HEIGHT / 2 + 20);
    }

    private void drawGameOverSnakeIcon(Graphics2D g2d, int x, int y) {
        int iconSize = 12;
        g2d.setColor(new Color(80, 255, 80));
        for (int i = 0; i < 5; i++) {
            g2d.fillRoundRect(x + i * iconSize, y, iconSize - 2, iconSize - 2, 5, 5);
        }
        g2d.setColor(Color.WHITE);
        g2d.fillOval(x + iconSize * 4 + 2, y + 2, 3, 3);
        g2d.fillOval(x + iconSize * 4 + 2, y + 7, 3, 3);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x + iconSize * 4 + 3, y + 3, 1, 1);
        g2d.fillOval(x + iconSize * 4 + 3, y + 8, 1, 1);
    }
}