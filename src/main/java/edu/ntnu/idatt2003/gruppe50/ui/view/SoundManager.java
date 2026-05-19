package edu.ntnu.idatt2003.gruppe50.ui.view;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import static java.lang.Math.clamp;

public class SoundManager {

  private final AudioClip click;
  private final AudioClip hover;
//  private final AudioClip buy;
//  private final AudioClip sell;
//  private final AudioClip advance;
//  private final AudioClip win;
//  private final AudioClip lose;
  private final MediaPlayer music;

  private double masterVolume = 0.65; // 0.0–1.0
  private boolean musicEnabled = true;
  private boolean sfxEnabled = true;
  private boolean masterEnabled = true;

  public SoundManager() {
    click = loadClip("click.wav");
    hover = loadClip("hover.wav");
    music = loadMusic("relaxed-jazz.mp3");
    applyVolumes();
    if (music != null) music.setCycleCount(MediaPlayer.INDEFINITE);
  }

  public void playClick() {
    if (masterEnabled && sfxEnabled && hover != null) hover.play();
  }

  public void playMusic() {
    play(music);
  }

  public void setMasterVolume(double volume) {
    this.masterVolume = clamp(volume);
    applyVolumes();
  }

  public void setMasterEnabled(boolean enabled) {
    this.masterEnabled = enabled;
    applyVolumes();
    if (music == null) return;
    if (enabled && musicEnabled) music.play();
    else music.pause();
  }

  public void setMusicEnabled(boolean enabled) {
    this.musicEnabled = enabled;
    applyVolumes();
    if (music == null) return;
    if (enabled) music.play();
    else music.pause();
  }

  public void setSfxEnabled(boolean enabled) {
    this.sfxEnabled = enabled;
    applyVolumes();
  }

  public double getMasterVolume() { return masterVolume; }
  public boolean isMusicEnabled() { return musicEnabled; }
  public boolean isSfxEnabled() { return sfxEnabled; }
  public boolean isMasterEnabled() { return masterEnabled; }

  private void applyVolumes() {
    double effective = masterEnabled ? masterVolume : 0.0;
    double musicVol = musicEnabled ? effective : 0.0;
    double sfxVol = sfxEnabled ? effective : 0.0;
    if (music != null) music.setVolume(musicVol);
    if (click != null) click.setVolume(sfxVol);
    if (hover != null) hover.setVolume(sfxVol);
  }

  private static double clamp(double v) {
    return Math.max(0.0, Math.min(1.0, v));
  }

  private void play(AudioClip clip) {
    if (clip != null) {
      clip.play();
    }
  }

  private void play(MediaPlayer media) {
    if (media != null) {
      media.play();
    }
  }

  private AudioClip loadClip(String filename) {
    try {
      var url = getClass().getResource("/sounds/" + filename);
      return url != null ? new AudioClip(url.toExternalForm()) : null;
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not load sound: " + filename);
    }
  }

  private MediaPlayer loadMusic(String filename) {
    try {
      var url = getClass().getResource("/sounds/" + filename);
      return url != null ? new MediaPlayer(new Media(url.toExternalForm())) : null;
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not load music: " + filename);
    }
  }
}
