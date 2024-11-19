package com.example.dinogame;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Pterosaurio extends BaseObstacle {
    private final int frameCount = 2;
    private int currentFrame = 0;
    private Timeline flapAnimation;
    private static final int frameWidth = 92;
    private static final int frameHeight = 80;
    private static final int spriteX = 260;
    private static final int spriteY = 2;

    public Pterosaurio(Image spriteSheet) {
        //frame inicial
        this.width = frameWidth;
        this.height = frameHeight;
        this.imageView = new ImageView(spriteSheet);

        imageView.setViewport(new Rectangle2D(spriteX, spriteY, width, height));
        imageView.setX(800);
        imageView.setY(GROUND_Y - 60);

        createFlapAnimation();
    }

    private void createFlapAnimation() {
        flapAnimation = new Timeline(
                new KeyFrame(Duration.millis(150), e -> {
                    currentFrame = (currentFrame + 1) % frameCount;
                    int frameX = spriteX + (currentFrame * frameWidth);
                    imageView.setViewport(new Rectangle2D(frameX, spriteY, width, height));
                })
        );
        flapAnimation.setCycleCount(Timeline.INDEFINITE);
        flapAnimation.play();
    }

    @Override
    public Rectangle2D getCollisionBounds() {
        double hitboxX = imageView.getX() + 15;
        double hitboxY = imageView.getY() + 25;
        double hitboxWidth = width - 15;
        double hitboxHeight = height - 10;

        return new Rectangle2D(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public void stopAnimation() {
        if (flapAnimation != null) {
            flapAnimation.stop();
        }
    }

    public void resumeAnimation() {
        if (flapAnimation != null) {
            flapAnimation.play();
        }
    }

    @Override
    public Obstacle clone() {
        Pterosaurio cloned = (Pterosaurio) super.clone();
        cloned.createFlapAnimation();
        return cloned;
    }
}