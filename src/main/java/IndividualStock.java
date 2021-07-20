import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public final class IndividualStock {
  public static ArrayList<String> dates = new ArrayList<>();
  public static String chartCurrency;
  private final String symbol;
  private final JSONObject json;
  private final String currency;

  public IndividualStock(String symbol) throws IOException, InterruptedException {
    this.symbol = symbol;
    this.json =
        new JSONObject(WebLinks.stockOptionsJSON(symbol))
            .getJSONObject("meta")
            .getJSONObject("quote");
    this.currency = json.getString("currency");
  }

  public static ArrayList<Double> chartData(String symbol, String range)
      throws IOException, InterruptedException {
    ArrayList<Double> chartData = new ArrayList<>();
    if (!dates.isEmpty()) dates.clear();
    Map<String, String> rangeConverter =
        Map.of(
            "1 month",
            "1mo",
            "3 months",
            "3mo",
            "6 months",
            "6mo",
            "1 year",
            "1y",
            "2 years",
            "2y",
            "5 years",
            "5y",
            "Max",
            "max");
    String convertedRange = rangeConverter.get(range);
    String json = WebLinks.JSONChartData(symbol, convertedRange);
    JSONArray arr = new JSONObject(json).getJSONObject("chart").getJSONArray("result");
    for (int i = 0; i < arr.length(); i++) {
      chartCurrency = arr.getJSONObject(i).getJSONObject("meta").getString("currency");
      JSONArray timeStamp = arr.getJSONObject(i).getJSONArray("timestamp");
      JSONArray quote = arr.getJSONObject(i).getJSONObject("indicators").getJSONArray("quote");
      for (int j = 0; j < quote.length(); j++) {
        JSONArray close = quote.getJSONObject(j).getJSONArray("close");
        // close size == timestamp size -- every close value has a date
        for (int k = 0; k < close.length(); k++) {
          chartData.add(Double.parseDouble(String.format("%.2f", close.getDouble(k))));
          Calendar cal = Calendar.getInstance();
          cal.setTime(new Date(timeStamp.getInt(k) * 1000L));
          dates.add(
              String.format(
                  "%s/%s/%s",
                  cal.get(Calendar.DATE), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)));
        }
      }
    }
    return chartData;
  }

  public String previousDayClose() {
    try {
      return "Previous close: " + json.getDouble("regularMarketPreviousClose") + " " + currency;
    } catch (JSONException e) {
      return "NA";
    }
  }

  public String getLastDatedStockValue() {
    try {
      double lastPriced = json.getDouble("regularMarketPrice");
      return String.format("%.2f", lastPriced) + " " + currency;
    } catch (JSONException e) {
      return "NA";
    }
  }

  public String shiftPercentage() throws IOException, InterruptedException {
    return String.format("%.2f", json.getDouble("regularMarketChangePercent"));
  }

  public String differenceFromPreviousDay() {
    return String.format("%.2f", json.getDouble("regularMarketChange"));
  }

  public String getName() {
    return "Name: " + json.getString("longName");
  }

  public String getExchange() {
    return "Exchange: " + json.getString("fullExchangeName");
  }

  public String getFiftyTwoWeekLow() {
    return "52w low: " + json.getDouble("fiftyTwoWeekLow") + " " + currency;
  }

  public String getFiftyTwoWeekHigh() {
    return "52w high: " + json.getDouble("fiftyTwoWeekHigh") + " " + currency;
  }

  public String getMarketDayRange() {
    return "Today's high - lows: "
        + json.getString("regularMarketDayRange")
        + " "
        + json.getString("currency");
  }

  public String getMarketCap() {
    return "Market cap: " + String.format("%,.2f", json.getDouble("marketCap")) + " " + currency;
  }

  public String getShareVolume() {
    return "Share Volume: "
        + String.format("%,.2f", json.getDouble("regularMarketVolume"))
        + " "
        + currency;
  }
}
