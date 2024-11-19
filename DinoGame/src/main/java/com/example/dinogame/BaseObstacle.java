package com.example.dinogame;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

public abstract class BaseObstacle implements Obstacle {
    protected ImageView imageView;
    protected double width;
    protected double height;
    protected final double GROUND_Y = 200;
    protected ObstacleBehavior behavior;
    protected Rectangle2D collisionBounds;

    public void setBehavior(ObstacleBehavior behavior) {
        this.behavior = behavior;
    }

    public ObstacleBehavior getBehavior() {
        return behavior;
    }

    public void performMove() {
        if (behavior != null) {
            behavior.move(this);
        }
    }

    @Override
    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void reset(double startX) {
        imageView.setX(startX);
        updateCollisionBounds();
    }

    @Override
    public Rectangle2D getCollisionBounds() {
        if (collisionBounds == null) {
            updateCollisionBounds();
        }
        return collisionBounds;
    }

    @Override
    public void updateCollisionBounds() {
        // Ajustar colision
        double adjustedX = imageView.getX();
        double adjustedY = imageView.getY();
        double adjustedWidth = width;
        double adjustedHeight = height;

        // ajuste de la hitbox
        if (this instanceof SmallCactus) {
            adjustedX += width * 0.1;
            adjustedWidth *= 0.8;
            adjustedHeight *= 0.9;
        }

        this.collisionBounds = new Rectangle2D(
                adjustedX,
                adjustedY,
                adjustedWidth,
                adjustedHeight
        );
    }

    @Override
    public Obstacle clone() {
        try {
            BaseObstacle cloned = (BaseObstacle) super.clone();
            cloned.imageView = new ImageView(imageView.getImage());
            cloned.imageView.setViewport(imageView.getViewport());
            cloned.imageView.setX(imageView.getX());
            cloned.imageView.setY(imageView.getY());
            // Actualizar bounds del clon
            cloned.updateCollisionBounds();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}