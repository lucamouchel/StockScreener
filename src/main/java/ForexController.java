import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ResourceBundle;

public class ForexController implements Initializable {
  private ObservableList<String> forexList;
  @FXML private ChoiceBox<String> fromCurrency, toCurrency;
  @FXML private TextField amount;
  @FXML private Text text;
  @FXML private Label errorLabel;

  @FXML
  public void searchForexExchange() {
    try {
      double parsedAmount = Double.parseDouble(amount.getText());
      double exchangeRate =
          Double.parseDouble(
              WebLinks.getExchangeRate(fromCurrency.getValue(), toCurrency.getValue()));
      text.setText(
          String.format(
              "%s %s ≈ %.3f %s",
              parsedAmount,
              fromCurrency.getValue(),
              parsedAmount * exchangeRate,
              toCurrency.getValue()));
    } catch (RuntimeException | IOException e) {
      if (fromCurrency.getValue() == null && toCurrency.getValue() == null)
        errorLabel.setText("No selected currencies");
      else if (fromCurrency.getValue() == null)
        errorLabel.setText("No selected currency to convert to " + toCurrency.getValue());
      else if (toCurrency.getValue() == null)
        errorLabel.setText("No selected currency to convert to from " + fromCurrency.getValue());
    }
  }

  @FXML
  public void reverseCurrencies() {
    if (toCurrency != null && fromCurrency != null) {
      String storedValue = fromCurrency.getValue();
      fromCurrency.setValue(toCurrency.getValue());
      toCurrency.setValue(storedValue);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    forexList =
        FXCollections.observableList(
            List.of(
                "USD", "EUR", "JPY", "GBP", "AUD", "CAD", "CHF", "CNY", "HKD", "NZD", "SEK", "KRW",
                "SGD", "MXN", "INR", "RUB", "BRL"));
    forexList.forEach(
        s -> {
          fromCurrency.getItems().add(s);
          toCurrency.getItems().add(s);
        });
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-summary?symbol=AMZN&region=US"))
            .header("x-rapidapi-key", "c58c838e38msh5e0d93eed17da54p10f366jsn41b0c8b57fe2")
            .header("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();
    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println(response.body());
  }
}
