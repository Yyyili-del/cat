package com.snakegame.model;

import java.awt.Point;
import java.util.LinkedList;

public class Snake {
    private final LinkedList<Point> body;
    private char direction;
    private char nextDirection;

    public Snake() {
        this.body = new LinkedList<>();
        this.direction = 'R';
        this.nextDirection = 'R';
    }

    public void init(int startX, int startY, int unitSize) {
        body.clear();
        body.add(new Point(startX, startY));
        body.add(new Point(startX - unitSize, startY));
        body.add(new Point(startX - 2 * unitSize, startY));
        direction = 'R';
        nextDirection = 'R';
    }

    public LinkedList<Point> getBody() { return body; }
    public Point getHead() { return body.getFirst(); }
    public char getDirection() { return direction; }
    public char getNextDirection() { return nextDirection; }
    public void setDirection(char dir) { this.direction = dir; }
    public void setNextDirection(char nextDir) { this.nextDirection = nextDir; }

    public void addHead(Point newHead) { body.addFirst(newHead); }
    public void removeTail() { body.removeLast(); }
    public int getLength() { return body.size(); }
}
