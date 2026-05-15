package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Portfolio aggregate response for the UI layer.
 *
 * @param cash available player cash
 * @param portfolioValue current portfolio liquidation value
 * @param netWorth total net worth (cash + portfolio)
 * @param shares current owned shares as DTOs
 */
public record PortfolioDto (
    BigDecimal cash,
    BigDecimal portfolioValue,
    BigDecimal netWorth,
    List<ShareDto> shares,
    List<BigDecimal> netWorthHistory
) {}
