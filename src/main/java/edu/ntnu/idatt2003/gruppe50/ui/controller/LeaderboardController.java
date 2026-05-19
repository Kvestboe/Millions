package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetAllSavesUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import java.util.Comparator;
import java.util.List;

public class LeaderboardController {

  private final GetAllSavesUseCase getAllSaves;

  public LeaderboardController(GetAllSavesUseCase getAllSaves) {
    this.getAllSaves = getAllSaves;
  }

  public List<SaveSummaryDto> getRanked() {
    return getAllSaves.execute().stream()
        .sorted(Comparator.comparing(SaveSummaryDto::netWorth).reversed())
        .toList();
  }
}
