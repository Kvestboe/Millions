package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import java.math.BigDecimal;
import java.util.List;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class AreaChartView {
  private final AreaChart<Number, Number> chart;
  private final XYChart.Series<Number, Number> series;

  public AreaChartView(String xLabel, String yLabel){
    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();

    xAxis.setLabel(xLabel);
    yAxis.setLabel(yLabel);
    series = new XYChart.Series<>();
    chart = new AreaChart<>(xAxis, yAxis);
    chart.getData().add(series);
    chart.setAnimated(false);
  }

  public void display(String title, List<BigDecimal> history) {
    chart.setTitle(title);
    series.setName(title);
    series.getNode().setStyle("-fx-stroke: #00C9A7;");
    series.getData().clear();
    for (int i = 0; i < history.size(); i++) {
      series.getData().add(new XYChart.Data<>(i, history.get(i)));
    }
  }

  public void appendPoint(int index, BigDecimal value) {
    series.getData().add(new XYChart.Data<>(index, value));
  }

  public AreaChart<Number, Number> getChart() {
    return chart;
  }
}
