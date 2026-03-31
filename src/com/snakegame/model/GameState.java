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

    // Getters and Setters（仅保留实际使用的）
    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public int getScore() { return score; }
    public void addScore() { this.score++; }

    // 移除未使用的 setScore(int) 方法
    // 移除逻辑错误的 reset(int score) 方法
}