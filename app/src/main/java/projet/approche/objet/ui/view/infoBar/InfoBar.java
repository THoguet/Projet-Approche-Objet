package projet.approche.objet.ui.view.infoBar;

import java.awt.Image;

import javafx.geometry.Insets;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import projet.approche.objet.application.App;
import projet.approche.objet.domain.valueObject.game.GameStarter;
import projet.approche.objet.infrastructure.Infrastructure;
import projet.approche.objet.ui.view.GameView;
import projet.approche.objet.ui.view.Updateable;
import projet.approche.objet.ui.view.imageResource.ButtonImageResource;

public class InfoBar extends BorderPane implements Updateable {

	private final Inventory inventory;
	private final Button playPauseButton;
	private final App app;
	private Text day = new Text();

	public InfoBar(GameView gv, App app) {
		this.inventory = new Inventory(app);
		HBox buttonBox = new HBox();
		buttonBox.setSpacing(15);
		buttonBox.setPadding(new Insets(10));
		this.playPauseButton = new Button(gv, app);
		ImageView saveButton = new ImageView(ButtonImageResource.SAVE.getImage());

		saveButton.setOnMouseClicked(e -> {
			Infrastructure is = new Infrastructure();
			is.save(app.getManager());
		});

		ColorAdjust colorAdjust = new ColorAdjust();
		colorAdjust.setBrightness(-0.4);
		saveButton.setOnMouseEntered(e -> {
			saveButton.setEffect(colorAdjust);
		});
		saveButton.setOnMouseExited(e -> {
			saveButton.setEffect(null);
		});

		buttonBox.getChildren().addAll(playPauseButton, saveButton);
		day.setFill(javafx.scene.paint.Color.BLACK);
		day.setFont(new Font(20));
		day.getStyleClass().add("number");
		day.setCache(true);
		this.app = app;
		this.setRight(inventory);
		this.setTop(buttonBox);
	}

	public void update() {
		this.inventory.update();
		this.playPauseButton.update();
		this.updateDay();
	}

	public void updateDay() {
		day.setText("   Day " + app.getDay());
		this.setLeft(day);
	}
}
