package edu.ntnu.idatt2003.gruppe50.infrastructure;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for reading and writing stock CSV files.
 */
public class CSVFileHandler {

  /**
   * Reads stock data from a CSV file and returns a list of {@link Stock} objects.
   * <p>
   * Lines starting with {@code #} or blank lines are ignored.
   * Each valid line must be on the form {@code symbol,name,price}.
   * </p>
   *
   * <p>
   * Code gotten from code-example from lecture material.
   * <a href="https://gitlab.com/atleolso/file-handling/-/blob/master/src/main/java/edu/ntnu/idatt2003/TextFiles.java">...</a>
   * </p>
   *
   * @param path the path to the CSV file
   * @return a list of stocks read from the file
   * @throws RuntimeException if the file cannot be found or read
   *
   */
  public static List<Stock> readLines(Path path) {
    try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
      String line;
      List<Stock> stocks = new ArrayList<>();

      while ((line = bufferedReader.readLine()) != null) {
        if (line.isBlank() || line.startsWith("#")) continue;

        String[] parts = line.split(",");
        if (parts.length != 3) continue;

        String symbol = parts[0];
        String company = parts[1];
        BigDecimal price = new BigDecimal(parts[2]);

        Stock stock = new Stock(symbol, company, price);
        stocks.add(stock);
      }
      return stocks;
    } catch (IOException e) {
      throw new RuntimeException("No path found by: " + path, e);
    }
  }

  /**
   * Writes a list of {@link Stock} objects to a CSV file.
   * <p>
   * Each stock is written on the form {@code symbol,name,price}.
   * </p>
   * <p>
   * Try-with-resource closes the stream automatically.
   * <p>
   * Code gotten from code-example from lecture material.
   * <a href="https://gitlab.com/atleolso/file-handling/-/blob/master/src/main/java/edu/ntnu/idatt2003/TextFiles.java">...</a>
   * </p>
   *
   * @param path   destination path for the CSV file
   * @param stocks stocks to write
   * @throws RuntimeException if the file cannot be written
   */
  public static void writeToFile(Path path, List<Stock> stocks) {
    try (Writer writer = Files.newBufferedWriter(path)) {
      for (Stock stock : stocks) {
        writer.write(
            stock.getSymbol() + "," + stock.getCompany() + "," + stock.getSalesPrice() + "\n"
        );
      }
    } catch (IOException e) {
      throw new RuntimeException("No path found by: " + path, e);
    }
  }

}
