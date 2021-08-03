package Main;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public final class WebLinks {

  public static String getJsonString(String urlToExtractFrom) throws IOException {
    if (!urlToExtractFrom.startsWith("https")) throw new IllegalStateException("Not a valid URL");
    URL url = new URL(urlToExtractFrom);
    InputStream is = url.openConnection().getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    String line;
    StringBuilder jsonStringBuilder = new StringBuilder();
    while ((line = reader.readLine()) != null) jsonStringBuilder.append(line).append("\n");
    reader.close();
    return jsonStringBuilder.toString();
  }

  public static List<String> getNASDAQTickers() throws IOException {
    String json = getJsonString("https://dumbstockapi.com/stock?exchanges=NASDAQ");
    return new ArrayList<>(formattedJSONInfo(new JSONArray(json)));
  }

  public static List<String> getNYSETickers() throws IOException {
    String json = getJsonString("https://dumbstockapi.com/stock?exchanges=NYSE");
    return new ArrayList<>(formattedJSONInfo(new JSONArray(json)));
  }

  public static List<String> getAMEXTickers() throws IOException {
    String json = getJsonString("https://dumbstockapi.com/stock?exchanges=AMEX");
    return new ArrayList<>(formattedJSONInfo(new JSONArray(json)));
  }

  public static List<String> formattedJSONInfo(JSONArray arr) {
    List<String> tickersForGivenExchange = new ArrayList<>();
    for (int i = 0; i < arr.length(); i++) {
      String symbol =
          arr.getJSONObject(i).getString("name") + " | " + arr.getJSONObject(i).getString("ticker");
      tickersForGivenExchange.add(symbol);
    }
    return tickersForGivenExchange;
  }

  public static String getExchangeRate(String fromCurrency, String toCurrency) throws IOException {
    String link =
        String.format(
            "https://www.alphavantage.co/query?function=CURRENCY_EXCHANGE_RATE&from_currency=%s&to_currency=%s&apikey=H33OBY74UXT9G1Z7",
            fromCurrency, toCurrency);
    String json = getJsonString(link);
    JSONObject obj = new JSONObject(json);
    return obj.getJSONObject("Realtime Currency Exchange Rate").getString("5. Exchange Rate");
  }

  /////// YAHOO FINANCE API BELOW

  public static String stockOptionsJSON(String symbol) throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    String.format(
                        "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-options?symbol=%s&date=1562284800&region=US",
                        symbol)))
            .header("x-rapidapi-key", "024a0ea4ccmsh5d44517eb45e8c2p124b12jsn54dea574ba43")
            .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }

  public static String JSONChartData(String symbol, String range)
      throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    String.format(
                        "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-chart?interval=1d&symbol=%s&range=%s&region=US",
                        symbol, range)))
            .header("x-rapidapi-key", "024a0ea4ccmsh5d44517eb45e8c2p124b12jsn54dea574ba43")
            .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }

  public static String JSONNewsInfo() throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    "https://apidojo-yahoo-finance-v1.p.rapidapi.com/news/v2/list?region=US&snippetCount=10"))
            .header("content-type", "text/plain")
            .header("x-rapidapi-key", "024a0ea4ccmsh5d44517eb45e8c2p124b12jsn54dea574ba43")
            .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
            .method(
                "POST",
                HttpRequest.BodyPublishers.ofString(
                    "Pass in the value of uuids field returned right in this endpoint to load the next page, or leave empty to load first page"))
            .build();
    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }
}
