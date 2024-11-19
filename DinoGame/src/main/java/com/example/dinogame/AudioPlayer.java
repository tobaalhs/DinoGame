package com.example.dinogame;

import javax.sound.sampled.*;
import java.io.*;

public class AudioPlayer {
    private static AudioPlayer instance;
    private Clip jumpSound;
    private Clip deathSound;
    private Clip pointSound;

    private AudioPlayer() {
        try {
            //sonido de salto
            InputStream jumpAudioSrc = getClass().getResourceAsStream("/jump.wav");
            InputStream bufferedJumpIn = new BufferedInputStream(jumpAudioSrc);
            AudioInputStream jumpAudioStream = AudioSystem.getAudioInputStream(bufferedJumpIn);

            AudioFormat jumpFormat = jumpAudioStream.getFormat();
            DataLine.Info jumpInfo = new DataLine.Info(Clip.class, jumpFormat);

            jumpSound = (Clip) AudioSystem.getLine(jumpInfo);
            jumpSound.open(jumpAudioStream);

            //sonido de muerte
            InputStream deathAudioSrc = getClass().getResourceAsStream("/die.wav");
            InputStream bufferedDeathIn = new BufferedInputStream(deathAudioSrc);
            AudioInputStream deathAudioStream = AudioSystem.getAudioInputStream(bufferedDeathIn);

            AudioFormat deathFormat = deathAudioStream.getFormat();
            DataLine.Info deathInfo = new DataLine.Info(Clip.class, deathFormat);

            deathSound = (Clip) AudioSystem.getLine(deathInfo);
            deathSound.open(deathAudioStream);

            //sonido de punto
            InputStream pointAudioSrc = getClass().getResourceAsStream("/point.wav");
            InputStream bufferedPointIn = new BufferedInputStream(pointAudioSrc);
            AudioInputStream pointAudioStream = AudioSystem.getAudioInputStream(bufferedPointIn);

            AudioFormat pointFormat = pointAudioStream.getFormat();
            DataLine.Info pointInfo = new DataLine.Info(Clip.class, pointFormat);

            // Obtener y abrir el clip de punto
            pointSound = (Clip) AudioSystem.getLine(pointInfo);
            pointSound.open(pointAudioStream);

        } catch (Exception e) {
            System.out.println("Error al cargar los sonidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static AudioPlayer getInstance() {
        if (instance == null) {
            instance = new AudioPlayer();
        }
        return instance;
    }

    public void playJumpSound() {
        if (jumpSound != null) {
            if (jumpSound.isRunning()) {
                jumpSound.stop();
            }
            jumpSound.setFramePosition(0);
            jumpSound.start();
        }
    }

    public void playDeathSound() {
        if (deathSound != null) {
            if (deathSound.isRunning()) {
                deathSound.stop();
            }
            deathSound.setFramePosition(0);
            deathSound.start();
        }
    }

    public void playPointSound() {
        if (pointSound != null) {
            if (pointSound.isRunning()) {
                pointSound.stop();
            }
            pointSound.setFramePosition(0);
            pointSound.start();
        }
    }
}