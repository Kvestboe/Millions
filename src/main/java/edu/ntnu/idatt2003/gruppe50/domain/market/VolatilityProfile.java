package edu.ntnu.idatt2003.gruppe50.domain.market;

/**
 * Immutable value object describing market volatility parameters.
 * Controls direction bias and magnitude of weekly price swings in Exchange.
 */
public record VolatilityProfile(double upChance, double maxGain, double maxLoss) {}