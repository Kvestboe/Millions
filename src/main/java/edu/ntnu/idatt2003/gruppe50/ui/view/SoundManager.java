package edu.ntnu.idatt2003.gruppe50.ui.view;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Loads and plays the application's background music and UI sound effects.
 *
 * <p>Owns one looping {@link MediaPlayer} for music and short {@link AudioClip}
 * clips for one-shot effects. Master, music and SFX volume can be controlled
 * independently. Files are loaded from {@code /sounds/} on the classpath; if a
 * file is missing the manager falls back to silently doing nothing rather than
 * crashing the application.
 */
public class SoundManager {

  private final AudioClip click;
  private final AudioClip hover;
  private final MediaPlayer music;

  private double masterVolume = 0.65;
  private boolean musicEnabled = true;
  private boolean sfxEnabled = true;
  private boolean masterEnabled = true;

  /** Loads all bundled audio resources and applies the default volumes. */
  public SoundManager() {
    click = loadClip("click.wav");
    hover = loadClip("hover.wav");
    music = loadMusic("relaxed-jazz.mp3");
    applyVolumes();
    if (music != null) {
      music.setCycleCount(MediaPlayer.INDEFINITE);
    }
  }

  /** Plays the standard UI click sound effect. */
  public void playClick() {
    if (masterEnabled && sfxEnabled && hover != null) {
      hover.play();
    }
  }

  /** Starts playing the looping background music. */
  public void playMusic() {
    play(music);
  }

  /**
   * Sets the master volume, clamped to {@code [0.0, 1.0]}.
   *
   * @param volume desired master volume
   */
  public void setMasterVolume(double volume) {
    this.masterVolume = clamp(volume);
    applyVolumes();
  }

  /**
   * Enables or disables all sound. Music resumes or pauses to follow the state.
   *
   * @param enabled whether master sound should be on
   */
  public void setMasterEnabled(boolean enabled) {
    this.masterEnabled = enabled;
    applyVolumes();
    if (music == null) {
      return;
    }
    if (enabled && musicEnabled) {
      music.play();
    } else {
      music.pause();
    }
  }

  /**
   * Enables or disables background music.
   *
   * @param enabled whether music should play
   */
  public void setMusicEnabled(boolean enabled) {
    this.musicEnabled = enabled;
    applyVolumes();
    if (music == null) {
      return;
    }
    if (enabled) {
      music.play();
    } else {
      music.pause();
    }
  }

  /**
   * Enables or disables UI sound effects.
   *
   * @param enabled whether SFX should play
   */
  public void setSfxEnabled(boolean enabled) {
    this.sfxEnabled = enabled;
    applyVolumes();
  }

  /**
   * Returns the current master volume.
   *
   * @return current master volume in {@code [0.0, 1.0]}
   */
  public double getMasterVolume() {
    return masterVolume;
  }

  /**
   * Indicates whether background music is currently enabled.
   *
   * @return {@code true} if music is currently enabled
   */
  public boolean isMusicEnabled() {
    return musicEnabled;
  }

  /**
   * Indicates whether sound effects are currently enabled.
   *
   * @return {@code true} if sound effects are currently enabled
   */
  public boolean isSfxEnabled() {
    return sfxEnabled;
  }

  /**
   * Indicates whether master sound is currently enabled.
   *
   * @return {@code true} if master sound is currently enabled
   */
  public boolean isMasterEnabled() {
    return masterEnabled;
  }

  private void applyVolumes() {
    double effective = masterEnabled ? masterVolume : 0.0;
    double musicVol = musicEnabled ? effective : 0.0;
    double sfxVol = sfxEnabled ? effective : 0.0;
    if (music != null) {
      music.setVolume(musicVol);
    }
    if (click != null) {
      click.setVolume(sfxVol);
    }
    if (hover != null) {
      hover.setVolume(sfxVol);
    }
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
