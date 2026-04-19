package com.snakegame.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyHandler extends KeyAdapter {
    private final GameController controller;
    private final Runnable restartAction;

    public KeyHandler(GameController controller, Runnable restartAction) {
        this.controller = controller;
        this.restartAction = restartAction;
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_P:
                controller.togglePause();
                break;
            case KeyEvent.VK_SPACE:
                restartAction.run();
                break;
            case KeyEvent.VK_UP:
                controller.changeDirection('U');
                break;
            case KeyEvent.VK_DOWN:
                controller.changeDirection('D');
                break;
            case KeyEvent.VK_LEFT:
                controller.changeDirection('L');
                break;
            case KeyEvent.VK_RIGHT:
                controller.changeDirection('R');
                break;
            default:
                break;
        }
    }
}