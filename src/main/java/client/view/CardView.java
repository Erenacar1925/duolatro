package client.view;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CardView {
  public static ImageView getCardImage(String cardCode) {
    // cardCode z.B. "2H", "KC", "JD", "AS"
    String path = "/images/cards/" + cardCode + ".png";
    Image image = new Image(CardView.class.getResourceAsStream(path));
    ImageView view = new ImageView(image);
    view.setFitWidth(85);
    view.setFitHeight(115);
    return view;
  }

  public static ImageView getBackSide() {
    String path = "/images/cards/back.png";
    Image image = new Image(CardView.class.getResourceAsStream(path));
    ImageView view = new ImageView(image);
    view.setFitWidth(85);
    view.setFitHeight(115);
    return view;
  }

  // Optional: mehrere Karten auf einmal
  public static List<ImageView> getCardRow(String... codes) {
    return Arrays.stream(codes).map(CardView::getCardImage).collect(Collectors.toList());
  }
}
