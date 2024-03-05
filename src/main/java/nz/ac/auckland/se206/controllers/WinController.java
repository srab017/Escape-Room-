package nz.ac.auckland.se206.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

/** Controller class for the Win Screen view. */
public class WinController {
  // Intialising variables for the scene
  @FXML private ImageView alein;
  @FXML private Button buttonReplay;
  @FXML private Label timeLeft;

  private Image[] runningImages = new Image[4];
  private int currentImageIndex = 0;

  /** Initializes the room view, it is called when the room loads. */
  @FXML
  public void initialize() {
    // Load images RUN1 to RUN4
    for (int i = 0; i < 4; i++) {
      runningImages[i] = new Image(("images/RUN" + (i + 1) + ".png"));
    }

    timeLeft.setText("YOU ESCAPED WITH " + GameState.getTimeLeft() + " LEFT!");

    // Set initial image to Alein
    alein.setImage(runningImages[currentImageIndex]);

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
                new KeyValue(alein.layoutXProperty(), 1100 - alein.getFitWidth())));
    moveTimeline.setCycleCount(Timeline.INDEFINITE);
    moveTimeline.setOnFinished(evt -> alein.setLayoutX(0)); // Reset position to leftmost
    moveTimeline.play();
  }

  /** Moves image of Gamemaster. Used in the animation of Gamemaster. */
  private void switchImage() {
    currentImageIndex = (currentImageIndex + 1) % 4;
    alein.setImage(runningImages[currentImageIndex]);
  }

  /** Resets the settings of the game for the user to play again. Changes to intro view. */
  @FXML
  private void onReset() {
    GameState.restart();
    // Setting the room to the intro room so the user is directed back to the intro
    // room when
    // restart is clicked
    App.setUi("intro");
  }
}
