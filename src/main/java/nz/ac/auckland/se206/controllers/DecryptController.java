package nz.ac.auckland.se206.controllers;

import java.io.IOException;
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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/** Controller class for the Decryption Puzzle view. */
public class DecryptController {
  // Intialisng the variables for the scene

  @FXML private Label decryptTimer;
  @FXML private TextField inputText;
  @FXML private Button sendButton;
  @FXML private Label incorrect;
  @FXML private Canvas decryptGameMaster;
  @FXML private Rectangle quizMaster;
  private int currentImageIndex = 0;
  @FXML private TextArea message;
  @FXML private TextArea objText;
  @FXML private TextArea hintsText;
  @FXML private ImageView tape;
  @FXML private ImageView sdCard;
  @FXML private ImageView globe;
  @FXML private Button rgbClue1;

  // create array of alien symbols
  private String[] randomLights = {"⎎⟟⍀⌇⏁", "⌇⟒☊⍜⋏⎅", "⏁⊑⟟⍀⎅"};

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // when the enter key is pressed
    inputText.setOnAction(
        e -> {
          try {
            onSendMessage(e);
          } catch (ApiProxyException | IOException ex) {
            ex.printStackTrace();
            // Handle other exceptions appropriately.
          }
        });
    // Intiailising the hints, objectives seciton of the scene
    hintsText.setText(GameState.getHint());
    // Intialising the the items that the user has collected/ will collect from the
    // scene
    if (GameState.isRgbClueFound) {
      rgbClue1.setOpacity(1);
      rgbClue1.setText(GameState.password);
    }
    if (GameState.isGlobeFound) {
      globe.setOpacity(1);
    }
    if (GameState.isElectricalTapeFound) {
      tape.setOpacity(1);
    }
    if (GameState.isSdCardFound) {
      sdCard.setOpacity(1);
    }
    decryptTimer.setText(GameState.getTimeLeft());
    // Timer thread
    Thread decryptTimeThread =
        new Thread(
            () -> {
              startDecryptTimer();
            });
    decryptTimeThread.start();

    // Start the animation
    startAnimation();

    TranslateTransition translateTransition =
        new TranslateTransition(Duration.seconds(2), decryptGameMaster);

    // set the Y-axis translation value
    translateTransition.setByY(-10);

    // set the number of cycles for the animation
    translateTransition.setCycleCount(TranslateTransition.INDEFINITE);

    // Set auto-reverse to true to make the label return to its original position
    translateTransition.setAutoReverse(true);

    // Start the animation
    translateTransition.play();
  }

  /** Starts the animation movement for the Gamemaster image. */
  private void startAnimation() {
    GraphicsContext gc = decryptGameMaster.getGraphicsContext2D();
    AnimationTimer timer =
        new AnimationTimer() {
          private long lastTime = 0;
          private final long frameDurationMillis = 100; // 1000 milliseconds = 1 second

          @Override
          public void handle(long currentTime) {
            if (currentTime - lastTime >= frameDurationMillis * 1_000_000) {
              if (currentImageIndex < GameState.alienImages.length) {
                gc.clearRect(0, 0, decryptGameMaster.getWidth(), decryptGameMaster.getHeight());
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
    // Setting the scene up with the objectives, hint and the message
    if (GameState.isDecryptCompleted) {
      objText.setText(GameState.getObjective());
    } else {
      objText.setText("Decipher the message from the quiz master");
    }
    if (!GameState.isDecryptCompleted) {
      // Setting the decrypt message
      message.setText(
          "☌⍜  ⏁⍜  ⏁⊑⟒  ⏚⏃⏁⊑⍀⍜⍜⋔  ⏃⋏⎅  ⎎⟟⌖  ⏁⊑⟒  " + randomLights[GameState.randomNum] + "  ⌰⟟☌⊑⏁");
    } else {
      message.setText(
          "The message is: Go to the bathroom and fix the " + GameState.randomLight + " light");
      // disable send and input text
      sendButton.setDisable(true);
      inputText.setDisable(true);
    }
  }

  /**
   * Handles the click event on the Gamemaster to open the chat view.
   *
   * @param event the mouse click event.
   */
  @FXML
  private void clickQuizMaster(MouseEvent event) {
    App.setUi("chat");
  }

  /** Starts updating the timer according to the time left. */
  public void startDecryptTimer() {
    Timeline decryptTimeline =
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
                            decryptTimer.setText(GameState.getTimeLeft());
                          }
                        });
                  }
                }));

    decryptTimeline.setCycleCount((GameState.minutes * 60) + GameState.seconds - 1);
    decryptTimeline.play();
  }

  /**
   * Sends the decoded message to be checked.
   *
   * @param event the event that starts sending the message, (mouse click or enter button.)
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an error in an input or output operation
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    // Getting the user message for the decryption puzzle
    String message = inputText.getText();
    if (message.trim().isEmpty()) {
      return;
    }
    inputText.clear();
    message = message.toLowerCase();
    // check if the message is equal to another string
    if (message.equals("go to the bathroom and fix the " + GameState.randomLight + " light")) {
      GameState.isDecryptCompleted = true;
      objText.setText(
          "Good job! You have completed the decryption. Using the message find your next puzzle");
      incorrect.setText("Good job! You've decrypted the message");
      // disable the send button and input text
      sendButton.setDisable(true);
      inputText.setDisable(true);
      Thread thread =
          new Thread(
              () -> {
                GameState.sendPrompt(
                    "The player has deciphered the message. The player now has to go to the"
                        + " bathroom and fix the"
                        + GameState.randomLight
                        + "light by looking behind it. To get behind the light the player only need"
                        + " to click on the light.");
              });
      thread.start();
    } else {
      incorrect.setText("Incorrect! Try again");
    }
  }

  /**
   * Initializes the previous room view.
   *
   * @param event the event that starts changing to previous room view, (mouse click).
   */
  @FXML
  private void onGoBack(ActionEvent event) {
    // if the user has completed the decryption puzzle but not ocmpleted light
    if (GameState.isDecryptCompleted && !GameState.isLightPuzzleSolved) {
      GameState.currentObj = "Light Puzzle";
    }
    GameState.currentRoom = "computerroom";
    App.setUi("computerroom");
  }
}
