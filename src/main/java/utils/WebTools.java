package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;

public class WebTools {
  public static void openCryptoWebPage(String cryptoName) {
    ChromeDriver driver = new ChromeDriver();
    new Thread(
            () -> {
              driver.get(String.format("https://coinmarketcap.com/currencies/%s/", cryptoName));
              try {
                new ArrayList<>(By.xpath("//a[@href='/']").findElements(driver))
                    .stream()
                        .filter(elem -> elem.getText().contains("Homepage"))
                        .findAny()
                        .ifPresent(WebElement::click);
              } catch (Exception ignored) {
              }
            })
        .start();
  }

  public static void searchWebForStock(CharSequence symbolToSearch) {
    ChromeDriver driver = new ChromeDriver();
    driver.get("https://finance.yahoo.com/");
    new Thread(
            () -> {
              try {
                driver.findElement(By.name("agree")).click();
                Thread.sleep(1000);
                WebElement searchStock =
                    driver.findElement(
                        By.xpath("//input[@placeholder='Search for news, symbols or companies']"));
                searchStock.click();
                searchStock.sendKeys(symbolToSearch);
                Thread.sleep(3000);
                for (WebElement elem : searchStock.findElements(By.xpath("//li[@role='option']"))) {
                  if (elem.getText().contains(symbolToSearch)) {
                    elem.click();
                    break;
                  }
                }
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            })
        .start();
  }
}
