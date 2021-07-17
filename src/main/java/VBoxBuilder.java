import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class VBoxBuilder extends VBox {

    public VBoxBuilder() {
    }

    public VBoxBuilder(double spacing) {
        super(spacing);
    }

    public VBoxBuilder(Node... children) {
        super(children);
    }

    public VBoxBuilder(double spacing, Node... children) {
        super(spacing, children);
    }


}
