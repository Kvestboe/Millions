package edu.ntnu.idatt2003.gruppe50.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for formatting monetary and percentage values.
 *
 * <p>The class formats {@link BigDecimal} values with two decimal places using
 * {@link RoundingMode#HALF_UP}. Positive signed values are prefixed with a plus
 * sign, while negative values keep their minus sign.</p>
 */
public final class MoneyFormat {

  /**
   * Formats a value with two decimal places.
   *
   * <p>If the given value is {@code null}, an empty string is returned.</p>
   *
   * @param value the value to format
   * @return the formatted value with two decimal places, or an empty string if the value is {@code null}
   */
  public static String format(BigDecimal value) {
    if (value == null) {
      return "";
    }
    return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
  }

  /**
   * Formats a value with two decimal places and a sign prefix.
   *
   * <p>Positive values and zero are prefixed with {@code +}. Negative values keep
   * their {@code -} sign from the formatted number. If the given value is
   * {@code null}, an empty string is returned.</p>
   *
   * @param value the value to format
   * @return the formatted signed value, or an empty string if the value is {@code null}
   */
  public static String formatSigned(BigDecimal value) {
    if (value == null) {
      return "";
    }
    BigDecimal scaled = value.setScale(2, RoundingMode.HALF_UP);
    return (scaled.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + scaled.toPlainString();
  }

  /**
   * Formats a value as a currency amount in Norwegian kroner.
   *
   * <p>The returned value includes a sign prefix and the {@code kr} suffix.
   * If the given value is {@code null}, the result will be {@code " kr"}.If the given value is
   * {@code null}, an empty string is returned.</p>
   *
   * @param value the currency value to format
   * @return the formatted currency value
   */
  public static String formatCurrency(BigDecimal value) {
    if (value == null) {
      return "";
    }
    return formatSigned(value) + " kr";
  }

  /**
   * Formats a value as a percentage.
   *
   * <p>The returned value includes a sign prefix and the {@code %} suffix.
   * If the given value is {@code null}, the result will be {@code "%"}.If the given value is
   * {@code null}, an empty string is returned.</p>
   *
   * @param value the percentage value to format
   * @return the formatted percentage value
   */
  public static String formatPercent(BigDecimal value) {
    if (value == null) {
      return "";
    }
    return formatSigned(value) + "%";
  }
}
