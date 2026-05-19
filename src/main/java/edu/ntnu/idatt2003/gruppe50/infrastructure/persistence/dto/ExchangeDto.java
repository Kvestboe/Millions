package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.StockDto;
import java.util.List;

public record ExchangeDto(
    String name,
    int week,
    List<StockDto> stocks,
    List<LimitOrderDto> pendingOrders
) {

}
