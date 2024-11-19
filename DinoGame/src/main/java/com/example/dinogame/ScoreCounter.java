package com.example.dinogame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.paint.Color;

public class ScoreCounter {
    private static ScoreCounter instance;
    private final GameState gameState = GameState.getInstance();
    private int score = 0;
    private int highScore = 0;
    private final Label scoreLabel;
    private final Label highScoreLabel;
    private final Timeline scoreTimeline;
    private AudioPlayer audioPlayer = AudioPlayer.getInstance();

    private ScoreCounter(Pane root) {
        this.highScore = (int) gameState.getHighScore();
        // fuente
        Font font = Font.loadFont(getClass().getResourceAsStream("/PressStart2P-Regular.ttf"), 14);

        //puntaje mas alto
        highScoreLabel = new Label("HI " + String.format("%05d", highScore));
        highScoreLabel.setFont(font);
        highScoreLabel.setStyle("-fx-text-fill: black;");
        highScoreLabel.setLayoutX(550);
        highScoreLabel.setLayoutY(20);

        //puntaje actual
        scoreLabel = new Label(String.format("%05d", score));
        scoreLabel.setFont(font);
        scoreLabel.setStyle("-fx-text-fill: black;");
        scoreLabel.setLayoutX(700);
        scoreLabel.setLayoutY(20);

        root.getChildren().addAll(highScoreLabel, scoreLabel);

        // incremento del puntaje
        scoreTimeline = new Timeline(new KeyFrame(Duration.millis(70), e -> {
            if (!gameState.isPaused() && gameState.isPlaying() && !gameState.isGameOver()) {
                incrementScore();
            }
        }));
        scoreTimeline.setCycleCount(Timeline.INDEFINITE);
        updateHighScoreDisplay();
    }

    public static ScoreCounter getInstance(Pane root) {
        if (instance == null) {
            instance = new ScoreCounter(root);
        }
        if (!root.getChildren().contains(instance.scoreLabel)) {
            root.getChildren().addAll(instance.scoreLabel, instance.highScoreLabel);
        }
        return instance;
    }

    public static void resetInstance() {
        if (instance != null) {
            // Guardar putnaje maximo
            GameState.getInstance().setHighScore(instance.getHighScore());
            instance.stop();
            instance = null;
        }
    }

    public void start() {
        score = 0;
        updateScoreDisplay();
        scoreTimeline.play();
    }


    public void stop() {
        scoreTimeline.stop();
        if (score > highScore) {
            highScore = score;
            gameState.setHighScore(highScore);
            updateHighScoreDisplay();
        }
        gameState.setCurrentScore(score);
        gameState.setGameOver();

        Pane root = (Pane) scoreLabel.getScene().getRoot();
        changeBackgroundColor(root, Color.WHITE, Color.BLACK);
    }

    private void changeBackgroundColor(Pane root, Color backgroundColor, Color textColor) {
        root.setStyle("-fx-background-color: " + toHexString(backgroundColor) + ";");
        scoreLabel.setStyle("-fx-text-fill: " + toHexString(textColor) + ";");
        highScoreLabel.setStyle("-fx-text-fill: " + toHexString(textColor) + ";");
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }



    private void incrementScore() {
        int previousScore = score;
        score += 1;
        updateScoreDisplay();
        //cambiar fondo a los 1k puntos
        if (score == 1000) {
            Pane root = (Pane) scoreLabel.getScene().getRoot();
            changeBackgroundColor(root, Color.BLACK, Color.WHITE);
        }

        // Verificar si se alcanzo un multiplo de 100
        if (score % 100 == 0 && score != 0) {
            audioPlayer.playPointSound();
        }

        if (score > highScore) {
            highScore = score;
            gameState.setHighScore(highScore);
            updateHighScoreDisplay();
        }

        gameState.setCurrentScore(score);
    }

    private void updateScoreDisplay() {
        scoreLabel.setText(String.format("%05d", score));
    }

    private void updateHighScoreDisplay() {
        highScoreLabel.setText("HI " + String.format("%05d", highScore));
    }

    public void resetScore() {
        score = 0;
        updateScoreDisplay();
    }

    public int getScore() {
        return score;
    }

    public int getHighScore() {
        return highScore;
    }


}