package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Purchase;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Sale;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class DtoMapper {

  public static ShareDto createShareDto(Share share) {
    return new ShareDto(
            share.getShareId(),
            share.getStock().getSymbol(),
            share.getStock().getCompany(),
            share.getQuantity(),
            share.getPurchasePrice(),
            share.getStock().getSalesPrice(),
            share.getStock().getSalesPrice().multiply(share.getQuantity()),
            share.getStock()
                .getSalesPrice()
                .subtract(share.getPurchasePrice())
                .multiply(share.getQuantity()),
            share.getStock()
                .getSalesPrice().subtract(share.getPurchasePrice())
                .divide(share.getPurchasePrice(), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
    );
  }

  public static TransactionType defineTransactionType(Transaction transaction) {
    if (transaction instanceof Purchase) {
      return TransactionType.PURCHASE;
    }
    if (transaction instanceof Sale) {
      return TransactionType.SALE;
    }
    throw new IllegalStateException("The transaction type is not of sale or purchase");
  }

  public static StockDto createStockDto(Stock s) {
    return new StockDto(
        s.getSymbol(),
        s.getCompany(),
        List.of(),
        s.getSalesPrice(),
        s.getLatestPriceChange(),
        s.getLatestPriceChangePercent()
    );
  }

  public static StockDto createStockDetail(Stock s) {
    return new StockDto(
        s.getSymbol(),
        s.getCompany(),
        s.getHistoricalPrices(),
        s.getSalesPrice(),
        s.getLatestPriceChange(),
        s.getLatestPriceChangePercent()
    );
  }
}
