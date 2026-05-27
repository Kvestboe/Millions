package edu.ntnu.idatt2003.gruppe50.ui.model;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import java.io.File;
import java.math.BigDecimal;

/**
 * Holds all player choices made during the onboarding flow.
 *
 * @param playerName      the name entered by the player
 * @param difficulty      the chosen difficulty level
 * @param startingCapital the starting capital in kroner
 * @param stockFile       the CSV file containing stock data for the game session
 * @param marketName      display name of the selected market
 */
public record OnboardingData(
    String playerName,
    Difficulty difficulty,
    BigDecimal startingCapital,
    File stockFile,
    String marketName) {
}
