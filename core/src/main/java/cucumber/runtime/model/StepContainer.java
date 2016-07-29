package cucumber.runtime.model;

import cucumber.runtime.Runtime;
import cucumber.runtime.StepImpl;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.BasicStatement;
import gherkin.formatter.model.Step;

import java.util.ArrayList;
import java.util.List;

public class StepContainer {
    private final List<Step> steps = new ArrayList<Step>();
    final CucumberFeature cucumberFeature;
    private final BasicStatement statement;

    StepContainer(CucumberFeature cucumberFeature, BasicStatement statement) {
        this.cucumberFeature = cucumberFeature;
        this.statement = statement;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void step(Step step) {
        steps.add(step);
    }

    void format(Formatter formatter) {
        statement.replay(formatter);
        for (Step step : getSteps()) {
            formatter.step(step);
        }
    }

    void runSteps(Formatter formatter, Reporter reporter, Runtime runtime) {
        statement.replay(formatter);
        for (Step step : getSteps()) {
            cucumber.api.Step stepResult = new StepImpl(reporter, step);
            runtime.runBeforeStepHooks(reporter, stepResult);
            formatter.step(step);
            runStep(step, reporter, runtime);
            runtime.runAfterStepHooks(reporter, stepResult);
        }
    }

    void runSteps(Reporter reporter, Runtime runtime) {
        for (Step step : getSteps()) {
            runStep(step, reporter, runtime);
        }
    }

    void runStep(Step step, Reporter reporter, Runtime runtime) {
        runtime.runStep(cucumberFeature.getPath(), step, reporter, cucumberFeature.getI18n());
    }
}
