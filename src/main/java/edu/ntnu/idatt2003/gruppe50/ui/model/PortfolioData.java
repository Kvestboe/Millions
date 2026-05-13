package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioData(
    BigDecimal cash,
    BigDecimal portfolioValue,
    BigDecimal netWorth,
    List<ShareData> shares,
    List<BigDecimal> netWorthHistory
) {}
