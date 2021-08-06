package Main;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.JFXTools;
import utils.JavaTools;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
  private final File porfolioFile = new File("src/main/resources/portfolioStocks.txt"),
      investmentFile = new File("src/main/resources/investments.txt");
  @FXML AnchorPane root, pane;
  @FXML
  private TextField input, portfolioStock, cryptoName, otherStock, sharesBought, symbolInvested;
  @FXML private ChoiceBox<String> rangeSelector;
  private AreaChart<String, Number> areaChart;
  @FXML private ScrollPane scrollPane;
  @FXML private Button addStockToPFolio;
  @FXML private MenuButton myPortfolio;
  @FXML private Label stockPresentLabel;
  private FileOutputStream portfolioOutputStream, investmentOutputStream;
  private BufferedReader portfolioReader, investmentReader;
  private List<String> supportedStocks;
  private WebDriver driver;
  @FXML private Text news;
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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\chromedriver.exe");
      TrendingStocks.createTrendingStocksBox(stockList);
      supportedStocks =
          JavaTools.concatLists(
              WebLinks.getNASDAQTickers(), WebLinks.getAMEXTickers(), WebLinks.getNYSETickers());
      TextFields.bindAutoCompletion(input, supportedStocks);
      portfolioOutputStream = new FileOutputStream(porfolioFile, true);
      portfolioReader = new BufferedReader(new FileReader(porfolioFile));
      String line;
      while ((line = portfolioReader.readLine()) != null) {
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

      investmentOutputStream = new FileOutputStream(investmentFile, true);
      investmentReader = new BufferedReader(new FileReader(investmentFile));
      String investLine;
      // in the .txt file the formatting will be in the form symbol|100x301 - the second part being
      // the number of shares by the value of each share
      while ((investLine = investmentReader.readLine()) != null) {
        String[] investment = investLine.split("\\|");
        addInvestmentDataToVboxes(new SingleInvestment(investment[0], investment[1]));
      }

      String json = WebLinks.JSONNewsInfo();
      JSONObject obj = new JSONObject(json).getJSONObject("data").getJSONObject("main");
      JSONArray arr = obj.getJSONArray("stream");
      for (int i = 0; i < arr.length(); i++) {
        JSONObject content = arr.getJSONObject(i).getJSONObject("content");
        String url;
        try {
          url = content.getJSONObject("clickThroughUrl").getString("url");
        } catch (JSONException jsonException) {
          url = "https://www.google.com/search?q=" + input.getText().replaceAll(" ", "+");
        }
        NewsInfo newsInfo =
            new NewsInfo(
                content.getString("title"), content.getString("pubDate").substring(0, 10), url);
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

  private void addHbox(String info) {
    singleStockBox.getChildren().addAll(new HBox(new Text(info)), new Separator());
  }

  @FXML
  public void addStock() throws IOException {
    String detectedSymbol;
    String userInput = portfolioStock.getText();
    MenuItem stock;
    if (portfolioStock.getText().contains("|")) {
      detectedSymbol = userInput.substring(userInput.indexOf("|") + 1).replace(" ", "");
      stock = new MenuItem(detectedSymbol);
      stock.setOnAction(
          e -> {
            try {
              setMenuItemAction(detectedSymbol);
            } catch (IOException | InterruptedException exception) {
              exception.printStackTrace();
            }
          });
    } else {
      detectedSymbol = userInput;
      stock = new MenuItem(detectedSymbol);
      stock.setOnAction(
          e -> {
            try {
              setMenuItemAction(detectedSymbol);
            } catch (IOException | InterruptedException exception) {
              exception.printStackTrace();
            }
          });
    }
    if (myPortfolio.getItems().stream()
            .noneMatch(menuItem -> menuItem.getText().equals(stock.getText()))
        || myPortfolio.getItems().isEmpty()) {
      myPortfolio.getItems().add(stock);
      stockPresentLabel.setText("");
    } else {
      stockPresentLabel.setText("This stock is already in your portfolio");
      stockPresentLabel.setTextFill(Color.RED);
    }
    portfolioOutputStream.write(
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
  public void searchWebForStock() throws InterruptedException {
    CharSequence chars = otherStock.getCharacters();
    driver = new ChromeDriver();
    driver.get("https://finance.yahoo.com/");
    Runnable runnable =
        () -> {
          try {
            driver.findElement(By.name("agree")).click();
            Thread.sleep(1000);
            WebElement searchStock =
                driver.findElement(
                    By.xpath("//input[@placeholder='Search for news, symbols or companies']"));
            searchStock.click();
            searchStock.sendKeys(chars);
            Thread.sleep(3000);
            for (WebElement elem : searchStock.findElements(By.xpath("//li[@role='option']"))) {
              if (elem.getText().contains(chars)) {
                elem.click();
                break;
              }
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        };
    new Thread(runnable).start();
  }

  @FXML
  public void openCrypto() {
    driver = new ChromeDriver();
    Runnable runnable =
        () -> {
          driver.get(
              String.format("https://coinmarketcap.com/currencies/%s/", cryptoName.getText()));
          try {
            new ArrayList<>(By.xpath("//a[@href='/']").findElements(driver))
                .stream()
                    .filter(elem -> elem.getText().contains("Homepage"))
                    .findAny()
                    .ifPresent(WebElement::click);
          } catch (Exception ignored) {
          }
        };
    new Thread(runnable).start();
  }

  @FXML
  public void addInvestment() throws IOException {
    String symbol = symbolInvested.getText(), shares = sharesBought.getText();
    SingleInvestment newInvestment = new SingleInvestment(symbol, shares);
    addInvestmentDataToVboxes(newInvestment);
    investmentOutputStream.write(
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
        .addAll(new Text(investment.getValueAtPurchase()), new Separator());
    currentValueBox.getChildren().addAll(new Text(investment.getCurrentValue()), new Separator());
    profitsBox.getChildren().addAll(new Text(investment.getGains()), new Separator());
  }
}
