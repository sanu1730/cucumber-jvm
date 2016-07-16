package cucumber.runtime;

import cucumber.api.Scenario;
import gherkin.GherkinDialect;
import gherkin.pickles.PickleStep;

public class AmbiguousStepDefinitionMatch extends StepDefinitionMatch {
    private AmbiguousStepDefinitionsException exception;

    public AmbiguousStepDefinitionMatch(PickleStep step, AmbiguousStepDefinitionsException e) {
        super(null, new NoStepDefinition(), null, step, null);
        this.exception = e;
    }

    @Override
    public void runStep(GherkinDialect i18n, Scenario scenario) throws Throwable {
        throw exception;
    }

    @Override
    public void dryRunStep(GherkinDialect i18n, Scenario scenario) throws Throwable {
        runStep(i18n, scenario);
    }

    @Override
    public Match getMatch() {
        return exception.getMatches().get(0);
    }
}
