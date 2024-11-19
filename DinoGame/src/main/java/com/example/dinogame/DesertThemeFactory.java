package com.example.dinogame;

import javafx.scene.image.Image;

import java.util.Objects;

public class DesertThemeFactory implements ThemeFactory {
    private String themeName;
    private Image spriteSheet;

    public DesertThemeFactory() {
        this.themeName = "desert";
        this.spriteSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprite.png")));
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