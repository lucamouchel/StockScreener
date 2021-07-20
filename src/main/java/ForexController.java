import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
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
}
