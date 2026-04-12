package com.snakegame.model;

import java.awt.Point;
import java.util.*;

public class Food {
    private List<FoodItem> foodList;
    private Random random;
    private static final int MAX_FOOD = 5;

    public Food() {
        this.foodList = new ArrayList<>();
        this.random = new Random();
    }

    public void spawn(int width, int height, int unitSize, LinkedList<Point> snakeBody) {
        foodList.clear();
        for (int i = 0; i < MAX_FOOD; i++) {
            FoodItem newFood = generateValidFood(width, height, unitSize, snakeBody);
            if (newFood != null) {
                foodList.add(newFood);
            }
        }
    }

    private FoodItem generateValidFood(int width, int height, int unitSize, LinkedList<Point> snakeBody) {
        int maxAttempts = 1000;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int foodX = random.nextInt(width / unitSize) * unitSize;
            int foodY = random.nextInt(height / unitSize) * unitSize;
            Point position = new Point(foodX, foodY);

            boolean collidesWithSnake = snakeBody.stream().anyMatch(p -> p.equals(position));
            boolean collidesWithFood = foodList.stream().anyMatch(f -> f.getPosition().equals(position));

            if (!collidesWithSnake && !collidesWithFood) {
                FoodType type = getRandomFoodType();
                return new FoodItem(position, type);
            }
        }
        return null;
    }

    private FoodType getRandomFoodType() {
        int rand = random.nextInt(100);
        if (rand < 70) return FoodType.NORMAL;
        if (rand < 85) return FoodType.GOLDEN;
        if (rand < 95) return FoodType.STAR;
        return FoodType.BOMB;
    }

    public FoodItem checkFoodCollision(Point head) {
        for (FoodItem food : foodList) {
            if (food.getPosition().equals(head)) {
                return food;
            }
        }
        return null;
    }

    public void removeFood(FoodItem food) {
        foodList.remove(food);
    }

    public List<FoodItem> getFoodList() { return foodList; }
    public int getRemainingFoodCount() { return foodList.size(); }
}