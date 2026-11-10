package realestate;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;

/**
 * Step 2: generative functions. We declare Java methods, the model
 * answers them, and each answer comes back as a Java type.
 * Run: mvn -q compile exec:java -Dexec.mainClass=realestate.Step2TypedAnswers
 */
public class Step2TypedAnswers {

    // A record is a small class that only holds data
    record Listing(int bedrooms, int rent,
            boolean petsAllowed, String neighbourhood) {}

    // An enum is a fixed set of labels to choose from
    enum Decision { PUBLISH, REVIEW, REJECT }

    // An interface lists methods without bodies.
    // LangChain4j writes the bodies for us.
    interface ListingAssistant {

        @UserMessage("Extract the listing details from "
                + "these notes: {{notes}}")
        Listing extract(String notes);

        @UserMessage("Screen this rental listing. "
                + "PUBLISH: nothing unusual. "
                + "REVIEW: odd details that a person "
                + "should check. "
                + "REJECT: clear scam signs, like money "
                + "before any viewing. "
                + "Listing: {{listing}}")
        Decision screen(String listing);
    }

    public static void main(String[] args) {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("gemma3:1b")
                .temperature(0.0)  // no randomness
                .build();
        ListingAssistant assistant = AiServices.create(
                ListingAssistant.class, model);

        String notes = "Bright 2 bed suite in Fernwood, "
                + "$2,350 a month, cats OK.";
        Listing listing = assistant.extract(notes);
        System.out.println(listing);
        System.out.println(listing.rent() + 75); // an int

        String scam = "Condo, $800. I'm abroad: wire the "
                + "deposit and I'll mail the keys.";
        Decision decision = assistant.screen(scam);
        System.out.println(decision);
    }
}
