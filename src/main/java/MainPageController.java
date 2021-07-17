import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainPageController implements Initializable {
  @FXML AnchorPane root, pane;
  @FXML private TextField input;
  @FXML private VBox vbox;
  @FXML private Text text, stockName, stockValue, percentage;
  @FXML private DatePicker datePicker;
  @FXML private LineChart<Number, Number> lineChart;
  @FXML private FontIcon arrow;
  @FXML private VBox big;

  public void setListItems() throws IOException {}

  @FXML
  public void onText() throws IOException, InterruptedException {
    String symbolToFindURL = WebLinks.findSymbol(input.getText());
    List<String> potentialSymbols = new ArrayList<>();
    if (!vbox.getChildren().isEmpty()) {
      vbox.getChildren().clear();
      text.setText("");
      // detailText.setText("");
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
    } else {
      stockValue.setVisible(true);
      stockName.setVisible(true);
      pane.setVisible(true);
      percentage.setVisible(true);
      arrow.setVisible(true);

      String userInput = input.getText();
      String detectedSymbol = userInput.substring(userInput.indexOf("|") + 1).replace(" ", "");
      new StockDetail(userInput.substring(0, userInput.indexOf("|") - 1), detectedSymbol, text)
          .setCompanyDescription();
      stockName.setText(detectedSymbol);
      System.out.println(detectedSymbol);
      IndividualStock individualStock = new IndividualStock(detectedSymbol);
      stockValue.setText(individualStock.lastRefreshedStockValue(detectedSymbol));
      JSONObject obj =
          new JSONObject(WebLinks.getJsonString(WebLinks.openSymbolDaily(detectedSymbol)));

      //      root.getChildren()
      //          .add(
      //              CreateLineChart.createChart(
      //                  new LineChart(new CategoryAxis(), new CategoryAxis()), detectedSymbol));
    }
    TextFields.bindAutoCompletion(input, potentialSymbols);
  }

  @FXML
  public void openForexWindow() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(Main.class.getResource("forex.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 392, 216);
    Stage stage = new Stage();
    stage.setTitle("Forex");
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    List<String> nasdaqTickers = new ArrayList<>();
    List<String> nyseTickers = new ArrayList<>();
    // List<String> trendingStocks = new ArrayList<>();
    try {
      nasdaqTickers = WebLinks.getNASDAQTickers();
      nyseTickers = WebLinks.getNYSETickers();
      // trendingStocks = TrendingStocks.trendingStocks();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    List<String> possibleSymbols =
        Stream.concat(nasdaqTickers.stream(), nyseTickers.stream()).collect(Collectors.toList());
    TextFields.bindAutoCompletion(input, possibleSymbols);

    List<String> trendingList =
        new ArrayList<>(
            List.of(
                "AMZN", "AAPL", "GOOGL", "TSLA", "NFLX", "FB", "MSFT", "AMD", "MRNA", "NVDA",
                "ORCL"));

    for (String stock : new HashSet<>(trendingList)) {
      try {
        IndividualStock individualStock = new IndividualStock(stock);
        setUpAllVboxes(stock, individualStock, Double.parseDouble(individualStock.shiftPercentage()));
      } catch (IOException | InterruptedException exception) {
        exception.printStackTrace();
      }
    }
  }

  private void setUpAllVboxes(String stock, IndividualStock individualStock, double shiftPercentage)
      throws IOException, InterruptedException {
    Color color = shiftPercentage < 0 ? Color.RED : Color.GREEN;
    FontIcon arrow =
        new FontIcon(color.equals(Color.RED) ? "fa-long-arrow-down" : "fa-long-arrow-up");
    arrow.setIconColor(color);
    big.setSpacing(10);
    big.getChildren()
        .add(
            new HBox(
                40,
                textWithColor(stock, Color.BLACK),
                textWithColor(individualStock.getLastDatedStockValue(), color),
                textWithColor(individualStock.differenceFromPreviousDay(), color),
                textWithColor(shiftPercentage + "%", color),
                arrow));
    big.getChildren().add(new Separator());
  }

  private Text textWithColor(String text, Color color) {
    Text textWithColor = new Text(text);
    textWithColor.setFill(Paint.valueOf(String.valueOf(color)));
    return textWithColor;
  }
}
