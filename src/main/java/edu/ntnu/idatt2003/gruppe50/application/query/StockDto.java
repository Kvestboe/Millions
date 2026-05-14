package edu.ntnu.idatt2003.gruppe50.application.query;

import java.math.BigDecimal;
import java.util.List;

public record StockDto(
    String symbol,
    String company,
    List<BigDecimal> prices
) {}
