package com.snakegame.model;

public class GameState {
    private boolean running;
    private boolean paused;
    private int score;

    public GameState() {
        this.running = false;
        this.paused = false;
        this.score = 0;
    }

    public void reset() {
        this.running = true;
        this.paused = false;
        this.score = 0;
    }

    public void addScore() {
        this.score++;
    }

    public void addScore(int amount) {
        this.score += amount;
        if (this.score < 0) this.score = 0;
    }

    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public int getScore() { return score; }
}