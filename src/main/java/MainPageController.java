import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainPageController implements Initializable {
  @FXML AnchorPane root, pane;
  @FXML private TextField input, portfolioStock;
  @FXML private VBox vbox;
  @FXML private Text text;
  @FXML private VBox stockList, singleStockBox, newsBox;
  @FXML private ChoiceBox<String> rangeSelector;
  private String detectedSymbol;
  private AreaChart<String, Number> areaChart;
  @FXML private ScrollPane scrollPane;
  @FXML private Button addStock;
  @FXML private MenuButton myPortfolio;
  private List<String> bindingStocks = new ArrayList<>();

  @FXML
  public void addStock() {
    String detectedSymbol;
    String userInput = portfolioStock.getText();
    if (portfolioStock.getText().contains("|")) {
      detectedSymbol = userInput.substring(userInput.indexOf("|") + 1).replace(" ", "");
      MenuItem stock = new MenuItem(detectedSymbol);
      stock.setOnAction(e -> {
        try {
          setMenuItemAction(detectedSymbol);
        } catch (IOException | InterruptedException exception) {
          exception.printStackTrace();
        }
      });
      myPortfolio.getItems().add(stock);
    } else {
      detectedSymbol = userInput;
      if (userInput.chars().mapToObj(c -> (char) c).allMatch(Character::isUpperCase)) {
        MenuItem stock = new MenuItem(detectedSymbol);
        stock.setOnAction(e -> {
          try {
            setMenuItemAction(detectedSymbol);
          } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
          }
        });
        myPortfolio.getItems().add(stock);
      }
    }
    myPortfolio
        .getItems()
        .setAll(myPortfolio.getItems().stream().distinct().collect(Collectors.toList()));
  }

  private void setMenuItemAction(String symbol) throws IOException, InterruptedException {
    root.getChildren().remove(areaChart);
    scrollPane.setVisible(false);
    newsBox.setVisible(false);
    pane.setVisible(true);
    addSingleStockToPage(symbol);
  }



  @FXML
  public void addStockToPortfolio() {
    portfolioStock.setVisible(true);
    addStock.setVisible(true);
    TextFields.bindAutoCompletion(portfolioStock, bindingStocks);
  }

  @FXML
  public void onText() throws IOException, InterruptedException {
    root.getChildren().remove(areaChart);

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
            });
      }
    } else {
      scrollPane.setVisible(false);
      newsBox.setVisible(false);
      pane.setVisible(true);
      String userInput = input.getText();
      detectedSymbol = userInput.substring(userInput.indexOf("|") + 1).replace(" ", "");
      addSingleStockToPage(detectedSymbol);
    }
    TextFields.bindAutoCompletion(input, potentialSymbols);
  }

  private void addHbox(String info) {
    singleStockBox.getChildren().addAll(new HBox(new Text(info)), new Separator());
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
    List<String> amexTickers = new ArrayList<>();
    try {
      nasdaqTickers = WebLinks.getNASDAQTickers();
      nyseTickers = WebLinks.getNYSETickers();
      amexTickers = WebLinks.getAMEXTickers();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    bindingStocks = concatLists(nasdaqTickers, nyseTickers, amexTickers);
    TextFields.bindAutoCompletion(input, bindingStocks);

    List<String> trendingList = new ArrayList<>(List.of());
    //                "AMZN", "AAPL", "GOOGL", "TSLA", "NFLX", "FB", "MSFT", "AMD", "MRNA", "NVDA",
    //                "ORCL"));

    for (String stock : trendingList) {
      try {
        IndividualStock individualStock = new IndividualStock(stock);
        setUpVbox(
            stockList,
            stock,
            individualStock,
            Double.parseDouble(individualStock.shiftPercentage()));
      } catch (IOException | InterruptedException | JSONException exception) {
        exception.printStackTrace();
      }
    }
    rangeSelector.setValue("1 month");
    rangeSelector
        .getItems()
        .addAll("1 month", "3 months", "6 months", "1 year", "2 years", "5 years", "Max");

    // news info below

    //    try {
    //      String json = WebLinks.JSONNewsInfo();
    //      JSONObject obj = new JSONObject(json).getJSONObject("data").getJSONObject("main");
    //      JSONArray arr = obj.getJSONArray("stream");
    //      for (int i = 0; i < arr.length(); i++) {
    //        JSONObject content = arr.getJSONObject(i).getJSONObject("content");
    //        String url;
    //        try {
    //          url = content.getJSONObject("clickThroughUrl").getString("url");
    //        } catch (JSONException jsonException) {
    //          url = "www.google.com";
    //        }
    //        NewsInfo newsInfo =
    //            new NewsInfo(
    //                content.getString("title"), content.getString("pubDate").substring(0, 10),
    // url);
    //        newsBox.getChildren().addAll(newsInfo.createNewsVbox(), new Separator());
    //      }
    //    } catch (IOException | InterruptedException exception) {
    //      exception.printStackTrace();
    //    }
  }

  @SafeVarargs
  private <T> List<T> concatLists(List<T>... lists) {
    return Stream.of(lists).flatMap(List::stream).collect(Collectors.toList());
  }

  private void addSingleStockToPage(String symbol) throws IOException, InterruptedException {
    if (!singleStockBox.getChildren().isEmpty()) singleStockBox.getChildren().clear();
    rangeSelector.setVisible(true);
    IndividualStock singleStock = new IndividualStock(symbol);
    setUpVbox(
            singleStockBox,
            symbol,
            singleStock,
            Double.parseDouble(singleStock.shiftPercentage()));
    addHbox(singleStock.getName());
    addHbox(singleStock.getExchange());
    addHbox(singleStock.getFiftyTwoWeekLow());
    addHbox(singleStock.getFiftyTwoWeekHigh());
    addHbox(singleStock.getMarketDayRange());
    addHbox(singleStock.previousDayClose());
    addHbox(singleStock.getMarketCap());
    addHbox(singleStock.getShareVolume());
    areaChart = CreateLineChart.createChart(symbol, rangeSelector.getValue());
    root.getChildren().add(areaChart);
    rangeSelector.setOnAction(
            e -> {
              try {
                root.getChildren().remove(areaChart);
                areaChart = CreateLineChart.createChart(symbol, rangeSelector.getValue());
                root.getChildren().add(areaChart);
              } catch (IOException | InterruptedException exception) {
                exception.printStackTrace();
              }
            });
  }

  private void setUpVbox(
      VBox stockToAdd, String stock, IndividualStock individualStock, double shiftPercentage)
      throws IOException, InterruptedException {
    Color color = shiftPercentage < 0 ? Color.RED : Color.GREEN;
    FontIcon arrow =
        new FontIcon(color.equals(Color.RED) ? "fa-long-arrow-down" : "fa-long-arrow-up");
    arrow.setIconColor(color);
    stockToAdd.setSpacing(10);
    stockToAdd
        .getChildren()
        .add(
            new HBox(
                20,
                textWithColor(stock, Color.BLACK),
                textWithColor(individualStock.getLastDatedStockValue(), color),
                textWithColor(individualStock.differenceFromPreviousDay(), color),
                textWithColor(shiftPercentage + "%", color),
                arrow));
    stockToAdd.getChildren().add(new Separator());
  }

  private Text textWithColor(String text, Color color) {
    Text textWithColor = new Text(text);
    textWithColor.setFill(Paint.valueOf(String.valueOf(color)));
    return textWithColor;
  }
}
