package edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.ui.model.OnboardingData;
import java.io.File;
import java.math.BigDecimal;

/**
 * Mutable data container used during the onboarding flow.
 * Each step contributes its data to this object via
 * {@link OnboardingStep#contribute(OnboardingFlowData)}.
 * When the flow is complete, this is converted to an immutable
 * {@link OnboardingData} record.
 */
public class OnboardingFlowData {

  /**
   * The player's chosen name. Set by NameStep.
   */
  public String playerName;

  /**
   * The chosen difficulty level. Set by DifficultyStep.
   */
  public Difficulty difficulty;

  /**
   * The chosen starting capital in kr. Set by CapitalStep.
   */
  public BigDecimal startingCapital;

  /**
   * The chosen stock data file. Set by MarketStep.
   */
  public File stockFile;

  /**
   * The display name of the chosen market. Set by MarketStep.
   */
  public String marketName;

  /**
   * Converts this mutable container into an immutable {@link OnboardingData} record.
   *
   * @return a new OnboardingData with all collected values
   */
  public OnboardingData toOnboardingData() {
    return new OnboardingData(playerName, difficulty, startingCapital, stockFile, marketName);
  }
}