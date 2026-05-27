package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.util.List;

/**
 * DTO containing the top gaining and losing stocks for the current week.
 *
 * @param topGainers stocks with the highest percent price change, sorted descending
 * @param topLosers  stocks with the lowest percent price change, sorted ascending
 */
public record WeeklyMoversDto(
    List<StockDto> topGainers,
    List<StockDto> topLosers
) {
}