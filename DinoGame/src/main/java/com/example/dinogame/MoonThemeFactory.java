package com.example.dinogame;

import javafx.scene.image.Image;

import java.util.Objects;

public class MoonThemeFactory implements ThemeFactory {
    private String themeName;
    private Image spriteSheet;

    public MoonThemeFactory() {
        this.themeName = "moon";
        this.spriteSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprite2.png")));
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