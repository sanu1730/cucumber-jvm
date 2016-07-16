package cucumber.runner;

import cucumber.api.Pending;
import cucumber.api.Scenario;
import cucumber.runtime.DefinitionMatch;
import cucumber.runtime.StopWatch;
import cucumber.runtime.UndefinedStepDefinitionException;
import gherkin.GherkinDialect;

import java.util.Arrays;

public class TestStep {
    private static final String[] PENDING_EXCEPTIONS = {
            "org.junit.AssumptionViolatedException",
            "org.junit.internal.AssumptionViolatedException"
    };
    static {
        Arrays.sort(PENDING_EXCEPTIONS);
    }
    private static final Object DUMMY_ARG = new Object();
    private final StopWatch stopWatch;
    protected final DefinitionMatch definitionMatch;

    public TestStep(DefinitionMatch definitionMatch, StopWatch stopWatch) {
        this.definitionMatch = definitionMatch;
        this.stopWatch = stopWatch;
    }

    public Result run(EventBus bus, GherkinDialect i18n, Scenario scenario, boolean skipSteps) {
        bus.send(new TestStepStarted(definitionMatch));
        String status = skipSteps ? Result.SKIPPED.getStatus() : Result.PASSED;
        Throwable error = null;
        stopWatch.start();
        try {
            executeStep(i18n, scenario, skipSteps);
        } catch (Throwable t) {
            error = t;
            status = mapThrowableToStatus(t);
        }
        long duration = stopWatch.stop();
        Result result = mapStatusToResult(status, error, duration);
        bus.send(new TestStepFinished(definitionMatch, result));
        return result;
    }

    protected void executeStep(GherkinDialect i18n, Scenario scenario, boolean skipSteps) throws Throwable {
        if (!skipSteps) {
            definitionMatch.runStep(i18n, scenario);
        } else {
            definitionMatch.dryRunStep(i18n, scenario);
        }
    }

    private String mapThrowableToStatus(Throwable t) {
        if (t.getClass().isAnnotationPresent(Pending.class) || Arrays.binarySearch(PENDING_EXCEPTIONS, t.getClass().getName()) >= 0) {
            return "pending";
        }
        if (t.getClass() == UndefinedStepDefinitionException.class) {
            return Result.UNDEFINED.getStatus();
        }
        return Result.FAILED;
    }

    private Result mapStatusToResult(String status, Throwable error, long duration) {
        Long resultDuration = duration;
        Throwable resultError = error;
        if (status == Result.SKIPPED.getStatus()) {
            return Result.SKIPPED;
        }
        if (status == Result.UNDEFINED.getStatus()) {
            return Result.UNDEFINED;
        }
        Result result = new Result(status, resultDuration, resultError, DUMMY_ARG);
        return result;
    }
}
