package Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import utils.JFXTools;
import utils.JavaTools;
import utils.WebTools;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
  private final File dataFile = new File("src/main/resources/data.txt");
  @FXML AnchorPane root, pane;
  @FXML
  private TextField input, portfolioStock, cryptoName, otherStock, sharesBought, symbolInvested;
  @FXML private ChoiceBox<String> rangeSelector;
  @FXML private ScrollPane scrollPane;
  @FXML private Button addStockToPFolio;
  @FXML private MenuButton myPortfolio;
  @FXML private Label stockPresentLabel;
  @FXML private Text news, totalGains, totalCurrentValue, totalSpent;
  @FXML
  private VBox stockList,
      singleStockBox,
      newsBox,
      symbolBox,
      currentStockValueBox,
      sharesBoughtBox,
      valueAtPurchase,
      currentValueBox,
      profitsBox;
  private AreaChart<String, Number> areaChart;
  private FileOutputStream dataOutputStream;
  private List<String> supportedStocks;
  private WebDriver driver;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\chromedriver.exe");
      TrendingStocks.createTrendingStocksBox(stockList);
      supportedStocks =
          JavaTools.concatLists(
              WebLinks.getNASDAQTickers(), WebLinks.getAMEXTickers(), WebLinks.getNYSETickers());
      TextFields.bindAutoCompletion(input, supportedStocks);
      dataOutputStream = new FileOutputStream(dataFile, true);
      BufferedReader dataReader = new BufferedReader(new FileReader(dataFile));
      String line;
      // in the .txt file the formatting will be in the form symbol|100x301 for investments (the
      // second part being the number of shares by the value of each share) or simply SYMBOL for a
      // stock in the portfolio
      double spent = 0, currentValue = 0, gains = 0;
      while ((line = dataReader.readLine()) != null) {
        if (line.contains("|")) {
          String[] investment = line.split("\\|");
          SingleInvestment correspondingInvestment =
              new SingleInvestment(investment[0], investment[1]);
          addInvestmentDataToVboxes(correspondingInvestment);
          spent += correspondingInvestment.getValueAtPurchase();
          currentValue += correspondingInvestment.getCurrentValue();
          gains += correspondingInvestment.getGains();
        } else {
          MenuItem item = new MenuItem(line);
          String finalLine = line;
          item.setOnAction(
              e -> {
                try {
                  addSingleStockToPage(finalLine);
                } catch (IOException | InterruptedException exception) {
                  exception.printStackTrace();
                }
              });
          myPortfolio.getItems().add(item);
        }
      }
      totalCurrentValue.setText(JavaTools.formattedValue(currentValue));
      totalSpent.setText(JavaTools.formattedValue(spent));
      totalGains.setText(JavaTools.formattedValue(gains));

      String json = WebLinks.JSONNewsInfo();
      JSONObject obj = new JSONObject(json).getJSONObject("data").getJSONObject("main");
      JSONArray arr = obj.getJSONArray("stream");
      for (int i = 0; i < arr.length(); i++) {
        JSONObject content = arr.getJSONObject(i).getJSONObject("content");
        String title = content.getString("title");
        String url;
        try {
          url = content.getJSONObject("clickThroughUrl").getString("url");
        } catch (JSONException jsonException) {
          url = "https://www.google.com/search?q=" + title.replaceAll(" ", "+");
        }
        NewsInfo newsInfo = new NewsInfo(title, content.getString("pubDate").substring(0, 10), url);
        newsBox.getChildren().addAll(newsInfo.createNewsVbox(), new Separator());
      }
    } catch (IOException | InterruptedException exception) {
      exception.printStackTrace();
    }
    rangeSelector
        .getItems()
        .addAll("1 month", "3 months", "6 months", "1 year", "2 years", "5 years", "Max");
    rangeSelector.setValue("1 month");
  }

  @FXML
  public void onInputEntered() throws IOException, InterruptedException {
    root.getChildren().remove(areaChart);
    String userInput = input.getText();
    String detectedSymbol = userInput.substring(userInput.indexOf("|") + 1).replace(" ", "");
    addSingleStockToPage(detectedSymbol);
  }

  @FXML
  public void addStockToPortfolio() throws IOException {
    String detectedSymbol;
    String userInput = portfolioStock.getText();
    MenuItem stock;
    detectedSymbol =
        portfolioStock.getText().contains("|")
            ? userInput.substring(userInput.indexOf("|") + 1).replace(" ", "")
            : userInput;
    stock = new MenuItem(detectedSymbol);
    stock.setOnAction(
        e -> {
          try {
            setMenuItemAction(detectedSymbol);
          } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
          }
        });
    JFXTools.verifyPortfolioContainsStock(myPortfolio.getItems(), stock, stockPresentLabel);
    dataOutputStream.write(
        JavaTools.mergeStrings(detectedSymbol, System.getProperty("line.separator")).getBytes());
  }

  private void setMenuItemAction(String symbol) throws IOException, InterruptedException {
    root.getChildren().remove(areaChart);
    addSingleStockToPage(symbol);
  }

  private void addSingleStockToPage(String symbol) throws IOException, InterruptedException {
    root.getChildren().remove(areaChart);
    news.setText("");
    scrollPane.setVisible(false);
    newsBox.setVisible(false);
    pane.setVisible(true);
    if (!singleStockBox.getChildren().isEmpty()) singleStockBox.getChildren().clear();
    rangeSelector.setVisible(true);
    IndividualStock singleStock = new IndividualStock(symbol);
    JFXTools.setUpVbox(
        singleStockBox, symbol, singleStock, Double.parseDouble(singleStock.shiftPercentage()));
    new LinkedList<>(singleStock.dataList()).forEach(this::addHbox);
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

  private void addHbox(String info) {
    singleStockBox.getChildren().addAll(new HBox(new Text(info)), new Separator());
  }

  @FXML
  public void openForexWindow() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(Main.class.getResource("/forex.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 392, 216);
    Stage stage = new Stage();
    stage.setTitle("Forex");
    stage.setScene(scene);
    stage.show();
  }

  @FXML
  public void openCryptoWebsite() {
    cryptoName.setVisible(true);
  }

  @FXML
  public void searchForOtherStocks() {
    otherStock.setVisible(true);
  }

  @FXML
  public void searchWebForStock() {
    WebTools.searchWebForStock(otherStock.getCharacters());
  }

  @FXML
  public void openCrypto() {
    WebTools.openCryptoWebPage(cryptoName.getText());
  }

  @FXML
  public void addInvestment() throws IOException {
    String symbol = symbolInvested.getText(), shares = sharesBought.getText();
    try {
      if (!shares.contains("x") || shares.split("x").length > 2) {
        sharesBought.setText("Wrong Format");
        return;
      }
      // parse the first element which is supposed to be an int, if it isnt, exception thrown, ie
      // number format is incorrect
      Integer.parseInt(shares.split("x")[0]);
    } catch (NumberFormatException e) {
      sharesBought.setText("Wrong Format");
      return;
    }
    symbolInvested.setText("Loading...");
    SingleInvestment newInvestment = new SingleInvestment(symbol, shares);
    double currentTotalSpent = Double.parseDouble(totalSpent.getText().replaceAll(",", "")),
        currentTotalValue = Double.parseDouble(totalCurrentValue.getText().replaceAll(",", "")),
        currentTotalGains = Double.parseDouble(totalGains.getText().replaceAll(",", ""));

    addInvestmentDataToVboxes(newInvestment);

    totalCurrentValue.setText(
        JavaTools.formattedValue(currentTotalValue + newInvestment.getCurrentValue()));
    totalGains.setText(JavaTools.formattedValue(currentTotalGains + newInvestment.getGains()));
    totalSpent.setText(
        JavaTools.formattedValue(currentTotalSpent + newInvestment.getValueAtPurchase()));

    dataOutputStream.write(
        JavaTools.mergeStrings(symbol, "|", shares, System.getProperty("line.separator"))
            .getBytes());
    symbolInvested.clear();
    sharesBought.clear();
  }

  @FXML
  public void setNodesVisible() {
    addStockToPFolio.setVisible(true);
    portfolioStock.setVisible(true);
    TextFields.bindAutoCompletion(portfolioStock, supportedStocks);
  }

  private void addInvestmentDataToVboxes(SingleInvestment investment) throws IOException {
    symbolBox.getChildren().addAll(new Text(investment.getSymbol()), new Separator());
    currentStockValueBox
        .getChildren()
        .addAll(new Text(investment.getCurrentStockValue()), new Separator());
    sharesBoughtBox.getChildren().addAll(new Text(investment.getSharesBought()), new Separator());
    valueAtPurchase
        .getChildren()
        .addAll(
            new Text(JavaTools.formattedValue(investment.getValueAtPurchase())), new Separator());
    currentValueBox
        .getChildren()
        .addAll(new Text(JavaTools.formattedValue(investment.getCurrentValue())), new Separator());
    profitsBox
        .getChildren()
        .addAll(new Text(JavaTools.formattedValue(investment.getGains())), new Separator());
  }

  @FXML
  public void refreshInvestments() throws IOException {
    List<Node> symbols = symbolBox.getChildren();
    for (Node symbol : symbols) {
      Node sharesBought = sharesBoughtBox.getChildren().get(symbols.indexOf(symbol));
      if (symbol instanceof Text && sharesBought instanceof Text) {
        SingleInvestment refreshed =
            new SingleInvestment(((Text) symbol).getText(), ((Text) sharesBought).getText());
        String currentStockValue = refreshed.getCurrentStockValue();
        currentStockValueBox
            .getChildren()
            .set(symbols.indexOf(symbol), new Text(currentStockValue));
        double currentValue = refreshed.getCurrentValue();
        currentValueBox
            .getChildren()
            .set(symbols.indexOf(symbol), new Text(JavaTools.formattedValue(currentValue)));
        profitsBox
            .getChildren()
            .set(
                symbols.indexOf(symbol),
                new Text(JavaTools.formattedValue(currentValue - refreshed.getValueAtPurchase())));
      }
    }
  }
}
