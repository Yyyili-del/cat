package com.snakegame;

import com.snakegame.model.*;
import com.snakegame.view.GamePanel;
import com.snakegame.controller.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SnakeGameMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final Snake snake = new Snake();
                final Food food = new Food();
                final GameState gameState = new GameState();
                final GamePanel panel = new GamePanel(snake, food, gameState);
                final GameController controller = new GameController(snake, food, gameState, panel);

                panel.setController(controller);
                panel.addKeyListener(new KeyHandler(controller, new Runnable() {
                    public void run() {
                        controller.restart(GamePanel.WIDTH, GamePanel.HEIGHT, GamePanel.UNIT_SIZE);
                        panel.repaint();
                    }
                }));

                JPanel buttonPanel = new JPanel();
                buttonPanel.setBackground(Color.DARK_GRAY);

                final JButton restartButton = new JButton("重新开始");
                restartButton.setFocusable(false);
                restartButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        controller.restart(GamePanel.WIDTH, GamePanel.HEIGHT, GamePanel.UNIT_SIZE);
                        panel.repaint();
                    }
                });

                final JButton pauseButton = new JButton("暂停");
                pauseButton.setFocusable(false);
                pauseButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        controller.togglePause();
                        pauseButton.setText(gameState.isPaused() ? "继续" : "暂停");
                        panel.repaint();
                        panel.requestFocusInWindow();
                    }
                });

                buttonPanel.add(restartButton);
                buttonPanel.add(pauseButton);

                JFrame frame = new JFrame("贪吃蛇游戏");
                frame.setLayout(new BorderLayout());
                frame.add(panel, BorderLayout.CENTER);
                frame.add(buttonPanel, BorderLayout.SOUTH);
                frame.pack();
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                controller.startGame(GamePanel.WIDTH, GamePanel.HEIGHT, GamePanel.UNIT_SIZE);
                panel.requestFocusInWindow();
            }
        });
    }
}