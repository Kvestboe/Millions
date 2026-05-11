package edu.ntnu.idatt2003.gruppe50.ui.mapper;

import edu.ntnu.idatt2003.gruppe50.application.query.TransactionDto;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionData;
import static edu.ntnu.idatt2003.gruppe50.ui.mapper.ShareDataMapper.mapShare;

public final class TransactionDataMapper {

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
