package com.example.dinogame;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class DynamicBehavior implements ObstacleBehavior {
    private final Random random = new Random();
    private boolean hasStartedAnimation = false;

    @Override
    public void move(Obstacle obstacle) {
        // Verificar si es que el obstaculo salio de la ventana
        if (obstacle.getImageView().getScene() != null && !hasStartedAnimation) {
            Pane root = (Pane) obstacle.getImageView().getScene().getRoot();
            checkAndAnimate(obstacle, root);
            hasStartedAnimation = true;
        }
    }

    public void checkAndAnimate(Obstacle obstacle, Pane root) {
        if (obstacle.getImageView().getX() <= 750) {
            updateHitboxPosition(obstacle, root);
            return;
        }

        // Posición del obstáculo
        double startY = obstacle.getImageView().getY();
        double targetY;

        // Lógica de grupo de cactus
        if (obstacle instanceof SmallCactus) {
            double currentX = obstacle.getImageView().getX();
            List<Obstacle> cactusGroup = findNearbyCactus(root, currentX);

            if (random.nextDouble() < 0.3) {
                targetY = startY - 75;
                // se anima un grupo de cactus
                for (Obstacle groupMember : cactusGroup) {
                    animateObstacle(groupMember, root, groupMember.getImageView().getY(), targetY);
                }
            }
        } else if (obstacle instanceof Pterosaurio) {
            if (random.nextDouble() < 0.5) {
                targetY = startY + (random.nextBoolean() ? 70 : -70);
                animateObstacle(obstacle, root, startY, targetY);
            }
        }
    }

    private List<Obstacle> findNearbyCactus(Pane root, double currentX) {
        List<Obstacle> cactusGroup = new ArrayList<>();
        double groupThreshold = 100; // Distancia máxima para considerar cactus del mismo grupo

        // Buscar cactus cerca
        for (javafx.scene.Node node : root.getChildren()) {
            if (node.getProperties().get("obstacle") instanceof SmallCactus) {
                Obstacle otherCactus = (Obstacle) node.getProperties().get("obstacle");
                double otherX = otherCactus.getImageView().getX();

                // Si está dentro del rango de agrupación
                if (Math.abs(currentX - otherX) <= groupThreshold) {
                    cactusGroup.add(otherCactus);
                }
            }
        }

        return cactusGroup;
    }

    private void animateObstacle(Obstacle obstacle, Pane root, double startY, double targetY) {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        Duration.ZERO,
                        event -> {
                            obstacle.getImageView().setY(startY);
                            updateHitboxPosition(obstacle, root);
                        }
                ),
                new javafx.animation.KeyFrame(
                        Duration.millis(300),
                        new javafx.animation.KeyValue(
                                obstacle.getImageView().yProperty(),
                                targetY,
                                javafx.animation.Interpolator.EASE_BOTH
                        )
                )
        );

        timeline.getKeyFrames().add(
                new javafx.animation.KeyFrame(
                        Duration.millis(16),
                        event -> updateHitboxPosition(obstacle, root)
                )
        );

        timeline.setCycleCount(1);
        timeline.setOnFinished(event -> {
            obstacle.getImageView().setY(targetY);
            updateHitboxPosition(obstacle, root);
        });

        timeline.play();
    }

    private void updateHitboxPosition(Obstacle obstacle, Pane root) {
        for (javafx.scene.Node node : root.getChildren()) {
            if (node instanceof Rectangle && node.getProperties().get("obstacle") == obstacle) {
                Rectangle obstacleHitbox = (Rectangle) node;
                obstacleHitbox.setX(obstacle.getImageView().getX());
                obstacleHitbox.setY(obstacle.getImageView().getY());
                obstacle.updateCollisionBounds();
            }
        }
    }
}