package realestate;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

/**
 * Step 1: send one question to a local model and print the answer.
 * Run it from your IDE, or with Maven:
 *   mvn -q compile exec:java -Dexec.mainClass=realestate.Step1FirstCall
 */
public class Step1FirstCall {

    public static void main(String[] args) {
        // Connect to the model that Ollama runs here
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("gemma3:1b")
                .build();

        // Text in, text out
        String question = "Write a one-sentence ad for "
                + "a sunny 2-bedroom apartment near UVic.";
        String answer = model.chat(question);
        System.out.println(answer);
    }
}
