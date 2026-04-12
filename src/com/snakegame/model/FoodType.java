package com.snakegame.model;

import java.awt.Color;

public enum FoodType {
    NORMAL("★", new Color(255, 80, 80), 1, 1),
    GOLDEN("●", new Color(255, 215, 0), 5, 2),
    STAR("⭐", new Color(200, 100, 255), 3, 1),
    BOMB("💣", new Color(80, 80, 80), -2, -1);

    private final String symbol;
    private final Color color;
    private final int scoreValue;
    private final int growthValue;

    FoodType(String symbol, Color color, int scoreValue, int growthValue) {
        this.symbol = symbol;
        this.color = color;
        this.scoreValue = scoreValue;
        this.growthValue = growthValue;
    }

    public String getSymbol() { return symbol; }
    public Color getColor() { return color; }
    public int getScoreValue() { return scoreValue; }
    public int getGrowthValue() { return growthValue; }
}