package edu.ntnu.idatt2003.gruppe50.infrastructure.csv;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parses stock data in CSV format.
 *
 * <p>This class deals only with strings, it has no file access, so it
 * can be unit-tested without touching the filesystem.
 */
public class StockCsvParser {

  private static final Logger LOG = Logger.getLogger(StockCsvParser.class.getName());

  private static final String COMMENT_PREFIX = "#";
  private static final String FIELD_SEPARATOR = ",";
  private static final int EXPECTED_FIELDS = 3;

  /**
   * Parses a single CSV line.
   *
   * @param line the line to parse
   * @return the parsed stock, or {@link Optional#empty()} if the line is
   *     blank or a comment
   * @throws InvalidStockDataException if the line is non-empty but malformed
   *     (wrong number of fields, blank symbol/name, or invalid price)
   */
  public Optional<Stock> parseLine(String line) throws InvalidStockDataException {
    if (line == null || line.isBlank() || line.startsWith(COMMENT_PREFIX)) {
      return Optional.empty();
    }

    String[] parts = line.split(FIELD_SEPARATOR, -1);
    if (parts.length != EXPECTED_FIELDS) {
      throw new InvalidStockDataException(
          "Expected " + EXPECTED_FIELDS + " fields but got " + parts.length + ": " + line);
    }

    String symbol = parts[0].trim();
    String company = parts[1].trim();
    String priceText = parts[2].trim();

    if (symbol.isBlank() || company.isBlank()) {
      throw new InvalidStockDataException("Symbol and name must be non-blank: " + line);
    }

    BigDecimal price;
    try {
      price = new BigDecimal(priceText);
    } catch (NumberFormatException e) {
      throw new InvalidStockDataException("Invalid price '" + priceText + "' on line: " + line, e);
    }

    try {
      return Optional.of(new Stock(symbol, company, price));
    } catch (IllegalArgumentException e) {
      throw new InvalidStockDataException("Invalid stock on line: " + line, e);
    }
  }

  /**
   * Parses a list of CSV lines.
   *
   * <p>Blank lines and comment lines are silently skipped. Lines that are
   * present but malformed are logged and skipped,
   * so a single corrupt line does not abort the whole import.
   *
   * @param lines the lines to parse
   * @return the stocks that were parsed successfully
   * @throws IllegalArgumentException if {@code lines} is null
   */
  public List<Stock> parseAll(List<String> lines) {
    Validate.notNull(lines, "Lines");

    List<Stock> stocks = new ArrayList<>();
    for (String line : lines) {
      try {
        parseLine(line).ifPresent(stocks::add);
      } catch (InvalidStockDataException e) {
        LOG.log(Level.WARNING, "Skipping malformed CSV line: {0}", e.getMessage());
      }
    }
    return stocks;
  }

  /**
   * Formats a stock as a single CSV line, without a trailing newline.
   *
   * @param stock the stock to format
   * @return the CSV representation of the stock
   * @throws IllegalArgumentException if {@code stock} is null
   */
  public String formatLine(Stock stock) {
    Validate.notNull(stock, "Stock");
    return stock.getSymbol() + FIELD_SEPARATOR
        + stock.getCompany() + FIELD_SEPARATOR
        + stock.getSalesPrice();
  }
}
