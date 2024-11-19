package com.example.dinogame;

import javafx.scene.image.Image;

// Implementación concreta de la factory
public class GameObstacleFactory implements ObstacleFactory {
    private final Image spriteSheet;

    public GameObstacleFactory(Image spriteSheet) {
        this.spriteSheet = spriteSheet;
    }

    @Override
    public Obstacle createSmallCactus() {
        return new SmallCactus(spriteSheet);
    }
    @Override
    public Obstacle createPterosaurio() {
        return new Pterosaurio(spriteSheet);
    }
}
