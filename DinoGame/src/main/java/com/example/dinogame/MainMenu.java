package com.example.dinogame;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import java.io.InputStream;

public class MainMenu {
    private Scene menuScene;
    private DinoGame game;
    AudioPlayer audioPlayer = AudioPlayer.getInstance();

    public MainMenu(Stage primaryStage) {

        GameState.getInstance().resetGame();
        this.game = new DinoGame();

        //fuente
        try {
            InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");
            if (is == null) {
                System.out.println("Error: No se pudo encontrar el archivo de fuente");
                return;
            }
            Font.loadFont(is, 10);
        } catch (Exception e) {
            System.out.println("Error al cargar la fuente: " + e.getMessage());
            e.printStackTrace();
        }

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        // Imagen del título
        Image titleImage = new Image(getClass().getResourceAsStream("/sprite.png"));
        ImageView titleImageView = new ImageView(titleImage);
        titleImageView.setViewport(new Rectangle2D(1514, 2, 88, 94));
        titleImageView.setFitHeight(150);
        titleImageView.setFitWidth(150);
        titleImageView.setPreserveRatio(true);

        // botnes de temas
        Button desertButton = createThemeButton("Desierto", primaryStage, new DesertThemeFactory());
        Button moonButton = createThemeButton("Luna", primaryStage, new MoonThemeFactory());
        Button forestButton = createThemeButton("Bosque", primaryStage, new ForestThemeFactory());

        root.getChildren().addAll(titleImageView, desertButton, moonButton, forestButton);
        menuScene = new Scene(root, 800, 350);
        menuScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    private Button createThemeButton(String text, Stage primaryStage, ThemeFactory themeFactory) {
        Button button = new Button(text);

        String baseStyle =
                "-fx-font-family: 'Press Start 2P';" +
                        "-fx-font-size: 20px;" +
                        "-fx-background-color: #535353;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-background-radius: 5px;";

        String hoverStyle =
                "-fx-font-family: 'Press Start 2P';" +
                        "-fx-font-size: 20px;" +
                        "-fx-background-color: #666666;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 10px 20px;" +
                        "-fx-background-radius: 5px;";

        button.setStyle(baseStyle);

        // animaciones de escala
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        // hover
        button.setOnMouseEntered(e -> {
            button.setStyle(hoverStyle);
            scaleUp.playFromStart();
        });

        button.setOnMouseExited(e -> {
            button.setStyle(baseStyle);
            scaleDown.playFromStart();
        });

        button.setOnMousePressed(e -> scaleDown.playFromStart());

        button.setOnMouseReleased(e -> {
            if (button.isHover()) {
                scaleUp.playFromStart();
            }
        });

        button.setOnAction(e -> {
            ScaleTransition clickTransition = new ScaleTransition(Duration.millis(100), button);
            clickTransition.setToX(0.9);
            clickTransition.setToY(0.9);
            clickTransition.play();
            audioPlayer.playJumpSound();

            clickTransition.setOnFinished(event -> {
                double previousHighScore = GameState.getInstance().getHighScore();

                GameState.resetInstance();
                ScoreCounter.resetInstance();
                ObstacleManager.resetInstance();

                GameState.getInstance().setHighScore(previousHighScore);
                GameState.getInstance().resetGame();
                GameState.getInstance().setPlaying(true);

                game = new DinoGame();
                game.startGame(primaryStage, themeFactory);
            });
        });

        return button;
    }

    public Scene getMenuScene() {
        return menuScene;
    }
}