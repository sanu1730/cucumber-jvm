package cucumber.runner;

import cucumber.runtime.ScenarioImpl;
import gherkin.GherkinDialect;
import gherkin.pickles.Pickle;
import gherkin.pickles.PickleLocation;
import gherkin.pickles.PickleTag;

import java.util.List;

public class TestCase {
    private final Pickle pickle;
    private final List<PickleTag> tags;
    private final List<TestStep> testSteps;

    public TestCase(List<TestStep> testSteps, List<PickleTag> tags, Pickle pickle) {
        this.testSteps = testSteps;
        this.tags = tags;
        this.pickle = pickle;
    }

    public void run(EventBus bus, GherkinDialect i18n) {
        boolean skipNextStep = false;
        bus.send(new TestCaseStarted(this));
        ScenarioImpl scenarioResult = new ScenarioImpl(bus, tags, pickle);
        for (TestStep step : testSteps) {
            Result stepResult = step.run(bus, i18n, scenarioResult, skipNextStep);
            if (stepResult.getStatus() != Result.PASSED) {
                skipNextStep = true;
            }
            scenarioResult.add(stepResult);
        }
        bus.send(new TestCaseFinished(this, new Result(scenarioResult.getStatus(), null, null)));
    }

    public String getName() {
        return pickle.getName();
    }

    public String getLocation() {
        List<PickleLocation> locations = pickle.getLocations();
        if (locations.isEmpty()) {
            return "";
        } else if (locations.size() == 1) {
            return fileColonLine(locations.get(0));
        } else {
            return fileColonLine(locations.get(0)) + "," + Integer.toString(locations.get(1).getLine());
        }
    }

    public String getScenarioDesignation() {
        return fileColonLine(pickle.getLocations().get(0)) + " # " + getName();
    }

    private String fileColonLine(PickleLocation location) {
        return location.getPath() + ":" + Integer.toString(location.getLine());
    }
}
