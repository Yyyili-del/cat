package com.snakegame;

import com.snakegame.model.*;
import com.snakegame.view.GamePanel;
import com.snakegame.controller.*;
import javax.swing.*;
import java.awt.*;

public class SnakeGameMain {
    @SuppressWarnings("unused")
    static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Snake snake = new Snake();
            Food food = new Food();
            GameState gameState = new GameState();
            GamePanel panel = new GamePanel(snake, food, gameState);

            // ✅ 关键：传入 panel 参数
            GameController controller = new GameController(snake, food, gameState, panel);

            panel.addKeyListener(new KeyHandler(controller, () -> {
                controller.restart(GamePanel.WIDTH, GamePanel.HEIGHT, GamePanel.UNIT_SIZE);
                panel.repaint();
            }));

            JButton restartButton = new JButton("重新开始");
            restartButton.setFocusable(false);
            restartButton.addActionListener(_ -> {
                controller.restart(GamePanel.WIDTH, GamePanel.HEIGHT, GamePanel.UNIT_SIZE);
                panel.repaint();
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.DARK_GRAY);
            buttonPanel.add(restartButton);

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
            panel.requestFocusInWindow();  // 确保面板获得焦点
        });
    }
}