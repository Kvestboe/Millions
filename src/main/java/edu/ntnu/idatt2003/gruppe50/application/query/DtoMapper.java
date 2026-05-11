package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Purchase;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Sale;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;

public final class DtoMapper {
  static ShareDto createShareDto(Share share) {
    return new ShareDto(
            share.getShareId(),
            share.getStock().getSymbol(),
            share.getStock().getCompany(),
            share.getQuantity(),
            share.getPurchasePrice(),
            share.getStock().getSalesPrice(),
            share.getStock().getSalesPrice().multiply(share.getQuantity())
    );
  }

  static TransactionType defineTransactionType(Transaction transaction) {
    if (transaction instanceof Purchase) {
      return TransactionType.PURCHASE;
    }
    if (transaction instanceof Sale) {
      return TransactionType.SALE;
    }
    throw new IllegalStateException("The transaction type is not of sale or purchase");
  }
}
