package utils;

import Main.IndividualStock;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

public class JFXTools {
  public static void setUpVbox(
      VBox stockToAdd, String stock, IndividualStock individualStock, double shiftPercentage) {
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
}
