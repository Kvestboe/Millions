package edu.ntnu.idatt2003.gruppe50.ui.mapper;

import edu.ntnu.idatt2003.gruppe50.application.query.ShareDto;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;

public final class ShareDataMapper {

  public static ShareData mapShare(ShareDto dto) {
    return new ShareData(
        dto.shareId(),
        dto.symbol(),
        dto.stock(),
        dto.quantity(),
        dto.purchasePrice(),
        dto.currentPrice(),
        dto.currentShareValue(),
        dto.gain(),
        dto.percentageGain()
    );
  }
}
