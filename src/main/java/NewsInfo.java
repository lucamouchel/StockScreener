import javafx.scene.Group;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public final class NewsInfo {
  private final String title, date, websiteUrl;

  public NewsInfo(String title, String date, String websiteUrl) {
    this.title = title;
    this.date = date;
    this.websiteUrl = websiteUrl;
  }

  public Group createNewsVbox() {
    Group newsBox = new Group();
    Text title = new Text(this.title);
    title.setStyle("-fx-font-weight: bold; -fx-font-size: 16");
    Text date = new Text(this.date);
    date.setStyle("-fx-font-size: 10");

    Hyperlink website = new Hyperlink(websiteUrl);
    website.setOnAction(
        event -> {
          try {
            Desktop.getDesktop().browse(new URL(websiteUrl).toURI());
          } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
          }
        });
    newsBox.getChildren().add(new HBox(15, new VBox(title, date, website)));
    return newsBox;
  }
}
