package com.snakegame;

import com.snakegame.model.*;
import com.snakegame.view.GamePanel;
import com.snakegame.controller.*;
import javax.swing.*;
import java.awt.*;

public class SnakeGameMain {
    @SuppressWarnings("unused")
    static void main(String[] args) {   // 改回 args，不使用下划线
        SwingUtilities.invokeLater(() -> {
            // 创建模型
            Snake snake = new Snake();
            Food food = new Food();
            GameState gameState = new GameState();

            // 创建视图
            GamePanel panel = new GamePanel(snake, food, gameState);

            // 创建控制器
            GameController controller = new GameController(snake, food, gameState, panel);

            // 键盘监听
            panel.addKeyListener(new KeyHandler(controller, () -> {
                controller.restart(GamePanel.WIDTH, GamePanel.HEIGHT, GamePanel.UNIT_SIZE);
                panel.repaint();
            }));

            // 创建按钮面板
            JPanel buttonPanel = createButtonPanel(controller, gameState, panel);

            // 主窗口
            JFrame frame = new JFrame("贪吃蛇游戏");
            frame.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.CENTER);
            frame.add(buttonPanel, BorderLayout.SOUTH);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            controller.startGame(GamePanel.WIDTH, GamePanel.HEIGHT, GamePanel.UNIT_SIZE);
            panel.requestFocusInWindow();
        });
    }

    private static JPanel createButtonPanel(GameController controller, GameState gameState, GamePanel panel) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.DARK_GRAY);

        JButton restartButton = new JButton("重新开始");
        restartButton.setFocusable(false);
        restartButton.addActionListener(_ -> {
            controller.restart(GamePanel.WIDTH, GamePanel.HEIGHT, GamePanel.UNIT_SIZE);
            panel.repaint();
        });

        JButton pauseButton = new JButton("暂停");
        pauseButton.setFocusable(false);
        pauseButton.addActionListener(_ -> {
            controller.togglePause();
            pauseButton.setText(gameState.isPaused() ? "继续" : "暂停");
            panel.repaint();
            panel.requestFocusInWindow();
        });

        buttonPanel.add(restartButton);
        buttonPanel.add(pauseButton);
        return buttonPanel;
    }
}