package tinycpu.simulator.Components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tinycpu.simulator.SimCPU;

import java.util.Objects;

public class ImageIcon extends ImageView {

    public ImageIcon(String resourceUrl) {
        super(new Image(Objects.requireNonNull(SimCPU.class.getResourceAsStream(resourceUrl))));

        this.setFitHeight(28);
        this.setFitWidth(28);
    }
}
