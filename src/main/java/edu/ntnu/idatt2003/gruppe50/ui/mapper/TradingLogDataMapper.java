package edu.ntnu.idatt2003.gruppe50.ui.mapper;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.TradingLogDto;
import edu.ntnu.idatt2003.gruppe50.ui.model.TradingLogData;

/**
 * Maps application trading log DTOs to UI trading log models.
 */
public final class TradingLogDataMapper {

  private TradingLogDataMapper() {
  }

  /**
   * Maps a trading log DTO to UI trading log data.
   *
   * @param dto trading log DTO to map
   * @return UI trading log data
   */
  public static TradingLogData map(TradingLogDto dto) {
    return new TradingLogData(
        dto.totalTrades(),
        dto.purchases(),
        dto.sales(),
        dto.totalFees(),
        dto.totalTaxes(),
        dto.realizedPnL(),
        dto.recentActivity().stream().map(TransactionDataMapper::mapTransaction).toList()
    );
  }
}
