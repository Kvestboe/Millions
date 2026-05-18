package edu.ntnu.idatt2003.gruppe50.ui.view;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {

  private final AudioClip click;
  private final AudioClip hover;
//  private final AudioClip buy;
//  private final AudioClip sell;
//  private final AudioClip advance;
//  private final AudioClip win;
//  private final AudioClip lose;
  private final MediaPlayer music;

  private double volume = 0.4;

  public SoundManager() {
    click = loadClip("click.wav");
    hover = loadClip("hover.wav");
    music = loadMusic("relaxed-jazz.mp3");
  }

  public void playClick() {
    play(hover);
  }

  public void playMusic() {
    play(music);
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
