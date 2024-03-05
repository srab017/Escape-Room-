package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

/** Controller class for the loss screen view. */
public class EndController {
  // Intialisng all the variables for the scene
  @FXML private ImageView alien;
  @FXML private Button buttonReplay;

  private Image[] runningImages = new Image[4];
  private int currentImageIndex = 0;

  /** Initializes the room view, it is called when the room loads. */
  @FXML
  public void initialize() {
    // Load images RUN1 to RUN4
    for (int i = 0; i < 4; i++) {
      runningImages[i] = new Image(("images/RUN" + (i + 1) + ".png"));
    }

    // Set initial image to Alien
    alien.setImage(runningImages[currentImageIndex]);

    // Timeline to switch images
    double switchSpeed = 0.2; // adjust for faster or slower switching
    Timeline switchImageTimeline =
        new Timeline(new KeyFrame(Duration.seconds(switchSpeed), evt -> switchImage()));
    switchImageTimeline.setCycleCount(Timeline.INDEFINITE);
    switchImageTimeline.play();

    // Timeline to move the ImageView
    double movementDuration =
        10.0; // Adjust this for the entire movement duration across the screen
    Timeline moveTimeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(movementDuration),
                new KeyValue(alien.layoutXProperty(), 1100 - alien.getFitWidth())));
    moveTimeline.setCycleCount(Timeline.INDEFINITE);
    moveTimeline.setOnFinished(evt -> alien.setLayoutX(0)); // Reset position to leftmost
    moveTimeline.play();
  }

  // Switching the image
  private void switchImage() {
    currentImageIndex = (currentImageIndex + 1) % 4;
    alien.setImage(runningImages[currentImageIndex]);
  }

  /** Resets the game for the user to play again. */
  @FXML
  private void reset() {
    GameState.restart();
    // Setting the rooom to the intro room so the user is able to restart the game
    // and select their
    // options
    App.setUi("intro");
  }
}
