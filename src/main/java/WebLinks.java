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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class WebLinks {
  public static final String GOOGLE_QUERY = "https://www.google.com/search?q=";

  public static String openSymbolDaily(String symbol) {
    return String.format(
        "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=%s&apikey=H33OBY74UXT9G1Z7",
        symbol);
  }

  public static String findSymbol(String input) {
    return String.format(
        "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=%s&apikey=H33OBY74UXT9G1Z7",
        input);
  }

  public static String companyOverview(String company) {
    return String.format(
        "https://www.alphavantage.co/query?function=OVERVIEW&symbol=%s&apikey=H33OBY74UXT9G1Z7",
        company);
  }

  public static String openIntraday(String symbol) {
    return String.format(
        "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=%s&interval=30min&apikey=H33OBY74UXT9G1Z7",
        symbol);
  }

  public static String getCompanyInfo(String symbol, LocalDate date) throws IOException {
    String json = getJsonString(openSymbolDaily(symbol));
    JSONObject obj = new JSONObject(json).getJSONObject("Time Series (Daily)");
    if (date != null)
      return obj.getJSONObject(
              String.format(
                  "%s-%s-%s",
                  date.getYear(),
                  appendPrefixIfNecessary(date.getMonthValue()),
                  appendPrefixIfNecessary(date.getDayOfMonth() - 1)))
          .getString("1. open");
    else {
      Calendar cal = Calendar.getInstance();
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      cal.add(Calendar.DATE, -1);
      return obj.getJSONObject(dateFormat.format(cal.getTime())).getString("1. open");
    }
  }

  private static String appendPrefixIfNecessary(int i) {
    return i < 10 ? "0" + i : String.valueOf(i);
  }

  public static String getStockValueDaily(String symbol, String date) throws IOException {
    return new JSONObject(getJsonString(openSymbolDaily(symbol)))
        .getJSONObject("Time Series (Daily)")
        .getJSONObject(date)
        .getString("4. close");
  }

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

  ///////YAHOO FINANCE API BELOW

  public static String stockOptionsJSON(String symbol) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-options?symbol=%s&date=1562284800&region=US", symbol)))
            .header("x-rapidapi-key", "c58c838e38msh5e0d93eed17da54p10f366jsn41b0c8b57fe2")
            .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }

  public static String stockHistoryJSON(String symbol) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v3/get-historical-data?symbol=%s&region=US", symbol)))
            .header("x-rapidapi-key", "c58c838e38msh5e0d93eed17da54p10f366jsn41b0c8b57fe2")
            .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println(stockOptionsJSON("TSLA"));
  }
}
