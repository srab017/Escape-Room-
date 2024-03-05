package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;
import nz.ac.auckland.se206.speech.TextToSpeech;

// import javafx.scene.control.Alert;

/** Controller class for the chat view. */
public class ChatController {
  // Creating the variables for the chat view
  @FXML private TextArea chatTextArea;
  @FXML private TextField inputText;
  @FXML private Button sendButton;
  @FXML private Canvas chatQuizMaster;
  @FXML private Label chatTimer;
  @FXML private Label labelTranslate;
  private Image[] chatAlienImages;
  private int currentImageIndex = 0;
  @FXML private ImageView sdCard;
  @FXML private Label sdCollect;
  @FXML private ImageView sdCard1;
  @FXML private ImageView tape;
  @FXML private TextArea objText;
  @FXML private TextArea hintsText;
  @FXML private ImageView globe;
  @FXML private ImageView soundImage;
  @FXML private Button rgbClue1;
  @FXML private Rectangle sound;

  private String gptResponse;

  private TextToSpeech textToSpeech = new TextToSpeech();

  /**
   * Initializes the chat view, loading the riddle.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() throws ApiProxyException {
    // Display the hint count if its medium level
    if (GameState.isMediumPicked) {
      hintsText.setText("Hints remaining: " + (5 - GameState.hintCounter));
    }
    // Intialising the text area of the scene with game progress
    objText.setText(GameState.getObjective());
    hintsText.setText(GameState.getHint());
    chatTextArea.setText(GameState.chatContents);
    // Displaying the item if its found
    if (GameState.isRgbClueFound) {
      rgbClue1.setOpacity(1);
      rgbClue1.setText(GameState.password);
    }

    // if the player exited and the text area was empty run gpt
    if (GameState.isGameMasterLoaded && chatTextArea.getText().isEmpty()) {
      if (GameState.isEasyPicked) {
        runGpt(
            new ChatMessage(
                "user", GptPromptEngineering.getRiddleWithGivenWordEasy(GameState.riddleAnswer)));
      } else if (GameState.isMediumPicked) {
        runGpt(
            new ChatMessage(
                "user", GptPromptEngineering.getRiddleWithGivenWordMedium(GameState.riddleAnswer)));
      } else {
        runGpt(
            new ChatMessage(
                "user", GptPromptEngineering.getRiddleWithGivenWordHard(GameState.riddleAnswer)));
      }
    }

    // Allowing the rungpt if the gamemaster is loaded
    if (!GameState.isGameMasterLoaded && GameState.isEasyPicked) {
      runGpt(
          new ChatMessage(
              "user", GptPromptEngineering.getRiddleWithGivenWordEasy(GameState.riddleAnswer)));
      GameState.isGameMasterLoaded = true;
    } else if (!GameState.isGameMasterLoaded && GameState.isMediumPicked) {
      runGpt(
          new ChatMessage(
              "user", GptPromptEngineering.getRiddleWithGivenWordMedium(GameState.riddleAnswer)));
      GameState.isGameMasterLoaded = true;
    } else if (!GameState.isGameMasterLoaded && GameState.isDifficultPicked) {
      runGpt(
          new ChatMessage(
              "user", GptPromptEngineering.getRiddleWithGivenWordHard(GameState.riddleAnswer)));
      GameState.isGameMasterLoaded = true;
    }
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
    // Displaying the sdcard if its found
    if (!GameState.isSdCardFound) {
      sdCard.setVisible(GameState.isRiddleResolved);
      if (GameState.isRiddleResolved) {
        sdCollect.setText("Collect the SD card!");
      }
    } else {
      sdCard.setVisible(false);
    }
    // Displaying the items if found/not found
    if (GameState.isSdCardFound) {
      sdCard1.setOpacity(1);
    }
    if (GameState.isElectricalTapeFound) {
      tape.setOpacity(1);
    }
    if (GameState.isGlobeFound) {
      globe.setOpacity(1);
    }
    chatTimer.setText(GameState.getTimeLeft());
    // thread for the timer
    Thread chatTimeThread =
        new Thread(
            () -> {
              startChatTimer();
            });
    chatTimeThread.start();

    // game master animation
    // Initialize alienImages with your image paths
    chatAlienImages = new Image[] {new Image("images/blink1.png"), new Image("images/blink2.png")};

    TranslateTransition translateTransition =
        new TranslateTransition(Duration.seconds(2), chatQuizMaster);

    // set the Y-axis translation value
    translateTransition.setByY(-10);

    // set the number of cycles for the animation
    translateTransition.setCycleCount(TranslateTransition.INDEFINITE);

    // Set auto-reverse to true to make the label return to its original position
    translateTransition.setAutoReverse(true);

    // Start the animation
    translateTransition.play();

    // Start the animation
    startAnimation();
  }

  /** Starts the animation of the Gamemaster. */
  private void startAnimation() {
    // Animation for the gamemaster
    GraphicsContext gc = chatQuizMaster.getGraphicsContext2D();
    AnimationTimer timer =
        new AnimationTimer() {
          private long lastTime = 0;
          private final long frameDurationMillis = 1000; // 1000 milliseconds = 1 second

          @Override
          public void handle(long currentTime) {
            if (currentTime - lastTime >= frameDurationMillis * 1_000_000) {
              if (currentImageIndex < chatAlienImages.length) {
                gc.clearRect(0, 0, chatQuizMaster.getWidth(), chatQuizMaster.getHeight());
                gc.drawImage(chatAlienImages[currentImageIndex], 0, 0);
                currentImageIndex++;
                // Check if we have displayed all images; if so, reset the index to 0
                if (currentImageIndex >= chatAlienImages.length) {
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
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {

    String emojiRepresentation = "";
    if (msg.getRole().equals("user")) {
      emojiRepresentation = "👤: ";
      chatTextArea.setStyle("-fx-text-fill: blue;");
    } else if (msg.getRole().equals("assistant")) {
      emojiRepresentation = "⏃⌰⟟⟒⋏: ";
      chatTextArea.setStyle("-fx-text-fill: green;");
      GameState.assistanceSpeech = msg.getContent();
      if (GameState.isSoundOn) {
        Thread assSpeechThread =
            new Thread(
                () -> {
                  textToSpeech.speak(GameState.assistanceSpeech);
                });
        assSpeechThread.setDaemon(true);
        assSpeechThread.start();
      }
    }

    chatTextArea.appendText(emojiRepresentation + msg.getContent() + "\n\n");
    gptResponse = emojiRepresentation + msg.getContent() + "\n\n";

    // ensure that the response is appended even if the player exited the chat view
    if (chatTextArea.getText().contains(gptResponse)) {
      GameState.chatContents = chatTextArea.getText();
    } else {
      GameState.chatContents = chatTextArea.getText() + "\n\n" + gptResponse + "\n\n";
    }
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private CompletableFuture<ChatMessage> runGpt(ChatMessage msg) throws ApiProxyException {
    labelTranslate.setOpacity(0.55);
    GameState.chatCompletionRequest.addMessage(msg);
    CompletableFuture<ChatMessage> completableFuture = new CompletableFuture<>();

    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            try {
              ChatCompletionResult chatCompletionResult = GameState.chatCompletionRequest.execute();
              Choice result = chatCompletionResult.getChoices().iterator().next();
              GameState.chatCompletionRequest.addMessage(result.getChatMessage());

              Platform.runLater(
                  () -> {
                    appendChatMessage(result.getChatMessage());
                    labelTranslate.setOpacity(0);
                    completableFuture.complete(result.getChatMessage()); // Complete the future
                  });
            } catch (ApiProxyException e) {
              // TODO handle exception appropriately
              e.printStackTrace();
              completableFuture.completeExceptionally(e); // Complete the future exceptionally
            }
            return null;
          }
        };

    Thread thread = new Thread(task);
    thread.start();

    return completableFuture;
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {

    String message = inputText.getText();
    if (message.trim().isEmpty()) {
      return;
    }
    // Sends prompts to the GPT api to overlook the gameflow and game progress if
    // the user does ask
    // for a hint
    if (GameState.hintCounter == 5 && GameState.isMediumPicked) {
      Thread thread =
          new Thread(
              () -> {
                GameState.sendPrompt(
                    "The player has reached their hint limit. Do not provide any help to the"
                        + " player. Do not give the player any hints. Do not tell the player the"
                        + " next step.");
              });
      thread.start();
    }
    // Sends prompts to the GPT api to overlook the gameflow and game progress if
    // the user does not
    // ask for a hint
    if (GameState.isDifficultPicked) {
      Thread thread =
          new Thread(
              () -> {
                GameState.sendPrompt(
                    "The player is in difficult mode. Do not provide any help to the player. Do not"
                        + " give the player any hints. Do not tell the player the next step.");
              });
      thread.start();
    }

    inputText.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);
    // Checking the riddle answer of the user to the riddle given
    CompletableFuture<ChatMessage> future = runGpt(msg);
    future.thenAccept(
        lastMsg -> {
          if (lastMsg.getRole().equals("assistant")) {
            if (lastMsg.getContent().startsWith("Correct")) {
              GameState.isRiddleResolved = true;
              sdCard.setVisible(true);
              sdCollect.setText("Collect the SD card!");
              objText.setText(GameState.getObjective());
              GameState.currentObj = "Decrypt";
              // Creating prompt for the GPT api to overlook the gameflow and game progress
              Thread thread =
                  new Thread(
                      () -> {
                        GameState.sendPrompt(
                            "The player has solved the riddle. Tell the player to find an object in"
                                + " the room which is a SD card object. The object is not related"
                                + " to the riddle answer. Be explicit that the object is a SD"
                                + " card");
                      });
              thread.start();
            } else if (lastMsg.getContent().contains("hint: ")
                || lastMsg.getContent().contains("Hint: ")
                || lastMsg.getContent().contains("Clue: ")
                || lastMsg.getContent().contains("clue: ")
                || lastMsg.getContent().contains("Help: ")
                || lastMsg.getContent().contains("help: ")) {
              // Updating hint counter
              if (GameState.hintCounter < 5) {
                GameState.hintCounter++;
              }
              GameState.latestHint = lastMsg.getContent();
              // Display the hint count if its medium level
              if (GameState.isMediumPicked && GameState.hintCounter <= 5) {
                hintsText.setText(
                    "Hints Remaining: "
                        + (5 - GameState.hintCounter)
                        + "\n"
                        + GameState.latestHint);
                // Display the hint on easy.
              } else if (GameState.isEasyPicked) {
                hintsText.setText("Unlimited number of hints" + "\n" + GameState.latestHint);
              }
            }
          }
        });
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setUi(GameState.currentRoom);
  }

  /** Starts updating the timer according to time left in the game. */
  public void startChatTimer() {
    Timeline chatTimeline =
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
                            chatTimer.setText(GameState.getTimeLeft());
                          }
                        });
                  }
                }));

    chatTimeline.setCycleCount((GameState.minutes * 60) + GameState.seconds - 1);
    chatTimeline.play();
  }

  // Method for increasing size the SD card when hovering
  /** Highlights the sd card when the mouse hovers over. */
  @FXML
  private void increaseSize() {
    sdCard.setScaleX(1.2);
    sdCard.setScaleY(1.2);
  }

  /** Un-highlights the sd card when the mouse stops hovering. */
  @FXML
  private void decreaseSize() {
    sdCard.setScaleX(1);
    sdCard.setScaleY(1);
  }

  /** Moves sd card into 'inventory' when user clicks on it. */
  @FXML
  private void clickSdCard() {
    GameState.isSdCardFound = true;
    // Sending a prompt to GPT to update the user's progress and to
    // ensure the right hint is givento the user
    Thread thread =
        new Thread(
            () -> {
              GameState.sendPrompt(
                  "The player has collected the SD card. Now tell the user to find an object in any"
                      + " room which will use the SD Card they just collected.");
            });
    thread.start();
    sdCard.setVisible(false);
    sdCollect.setText("");
    sdCard1.setOpacity(1);
  }

  /**
   * Toggles the sound state and updates the UI accordingly.
   *
   * <p>If the sound is currently on, this method will: - Switch to the "sound off" image. -
   * Interrupt ongoing speech. Otherwise, it will: - Switch to the "sound on" image.
   *
   * @param event The triggering MouseEvent.
   */
  @FXML
  private void toggleSoundOnClick(MouseEvent event) {
    // check if the sound is currently on
    if (GameState.isSoundOn) {
      // set the image to represen the sound is off
      Image newImage = new Image("images/musicoff.png");
      soundImage.setImage(newImage);
      // interrupt any ongoing speech
      textToSpeech.interruptSpeech();
      // update the game state
      GameState.isSoundOn = false;
    } else {
      // Set the image to represent the "sound on" state
      Image newImage = new Image("images/musicOn.png");
      soundImage.setImage(newImage);
      // update the game state
      GameState.isSoundOn = true;
    }
  }
}
