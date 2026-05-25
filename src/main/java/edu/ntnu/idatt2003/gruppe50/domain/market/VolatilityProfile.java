package edu.ntnu.idatt2003.gruppe50.domain.market;

/**
 * Describes how much stock prices can change in the market.
 *
 * @param upChance the chance that a stock price goes up
 * @param maxGain the highest possible price increase
 * @param maxLoss the highest possible price decrease
 */
public record VolatilityProfile(double upChance, double maxGain, double maxLoss) {}