import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    // 游戏窗口尺寸
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    // 格子大小（像素）
    private static final int UNIT_SIZE = 20;
    // 游戏循环延迟（毫秒）
    private static final int DELAY = 100;

    // 蛇的身体坐标（x, y）使用LinkedList方便头插尾删
    private final LinkedList<Point> snake;
    // 食物坐标
    private Point food;
    // 当前移动方向
    private char direction;
    // 下一个移动方向（用于缓冲）
    private char nextDirection;
    // 游戏状态
    private boolean running;
    // 计时器
    private Timer timer;
    // 随机数生成器
    private final Random random;

    // 游戏得分
    private int score;
    // 是否暂停
    private boolean paused;

    public SnakeGame() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        snake = new LinkedList<>();
        random = new Random();
        direction = 'R'; // 初始向右
        nextDirection = 'R';
        running = false;
        paused = false;
        score = 0;

        // 创建重启按钮（改为局部变量）
        JButton restartButton = new JButton("重新开始");
        restartButton.setFocusable(false);
        restartButton.addActionListener(ignored -> restartGame());

        this.setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.add(restartButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        startGame();
    }

    // 开始/初始化游戏
    public void startGame() {
        // 初始化蛇：长度为3，位于屏幕中部偏左
        snake.clear();
        int startX = WIDTH / 2 / UNIT_SIZE * UNIT_SIZE;
        int startY = HEIGHT / 2 / UNIT_SIZE * UNIT_SIZE;
        snake.add(new Point(startX, startY));
        snake.add(new Point(startX - UNIT_SIZE, startY));
        snake.add(new Point(startX - 2 * UNIT_SIZE, startY));

        direction = 'R';
        nextDirection = 'R';
        running = true;
        paused = false;
        score = 0;
        spawnFood();
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        timer.start();
    }

    // 重新启动游戏（重置所有状态）
    public void restartGame() {
        startGame();
        repaint();
    }

    // 生成新食物，确保食物不在蛇身上
    public void spawnFood() {
        boolean valid = false;
        int foodX = 0;
        int foodY = 0;
        while (!valid) {
            foodX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
            foodY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            valid = true;
            for (Point p : snake) {
                if (p.x == foodX && p.y == foodY) {
                    valid = false;
                    break;
                }
            }
        }
        food = new Point(foodX, foodY);
    }

    // 移动蛇的核心逻辑
    public void move() {
        // 更新方向（禁止反向）
        direction = nextDirection;

        // 计算新头部坐标
        Point newHead = calculateNewHead();

        // 检查是否吃到食物
        boolean ate = newHead.equals(food);

        // 执行移动：插入新头，如果没吃到食物则移除尾
        snake.addFirst(newHead);
        if (!ate) {
            snake.removeLast();
        } else {
            // 吃到食物，增加分数，生成新食物
            score++;
            spawnFood();
        }
    }

    // 计算新头部位置
    private Point calculateNewHead() {
        Point head = snake.getFirst();
        int newX = head.x;
        int newY = head.y;

        switch (direction) {
            case 'U': // 上
                newY -= UNIT_SIZE;
                break;
            case 'D': // 下
                newY += UNIT_SIZE;
                break;
            case 'L': // 左
                newX -= UNIT_SIZE;
                break;
            case 'R': // 右
                newX += UNIT_SIZE;
                break;
            default:
                break;
        }

        return new Point(newX, newY);
    }

    // 检查碰撞（墙壁或自身）
    public boolean checkCollisions() {
        Point head = snake.getFirst();

        // 检查墙壁碰撞
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            return true;
        }

        // 检查自身碰撞（从索引1开始，因为头部会与自身重叠）
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                return true;
            }
        }
        return false;
    }

    // 游戏主循环更新（由Timer触发）
    @Override
    public void actionPerformed(ActionEvent ignored) {
        if (running && !paused) {
            move();
            if (checkCollisions()) {
                running = false;
                timer.stop();
            }
        }
        repaint(); // 重绘界面
    }

    // 绘制游戏画面
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // 绘制网格（可选，视觉辅助）
            g.setColor(Color.DARK_GRAY);
            for (int i = 0; i < WIDTH / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, WIDTH, i * UNIT_SIZE);
            }

            // 绘制食物
            g.setColor(Color.RED);
            g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);

            // 绘制蛇
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                // 蛇头用更亮的绿色
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
                // 蛇身边界
                g.setColor(Color.BLACK);
                g.drawRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
            }

            // 显示分数
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + score, WIDTH - metrics.stringWidth("Score: " + score) - 10, 30);

            // 显示暂停提示
            if (paused) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Monospaced", Font.BOLD, 30));
                String pauseText = "PAUSED";
                FontMetrics fm = getFontMetrics(g.getFont());
                int x = (WIDTH - fm.stringWidth(pauseText)) / 2;
                int y = HEIGHT / 2;
                g.drawString(pauseText, x, y);
            }
        } else {
            // 游戏结束界面
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        // 绘制背景半透明效果
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 显示游戏结束文字
        g.setColor(Color.RED);
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        String gameOverText = "GAME OVER";
        int x1 = (WIDTH - metrics1.stringWidth(gameOverText)) / 2;
        g.drawString(gameOverText, x1, HEIGHT / 2 - 40);

        // 显示最终得分
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 30));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String scoreText = "Score: " + score;
        int x2 = (WIDTH - metrics2.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, x2, HEIGHT / 2 + 20);

        // 显示重启提示
        g.setFont(new Font("Monospaced", Font.PLAIN, 18));
        String restartMsg = "Press SPACE to restart";
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        int x3 = (WIDTH - metrics3.stringWidth(restartMsg)) / 2;
        g.drawString(restartMsg, x3, HEIGHT / 2 + 80);
    }

    // 键盘控制
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            // 处理暂停（仅在游戏中有效）
            if (running && key == KeyEvent.VK_P) {
                paused = !paused;
                repaint();
                return;
            }

            // 游戏未运行时，按空格重新开始
            if (!running) {
                if (key == KeyEvent.VK_SPACE) {
                    restartGame();
                }
                return;
            }

            // 如果暂停，不接受方向改变（但可以取消暂停已经处理了）
            if (paused) {
                return;
            }

            // 方向控制，禁止反向逻辑
            switch (key) {
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        nextDirection = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        nextDirection = 'D';
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        nextDirection = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        nextDirection = 'R';
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // 创建并显示游戏窗口（Java 25 允许省略 public 修饰符）
    static void main(String[] ignored) {
        JFrame frame = new JFrame("贪吃蛇游戏");
        SnakeGame gamePanel = new SnakeGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
