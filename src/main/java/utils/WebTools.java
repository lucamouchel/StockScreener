package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WebTools {

    public static String CRYPTO_WEBSITE = "https://coinmarketcap.com/currencies",
            YAHOO_SEARCH = "https://finance.yahoo.com/lookup?s=";

    public static void openCryptoWebPage(String cryptoName) {
        ChromeDriver driver = new ChromeDriver();
        new Thread(
                () -> {
                    driver.get(String.format(CRYPTO_WEBSITE + "/%s/", cryptoName));
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

    public static void searchWebForStock(String symbolToSearch) throws InterruptedException {
        ChromeDriver driver = new ChromeDriver();
        driver.get(String.format("%s%s", YAHOO_SEARCH, symbolToSearch));
        Thread.sleep(1000);
        List<WebElement> searchStock =
                driver.findElements(
                        By.xpath("//a[@class='Fw(b)']"));
        for (WebElement linkName : searchStock) {
            if (linkName.getText().contains(symbolToSearch.toUpperCase(Locale.ROOT))) {
                linkName.click();
                break;
            }
        }

    }
}
