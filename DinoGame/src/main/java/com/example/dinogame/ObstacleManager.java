package com.example.dinogame;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import java.util.List;

public class ObstacleManager {
    private static ObstacleManager instance;
    private final ObstacleFactory factory;
    private Obstacle smallCactusPrototype;
    private Obstacle pterosaurioPrototype;

    private ObstacleManager(ObstacleFactory factory) {
        this.factory = factory;
        initializePrototypes();
    }

    public static ObstacleManager getInstance(ObstacleFactory factory) {
        if (instance == null || factory != instance.factory) {
            // crear una instancia nueva si es q no existe o si la factory es diferente
            instance = new ObstacleManager(factory);
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    private void initializePrototypes() {
        smallCactusPrototype = factory.createSmallCactus();
        pterosaurioPrototype = factory.createPterosaurio();
    }

    public Obstacle createRandomObstacle() {
        double randomValue = Math.random();
        Obstacle obstacle;
        if (randomValue < 0.65) {
            obstacle = smallCactusPrototype.clone();
        } else {
            obstacle = pterosaurioPrototype.clone();
        }

        //tradicional o dinamico
        if (Math.random() < 0.6) {
            obstacle.setBehavior(new TraditionalBehavior());
        } else {
            obstacle.setBehavior(new DynamicBehavior());
        }

        obstacle.performMove();
        return obstacle;
    }

}