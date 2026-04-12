package com.snakegame.utils;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {
    private static SoundManager instance;
    private Clip bgmClip;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void playBackgroundMusic(String resourcePath) {
        if (bgmClip != null && bgmClip.isRunning()) {
            return;
        }
        new Thread(() -> {
            try {
                URL url = getClass().getResource(resourcePath);
                if (url == null) {
                    System.err.println("背景音乐文件未找到：" + resourcePath);
                    return;
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                bgmClip = AudioSystem.getClip();
                bgmClip.open(audioIn);
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
                bgmClip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void stopBackgroundMusic() {
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
            bgmClip = null;
        }
    }
}