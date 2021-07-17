import org.json.JSONObject;

import java.io.IOException;

public final class IndividualStock {
  private final String symbol, json, lastRefreshed, previousDayClose;
  private final double previousDayValue, lastRefreshedValue;

  public IndividualStock(String symbol) throws IOException, InterruptedException {
    this.symbol = symbol;
    this.json = WebLinks.stockHistoryJSON(symbol);
    this.lastRefreshed =
        String.format(
            "%.2f",
            new JSONObject(json).getJSONArray("prices").getJSONObject(0).getDouble("close"));
    this.previousDayClose =
        String.valueOf(
            new JSONObject(WebLinks.stockOptionsJSON(symbol))
                .getJSONObject("meta")
                .getJSONObject("quote")
                .getDouble("regularMarketPreviousClose"));
    this.previousDayValue = Double.parseDouble(previousDayClose);
    this.lastRefreshedValue = Double.parseDouble(lastRefreshed);
  }

  public static String lastRefreshedStockValue(String symbol) throws IOException {
    JSONObject obj2 = new JSONObject(WebLinks.getJsonString(WebLinks.openIntraday(symbol)));
    String lastRefreshedTimeDate = obj2.getJSONObject("Meta Data").getString("3. Last Refreshed");
    String lastRefreshedValue =
        obj2.getJSONObject("Time Series (30min)")
            .getJSONObject(lastRefreshedTimeDate)
            .getString("4. close");
    return String.format("%.2f", Double.parseDouble(lastRefreshedValue));
  }

  public String previousDayClose() {
    return previousDayClose;
  }

  public String getLastDatedStockValue() {
    return lastRefreshed;
  }

  public String shiftPercentage() throws IOException, InterruptedException {
    double shift = (lastRefreshedValue - previousDayValue) / previousDayValue * 100;
    return String.format("%.2f", shift);
  }

  public String differenceFromPreviousDay() {
    return String.format("%.2f", lastRefreshedValue - previousDayValue);
  }
}
