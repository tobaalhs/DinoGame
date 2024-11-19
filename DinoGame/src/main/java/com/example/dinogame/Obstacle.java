package com.example.dinogame;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

public interface Obstacle extends Cloneable {
    ImageView getImageView();
    void reset(double startX);
    double getWidth();
    double getHeight();
    Rectangle2D getCollisionBounds();
    Obstacle clone();
    void setBehavior(ObstacleBehavior behavior);
    void performMove();
    ObstacleBehavior getBehavior();
    void updateCollisionBounds();
}