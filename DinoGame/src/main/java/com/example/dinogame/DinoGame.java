package com.example.dinogame;

import javafx.animation.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Shape;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.InputStream;
import java.util.*;

public class DinoGame extends Application {
    private int widthbackground = 800;
    private ThemeFactory themeFactory;
    private AudioPlayer audioPlayer;
    private Text pauseText;
    private Timeline breathingAnimation;
    private final double cactusSpacing = 40.0;
    private final Random random = new Random();
    private Pane gamePane;
    private ImageView dinoSprite;
    private ObstacleManager obstacleManager;
    private final List<Obstacle> activeObstacles = new ArrayList<>();
    private final double obstacleSpawnInterval = 2000;
    private double lastObstacleSpawnTime = 0;
    private final GameState gameState = GameState.getInstance();
    private ScoreCounter scoreCounter;

    private int currentFrame = 0;
    private static final int dinoFrameWidth = 88;
    private static final int dinoFrameHeight = 94;
    private static final int dinoStartX = 1514;
    private static final int dinoStartY = 2;
    private static final int dinoCrouchWidth = 118;
    private static final int dinoCrouchX = 1866;
    private static final int dinoCrouchY = 2;
    private static final int dinoJumpFrameX = 1338;

    private boolean isCrouching = false;
    private boolean wantsToCrouch = false;
    private static final int crouchFrameCount = 2;
    private static final int frameCount = 2;

    private double initialJumpPower = -17;
    private double jumpDecay = 0.7;
    private double maxGravity = 2;
    private double normalGravity = 0.8;
    private double fastFallGravity = 1.6;
    private static final double groundY = 200;
    private double maxFallSpeed = 20;
    private static final double maxLimit = 2.0;

    private static final double moonInitialJumpPower = -11;
    private static final double moonJumpDecay = 0.35;
    private static final double moonMaxGravity = 0.1;
    private static final double moonNormalGravity = 0.3;
    private static final double moonFastFallGravity = 0.8;
    private static final double moonMaxFallSpeed = 10;

    private double positionY = groundY;
    private double velocityY = 0;
    private boolean isJumping = false;
    private boolean isOnGround = true;
    private boolean isFastFalling = false;

    private final int groundHeight = 26;
    private final List<ImageView> groundTiles = new ArrayList<>();
    private final int cloudX = 166;
    private final int cloudY = 2;
    private final int cloudWidth = 92;
    private final int cloudHeight = 26;

    @Override
    public void start(Stage primaryStage) {
        MainMenu mainMenu = new MainMenu(primaryStage);
        primaryStage.setTitle("DinoGame");
        primaryStage.setScene(mainMenu.getMenuScene());
        primaryStage.show();
    }

    public void startGame(Stage primaryStage, ThemeFactory themeFactory) {
        setupGameEnvironment(themeFactory);
        setupGameScene(primaryStage);
        setupAnimations();
        setupControls(primaryStage.getScene());
    }

    private void setupGameEnvironment( ThemeFactory themeFactory) {
        this.themeFactory = themeFactory;
        widthbackground = 800;
        if (themeFactory.getThemeName().equals("moon")) {
            setupMoonPhysics();
        } else {
            widthbackground = 800;
        }
        initializeGameComponents();
    }

    private void setupMoonPhysics() {
        widthbackground = 6400;
        initialJumpPower = moonInitialJumpPower;
        jumpDecay = moonJumpDecay;
        maxGravity = moonMaxGravity;
        normalGravity = moonNormalGravity;
        fastFallGravity = moonFastFallGravity;
        maxFallSpeed = moonMaxFallSpeed;
    }

    private void initializeGameComponents() {
        audioPlayer = AudioPlayer.getInstance();
        gameState.resetGame();
        gameState.setPlaying(true);
        Image spriteSheet = themeFactory.getSpriteSheet();
        ObstacleManager.resetInstance();
        ObstacleFactory obstacleFactory = new GameObstacleFactory(spriteSheet);
        obstacleManager = ObstacleManager.getInstance(obstacleFactory);
        setupDinoSprite(spriteSheet);
    }

    private void setupDinoSprite(Image spriteSheet) {
        dinoSprite = new ImageView(spriteSheet);
        dinoSprite.setViewport(new Rectangle2D(dinoStartX, dinoStartY, dinoFrameWidth, dinoFrameHeight));
        dinoSprite.setX(100);
        dinoSprite.setY(positionY);
    }

    private Pane createGameRoot() {
        gamePane = new Pane();
        setupBackground(gamePane);
        return gamePane;
    }

    private void setupGameScene(Stage primaryStage) {
        Pane root = createGameRoot();
        setupPauseText(root);
        createGround(root, themeFactory.getSpriteSheet());
        createClouds(root, themeFactory.getSpriteSheet());
        setupScoreCounter(root);
        root.getChildren().add(dinoSprite);
        Scene scene = new Scene(root, 800, 330);
        primaryStage.setScene(scene);
    }

    private void setupScoreCounter(Pane root){
        scoreCounter = ScoreCounter.getInstance(root);
        scoreCounter.resetScore();
        scoreCounter.start();
    }

    private void setupBackground(Pane root) {
        ImageView backgroundImageView1 = new ImageView();
        ImageView backgroundImageView2 = new ImageView();
        if (themeFactory.getThemeName().equals("forest")) {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/forest.png")));
            backgroundImageView1.setImage(backgroundImage);
            backgroundImageView1.setFitWidth(800);
            backgroundImageView1.setFitHeight(330);
            backgroundImageView1.setX(0);

            backgroundImageView2.setImage(backgroundImage);
            backgroundImageView2.setFitWidth(800);
            backgroundImageView2.setFitHeight(330);
            backgroundImageView2.setX(800);

            root.getChildren().addAll(backgroundImageView1, backgroundImageView2);
        } else if (themeFactory.getThemeName().equals("desert")) {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/desert.png")));
            backgroundImageView1.setImage(backgroundImage);
            backgroundImageView1.setFitWidth(800);
            backgroundImageView1.setFitHeight(330);
            backgroundImageView1.setX(0);

            backgroundImageView2.setImage(backgroundImage);
            backgroundImageView2.setFitWidth(800);
            backgroundImageView2.setFitHeight(330);
            backgroundImageView2.setX(800);

            root.getChildren().addAll(backgroundImageView1, backgroundImageView2);
        } else if (themeFactory.getThemeName().equals("moon")) {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/moon.png")));
            backgroundImageView1.setImage(backgroundImage);
            backgroundImageView1.setFitWidth(6400);
            backgroundImageView1.setFitHeight(330);
            backgroundImageView1.setX(0);

            backgroundImageView2.setImage(backgroundImage);
            backgroundImageView2.setFitWidth(6400);
            backgroundImageView2.setFitHeight(330);
            backgroundImageView2.setX(6400);

            root.getChildren().addAll(backgroundImageView1, backgroundImageView2);
        } else {
            root.setStyle("-fx-background-color: white;");
        }

        setupBackgroundAnimation(backgroundImageView1, backgroundImageView2);
    }

    private void setupPauseText(Pane root) {
        pauseText = new Text("En pausa \n\n   ||");
        pauseText.setStyle("-fx-font-family: 'Press Start 2P'; -fx-font-size: 20px;");
        pauseText.setX(20);
        pauseText.setY(30);
        pauseText.setVisible(false);
        root.getChildren().add(pauseText);
        setupBreathingAnimation();
    }

    private void setupBreathingAnimation() {
        breathingAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(pauseText.scaleXProperty(), 1.0),
                        new KeyValue(pauseText.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(pauseText.scaleXProperty(), 1.2),
                        new KeyValue(pauseText.scaleYProperty(), 1.2))
        );
        breathingAnimation.setAutoReverse(true);
        breathingAnimation.setCycleCount(Animation.INDEFINITE);
    }

    private void setupAnimations() {
        setupRunAnimation();
        setupJumpAnimation();
        setupObstacleAnimation();
        setupSpeedUpdateAnimation();
        setupGroundAnimation();
    }

    private void setupRunAnimation() {
        Timeline runAnimation = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            if (isGameActive() && isOnGround) {
                updateRunningSprite();
            }
        }));
        runAnimation.setCycleCount(Timeline.INDEFINITE);
        runAnimation.play();
    }

    private void setupJumpAnimation() {
        Timeline jumpAnimation = new Timeline(new KeyFrame(Duration.millis(16.66), e -> updateJumpingSprite()));
        jumpAnimation.setCycleCount(Timeline.INDEFINITE);
        jumpAnimation.play();
    }

    private void updateRunningSprite() {
        if (!isCrouching) {
            int xOffset = dinoStartX + (currentFrame % frameCount) * dinoFrameWidth;
            dinoSprite.setViewport(new Rectangle2D(xOffset, dinoStartY, dinoFrameWidth, dinoFrameHeight));
        } else {
            int xOffset = dinoCrouchX + (currentFrame % crouchFrameCount) * dinoCrouchWidth;
            dinoSprite.setViewport(new Rectangle2D(xOffset, dinoCrouchY, dinoCrouchWidth, dinoFrameHeight));
        }
        currentFrame = (currentFrame + 1) % frameCount;
    }

    private void updateJumpingSprite() {
        if (!gameState.isPaused() && gameState.isPlaying() && !gameState.isGameOver()) {
            if (isJumping) {
                double currentGravity;
                if (Math.abs(velocityY) < maxLimit && velocityY < 0) {
                    currentGravity = maxGravity;
                    velocityY *= jumpDecay;
                } else if (isFastFalling) {
                    currentGravity = fastFallGravity;
                } else {
                    currentGravity = normalGravity;
                }
                velocityY += currentGravity;

                if (velocityY > maxFallSpeed) {
                    velocityY = maxFallSpeed;
                }
                positionY += velocityY;

                if (positionY >= groundY) {
                    positionY = groundY;
                    velocityY = 0;
                    isJumping = false;
                    isOnGround = true;
                    isFastFalling = false;

                    if (wantsToCrouch) {
                        isCrouching = true;
                        dinoSprite.setViewport(new Rectangle2D(dinoCrouchX, dinoCrouchY, dinoCrouchWidth, 94));
                    } else {
                        dinoSprite.setViewport(new Rectangle2D(dinoStartX, dinoStartY, dinoFrameWidth, 94));
                    }
                } else {
                    isOnGround = false;
                    dinoSprite.setViewport(new Rectangle2D(dinoJumpFrameX, dinoStartY, dinoFrameWidth, dinoFrameHeight));
                }

                dinoSprite.setY(positionY);
            }
        }
    }

    private boolean isGameActive() {
        return !gameState.isPaused() && gameState.isPlaying() && !gameState.isGameOver();
    }

    private void setupControls(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (!gameState.isGameOver() && !gameState.isPaused() && gameState.isPlaying()) {
                if ((event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W ||
                        event.getCode() == KeyCode.SPACE) && isOnGround && !isJumping) {
                    isJumping = true;
                    isOnGround = false;
                    velocityY = initialJumpPower;
                    isCrouching = false;
                    isFastFalling = false;
                    audioPlayer.playJumpSound();
                } else if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) {
                    wantsToCrouch = true;
                    if (isOnGround) {
                        isCrouching = true;
                    } else if (isJumping) {
                        isFastFalling = true;
                        if (velocityY < 5) {
                            velocityY = 5;
                        }
                    }
                }
            }

            if (event.getCode() == KeyCode.ESCAPE && !gameState.isGameOver()) {
                gameState.setPaused(!gameState.isPaused());
                pauseText.setVisible(gameState.isPaused());

                handlePauseState();

                if (gameState.isPaused()) {
                    pauseText.setTranslateX(pauseText.getBoundsInLocal().getWidth() / 2);
                    pauseText.setTranslateY(pauseText.getBoundsInLocal().getHeight() / 2);
                    breathingAnimation.play();
                } else {
                    breathingAnimation.stop();
                    pauseText.setScaleX(1.0);
                    pauseText.setScaleY(1.0);
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) {
                wantsToCrouch = false;
                isCrouching = false;
            }

            if (event.getCode() == KeyCode.SPACE && gameState.isGameOver()) {
                gameState.setCanReset(true);
            }
        });
    }

    private void createGround(Pane root, Image spriteSheet) {
        int[] groundXPositions = {2, 402, 802, 1202, 1602, 2002};
        int groundYPosition = 104;
        int groundTileWidth = 400;

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int randomGroundIndex = random.nextInt(groundXPositions.length);
            ImageView groundTile = new ImageView(spriteSheet);
            groundTile.setViewport(new Rectangle2D(groundXPositions[randomGroundIndex], groundYPosition, groundTileWidth, groundHeight));

            groundTile.setX(i * groundTileWidth);
            groundTile.setY(groundY + 70);
            groundTiles.add(groundTile);
            root.getChildren().add(groundTile);
        }

        Timeline groundAnimation = new Timeline(new KeyFrame(Duration.millis(14), e -> {
            if (!gameState.isPaused() && gameState.isPlaying()) {
                for (ImageView tile : groundTiles) {
                    tile.setX(tile.getX() - gameState.getGameSpeed());
                    if (tile.getX() <= -groundTileWidth) {
                        tile.setX(groundTileWidth * (groundTiles.size() - 1));
                        int newRandomIndex = random.nextInt(groundXPositions.length);
                        tile.setViewport(new Rectangle2D(groundXPositions[newRandomIndex], groundYPosition, groundTileWidth, groundHeight));
                    }
                }
            }
        }));
        groundAnimation.setCycleCount(Timeline.INDEFINITE);
        groundAnimation.play();
    }

    private void createClouds(Pane root, Image spriteSheet) {
        Random random = new Random();
        final Runnable[] spawnCloudWithRandomDelay = new Runnable[1];

        spawnCloudWithRandomDelay[0] = () -> {
            if (!gameState.isGameOver()) {
                ImageView cloud = new ImageView(spriteSheet);
                cloud.setViewport(new Rectangle2D(cloudX, cloudY, cloudWidth, cloudHeight));

                double dinoStartX = 800 + random.nextInt(200);
                double dinoStartY = 30 + random.nextInt(70);

                cloud.setX(dinoStartX);
                cloud.setY(dinoStartY);

                root.getChildren().addFirst(cloud);

                Timeline cloudAnimation = new Timeline(new KeyFrame(Duration.millis(16), e -> {
                    if (!gameState.isPaused() && gameState.isPlaying()) {
                        cloud.setX(cloud.getX() - (gameState.getGameSpeed() * 0.5)); // mitad de velocidad
                        if (cloud.getX() + cloudWidth < 0) {
                            root.getChildren().remove(cloud);
                        }
                    }
                }));
                cloudAnimation.setCycleCount(Timeline.INDEFINITE);
                cloudAnimation.play();

                PauseTransition delay = new PauseTransition(Duration.seconds(3 + random.nextInt(5)));
                delay.setOnFinished(event -> spawnCloudWithRandomDelay[0].run());
                delay.play();
            }
        };

        spawnCloudWithRandomDelay[0].run();
    }

    private void handlePauseState() {
        if (gameState.isPaused()) {
            // Pausar las animaciones
            for (Obstacle obstacle : activeObstacles) {
                if (obstacle instanceof Pterosaurio) {
                    ((Pterosaurio) obstacle).stopAnimation();
                }
            }
        } else {
            // seguir con las animaciones
            for (Obstacle obstacle : activeObstacles) {
                if (obstacle instanceof Pterosaurio) {
                    ((Pterosaurio) obstacle).resumeAnimation();
                }
            }
        }
    }


    private void setupBackgroundAnimation(ImageView backgroundImageView1, ImageView backgroundImageView2) {
        if (backgroundImageView1.getImage() == null || backgroundImageView2.getImage() == null) return;

        Timeline backgroundAnimation = new Timeline(new KeyFrame(Duration.millis(7.5), e -> {
            if (!gameState.isPaused() && gameState.isPlaying()) {
                double gameSpeed = gameState.getGameSpeed();
                backgroundImageView1.setX(backgroundImageView1.getX() - gameSpeed);
                backgroundImageView2.setX(backgroundImageView2.getX() - gameSpeed);

                if (backgroundImageView1.getX() <= -widthbackground) {
                    backgroundImageView1.setX(backgroundImageView2.getX() + widthbackground);
                }
                if (backgroundImageView2.getX() <= -widthbackground) {
                    backgroundImageView2.setX(backgroundImageView1.getX() + widthbackground);
                }
            }
        }));

        backgroundAnimation.setCycleCount(Timeline.INDEFINITE);
        backgroundAnimation.play();
    }



    private void updateObstacles(Pane root) {
        double currentTime = System.currentTimeMillis();

        // generar obstaculos
        if (currentTime - lastObstacleSpawnTime >= obstacleSpawnInterval) {
            Obstacle firstObstacle = obstacleManager.createRandomObstacle();
            boolean isCactus = firstObstacle.getImageView().getY() > 150;

            if (isCactus) {
                int groupSize = random.nextInt(3) + 1;
                double baseX = 800;

                ImageView firstView = firstObstacle.getImageView();
                firstView.setX(baseX);
                activeObstacles.add(firstObstacle);
                root.getChildren().add(firstView);
                addHitbox(root, firstObstacle);

                for (int i = 1; i < groupSize; i++) {
                    Obstacle additionalCactus = obstacleManager.createRandomObstacle();
                    if (additionalCactus.getImageView().getY() > 150) {
                        ImageView obstacleView = additionalCactus.getImageView();
                        obstacleView.setX(baseX + (i * cactusSpacing));
                        activeObstacles.add(additionalCactus);
                        root.getChildren().add(obstacleView);
                        addHitbox(root, additionalCactus);
                    }
                }
                lastObstacleSpawnTime = currentTime + (groupSize - 1) * 200;
            } else {
                firstObstacle.getImageView().setX(800);
                activeObstacles.add(firstObstacle);
                root.getChildren().add(firstObstacle.getImageView());
                addHitbox(root, firstObstacle);
                lastObstacleSpawnTime = currentTime;
            }
        }

        // actualizar pos del obstáculo
        Iterator<Obstacle> iterator = activeObstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obstacle = iterator.next();
            ImageView obstacleView = obstacle.getImageView();
            double gameSpeed = gameState.getGameSpeed();
            obstacleView.setX(obstacleView.getX() - gameSpeed);

            obstacle.performMove();

            obstacle.updateCollisionBounds();

            // hitbox
            root.getChildren().stream()
                    .filter(node -> node instanceof Rectangle &&
                            node.getProperties().get("obstacle") == obstacle)
                    .map(node -> (Rectangle) node)
                    .forEach(hitbox -> {
                        Rectangle2D bounds = obstacle.getCollisionBounds();
                        hitbox.setX(bounds.getMinX());
                        hitbox.setY(bounds.getMinY());
                        hitbox.setWidth(bounds.getWidth());
                        hitbox.setHeight(bounds.getHeight());
                    });

            // eliminar obs fuera de pantalla
            if (obstacleView.getX() + obstacleView.getBoundsInLocal().getWidth() < 0) {
                root.getChildren().remove(obstacleView);
                root.getChildren().removeIf(node ->
                        node instanceof Rectangle &&
                                node.getProperties().get("obstacle") == obstacle);
                iterator.remove();
            }
        }
    }

    private void setupSpeedUpdateAnimation() {
        Timeline speedUpdateTimeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            if (isGameActive()) {
                gameState.updateGameSpeed();
            }
        }));
        speedUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        speedUpdateTimeline.play();
    }

    private void setupObstacleAnimation() {
        Timeline obstacleAnimation = new Timeline(new KeyFrame(Duration.millis(7.55), e -> {
            if (isGameActive()) {
                updateObstacles(gamePane);
                checkCollisions(gamePane);
            }
        }));
        obstacleAnimation.setCycleCount(Timeline.INDEFINITE);
        obstacleAnimation.play();
    }

    private void setupGroundAnimation() {
        Timeline groundAnimation = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            if (isGameActive()) {
                for (ImageView tile : groundTiles) {
                    tile.setX(tile.getX() - gameState.getGameSpeed());
                    if (tile.getX() <= -400) {
                        tile.setX(400 * (groundTiles.size() - 1));
                    }
                }
            }
        }));
        groundAnimation.setCycleCount(Timeline.INDEFINITE);
        groundAnimation.play();
    }

    private void addHitbox(Pane root, Obstacle obstacle) {
        Rectangle obstacleHitbox = new Rectangle(
                obstacle.getImageView().getX(),
                obstacle.getImageView().getY(),
                obstacle.getCollisionBounds().getWidth(),
                obstacle.getCollisionBounds().getHeight()
        );
        obstacleHitbox.setFill(null);
        obstacleHitbox.getProperties().put("obstacle", obstacle);
        root.getChildren().add(obstacleHitbox);
    }

    private void checkCollisions(Pane root) {
        Rectangle2D dinoBounds = calculateDinoHitbox();
        updateVisualHitbox(root, dinoBounds);
        checkObstacleCollisions(dinoBounds, root);
    }

    private Rectangle2D calculateDinoHitbox() {
        double dinoHitboxX = dinoSprite.getX();
        double dinoHitboxY = dinoSprite.getY();
        double dinoHitboxWidth;
        double dinoHitboxHeight;

        if (isCrouching) {
            dinoHitboxX += 10;
            dinoHitboxY += 45;
            dinoHitboxWidth = dinoCrouchWidth - 20;
            dinoHitboxHeight = dinoFrameHeight - 55;
        } else {
            dinoHitboxX += 20;
            dinoHitboxY += 10;
            dinoHitboxWidth = dinoFrameWidth - 40;
            dinoHitboxHeight = dinoFrameHeight - 30;
        }

        return new Rectangle2D(dinoHitboxX, dinoHitboxY, dinoHitboxWidth, dinoHitboxHeight);
    }

    private void updateVisualHitbox(Pane root, Rectangle2D dinoBounds) {
        root.getChildren().removeIf(node ->
                node instanceof Rectangle &&
                        ((Shape)node).getStroke() != null &&
                        ((Shape)node).getStroke().equals(Color.RED)
        );

        Rectangle hitboxRect = new Rectangle(
                dinoBounds.getMinX(),
                dinoBounds.getMinY(),
                dinoBounds.getWidth(),
                dinoBounds.getHeight()
        );
        hitboxRect.setFill(null);
        root.getChildren().add(hitboxRect);
    }

    private void checkObstacleCollisions(Rectangle2D dinoBounds, Pane root) {
        for (Obstacle obstacle : activeObstacles) {
            if (dinoBounds.intersects(obstacle.getCollisionBounds())) {
                handleCollision(root);
                break;
            }
        }
    }

    private void handleCollision(Pane root) {
        if (!gameState.isGameOver()) {
            stopObstacleAnimations();
            audioPlayer.playDeathSound();
            gameState.setGameOver();
            scoreCounter.stop();
            updateDinoDeathSprite();
            createGameOverUI(root);
        }
    }

    private void stopObstacleAnimations() {
        activeObstacles.stream()
                .filter(obstacle -> obstacle instanceof Pterosaurio)
                .forEach(obstacle -> ((Pterosaurio) obstacle).stopAnimation());
    }

    private void updateDinoDeathSprite() {
        dinoSprite.setViewport(new Rectangle2D(1690, 2, dinoFrameWidth + 4, dinoFrameHeight));
        if (isCrouching) {
            dinoSprite.setY(groundY);
        }
    }

    private void createGameOverUI(Pane root) {
        loadCustomFont();
        Button restartButton = createGameButton("Reiniciar", 400 - 50, 165);
        Button menuButton = createGameButton("Menú Principal", 310, 205);
        Text gameOverText = createGameOverText();

        root.getChildren().addAll(restartButton, menuButton, gameOverText);
        setupGameOverAnimation(gameOverText);
        setupButtonHandlers(restartButton, menuButton, root, gameOverText);
    }

    private void loadCustomFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/PressStart2P-Regular.ttf");
            if (is != null) {
                Font.loadFont(is, 10);
            }
        } catch (Exception e) {
            System.out.println("Error al cargar la fuente: " + e.getMessage());
        }
    }

    private Button createGameButton(String text, double x, double y) {
        Button button = new Button(text);
        button.setStyle("-fx-font-family: 'Press Start 2P';-fx-background-color: #535252; -fx-text-fill: white; -fx-font-size: 16px;");
        button.setLayoutX(x);
        button.setLayoutY(y);
        return button;
    }

    private Text createGameOverText() {
        Text gameOverText = new Text("Game Over");
        gameOverText.setStyle("-fx-font-family: 'Press Start 2P';-fx-font-size: 24px; -fx-fill: #FF0000;");
        gameOverText.setLayoutX(400-100);
        gameOverText.setLayoutY(140);
        gameOverText.setTranslateX(gameOverText.getBoundsInLocal().getWidth() / 2);
        gameOverText.setTranslateY(gameOverText.getBoundsInLocal().getHeight() / 2);
        return gameOverText;
    }

    private void setupGameOverAnimation(Text gameOverText) {
        Timeline gameOverAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(gameOverText.scaleXProperty(), 1.0),
                        new KeyValue(gameOverText.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(gameOverText.scaleXProperty(), 1.2),
                        new KeyValue(gameOverText.scaleYProperty(), 1.2)
                )
        );
        gameOverAnimation.setAutoReverse(true);
        gameOverAnimation.setCycleCount(Animation.INDEFINITE);
        gameOverAnimation.play();
    }

    private void setupButtonHandlers(Button restartButton, Button menuButton, Pane root, Text gameOverText) {
        restartButton.setOnAction(event -> handleRestart(root, gameOverText, restartButton, menuButton));
        menuButton.setOnAction(event -> handleMainMenu());
    }

    private void handleRestart(Pane root, Text gameOverText, Button restartButton, Button menuButton) {
        root.getChildren().removeAll(gameOverText, restartButton, menuButton);
        resetGameState();
        clearObstacles(root);
        scoreCounter.start();
    }

    private void resetGameState() {
        gameState.resetGame();
        gameState.setPlaying(true);
        resetPhysics();
        resetDinoSprite();
    }

    private void resetPhysics() {
        positionY = groundY;
        velocityY = 0;
        isJumping = false;
        isOnGround = true;
        isFastFalling = false;
        isCrouching = false;
        wantsToCrouch = false;
    }

    private void resetDinoSprite() {
        dinoSprite.setY(groundY);
        dinoSprite.setViewport(new Rectangle2D(dinoStartX, dinoStartY, dinoFrameWidth, dinoFrameHeight));
    }

    private void clearObstacles(Pane root) {
        // del obstaculos
        new ArrayList<>(activeObstacles).forEach(obstacle -> {
            root.getChildren().remove(obstacle.getImageView());
            // del hitboxes
            root.getChildren().removeIf(node ->
                    node instanceof Rectangle &&
                            node.getProperties().get("obstacle") == obstacle
            );
        });

        // hitboxes debug
        root.getChildren().removeIf(node ->
                node instanceof Rectangle &&
                        ((Rectangle)node).getStroke() != null &&
                        (((Rectangle)node).getStroke().equals(Color.RED) ||
                                ((Rectangle)node).getStroke().equals(Color.BLUE))
        );

        activeObstacles.clear();
        lastObstacleSpawnTime = 0;
    }

    private void handleMainMenu() {
        Stage stage = (Stage) dinoSprite.getScene().getWindow();
        MainMenu mainMenu = new MainMenu(stage);
        stage.setScene(mainMenu.getMenuScene());
    }

    public static void main(String[] args) {
        launch(args);
    }
}