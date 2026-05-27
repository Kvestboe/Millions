package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Stock data exposed to the UI layer.
 *
 * @param symbol        stock symbol
 * @param company       company name
 * @param prices        historical prices, or an empty list for summary views
 * @param salesPrice    current sales price
 * @param priceChange   latest absolute price change
 * @param percentChange latest percentage price change
 */
public record StockDto(
    String symbol,
    String company,
    List<BigDecimal> prices,
    BigDecimal salesPrice,
    BigDecimal priceChange,
    BigDecimal percentChange
) {
}
