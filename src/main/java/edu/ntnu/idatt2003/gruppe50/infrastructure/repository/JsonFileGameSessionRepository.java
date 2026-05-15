package edu.ntnu.idatt2003.gruppe50.infrastructure.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.GameSaveMapper;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.GameSaveDto;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class JsonFileGameSessionRepository implements GameSessionRepository {

  private static final Path SAVE_DIR = Path.of("saves");

  private final ObjectMapper mapper;
  private final TransactionFactory factory;
  private final Map<UUID, GameSession> cache = new HashMap<>();

  public JsonFileGameSessionRepository(TransactionFactory factory) {
    this.factory = factory;
    this.mapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);
    try {
      Files.createDirectories(SAVE_DIR);
    } catch (IOException e) {
      throw new RuntimeException("Could not create save directory: " + SAVE_DIR, e);
    }
  }

  @Override
  public void save(GameSession session) {
    cache.put(session.getGameId(), session);
    Path file = SAVE_DIR.resolve(session.getGameId() + ".json");
    try {
      mapper.writeValue(file.toFile(), GameSaveMapper.toDto(session));
    } catch (IOException e) {
      throw new RuntimeException("Failed to save game session " + session.getGameId(), e);
    }
  }

  @Override
  public Optional<GameSession> findById(UUID gameId) {
    if (cache.containsKey(gameId)) {
      return Optional.of(cache.get(gameId));
    }
    Path file = SAVE_DIR.resolve(gameId + ".json");
    if (!Files.exists(file)) {
      return Optional.empty();
    }
    try {
      GameSaveDto dto = mapper.readValue(file.toFile(), GameSaveDto.class);
      GameSession session = GameSaveMapper.fromDto(dto, factory);
      cache.put(gameId, session);
      return Optional.of(session);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load game session " + gameId, e);
    }
  }
}
