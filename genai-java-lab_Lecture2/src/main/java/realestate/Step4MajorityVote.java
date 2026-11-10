package realestate;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Step 4: majority vote. Ask the same question several times and
 * keep the most common answer: more calls, fewer unlucky answers.
 * Run: mvn -q compile exec:java -Dexec.mainClass=realestate.Step4MajorityVote
 */
public class Step4MajorityVote {

    enum Decision { PUBLISH, REVIEW, REJECT }

    interface Screener {

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
                .modelName("granite4.1:3b")
                .temperature(0.8)  // some randomness
                .build();
        Screener screener =
                AiServices.create(Screener.class, model);

        String listing = "Studio near UVic, $1,100 a "
                + "month. Owner travelling; pay a deposit "
                + "to hold it.";

        // Ask 5 times and count the votes for each decision
        Map<Decision, Integer> votes = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            Decision d = screener.screen(listing);
            votes.put(d, votes.getOrDefault(d, 0) + 1);
        }
        System.out.println("Votes: " + votes);

        // Keep the decision with the most votes
        Decision winner = null;
        for (Decision d : votes.keySet()) {
            if (winner == null
                    || votes.get(d) > votes.get(winner)) {
                winner = d;
            }
        }
        System.out.println("Majority decision: " + winner);
    }
}
