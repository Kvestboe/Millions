package edu.ntnu.idatt2003.gruppe50.domain.game;

import edu.ntnu.idatt2003.gruppe50.domain.market.VolatilityProfile;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Represents the difficulty level of a game session in Millions.
 *
 * <p>Each difficulty level defines the market behavior, hangar costs,
 * starting capital restrictions, and game over threshold for the player.
 */
public enum Difficulty {

  /**
   * Easy difficulty. Market favors the player with higher up-chance
   * and asymmetric price swings (gains larger than losses).
   * No capital restriction and low hangar cost.
   */
  EASY(0.58, 0.12, 0.08, 0.006, Optional.empty(), 0.40),

  /**
   * Medium difficulty. Balanced 50/50 market with moderate hangar cost.
   * Starting capital capped at 25 000 kr.
   */
  MEDIUM(0.50, 0.14, 0.14, 0.015, Optional.of(new BigDecimal("25000")), 0.50),

  /**
   * Hard difficulty. Same up-chance as medium, but with higher downside risk.
   * Losses can be larger than gains. High hangar cost and starting capital
   * capped at 5 000 kr.
   */
  HARD(0.46, 0.16, 0.16, 0.02, Optional.of(new BigDecimal("5000")), 0.60);

  private final double upChance;
  private final double maxGain;
  private final double maxLoss;
  private final double hangarCostRate;
  private final Optional<BigDecimal> maxStartingCapital;
  private final double gameOverThreshold;

  Difficulty(double upChance, double maxGain, double maxLoss, double hangarCostRate,
             Optional<BigDecimal> maxStartingCapital, double gameOverThreshold) {
    this.upChance = upChance;
    this.maxGain = maxGain;
    this.maxLoss = maxLoss;
    this.hangarCostRate = hangarCostRate;
    this.maxStartingCapital = maxStartingCapital;
    this.gameOverThreshold = gameOverThreshold;
  }

  /**
   * Returns the probability of a stock price increasing during advance().
   *
   * @return up chance as a decimal, e.g. 0.60 for 60%
   */
  public double getUpChance() {
    return upChance;
  }

  /**
   * Returns the maximum percentage a stock price can increase in one week.
   *
   * @return max gain as a decimal, e.g. 0.06 for 6%
   */
  public double getMaxGain() {
    return maxGain;
  }

  /**
   * Returns the maximum percentage a stock price can decrease in one week.
   *
   * @return max loss as a decimal, e.g. 0.04 for 4%
   */
  public double getMaxLoss() {
    return maxLoss;
  }

  /**
   * Returns the weekly hangar cost as a fraction of the player's starting capital.
   * Deducted from the player's cash balance each time advance() is called.
   *
   * @return hangar cost rate as a decimal, e.g. 0.005 for 0.5%
   */
  public double getHangarCostRate() {
    return hangarCostRate;
  }

  /**
   * Returns the maximum allowed starting capital for this difficulty level,
   * or empty if there is no restriction.
   *
   * @return optional max starting capital in kr
   */
  public Optional<BigDecimal> getMaxStartingCapital() {
    return maxStartingCapital;
  }

  /**
   * Returns the net worth threshold below which the game ends.
   * Expressed as a fraction of the player's starting capital.
   *
   * @return game over threshold as a decimal, e.g. 0.40 for 40%
   */
  public double getGameOverThreshold() {
    return gameOverThreshold;
  }

  /**
   * Returns the volatility profile derived from this difficulty's market parameters.
   *
   * @return a {@link VolatilityProfile} for use in {@link edu.ntnu.idatt2003.gruppe50.domain.market.Exchange}
   */
  public VolatilityProfile toVolatilityProfile() {
    return new VolatilityProfile(upChance, maxGain, maxLoss);
  }
}
