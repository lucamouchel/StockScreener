package Main;

import javafx.scene.Cursor;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.JavaTools;
import utils.WebTools;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static Main.SingleInvestment.GOOGLE_QUERY;
import static utils.JFXTools.textWithColor;

public final class TrendingStocks {
  public static void createTrendingStocksBox(VBox stockList)
      throws IOException, InterruptedException {
    stockList.setSpacing(10);
    JSONObject obj = new JSONObject(trendingStocksInJson());
    JSONArray result = obj.getJSONObject("finance").getJSONArray("result");
    for (int i = 0; i < result.length(); i++) {
      JSONArray quotes = result.getJSONObject(i).getJSONArray("quotes");
      for (int j = 0; j < quotes.length(); j++) {
        // individuating all the quotes from the array
        JSONObject quote = quotes.getJSONObject(j);
        double marketChange = quote.getDouble("regularMarketChange");
        FontIcon arrow = new FontIcon(marketChange < 0 ? "fa-long-arrow-down" : "fa-long-arrow-up");
        Color color = marketChange < 0 ? Color.RED : Color.GREEN;
        arrow.setIconColor(color);
        String symbol = quote.getString("symbol");
        HBox trendingStock =
            new HBox(
                // adding essentiel data - symbol, market price, percentages and shift from previous
                // day
                20,
                textWithColor(symbol, Color.BLACK),
                textWithColor(String.format("%.2f", quote.getDouble("regularMarketPrice")), color),
                textWithColor(
                    String.format("%s%.2f", color.equals(Color.GREEN) ? "+" : "", marketChange),
                    color),
                textWithColor(
                    String.format("%.2f", quote.getDouble("regularMarketChangePercent")) + "%",
                    color),
                arrow);
        trendingStock.setOnMouseClicked(e -> WebTools.searchWebForStock(symbol));
        trendingStock.setCursor(Cursor.HAND);
        stockList.getChildren().addAll(trendingStock, new Separator());
      }
    }
  }

  private static String trendingStocksInJson() throws IOException, InterruptedException {
    // creates the request to the api to extract the data from
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    "https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/get-trending-tickers?region=US"))
            .header("x-rapidapi-key", "024a0ea4ccmsh5d44517eb45e8c2p124b12jsn54dea574ba43")
            .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }
}
