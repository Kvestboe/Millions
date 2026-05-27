package edu.ntnu.idatt2003.gruppe50.ui.mapper;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;

/**
 * Maps application share DTOs to UI share models.
 */
public final class ShareDataMapper {

  /**
   * Maps a share DTO to UI share data.
   *
   * @param dto share DTO to map
   * @return UI share data
   */
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
