package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static Scene scene;


  public static void main(final String[] args) {
    launch();
  }

  public static void setRoot(String fxml) throws IOException {
    scene.setRoot(loadFxml(fxml));
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    // chatParent = loadFxml("chat");
    Parent root = loadFxml("intro");
    scene = new Scene(root, 1000, 503);
    stage.setScene(scene);
    stage.show();
    stage.setResizable(false);
    root.requestFocus();
    // Speech thread allowing text to speech
    Thread initSpeechThread =
        new Thread(
            () -> {
              TextToSpeech textToSpeech = new TextToSpeech();
              textToSpeech.speak(
                  "Welcome, challengers. I am the Alien Gamemaster, creator of this intricate lair"
                      + " and spaceship. Solve my riddle, overcome my challenges, or be trapped"
                      + " forever. Good Luck");
            });
    initSpeechThread.setDaemon(true);
    initSpeechThread.start();
  }

  /**
   * This method allows for the switching between the two different scenes
   *
   * @param fxml The name of the FXML file (without extension).
   * @throws IOException If the file is not found.
   */
  public static void setUi(String fxml) {
    try {
      scene.setRoot(loadFxml(fxml));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
