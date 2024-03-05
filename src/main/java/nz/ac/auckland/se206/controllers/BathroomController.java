package nz.ac.auckland.se206.controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

/** Controller class for the Bathroom view. */
public class BathroomController {

  // Creating variables required for the FXML scene
  @FXML private Rectangle quizMaster;
  @FXML private Canvas bathroomGameMaster;
  @FXML private Label bathroomTimer;
  private int currentImageIndex = 0;
  @FXML private ImageView lightOne;
  @FXML private ImageView lightTwo;
  @FXML private ImageView lightThree;
  @FXML private ImageView toLockedRoom;
  @FXML private Ellipse ellipseOne;
  @FXML private Ellipse ellipseTwo;
  @FXML private Ellipse ellipseThree;
  @FXML private Label label;
  @FXML private ImageView sdCard;
  @FXML private ImageView tape;
  @FXML private TextArea objText;
  @FXML private TextArea hintsText;
  @FXML private ImageView globe;
  @FXML private Button rgbClue1;

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // Making sure the RGB clue is still able to be picked up if the user
    // accidentally leaves the
    // room
    if (GameState.isRgbClueFound) {
      rgbClue1.setOpacity(1);
      rgbClue1.setText(GameState.password);
    }
    // Intialising the objectives and hints text plus the items
    objText.setText(GameState.getObjective());
    hintsText.setText(GameState.getHint());
    if (GameState.isSdCardFound) {
      sdCard.setOpacity(1);
    }
    if (GameState.isElectricalTapeFound) {
      tape.setOpacity(1);
    }
    if (GameState.isGlobeFound) {
      globe.setOpacity(1);
    }
    // Dimming the Lights
    if (!GameState.isLightPuzzleSolved) {
      ellipseOne.setOpacity(0.45);
      ellipseTwo.setOpacity(0.45);
      ellipseThree.setOpacity(0.45);
    }
    // Making sure the player can't click on the lights if the decrypt puzzle is
    // solved
    if (!GameState.isDecryptCompleted) {
      ellipseOne.setOnMouseClicked(null);
      ellipseOne.setOnMouseEntered(null);
      ellipseTwo.setOnMouseClicked(null);
      ellipseTwo.setOnMouseEntered(null);
      ellipseThree.setOnMouseClicked(null);
      ellipseThree.setOnMouseEntered(null);
    } else if (GameState.randomLight.equals("first")) {
      ellipseTwo.setOnMouseClicked(null);
      ellipseTwo.setOnMouseEntered(null);
      ellipseThree.setOnMouseClicked(null);
      ellipseThree.setOnMouseEntered(null);
    } else if (GameState.randomLight.equals("second")) {
      ellipseOne.setOnMouseClicked(null);
      ellipseOne.setOnMouseEntered(null);
      ellipseThree.setOnMouseClicked(null);
      ellipseThree.setOnMouseEntered(null);
    } else {
      ellipseTwo.setOnMouseClicked(null);
      ellipseTwo.setOnMouseEntered(null);
      ellipseOne.setOnMouseClicked(null);
      ellipseOne.setOnMouseEntered(null);
    }
    // Timing thread for the timer
    bathroomTimer.setText(GameState.getTimeLeft());
    Thread bathroomTimeThread =
        new Thread(
            () -> {
              startBathroomTimer();
            });
    bathroomTimeThread.start();
    // game master animation
    // Start the animation
    startAnimation();

    TranslateTransition translateTransition =
        new TranslateTransition(Duration.seconds(2), bathroomGameMaster);

    // set the Y-axis translation value
    translateTransition.setByY(-10);

    // set the number of cycles for the animation
    translateTransition.setCycleCount(TranslateTransition.INDEFINITE);

    // Set auto-reverse to true to make the label return to its original position
    translateTransition.setAutoReverse(true);

    // Start the animation
    translateTransition.play();
  }

  /** Starts the animation of the Gamemaster. */
  private void startAnimation() {
    GraphicsContext gc = bathroomGameMaster.getGraphicsContext2D();
    AnimationTimer timer =
        new AnimationTimer() {
          private long lastTime = 0;
          private final long frameDurationMillis = 100; // 1000 milliseconds = 1 second

          @Override
          public void handle(long currentTime) {
            if (currentTime - lastTime >= frameDurationMillis * 1_000_000) {
              if (currentImageIndex < GameState.alienImages.length) {
                gc.clearRect(0, 0, bathroomGameMaster.getWidth(), bathroomGameMaster.getHeight());
                gc.drawImage(GameState.alienImages[currentImageIndex], 0, 0);
                currentImageIndex++;
                // Check if we have displayed all images; if so, reset the index to 0
                if (currentImageIndex >= GameState.alienImages.length) {
                  currentImageIndex = 0;
                }
                lastTime = currentTime;
              }
            }
          }
        };
    timer.start();
  }

  /**
   * Handles the click event on the Gamemaster to open chat view.
   *
   * @param event the key event.
   */
  @FXML
  private void clickBathroomQuizMaster(MouseEvent event) {
    App.setUi("chat");
  }

  /**
   * Handles the click event on the left arrow to enter the locked room view.
   *
   * @param event the key event.
   */
  @FXML
  private void enterLockedRoom(MouseEvent event) {
    GameState.currentRoom = "lockedroom";
    App.setUi("lockedroom");
  }

  /** Enlarges the left arrow when the arrow hovers over it. */
  @FXML
  private void highlightLeftArrow() {
    toLockedRoom.setOpacity(1);
    toLockedRoom.setScaleX(1.2);
    toLockedRoom.setScaleY(1.2);
  }

  /** Removes the enlargement when the mouse leaves the left arrow. */
  @FXML
  private void removeHighlightLeftArrow() {
    toLockedRoom.setOpacity(0.3);
    toLockedRoom.setScaleX(1);
    toLockedRoom.setScaleY(1);
  }

  /**
   * Handles the click event of the first light to enter the light scene if decrypt puzzle is
   * solved.
   */
  @FXML
  public void clickLightOne() {
    if (!GameState.isLightPuzzleStarted) {
      // Creaating the prompts for the hints for GPT flow when user enters light
      Thread thread =
          new Thread(
              () -> {
                GameState.sendPrompt(
                    "The player has access behind the light. Some wires are broken. The player has"
                        + " to find the electrical tape to patch the wires with. The electrical"
                        + " tape can be found on the ground in the computer room.");
              });
      thread.start();
      GameState.isLightPuzzleStarted = true;
    }
    // If user has collected the globe and solved the puzzle, they can't click on
    // the lights
    if (GameState.isLightPuzzleSolved && GameState.isGlobeFound) {
      return;
    } else {
      // Otherwise, they can click on the lights
      GameState.currentRoom = "light";
      App.setUi("light");
    }
  }

  /**
   * Handles the click event of the second light to enter the light scene if decrypt puzzle is
   * solved.
   */
  @FXML
  public void clickLightTwo() {
    // Creaating the prompts for the hints for GPT flow when user enters light
    if (!GameState.isLightPuzzleStarted) {
      Thread thread =
          new Thread(
              () -> {
                GameState.sendPrompt(
                    "The player has access behind the light. Some wires are broken. The player has"
                        + " to find the electrical tape to patch the wires with. The electrical"
                        + " tape can be found on the ground in the computer room.");
              });
      thread.start();
      GameState.isLightPuzzleStarted = true;
    }
    // If user has collected the globe and solved the puzzle, they can't click on
    // the lights
    if (GameState.isLightPuzzleSolved && GameState.isGlobeFound) {
      return;
    } else {
      // Otherwise, they can click on the lights
      GameState.currentRoom = "light";
      App.setUi("light");
    }
  }

  /**
   * Handles the click event of the Third light to enter the light scene if decrypt puzzle is
   * solved.
   */
  @FXML
  public void clickLightThree() {
    // Creaating the prompts for the hints for GPT flow when user enters light
    if (!GameState.isLightPuzzleStarted) {
      Thread thread =
          new Thread(
              () -> {
                GameState.sendPrompt(
                    "The player has access behind the light. Some wires are broken. The player has"
                        + " to find the electrical tape to patch the wires with. The electrical"
                        + " tape can be found on the ground in the computer room.");
              });
      thread.start();
      GameState.isLightPuzzleStarted = true;
      // If user has collected the globe and solved the puzzle, they can't click on
      // the lights
    }
    if (GameState.isLightPuzzleSolved && GameState.isGlobeFound) {
      return;
    } else {
      // Otherwise, they can click on the lights
      GameState.currentRoom = "light";
      App.setUi("light");
    }
  }

  /** Starts updating the timer accordingly. */
  public void startBathroomTimer() {
    Timeline bathroomTimeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                    // Counts down the timer.
                    Platform.runLater(
                        new Runnable() {
                          @Override
                          public void run() {
                            bathroomTimer.setText(GameState.getTimeLeft());
                          }
                        });
                  }
                }));

    bathroomTimeline.setCycleCount((GameState.minutes * 60) + GameState.seconds - 1);
    bathroomTimeline.play();
  }

  /**
   * Handles the mouse enter event on the first light to increase it's size.
   *
   * @param event the mouse event
   */
  @FXML
  private void increaseSizeOne(MouseEvent event) {
    ellipseOne.setScaleX(1.2);
    ellipseOne.setScaleY(1.2);
    lightOne.setScaleX(1.2);
    lightOne.setScaleY(1.2);
  }

  /**
   * Handles the mouse enter event on the second light to increase it's size.
   *
   * @param event the mouse event
   */
  @FXML
  private void increaseSizeTwo(MouseEvent event) {
    ellipseTwo.setScaleX(1.2);
    ellipseTwo.setScaleY(1.2);
    lightTwo.setScaleX(1.2);
    lightTwo.setScaleY(1.2);
  }

  /**
   * Handles the mouse enter event on the third light to increase it's size.
   *
   * @param event the mouse event
   */
  @FXML
  private void increaseSizeThree(MouseEvent event) {
    ellipseThree.setScaleX(1.2);
    ellipseThree.setScaleY(1.2);
    lightThree.setScaleX(1.2);
    lightThree.setScaleY(1.2);
  }

  /**
   * Handles the mouse exit event on the first light to increase it's size.
   *
   * @param event the mouse event
   */
  @FXML
  private void decreaseSizeOne(MouseEvent event) {
    ellipseOne.setScaleX(1);
    ellipseOne.setScaleY(1);
    lightOne.setScaleX(1);
    lightOne.setScaleY(1);
  }

  /**
   * Handles the mouse exit event on the second light to increase it's size.
   *
   * @param event the mouse event
   */
  @FXML
  private void decreaseSizeTwo(MouseEvent event) {
    ellipseTwo.setScaleX(1);
    ellipseTwo.setScaleY(1);
    lightTwo.setScaleX(1);
    lightTwo.setScaleY(1);
  }

  /**
   * Handles the mouse exit event on the third light to increase it's size.
   *
   * @param event the mouse event
   */
  @FXML
  private void decreaseSizeThree(MouseEvent event) {
    ellipseThree.setScaleX(1);
    ellipseThree.setScaleY(1);
    lightThree.setScaleX(1);
    lightThree.setScaleY(1);
  }
}
