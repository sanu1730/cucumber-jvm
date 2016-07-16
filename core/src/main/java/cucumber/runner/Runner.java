package cucumber.runner;

import cucumber.api.StepDefinitionReporter;
import cucumber.runtime.AmbiguousStepDefinitionMatch;
import cucumber.runtime.AmbiguousStepDefinitionsException;
import cucumber.runtime.Backend;
import cucumber.runtime.Glue;
import cucumber.runtime.HookDefinition;
import cucumber.runtime.HookDefinitionMatch;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.StepDefinitionMatch;
import cucumber.runtime.StopWatch;
import cucumber.runtime.UndefinedStepDefinitionMatch;
import cucumber.runtime.UndefinedStepsTracker;
import cucumber.runtime.UnreportedStepExecutor;
import gherkin.GherkinDialect;
import gherkin.pickles.Argument;
import gherkin.pickles.Pickle;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleRow;
import gherkin.pickles.PickleStep;
import gherkin.pickles.PickleString;
import gherkin.pickles.PickleTable;
import gherkin.pickles.PickleTag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Runner implements UnreportedStepExecutor {
    private final Glue glue;
    private final EventBus bus;
    private final Collection<? extends Backend> backends;
    private final RuntimeOptions runtimeOptions;
    private final UndefinedStepsTracker undefinedStepsTracker;
    private final StopWatch stopWatch;

    public Runner(Glue glue, EventBus bus, Collection<? extends Backend> backends, RuntimeOptions runtimeOptions, UndefinedStepsTracker undefinedStepsTracker, StopWatch stopWatch) {
        this.glue = glue;
        this.bus = bus;
        this.runtimeOptions = runtimeOptions;
        this.undefinedStepsTracker = undefinedStepsTracker;
        this.stopWatch = stopWatch;
        this.backends = backends;
        for (Backend backend : backends) {
            backend.loadGlue(glue, runtimeOptions.getGlue());
            backend.setUnreportedStepExecutor(this);
        }

    }

    //TODO: Maybe this should go into the cucumber step execution model and it should return the result of that execution!
    @Override
    public void runUnreportedStep(String featurePath, GherkinDialect i18n, String stepKeyword, String stepName, int line, List<PickleRow> dataTableRows, PickleString docString) throws Throwable {
        List<Argument> arguments = new ArrayList<Argument>();
        if (dataTableRows != null && !dataTableRows.isEmpty()) {
            arguments.add((Argument) new PickleTable(dataTableRows));
        } else if (docString != null) {
            arguments.add(docString);
        }
        PickleStep step = new PickleStep(stepName, arguments, Collections.<PickleLocation>emptyList());

        StepDefinitionMatch match = glue.stepDefinitionMatch(featurePath, step, i18n);
        if (match == null) {
            UndefinedStepException error = new UndefinedStepException(step);

            StackTraceElement[] originalTrace = error.getStackTrace();
            StackTraceElement[] newTrace = new StackTraceElement[originalTrace.length + 1];
            newTrace[0] = new StackTraceElement("✽", "StepDefinition", featurePath, line);
            System.arraycopy(originalTrace, 0, newTrace, 1, originalTrace.length);
            error.setStackTrace(newTrace);

            throw error;
        }
        match.runStep(i18n, null);
    }

    public void runPickle(Pickle pickle, GherkinDialect i18n) {
        List<PickleTag> tags;
        try { // TODO: Fix when Gherkin provide a getter for the tags.
            Field f;
            f = pickle.getClass().getDeclaredField("tags");
            f.setAccessible(true);
            tags = (List<PickleTag>) f.get(pickle);
        } catch (Exception e) {
            tags = Collections.<PickleTag>emptyList();
        }
        buildBackendWorlds(); // Java8 step definitions will be added to the glue here
        TestCase testCase = createTestCaseForPickle(pickle, i18n, tags);
        testCase.run(bus, i18n);
        disposeBackendWorlds();
    }

    public List<String> getSnippets() {
        return undefinedStepsTracker.getSnippets(backends, runtimeOptions.getSnippetType().getFunctionNameGenerator());
    }

    public boolean hasUndefinedSteps() {
        return undefinedStepsTracker.hasUndefinedSteps();
    }

    public Glue getGlue() {
        return glue;
    }


    public void reportStepDefinitions(StepDefinitionReporter stepDefinitionReporter) {
        glue.reportStepDefinitions(stepDefinitionReporter);
    }

    private TestCase createTestCaseForPickle(Pickle pickle, GherkinDialect i18n, List<PickleTag> tags) {
        List<TestStep> testSteps = new ArrayList<TestStep>();
        if (!runtimeOptions.isDryRun()) {
            addTestStepsForBeforeHooks(testSteps, tags);
        }
        addTestStepsForPickleSteps(testSteps, pickle, i18n);
        if (!runtimeOptions.isDryRun()) {
            addTestStepsForAfterHooks(testSteps, tags);
        }
        TestCase testCase = new TestCase(testSteps, tags, pickle);
        return testCase;
    }

    private void addTestStepsForPickleSteps(List<TestStep> testSteps, Pickle pickle, GherkinDialect i18n) {
        for (PickleStep step : pickle.getSteps()) {
            StepDefinitionMatch match;
            try {
                match = glue.stepDefinitionMatch(pickle.getLocations().get(0).getPath(), step, i18n);
                if (match == null) {
                    match = new UndefinedStepDefinitionMatch(step);
                }
            } catch (AmbiguousStepDefinitionsException e) {
                match = new AmbiguousStepDefinitionMatch(step, e);
            }
            testSteps.add(new TestStep(match, stopWatch));
        }
    }

    private void addTestStepsForBeforeHooks(List<TestStep> testSteps, List<PickleTag> tags) {
        addTestStepsForHooks(testSteps, tags, glue.getBeforeHooks());
    }

    private void addTestStepsForAfterHooks(List<TestStep> testSteps, List<PickleTag> tags) {
        addTestStepsForHooks(testSteps, tags, glue.getAfterHooks());
    }

    private void addTestStepsForHooks(List<TestStep> testSteps, List<PickleTag> tags,  List<HookDefinition> hooks) {
        for (HookDefinition hook : hooks) {
            if (hook.matches(tags)) {
                TestStep testStep = new UnskipableStep(new HookDefinitionMatch(hook), stopWatch);
                testSteps.add(testStep);
            }
        }
    }

    private void buildBackendWorlds() {
        runtimeOptions.getPlugins(); // To make sure that the plugins are instantiated after
        // the features have been parsed but before the pickles starts to execute.
        for (Backend backend : backends) {
            backend.buildWorld();
        }
        undefinedStepsTracker.reset();

    }

    private void disposeBackendWorlds() {
        for (Backend backend : backends) {
            backend.disposeWorld();
        }
    }
}
