// src/main/java/com/example/dinogame/ForestThemeFactory.java
package com.example.dinogame;

import javafx.scene.image.Image;

import java.util.Objects;

public class ForestThemeFactory implements ThemeFactory {
    private String themeName;
    private Image spriteSheet;

    public ForestThemeFactory() {
        this.themeName = "forest";
        this.spriteSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprite3.png")));
    }

    @Override
    public String getThemeName() {
        return themeName;
    }

    @Override
    public Image getSpriteSheet() {
        return spriteSheet;
    }
}