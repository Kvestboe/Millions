package edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding;

import javafx.scene.Parent;

/**
 * Represents a single step in the onboarding flow of Millions.
 *
 * <p>Each step is responsible for rendering its own view and reporting
 * whether the player has completed the required input to proceed.
 * Navigation between steps is handled by {@link OnboardingFlow}.
 */
public interface OnboardingStep {

  /**
   * Returns the JavaFX node representing this step's UI.
   *
   * @return the root node of this step's view
   */
  Parent getView();

  /**
   * Returns whether this step is complete and the player may proceed.
   * Used by {@link OnboardingFlow} to validate before navigating to the next step.
   *
   * @return true if all required input is valid, false otherwise
   */
  boolean isValid();

  /**
   * Contributes this step's collected data to the shared onboarding data object.
   * Called by {@link OnboardingFlow} when the player proceeds to the next step.
   *
   * @param data the shared mutable data object to update
   */
  void contribute(OnboardingFlowData data);
}