package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

/** Controller class for the globe puzzle view. */
public class PuzController {
  // Intialising variables required for the room and the puzzle
  @FXML private Rectangle p1;
  @FXML private Rectangle p2;
  @FXML private Rectangle p3;
  @FXML private Rectangle p4;
  @FXML private Rectangle p5;
  @FXML private Rectangle p6;
  @FXML private Rectangle p7;
  @FXML private Rectangle p8;
  @FXML private Rectangle p9;
  @FXML private ImageView pic1;
  @FXML private ImageView pic2;
  @FXML private ImageView pic3;
  @FXML private ImageView pic4;
  @FXML private ImageView pic5;
  @FXML private ImageView pic6;
  @FXML private ImageView pic7;
  @FXML private ImageView pic8;
  @FXML private ImageView pic9;
  @FXML private Label status;
  @FXML private Label puzzleTimer;
  @FXML private Button check;
  @FXML private Button goBackBtn;
  @FXML private Canvas gameMaster;
  @FXML private Rectangle quizMaster;
  private int currentImageIndex = 0;
  @FXML private TextArea objText;
  @FXML private TextArea hintsText;
  @FXML private ImageView tape;
  @FXML private ImageView sdCard;
  @FXML private ImageView globe;
  @FXML private Button rgbClue;
  @FXML private Button rgbClue1;
  @FXML private Label collect;

  private List<Rectangle> rectangles;
  private List<ImageView> imageViews;
  private List<Image> correctOrder;

  private Map<Rectangle, ImageView> map = new HashMap<>();
  private Rectangle firstSelected = null;

  // Add this member variable to store row and column data of rectangles
  private Map<Rectangle, int[]> positionMap = new HashMap<>();

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // Displaying the items which are collected/not collected
    hintsText.setText(GameState.getHint());
    if (GameState.isPuzzleSolved && !GameState.isRgbClueFound) {
      rgbClue.setVisible(true);
      rgbClue.setText(GameState.password);
    } else {
      rgbClue.setVisible(false);
    }
    if (GameState.isRgbClueFound) {
      rgbClue1.setOpacity(1);
      rgbClue1.setText(GameState.password);
    }
    if (!GameState.isPuzzleSolved) {
      objText.setText(
          "You need to solve the picture puzzle by unscrambling it. Remember you can only move"
              + " neighbouring tiles.");
    } else {
      objText.setText(GameState.objMessage);
    }
    if (GameState.isElectricalTapeFound) {
      tape.setOpacity(1);
    }
    if (GameState.isSdCardFound) {
      sdCard.setOpacity(1);
    }
    if (GameState.isGlobeFound) {
      globe.setOpacity(1);
    }
    puzzleTimer.setText(GameState.getTimeLeft());
    // Starting the timer thread for the room
    Thread puzzleTimeThread =
        new Thread(
            () -> {
              startPuzzleTimer();
            });
    puzzleTimeThread.start();

    // Start the animation
    startAnimation();
    // Starting the animations for the gamemaster

    TranslateTransition translateTransition =
        new TranslateTransition(Duration.seconds(2), gameMaster);

    // set the Y-axis translation value
    translateTransition.setByY(-10);

    // set the number of cycles for the animation
    translateTransition.setCycleCount(TranslateTransition.INDEFINITE);

    // Set auto-reverse to true to make the label return to its original position
    translateTransition.setAutoReverse(true);

    // Start the animation
    translateTransition.play();
    // Define position (row, column) for each rectangle
    positionMap.put(p1, new int[] {0, 0});
    positionMap.put(p2, new int[] {0, 1});
    positionMap.put(p3, new int[] {0, 2});
    positionMap.put(p4, new int[] {1, 0});
    positionMap.put(p5, new int[] {1, 1});
    positionMap.put(p6, new int[] {1, 2});
    positionMap.put(p7, new int[] {2, 0});
    positionMap.put(p8, new int[] {2, 1});
    positionMap.put(p9, new int[] {2, 2});

    rectangles = Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9);
    imageViews = Arrays.asList(pic1, pic2, pic3, pic4, pic5, pic6, pic7, pic8, pic9);

    Image image = new Image("images/puzzle.png");
    correctOrder = splitImage(image); // Assign to correctOrder
    List<Image> shuffledOrder = new ArrayList<>(correctOrder);
    Collections.shuffle(shuffledOrder);

    for (int i = 0; i < 9; i++) {
      imageViews.get(i).setImage(shuffledOrder.get(i));
      map.put(rectangles.get(i), imageViews.get(i));
      rectangles.get(i).setOnMouseClicked(this::swap);
    }
  }

  /** Begins animation of the Gamemaster. */
  private void startAnimation() {
    GraphicsContext gc = gameMaster.getGraphicsContext2D();
    AnimationTimer timer =
        new AnimationTimer() {
          private long lastTime = 0;
          private final long frameDurationMillis = 100; // 1000 milliseconds = 1 second

          @Override
          public void handle(long currentTime) {
            if (currentTime - lastTime >= frameDurationMillis * 1_000_000) {
              if (currentImageIndex < GameState.alienImages.length) {
                gc.clearRect(0, 0, gameMaster.getWidth(), gameMaster.getHeight());
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
   * Handles mouse click event on the Gamemaster. Changes to chat view.
   *
   * @param event mouse click event.
   */
  @FXML
  private void clickQuizMaster(MouseEvent event) {
    App.setUi("chat");
  }

  /** Starts updating label timer according to timeleft in game. */
  private void startPuzzleTimer() {
    Timeline puzzleTimeline =
        new Timeline(
            new KeyFrame(
                // Setting the time countdown to one second
                Duration.seconds(1),
                new EventHandler<ActionEvent>() {
                  @Override
                  public void handle(ActionEvent event) {
                    // Counts down the timer.
                    Platform.runLater(
                        new Runnable() {
                          @Override
                          public void run() {
                            // Displaying the time left on the label
                            puzzleTimer.setText(GameState.getTimeLeft());
                          }
                        });
                  }
                }));

    puzzleTimeline.setCycleCount((GameState.minutes * 60) + GameState.seconds - 1);
    puzzleTimeline.play();
  }

  /**
   * Splits image into 9 pieces for the puzzle.
   *
   * @param image Original image.
   * @return List of split up pieces of the puzzle.
   */
  private List<Image> splitImage(Image image) {
    List<Image> pieces = new ArrayList<>();
    PixelReader reader = image.getPixelReader();

    // Calculate piece dimensions based on the original image
    int pieceWidth = (int) image.getWidth() / 3;
    int pieceHeight = (int) image.getHeight() / 3;

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        WritableImage piece =
            new WritableImage(reader, j * pieceWidth, i * pieceHeight, pieceWidth, pieceHeight);
        pieces.add(piece);
      }
    }
    return pieces;
  }

  /**
   * Checks if two pieces are adjacent.
   *
   * @param first first image piece
   * @param second second image piece
   * @return boolean true if pieces are adjacent.
   */
  private boolean isAdjacent(Rectangle first, Rectangle second) {
    int[] pos1 = positionMap.get(first);
    int[] pos2 = positionMap.get(second);

    // Check horizontal and vertical adjacency
    boolean horizontal = pos1[0] == pos2[0] && Math.abs(pos1[1] - pos2[1]) == 1;
    boolean vertical = pos1[1] == pos2[1] && Math.abs(pos1[0] - pos2[0]) == 1;

    return horizontal || vertical;
  }

  // Method to check if the puzzle is in the correct order
  private boolean isCorrectOrder() {
    for (int i = 0; i < 9; i++) {
      if (!imageViews.get(i).getImage().equals(correctOrder.get(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Handles mouse click event on a image piece to swaps two adjacent pieces.
   *
   * @param event Mouse click event
   */
  @FXML
  private void swap(MouseEvent event) {
    Rectangle clicked = (Rectangle) event.getSource();

    // Determines when valid images have been selected and swaps accordingly.
    if (firstSelected == null) {
      firstSelected = clicked;
      firstSelected.setOpacity(0.7);
    } else {
      if (isAdjacent(firstSelected, clicked)) {
        swapImages(firstSelected, clicked);
      } else {
        firstSelected.setOpacity(0);
        firstSelected = null;
        status.setText("They must be neighbouring");
      }
    }
  }

  /**
   * Handles actions event on check button to check if puzzle is solved.
   *
   * @param event action event on check button.
   */
  @FXML
  private void onCheckPuzzle(ActionEvent event) {
    // If the puzzle is solved, display the correct message
    if (isCorrectOrder()) {
      status.setText("Correct");
      GameState.isPuzzleSolved = true;
      objText.setText(
          "Good Job! You have solved the picture puzzle. Collect the RGB Clue and travel to the"
              + " door for your final puzzle");
      // Thread to send the prompt to the GPT to over look progress
      Thread thread =
          new Thread(
              () -> {
                GameState.sendPrompt(
                    "The player has solved the jigsaw puzzle and has received the passcode to the"
                        + " locked door. The user needs to click on the RGB clue to receive the"
                        + " passcode which will unlock the door.");
              });
      // Starting the thread and setting game items
      thread.start();
      rgbClue.setVisible(true);
      rgbClue.setText(GameState.password);
      collect.setText("Collect!");
      check.disableProperty().setValue(true);
    } else {
      status.setText("Not quite! Try again.");
    }
  }

  /**
   * Swaps the locations of two adjacent image pieces.
   *
   * @param first first image piece
   * @param second second image piece
   */
  private void swapImages(Rectangle first, Rectangle second) {
    ImageView firstImage = map.get(first);
    ImageView secondImage = map.get(second);

    Image temp = firstImage.getImage();
    firstImage.setImage(secondImage.getImage());
    secondImage.setImage(temp);

    animateSwap(firstImage, secondImage);

    // After animation is done, reset opacity
    first.setOpacity(0);
    second.setOpacity(0);
    firstSelected = null; // Resetting firstSelected for next selection
  }

  /**
   * Animates the swapping of two image pieces.
   *
   * @param first first image piece.
   * @param second second image piece.
   */
  private void animateSwap(ImageView first, ImageView second) {
    // Create two translate transitions, one for each image
    TranslateTransition tt1 = new TranslateTransition();
    TranslateTransition tt2 = new TranslateTransition();
    // Set the dpositions of the images
    double dx = second.getX() - first.getX();
    double dy = second.getY() - first.getY();
    // Swapping the x and y coordinates of the images
    tt1.setByX(dx);
    tt1.setByY(dy);

    tt2.setByX(-dx);
    tt2.setByY(-dy);

    tt1.setNode(first);
    tt2.setNode(second);
    // Playing the animation for the two images

    tt1.play();
    tt2.play();

    tt1.setOnFinished(
        e -> {
          first.setTranslateX(0);
          first.setTranslateY(0);
          second.setTranslateX(0);
          second.setTranslateY(0);
        });
  }

  // Method to go back to the previous room
  /**
   * Handles action on GoBack button. Changes to locked room view.
   *
   * @param event action event on GoBack button.
   */
  @FXML
  private void onGoBack(ActionEvent event) {
    GameState.currentObj = "RGB Puzzle";
    GameState.currentRoom = "lockedroom";
    App.setUi("lockedroom");
  }

  /** Moves RGB clue to inventory. */
  @FXML
  private void clickRgb() {
    GameState.isRgbClueFound = true;
    Thread thread =
        new Thread(
            () -> {
              GameState.sendPrompt(
                  "The player has collected the RGB clue containing the passcode. Tell the user to"
                      + " head to the locked room and enter the passcode to unlock the door using"
                      + " the four coloured squares. Tell the user to decrypt the passcode on the"
                      + " clue by using the alphabet in the computer.");
            });
    // Starting the thread and setting game items
    thread.start();
    rgbClue1.setOpacity(1);
    rgbClue1.setText(GameState.password);
    rgbClue.setVisible(false);
    collect.setText("");
  }
}
