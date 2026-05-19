package edu.ntnu.idatt2003.gruppe50.ui.model;

import java.math.BigDecimal;

// ui/model/WeekHolding.java
public record WeekHolding(
    String symbol,
    BigDecimal quantity,
    BigDecimal weeklyDelta,
    BigDecimal weeklyPercent
) {}
