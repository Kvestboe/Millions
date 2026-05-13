package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.math.BigDecimal;
import java.util.UUID;

public record ShareData(
    UUID shareId,
    String symbol,
    String stock,
    BigDecimal quantity,
    BigDecimal purchasePrice,
    BigDecimal currentPrice,
    BigDecimal currentShareValue
) {}
