package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence;

import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultGameSession;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.GameSaveDto;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class GameSaveMapperTest {

  @Test
  void coinExchangePriceHistorySurvivesRoundtrip() {
    GameSession session = createDefaultGameSession();
    session.getCoinExchange().advanceShop();
    session.getCoinExchange().advanceShop();
    session.getCoinExchange().advanceShop();

    List<BigDecimal> originalHistory = session.getCoinExchange().getPriceHistory();

    GameSaveDto dto = GameSaveMapper.toDto(session);
    GameSession rehydrated = GameSaveMapper.fromDto(dto, new TransactionFactory());

    assertEquals(originalHistory, rehydrated.getCoinExchange().getPriceHistory());
    assertEquals(originalHistory.size(), rehydrated.getCoinExchange().getPriceHistory().size());
    assertEquals(
        session.getCoinExchange().getCurrentPricePerCoin(),
        rehydrated.getCoinExchange().getCurrentPricePerCoin()
    );
  }

  @Test
  void coinExchangePriceHistorySurvivesJsonRoundtrip() throws Exception {
    GameSession session = createDefaultGameSession();
    session.getCoinExchange().advanceShop();
    session.getCoinExchange().advanceShop();

    List<BigDecimal> originalHistory = session.getCoinExchange().getPriceHistory();

    ObjectMapper jackson = new ObjectMapper().registerModule(new JavaTimeModule());
    String json = jackson.writeValueAsString(GameSaveMapper.toDto(session));
    GameSaveDto deserialized = jackson.readValue(json, GameSaveDto.class);
    GameSession rehydrated = GameSaveMapper.fromDto(deserialized, new TransactionFactory());

    assertEquals(originalHistory, rehydrated.getCoinExchange().getPriceHistory());
  }

  @Test
  void coinExchangeIsNotResetToBasePriceAfterRoundtrip() {
    GameSession session = createDefaultGameSession();
    BigDecimal basePrice = session.getCoinExchange().getCurrentPricePerCoin();

    for (int i = 0; i < 25; i++) {
      session.getCoinExchange().advanceShop();
    }

    BigDecimal advancedPrice = session.getCoinExchange().getCurrentPricePerCoin();
    assertNotEquals(basePrice, advancedPrice);

    GameSaveDto dto = GameSaveMapper.toDto(session);
    GameSession rehydrated = GameSaveMapper.fromDto(dto, new TransactionFactory());

    assertEquals(advancedPrice, rehydrated.getCoinExchange().getCurrentPricePerCoin());
    assertNotEquals(basePrice, rehydrated.getCoinExchange().getCurrentPricePerCoin());
  }
}
