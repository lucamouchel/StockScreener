import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WebData implements Initializable {
  public List<String> nasdaqTickers = new ArrayList<>();
  public List<String> nyseTickers = new ArrayList<>();
  @FXML AnchorPane root;
  @FXML private TextField input;
  @FXML private VBox vbox;
  @FXML private Text text;
  @FXML private DatePicker datePicker;
  @FXML private LineChart<Number, Number> lineChart;

  public void setListItems() throws IOException {}

  public void onText() throws IOException {
    String symbolToFindURL = WebLinks.findSymbol(input.getText());
    List<String> potentialSymbols = new ArrayList<>();
    if (!vbox.getChildren().isEmpty()) {
      vbox.getChildren().clear();
      text.setText("");
      //detailText.setText("");
    }

    if (!input.getText().contains("|")) {
      String json = WebLinks.getJsonString(symbolToFindURL);
      JSONObject obj = new JSONObject(json);
      JSONArray arr = obj.getJSONArray("bestMatches");
      for (int i = 0; i < arr.length(); i++) {
        String symbol = arr.getJSONObject(i).getString("1. symbol");
        potentialSymbols.add(symbol);

        Button symbolButton = new Button(symbol);
        symbolButton.setStyle("-fx-background-color: grey; -fx-padding: 3");
        vbox.getChildren().add(symbolButton);

        int finalI = i;
        symbolButton.setOnAction(
            e -> {
              String companyName = arr.getJSONObject(finalI).getString("2. name");
              try {
                new StockDetail(companyName, symbol, text).setCompanyDescription().setDetails();
              } catch (IOException exception) {
                exception.printStackTrace();
              }
            });
      }
    }else{
      String userInput = input.getText();
      String detectedSymbol = userInput.substring(userInput.indexOf("|") + 1).replace(" ", "");
      new StockDetail(
              userInput.substring(0, userInput.indexOf("|") - 1), detectedSymbol, text)
              .setCompanyDescription();
    }
    TextFields.bindAutoCompletion(input, potentialSymbols);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      nasdaqTickers = WebLinks.getNASDAQTickers();
      nyseTickers = WebLinks.getNYSETickers();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    List<String> possibleSymbols =
        Stream.concat(nasdaqTickers.stream(), nyseTickers.stream()).collect(Collectors.toList());
    TextFields.bindAutoCompletion(input, possibleSymbols);
  }
}
