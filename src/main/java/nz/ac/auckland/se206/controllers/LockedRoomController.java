package nz.ac.auckland.se206.controllers;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
// import javafx.scene.control.Alert;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

/** Controller class for the room view. */
public class LockedRoomController {
  // Intialising all the variables

  @FXML private ImageView toBathroom;
  @FXML private ImageView toComputerRoom;
  @FXML private Rectangle quizMaster;
  @FXML private Rectangle buttonBlue;
  @FXML private Rectangle buttonRed;
  @FXML private Rectangle buttonGreen;
  @FXML private Rectangle buttonYellow;
  @FXML private Rectangle rectangleDoorOne;
  @FXML private Rectangle rectangleDoorTwo;
  @FXML private Rectangle rectangleDoorThree;
  @FXML private Canvas lockedRoomGameMaster;
  @FXML private Label lockedRoomTimer;
  @FXML private Label labelPasscode;
  @FXML private Label labelObjective;
  @FXML private ImageView globe;
  private int currentImageIndex = 0;
  @FXML private ImageView tape;
  @FXML private ImageView sdCard;
  @FXML private TextArea objText;
  @FXML private TextArea hintsText;
  @FXML private ImageView globe1;
  @FXML private Button rgbClue1;

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // Displaying the items which are collected/not collected
    if (GameState.isRgbClueFound) {
      rgbClue1.setOpacity(1);
      rgbClue1.setText(GameState.password);
    }
    // Intialisng the hints and objectives section of the scene
    objText.setText(GameState.getObjective());
    hintsText.setText(GameState.getHint());
    if (GameState.isSdCardFound) {
      sdCard.setOpacity(1);
    }
    if (GameState.isElectricalTapeFound) {
      tape.setOpacity(1);
    }
    if (GameState.isGlobeFound) {
      globe1.setOpacity(1);
    }
    lockedRoomTimer.setText(GameState.getTimeLeft());
    // Timer thread
    Thread lockedRoomTimeThread =
        new Thread(
            () -> {
              startLockedRoomTimer();
            });
    lockedRoomTimeThread.start();
    // game master animation
    // Start the animation
    startAnimation();

    TranslateTransition translateTransition =
        new TranslateTransition(Duration.seconds(2), lockedRoomGameMaster);

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
    GraphicsContext gc = lockedRoomGameMaster.getGraphicsContext2D();
    AnimationTimer timer =
        new AnimationTimer() {
          private long lastTime = 0;
          private final long frameDurationMillis = 100; // 1000 milliseconds = 1 second

          @Override
          public void handle(long currentTime) {
            if (currentTime - lastTime >= frameDurationMillis * 1_000_000) {
              if (currentImageIndex < GameState.alienImages.length) {
                gc.clearRect(
                    0, 0, lockedRoomGameMaster.getWidth(), lockedRoomGameMaster.getHeight());
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
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  private void onKeyPressed(KeyEvent event) {
    System.out.println("key " + event.getCode() + " pressed");
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  private void onKeyReleased(KeyEvent event) {
    System.out.println("key " + event.getCode() + " released");
  }

  /** Changes room view to bathroom view. */
  @FXML
  private void enterBathroom() {
    GameState.currentRoom = "bathroom";
    App.setUi("bathroom");
  }

  /** Changes room view to Computer room view. */
  @FXML
  private void enterComputerRoom() {
    GameState.currentRoom = "computerroom";
    App.setUi("computerroom");
  }

  /** Highlights the navigation arrow towards the bathroom when mouse hovers over arrow. */
  @FXML
  private void highlightBathroom() {
    toBathroom.setOpacity(1);
    toBathroom.setScaleX(1.2);
    toBathroom.setScaleY(1.2);
  }

  /** Unhighlights the navigation arrow towards the bathroom when mouse stops hover over arrow. */
  @FXML
  private void removeHighlightBathroom() {
    toBathroom.setOpacity(0.3);
    toBathroom.setScaleX(1);
    toBathroom.setScaleY(1);
  }

  /** Highlights the navigation arrow towards the Computer room when mouse hovers over arrow. */
  @FXML
  private void highlightComputerRoom() {
    toComputerRoom.setOpacity(1);
    toComputerRoom.setScaleX(1.2);
    toComputerRoom.setScaleY(1.2);
  }

  /**
   * Unhighlights the navigation arrow towards the Computer room when mouse stops hover over arrow.
   */
  @FXML
  private void removeHighlightComputerRoom() {
    toComputerRoom.setOpacity(0.3);
    toComputerRoom.setScaleX(1);
    toComputerRoom.setScaleY(1);
  }

  /**
   * Handles mouse click on the Gamemaster to change to chat view.
   *
   * @param event Mouse click event
   */
  @FXML
  private void clickQuizMaster(MouseEvent event) {

    App.setUi("chat");
  }

  /** Begins updating timer label according to time left in the game. */
  public void startLockedRoomTimer() {
    Timeline lockedRoomTimeline =
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
                            lockedRoomTimer.setText(GameState.getTimeLeft());
                          }
                        });
                  }
                }));

    lockedRoomTimeline.setCycleCount((GameState.minutes * 60) + GameState.seconds - 1);
    lockedRoomTimeline.play();
  }

  /**
   * Handles mouse enter event on the globe to highlight globe.
   *
   * @param event mouse enter event.
   */
  @FXML
  private void increaseGlobeSize(MouseEvent event) {
    if (GameState.isLightPuzzleSolved) {
      globe.setScaleX(1.05);
      globe.setScaleY(1.05);
    } else {
      return;
    }
  }

  /**
   * Handles mouse exit event on the globe to un-highlight globe.
   *
   * @param event mouse exit event.
   */
  @FXML
  private void decreaseGlobeSize(MouseEvent event) {
    globe.setScaleX(1);
    globe.setScaleY(1);
  }

  /** Highlights the blue button when mouse hovers over. */
  @FXML
  private void enterBlue() {
    if (GameState.isPuzzleSolved) {
      buttonBlue.setScaleX(1.2);
      buttonBlue.setScaleY(1.2);
    }
  }

  /** Un-highlights the blue button when mouse stops hover. */
  @FXML
  private void exitBlue() {
    if (GameState.isPuzzleSolved) {
      buttonBlue.setScaleX(1);
      buttonBlue.setScaleY(1);
    }
  }

  /** Highlights the red button when mouse hovers over. */
  @FXML
  private void enterRed() {
    if (GameState.isPuzzleSolved) {
      buttonRed.setScaleX(1.2);
      buttonRed.setScaleY(1.2);
    }
  }

  /** Un-highlights the red button when mouse stops hover. */
  @FXML
  private void exitRed() {
    if (GameState.isPuzzleSolved) {
      buttonRed.setScaleX(1);
      buttonRed.setScaleY(1);
    }
  }

  /** Highlights the green button when mouse hovers over. */
  @FXML
  private void enterGreen() {
    if (GameState.isPuzzleSolved) {
      buttonGreen.setScaleX(1.2);
      buttonGreen.setScaleY(1.2);
    }
  }

  /** Un-highlights the green button when mouse stops hover. */
  @FXML
  private void exitGreen() {
    if (GameState.isPuzzleSolved) {
      buttonGreen.setScaleX(1);
      buttonGreen.setScaleY(1);
    }
  }

  /** Highlights the yellow button when mouse hovers over. */
  @FXML
  private void enterYellow() {
    if (GameState.isPuzzleSolved) {
      buttonYellow.setScaleX(1.2);
      buttonYellow.setScaleY(1.2);
    }
  }

  /** Un-highlights the yellow button when mouse stops hover. */
  @FXML
  private void exitYellow() {
    if (GameState.isPuzzleSolved) {
      buttonYellow.setScaleX(1);
      buttonYellow.setScaleY(1);
    }
  }

  /** Checks if entered passcode matches the correct code. */
  private void checkPasscode() {
    // If passcode is correct, pauses the timer.
    if (labelPasscode.getText().equals(GameState.password)) {
      GameState.gameTimeline.pause();
      // Setting details for the passcode if correct
      System.out.println("Success");
      buttonBlue.setOnMouseClicked(null);
      buttonBlue.setOnMouseEntered(null);
      buttonRed.setOnMouseClicked(null);
      buttonRed.setOnMouseEntered(null);
      buttonGreen.setOnMouseClicked(null);
      buttonGreen.setOnMouseEntered(null);
      buttonYellow.setOnMouseClicked(null);
      buttonYellow.setOnMouseEntered(null);
      rectangleDoorOne.setOpacity(0);
      rectangleDoorTwo.setOpacity(0);
      rectangleDoorThree.setOpacity(0);
      GameState.isRgbSolved = true;
    } else {
      labelPasscode.setText("");
    }
  }

  /** Changes view to win view when passcode is correct. */
  public void endGame() {
    // create a to mintues delay
    // then go to the win screen
    if (GameState.isRgbSolved) {
      Timeline timeline =
          new Timeline(
              new KeyFrame(
                  Duration.seconds(1.2),
                  new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                      GameState.currentRoom = "win";
                      App.setUi("win");
                    }
                  }));
      timeline.play();
    }
  }

  /**
   * Handles mouse click event on the blue button. Enters the code and checks.
   *
   * @param event mouse click event.
   */
  @FXML
  public void clickBlue(MouseEvent event) {
    // Adds the letter "B" to the passcode and checks if it is correct if the length
    // of the passcode
    // is now equal to four.
    if (GameState.isPuzzleSolved) {
      labelPasscode.setText(labelPasscode.getText() + "⏚");
      try {
        Thread.sleep(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (labelPasscode.getText().length() == 4) {
        checkPasscode();
        endGame();
      }
    }
  }

  /**
   * Handles mouse click event on the red button. Enters the code and checks.
   *
   * @param event mouse click event.
   */
  @FXML
  public void clickRed(MouseEvent event) {
    // Adds the letter "B" to the passcode and checks if it is correct if the length
    // of the passcode
    // is now equal to four.
    if (GameState.isPuzzleSolved) {
      labelPasscode.setText(labelPasscode.getText() + "⍀");
      try {
        Thread.sleep(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (labelPasscode.getText().length() == 4) {
        checkPasscode();
        endGame();
      }
    }
  }

  /**
   * Handles mouse click event on the green button. Enters the code and checks.
   *
   * @param event mouse click event.
   */
  @FXML
  public void clickGreen(MouseEvent event) {
    // Adds the letter "B" to the passcode and checks if it is correct if the length
    // of the passcode
    // is now equal to four.
    if (GameState.isPuzzleSolved) {
      labelPasscode.setText(labelPasscode.getText() + "☌");
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (labelPasscode.getText().length() == 4) {
        checkPasscode();
        endGame();
      }
    }
  }

  /**
   * Handles mouse click event on the yellow button. Enters the code and checks.
   *
   * @param event mouse click event.
   */
  @FXML
  public void clickYellow(MouseEvent event) {
    // Adds the letter "B" to the passcode and checks if it is correct if the length
    // of the passcode
    // is now equal to four.
    if (GameState.isPuzzleSolved) {
      labelPasscode.setText(labelPasscode.getText() + "⊬");
      try {
        Thread.sleep(250);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (labelPasscode.getText().length() == 4) {
        checkPasscode();
        endGame();
      }
    }
  }

  /**
   * Handles mouse click event on the globe. Changes view to puzzle view.
   *
   * @param event mouse click event.
   */
  @FXML
  private void clickGlobe(MouseEvent event) {
    if (!GameState.isGlobeFound) {
      objText.setText("You're missing the globe item required to access the puzzle!");
      return;
    } else {
      // Sending prompt to GPT for the usre progress and gameflow
      Thread thread =
          new Thread(
              () -> {
                GameState.sendPrompt(
                    "The player has found the globe and started the jigsaw puzzle. The jigsaw"
                        + " puzzle represent an image of part of the locked room. Tell the user to"
                        + " solve the jigsaw puzzle so it creates the image of the locked room");
              });
      thread.start();
      GameState.isGlobeAccessed = true;
      App.setUi("puz");
      GameState.currentRoom = "puz";
    }
  }
}
