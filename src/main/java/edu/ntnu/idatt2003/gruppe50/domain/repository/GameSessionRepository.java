package edu.ntnu.idatt2003.gruppe50.domain.repository;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameSessionRepository {

  Optional<GameSession> findById(UUID gameId);

  void save(GameSession session);

  List<GameSession> findAll();

  void delete(UUID gameId);
}
