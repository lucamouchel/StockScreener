import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

  public static String getJsonString(String urlToExtractFrom) throws IOException {
    if (!urlToExtractFrom.contains("https")) throw new IllegalArgumentException("Not a valid URL");
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
}
