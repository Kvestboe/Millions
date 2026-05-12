package edu.ntnu.idatt2003.gruppe50.infrastructure.repository;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class InMemoryGameSessionRepository implements GameSessionRepository {
  private final Map<UUID, GameSession> sessions = new HashMap<>();

  @Override
  public Optional<GameSession> findById(UUID gameId) {
    Validate.notNull(gameId, "Game id");
    return Optional.ofNullable(sessions.get(gameId));
  }

  @Override
  public void save(GameSession session) {
    Validate.notNull(session, "Game session");
    sessions.put(session.getGameId(), session);
  }
}
