package com.snakegame.model;

import java.awt.Point;
import java.awt.Color;
import java.util.LinkedList;

public class Snake {
    private final LinkedList<Point> body;
    private char direction;
    private char nextDirection;
    private int growthPending;

    public Snake() {
        this.body = new LinkedList<Point>();
        this.direction = 'R';
        this.nextDirection = 'R';
        this.growthPending = 0;
    }

    public void init(int startX, int startY, int unitSize) {
        body.clear();
        body.add(new Point(startX, startY));
        body.add(new Point(startX - unitSize, startY));
        body.add(new Point(startX - 2 * unitSize, startY));
        direction = 'R';
        nextDirection = 'R';
        growthPending = 0;
    }

    public void moveAndGrow(Point newHead, boolean ate) {
        body.addFirst(newHead);
        if (!ate && growthPending == 0) {
            body.removeLast();
        } else if (growthPending > 0) {
            growthPending--;
        }
    }

    public void addGrowth(int amount) {
        this.growthPending += amount;
    }

    public void removeTail() {
        if (body.size() > 3) {
            body.removeLast();
        }
    }

    public Color getBodyColor(int index, int totalLength) {
        float ratio = (float) index / totalLength;
        if (index == 0) {
            return new Color(80, 255, 80);
        } else {
            int r = (int)(50 + 205 * ratio);
            int g = (int)(150 - 70 * ratio);
            int b = (int)(50 + 100 * (1 - ratio));
            return new Color(r, g, b);
        }
    }

    public LinkedList<Point> getBody() { return body; }
    public Point getHead() { return body.getFirst(); }
    public char getDirection() { return direction; }
    public void setDirection(char dir) { this.direction = dir; }
    public char getNextDirection() { return nextDirection; }
    public void setNextDirection(char nextDir) { this.nextDirection = nextDir; }
    public int getLength() { return body.size(); }
}