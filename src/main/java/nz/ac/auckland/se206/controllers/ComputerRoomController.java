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
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

/** Controller class for the Computer room view. */
public class ComputerRoomController {
  // Intialising variables required for the room

  @FXML private ImageView tape;
  @FXML private ImageView toLockedRoom;
  @FXML private Rectangle quizMaster;
  @FXML private Canvas computerRoomGameMaster;
  @FXML private Label computerRoomTimer;
  @FXML private Rectangle decrypt;
  private int currentImageIndex = 0;
  @FXML private ImageView hoverImage;
  @FXML private ImageView tape1;
  @FXML private ImageView sdCard;
  @FXML private TextArea objText;
  @FXML private TextArea hintsText;
  @FXML private ImageView globe;
  @FXML private Button rgbClue1;

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // Displaying the items which are collected/not collected
    if (GameState.isRgbClueFound) {
      rgbClue1.setOpacity(1);
      rgbClue1.setText(GameState.password);
    }
    // Displaying the items which are collected/not collected
    objText.setText(GameState.getObjective());
    hintsText.setText(GameState.getHint());
    if (GameState.isSdCardFound) {
      sdCard.setOpacity(1);
    }
    if (GameState.isElectricalTapeFound) {
      tape1.setOpacity(1);
    }
    if (GameState.isGlobeFound) {
      globe.setOpacity(1);
    }
    // Allowing the user to collect the tape only when the light puzzle is started
    if (!GameState.isLightPuzzleStarted) {
      tape.setOnMouseClicked(null);
      tape.setOnMouseEntered(null);
      tape.setOpacity(0);
    }
    // Tape disappeares once picked up
    if (GameState.isElectricalTapeFound) {
      tape.setOpacity(0);
      tape.setOnMouseClicked(null);
    }
    // Timer thread
    computerRoomTimer.setText(GameState.getTimeLeft());
    Thread computerRoomTimeThread =
        new Thread(
            () -> {
              startComputerRoomTimer();
            });
    computerRoomTimeThread.start();
    // game master animation
    // Start the animation
    startAnimation();

    TranslateTransition translateTransition =
        new TranslateTransition(Duration.seconds(2), computerRoomGameMaster);

    // set the Y-axis translation value
    translateTransition.setByY(-10);

    // set the number of cycles for the animation
    translateTransition.setCycleCount(TranslateTransition.INDEFINITE);

    // Set auto-reverse to true to make the label return to its original position
    translateTransition.setAutoReverse(true);

    // Start the animation
    translateTransition.play();
  }

  /** Starts the animation for the Gamemaster. */
  private void startAnimation() {
    GraphicsContext gc = computerRoomGameMaster.getGraphicsContext2D();
    AnimationTimer timer =
        new AnimationTimer() {
          private long lastTime = 0;
          private final long frameDurationMillis = 100; // 1000 milliseconds = 1 second

          @Override
          public void handle(long currentTime) {
            if (currentTime - lastTime >= frameDurationMillis * 1_000_000) {
              if (currentImageIndex < GameState.alienImages.length) {
                gc.clearRect(
                    0, 0, computerRoomGameMaster.getWidth(), computerRoomGameMaster.getHeight());
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
   * Handles the click event on the Gamemaster to enter chat view.
   *
   * @param event the mouse event.
   */
  @FXML
  private void clickComputerRoomQuizMaster(MouseEvent event) {
    App.setUi("chat");
  }

  /**
   * Handles the click event on the right arrow to open locked room view.
   *
   * @param event the mouse event.
   */
  @FXML
  private void enterLockedRoom(MouseEvent event) {
    GameState.currentRoom = "lockedroom";
    App.setUi("lockedroom");
  }

  /** Highlights the right arrow when the mouse hovers. */
  @FXML
  private void highlightRightArrow() {
    toLockedRoom.setOpacity(1);
    toLockedRoom.setScaleX(1.2);
    toLockedRoom.setScaleY(1.2);
  }

  /** Removes highlight on right arrow when the mouse stops hovering. */
  @FXML
  private void removeHighlightRightArrow() {
    toLockedRoom.setOpacity(0.3);
    toLockedRoom.setScaleX(1);
    toLockedRoom.setScaleY(1);
  }

  /** Starts updating timer according to time left. */
  public void startComputerRoomTimer() {
    Timeline computerRoomTimeline =
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
                            computerRoomTimer.setText(GameState.getTimeLeft());
                          }
                        });
                  }
                }));

    computerRoomTimeline.setCycleCount((GameState.minutes * 60) + GameState.seconds - 1);
    computerRoomTimeline.play();
  }

  /**
   * Handles the click event on the middle computer to enter the decryption puzzle view.
   *
   * @param event the mouse event
   */
  @FXML
  public void enterDecrypt(MouseEvent event) {
    // Conditions letting the user to enter the puzzle
    if (!GameState.isRiddleResolved || !GameState.isSdCardFound) {
      objText.setText("You need the SD card to access the computer!");
    } else {
      GameState.currentRoom = "decrypt";
      if (!GameState.isComputerAccessed) {
        // Thread to send the prompt to the GPT to over look progress
        Thread thread =
            new Thread(
                () -> {
                  GameState.sendPrompt(
                      "The player has accessed the computer. The player must decipher an alien"
                          + " message using an onscreen alien alphabet.");
                });
        thread.start();
        GameState.isComputerAccessed = true;
      }
      App.setUi("decrypt");
    }
  }

  /**
   * Handles the mouse enter event on the middle computer image.
   *
   * @param event the mouse event
   */
  @FXML
  private void increaseSize(MouseEvent event) {
    if (!GameState.isRiddleResolved) {
      return;
    } else {
      hoverImage.setScaleX(1.05); // Increase the size by a factor of 1.2 horizontally
      hoverImage.setScaleY(1.05);
    }
  }

  /**
   * Handles the mouse exit event on the middle computer image.
   *
   * @param event the mouse event
   */
  @FXML
  private void decreaseSize(MouseEvent event) {
    // decrease the size of the image
    hoverImage.setScaleX(1);
    hoverImage.setScaleY(1);
  }

  /** Moves tape to inventory on mouse click. */
  @FXML
  public void clickTape() {
    if (GameState.isComputerAccessed) {
      // Thread to send the prompt to the GPT to over look progress
      Thread thread =
          new Thread(
              () -> {
                GameState.sendPrompt(
                    "The player has gotten the tape. The player must now go back to the broken"
                        + " light and fix the broken wires by simply clicking on areas that appear"
                        + " broken. The player does not need to consider the colours of the"
                        + " wires.");
              });
      thread.start();
      GameState.isComputerAccessed = true;
    }
    tape.setOpacity(0);
    tape.setOnMouseClicked(null);
    GameState.isElectricalTapeFound = true;
    objText.setText("Good job you have found the electrical tape, now fix the wires.");
    tape1.setOpacity(1);
  }

  /**
   * Handles the mouse enter event on the tape image.
   *
   * @param event the mouse event
   */
  @FXML
  private void increaseTapeSize(MouseEvent event) {
    tape.setScaleX(1.2);
    tape.setScaleY(1.2);
  }

  /**
   * Handles the mouse exit event on the tape image.
   *
   * @param event the mouse event
   */
  @FXML
  private void decreaseTapeSize(MouseEvent event) {
    tape.setScaleX(1);
    tape.setScaleY(1);
  }
}
