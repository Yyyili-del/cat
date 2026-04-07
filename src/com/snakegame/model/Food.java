package com.snakegame.model;

import java.awt.Point;
import java.util.*;

public class Food {
    private List<Point> foodList;
    private Random random;
    private static final int MAX_FOOD = 5;

    public Food() {
        this.foodList = new ArrayList<>();
        this.random = new Random();
    }

    public void spawn(int width, int height, int unitSize, LinkedList<Point> snakeBody) {
        foodList.clear();
        for (int i = 0; i < MAX_FOOD; i++) {
            Point newFood = generateValidFood(width, height, unitSize, snakeBody);
            if (newFood != null) {
                foodList.add(newFood);
            }
        }
    }

    private Point generateValidFood(int width, int height, int unitSize, LinkedList<Point> snakeBody) {
        int maxAttempts = 1000;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int foodX = random.nextInt(width / unitSize) * unitSize;
            int foodY = random.nextInt(height / unitSize) * unitSize;
            Point newFood = new Point(foodX, foodY);

            boolean collidesWithSnake = snakeBody.stream().anyMatch(p -> p.equals(newFood));
            boolean collidesWithFood = foodList.stream().anyMatch(p -> p.equals(newFood));

            if (!collidesWithSnake && !collidesWithFood) {
                return newFood;
            }
        }
        return null;
    }

    public Point checkFoodCollision(Point head) {
        for (Point food : foodList) {
            if (food.equals(head)) {
                return food;
            }
        }
        return null;
    }

    public void removeFood(Point food) {
        foodList.remove(food);
    }

    public List<Point> getFoodList() { return foodList; }
    public int getRemainingFoodCount() { return foodList.size(); }
}