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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

/** Controller class for the light puzzle view. */
public class LightController {
  // Intialising all the variables for the scene

  @FXML private Circle behindLight;
  @FXML private Label lightTimer;
  @FXML private Canvas lightGameMaster;
  @FXML private Rectangle quizMaster;
  @FXML private ImageView fixOne;
  @FXML private ImageView fixTwoOne;
  @FXML private ImageView fixTwoTwo;
  @FXML private Rectangle fixTwoThree;
  @FXML private ImageView fixThreeOne;
  @FXML private ImageView fixThreeTwo;
  @FXML private ImageView fixFour;
  @FXML private Label lightSuggest;
  private int currentImageIndex = 0;
  @FXML private TextArea objText;
  @FXML private TextArea hintsText;
  @FXML private ImageView tape;
  @FXML private ImageView sdCard;
  @FXML private ImageView globe;
  @FXML private ImageView globe1;
  @FXML private Button rgbClue1;

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // Intialisng the items collected by the user/ will collect from the scene
    if (GameState.isRgbClueFound) {
      rgbClue1.setOpacity(1);
      rgbClue1.setText(GameState.password);
    }
    // Intialisng the objectives and hints section of the scene
    objText.setText(GameState.getObjective());
    hintsText.setText(GameState.getHint());
    if (GameState.isSdCardFound) {
      sdCard.setOpacity(1);
    }
    if (GameState.isElectricalTapeFound) {
      tape.setOpacity(1);
    }
    // Intialisng the items collected by the user/ will collect from the scene
    if (!GameState.isGlobeFound && GameState.isLightPuzzleSolved) {
      globe.setVisible(GameState.isRiddleResolved);
    } else {
      globe.setVisible(false);
    }
    if (GameState.isGlobeFound) {
      globe1.setOpacity(1);
    }
    if (GameState.isLightPuzzleSolved) {
      objText.setText(GameState.getObjective());
    } else {
      objText.setText("Fix the wires to turn on the light.");
    }
    // Checking if the light wires are fixed
    if (GameState.isWireOneFixed) {
      fixOne.setOpacity(1);
      fixOne.setOnMouseClicked(null);
    }
    // Checking if the light wires are fixed
    if (GameState.isWireTwoFixed) {
      fixTwoOne.setOpacity(1);
      fixTwoTwo.setOpacity(1);
      fixTwoThree.setOpacity(1);
      fixTwoOne.setOnMouseClicked(null);
      fixTwoTwo.setOnMouseClicked(null);
      fixTwoThree.setOnMouseClicked(null);
    }
    // Checking if the light wires are fixed
    if (GameState.isWireThreeFixed) {
      fixThreeOne.setOpacity(1);
      fixThreeTwo.setOpacity(1);
      fixThreeOne.setOnMouseClicked(null);
      fixThreeTwo.setOnMouseClicked(null);
    }
    // Checking if the light wires are fixed
    if (GameState.isWireFourFixed) {
      fixFour.setOpacity(1);
      fixFour.setOnMouseClicked(null);
    }
    // If all of them are fixed display the mesage
    if (GameState.wireFixes == 4) {
      lightSuggest.setText("All the wires have been fixed.");
    }
    lightSuggest.setWrapText(true);
    lightTimer.setText(GameState.getTimeLeft());
    // timer thread
    Thread lightTimeThread =
        new Thread(
            () -> {
              startLightTimer();
            });
    lightTimeThread.start();

    // Start the animation
    startAnimation();

    TranslateTransition translateTransition =
        new TranslateTransition(Duration.seconds(2), lightGameMaster);

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
    GraphicsContext gc = lightGameMaster.getGraphicsContext2D();
    AnimationTimer timer =
        new AnimationTimer() {
          private long lastTime = 0;
          private final long frameDurationMillis = 100; // 1000 milliseconds = 1 second

          @Override
          public void handle(long currentTime) {
            if (currentTime - lastTime >= frameDurationMillis * 1_000_000) {
              if (currentImageIndex < GameState.alienImages.length) {
                gc.clearRect(0, 0, lightGameMaster.getWidth(), lightGameMaster.getHeight());
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
   * Handles mouse click on the game master. Opens chat view.
   *
   * @param event Mouse click event.
   */
  @FXML
  private void clickQuizMaster(MouseEvent event) {
    App.setUi("chat");
  }

  /** Begins updating timer according to time left in the game. */
  public void startLightTimer() {
    Timeline lightTimeline =
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
                            lightTimer.setText(GameState.getTimeLeft());
                          }
                        });
                  }
                }));

    lightTimeline.setCycleCount((GameState.minutes * 60) + GameState.seconds - 1);
    lightTimeline.play();
  }

  /**
   * Handles action event on go back button. Changes to bathroom view.
   *
   * @param event Action event on button.
   */
  @FXML
  private void onGoBack(ActionEvent event) {
    if (GameState.isLightPuzzleSolved) {
      GameState.currentObj = "Picture Puz";
    }
    GameState.isLightPuzzleStarted = true;
    GameState.currentRoom = "bathroom";
    App.setUi("bathroom");
  }

  /** Fixes the first broken wire. */
  @FXML
  private void clickBreakOne() {
    // Checking if the electrical tape is found
    if (GameState.isElectricalTapeFound) {
      // Patching the wire
      objText.setText("Patch the wires with the electrical tape.");
      fixOne.setOpacity(1);
      GameState.isWireOneFixed = true;
      fixOne.setOnMouseClicked(null);
      GameState.wireFixes++;
      if (GameState.wireFixes == 4) {
        lightSuggest.setText("Good Job! Collect your next clue.");
        objText.setText(
            "Good Job! You have solved the light puzzle. Collect the Picture of the Globe and"
                + " travel to your next puzzle.");
        // Sending prompt to gpt to update game flow and progress
        Thread thread =
            new Thread(
                () -> {
                  GameState.sendPrompt(
                      "The player has fixed the broken light. Tell the user to collect the Globe"
                          + " clue which has appeared in the room. Tell the user explicitly that"
                          + " the item is a pictuer of the globe.");
                });
        thread.start();
        GameState.isLightPuzzleSolved = true;
        globe.setVisible(true);
      }
    } else {
      objText.setText(
          "You need to find the Electrical Tape needed to patch the wires. Check every room"
              + " carefully!");
    }
  }

  /** Fixes the second broken wire. */
  @FXML
  private void clickBreakTwo() {
    // Checking if the electrical tape is found
    if (GameState.isElectricalTapeFound) {
      // Patching the wire
      objText.setText("Patch the wires with the electrical tape.");
      fixTwoOne.setOpacity(1);
      fixTwoTwo.setOpacity(1);
      fixTwoThree.setOpacity(1);
      GameState.isWireTwoFixed = true;
      fixTwoOne.setOnMouseClicked(null);
      fixTwoTwo.setOnMouseClicked(null);
      fixTwoThree.setOnMouseClicked(null);
      GameState.wireFixes++;
      if (GameState.wireFixes == 4) {
        lightSuggest.setText("Good Job! Collect your next clue.");
        objText.setText(
            "Good Job! You have solved the light puzzle. Collect the Picture of the Globe and"
                + " travel to your next puzzle.");
        // Sending prompt to gpt to update game flow and progress
        Thread thread =
            new Thread(
                () -> {
                  GameState.sendPrompt(
                      "The player has fixed the broken light. Tell the user to collect the Globe"
                          + " clue which has appeared in the room. Tell the user explicitly that"
                          + " the item is a pictuer of the globe.");
                });
        thread.start();
        GameState.isLightPuzzleSolved = true;
        globe.setVisible(true);
      }
    } else {
      objText.setText(
          "You need to find the Electrical Tape needed to patch the wires. Check every room"
              + " carefully!");
    }
  }

  /** Fixes the third broken wire. */
  @FXML
  private void clickBreakThree() {
    // Checking if the electrical tape is found
    if (GameState.isElectricalTapeFound) {
      // Patching the wire
      objText.setText("Patch the wires with the electrical tape.");
      fixThreeOne.setOpacity(1);
      fixThreeTwo.setOpacity(1);
      GameState.isWireThreeFixed = true;
      fixThreeOne.setOnMouseClicked(null);
      fixThreeTwo.setOnMouseClicked(null);
      GameState.wireFixes++;
      if (GameState.wireFixes == 4) {
        lightSuggest.setText("Good Job! Collect your next clue.");
        objText.setText(
            "Good Job! You have solved the light puzzle. Collect the Picture of the Globe and"
                + " travel to your next puzzle.");
        // Sending prompt to gpt to update game flow and progress
        Thread thread =
            new Thread(
                () -> {
                  GameState.sendPrompt(
                      "The player has fixed the broken light. Tell the user to collect the Globe"
                          + " clue which has appeared in the room. Tell the user explicitly that"
                          + " the item is a pictuer of the globe.");
                });
        thread.start();
        GameState.isLightPuzzleSolved = true;
        globe.setVisible(true);
      }
    } else {
      objText.setText(
          "You need to find the Electrical Tape needed to patch the wires. Check every room"
              + " carefully!");
    }
  }

  /** Fixes the fourth broken wire. */
  @FXML
  private void clickBreakFour() {
    // Checking if the electrical tape is found
    if (GameState.isElectricalTapeFound) {
      // Patching the wire
      objText.setText("Patch the wires with the electrical tape.");
      fixFour.setOpacity(1);
      GameState.isWireFourFixed = true;
      fixFour.setOnMouseClicked(null);
      GameState.wireFixes++;
      if (GameState.wireFixes == 4) {
        lightSuggest.setText("Good Job! Collect your next clue.");
        objText.setText(
            "Good Job! You have solved the light puzzle. Collect the Picture of the Globe and"
                + " travel to your next puzzle.");
        // Sending prompt to gpt to update game flow and progress
        Thread thread =
            new Thread(
                () -> {
                  GameState.sendPrompt(
                      "The player has fixed the broken light. Tell the user to collect the Globe"
                          + " clue which has appeared in the room. Tell the user explicitly that"
                          + " the item is a picture of the globe.");
                });
        thread.start();
        GameState.isLightPuzzleSolved = true;
        globe.setVisible(true);
      }
    } else {
      objText.setText(
          "You need to find the Electrical Tape needed to patch the wires. Check every room"
              + " carefully!");
    }
  }

  /** Highlights the given puzzle piece when mouse hovers. */
  @FXML
  private void increaseGlobeSize() {
    globe.setScaleX(1.2);
    globe.setScaleY(1.2);
  }

  /** Unhighlights the given puzzle piece when mouse stops hover. */
  @FXML
  private void decreaseGlobeSize() {
    globe.setScaleX(1);
    globe.setScaleY(1);
  }

  /** Moves the puzzle piece item to inventory. */
  @FXML
  private void clickGlobe() {
    GameState.isGlobeFound = true;
    // Sending a prompt to gpt to update it on the user's progress
    // and the gameflow, to ensure the right hint is being sent.
    Thread thread =
        new Thread(
            () -> {
              GameState.sendPrompt(
                  "The player has now collected the clue of the Globe. Now tell the user to go to"
                      + " the locked room and click on the globe to access the next puzzle. Be"
                      + " explicit in telling the user to go to the locked room");
            });
    thread.start();
    GameState.currentObj = "Picture Puz";
    lightSuggest.setText("");
    globe.setVisible(false);
    globe1.setOpacity(1);
  }
}
