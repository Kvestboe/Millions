package edu.ntnu.idatt2003.gruppe50.ui.mapper;

import static edu.ntnu.idatt2003.gruppe50.ui.mapper.ShareDataMapper.mapShare;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.TransactionDto;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionData;

/** Maps application transaction DTOs to UI transaction models. */
public final class TransactionDataMapper {

  /**
   * Maps a transaction DTO to UI transaction data.
   *
   * @param dto transaction DTO to map
   * @return UI transaction data
   */
  public static TransactionData mapTransaction(TransactionDto dto) {
    return new TransactionData(
        mapShare(dto.share()),
        dto.week(),
        dto.type(),
        dto.committed(),
        dto.taxFee(),
        dto.commissionFee(),
        dto.total()
    );
  }
}
