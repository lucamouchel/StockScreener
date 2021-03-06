package utils;

import Main.Stock;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

public class JFXTools {
  public static void setUpVbox(
          VBox stockToAdd, String stock, Stock individualStock, double shiftPercentage) {
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

  public static Text textWithColor(String text, Color color) {
    Text textWithColor = new Text(text);
    textWithColor.setFill(Paint.valueOf(String.valueOf(color)));
    return textWithColor;
  }

  public static void verifyPortfolioContainsStock(
      ObservableList<MenuItem> myPortfolio, MenuItem toAdd, Label stockIsPresent) {
    if (myPortfolio.stream().noneMatch(menuItem -> menuItem.getText().equals(toAdd.getText()))
        || myPortfolio.isEmpty()) {
      myPortfolio.add(toAdd);
      stockIsPresent.setText("");
    } else {
      stockIsPresent.setText("This stock is already in your portfolio");
      stockIsPresent.setTextFill(Color.RED);
    }
  }
}
