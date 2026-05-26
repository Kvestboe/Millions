package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serializable player state for a saved game.
 *
 * @param name player name
 * @param startingMoney original starting money
 * @param money current cash balance
 * @param coins current coin balance
 * @param activeTheme active theme id
 * @param ownedThemes owned theme ids
 * @param shares saved portfolio shares
 * @param transactions saved transaction history
 */
public record PlayerDto(
    String name,
    BigDecimal startingMoney,
    BigDecimal money,
    int coins,
    String activeTheme,
    List<String> ownedThemes,
    List<ShareDto> shares,
    List<TransactionDto> transactions
) {}
