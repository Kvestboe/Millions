package edu.ntnu.idatt2003.gruppe50.shared.observer;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ObservableTest {

  private Exchange exchange;
  private TestObserver observer;

  @BeforeEach
  void setUp() {
    Stock stock = new Stock("AAPL", "Apple", new BigDecimal("100.00"));
    exchange = new Exchange("TestExchange", List.of(stock), new TransactionFactory());
    observer = new TestObserver();
  }

  @Test
  void addObserver_notifiesObserverOnUpdate() {
    exchange.addObserver(observer);
    exchange.notifyObservers();
    assertEquals(1, observer.updateCount);
  }

  @Test
  void addObserver_multipleObservers_allGetNotified() {
    TestObserver second = new TestObserver();
    exchange.addObserver(observer);
    exchange.addObserver(second);
    exchange.notifyObservers();
    assertEquals(1, observer.updateCount);
    assertEquals(1, second.updateCount);
  }

  @Test
  void addObserver_null_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> exchange.addObserver(null));
  }

  @Test
  void addObserver_sameObserverTwice_notifiedTwice() {
    exchange.addObserver(observer);
    exchange.addObserver(observer);
    exchange.notifyObservers();
    assertEquals(2, observer.updateCount);
  }

  @Test
  void removeObserver_removedObserverIsNotNotified() {
    exchange.addObserver(observer);
    exchange.removeObserver(observer);
    exchange.notifyObservers();
    assertEquals(0, observer.updateCount);
  }

  @Test
  void removeObserver_nonExistentObserver_doesNotThrow() {
    assertDoesNotThrow(() -> exchange.removeObserver(observer));
  }

  @Test
  void removeObserver_null_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> exchange.removeObserver(null));
  }

  @Test
  void notifyObservers_noObservers_doesNotThrow() {
    assertDoesNotThrow(() -> exchange.notifyObservers());
  }

  @Test
  void notifyObservers_calledMultipleTimes_observerUpdatedEachTime() {
    exchange.addObserver(observer);
    exchange.notifyObservers();
    exchange.notifyObservers();
    assertEquals(2, observer.updateCount);
  }

  @Test
  void advance_notifiesObservers() {
    exchange.addObserver(observer);
    exchange.advance();
    assertEquals(1, observer.updateCount);
  }

  private static class TestObserver implements Observer {
    int updateCount = 0;

    @Override
    public void update() {
      updateCount++;
    }
  }
}
