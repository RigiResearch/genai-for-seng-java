package realestate;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;

/**
 * Step 6: a workflow. Our Java code decides the order of the steps,
 * and each step is one call to the model:
 * screen the listing -> save its details -> write an ad -> translate it
 * Run: mvn -q compile exec:java -Dexec.mainClass=realestate.Step6Workflow
 */
public class Step6Workflow {

    record Listing(int bedrooms, int rent,
            boolean petsAllowed, String neighbourhood) {}

    enum Decision { PUBLISH, REVIEW, REJECT }

    interface ListingDesk {

        @UserMessage("Screen this rental listing. "
                + "PUBLISH: nothing unusual. "
                + "REVIEW: odd details that a person "
                + "should check. "
                + "REJECT: clear scam signs, like money "
                + "before any viewing. "
                + "Listing: {{notes}}")
        Decision screen(String notes);

        @UserMessage("Extract the listing details from "
                + "these notes: {{notes}}")
        Listing extract(String notes);

        @UserMessage("Write a rental ad of at most 60 "
                + "words, in one paragraph, from these "
                + "notes: {{notes}}")
        String write(String notes);

        @UserMessage("Translate this rental ad into "
                + "French: {{ad}}")
        String translate(String ad);
    }

    public static void main(String[] args) {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("granite4.1:3b")
                .temperature(0.0)
                .build();
        ListingDesk desk =
                AiServices.create(ListingDesk.class, model);

        String notes = "2 bed suite, Fernwood, "
                + "$2,350/month, cats OK, near bus 22, "
                + "shared yard";

        // 1. Screen it: anything odd goes to a person
        Decision decision = desk.screen(notes);
        if (decision != Decision.PUBLISH) {
            System.out.println("Moderator: " + decision);
            return;
        }

        // 2. Save the details (a real app: a database row)
        Listing listing = desk.extract(notes);
        System.out.println("Saved: " + listing);

        // 3. Write the ad, then translate it
        String ad = desk.write(notes);
        String french = desk.translate(ad);
        System.out.println(ad);
        System.out.println(french);
    }
}
