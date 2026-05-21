package edu.ntnu.idatt2003.gruppe50.infrastructure.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.Leaderboard;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.LeaderboardEntry;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class LeaderboardFileHandler {

  private static final Path FILE = Path.of("leaderboard.json");

  private final ObjectMapper mapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .enable(SerializationFeature.INDENT_OUTPUT)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  public Leaderboard load() {
    Leaderboard board = new Leaderboard();

    if (!Files.exists(FILE)) {
      board.replaceAll(defaultEntries());
      save(board);
      return board;
    }

    try {
      List<LeaderboardEntry> entries = mapper.readValue(
          FILE.toFile(),
          new TypeReference<List<LeaderboardEntry>>() {});
      board.replaceAll(entries);

      if (board.all().isEmpty()) {
        board.replaceAll(defaultEntries());
        save(board);
      }

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

  private List<LeaderboardEntry> defaultEntries() {
    return List.of(
        new LeaderboardEntry(
            "Kristian",
            LeaderboardEntry.calculateScore(1000, new BigDecimal("5000")),
            1000,
            new BigDecimal("1000000"),
            new BigDecimal("5000"),
            Difficulty.HARD,
            LocalDate.now()
        ),
        new LeaderboardEntry(
            "Marius",
            LeaderboardEntry.calculateScore(500, new BigDecimal("25000")),
            500,
            new BigDecimal("1000000"),
            new BigDecimal("25000"),
            Difficulty.MEDIUM,
            LocalDate.now()
        ),
        new LeaderboardEntry(
            "Stonk Master",
            LeaderboardEntry.calculateScore(200, new BigDecimal("100000")),
            200,
            new BigDecimal("1000000"),
            new BigDecimal("50000"),
            Difficulty.EASY,
            LocalDate.now()
        )
    );
  }
}
