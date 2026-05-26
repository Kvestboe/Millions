package edu.ntnu.idatt2003.gruppe50.infrastructure.csv;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.market.StockDataSource;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads and writes stocks from CSV files on disk.
 *
 * <p>This adapter handles file I/O only; all line parsing and formatting
 * is delegated to {@link StockCsvParser}, which is what makes the
 * parsing logic unit-testable without temporary files.
 */
public class CsvStockDataSource implements StockDataSource {

  private static final Logger LOG = Logger.getLogger(CsvStockDataSource.class.getName());

  private final StockCsvParser parser;

  /**
   * Creates a new {@code CsvStockDataSource} using a fresh {@link StockCsvParser}.
   */
  public CsvStockDataSource() {
    this(new StockCsvParser());
  }

  /**
   * Creates a new {@code CsvStockDataSource} using the given parser.
   *
   * <p>This constructor exists primarily to allow tests to supply a parser
   * instance, but it can also be used to plug in a custom parser if the
   * CSV dialect should differ.
   *
   * @param parser the parser used to turn lines into stocks (and back)
   * @throws IllegalArgumentException if {@code parser} is null
   */
  public CsvStockDataSource(StockCsvParser parser) {
    Validate.notNull(parser, "Parser");
    this.parser = parser;
  }

  /**
   * Reads stocks from a CSV file at the given path.
   *
   * <p>Malformed individual lines are logged and skipped. Only I/O failures cause this
   * method to throw.
   *
   * @param source the path to read from
   * @return the list of stocks read from the file
   * @throws InvalidStockDataException if the file cannot be read
   * @throws IllegalArgumentException if {@code source} is null
   */
  @Override
  public List<Stock> readStocks(Path source) throws InvalidStockDataException {
    Validate.notNull(source, "Source");

    try (BufferedReader reader = Files.newBufferedReader(source)) {
      List<String> lines = reader.lines().toList();
      return parser.parseAll(lines);
    } catch (IOException e) {
      LOG.log(Level.SEVERE, "Failed to read stock data from " + source, e);
      throw new InvalidStockDataException("Could not read stock data from " + source, e);
    }
  }

  /**
   * Writes the given stocks to a CSV file at the given path.
   *
   * @param destination the path to write to
   * @param stocks the stocks to write
   * @throws InvalidStockDataException if the file cannot be written
   * @throws IllegalArgumentException if any argument is null
   */
  @Override
  public void writeStocks(Path destination, List<Stock> stocks) throws InvalidStockDataException {
    Validate.notNull(destination, "Destination");
    Validate.notNull(stocks, "Stocks");

    try (Writer writer = Files.newBufferedWriter(destination)) {
      for (Stock stock : stocks) {
        writer.write(parser.formatLine(stock));
        writer.write(System.lineSeparator());
      }
    } catch (IOException e) {
      throw new InvalidStockDataException("Could not write stock data to " + destination, e);
    }
  }
}
