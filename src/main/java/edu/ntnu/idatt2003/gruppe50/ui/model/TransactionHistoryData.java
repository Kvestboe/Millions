package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.util.List;

/**
 * Container for TransactionData
 * <p>
 *   This is like the TransactionArchive in the model,
 *   it's a list of the TransactionData that makes it easier to
 *   pass TransactionData around. This translates to a container.
 * </p>
 */
public record TransactionHistoryData(List<TransactionData> transactionData) { }
