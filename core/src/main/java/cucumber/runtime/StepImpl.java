package cucumber.runtime;

import cucumber.api.Step;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Tag;

import java.util.*;

import static java.util.Arrays.asList;

public class StepImpl implements Step {
    private static final List<String> SEVERITY = asList("passed", "skipped", "pending", "undefined", "failed");
    private final Reporter reporter;
    private final String stepName;
    private String status;
    private gherkin.formatter.model.Step gherkinStep;

    public StepImpl(Reporter reporter, gherkin.formatter.model.Step gherkinStep) {
        this.reporter = reporter;
        this.gherkinStep = gherkinStep;
        this.stepName = gherkinStep.getName();
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean isFailed() {
        return "failed".equals(getStatus());
    }

    @Override
    public void embed(byte[] data, String mimeType) {
        reporter.embedding(mimeType, data);
    }

    @Override
    public void write(String text) {
        reporter.write(text);
    }

    @Override
    public String getName() {
        return stepName;
    }


    @Override
    public gherkin.formatter.model.Step getGherkinStep() {
        return gherkinStep;
    }
}
