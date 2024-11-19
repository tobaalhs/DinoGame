package com.example.dinogame;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class SmallCactus extends BaseObstacle {
    public SmallCactus(Image spriteSheet) {
        this.width = 33.5;
        this.height = 70;
        this.imageView = new ImageView(spriteSheet);

        imageView.setViewport(new Rectangle2D(446, 2, width, height));
        imageView.setX(800);
        imageView.setY(GROUND_Y + 30);
    }
}
