package cucumber.runtime;

import cucumber.runtime.snippets.FunctionNameGenerator;
import cucumber.runtime.snippets.Snippet;
import cucumber.runtime.snippets.SnippetGenerator;
import cucumber.runtime.snippets.UnderscoreConcatenator;
import gherkin.GherkinDialect;
import gherkin.GherkinDialectProvider;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleStep;
import gherkin.pickles.Argument;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UndefinedStepsTrackerTest {

    private static final GherkinDialectProvider DIALECT_PROVIDER = new GherkinDialectProvider("en");
    private static final GherkinDialect ENGLISH = DIALECT_PROVIDER.getDefaultDialect();
    private FunctionNameGenerator functionNameGenerator = new FunctionNameGenerator(new UnderscoreConcatenator());

    @Test
    public void has_undefined_steps() {
        UndefinedStepsTracker undefinedStepsTracker = new UndefinedStepsTracker();
        undefinedStepsTracker.addUndefinedStep(new PickleStep("A", Collections.<Argument>emptyList(), Collections.<PickleLocation>emptyList()), ENGLISH);
        assertTrue(undefinedStepsTracker.hasUndefinedSteps());
    }

    @Test
    public void has_no_undefined_steps() {
        UndefinedStepsTracker undefinedStepsTracker = new UndefinedStepsTracker();
        assertFalse(undefinedStepsTracker.hasUndefinedSteps());
    }

    @Test
    public void removes_duplicates() {
        Backend backend = new TestBackend();
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
        tracker.storeStepKeyword(new PickleStep("A", Collections.<Argument>emptyList(), Collections.<PickleLocation>emptyList()), ENGLISH);
        tracker.addUndefinedStep(new PickleStep("B", Collections.<Argument>emptyList(), Collections.<PickleLocation>emptyList()), ENGLISH);
        tracker.addUndefinedStep(new PickleStep("B", Collections.<Argument>emptyList(), Collections.<PickleLocation>emptyList()), ENGLISH);
        assertEquals("[Given ^B$]", tracker.getSnippets(asList(backend), functionNameGenerator).toString());
    }

    @Test
    public void converts_and_to_previous_step_keyword() {
        Backend backend = new TestBackend();
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
//        tracker.storeStepKeyword(new Step(null, "When ", "A", 1, null, null), ENGLISH);
//        tracker.storeStepKeyword(new Step(null, "And ", "B", 1, null, null), ENGLISH);
//        tracker.addUndefinedStep(new Step(null, "But ", "C", 1, null, null), ENGLISH);
//        assertEquals("[When ^C$]", tracker.getSnippets(asList(backend), functionNameGenerator).toString());
    }

    @Test
    public void doesnt_try_to_use_star_keyword() {
        Backend backend = new TestBackend();
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
//        tracker.storeStepKeyword(new Step(null, "When ", "A", 1, null, null), ENGLISH);
//        tracker.storeStepKeyword(new Step(null, "And ", "B", 1, null, null), ENGLISH);
//        tracker.addUndefinedStep(new Step(null, "* ", "C", 1, null, null), ENGLISH);
//        assertEquals("[When ^C$]", tracker.getSnippets(asList(backend), functionNameGenerator).toString());
    }

    @Test
    public void star_keyword_becomes_given_when_no_previous_step() {
        Backend backend = new TestBackend();
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
//        tracker.addUndefinedStep(new Step(null, "* ", "A", 1, null, null), ENGLISH);
//        assertEquals("[Given ^A$]", tracker.getSnippets(asList(backend), functionNameGenerator).toString());
    }

    @Test
    public void snippets_are_generated_for_correct_locale() throws Exception {
        Backend backend = new TestBackend();
        UndefinedStepsTracker tracker = new UndefinedStepsTracker();
        tracker.addUndefinedStep(new PickleStep("Б", Collections.<Argument>emptyList(), Collections.<PickleLocation>emptyList()), DIALECT_PROVIDER.getDialect("ru", null));
        assertEquals("[Допустим ^Б$]", tracker.getSnippets(asList(backend), functionNameGenerator).toString());
    }

    private class TestBackend implements Backend {
        @Override
        public void loadGlue(Glue glue, List<String> gluePaths) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setUnreportedStepExecutor(UnreportedStepExecutor executor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void buildWorld() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void disposeWorld() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getSnippet(PickleStep step, String keyword, FunctionNameGenerator functionNameGenerator) {
            return new SnippetGenerator(new TestSnippet()).getSnippet(step, keyword, functionNameGenerator);
        }
    }

    private class TestSnippet implements Snippet {
        @Override
        public String template() {
            return "{0} {1}";
        }

        @Override
        public String tableHint() {
            return null;
        }

        @Override
        public String arguments(List<Class<?>> argumentTypes) {
            return argumentTypes.toString();
        }

        @Override
        public String namedGroupStart() {
            return null;
        }

        @Override
        public String namedGroupEnd() {
            return null;
        }

        @Override
        public String escapePattern(String pattern) {
            return pattern;
        }
    }
}
