package com.snakegame.model;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Random;

public class Food {
    private Point position;
    private final Random random;

    public Food() {
        this.random = new Random();
        this.position = new Point(0, 0);
    }

    public void spawn(int width, int height, int unitSize, LinkedList<Point> snakeBody) {
        boolean valid;
        int foodX, foodY;
        do {
            valid = true;
            foodX = random.nextInt(width / unitSize) * unitSize;
            foodY = random.nextInt(height / unitSize) * unitSize;
            for (Point p : snakeBody) {
                if (p.x == foodX && p.y == foodY) {
                    valid = false;
                    break;
                }
            }
        } while (!valid);
        position = new Point(foodX, foodY);
    }

    public Point getPosition() { return position; }
}
