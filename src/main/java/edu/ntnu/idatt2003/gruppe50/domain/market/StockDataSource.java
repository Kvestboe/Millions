package edu.ntnu.idatt2003.gruppe50.domain.market;

import edu.ntnu.idatt2003.gruppe50.infrastructure.csv.InvalidStockDataException;
import java.nio.file.Path;
import java.util.List;

/**
 * Port for reading and writing stock data from an external source.
 *
 * <p>Implementations decide the concrete format (CSV, JSON, XML, ...).
 * This abstraction lets the rest of the application work with any
 * supported format and makes it straightforward to add new formats later.
 */
public interface StockDataSource {

  /**
   * Reads stock data from the given location.
   *
   * @param source the location to read from
   * @return the list of stocks read from the source
   * @throws InvalidStockDataException if the source cannot be read
   *                                   or its contents are malformed
   */
  List<Stock> readStocks(Path source) throws InvalidStockDataException;

  /**
   * Writes the given stocks to the given location.
   *
   * @param destination the location to write to
   * @param stocks      the stocks to write
   * @throws InvalidStockDataException if the destination cannot be written
   */
  void writeStocks(Path destination, List<Stock> stocks) throws InvalidStockDataException;
}
