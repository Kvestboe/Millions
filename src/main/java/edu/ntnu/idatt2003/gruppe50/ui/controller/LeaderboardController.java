package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetAllSavesUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import java.util.Comparator;
import java.util.List;

/**
 * Provides ranked save summaries for leaderboard display.
 */
public class LeaderboardController {

  private final GetAllSavesUseCase getAllSaves;

  /**
   * Creates a leaderboard controller.
   *
   * @param getAllSaves use case used to retrieve saved game summaries
   */
  public LeaderboardController(GetAllSavesUseCase getAllSaves) {
    this.getAllSaves = getAllSaves;
  }

  /**
   * Returns saved games ranked by net worth in descending order.
   *
   * @return ranked save summaries
   */
  public List<SaveSummaryDto> getRanked() {
    return getAllSaves.execute().stream()
        .sorted(Comparator.comparing(SaveSummaryDto::netWorth).reversed())
        .toList();
  }
}
