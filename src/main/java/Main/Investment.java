package Main;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Investment {
  public static final String GOOGLE_QUERY = "https://www.google.com/search?q=";
  public static final String BING_QUERY = "https://www.bing.com/search?q=";

  // shares bought at X price has to have the format 20x102 = 20 shares at 102 dollars or other
  // currency
  private final String symbol, sharesBought;
  private String stockValue;
  private double currentValue, valueAtPurchase;

  public Investment(String symbol, String sharesBought) {
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
    //this turns off all warnings and exceptions from HtmlUnit
    Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    WebClient webClient = new WebClient(BrowserVersion.CHROME);
    webClient.getOptions().setJavaScriptEnabled(true);
    webClient.getOptions().setThrowExceptionOnScriptError(false);
    HtmlPage page = webClient.getPage(BING_QUERY + String.format("%s+stocks", symbol));
    stockValue =
        ((HtmlElement) page.getByXPath("//div[@class='b_focusTextMedium']").get(0))
            .getTextContent()
            .replaceAll(",", ".");
    return stockValue;
  }

  public double getValueAtPurchase() {
    valueAtPurchase =
        Arrays.stream(sharesBought.split("x"))
            .mapToDouble(Double::parseDouble)
            .reduce(1, (a, b) -> a * b);
    return valueAtPurchase;
  }

  public double getCurrentValue() {
    stockValue = stockValue.replaceAll(" ", "");
    currentValue = Double.parseDouble(sharesBought.split("x")[0]) * Double.parseDouble(stockValue);
    return currentValue;
  }

  public double getGains() {
    return currentValue - valueAtPurchase;
  }
}
