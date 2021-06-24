import javafx.scene.chart.LineChart;
import org.json.JSONArray;

import java.io.IOException;

public class CreateLineChart {
    //private final LineChart<Number, Number> lineChart;

    private CreateLineChart(){}

    public static void createChart(LineChart<Number, Number> lineChart, String symbol) throws IOException {
        String apiMonthlyURL = String.format("https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol=%s&apikey=H33OBY74UXT9G1Z7", symbol);
        JSONArray arr = new JSONArray(WebLinks.getJsonString(apiMonthlyURL));
    for (int i = 0; i < arr.length(); i++) {
        String data = arr.getJSONObject(i).getString("");
    }
       // List<String> monthlyStockValues = WebLinks.getJsonString(apiMonthlyURL);
    }
}
