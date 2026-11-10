# Generative programming in Java: a real-estate case study

Companion code for Lecture 2 ("From prompts to programs"). The running example is the term project:
a platform where people list apartments and houses for rent or sale.

Each step of the lecture is **one Java class** with a `main` method, in `src/main/java/realestate/`.
Everything runs on your laptop: no API keys, no cloud.

| Step | Class | Idea |
|---|---|---|
| 1 | `Step1FirstCall` | Send one question to a local model; get text back |
| 2 | `Step2TypedAnswers` | Generative functions: answers come back as Java types (a `record`, an `enum`) |
| 3 | `Step3CheckAndRepair` | Instruct, validate, repair: Java checks the ad, the model fixes it |
| 4 | `Step4MajorityVote` | Ask several times and keep the most common answer |
| 5 | `Step5ToolsAndMemory` | Tools (Java methods the model can ask for) and chat memory |
| 6 | `Step6Workflow` | A workflow: your code decides the steps (screen, save, write, translate) |

## Setup

1. Install **Java 21** (or newer) and **Maven 3.9+**. Check with `java -version` and `mvn -version`.
2. Install **Ollama** (https://ollama.com/download), then download the model used in class (about 2.2 GB):

   ```bash
   ollama pull granite4.2:3b
   ollama run granite4.2:3b "Say hello in five words"
   ```

   Ollama now answers on `http://localhost:11434`. To try another model, change the `modelName(...)`
   line in a step (for tools in step 5, pick a model that supports tool calling).

## Maven in one minute

Maven is Java's build tool and library manager (like `pip` for Python or `npm` for JavaScript).

- `pom.xml` describes the project: its name, the libraries it needs (LangChain4j, JUnit) and the Java version.
- The first build downloads those libraries from Maven Central into `~/.m2` (once; later builds reuse them).
- Your code lives in `src/main/java`, tests in `src/test/java`, and Maven writes compiled classes to `target/`.

| Command | What it does |
|---|---|
| `mvn compile` | Download the libraries (first time only) and compile the code |
| `mvn test` | Compile and run the tests |
| `mvn -q compile exec:java -Dexec.mainClass=realestate.Step1FirstCall` | Run a step (`-q` hides Maven's own messages) |
| `mvn clean` | Delete `target/` |

You can also open the folder in VS Code or IntelliJ and press **Run** above any `main` method.

## Run the steps

```bash
mvn -q compile exec:java -Dexec.mainClass=realestate.Step1FirstCall
mvn -q compile exec:java -Dexec.mainClass=realestate.Step2TypedAnswers
mvn -q compile exec:java -Dexec.mainClass=realestate.Step3CheckAndRepair
mvn -q compile exec:java -Dexec.mainClass=realestate.Step4MajorityVote
mvn -q compile exec:java -Dexec.mainClass=realestate.Step5ToolsAndMemory
mvn -q compile exec:java -Dexec.mainClass=realestate.Step6Workflow
```

To see exactly what is sent to the model, add `.logRequests(true).logResponses(true)` to the model
builder in a step and run it **without** `-q`.

## Tests

```bash
mvn test                       # fast: tests the rules of step 3, no model needed
RUN_LLM_TESTS=true mvn test    # also measures the screening accuracy of the real model (needs Ollama)
```

1. `Step3CheckTest`: the requirements are plain Java, so they get plain unit tests.
2. `ScreeningEvalTest`: runs labelled listings through the model and checks the accuracy is at least 70%.

## Notes

- Library versions: LangChain4j 1.23.0 (`langchain4j` and `langchain4j-ollama`).
- `pom.xml` compiles with `-parameters`, so a prompt can use a parameter's name, like `{{notes}}`.
  When a method has two or more parameters, LangChain4j also needs `@V("name")` on each one (see step 3).
- Small local models make mistakes, and the same question can get different answers.
  That is why the steps check answers in code, repair them, vote, and test.
