package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.TransactionDto;
import java.math.BigDecimal;
import java.util.List;

public record PlayerDto(
    String name,
    BigDecimal startingMoney,
    BigDecimal money,
    List<ShareDto> shares,
    List<TransactionDto> transactions
) {

}
