package realestate;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Step 3: instruct, validate, repair. The model writes a rental ad,
 * plain Java checks it, and if a check fails the model fixes the ad.
 * Run: mvn -q compile exec:java -Dexec.mainClass=realestate.Step3CheckAndRepair
 */
public class Step3CheckAndRepair {

    interface AdWriter {

        @UserMessage("Write a short rental ad from "
                + "these notes: {{notes}}")
        String write(String notes);

        @UserMessage("Rewrite this rental ad to fix one "
                + "problem: {{problem}} Answer with the "
                + "new ad only. Ad: {{ad}}")
        String fix(@V("ad") String ad,
                   @V("problem") String problem);
    }

    // A requirement is plain Java:
    // return the problem, or null if the ad is fine
    static String check(String ad) {
        int words = ad.split("\\s+").length;
        if (words > 60) {
            return "It has " + words + " words; use at "
                    + "most 60 words, in one paragraph.";
        }
        if (!ad.contains("$2,350")) {
            return "Say that the rent is $2,350 a month.";
        }
        return null;
    }

    public static void main(String[] args) {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("granite4.1:3b")
                .build();
        AdWriter writer =
                AiServices.create(AdWriter.class, model);

        String notes = "2 bed suite, Fernwood, "
                + "$2,350/month, cats OK, near bus 22, "
                + "shared yard";

        String ad = writer.write(notes);     // instruct
        String problem = check(ad);          // validate
        int tries = 1;
        while (problem != null && tries < 3) {
            System.out.println("Repairing: " + problem);
            ad = writer.fix(ad, problem);    // repair
            problem = check(ad);
            tries++;
        }
        if (problem != null) {               // fail loudly
            throw new IllegalStateException(
                    "Failed after 3 tries: " + problem);
        }
        System.out.println(ad);
    }
}
