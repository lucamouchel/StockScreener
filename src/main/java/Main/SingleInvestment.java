package Main;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public class SingleInvestment {
  private final String GOOGLE_QUERY = "https://www.google.com/search?q=";
  // shares bought at X price has to have the format 20x102 = 20 shares at 102 dollars or other
  // currency
  private final String symbol, sharesBought;
  private String stockValue;
  private double currentValue, valueAtPurchase;

  public SingleInvestment(String symbol, String sharesBought) {
    this.symbol = symbol;
    this.sharesBought = sharesBought;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getSharesBought() {
    return sharesBought;
  }

  public String getCurrentStockValue() throws IOException {
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    webClient.getOptions().setJavaScriptEnabled(true);
    HtmlPage page = webClient.getPage(GOOGLE_QUERY + String.format("%s+stocks", symbol));
    stockValue =
        ((HtmlSpan) page.getByXPath("//span[@jsname='vWLAgc']").get(0))
            .getTextContent()
            .replaceAll(",", ".");
    return stockValue;
  }

  public String getValueAtPurchase() {
    valueAtPurchase =
        Arrays.stream(sharesBought.split("x"))
            .mapToDouble(Double::parseDouble)
            .reduce(1, (a, b) -> a * b);
    return formattedValue(valueAtPurchase);
  }

  public String getCurrentValue() {
    stockValue = stockValue.replaceAll(" ", "");
    currentValue = Double.parseDouble(sharesBought.split("x")[0]) * Double.parseDouble(stockValue);
    return formattedValue(currentValue);
  }

  public String getGains() {
    return formattedValue(currentValue - valueAtPurchase);
  }

  private String formattedValue(double value) {
    return NumberFormat.getNumberInstance(Locale.US).format(value);
  }
}
