package edu.ntnu.idatt2003.gruppe50.ui.model;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import java.io.File;
import java.math.BigDecimal;

/**
 * Holds all player choices made during the onboarding flow (MissionBriefing).
 * Passed to the game controller when the player confirms and launches a new game.
 *
 * @param playerName      the name entered by the player
 * @param difficulty      the chosen difficulty level
 * @param startingCapital the starting capital in kr
 * @param stockFile       the CSV file containing stock data for the game session
 */
public record OnboardingData(
    String playerName,
    Difficulty difficulty,
    BigDecimal startingCapital,
    File stockFile
) {}
