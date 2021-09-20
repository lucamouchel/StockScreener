package Main;

import javafx.scene.Cursor;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class CreateLineChart {
  private CreateLineChart() {}

  public static AreaChart<String, Number> createChart(String symbol, String rangeSelected)
      throws IOException, InterruptedException {

    ArrayList<Double> values = Stock.chartData(symbol, rangeSelected);
    ArrayList<String> dates = Stock.dates;
    Map<String, Range> ranges =
        Map.of(
            "1 month", Range.ONE_MONTH,
            "3 months", Range.THREE_MONTHS,
            "6 months", Range.SIX_MONTHS,
            "1 year", Range.ONE_YEAR,
            "2 years", Range.TWO_YEARS,
            "5 years", Range.FIVE_YEARS,
            "Max", Range.MAX_YEARS);
    XYChart.Series<String, Number> series = new XYChart.Series<>();

    series.setName(rangeSelected);
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    yAxis.setLabel(Stock.chartCurrency);

    AreaChart<String, Number> areaChart = new AreaChart<>(xAxis, yAxis);
    areaChart.setPrefHeight(400);
    areaChart.setPrefWidth(494);
    areaChart.setLayoutX(450);
    areaChart.setLayoutY(250);
    areaChart.setTitle("Stock Monitoring - " + symbol);

    assert ranges.containsKey(rangeSelected);
    yAxis.setAutoRanging(false);
    yAxis.setLowerBound(values.stream().mapToInt(Double::intValue).min().orElse(0) - 2);
    yAxis.setUpperBound(values.stream().mapToInt(Double::intValue).max().orElse(0) + 3);

    switch (ranges.get(rangeSelected)) {
      case ONE_MONTH:
        for (int i = 0; i < values.size(); i++) setDataToChart(values, dates, series, i);
        break;
      case THREE_MONTHS, MAX_YEARS:
        for (int i = 0; i < values.size(); i += 3) setDataToChart(values, dates, series, i);
        break;
      case SIX_MONTHS:
        for (int i = 0; i < values.size(); i += 5) setDataToChart(values, dates, series, i);
        break;
      case ONE_YEAR:
        for (int i = 0; i < values.size(); i += 10) setDataToChart(values, dates, series, i);
        break;
      case TWO_YEARS:
        for (int i = 0; i < values.size(); i += 20) setDataToChart(values, dates, series, i);
        break;
      case FIVE_YEARS:
        for (int i = 0; i < values.size(); i += 30) setDataToChart(values, dates, series, i);
        break;
    }
    xAxis.setAutoRanging(true);
    areaChart.getData().add(series);
    return areaChart;
  }

  private static void setDataToChart(
      ArrayList<Double> values,
      ArrayList<String> dates,
      XYChart.Series<String, Number> series,
      int i) {
    XYChart.Data<String, Number> data = new XYChart.Data<>(dates.get(i), values.get(i));
    series.getData().add(data);
    data.setNode(new HoveredThresholdNode((i == 0) ? 0 : values.get(i - 1), values.get(i)));
  }

  private enum Range {
    ONE_MONTH,
    THREE_MONTHS,
    SIX_MONTHS,
    ONE_YEAR,
    TWO_YEARS,
    FIVE_YEARS,
    MAX_YEARS
  }

  private static class HoveredThresholdNode extends StackPane {
    HoveredThresholdNode(double priorValue, double value) {
      setPrefSize(10, 10);
      final Label label = createDataThresholdLabel(priorValue, value);

      setOnMouseEntered(
          mouseEvent -> {
            getChildren().setAll(label);
            setCursor(Cursor.NONE);
            toFront();
          });
      setOnMouseExited(
          mouseEvent -> {
            getChildren().clear();
            setCursor(Cursor.DEFAULT);
          });
    }

    private Label createDataThresholdLabel(double priorValue, double value) {
      final Label label = new Label(value + "");
      label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
      label.setStyle("-fx-font-size: 10; -fx-font-weight: bold;");
      if (value > priorValue) {
        label.setTextFill(Color.FORESTGREEN);
      } else {
        label.setTextFill(Color.FIREBRICK);
      }
      label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
      return label;
    }
  }
}
