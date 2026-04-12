package com.snakegame.model;

import java.awt.Point;

public class FoodItem {
    private Point position;
    private FoodType type;

    public FoodItem(Point position, FoodType type) {
        this.position = position;
        this.type = type;
    }

    public Point getPosition() { return position; }
    public FoodType getType() { return type; }
}