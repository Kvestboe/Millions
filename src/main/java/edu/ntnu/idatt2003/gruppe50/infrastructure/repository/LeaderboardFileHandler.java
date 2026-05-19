package edu.ntnu.idatt2003.gruppe50.infrastructure.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.Leaderboard;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.LeaderboardEntry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LeaderboardFileHandler {

  private static final Path FILE = Path.of("leaderboard.json");

  private final ObjectMapper mapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .enable(SerializationFeature.INDENT_OUTPUT)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  public Leaderboard load() {
    Leaderboard board = new Leaderboard();
    if (!Files.exists(FILE)) return board;
    try {
      List<LeaderboardEntry> entries = mapper.readValue(
          FILE.toFile(),
          new TypeReference<List<LeaderboardEntry>>() {});
      board.replaceAll(entries);
      return board;
    } catch (IOException e) {
      throw new RuntimeException("Failed to load leaderboard", e);
    }
  }

  public void save(Leaderboard leaderboard) {
    try {
      mapper.writeValue(FILE.toFile(), leaderboard.all());
    } catch (IOException e) {
      throw new RuntimeException("Failed to save leaderboard", e);
    }
  }
}
