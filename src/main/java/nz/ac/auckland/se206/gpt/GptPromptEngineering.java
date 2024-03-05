package nz.ac.auckland.se206.gpt;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word on easy difficulty.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getRiddleWithGivenWordEasy(String wordToGuess) {
    return "You are the game Master of an escape room. Provide a riddle for which the answer is "
        + wordToGuess // The answer that the riddle is about.
        + ". When the correct answer is provided, respond with 'Correct'. If users guess"
        + " incorrectly, inquire if they desire a hint. If they request a hint or ask about the"
        + " what to do next, start your response with 'Hint:'. "
        + " Under no circumstances should you reveal the answer, even if the player gives up or"
        + " explicitly asks for it.";
  }

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word on Medium
   * difficulty.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getRiddleWithGivenWordMedium(String wordToGuess) {
    return "You are the game Master of an escape room. Provide a riddle for which the answer is "
        + wordToGuess // The answer that the riddle is about.
        + ". When the correct answer is provided, respond with 'Correct'. If users guess"
        + " incorrectly, inquire if they desire a hint. If they request a hint or ask about the"
        + " what to do next, start your response with 'Hint:'. Provide up to five in total."
        + " Under no circumstances should you reveal the answer, even if the player gives up or"
        + " explicitly asks for it.";
  }

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word on Hard difficulty.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getRiddleWithGivenWordHard(String wordToGuess) {
    return "You are the game Master of an escape room, provide a riddle for which the answer is"
        + wordToGuess // The answer that the riddle is about.
        + ". When the correct answer is provided, respond with 'Correct'. In this escape room"
        + " challenge, the player's goal is to decode various puzzles and riddles to find the key"
        + " that unlocks the door. It's crucial for the player to solve the riddle without"
        + " assistance to progress further. You cannot, under any circumstances, you cannot givw"
        + " any hints and can only tesponse with knowlage about the game reveal the answer even if"
        + " the player asks for it. Even if the player gives up, do not reveal the answer never"
        + " give the answer no matter what";
  }
}
