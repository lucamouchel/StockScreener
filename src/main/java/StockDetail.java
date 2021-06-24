import javafx.scene.text.Text;
import org.json.JSONObject;

import java.io.IOException;

public final class StockDetail {
  private final String name, symbol;
  private final Text companyOverView;
  private JSONObject json;
  //    private final Text details;

  public StockDetail(String name, String symbol, Text companyOverview) throws IOException {
    this.name = name;
    this.symbol = symbol;
    this.json = new JSONObject(WebLinks.getJsonString(WebLinks.companyOverview(symbol)));
    this.companyOverView = companyOverview;
  }

  public StockDetail setCompanyDescription() {
    try {
      companyOverView.setText(
          String.format(
              "%s %s is part of the %s exchange and works in the %s sector.",
              json.getString("Description"), name, json.getString("Exchange"), json.get("Sector")));
    } catch (Exception ignored) {

    }
    return this;
  }

  public StockDetail setDetails() {
    return this;
  }
}
