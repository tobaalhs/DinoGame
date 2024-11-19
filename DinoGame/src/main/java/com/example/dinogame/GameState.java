package com.example.dinogame;

public class GameState {
    private static GameState instance;
    private boolean isGameOver;
    private boolean isPaused;
    private boolean isPlaying;
    private double currentScore;
    private double highScore;
    private double gameSpeed;
    private static final double initialGameSpeed = 5.0;
    private static final double speedIncrement = 0.001;
    private boolean canReset;

    private GameState() {
        resetGame();
    }

    public static synchronized GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public void resetGame() {
        isGameOver = false;
        isPaused = false;
        isPlaying = false;
        currentScore = 0;
        gameSpeed = initialGameSpeed;
        canReset = false;

    }

    public void updateGameSpeed() {
        if (!isPaused && isPlaying && !isGameOver) {
            gameSpeed += speedIncrement;
        }
    }

    public void setGameOver() {
        if (!isGameOver) {  // Solo ejecutar una vez
            isGameOver = true;
            isPlaying = false;
            if (currentScore > highScore) {
                highScore = currentScore;
            }
        }
    }

    // Nuevos métodos para el control de reset
    public boolean canReset() {
        return canReset;
    }

    public void setCanReset(boolean canReset) {
        this.canReset = canReset;
    }

    public static void resetInstance() {
        if (instance != null) {
            double previousHighScore = instance.getHighScore();
            instance = new GameState();
            instance.setHighScore(previousHighScore); // Mantener el high score
        }
    }

    // Getters y setters existentes...
    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public double getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(double score) {
        this.currentScore = score;
    }

    public double getHighScore() {
        return highScore;
    }

    public void setHighScore(double highScore) {
        this.highScore = highScore;
    }

    public double getGameSpeed() {
        return gameSpeed;
    }
}