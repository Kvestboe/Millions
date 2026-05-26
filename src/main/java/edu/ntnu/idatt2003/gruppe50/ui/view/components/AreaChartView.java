package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class AreaChartView {

  private final AreaChart<Number, Number> chart;
  private final XYChart.Series<Number, Number> series;
  private final XYChart.Series<Number, Number> buySeries  = new XYChart.Series<>();
  private final XYChart.Series<Number, Number> sellSeries = new XYChart.Series<>();

  public AreaChartView(String xlabel, String ylabel) {
    NumberAxis xaxis = new NumberAxis();
    xaxis.setAutoRanging(false);
    xaxis.setLowerBound(1);
    xaxis.setUpperBound(1);
    xaxis.setTickUnit(1);
    xaxis.setMinorTickVisible(false);
    xaxis.setForceZeroInRange(false);

    NumberAxis yaxis = new NumberAxis();

    xaxis.setLabel(xlabel);
    yaxis.setLabel(ylabel);
    series = new XYChart.Series<>();
    chart = new AreaChart<>(xaxis, yaxis);
    chart.getData().addAll(series, buySeries, sellSeries);
    chart.setAnimated(false);

    hideSeriesLineAndFill(buySeries);
    hideSeriesLineAndFill(sellSeries);
  }

  private void hideSeriesLineAndFill(XYChart.Series<Number, Number> s) {
    s.nodeProperty().addListener((_, _, node) -> {
      if (node != null) node.setStyle("-fx-stroke: transparent; -fx-fill: transparent;");
    });
  }

  public void display(String title, List<BigDecimal> history) {
    chart.setTitle(title);
    series.setName(title);
    series.getNode().setStyle("-fx-stroke: #00C9A7;");

    List<XYChart.Data<Number, Number>> points = new ArrayList<>(history.size());
    for (int i = 0; i < history.size(); i++) {
      points.add(new XYChart.Data<>(i + 1, history.get(i)));
    }
    series.getData().setAll(points);
    NumberAxis xaxis = (NumberAxis) chart.getXAxis();

    int weeks = history.size();
    xaxis.setAutoRanging(false);
    xaxis.setLowerBound(1);
    xaxis.setUpperBound(Math.max(weeks, 2));
    xaxis.setTickUnit(calculateWeekTickUnit(weeks));
    xaxis.setMinorTickVisible(false);
    xaxis.setForceZeroInRange(false);
  }

  public void setMarkers(Set<Integer> buyWeeks, Set<Integer> sellWeeks, List<BigDecimal> history) {
    buySeries.getData().setAll(toPoints(buyWeeks, history));
    sellSeries.getData().setAll(toPoints(sellWeeks, history));
  }

  private List<XYChart.Data<Number, Number>> toPoints(Set<Integer> weeks, List<BigDecimal> history) {
    List<XYChart.Data<Number, Number>> points = new ArrayList<>(weeks.size());
    for (int week : weeks) {
      int idx = week - 1;
      if (idx >= 0 && idx < history.size()) {
        points.add(new XYChart.Data<>(week, history.get(idx)));   // x = faktisk uke
      }
    }
    return points;
  }

  public void appendPoint(int index, BigDecimal value) {
    series.getData().add(new XYChart.Data<>(index, value));
  }

  public AreaChart<Number, Number> getChart() {
    return chart;
  }

  private int calculateWeekTickUnit(int weeks) {
    if (weeks <= 10) {
      return 1;
    }
    if (weeks <= 25) {
      return 5;
    }
    if (weeks <= 50) {
      return 10;
    }
    return 25;
  }
}
