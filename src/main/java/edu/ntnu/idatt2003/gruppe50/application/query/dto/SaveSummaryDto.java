package edu.ntnu.idatt2003.gruppe50.application.query.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SaveSummaryDto(
    UUID gameId,
    String playerName,
    int week,
    String status,
    BigDecimal netWorth,
    LocalDateTime lastPlayed,
    boolean isFinished
) {}
