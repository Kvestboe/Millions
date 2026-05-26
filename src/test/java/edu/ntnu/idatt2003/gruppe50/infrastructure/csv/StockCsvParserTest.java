package edu.ntnu.idatt2003.gruppe50.infrastructure.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StockCsvParserTest {

  private StockCsvParser parser;

  @BeforeEach
  void setUp() {
    parser = new StockCsvParser();
  }

  @Nested
  class ParseLineTests {

    @Test
    void validLine_returnsStock() throws InvalidStockDataException {
      Optional<Stock> result = parser.parseLine("AAPL,Apple Inc.,276.0");

      assertTrue(result.isPresent());
      assertEquals("AAPL", result.get().getSymbol());
      assertEquals("Apple Inc.", result.get().getCompany());
      assertEquals(0, new BigDecimal("276.0").compareTo(result.get().getSalesPrice()));
    }

    @Test
    void trimsLeadingAndTrailingWhitespace() throws InvalidStockDataException {
      Optional<Stock> result = parser.parseLine("  AAPL , Apple Inc. , 276.0 ");

      assertTrue(result.isPresent());
      assertEquals("AAPL", result.get().getSymbol());
      assertEquals("Apple Inc.", result.get().getCompany());
    }

    @Test
    void blankLine_returnsEmpty() throws InvalidStockDataException {
      assertTrue(parser.parseLine("").isEmpty());
      assertTrue(parser.parseLine("   ").isEmpty());
    }

    @Test
    void commentLine_returnsEmpty() throws InvalidStockDataException {
      assertTrue(parser.parseLine("# This is a comment").isEmpty());
      assertTrue(parser.parseLine("# Ticker,Name,Price").isEmpty());
    }

    @Test
    void nullLine_returnsEmpty() throws InvalidStockDataException {
      assertTrue(parser.parseLine(null).isEmpty());
    }

    @Test
    void tooFewFields_throwsException() {
      InvalidStockDataException ex = assertThrows(
          InvalidStockDataException.class, () -> parser.parseLine("AAPL,Apple"));
      assertTrue(ex.getMessage().contains("Expected 3 fields"));
    }

    @Test
    void tooManyFields_throwsException() {
      assertThrows(InvalidStockDataException.class,
          () -> parser.parseLine("AAPL,Apple,100,extra"));
    }

    @Test
    void invalidPrice_throwsException() {
      InvalidStockDataException ex = assertThrows(
          InvalidStockDataException.class, () -> parser.parseLine("AAPL,Apple,not-a-number"));
      assertTrue(ex.getMessage().contains("Invalid price"));
    }

    @Test
    void invalidPrice_exceptionWrapsNumberFormatException() {
      InvalidStockDataException ex = assertThrows(
          InvalidStockDataException.class, () -> parser.parseLine("AAPL,Apple,bad"));
      assertTrue(ex.getCause() instanceof NumberFormatException);
    }

    @Test
    void blankSymbol_throwsException() {
      assertThrows(InvalidStockDataException.class,
          () -> parser.parseLine(",Apple,100"));
    }

    @Test
    void blankCompany_throwsException() {
      assertThrows(InvalidStockDataException.class,
          () -> parser.parseLine("AAPL,,100"));
    }

    @Test
    void negativePrice_throwsException() {
      InvalidStockDataException ex = assertThrows(
          InvalidStockDataException.class, () -> parser.parseLine("AAPL,Apple,-100"));
      assertTrue(ex.getMessage().contains("Invalid stock"));
    }

    @Test
    void zeroPrice_throwsException() {
      assertThrows(InvalidStockDataException.class,
          () -> parser.parseLine("AAPL,Apple,0"));
    }
  }

  @Nested
  class ParseAllTests {

    @Test
    void mixedContent_skipsBlanksAndComments() {
      List<String> lines = List.of(
          "# Top stocks",
          "",
          "AAPL,Apple Inc.,276.0",
          "   ",
          "MSFT,Microsoft,404.0"
      );

      List<Stock> stocks = parser.parseAll(lines);

      assertEquals(2, stocks.size());
      assertEquals("AAPL", stocks.get(0).getSymbol());
      assertEquals("MSFT", stocks.get(1).getSymbol());
    }

    @Test
    void malformedLine_isSkippedAndOtherLinesAreReturned() {
      List<String> lines = List.of(
          "AAPL,Apple,100",
          "BAD-LINE-HERE",
          "MSFT,Microsoft,200"
      );

      List<Stock> stocks = parser.parseAll(lines);

      assertEquals(2, stocks.size());
      assertEquals("AAPL", stocks.get(0).getSymbol());
      assertEquals("MSFT", stocks.get(1).getSymbol());
    }

    @Test
    void emptyList_returnsEmpty() {
      assertTrue(parser.parseAll(List.of()).isEmpty());
    }

    @Test
    void nullList_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> parser.parseAll(null));
    }
  }

  @Nested
  class FormatLineTests {

    @Test
    void roundTripsThroughParseLine() throws InvalidStockDataException {
      Stock original = new Stock("AAPL", "Apple Inc.", new BigDecimal("276.0"));

      String csv = parser.formatLine(original);
      Optional<Stock> roundTripped = parser.parseLine(csv);

      assertTrue(roundTripped.isPresent());
      assertEquals(original.getSymbol(), roundTripped.get().getSymbol());
      assertEquals(original.getCompany(), roundTripped.get().getCompany());
      assertEquals(0, original.getSalesPrice().compareTo(roundTripped.get().getSalesPrice()));
    }

    @Test
    void nullStock_throwsException() {
      assertThrows(IllegalArgumentException.class, () -> parser.formatLine(null));
    }
  }
}
