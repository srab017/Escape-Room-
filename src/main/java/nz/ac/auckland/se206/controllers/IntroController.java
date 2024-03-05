package nz.ac.auckland.se206.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

/** Controller class for the introduction view. */
public class IntroController {
  // Intialisng all the variables required for the scene
  @FXML private Rectangle time;
  @FXML private Rectangle start;
  @FXML private Label title;
  @FXML private Button easy;
  @FXML private Button medium;
  @FXML private Button difficult;
  private boolean levelIsPicked;
  private boolean timeIsPicked;
  @FXML private Label chooseEasy;
  @FXML private Label chooseMedium;
  @FXML private Label chooseDifficult;
  @FXML private Label minTwo;
  @FXML private Label minFour;
  @FXML private Label minSix;
  @FXML private Button twoMin;
  @FXML private Button fourMin;
  @FXML private Button sixMin;
  @FXML private Label startStatus;
  @FXML private Label levelDetail;
  @FXML private ImageView alien;

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // create a translate transition for the label
    TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), title);

    // set the Y-axis translation value
    translateTransition.setByY(-10);

    // set the number of cycles for the animation
    translateTransition.setCycleCount(TranslateTransition.INDEFINITE);

    // Set auto-reverse to true to make the label return to its original position
    translateTransition.setAutoReverse(true);

    // Start the animation
    translateTransition.play();

    TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(2), alien);

    // set the Y-axis translation value
    translateTransition1.setByY(-10);

    // set the number of cycles for the animation
    translateTransition1.setCycleCount(TranslateTransition.INDEFINITE);

    // Set auto-reverse to true to make the label return to its original position
    translateTransition1.setAutoReverse(true);

    // Start the animation
    translateTransition1.play();
  }

  /**
   * Handles the mouse event of clicking on start game. Begins the gaming based on the users choice
   * of settings.
   *
   * @param event The mouse click event.
   */
  @FXML
  private void startGame(MouseEvent event) {
    if (levelIsPicked && timeIsPicked) {
      // Only starting the thread if the user has chosen a difficulty level and a time
      // limit
      Thread gameTimeThread =
          new Thread(
              () -> {
                GameState.startGameTimer();
              });
      gameTimeThread.start();
      // Switching to the locked room scene so the user can begin their escape
      App.setUi("lockedroom");
    } else {
      // Telling the user to pick a level and a time duration
      startStatus.setText("Please pick a level and a time duration");
    }
  }

  /**
   * Handles the mouse click on easy difficulty selection.
   *
   * @param event Mouse click event.
   */
  @FXML
  private void easyPicked(MouseEvent event) {
    // Sertting all the details
    levelIsPicked = true;
    chooseEasy.setText("CHOSEN");
    chooseDifficult.setText("");
    chooseMedium.setText("");
    GameState.isEasyPicked = true;
    GameState.isMediumPicked = false;
    GameState.isDifficultPicked = false;
    if (timeIsPicked) {
      startStatus.setText("");
    }
    levelDetail.setText(
        "EASY: You can ask as many questions as you like and get unlimited hints from the"
            + " Gamemaster");
  }

  /**
   * Handles the mouse click on medium difficulty selection.
   *
   * @param event Mouse click event.
   */
  @FXML
  private void mediumPicked(MouseEvent event) {
    // Setting all the details
    levelIsPicked = true;
    GameState.isMediumPicked = true;
    GameState.isEasyPicked = false;
    GameState.isDifficultPicked = false;
    chooseMedium.setText("CHOSEN");
    chooseDifficult.setText("");
    chooseEasy.setText("");
    if (timeIsPicked) {
      startStatus.setText("");
    }
    levelDetail.setText(
        "MEDIUM: You have a maximum of five hints from the"
            + " Gamemaster, but feel free to talk to him");
  }

  /**
   * Handles the mouse click on difficult difficulty selection.
   *
   * @param event Mouse click event.
   */
  @FXML
  private void difficultPicked(MouseEvent event) {
    // Setting all the details
    levelIsPicked = true;
    GameState.isDifficultPicked = true;
    GameState.isEasyPicked = false;
    GameState.isMediumPicked = false;
    chooseDifficult.setText("CHOSEN");
    chooseEasy.setText("");
    chooseMedium.setText("");
    if (timeIsPicked) {
      startStatus.setText("");
    }
    levelDetail.setText(
        "DIFFICULT: You are not able to get hints from the"
            + " Gamemaster, but feel free to talk to him");
  }

  /**
   * Handles the mouse click on two minute time limit selection.
   *
   * @param event Mouse click event.
   */
  @FXML
  private void twoPicked(MouseEvent event) {
    // Setting all the details
    timeIsPicked = true;
    GameState.minutes = 2;
    minTwo.setText("CHOSEN");
    minFour.setText("");
    minSix.setText("");

    if (levelIsPicked) {
      startStatus.setText("");
    }
  }

  /**
   * Handles the mouse click on four minute time limit selection.
   *
   * @param event Mouse click event.
   */
  @FXML
  private void fourPicked(MouseEvent event) {
    // Setting all the details
    timeIsPicked = true;
    GameState.minutes = 4;
    minFour.setText("CHOSEN");
    minTwo.setText("");
    minSix.setText("");
    if (levelIsPicked) {
      startStatus.setText("");
    }
  }

  /**
   * Handles the mouse click on six minute time limit selection.
   *
   * @param event Mouse click event.
   */
  @FXML
  private void sixPicked(MouseEvent event) {
    // Setting all the details
    timeIsPicked = true;
    GameState.minutes = 6;
    minSix.setText("CHOSEN");
    minTwo.setText("");
    minFour.setText("");
    if (levelIsPicked) {
      startStatus.setText("");
    }
  }
}
