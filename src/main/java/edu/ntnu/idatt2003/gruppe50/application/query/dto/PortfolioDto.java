package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Portfolio aggregate response for the UI layer.
 *
 * @param cash            available player cash
 * @param portfolioValue  current portfolio liquidation value
 * @param netWorth        total net worth including cash and portfolio value
 * @param shares          current owned shares as DTOs
 * @param netWorthHistory historical net worth values for chart display
 * @param buyWeeks        weeks in which at least one purchase occurred
 * @param sellWeeks       weeks in which at least one sale occurred
 */
public record PortfolioDto(
    BigDecimal cash,
    BigDecimal portfolioValue,
    BigDecimal netWorth,
    List<ShareDto> shares,
    List<BigDecimal> netWorthHistory,
    Set<Integer> buyWeeks,
    Set<Integer> sellWeeks
) {
}
