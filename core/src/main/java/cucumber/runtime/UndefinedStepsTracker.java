package cucumber.runtime;

import cucumber.runtime.snippets.FunctionNameGenerator;
import gherkin.GherkinDialect;
import gherkin.pickles.PickleStep;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public class UndefinedStepsTracker {
    private final List<SimpleEntry<PickleStep, String>> undefinedSteps = new ArrayList<SimpleEntry<PickleStep, String>>();

    public void reset() {
    }

    /**
     * @param backends              what backends we want snippets for
     * @param functionNameGenerator responsible for generating method name
     * @return a list of code snippets that the developer can use to implement undefined steps.
     *         This should be displayed after a run.
     */
    public List<String> getSnippets(Iterable<? extends Backend> backends, FunctionNameGenerator functionNameGenerator) {
        // TODO: Convert "And" and "But" to the Given/When/Then keyword above in the Gherkin source.
        List<String> snippets = new ArrayList<String>();
        for (SimpleEntry<PickleStep, String> entry : undefinedSteps) {
            for (Backend backend : backends) {
                String snippet = backend.getSnippet(entry.getKey(), entry.getValue(), functionNameGenerator);
                if (snippet == null) {
                    throw new NullPointerException("null snippet");
                }
                if (!snippets.contains(snippet)) {
                    snippets.add(snippet);
                }
            }
        }
        return snippets;
    }

    public void storeStepKeyword(PickleStep step, GherkinDialect i18n) {
    }

    public void addUndefinedStep(PickleStep step, GherkinDialect i18n) {
        undefinedSteps.add(new SimpleEntry<PickleStep, String>(step, givenWhenThenKeyword(step, i18n)));
    }

    private String givenWhenThenKeyword(PickleStep step, GherkinDialect i18n) {
        return getFirstCodeKeyword(i18n);
    }

    private String getFirstCodeKeyword(GherkinDialect i18n) {
        for (String keyword : i18n.getStepKeywords()) {
            if (!keyword.equals("* ")) {
                return keyword.replaceAll("[\\s',!]", "");
            }
        }
        return null;
    }

    public boolean hasUndefinedSteps() {
        return !undefinedSteps.isEmpty();
    }
}
