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
      points.add(new XYChart.Data<>(i, history.get(i)));
    }
    series.getData().setAll(points);
  }

  public void setMarkers(Set<Integer> buyWeeks, Set<Integer> sellWeeks, List<BigDecimal> history) {
    buySeries.getData().setAll(toPoints(buyWeeks, history));
    sellSeries.getData().setAll(toPoints(sellWeeks, history));
  }

  private List<XYChart.Data<Number, Number>> toPoints(Set<Integer> weeks, List<BigDecimal> history) {
    List<XYChart.Data<Number, Number>> points = new ArrayList<>(weeks.size());
    for (int week : weeks) {
      if (week < history.size()) {
        points.add(new XYChart.Data<>(week, history.get(week)));
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
}
