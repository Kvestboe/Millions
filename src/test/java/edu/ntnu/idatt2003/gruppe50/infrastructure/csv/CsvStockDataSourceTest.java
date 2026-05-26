package edu.ntnu.idatt2003.gruppe50.infrastructure.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.market.StockDataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvStockDataSourceTest {

  private StockDataSource dataSource;

  @BeforeEach
  void setUp() {
    dataSource = new CsvStockDataSource();
  }

  @Nested
  class ReadTests {

    @Test
    void readsValidCsvFile(@TempDir Path tmp) throws Exception {
      Path file = tmp.resolve("stocks.csv");
      Files.writeString(file, """
          # Top stocks
          AAPL,Apple Inc.,276.0
          MSFT,Microsoft,404.0
          """);

      List<Stock> stocks = dataSource.readStocks(file);

      assertEquals(2, stocks.size());
      assertEquals("AAPL", stocks.get(0).getSymbol());
      assertEquals("MSFT", stocks.get(1).getSymbol());
    }

    @Test
    void readsEmptyFile_returnsEmptyList(@TempDir Path tmp) throws Exception {
      Path file = tmp.resolve("empty.csv");
      Files.writeString(file, "");

      assertTrue(dataSource.readStocks(file).isEmpty());
    }

    @Test
    void missingFile_throwsInvalidStockDataException(@TempDir Path tmp) {
      Path missing = tmp.resolve("does-not-exist.csv");

      InvalidStockDataException ex = assertThrows(
          InvalidStockDataException.class, () -> dataSource.readStocks(missing));
      assertTrue(ex.getCause() instanceof IOException);
    }

    @Test
    void malformedLines_areSkippedAndValidLinesReturned(@TempDir Path tmp) throws Exception {
      Path file = tmp.resolve("mixed.csv");
      Files.writeString(file, """
          AAPL,Apple,100
          this-line-is-bad
          MSFT,Microsoft,200
          """);

      List<Stock> stocks = dataSource.readStocks(file);

      assertEquals(2, stocks.size());
    }

    @Test
    void nullSource_throwsIllegalArgumentException() {
      assertThrows(IllegalArgumentException.class, () -> dataSource.readStocks(null));
    }
  }

  @Nested
  class WriteTests {

    @Test
    void writesStocks_roundTripsThroughRead(@TempDir Path tmp) throws Exception {
      Path file = tmp.resolve("out.csv");
      List<Stock> original = List.of(
          new Stock("AAPL", "Apple Inc.", new BigDecimal("276.0")),
          new Stock("MSFT", "Microsoft", new BigDecimal("404.0"))
      );

      dataSource.writeStocks(file, original);
      List<Stock> reloaded = dataSource.readStocks(file);

      assertEquals(2, reloaded.size());
      assertEquals("AAPL", reloaded.get(0).getSymbol());
      assertEquals("Microsoft", reloaded.get(1).getCompany());
    }

    @Test
    void writesEmptyList_producesEmptyFile(@TempDir Path tmp) throws Exception {
      Path file = tmp.resolve("empty.csv");

      dataSource.writeStocks(file, List.of());

      assertEquals("", Files.readString(file));
    }

    @Test
    void nullDestination_throwsIllegalArgumentException() {
      assertThrows(IllegalArgumentException.class,
          () -> dataSource.writeStocks(null, List.of()));
    }

    @Test
    void nullStocks_throwsIllegalArgumentException(@TempDir Path tmp) {
      Path file = tmp.resolve("out.csv");
      assertThrows(IllegalArgumentException.class,
          () -> dataSource.writeStocks(file, null));
    }
  }
}
