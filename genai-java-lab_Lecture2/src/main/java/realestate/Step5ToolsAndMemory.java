package realestate;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import java.util.List;

/**
 * Step 5: tools and memory. The model can ask our program to run a
 * Java method (a tool), and it remembers the earlier messages.
 * Run: mvn -q compile exec:java -Dexec.mainClass=realestate.Step5ToolsAndMemory
 */
public class Step5ToolsAndMemory {

    // A tool is a normal method the model can ask for
    @Tool("Monthly rents, in dollars, of similar listings")
    public List<Integer> similarRents(String neighbourhood,
                                      int bedrooms) {
        System.out.println("  [tool] similarRents("
                + neighbourhood + ", " + bedrooms + ")");
        // A real platform would search its database here
        if (neighbourhood.equalsIgnoreCase("Fernwood")
                && bedrooms == 2) {
            return List.of(2300, 2350, 2450, 2500, 2600);
        }
        return List.of();
    }

    @Tool("Typical monthly price, in dollars, of parking")
    public int parkingPrice(String neighbourhood) {
        System.out.println("  [tool] parkingPrice("
                + neighbourhood + ")");
        return 75;
    }

    interface RentAdvisor {

        @SystemMessage("You help owners price rentals in "
                + "Victoria, BC. Use the tools for every "
                + "number.")
        String chat(String message);
    }

    public static void main(String[] args) {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("granite4.1:3b")
                .temperature(0.0)
                .build();

        RentAdvisor advisor = AiServices
                .builder(RentAdvisor.class)
                .chatModel(model)
                .tools(new Step5ToolsAndMemory())
                .chatMemory(MessageWindowChatMemory
                        .withMaxMessages(20))
                .build();

        System.out.println(advisor.chat("What rent should "
                + "I ask for my 2-bedroom in Fernwood?"));
        System.out.println(advisor.chat(
                "And if I include a parking spot?"));
    }
}
