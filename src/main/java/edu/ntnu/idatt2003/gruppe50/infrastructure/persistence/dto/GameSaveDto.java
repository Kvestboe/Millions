package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;
import java.util.List;

public record GameSaveDto(
    String gameId,
    String state,
    String difficulty,
    String runStartedAt,
    String lastPlayed,
    List<BigDecimal> netWorthHistory,
    PlayerDto player,
    ExchangeDto exchange
) {

}
