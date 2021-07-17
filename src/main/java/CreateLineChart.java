import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateLineChart {

    private CreateLineChart() {
    }

    public static LineChart<String, Number> createChart(LineChart<String, Number> lineChart, String symbol) throws IOException {
        lineChart.setPrefHeight(310);
        lineChart.setPrefWidth(420);
        lineChart.setLayoutX(600);
        lineChart.setLayoutY(400);
        lineChart.setTitle("Stock Monitoring");
        String apiMonthlyURL = String.format("https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=%s&apikey=H33OBY74UXT9G1Z7", symbol);
        JSONObject json = new JSONObject(WebLinks.getJsonString(apiMonthlyURL)).getJSONObject("Monthly Time Series");

        List<Double> values = new ArrayList<>();
        for (String str : WorkingDays.lastyearsMonthsValues){
            values.add(Double.parseDouble(json.getJSONObject(str).getString("4. close")));
        }



        XYChart.Series<String, Number> series = new XYChart.Series<>();
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        lineChart =
                new LineChart<>(xAxis, yAxis);
        series.getData().add(new XYChart.Data<>("Jan 2020", values.get(0)));
        series.getData().add(new XYChart.Data<>("Feb", values.get(1)));
        series.getData().add(new XYChart.Data<>("Mar", values.get(2)));
        series.getData().add(new XYChart.Data<>("Apr", values.get(3)));
        series.getData().add(new XYChart.Data<>("May", values.get(4)));
        series.getData().add(new XYChart.Data<>("Jun", values.get(5)));
        series.getData().add(new XYChart.Data<>("Jul", values.get(6)));
        series.getData().add(new XYChart.Data<>("Aug", values.get(7)));
        series.getData().add(new XYChart.Data<>("Sep", values.get(8)));
        series.getData().add(new XYChart.Data<>("Oct", values.get(9)));
        series.getData().add(new XYChart.Data<>("Nov", values.get(10)));
        series.getData().add(new XYChart.Data<>("Dec", values.get(11)));
        lineChart.getData().add(series);

        lineChart.setLayoutX(50);
        return lineChart;
    }



}
