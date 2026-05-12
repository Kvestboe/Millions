package edu.ntnu.idatt2003.gruppe50.domain.portfolio;


import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class PortfolioTest {
  private Portfolio portfolio;
  private Share share1;
  private Share share2;
  private Share share3;

  @BeforeEach
  void setUp() {
    portfolio = new Portfolio();

    Stock stock = new Stock("KOG", "Kongsberg Gruppen", bd("330"));
    Stock stock2 = new Stock("AAPL", "Apple", bd("400"));

    share1 = new Share(stock, bd("5"), bd("310"));
    share2 = new Share(stock, bd("10"), bd("320"));
    share3 = new Share(stock2, bd("10"), bd("400"));
  }


  @Test
  void addShare_addsToList_returnsTrue() {
    int sizeBefore = portfolio.getShares().size();

    boolean result = portfolio.addShare(share1);

    assertTrue(result);
    assertEquals(sizeBefore + 1, portfolio.getShares().size());
  }

  @Test
  void addShare_NullShareThrowsIllegalArgument() {
    assertThrows(IllegalArgumentException.class, () -> portfolio.addShare(null));
  }

  @Test
  void removeShare_removesFromList_returnsTrue() {
    int sizeBefore = portfolio.getShares().size();

    portfolio.addShare(share1);
    boolean result = portfolio.removeShare(share1.getShareId());

    assertTrue(result);
    assertEquals(sizeBefore, portfolio.getShares().size());
  }

  @Test
  void removeShare_nullShareThrows() {
    assertThrows(IllegalArgumentException.class, () -> portfolio.removeShare(null));
  }

  @Test
  void removeShare_shareNotInPortfolio_returnsFalse() {
    int sizeBefore = portfolio.getShares().size();

    boolean result = portfolio.removeShare(share1.getShareId());

    assertFalse(result);
    assertEquals(sizeBefore, portfolio.getShares().size());
  }

  @Test
  void getShares_returnsListWithSameElements() {
    portfolio.addShare(share1);

    List<Share> result = portfolio.getShares();

    assertEquals(1, result.size());
    assertSame(share1, result.getFirst());
  }

  @Test
  void getShares_returnsUnmodifiableList() {
    List<Share> result = portfolio.getShares();

    assertThrows(UnsupportedOperationException.class, () -> result.add(share1));
  }

  @Test
  void getShares_returnsNewListEachTime() {
    portfolio.addShare(share1);

    assertNotSame(portfolio.getShares(), portfolio.getShares());
  }

  @Test
  void getShares_throwsIfSymbolIsNull() {
    assertThrows(IllegalArgumentException.class, () -> portfolio.getShares(null));
  }

  @Test
  void getShares_blankSymbol_throwsException() {
    assertThrows(IllegalArgumentException.class, () -> portfolio.getShares(" "));
  }

  @Test
  void getShares_filtersBySymbol() {
    portfolio.addShare(share1);
    portfolio.addShare(share2);

    List<Share> expected = List.of(share1, share2);
    List<Share> actual = portfolio.getShares("KOG");

    Comparator<Share> byId = Comparator.comparing(Share::getShareId);

    assertEquals(
        expected.stream().sorted(byId).toList(),
        actual.stream().sorted(byId).toList()
    );
  }

  @Test
  void getShares_whenNoSharesForSymbol_returnsEmptyList() {
    assertEquals(Collections.emptyList(), portfolio.getShares("AAPL"));
  }

  @Test
  void contains_findsShare_returnsTrue() {
    portfolio.addShare(share1);

    assertTrue(portfolio.contains(share1));
  }

  @Test
  void contains_findsDifferentShareWithSameStock_returnsFalse() {
    portfolio.addShare(share2);

    assertFalse(portfolio.contains(share1));
  }

  @Test
  void contains_cantFindShare_returnsFalse() {
    assertFalse(portfolio.contains(share3));
  }

  @Test
  void contains_onEmptyPortfolio_returnsFalse() {
    Portfolio empty = new Portfolio();

    assertFalse(empty.contains(share2));
  }

  @Test
  void contains_nullShare_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> portfolio.contains((Share) null));
  }

  @Test
  void contains_nullUUID_throwsException() {
    assertThrows(IllegalArgumentException.class,
        () -> portfolio.contains((UUID) null));
  }

  @Test
  void getNetWorth_returnsNetWorth() {
    portfolio.addShare(share1);
    portfolio.addShare(share2);
    portfolio.addShare(share3);
    assertEquals(bd("8815.350"), portfolio.getNetWorth());
  }

  //Helper method
  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }

}