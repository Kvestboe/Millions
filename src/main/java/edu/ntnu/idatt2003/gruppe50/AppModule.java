package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.command.AdvanceWeekUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.BuyCoinsUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceBuyLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceSellLimitOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PlaceStopLossOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.PurchaseShopItemUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.StartGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.DeleteSaveUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetAllSavesUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetGoalProgressUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetMarketUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetPendingOrdersUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetStockDetailUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetTradingLogUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetTransactionMarkersUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetTransactionsUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.Leaderboard;
import edu.ntnu.idatt2003.gruppe50.domain.market.StockDataSource;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.infrastructure.csv.CsvStockDataSource;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.JsonFileGameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.LeaderboardFileHandler;
import edu.ntnu.idatt2003.gruppe50.ui.view.SoundManager;
import java.util.UUID;

public final class AppModule {
  private final TransactionFactory transactionFactory = new TransactionFactory();
  private final GameSessionRepository sessions = new JsonFileGameSessionRepository(transactionFactory);
  public final StockDataSource stockDataSource = new CsvStockDataSource();

  // Use cases
  public final StartGameSessionUseCase startGameSession = new StartGameSessionUseCase(sessions);
  public final LoadGameSessionUseCase loadGameSession = new LoadGameSessionUseCase(sessions);
  public final BuyShareUseCase buyShare = new BuyShareUseCase(sessions);
  public final SellShareUseCase sellShare = new SellShareUseCase(sessions);
  public final AdvanceWeekUseCase advanceWeek = new AdvanceWeekUseCase(sessions);
  public final GetPortfolioUseCase getPortfolio = new GetPortfolioUseCase(sessions);
  public final GetTransactionsUseCase getTransactions = new GetTransactionsUseCase(sessions);
  public final GetTradingLogUseCase getTradingLog = new GetTradingLogUseCase(sessions);
  public final GetMarketUseCase getMarket = new GetMarketUseCase(sessions);
  public final GetStockDetailUseCase getStockDetail = new GetStockDetailUseCase(sessions);
  public final GetTransactionMarkersUseCase getTransactionMarkers = new GetTransactionMarkersUseCase(sessions);
  public final PlaceBuyLimitOrderUseCase buyLimitOrder = new PlaceBuyLimitOrderUseCase(sessions);
  public final PlaceSellLimitOrderUseCase sellLimitOrder = new PlaceSellLimitOrderUseCase(sessions);
  public final GetPendingOrdersUseCase getPendingOrders = new GetPendingOrdersUseCase(sessions);
  public final PlaceStopLossOrderUseCase stopLossOrder = new PlaceStopLossOrderUseCase(sessions);
  public final PreviewOrderUseCase previewOrder = new PreviewOrderUseCase(sessions);
  public final GetAllSavesUseCase getAllSaves = new GetAllSavesUseCase(sessions);
  public final DeleteSaveUseCase deleteSave = new DeleteSaveUseCase(sessions);
  public final LeaderboardFileHandler leaderboardFile = new LeaderboardFileHandler();
  public final Leaderboard leaderboard = leaderboardFile.load();
  public final SoundManager soundManager = new SoundManager();
  public final BuyCoinsUseCase buyCoins = new BuyCoinsUseCase(sessions);
  public final PurchaseShopItemUseCase purchaseShopItem = new PurchaseShopItemUseCase(sessions);
  public final GetGoalProgressUseCase getGoalProgress = new GetGoalProgressUseCase(sessions);

  public GameSessionControllerBundle gameBundle(UUID gameId) {
    GameSession session = sessions.findById(gameId).orElseThrow(GameSessionNotFoundException::new);
    return new GameSessionControllerBundle(this, session);
  }
}
